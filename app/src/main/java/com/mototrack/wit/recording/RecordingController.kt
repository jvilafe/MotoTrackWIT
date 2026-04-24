package com.mototrack.wit.recording

import android.content.Context
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import com.mototrack.wit.data.RouteRepository
import com.mototrack.wit.data.db.RouteEntity
import com.mototrack.wit.data.db.SampleEntity
import com.mototrack.wit.data.export.MtwExporter
import com.mototrack.wit.fusion.FusedSample
import com.mototrack.wit.fusion.SampleFusionEngine
import com.mototrack.wit.gps.GpsLocationSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

enum class RecordingState { IDLE, RECORDING, PAUSED, STOPPED }

data class RecordingStats(
    val sampleCount: Long = 0,
    val durationMs: Long = 0,
    val maxRollLeft: Float = 0f,    // roll > 0
    val maxRollRight: Float = 0f,   // -roll cuando roll < 0
    val maxSpeedKmh: Float = 0f,
    val maxAccelG: Float = 0f,
    val maxBrakeG: Float = 0f,
)

/**
 * Controlador de grabación.
 * Pipeline:
 *   IMU (BLE WIT) + GPS  →  SampleFusionEngine (25 Hz)  →  Room (lotes)
 *   stop() → RouteRepository.finalizeRoute() → MtwExporter.write(.mtw)
 */
@Singleton
class RecordingController @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val gps: GpsLocationSource,
    private val fusion: SampleFusionEngine,
    private val repo: RouteRepository,
) {
    private val tag = "RecCtrl"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _state = MutableStateFlow(RecordingState.IDLE)
    val state: StateFlow<RecordingState> = _state.asStateFlow()

    private val _stats = MutableStateFlow(RecordingStats())
    val stats: StateFlow<RecordingStats> = _stats.asStateFlow()

    private var startedAtMono: Long = 0L
    private var pausedAccum: Long = 0L
    private var lastPauseAt: Long = 0L

    private var routeId: Long = 0L
    private var routeName: String = "Ruta"
    private var lastFile: File? = null

    private var collectJob: Job? = null
    private var clockJob: Job? = null
    private val batchBuffer = mutableListOf<FusedSample>()
    private val batchMutex = Mutex()
    private val BATCH_SIZE = 50  // ~2 s a 25 Hz

    /** Inicia una nueva ruta: registra en Room y arranca fusion + GPS. */
    suspend fun start(name: String) = withContext(Dispatchers.IO) {
        if (_state.value == RecordingState.RECORDING) return@withContext
        routeName = name.ifBlank { "Ruta" }
        routeId = repo.startRoute(routeName)
        Log.i(tag, "Nueva ruta id=$routeId nombre=$routeName")

        startedAtMono = SystemClock.elapsedRealtime()
        pausedAccum = 0L
        lastPauseAt = 0L
        _stats.value = RecordingStats()
        _state.value = RecordingState.RECORDING

        gps.start()
        fusion.start()
        startCollectors()
    }

    suspend fun pause() = withContext(Dispatchers.IO) {
        if (_state.value != RecordingState.RECORDING) return@withContext
        lastPauseAt = SystemClock.elapsedRealtime()
        _state.value = RecordingState.PAUSED
    }

    suspend fun resume() = withContext(Dispatchers.IO) {
        if (_state.value != RecordingState.PAUSED) return@withContext
        if (lastPauseAt > 0) pausedAccum += SystemClock.elapsedRealtime() - lastPauseAt
        lastPauseAt = 0L
        _state.value = RecordingState.RECORDING
    }

    /** Detiene grabación: vuelca el último batch, finaliza ruta y exporta .mtw. */
    suspend fun stop() = withContext(Dispatchers.IO) {
        if (_state.value == RecordingState.IDLE) return@withContext

        collectJob?.cancel(); collectJob = null
        clockJob?.cancel(); clockJob = null
        fusion.stop()

        flushBatch()

        val rid = routeId
        if (rid > 0) {
            repo.finalizeRoute(rid)
            val route = repo.get(rid)
            val samples = repo.samples(rid)
            if (route != null && samples.isNotEmpty()) {
                lastFile = exportMtw(route.name, route, samples)
                Log.i(tag, "Ruta guardada: ${lastFile?.absolutePath} (${samples.size} pts)")
            } else {
                Log.w(tag, "Ruta $rid sin muestras, se omite export")
            }
        }

        _state.value = RecordingState.STOPPED
    }

    private fun startCollectors() {
        collectJob?.cancel()
        collectJob = scope.launch {
            fusion.flow.collect { fs ->
                if (_state.value != RecordingState.RECORDING) return@collect
                appendSample(fs)
            }
        }
        clockJob?.cancel()
        clockJob = scope.launch {
            while (isActive) {
                delay(500)
                if (_state.value == RecordingState.RECORDING) {
                    val now = SystemClock.elapsedRealtime()
                    val dur = now - startedAtMono - pausedAccum
                    _stats.update { it.copy(durationMs = dur) }
                }
            }
        }
    }

    private suspend fun appendSample(fs: FusedSample) {
        updateStats(fs)
        val toFlush: List<FusedSample>? = batchMutex.withLock {
            batchBuffer.add(fs)
            if (batchBuffer.size >= BATCH_SIZE) {
                val copy = batchBuffer.toList()
                batchBuffer.clear()
                copy
            } else null
        }
        toFlush?.let { repo.appendBatch(routeId, it) }
    }

    private suspend fun flushBatch() {
        val toFlush: List<FusedSample> = batchMutex.withLock {
            val copy = batchBuffer.toList()
            batchBuffer.clear()
            copy
        }
        if (toFlush.isNotEmpty() && routeId > 0) repo.appendBatch(routeId, toFlush)
    }

    private fun updateStats(s: FusedSample) {
        val speedKmh = s.vGps * 3.6f
        _stats.update { st ->
            st.copy(
                sampleCount  = st.sampleCount + 1,
                maxRollLeft  = if (s.roll > st.maxRollLeft) s.roll else st.maxRollLeft,
                maxRollRight = if (-s.roll > st.maxRollRight) -s.roll else st.maxRollRight,
                maxSpeedKmh  = max(st.maxSpeedKmh, speedKmh),
                maxAccelG    = max(st.maxAccelG, s.gMag),
                maxBrakeG    = if (s.ay < st.maxBrakeG) s.ay else st.maxBrakeG,
            )
        }
    }

    private fun exportMtw(
        name: String,
        route: RouteEntity,
        samples: List<SampleEntity>,
    ): File {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val safe = name.replace(Regex("[^A-Za-z0-9_\\-]"), "_")
        val dir = File(
            ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "MotoTrackWIT"
        ).apply { if (!exists()) mkdirs() }
        val file = File(dir, "${safe}_$ts.mtw")
        MtwExporter.write(file, route, samples)
        return file
    }

    fun currentFilePath(): String? = lastFile?.absolutePath
}

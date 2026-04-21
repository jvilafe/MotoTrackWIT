package com.mototrack.wit.recording

import android.content.Context
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import com.mototrack.wit.ble.WitBleManager
import com.mototrack.wit.ble.WitProtocol
import com.mototrack.wit.gps.GpsLocationSource
import com.mototrack.wit.gps.GpsSample
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

enum class RecordingState { IDLE, RECORDING, PAUSED, STOPPED }

data class RecordingStats(
    val sampleCount: Long = 0,
    val durationMs: Long = 0,
    val maxRollLeft: Float = 0f,    // roll < 0
    val maxRollRight: Float = 0f,   // roll > 0
    val maxSpeedKmh: Float = 0f,
    val maxAccelG: Float = 0f,      // |G| pico
    val maxBrakeG: Float = 0f       // pico negativo eje longitudinal
)

@Singleton
class RecordingController @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val ble: WitBleManager,
    private val gps: GpsLocationSource
) {
    private val tag = "RecCtrl"
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _state = MutableStateFlow(RecordingState.IDLE)
    val state: StateFlow<RecordingState> = _state.asStateFlow()

    private val _stats = MutableStateFlow(RecordingStats())
    val stats: StateFlow<RecordingStats> = _stats.asStateFlow()

    private var startedAt: Long = 0L
    private var pausedAccum: Long = 0L
    private var lastPauseAt: Long = 0L

    private var writer: BufferedWriter? = null
    private var currentFile: File? = null
    private var routeName: String = "Ruta"

    private var imuJob: Job? = null
    private var clockJob: Job? = null

    /** Inicia una nueva ruta. Crea el fichero CSV y arranca colectores. */
    suspend fun start(name: String) = withContext(Dispatchers.IO) {
        if (_state.value == RecordingState.RECORDING) return@withContext
        routeName = name.ifBlank { "Ruta" }

        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val safe = routeName.replace(Regex("[^A-Za-z0-9_\\-]"), "_")
        val dir = File(
            ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            "MotoTrackWIT"
        ).apply { if (!exists()) mkdirs() }
        currentFile = File(dir, "${safe}_$ts.csv")
        writer = BufferedWriter(FileWriter(currentFile, false)).apply {
            write("tMono,roll,pitch,yaw,ax,ay,az,wx,wy,wz,temp,lat,lon,alt,speedKmh,bearing,hAcc")
            newLine()
        }
        Log.i(tag, "Grabando en ${currentFile?.absolutePath}")

        startedAt = SystemClock.elapsedRealtime()
        pausedAccum = 0L
        lastPauseAt = 0L
        _stats.value = RecordingStats()
        _state.value = RecordingState.RECORDING

        gps.start()
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

    suspend fun stop() = withContext(Dispatchers.IO) {
        if (_state.value == RecordingState.IDLE) return@withContext
        imuJob?.cancel(); imuJob = null
        clockJob?.cancel(); clockJob = null
        try { writer?.flush(); writer?.close() } catch (_: Exception) {}
        writer = null
        _state.value = RecordingState.STOPPED
        Log.i(tag, "Ruta detenida. Fichero: ${currentFile?.absolutePath}")
    }

    private fun startCollectors() {
        imuJob?.cancel()
        imuJob = scope.launch {
            ble.samples.collect { s ->
                if (_state.value != RecordingState.RECORDING) return@collect
                val g = gps.samples.value
                writeRow(s, g)
                updateStats(s, g)
            }
        }
        clockJob?.cancel()
        clockJob = scope.launch {
            while (isActive) {
                delay(500)
                if (_state.value == RecordingState.RECORDING) {
                    val now = SystemClock.elapsedRealtime()
                    val dur = now - startedAt - pausedAccum
                    _stats.update { it.copy(durationMs = dur) }
                }
            }
        }
    }

    private fun writeRow(s: WitProtocol.Sample, g: GpsSample) {
        val w = writer ?: return
        try {
            w.write(
                "%d,%.3f,%.3f,%.3f,%.4f,%.4f,%.4f,%.2f,%.2f,%.2f,%.2f,%.6f,%.6f,%.1f,%.2f,%.1f,%.1f"
                    .format(
                        Locale.US,
                        s.tMono, s.roll, s.pitch, s.yaw,
                        s.ax, s.ay, s.az,
                        s.wx, s.wy, s.wz,
                        s.temp,
                        g.lat, g.lon, g.altitude,
                        g.speedMs * 3.6f, g.bearing, g.hAcc
                    )
            )
            w.newLine()
        } catch (e: Exception) {
            Log.e(tag, "write error", e)
        }
    }

    private fun updateStats(s: WitProtocol.Sample, g: GpsSample) {
        val gMag = kotlin.math.sqrt(s.ax*s.ax + s.ay*s.ay + s.az*s.az)
        val speedKmh = g.speedMs * 3.6f
        _stats.update { st ->
            st.copy(
                sampleCount = st.sampleCount + 1,
                maxRollLeft  = if (s.roll < st.maxRollLeft)  s.roll  else st.maxRollLeft,
                maxRollRight = if (s.roll > st.maxRollRight) s.roll  else st.maxRollRight,
                maxSpeedKmh  = if (speedKmh > st.maxSpeedKmh) speedKmh else st.maxSpeedKmh,
                maxAccelG    = if (gMag > st.maxAccelG) gMag else st.maxAccelG,
                maxBrakeG    = if (s.ay < st.maxBrakeG) s.ay else st.maxBrakeG
            )
        }
    }

    fun currentFilePath(): String? = currentFile?.absolutePath
}

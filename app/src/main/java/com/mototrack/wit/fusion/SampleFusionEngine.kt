package com.mototrack.wit.fusion

import android.location.Location
import com.mototrack.wit.ble.WitBleManager
import com.mototrack.wit.ble.WitProtocol
import com.mototrack.wit.gps.GpsLocationSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

/**
 * Combina IMU (50 Hz) + GPS (10 Hz) en muestras a 25 Hz interpolando linealmente
 * la posición entre los dos últimos fixes GPS.
 */
@Singleton
class SampleFusionEngine @Inject constructor(
    private val ble: WitBleManager,
    private val gps: GpsLocationSource,
) {
    private val _flow = MutableSharedFlow<FusedSample>(extraBufferCapacity = 512)
    val flow: SharedFlow<FusedSample> = _flow.asSharedFlow()

    private val _hz = MutableStateFlow(0f)
    val hz: StateFlow<Float> = _hz.asStateFlow()

    private var job: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Volatile private var lastImu: WitProtocol.Sample? = null
    @Volatile private var prevLoc: Location? = null
    @Volatile private var lastLoc: Location? = null

    fun start(periodMs: Long = 40L) { // 25 Hz
        if (job?.isActive == true) return
        lastImu = null; prevLoc = null; lastLoc = null
        job = scope.launch {
            launch { ble.samples.collect { lastImu = it } }
            launch { gps.stream(100L).collect {
                prevLoc = lastLoc
                lastLoc = it
            }}
            launch { tickLoop(periodMs) }
        }
    }

    fun stop() { job?.cancel(); job = null; _hz.value = 0f }

    private suspend fun tickLoop(periodMs: Long) {
        var count = 0
        var window = System.currentTimeMillis()
        while (currentCoroutineContext().isActive) {
            val t = System.currentTimeMillis()
            val imu = lastImu
            val loc = lastLoc
            if (imu != null && loc != null) {
                val (lat, lon, alt) = interpolate(t)
                val gMag = sqrt(imu.ax * imu.ax + imu.ay * imu.ay + imu.az * imu.az)
                _flow.tryEmit(FusedSample(
                    t = t, lat = lat, lon = lon, alt = alt,
                    vGps = loc.speed, bearing = loc.bearing,
                    hAcc = if (loc.hasAccuracy()) loc.accuracy else -1f,
                    ax = imu.ax, ay = imu.ay, az = imu.az,
                    gx = imu.gx, gy = imu.gy, gz = imu.gz,
                    roll = imu.roll, pitch = imu.pitch, yaw = imu.yaw,
                    gMag = gMag
                ))
            }
            count++
            val now = System.currentTimeMillis()
            if (now - window >= 1000) {
                _hz.value = count * 1000f / (now - window)
                count = 0; window = now
            }
            delay(periodMs)
        }
    }

    private fun interpolate(t: Long): Triple<Double, Double, Double> {
        val a = prevLoc; val b = lastLoc ?: return Triple(0.0, 0.0, 0.0)
        if (a == null || b.time == a.time) return Triple(b.latitude, b.longitude, b.altitude)
        val dt = (b.time - a.time).toDouble()
        val k = ((t - a.time).toDouble() / dt).coerceIn(0.0, 1.5)
        return Triple(
            a.latitude + (b.latitude - a.latitude) * k,
            a.longitude + (b.longitude - a.longitude) * k,
            a.altitude + (b.altitude - a.altitude) * k,
        )
    }
}

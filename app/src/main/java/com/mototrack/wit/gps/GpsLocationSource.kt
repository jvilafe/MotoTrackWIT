package com.mototrack.wit.gps

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.os.SystemClock
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/** Estado del proveedor GPS para HUD. */
data class GpsConnState(
    val providerEnabled: Boolean = false,
    val hasFix: Boolean = false
)

/** Muestra GPS para HUD/fusion. */
data class GpsSample(
    val tMono: Long = 0L,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val altitude: Double = 0.0,
    val speedMs: Float = 0f,
    val bearing: Float = 0f,
    val hAcc: Float = 0f
)

@Singleton
class GpsLocationSource @Inject constructor(@ApplicationContext private val ctx: Context) {

    private val client: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(ctx)
    private val lm: LocationManager =
        ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _samples = MutableStateFlow(GpsSample())
    val samples: StateFlow<GpsSample> = _samples.asStateFlow()

    private val _connectionState = MutableStateFlow(GpsConnState())
    val connectionState: StateFlow<GpsConnState> = _connectionState.asStateFlow()

    private val _sampleHz = MutableStateFlow(0f)
    val sampleHz: StateFlow<Float> = _sampleHz.asStateFlow()

    private var sampleCounter = 0
    private var hzJob: Job? = null
    private var providerJob: Job? = null
    private var locationCb: LocationCallback? = null

    /** Stream original (para grabación / fusion). */
    @SuppressLint("MissingPermission")
    fun stream(intervalMs: Long = 100L): Flow<Location> = callbackFlow {
        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
            .setMinUpdateIntervalMillis(intervalMs)
            .setMaxUpdateDelayMillis(intervalMs)
            .build()
        val cb = object : LocationCallback() {
            override fun onLocationResult(r: LocationResult) {
                r.lastLocation?.let { trySend(it) }
            }
        }
        client.requestLocationUpdates(req, cb, Looper.getMainLooper())
        awaitClose { client.removeLocationUpdates(cb) }
    }

    /** Sampling continuo para HUD. Idempotente. */
    @SuppressLint("MissingPermission")
    fun start(intervalMs: Long = 100L) {
        if (locationCb != null) return

        val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
            .setMinUpdateIntervalMillis(intervalMs)
            .setMaxUpdateDelayMillis(intervalMs)
            .build()

        val cb = object : LocationCallback() {
            override fun onLocationResult(r: LocationResult) {
                val loc = r.lastLocation ?: return
                _samples.value = GpsSample(
                    tMono = SystemClock.elapsedRealtime(),
                    lat = loc.latitude,
                    lon = loc.longitude,
                    altitude = if (loc.hasAltitude()) loc.altitude else 0.0,
                    speedMs = if (loc.hasSpeed()) loc.speed else 0f,
                    bearing = if (loc.hasBearing()) loc.bearing else 0f,
                    hAcc = if (loc.hasAccuracy()) loc.accuracy else 0f
                )
                sampleCounter++
                val acc = if (loc.hasAccuracy()) loc.accuracy else 999f
                if (acc < 50f) _connectionState.update { it.copy(hasFix = true) }
            }
        }
        locationCb = cb
        try {
            client.requestLocationUpdates(req, cb, Looper.getMainLooper())
        } catch (_: SecurityException) { /* sin permisos */ }

        hzJob?.cancel()
        hzJob = scope.launch {
            while (isActive) {
                delay(1000)
                _sampleHz.value = sampleCounter.toFloat()
                sampleCounter = 0
            }
        }

        providerJob?.cancel()
        providerJob = scope.launch {
            while (isActive) {
                val enabled = try { lm.isProviderEnabled(LocationManager.GPS_PROVIDER) } catch (_: Exception) { false }
                _connectionState.update { it.copy(providerEnabled = enabled) }
                delay(2000)
            }
        }
    }

    fun stop() {
        locationCb?.let {
            try { client.removeLocationUpdates(it) } catch (_: Exception) {}
        }
        locationCb = null
        hzJob?.cancel(); hzJob = null
        providerJob?.cancel(); providerJob = null
        _sampleHz.value = 0f
        _connectionState.value = GpsConnState()
    }
}

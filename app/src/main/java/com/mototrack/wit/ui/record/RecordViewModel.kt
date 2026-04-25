package com.mototrack.wit.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mototrack.wit.ble.BleConnState
import com.mototrack.wit.ble.WitBleManager
import com.mototrack.wit.ble.WitProtocol
import com.mototrack.wit.gps.GpsConnState
import com.mototrack.wit.gps.GpsLocationSource
import com.mototrack.wit.gps.GpsSample
import com.mototrack.wit.recording.RecordingController
import com.mototrack.wit.recording.RecordingState
import com.mototrack.wit.recording.RecordingStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sqrt

data class HudState(
    val bleConnected: Boolean = false,
    val bleDeviceName: String? = null,
    val bleRssi: Int? = null,
    val bleHz: Float = 0f,

    val gpsProviderEnabled: Boolean = false,
    val gpsFix: Boolean = false,
    val gpsHz: Float = 0f,
    val speedKmh: Float = 0f,
    val lat: Double? = null,
    val lon: Double? = null,
    val altitude: Double? = null,
    val bearing: Float? = null,
    val hAcc: Float? = null,

    val roll: Float = 0f, val pitch: Float = 0f, val yaw: Float = 0f,
    val ax: Float = 0f,   val ay: Float = 0f,    val az: Float = 0f,
    val wx: Float = 0f,   val wy: Float = 0f,    val wz: Float = 0f,
    val gMag: Float = 0f,
    val temp: Float = 0f,
    val battery: Int? = null,

    val sampleCount: Long = 0,
    val durationMs: Long = 0,
    val maxRollLeft: Float = 0f,
    val maxRollRight: Float = 0f,
    val maxSpeedKmh: Float = 0f,
    val maxAccelG: Float = 0f,
    val maxBrakeG: Float = 0f,
    val recording: RecordingState = RecordingState.IDLE
)

@HiltViewModel
class RecordViewModel @Inject constructor(
    private val ble: WitBleManager,
    private val gps: GpsLocationSource,
    private val controller: RecordingController
) : ViewModel() {

    private val _routeName = MutableStateFlow("")
    val routeName: StateFlow<String> = _routeName
    fun setRouteName(v: String) { _routeName.value = v }

    /** Atajo a los stats del controller (máximos en vivo) para la pestaña Ruta. */
    val statsForUi: StateFlow<RecordingStats> = controller.stats

    private val lastSample: StateFlow<WitProtocol.Sample> =
        ble.samples.stateIn(viewModelScope, SharingStarted.Eagerly, WitProtocol.Sample())

    val hud: StateFlow<HudState> = combine(
        listOf(
            ble.connectionState,
            ble.sampleHz,
            lastSample,
            gps.connectionState,
            gps.samples,
            gps.sampleHz,
            controller.state,
            controller.stats
        )
    ) { arr ->
        val bleConn = arr[0] as BleConnState
        val bHz     = arr[1] as Float
        val s       = arr[2] as WitProtocol.Sample
        val gpsConn = arr[3] as GpsConnState
        val g       = arr[4] as GpsSample
        val gHz     = arr[5] as Float
        val rec     = arr[6] as RecordingState
        val st      = arr[7] as RecordingStats

        HudState(
            bleConnected = bleConn.connected,
            bleDeviceName = bleConn.name,
            bleRssi = bleConn.rssi,
            bleHz = bHz,

            gpsProviderEnabled = gpsConn.providerEnabled,
            gpsFix = gpsConn.hasFix,
            gpsHz = gHz,
            speedKmh = g.speedMs * 3.6f,
            lat = if (g.lat != 0.0) g.lat else null,
            lon = if (g.lon != 0.0) g.lon else null,
            altitude = if (g.altitude != 0.0) g.altitude else null,
            bearing = g.bearing,
            hAcc = g.hAcc,

            // Intercambiamos roll por yaw según la configuración física detectada
            roll = s.yaw, 
            pitch = s.pitch, 
            yaw = s.roll,
            ax = s.ax, ay = s.ay, az = s.az,
            wx = s.wx, wy = s.wy, wz = s.wz,
            gMag = sqrt(s.ax*s.ax + s.ay*s.ay + s.az*s.az),
            temp = s.temp,
            battery = s.batteryPct,

            sampleCount = st.sampleCount,
            durationMs = st.durationMs,
            maxRollLeft = st.maxRollLeft,
            maxRollRight = st.maxRollRight,
            maxSpeedKmh = st.maxSpeedKmh,
            maxAccelG = st.maxAccelG,
            maxBrakeG = st.maxBrakeG,
            recording = rec
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), HudState())

    fun start()  = viewModelScope.launch { controller.start(_routeName.value.ifBlank { "Ruta" }) }
    fun pause()  = viewModelScope.launch { controller.pause() }
    fun resume() = viewModelScope.launch { controller.resume() }
    fun stop()   = viewModelScope.launch { controller.stop() }

    fun scanAndConnectBle() = viewModelScope.launch { ble.scanAndConnect() }
    fun disconnectBle()      = viewModelScope.launch { ble.disconnect() }
    fun startGps() = gps.start()
    fun stopGps()  = gps.stop()
}

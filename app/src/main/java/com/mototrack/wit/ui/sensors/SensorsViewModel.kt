package com.mototrack.wit.ui.sensors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mototrack.wit.ble.BleConnState
import com.mototrack.wit.ble.WitBleManager
import com.mototrack.wit.ble.WitDevice
import com.mototrack.wit.ble.WitProtocol
import com.mototrack.wit.gps.GpsConnState
import com.mototrack.wit.gps.GpsLocationSource
import com.mototrack.wit.gps.GpsSample
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sqrt

data class SensorTelemetry(
    val roll: Float = 0f,
    val pitch: Float = 0f,
    val yaw: Float = 0f,
    val ax: Float = 0f,
    val ay: Float = 0f,
    val az: Float = 0f,
    val wx: Float = 0f,
    val wy: Float = 0f,
    val wz: Float = 0f,
    val gMag: Float = 0f,
    val temp: Float = 0f,
    val battery: Int? = null,
)

@HiltViewModel
class SensorsViewModel @Inject constructor(
    private val ble: WitBleManager,
    private val gps: GpsLocationSource,
) : ViewModel() {

    private val _discovered = MutableStateFlow<List<WitDevice>>(emptyList())
    val discovered: StateFlow<List<WitDevice>> = _discovered.asStateFlow()

    private val _scanning = MutableStateFlow(false)
    val scanning: StateFlow<Boolean> = _scanning.asStateFlow()

    private var scanJob: Job? = null
    private var scanTimeoutJob: Job? = null

    private val lastSample: StateFlow<WitProtocol.Sample> =
        ble.samples.stateIn(viewModelScope, SharingStarted.Eagerly, WitProtocol.Sample())

    val bleConn: StateFlow<BleConnState> = ble.connectionState
    val bleHz: StateFlow<Float> = ble.sampleHz

    val gpsConn: StateFlow<GpsConnState> = gps.connectionState
    val gpsHz: StateFlow<Float> = gps.sampleHz
    val gpsSample: StateFlow<GpsSample> = gps.samples

    val telemetry: StateFlow<SensorTelemetry> = lastSample
        .combine(ble.connectionState) { s, _ ->
            // Intercambiamos X y Z según las pruebas del usuario:
            // En su configuración: Z (Yaw) es la inclinación lateral, X (Roll) es el rumbo/brújula.
            SensorTelemetry(
                roll = s.yaw,   // Usamos Yaw como inclinación lateral (Lean)
                pitch = s.pitch,
                yaw = s.roll,   // Usamos Roll como rumbo (Giro)
                ax = s.ax,
                ay = s.ay,
                az = s.az,
                wx = s.wx,
                wy = s.wy,
                wz = s.wz,
                gMag = sqrt(s.ax * s.ax + s.ay * s.ay + s.az * s.az),
                temp = s.temp,
                battery = s.batteryPct
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000), SensorTelemetry())

    init {
        gps.start()
    }

    fun startScan(timeoutMs: Long = 12_000L) {
        if (_scanning.value) return

        scanJob?.cancel()
        scanTimeoutJob?.cancel()

        scanJob = viewModelScope.launch {
            _scanning.value = true
            _discovered.value = emptyList()

            val seen = linkedMapOf<String, WitDevice>()

            try {
                ble.scan().collect { device ->
                    seen[device.address] = device
                    _discovered.value = seen.values.sortedByDescending { it.rssi }
                }
            } catch (_: Exception) {
                // flujo cerrado normalmente al cancelar el job
            } finally {
                _scanning.value = false
            }
        }

        // Timeout externo: cancela el job de escaneo al expirar.
        scanTimeoutJob = viewModelScope.launch {
            delay(timeoutMs)
            stopScan()
        }
    }

    fun stopScan() {
        scanJob?.cancel()
        scanJob = null
        scanTimeoutJob?.cancel()
        scanTimeoutJob = null
        _scanning.value = false
    }

    fun connect(address: String) {
        stopScan()
        ble.connect(address)
    }

    fun disconnect() {
        ble.disconnect()
    }

    fun setSixAxisMode(enabled: Boolean) = viewModelScope.launch {
        ble.sendCommand(WitProtocol.cmdUnlock())
        delay(100)
        ble.sendCommand(WitProtocol.cmdSetAlgorithm(enabled))
        delay(100)
        ble.sendCommand(WitProtocol.cmdSave())
    }

    fun calibrateAccelerometer() = viewModelScope.launch {
        ble.sendCommand(WitProtocol.cmdUnlock())
        delay(100)
        ble.sendCommand(WitProtocol.cmdCalibrateAccel())
        delay(100)
        ble.sendCommand(WitProtocol.cmdSave())
    }

    override fun onCleared() {
        super.onCleared()
        stopScan()
    }
}

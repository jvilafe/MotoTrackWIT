package com.mototrack.wit.ui.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mototrack.wit.ble.BleState
import com.mototrack.wit.ble.WitBleManager
import com.mototrack.wit.ble.WitDevice
import com.mototrack.wit.fusion.SampleFusionEngine
import com.mototrack.wit.service.RecordingForegroundService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    val ble: WitBleManager,
    val fusion: SampleFusionEngine,
) : ViewModel() {
    val bleState: StateFlow<BleState> = ble.state
    val rssi: StateFlow<Int?> = ble.rssi
    val hz: StateFlow<Float> = fusion.hz
    val recStatus: StateFlow<RecordingForegroundService.Status> = RecordingForegroundService.status
    val sampleCount: StateFlow<Int> = RecordingForegroundService.sampleCount

    private val _devices = MutableStateFlow<List<WitDevice>>(emptyList())
    val devices: StateFlow<List<WitDevice>> = _devices.asStateFlow()

    fun startScan() {
        _devices.value = emptyList()
        viewModelScope.launch {
            ble.scan().collect { dev ->
                if (_devices.value.none { it.address == dev.address })
                    _devices.value = _devices.value + dev
            }
        }
    }
    fun connect(addr: String) = ble.connect(addr)
    fun disconnect() = ble.disconnect()
}

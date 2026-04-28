package com.mototrack.wit.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mototrack.wit.gps.GpsLocationSource
import com.mototrack.wit.gps.GpsSample
import com.mototrack.wit.recording.RecordingController
import com.mototrack.wit.recording.RecordingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import com.mototrack.wit.fusion.FusedSample
import com.mototrack.wit.fusion.SampleFusionEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val gps: GpsLocationSource,
    private val fusion: SampleFusionEngine,
    private val recordingController: RecordingController
) : ViewModel() {

    val gpsSample: StateFlow<GpsSample> = gps.samples
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GpsSample(0, 0.0, 0.0))

    val recordingState: StateFlow<RecordingState> = recordingController.state
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RecordingState.IDLE)

    private val _liveTrail = MutableStateFlow<List<FusedSample>>(emptyList())
    val liveTrail: StateFlow<List<FusedSample>> = _liveTrail.asStateFlow()

    init {
        viewModelScope.launch {
            // Recoger muestras de la fusión para el trazado en vivo
            fusion.flow.collect { sample ->
                if (recordingState.value == RecordingState.RECORDING) {
                    _liveTrail.value = _liveTrail.value + sample
                } else if (recordingState.value == RecordingState.IDLE || recordingState.value == RecordingState.STOPPED) {
                    if (_liveTrail.value.isNotEmpty()) _liveTrail.value = emptyList()
                }
            }
        }
    }

    fun startGps() {
        gps.start()
    }
}

package com.mototrack.wit.ui.debug

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.mototrack.wit.ble.WitBleManager
import com.mototrack.wit.fusion.SampleFusionEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    val ble: WitBleManager, val fusion: SampleFusionEngine
) : ViewModel()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugScreen(nav: NavHostController, vm: DebugViewModel = hiltViewModel()) {
    val hz by vm.fusion.hz.collectAsState()
    val rssi by vm.ble.rssi.collectAsState()
    val state by vm.ble.state.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("Diagnóstico") }) }) { p ->
        Column(Modifier.padding(p).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Estado BLE: $state")
            Text("RSSI: ${rssi ?: "—"} dBm")
            Text("Frecuencia fusión: %.2f Hz".format(hz))
            Text("Objetivo: 25 Hz sostenidos")
            Divider()
            Text("Si Hz < 20: revisar permisos de localización en background y exclusión de batería (MIUI: Apps → MotoTrack WIT → Ahorro de batería → Sin restricciones).")
        }
    }
}

package com.mototrack.wit.ui.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mototrack.wit.recording.RecordingState
import com.mototrack.wit.ui.record.RecordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteTabScreen(
    onOpenMapa: () -> Unit,
    vm: RecordViewModel = hiltViewModel(),
) {
    val hud by vm.hud.collectAsState()
    val stats by vm.statsForUi.collectAsState()
    val name by vm.routeName.collectAsState()

    LaunchedEffect(Unit) { vm.startGps() }

    Scaffold(topBar = { TopAppBar(title = { Text("Ruta") }) }) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Estado BLE
            ElevatedCard(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    val color = if (hud.bleConnected) Color(0xFF22C55E) else Color(0xFFEF4444)
                    Box(
                        Modifier.size(36.dp).background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            if (hud.bleConnected) Icons.Default.Bluetooth else Icons.Default.BluetoothDisabled,
                            contentDescription = null, tint = color,
                        )
                    }
                    Column {
                        Text(
                            if (hud.bleConnected) "Sensor BLE conectado" else "Sensor BLE desconectado",
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            if (hud.bleConnected)
                                "${hud.bleDeviceName ?: "—"}  ·  ${"%.1f".format(hud.bleHz)} Hz"
                            else "Conéctalo desde la pestaña Sensores antes de iniciar.",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }

            // Descripción
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Descripción", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = name,
                        onValueChange = vm::setRouteName,
                        label = { Text("Nombre de la ruta") },
                        singleLine = true,
                        enabled = hud.recording == RecordingState.IDLE ||
                                hud.recording == RecordingState.STOPPED,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // Control
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Control", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        when (hud.recording) {
                            RecordingState.IDLE, RecordingState.STOPPED ->
                                Button(onClick = { vm.start() }, enabled = hud.bleConnected) { Text("Iniciar") }
                            RecordingState.RECORDING -> {
                                OutlinedButton(onClick = { vm.pause() }) { Text("Pausar") }
                                Button(onClick = { vm.stop() }) { Text("Detener") }
                            }
                            RecordingState.PAUSED -> {
                                Button(onClick = { vm.resume() }) { Text("Reanudar") }
                                Button(onClick = { vm.stop() }) { Text("Detener") }
                            }
                        }
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatCell("Estado", labelEs(hud.recording))
                        StatCell("Muestras", hud.sampleCount.toString())
                        StatCell("Duración", formatDuration(hud.durationMs))
                    }
                    if (hud.recording == RecordingState.STOPPED) {
                        Button(onClick = onOpenMapa, modifier = Modifier.fillMaxWidth()) {
                            Text("Ver en mapa")
                        }
                    }
                }
            }

            // Máximos en vivo
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Máximos", style = MaterialTheme.typography.titleMedium)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatCell("Incl. izq.", "%.1f°".format(stats.maxRollLeft))
                        StatCell("Incl. der.", "%.1f°".format(stats.maxRollRight))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatCell("Vel. máx.", "%.1f km/h".format(stats.maxSpeedKmh))
                        StatCell("G accel.", "%.2f g".format(stats.maxAccelG))
                        StatCell("G freno", "%.2f g".format(-stats.maxBrakeG))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCell(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}

private fun labelEs(s: RecordingState): String = when (s) {
    RecordingState.IDLE -> "Listo"
    RecordingState.RECORDING -> "Grabando"
    RecordingState.PAUSED -> "Pausada"
    RecordingState.STOPPED -> "Detenida"
}

private fun formatDuration(ms: Long): String {
    val s = ms / 1000
    return "%02d:%02d:%02d".format(s / 3600, (s % 3600) / 60, s % 60)
}


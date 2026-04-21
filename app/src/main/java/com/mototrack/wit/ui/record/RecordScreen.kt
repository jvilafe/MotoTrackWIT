package com.mototrack.wit.ui.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mototrack.wit.recording.RecordingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(vm: RecordViewModel = hiltViewModel()) {
    val hud by vm.hud.collectAsState()
    val name by vm.routeName.collectAsState()

    LaunchedEffect(Unit) { vm.startGps() }

    Scaffold(topBar = { TopAppBar(title = { Text("MotoTrack WIT") }) }) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ---- ESTADO DE SENSORES ----
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Sensores", style = MaterialTheme.typography.titleMedium)

                    SensorRow(
                        connected = hud.bleConnected,
                        iconOn = Icons.Default.Bluetooth,
                        iconOff = Icons.Default.BluetoothDisabled,
                        label = "WT901BLECL5.0",
                        detail = if (hud.bleConnected)
                            "${hud.bleDeviceName ?: "—"}  ·  ${"%.1f".format(hud.bleHz)} Hz  ·  RSSI ${hud.bleRssi ?: "—"}"
                        else "Desconectado"
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { vm.scanAndConnectBle() }, enabled = !hud.bleConnected) {
                            Text("Buscar y conectar")
                        }
                        OutlinedButton(onClick = { vm.disconnectBle() }, enabled = hud.bleConnected) {
                            Text("Desconectar")
                        }
                    }

                    Divider()

                    SensorRow(
                        connected = hud.gpsFix,
                        iconOn = Icons.Default.GpsFixed,
                        iconOff = Icons.Default.GpsOff,
                        label = "GPS interno",
                        detail = when {
                            !hud.gpsProviderEnabled -> "Proveedor desactivado"
                            !hud.gpsFix -> "Buscando fix…"
                            else -> "${"%.1f".format(hud.gpsHz)} Hz  ·  ±${hud.hAcc?.let { "%.0f".format(it) } ?: "—"} m"
                        }
                    )
                }
            }

            // ---- HUD EN VIVO ----
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Telemetría en vivo", style = MaterialTheme.typography.titleMedium)

                    BigMetric("Velocidad", "%.1f".format(hud.speedKmh), "km/h")

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Metric("Roll (incl.)", "%+.1f°".format(hud.roll))
                        Metric("Pitch", "%+.1f°".format(hud.pitch))
                        Metric("Yaw", "%.1f°".format(hud.yaw))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Metric("Ax", "%+.2f g".format(hud.ax))
                        Metric("Ay", "%+.2f g".format(hud.ay))
                        Metric("Az", "%+.2f g".format(hud.az))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Metric("ωx", "%+.0f°/s".format(hud.wx))
                        Metric("ωy", "%+.0f°/s".format(hud.wy))
                        Metric("ωz", "%+.0f°/s".format(hud.wz))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Metric("|G|", "%.2f g".format(hud.gMag))
                        Metric("Temp", "%.1f°C".format(hud.temp))
                        Metric("Bat", hud.battery?.let { "$it%" } ?: "—")
                    }

                    Divider()
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Metric("Lat", hud.lat?.let { "%.6f".format(it) } ?: "—")
                        Metric("Lon", hud.lon?.let { "%.6f".format(it) } ?: "—")
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Metric("Alt", hud.altitude?.let { "%.0f m".format(it) } ?: "—")
                        Metric("Rumbo", hud.bearing?.let { "%.0f°".format(it) } ?: "—")
                    }
                }
            }

            // ---- CONTROL DE RUTA ----
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ruta", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = name, onValueChange = vm::setRouteName,
                        label = { Text("Nombre de la ruta") },
                        singleLine = true, modifier = Modifier.fillMaxWidth()
                    )

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
                        Metric("Estado", hud.recording.name)
                        Metric("Muestras", hud.sampleCount.toString())
                        Metric("Duración", formatDuration(hud.durationMs))
                    }
                }
            }
        }
    }
}

@Composable
private fun SensorRow(
    connected: Boolean,
    iconOn: androidx.compose.ui.graphics.vector.ImageVector,
    iconOff: androidx.compose.ui.graphics.vector.ImageVector,
    label: String, detail: String
) {
    val color = if (connected) Color(0xFF22C55E) else Color(0xFFEF4444)
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            Modifier.size(36.dp).background(color.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) { Icon(if (connected) iconOn else iconOff, contentDescription = null, tint = color) }
        Column {
            Text(label, fontWeight = FontWeight.SemiBold)
            Text(detail, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun BigMetric(label: String, value: String, unit: String) {
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(value, fontSize = 44.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        Text(unit, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
        Spacer(Modifier.weight(1f))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(value, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium)
    }
}

private fun formatDuration(ms: Long): String {
    val s = ms / 1000
    return "%02d:%02d:%02d".format(s/3600, (s%3600)/60, s%60)
}

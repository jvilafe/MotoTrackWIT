package com.mototrack.wit.ui.sensors

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsOff
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mototrack.wit.ble.WitDevice

@Composable
fun SensorsTabScreen(vm: SensorsViewModel = hiltViewModel()) {
    val scanning by vm.scanning.collectAsState()
    val discovered by vm.discovered.collectAsState()
    val bleConn by vm.bleConn.collectAsState()
    val bleHz by vm.bleHz.collectAsState()
    val gpsConn by vm.gpsConn.collectAsState()
    val gpsHz by vm.gpsHz.collectAsState()
    val gpsSample by vm.gpsSample.collectAsState()
    val telemetry by vm.telemetry.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Sensores") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("WIT BLE", style = MaterialTheme.typography.titleMedium)

                        SensorStatusRow(
                            connected = bleConn.connected,
                            iconOn = Icons.Default.Bluetooth,
                            iconOff = Icons.Default.BluetoothDisabled,
                            label = bleConn.name ?: "WT901BLECL5.0",
                            detail = if (bleConn.connected) {
                                buildString {
                                    append("Conectado")
                                    append(" · ")
                                    append("%.1f".format(bleHz))
                                    append(" Hz")
                                    bleConn.rssi?.let {
                                        append(" · RSSI ")
                                        append(it)
                                    }
                                }
                            } else {
                                "Desconectado"
                            }
                        )

                        Divider()

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { vm.startScan() },
                                enabled = !scanning && !bleConn.connected
                            ) {
                                Text(if (scanning) "Buscando..." else "Buscar sensores")
                            }

                            OutlinedButton(
                                onClick = { vm.disconnect() },
                                enabled = bleConn.connected
                            ) {
                                Text("Desconectar")
                            }
                        }

                        if (discovered.isNotEmpty()) {
                            Spacer(Modifier.height(4.dp))
                            Text("Dispositivos encontrados", style = MaterialTheme.typography.labelLarge)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                discovered.forEach { device ->
                                    DeviceRow(
                                        device = device,
                                        onConnect = { vm.connect(device.address) }
                                    )
                                }
                            }
                        } else if (!scanning && !bleConn.connected) {
                            Text(
                                "Pulsa \"Buscar sensores\" para localizar el WitMotion.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Divider()

                        Text("Actividad en vivo", style = MaterialTheme.typography.labelLarge)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Metric("Roll", "%+.1f°".format(telemetry.roll))
                            Metric("Pitch", "%+.1f°".format(telemetry.pitch))
                            Metric("Yaw", "%.1f°".format(telemetry.yaw))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Metric("Ax", "%+.2f g".format(telemetry.ax))
                            Metric("Ay", "%+.2f g".format(telemetry.ay))
                            Metric("Az", "%+.2f g".format(telemetry.az))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Metric("|G|", "%.2f g".format(telemetry.gMag))
                            Metric("Temp", "%.1f°C".format(telemetry.temp))
                            Metric("Bat", telemetry.battery?.let { "$it%" } ?: "—")
                        }
                    }
                }
            }

            item {
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("GPS interno", style = MaterialTheme.typography.titleMedium)

                        SensorStatusRow(
                            connected = gpsConn.providerEnabled,
                            iconOn = Icons.Default.GpsFixed,
                            iconOff = Icons.Default.GpsOff,
                            label = "GPS del dispositivo",
                            detail = when {
                                !gpsConn.providerEnabled -> "Proveedor desactivado"
                                !gpsConn.hasFix -> "Buscando fix..."
                                else -> {
                                    buildString {
                                        append("Con fix")
                                        append(" · ")
                                        append("%.1f".format(gpsHz))
                                        append(" Hz")
                                        append(" · ±")
                                        append("%.0f".format(gpsSample.hAcc))
                                        append(" m")
                                    }
                                }
                            }
                        )

                        Divider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Metric("Lat", if (gpsSample.lat != 0.0) "%.6f".format(gpsSample.lat) else "—")
                            Metric("Lon", if (gpsSample.lon != 0.0) "%.6f".format(gpsSample.lon) else "—")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Metric("Velocidad", "%.1f km/h".format(gpsSample.speedMs * 3.6f))
                            Metric("Altitud", if (gpsSample.altitude != 0.0) "%.0f m".format(gpsSample.altitude) else "—")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Metric("Rumbo", if (gpsSample.bearing != 0f) "%.0f°".format(gpsSample.bearing) else "—")
                            Metric("Precisión", if (gpsSample.hAcc != 0f) "±%.0f m".format(gpsSample.hAcc) else "—")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SensorStatusRow(
    connected: Boolean,
    iconOn: androidx.compose.ui.graphics.vector.ImageVector,
    iconOff: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    detail: String,
) {
    val color = if (connected) Color(0xFF22C55E) else Color(0xFFEF4444)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(color.copy(alpha = 0.14f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (connected) iconOn else iconOff,
                contentDescription = null,
                tint = color
            )
        }

        Column {
            Text(label, fontWeight = FontWeight.SemiBold)
            Text(detail, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun DeviceRow(
    device: WitDevice,
    onConnect: () -> Unit,
) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Memory, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(device.name ?: "WitMotion", fontWeight = FontWeight.SemiBold)
                Text(
                    "${device.address} · RSSI ${device.rssi}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(onClick = onConnect) {
                Text("Conectar")
            }
        }
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Text(
            value,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MapPlaceholderScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mapa") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Map, contentDescription = null)
                Text("Pantalla de mapa pendiente de la siguiente tanda")
            }
        }
    }
}

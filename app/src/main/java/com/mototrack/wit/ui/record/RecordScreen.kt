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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ruta en Curso") },
                actions = {
                    // Indicadores rápidos de estado en la topbar
                    StatusIcon(connected = hud.bleConnected, iconOn = Icons.Default.Bluetooth, iconOff = Icons.Default.BluetoothDisabled)
                    Spacer(Modifier.width(8.dp))
                    StatusIcon(connected = hud.gpsFix, iconOn = Icons.Default.GpsFixed, iconOff = Icons.Default.GpsOff)
                    Spacer(Modifier.width(12.dp))
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ---- DASHBOARD PRINCIPAL (Métricas grandes) ----
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Velocidad
                Card(
                    modifier = Modifier.weight(1f).height(140.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("VELOCIDAD", style = MaterialTheme.typography.labelMedium)
                        Text(
                            "%.0f".format(hud.speedKmh),
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text("km/h", style = MaterialTheme.typography.labelSmall)
                    }
                }

                // Inclinación actual con HUD tipo MotoGP
                Card(
                    modifier = Modifier.weight(1f).height(140.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        MotoGpHud(leanAngle = hud.roll, modifier = Modifier.size(120.dp))
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("INCLINACIÓN", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                            Text(
                                "%.0f°".format(if (hud.roll < 0) -hud.roll else hud.roll),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = if (hud.roll > 0) Color(0xFFEF4444) else if (hud.roll < 0) Color(0xFF3B82F6) else MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(if (hud.roll > 0) "DER" else if (hud.roll < 0) "IZQ" else "RECTO", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp))
                        }
                    }
                }
            }

            // ---- FILA 2: PICADO Y BRÚJULA ----
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Picado / Caballito
                Card(
                    modifier = Modifier.weight(1f).height(120.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        PitchHud(pitchAngle = hud.pitch, modifier = Modifier.size(100.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("PICADO", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                            Text(
                                "%.0f°".format(hud.pitch),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                // Brújula
                Card(
                    modifier = Modifier.weight(1f).height(120.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val heading = if (hud.yaw < 0) hud.yaw + 360 else hud.yaw
                        CompassHud(heading = heading, modifier = Modifier.size(100.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("BRÚJULA", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp))
                            Text(
                                "%.0f°".format(heading),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // ---- VALORES MÁXIMOS (Objetivo 4) ----
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Máximos de la Sesión", style = MaterialTheme.typography.titleMedium)
                    }

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        MaxMetric("Incl. Izq.", "%.1f°".format(hud.maxRollRight)) // maxRollRight es -roll
                        MaxMetric("Incl. Der.", "%.1f°".format(hud.maxRollLeft))  // maxRollLeft es roll
                        MaxMetric("Vel. Máx.", "%.1f".format(hud.maxSpeedKmh))
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        MaxMetric("Acel. Máx.", "%.2f G".format(hud.maxAccelG))
                        MaxMetric("Frenada Máx.", "%.2f G".format(hud.maxBrakeG))
                        MaxMetric("Muestras", hud.sampleCount.toString())
                    }
                }
            }

            // ---- CONTROL DE GRABACIÓN ----
            ElevatedCard(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (hud.recording == RecordingState.RECORDING)
                        Color(0xFFFEF2F2) else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Grabación de Ruta", style = MaterialTheme.typography.titleMedium)

                    OutlinedTextField(
                        value = name,
                        onValueChange = vm::setRouteName,
                        label = { Text("Nombre / Descripción de la ruta") },
                        placeholder = { Text("Ej: Salida Domingo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = hud.recording == RecordingState.IDLE || hud.recording == RecordingState.STOPPED
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        when (hud.recording) {
                            RecordingState.IDLE, RecordingState.STOPPED -> {
                                Button(
                                    onClick = { vm.start() },
                                    modifier = Modifier.weight(1f).height(56.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("INICIAR RUTA", fontWeight = FontWeight.Bold)
                                }
                            }
                            RecordingState.RECORDING -> {
                                OutlinedButton(
                                    onClick = { vm.pause() },
                                    modifier = Modifier.weight(1f).height(56.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Pause, null)
                                    Text("PAUSAR")
                                }
                                Button(
                                    onClick = { vm.stop() },
                                    modifier = Modifier.weight(1f).height(56.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Stop, null)
                                    Text("DETENER")
                                }
                            }
                            RecordingState.PAUSED -> {
                                Button(
                                    onClick = { vm.resume() },
                                    modifier = Modifier.weight(1f).height(56.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, null)
                                    Text("REANUDAR")
                                }
                                Button(
                                    onClick = { vm.stop() },
                                    modifier = Modifier.weight(1f).height(56.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Stop, null)
                                    Text("DETENER")
                                }
                            }
                        }
                    }

                    if (hud.recording != RecordingState.IDLE) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier.size(8.dp).background(
                                    if (hud.recording == RecordingState.RECORDING) Color.Red else Color.Gray,
                                    RoundedCornerShape(50)
                                )
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Duración: ${formatDuration(hud.durationMs)}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // ---- TELEMETRÍA SECUNDARIA ----
            Text("Más datos", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Metric("Acel. X/Y", "%+.2f / %+.2f g".format(hud.ax, hud.ay))
                Metric("G Total", "%.2f g".format(hud.gMag))
                val batStr = hud.battery?.toString() ?: "—"
                Metric("Temp/Bat", "%.1f°C / %s%%".format(hud.temp, batStr))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Metric("Latitud", hud.lat?.let { "%.6f".format(it) } ?: "—")
                Metric("Longitud", hud.lon?.let { "%.6f".format(it) } ?: "—")
            }
        }
    }
}

@Composable
private fun MotoGpHud(leanAngle: Float, modifier: Modifier = Modifier) {
    val colorBase = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f)
    val colorLeft = Color(0xFF3B82F6) // Azul para izquierda
    val colorRight = Color(0xFFEF4444) // Rojo para derecha

    Canvas(modifier = modifier.padding(8.dp)) {
        val sweepLimit = 90f // Hasta 90 grados
        val startAngle = 180f // El arco empieza a la izquierda (180 grados en coordenadas de Canvas es la izquierda)
        
        // Dibujamos el arco de fondo (guía)
        drawArc(
            color = colorBase,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
        )

        // Dibujamos el arco de inclinación actual
        // En Canvas, el ángulo 0 es a la derecha. 180 es a la izquierda. 270 es arriba.
        // Queremos que 0 de inclinación sea 270 (arriba).
        if (leanAngle != 0f) {
            drawArc(
                color = if (leanAngle > 0) colorRight else colorLeft,
                startAngle = 270f,
                sweepAngle = leanAngle, // Lean positivo (derecha) va en sentido horario. Negativo (izq) antihorario.
                useCenter = false,
                style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Línea central de referencia
        drawLine(
            color = colorBase,
            start = center.copy(y = 0f),
            end = center.copy(y = 15.dp.toPx()),
            strokeWidth = 2.dp.toPx()
        )
    }
}

@Composable
private fun PitchHud(pitchAngle: Float, modifier: Modifier = Modifier) {
    val colorBase = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.15f)
    val colorAccent = MaterialTheme.colorScheme.tertiary

    Canvas(modifier = modifier.padding(12.dp)) {
        // Fondo: una línea vertical con marcas
        drawLine(colorBase, start = center.copy(y = 0f), end = center.copy(y = size.height), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
        
        // Marcas de escala (cada 30 grados)
        for (i in -2..2) {
            val y = center.y + (i * size.height / 5f)
            drawLine(colorBase, start = center.copy(x = center.x - 10.dp.toPx(), y = y), end = center.copy(x = center.x + 10.dp.toPx(), y = y), strokeWidth = 2.dp.toPx())
        }

        // Indicador de picado actual: un triángulo o círculo que sube/baja
        // Mapeamos -90..90 a 0..height (invertido porque Y crece hacia abajo)
        val yPos = (center.y - (pitchAngle / 90f * (size.height / 2f))).coerceIn(0f, size.height)
        
        drawCircle(
            color = colorAccent,
            radius = 6.dp.toPx(),
            center = center.copy(y = yPos)
        )
    }
}

@Composable
private fun CompassHud(heading: Float, modifier: Modifier = Modifier) {
    val colorBase = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
    val colorN = Color(0xFFEF4444) // Rojo para el Norte
    val colorOther = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(modifier = modifier.padding(8.dp)) {
        val radius = size.minDimension / 2
        
        // Círculo exterior
        drawCircle(colorBase, radius = radius, style = Stroke(width = 2.dp.toPx()))

        // Dibujamos las marcas de la brújula rotadas
        // Queremos que el ángulo 'heading' esté arriba (0 grados en brújula es Norte)
        // En Canvas 0 es Derecha, 270 es Arriba.
        // Rotación: -heading + 270
        val rotation = -heading + 270f

        // Marcas principales (N, E, S, W)
        val points = listOf(0f to "N", 90f to "E", 180f to "S", 270f to "W")
        points.forEach { (angle, label) ->
            val rad = Math.toRadians((angle + rotation).toDouble())
            val start = center.copy(
                x = center.x + (radius - 12.dp.toPx()) * Math.cos(rad).toFloat(),
                y = center.y + (radius - 12.dp.toPx()) * Math.sin(rad).toFloat()
            )
            val end = center.copy(
                x = center.x + radius * Math.cos(rad).toFloat(),
                y = center.y + radius * Math.sin(rad).toFloat()
            )
            drawLine(if (label == "N") colorN else colorOther, start, end, strokeWidth = if (label == "N") 4.dp.toPx() else 2.dp.toPx())
        }
        
        // Puntero central (fijo arriba)
        drawLine(
            color = Color.Black,
            start = center.copy(y = center.y - radius),
            end = center.copy(y = center.y - radius + 15.dp.toPx()),
            strokeWidth = 3.dp.toPx()
        )
    }
}

@Composable
private fun StatusIcon(connected: Boolean, iconOn: androidx.compose.ui.graphics.vector.ImageVector, iconOff: androidx.compose.ui.graphics.vector.ImageVector) {
    val color = if (connected) Color(0xFF22C55E) else Color(0xFFEF4444)
    Icon(
        if (connected) iconOn else iconOff,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
private fun MaxMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Text(value, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium)
    }
}

private fun formatDuration(ms: Long): String {
    val s = ms / 1000
    return "%02d:%02d:%02d".format(s/3600, (s%3600)/60, s%60)
}

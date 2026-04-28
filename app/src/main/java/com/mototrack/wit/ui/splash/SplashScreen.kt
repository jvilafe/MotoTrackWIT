package com.mototrack.wit.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val ducatiRed = Color(0xCE, 0x11, 0x26)
    
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1500), label = ""
    )

    val scanProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        startAnimation = true
        scanProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2500, easing = LinearEasing)
        )
        delay(300)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F14)),
        contentAlignment = Alignment.Center
    ) {
        // Estética HUD / Radar de fondo
        Canvas(modifier = Modifier.size(300.dp).alpha(0.2f * alphaAnim)) {
            drawCircle(
                color = ducatiRed,
                radius = size.minDimension / 2,
                style = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                color = ducatiRed,
                radius = size.minDimension / 4,
                style = Stroke(width = 0.5.dp.toPx())
            )
            
            // Líneas de horizonte artificial / retícula
            drawLine(
                color = ducatiRed,
                start = center.copy(x = 0f),
                end = center.copy(x = size.width),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = ducatiRed,
                start = center.copy(y = 0f),
                end = center.copy(y = size.height),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Animación de barrido circular (escaneo de sistemas)
        Canvas(modifier = Modifier.size(320.dp).alpha(alphaAnim)) {
            val sweepAngle = scanProgress.value * 360f
            drawArc(
                color = ducatiRed,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = true,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                alpha = 0.3f
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MOTOTRACKER",
                color = ducatiRed,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.alpha(alphaAnim)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            val systemText = if (scanProgress.value < 0.3f) "INITIALIZING..."
                             else if (scanProgress.value < 0.6f) "BLE SENSORS: OK"
                             else if (scanProgress.value < 0.9f) "GPS ENGINE: READY"
                             else "SYSTEM READY"

            Text(
                text = systemText,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.alpha(alphaAnim)
            )
        }
        
        // Indicador de versión
        Text(
            text = "PROYECTO PRIVADO v0.1",
            color = Color.Gray.copy(alpha = 0.5f),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
                .alpha(alphaAnim)
        )
    }
}

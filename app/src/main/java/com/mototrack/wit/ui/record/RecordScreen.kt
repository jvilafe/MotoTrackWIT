package com.mototrack.wit.ui.record

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mototrack.wit.ble.BleState
import com.mototrack.wit.service.RecordingForegroundService

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun RecordScreen(nav: NavHostController, vm: RecordViewModel = hiltViewModel()) {
    val ctx = LocalContext.current
    val perms = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= 31) {
            add(Manifest.permission.BLUETOOTH_SCAN)
            add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (Build.VERSION.SDK_INT >= 33) add(Manifest.permission.POST_NOTIFICATIONS)
    }
    val permState = rememberMultiplePermissionsState(perms)
    LaunchedEffect(Unit) { if (!permState.allPermissionsGranted) permState.launchMultiplePermissionRequest() }

    val bleState by vm.bleState.collectAsState()
    val devices by vm.devices.collectAsState()
    val hz by vm.hz.collectAsState()
    val rec by vm.recStatus.collectAsState()
    val count by vm.sampleCount.collectAsState()
    val rssi by vm.rssi.collectAsState()
    var routeName by remember { mutableStateOf("Ruta moto") }

    Scaffold(topBar = {
        TopAppBar(title = { Text("MotoTrack WIT") }, actions = {
            IconButton(onClick = { nav.navigate("routes") }) { Icon(Icons.Default.List, null) }
            IconButton(onClick = { nav.navigate("debug") }) { Icon(Icons.Default.BugReport, null) }
        })
    }) { p ->
        Column(Modifier.padding(p).padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Sensor BLE", style = MaterialTheme.typography.titleMedium)
                    Text("Estado: ${bleState::class.simpleName} ${rssi?.let { "RSSI $it dBm" } ?: ""}")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { vm.startScan() }) { Text("Buscar") }
                        OutlinedButton(onClick = { vm.disconnect() }) { Text("Desconectar") }
                    }
                    if (devices.isNotEmpty() && bleState !is BleState.Connected) {
                        LazyColumn(Modifier.heightIn(max = 180.dp)) {
                            items(devices) { d ->
                                ListItem(
                                    headlineContent = { Text(d.name ?: d.address) },
                                    supportingContent = { Text("${d.address} · ${d.rssi} dBm") },
                                    trailingContent = {
                                        TextButton(onClick = { vm.connect(d.address) }) { Text("Conectar") }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            OutlinedTextField(value = routeName, onValueChange = { routeName = it },
                label = { Text("Nombre de la ruta") }, modifier = Modifier.fillMaxWidth())

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Grabación: $rec", style = MaterialTheme.typography.titleMedium)
                    Text("Muestras: $count   ·   Hz: %.1f".format(hz))
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(enabled = rec == RecordingForegroundService.Status.IDLE && bleState is BleState.Connected,
                            onClick = { sendAction(ctx, RecordingForegroundService.ACTION_START, routeName) }) {
                            Icon(Icons.Default.PlayArrow, null); Text("Iniciar")
                        }
                        Button(enabled = rec == RecordingForegroundService.Status.RECORDING,
                            onClick = { sendAction(ctx, RecordingForegroundService.ACTION_PAUSE) }) {
                            Icon(Icons.Default.Pause, null); Text("Pausar")
                        }
                        Button(enabled = rec == RecordingForegroundService.Status.PAUSED,
                            onClick = { sendAction(ctx, RecordingForegroundService.ACTION_RESUME) }) {
                            Icon(Icons.Default.PlayArrow, null); Text("Reanudar")
                        }
                        Button(enabled = rec != RecordingForegroundService.Status.IDLE,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            onClick = { sendAction(ctx, RecordingForegroundService.ACTION_STOP) }) {
                            Icon(Icons.Default.Stop, null); Text("Finalizar")
                        }
                    }
                }
            }
        }
    }
}

private fun sendAction(ctx: android.content.Context, action: String, routeName: String? = null) {
    val i = Intent(ctx, RecordingForegroundService::class.java).setAction(action)
    if (routeName != null) i.putExtra(RecordingForegroundService.EXTRA_ROUTE_NAME, routeName)
    if (Build.VERSION.SDK_INT >= 26) ctx.startForegroundService(i) else ctx.startService(i)
}

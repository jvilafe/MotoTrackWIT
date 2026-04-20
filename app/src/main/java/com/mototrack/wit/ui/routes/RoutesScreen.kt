package com.mototrack.wit.ui.routes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.work.*
import com.mototrack.wit.data.RouteRepository
import com.mototrack.wit.data.db.RouteEntity
import com.mototrack.wit.drive.UploadWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

@HiltViewModel
class RoutesViewModel @Inject constructor(
    private val repo: RouteRepository,
    @ApplicationContext private val ctx: Context,
) : ViewModel() {
    val routes: StateFlow<List<RouteEntity>> =
        repo.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    fun delete(id: Long) = viewModelScope.launch { repo.delete(id) }
    fun syncToDrive(id: Long) {
        val req = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputData(workDataOf("routeId" to id))
            .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build())
            .build()
        WorkManager.getInstance(ctx).enqueue(req)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutesScreen(nav: NavHostController, vm: RoutesViewModel = hiltViewModel()) {
    val routes by vm.routes.collectAsState()
    val df = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    Scaffold(topBar = { TopAppBar(title = { Text("Rutas") }) }) { p ->
        LazyColumn(Modifier.padding(p).fillMaxSize()) {
            items(routes) { r ->
                ListItem(
                    headlineContent = { Text(r.name) },
                    supportingContent = {
                        Text("${df.format(Date(r.startedAt))} · ${"%.1f".format(r.distanceM/1000)} km · max ${"%.0f".format(r.maxSpeed*3.6)} km/h")
                    },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { vm.syncToDrive(r.id) }) { Icon(Icons.Default.CloudUpload, null) }
                            IconButton(onClick = { vm.delete(r.id) }) { Icon(Icons.Default.Delete, null) }
                        }
                    },
                    modifier = Modifier.clickable { nav.navigate("detail/${r.id}") }
                )
                HorizontalDivider()
            }
        }
    }
}

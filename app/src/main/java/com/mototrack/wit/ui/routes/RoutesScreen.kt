package com.mototrack.wit.ui.routes

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.mototrack.wit.data.RouteRepository
import com.mototrack.wit.data.db.RouteEntity
import com.mototrack.wit.data.export.MtwExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RoutesViewModel @Inject constructor(
    private val repo: RouteRepository,
) : ViewModel() {

    val routes: StateFlow<List<RouteEntity>> =
        repo.observeAll().stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _exportEvents = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val exportEvents: SharedFlow<String> = _exportEvents.asSharedFlow()

    fun delete(id: Long) = viewModelScope.launch {
        repo.delete(id)
    }

    fun exportMtw(context: Context, route: RouteEntity) = viewModelScope.launch {
        try {
            val samples = repo.samples(route.id)
            if (samples.isEmpty()) {
                _exportEvents.tryEmit("La ruta no tiene puntos para exportar.")
                return@launch
            }

            val fileName = buildFileName(route)
            val savedPath = withContext(Dispatchers.IO) {
                writeMtwToDocuments(context, fileName) { tmpFile ->
                    MtwExporter.write(tmpFile, route, samples)
                }
            }
            _exportEvents.tryEmit("Guardado: $savedPath")
        } catch (t: Throwable) {
            _exportEvents.tryEmit("Error al exportar: ${t.message ?: t.javaClass.simpleName}")
        }
    }

    private fun buildFileName(route: RouteEntity): String {
        val ts = SimpleDateFormat("yyyyMMdd_HHmm", Locale.US).format(Date(route.startedAt))
        val safeName = route.name
            .trim()
            .replace(Regex("[^A-Za-z0-9._-]+"), "_")
            .ifEmpty { "ruta" }
        return "${safeName}_$ts.mtw"
    }

    /**
     * Escribe el .mtw en /Documents/MotoTrackWIT/.
     * - API 29+: usa MediaStore (sin permisos).
     * - API < 29: escribe directamente con WRITE_EXTERNAL_STORAGE (ya en Manifest).
     * El bloque [writeBlock] recibe un fichero temporal donde MtwExporter escribe;
     * después se copia al destino final.
     */
    private fun writeMtwToDocuments(
        context: Context,
        fileName: String,
        writeBlock: (File) -> Unit,
    ): String {
        val cacheTmp = File(context.cacheDir, fileName).apply { if (exists()) delete() }
        writeBlock(cacheTmp)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val relPath = "${Environment.DIRECTORY_DOCUMENTS}/MotoTrackWIT"
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
                put(MediaStore.MediaColumns.RELATIVE_PATH, relPath)
            }
            val uri = resolver.insert(MediaStore.Files.getContentUri("external"), values)
                ?: error("No se pudo crear el fichero en MediaStore")
            resolver.openOutputStream(uri)?.use { out ->
                cacheTmp.inputStream().use { it.copyTo(out) }
            } ?: error("No se pudo abrir el fichero para escritura")
            cacheTmp.delete()
            "Documents/MotoTrackWIT/$fileName"
        } else {
            @Suppress("DEPRECATION")
            val docsDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "MotoTrackWIT"
            )
            if (!docsDir.exists()) docsDir.mkdirs()
            val dest = File(docsDir, fileName)
            FileOutputStream(dest).use { out ->
                cacheTmp.inputStream().use { it.copyTo(out) }
            }
            cacheTmp.delete()
            dest.absolutePath
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutesScreen(nav: NavHostController, vm: RoutesViewModel = hiltViewModel()) {
    val routes by vm.routes.collectAsState()
    val df = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vm.exportEvents.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Rutas") }) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            items(routes) { route ->
                ListItem(
                    headlineContent = { Text(route.name) },
                    supportingContent = {
                        Text(
                            "${df.format(Date(route.startedAt))} · " +
                                    "${"%.1f".format(route.distanceM / 1000)} km · " +
                                    "max ${"%.0f".format(route.maxSpeed * 3.6)} km/h"
                        )
                    },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { vm.exportMtw(context, route) }) {
                                Icon(
                                    Icons.Default.FileDownload,
                                    contentDescription = "Exportar .mtw"
                                )
                            }
                            IconButton(onClick = { vm.delete(route.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar ruta")
                            }
                        }
                    },
                    modifier = Modifier.clickable {
                        nav.navigate("detail/${route.id}")
                    }
                )
                HorizontalDivider()
            }
        }
    }
}

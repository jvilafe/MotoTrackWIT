package com.mototrack.wit.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.mototrack.wit.R
import com.mototrack.wit.data.RouteRepository
import com.mototrack.wit.data.db.RouteEntity
import com.mototrack.wit.data.db.SampleEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import javax.inject.Inject

@HiltViewModel
class RouteDetailViewModel @Inject constructor(
    private val repo: RouteRepository,
) : ViewModel() {
    private val _route = MutableStateFlow<RouteEntity?>(null)
    val route: StateFlow<RouteEntity?> = _route.asStateFlow()
    private val _samples = MutableStateFlow<List<SampleEntity>>(emptyList())
    val samples: StateFlow<List<SampleEntity>> = _samples.asStateFlow()

    fun load(id: Long) = viewModelScope.launch {
        _route.value = repo.get(id)
        _samples.value = repo.samples(id)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailScreen(id: Long, nav: NavHostController, vm: RouteDetailViewModel = hiltViewModel()) {
    LaunchedEffect(id) { vm.load(id) }
    val route by vm.route.collectAsState()
    val samples by vm.samples.collectAsState()
    var selected by remember { mutableStateOf<SampleEntity?>(null) }

    Scaffold(topBar = { TopAppBar(title = { Text(route?.name ?: "Ruta") }) }) { p ->
        Column(Modifier.padding(p).fillMaxSize()) {
            // Mapa MapLibre + tiles OpenStreetMap (gratis, sin API key)
            Box(Modifier.weight(1f).fillMaxWidth()) {
                AndroidView(factory = { ctx ->
                    // Inicialización requerida una vez. No necesita token (null).
                    MapLibre.getInstance(ctx)
                    MapView(ctx).apply {
                        onCreate(null)
                        getMapAsync { map ->
                            // Carga el estilo OSM definido en res/raw/osm_style.json
                            val styleJson = ctx.resources.openRawResource(R.raw.osm_style)
                                .bufferedReader().use { it.readText() }
                            map.setStyle(Style.Builder().fromJson(styleJson)) { style ->
                                if (samples.isNotEmpty()) {
                                    val pts = samples.map { Point.fromLngLat(it.lon, it.lat) }
                                    val line = LineString.fromLngLats(pts)
                                    style.addSource(GeoJsonSource("route-src", Feature.fromGeometry(line)))
                                    style.addLayer(
                                        LineLayer("route-layer", "route-src").withProperties(
                                            PropertyFactory.lineColor("#FF6B35"),
                                            PropertyFactory.lineWidth(5.0f)
                                        )
                                    )
                                    // Encuadrar la cámara sobre el trazado
                                    val bounds = LatLngBounds.Builder().apply {
                                        pts.forEach { include(LatLng(it.latitude(), it.longitude())) }
                                    }.build()
                                    map.cameraPosition = CameraPosition.Builder()
                                        .target(bounds.center)
                                        .zoom(13.0)
                                        .build()
                                }
                            }
                        }
                    }
                })
            }
            // Stats
            route?.let { r ->
                Card(Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Estadísticas", style = MaterialTheme.typography.titleMedium)
                        Text("Distancia: %.2f km".format(r.distanceM/1000))
                        Text("Velocidad max: %.0f km/h · media: %.0f km/h".format(r.maxSpeed*3.6, r.avgSpeed*3.6))
                        Text("Aceleración max: %.2f m/s² · Frenada max: %.2f m/s²".format(r.maxAccel, r.maxBrake))
                        Text("Inclinación izq max: %.1f° · der max: %.1f°".format(r.maxRollLeft, r.maxRollRight))
                        Text("G máxima: %.2f g".format(r.maxG))
                        Text("Muestras: ${samples.size}")
                    }
                }
            }
            selected?.let { s ->
                Card(Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Punto seleccionado", style = MaterialTheme.typography.titleSmall)
                        Text("v=%.1f km/h roll=%.1f° pitch=%.1f° |g|=%.2f".format(
                            s.vGps*3.6, s.roll, s.pitch, s.gMag))
                    }
                }
            }
        }
    }
}

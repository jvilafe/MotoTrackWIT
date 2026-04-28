package com.mototrack.wit.ui.map

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.mototrack.wit.R
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point

@Composable
fun MapTabScreen(vm: MapViewModel = hiltViewModel()) {
    val gpsSample by vm.gpsSample.collectAsState()
    val liveTrail by vm.liveTrail.collectAsState()
    val context = LocalContext.current
    
    var mapInstance by remember { mutableStateOf<MapLibreMap?>(null) }
    var followUser by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        vm.startGps()
    }

    // Actualizar posición en el mapa cuando cambie el GPS
    LaunchedEffect(gpsSample) {
        val map = mapInstance ?: return@LaunchedEffect
        if (gpsSample.lat != 0.0 && gpsSample.lon != 0.0) {
            if (followUser) {
                map.animateCamera(CameraUpdateFactory.newLatLng(LatLng(gpsSample.lat, gpsSample.lon)))
            }
        }
    }

    // Actualizar trazado en vivo
    LaunchedEffect(liveTrail) {
        val map = mapInstance ?: return@LaunchedEffect
        val style = map.style ?: return@LaunchedEffect
        
        if (liveTrail.size > 1) {
            val pts = liveTrail.map { Point.fromLngLat(it.lon, it.lat) }
            val line = LineString.fromLngLats(pts)
            val source = style.getSourceAs<GeoJsonSource>("live-trail-src")
            if (source != null) {
                source.setGeoJson(Feature.fromGeometry(line))
            } else {
                style.addSource(GeoJsonSource("live-trail-src", Feature.fromGeometry(line)))
                style.addLayer(
                    LineLayer("live-trail-layer", "live-trail-src").withProperties(
                        PropertyFactory.lineColor("#CE1126"), // Rojo Ducati
                        PropertyFactory.lineWidth(4f)
                    )
                )
            }
        } else {
            // Limpiar si no hay ruta
            style.removeLayer("live-trail-layer")
            style.removeSource("live-trail-src")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapLibre.getInstance(ctx)
                MapView(ctx).apply {
                    onCreate(null)
                    getMapAsync { map ->
                        mapInstance = map
                        val styleJson = ctx.resources.openRawResource(R.raw.osm_style)
                            .bufferedReader().use { it.readText() }
                        map.setStyle(Style.Builder().fromJson(styleJson))
                        
                        // Configuración inicial de cámara si hay posición
                        if (gpsSample.lat != 0.0) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(gpsSample.lat, gpsSample.lon), 15.0
                            ))
                        }
                    }
                }
            },
            update = { _ ->
                // El update se maneja mediante el LaunchedEffect(gpsSample) y mapInstance
            }
        )

        // Botón de centrado / auto-follow
        FloatingActionButton(
            onClick = { followUser = !followUser },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = if (followUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        ) {
            Icon(
                Icons.Default.MyLocation,
                contentDescription = "Seguir mi posición",
                tint = if (followUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Info overlay simple
        if (gpsSample.lat != 0.0) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("Velocidad: %.1f km/h".format(gpsSample.speedMs * 3.6f), style = MaterialTheme.typography.bodyMedium)
                    Text("Altitud: %.0f m".format(gpsSample.altitude), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

package com.mototrack.wit.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mototrack.wit.ui.route.RouteTabScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    innerPadding: PaddingValues,
) {
    NavHost(
        navController = navController,
        startDestination = BottomDestination.Ruta.route,
        modifier = Modifier.fillMaxSize().padding(innerPadding),
    ) {
        composable(BottomDestination.Sensores.route) {
            Placeholder("Sensores", "Aquí irá el escaneo BLE y el estado del GPS")
        }
        composable(BottomDestination.Ruta.route) {
            RouteTabScreen(
                onOpenMapa = {
                    navController.navigate(BottomDestination.Mapa.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
        composable(BottomDestination.Historico.route) {
            Placeholder("Histórico", "Aquí irá la lista de rutas guardadas")
        }
        composable(
            route = "${BottomDestination.Mapa.route}?routeId={routeId}",
            arguments = listOf(
                navArgument("routeId") { type = NavType.LongType; defaultValue = -1L },
            ),
        ) { entry ->
            val routeId = entry.arguments?.getLong("routeId") ?: -1L
            val sub = if (routeId > 0) "Ruta #$routeId" else "Última ruta o ninguna"
            Placeholder("Mapa", "Aquí irá el mapa OSM + scrubber  ·  $sub")
        }
    }
}

@Composable
private fun Placeholder(title: String, subtitle: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "$title\n\n$subtitle", textAlign = TextAlign.Center)
    }
}

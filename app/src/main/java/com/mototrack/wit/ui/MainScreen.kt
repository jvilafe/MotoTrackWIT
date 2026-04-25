package com.mototrack.wit.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mototrack.wit.ui.record.RecordScreen
import com.mototrack.wit.ui.routes.RoutesScreen

private sealed class BottomDestination(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    data object Sensors : BottomDestination("sensores", "Sensores", Icons.Default.Sensors)
    data object Route : BottomDestination("ruta", "Ruta", Icons.Default.PlayArrow)
    data object History : BottomDestination("historico", "Histórico", Icons.Default.History)
    data object Map : BottomDestination("mapa", "Mapa", Icons.Default.Map)
}

@Composable
fun MainScreen() {
    val nav = rememberNavController()
    val items = listOf(
        BottomDestination.Sensors,
        BottomDestination.Route,
        BottomDestination.History,
        BottomDestination.Map
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by nav.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination

                items.forEach { item ->
                    val selected = currentDestination
                        ?.hierarchy
                        ?.any { it.route == item.route } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            nav.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(nav.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = BottomDestination.Route.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomDestination.Sensors.route) {
                com.mototrack.wit.ui.sensors.SensorsTabScreen()
            }

            composable(BottomDestination.Route.route) {
                RecordScreen()
            }

            composable(BottomDestination.History.route) {
                RoutesScreen(nav = nav)
            }

            composable(BottomDestination.Map.route) {
                com.mototrack.wit.ui.sensors.MapPlaceholderScreen()
            }

            composable("detail/{id}") { backStack ->
                val id = backStack.arguments?.getString("id")?.toLongOrNull() ?: return@composable
                com.mototrack.wit.ui.detail.RouteDetailScreen(id = id, nav = nav)
            }
        }
    }
}

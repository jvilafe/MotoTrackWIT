package com.mototrack.wit.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mototrack.wit.ui.route.RouteTabScreen
import com.mototrack.wit.ui.routes.RoutesScreen
import com.mototrack.wit.ui.record.RecordScreen
import com.mototrack.wit.ui.map.MapTabScreen
import android.app.Activity
import kotlin.system.exitProcess

private sealed class BottomDestination(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    data object Sensors : BottomDestination("sensores", "Sensores", Icons.Default.Sensors)
    data object View : BottomDestination("view", "View", Icons.Default.Monitor)
    data object Route : BottomDestination("ruta", "Ruta", Icons.Default.PlayArrow)
    data object History : BottomDestination("historico", "Histórico", Icons.Default.History)
    data object Map : BottomDestination("mapa", "Mapa", Icons.Default.Map)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val nav = rememberNavController()
    val context = LocalContext.current
    val items = listOf(
        BottomDestination.Sensors,
        BottomDestination.View,
        BottomDestination.Route,
        BottomDestination.History,
        BottomDestination.Map
    )

    Scaffold(
        topBar = {
            val backStackEntry by nav.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            val title = items.find { it.route == currentRoute }?.label ?: "MotoTracker"
            
            TopAppBar(
                title = { Text(title) },
                actions = {
                    IconButton(onClick = {
                        (context as? Activity)?.finish()
                        exitProcess(0)
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Salir")
                    }
                }
            )
        },
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

            composable(BottomDestination.View.route) {
                RecordScreen()
            }

            composable(BottomDestination.Route.route) {
                RouteTabScreen(
                    onOpenMapa = { nav.navigate(BottomDestination.Map.route) }
                )
            }

            composable(BottomDestination.History.route) {
                RoutesScreen(nav = nav)
            }

            composable(BottomDestination.Map.route) {
                MapTabScreen()
            }

            composable("detail/{id}") { backStack ->
                val id = backStack.arguments?.getString("id")?.toLongOrNull() ?: return@composable
                com.mototrack.wit.ui.detail.RouteDetailScreen(id = id, nav = nav)
            }
        }
    }
}

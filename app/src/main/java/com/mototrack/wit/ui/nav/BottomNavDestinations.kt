package com.mototrack.wit.ui.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Map
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Las 4 pestañas de la Bottom Navigation, en castellano.
 * Cada destino tiene su ruta de navegación y su icono.
 */
sealed class BottomDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    data object Sensores : BottomDestination(
        route = "sensores",
        label = "Sensores",
        icon = Icons.Filled.Bluetooth,
    )

    data object Ruta : BottomDestination(
        route = "ruta",
        label = "Ruta",
        icon = Icons.Filled.FiberManualRecord,
    )

    data object Historico : BottomDestination(
        route = "historico",
        label = "Histórico",
        icon = Icons.Filled.FolderOpen,
    )

    data object Mapa : BottomDestination(
        route = "mapa",
        label = "Mapa",
        icon = Icons.Filled.Map,
    )

    companion object {
        val all: List<BottomDestination> = listOf(Sensores, Ruta, Historico, Mapa)
    }
}

package com.mototrack.wit.ui.routes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.mototrack.wit.data.RouteRepository
import com.mototrack.wit.data.db.RouteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
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

    fun delete(id: Long) = viewModelScope.launch {
        repo.delete(id)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutesScreen(nav: NavHostController, vm: RoutesViewModel = hiltViewModel()) {
    val routes by vm.routes.collectAsState()
    val df = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

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
                            "${df.format(Date(route.startedAt))} · ${"%.1f".format(route.distanceM / 1000)} km · max ${"%.0f".format(route.maxSpeed * 3.6)} km/h"
                        )
                    },
                    trailingContent = {
                        IconButton(onClick = { vm.delete(route.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar ruta")
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

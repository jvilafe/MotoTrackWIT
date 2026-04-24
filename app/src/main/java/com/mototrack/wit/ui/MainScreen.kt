package com.mototrack.wit.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.mototrack.wit.ui.nav.AppNavHost
import com.mototrack.wit.ui.nav.BottomNavBar

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavBar(navController) },
    ) { innerPadding ->
        AppNavHost(navController = navController, innerPadding = innerPadding)
    }
}

package com.mototrack.wit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mototrack.wit.ui.MainScreen
import com.mototrack.wit.ui.theme.MotoTrackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MotoTrackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    // Toda la navegación con Bottom Nav (4 pestañas) vive ahora en MainScreen.
                    // El NavHost antiguo (record/routes/detail/debug) queda sustituido
                    // por el grafo de AppNavHost (sensores/ruta/historico/mapa).
                    MainScreen()
                }
            }
        }
    }
}

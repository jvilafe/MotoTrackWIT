package com.mototrack.wit
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mototrack.wit.ui.debug.DebugScreen
import com.mototrack.wit.ui.detail.RouteDetailScreen
import com.mototrack.wit.ui.record.RecordScreen
import com.mototrack.wit.ui.routes.RoutesScreen
import com.mototrack.wit.ui.theme.MotoTrackTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MotoTrackTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val nav = rememberNavController()
                    NavHost(nav, startDestination = "record") {
                        composable("record") { RecordScreen() }
                        composable("routes") { RoutesScreen(nav) }
                        composable("detail/{id}") { b ->
                            RouteDetailScreen(b.arguments?.getString("id")?.toLongOrNull() ?: 0L, nav)
                        }
                        composable("debug") { DebugScreen(nav) }
                    }
                }
            }
        }
    }
}

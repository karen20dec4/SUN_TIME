package com.android.sun

import java.util.Calendar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.sun.ui.screens.AllDayScreen
import com.android.sun.ui.screens.LocationScreen
import com.android.sun.ui.screens.MainScreen
import com.android.sun.ui.theme.SunTheme
import com.android.sun.viewmodel.AstroViewModel
import com.android.sun.viewmodel.LocationViewModel
import com.android.sun.viewmodel.MainViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            SunTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = viewModel()
    val locationViewModel: LocationViewModel = viewModel()
    val astroViewModel: AstroViewModel = viewModel()
    
    val astroData by mainViewModel. astroData.collectAsState()
    val isLoading by mainViewModel.isLoading.collectAsState()
    val codeMode by mainViewModel.codeMode. collectAsState()
    
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        // ════════════════════════════════════
        // MAIN SCREEN
        // ════════════════════════════════════
        composable("main") {
            MainScreen(
                astroData = astroData,
                isLoading = isLoading,
                codeMode = codeMode,
                onCodeModeChange = { mainViewModel.toggleCodeMode() },
                onRefresh = { mainViewModel.refresh() },
                onNavigateToLocation = {
                    navController.navigate("location")
                },
                onNavigateToAllDay = {
                    navController.navigate("allday")
                }
            )
        }
        
        // ════════════════════════════════════
        // LOCATION SCREEN
        // ════════════════════════════════════
        composable("location") {
		LocationScreen(
			viewModel = locationViewModel,
			mainViewModel = mainViewModel,
			onLocationSelected = { location ->
				android.util.Log.d("MainActivity", "🟢 onLocationSelected called:  ${location.name} (${location.latitude}, ${location.longitude})")
				mainViewModel.setLocation(location)
				android.util.Log.d("MainActivity", "🟢 After setLocation, calling popBackStack...")
				navController.popBackStack()
				android.util.Log.d("MainActivity", "🟢 After popBackStack, calling refresh...")
				mainViewModel.refresh()
				android.util.Log.d("MainActivity", "🟢 Refresh completed!")
			},
			onBack = {
				android. util.Log.d("MainActivity", "🔵 onBack called")
				navController.popBackStack()
				android.util.Log.d("MainActivity", "🔵 Calling refresh after back...")
				mainViewModel.refresh()
				android.util.Log.d("MainActivity", "🔵 Back refresh completed!")
			}
		)
	}
		
		
        
        // ════════════════════════════════════
        // ALL DAY SCREEN
        // ════════════════════════════════════
        composable("allday") {
            if (astroData != null) {
                val tattvaDaySchedule = remember(astroData) {
                    astroViewModel. generateTattvaDayScheduleWithCurrentTime(
                        astroData = astroData!! ,
                        currentTime = Calendar.getInstance()
                    )
                }
                
                AllDayScreen(
                    tattvaDaySchedule = tattvaDaySchedule,
                    sunriseDate = astroData!! .sunrise,
                    sunriseTime = astroData!!. sunriseFormatted,
                    sunsetTime = astroData!!.sunsetFormatted,
                    actualSunriseTime = astroData!!. sunrise,
                    onBackClick = {
                        navController. popBackStack()
                    },
                    onNextDayClick = {
                        // TODO: Implementare NEXT DAY
                    }
                )
            }
        }
    }
}
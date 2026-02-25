package com.example.voyagetime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.voyagetime.ui.screens.Home
import com.example.voyagetime.ui.screens.Itinerary
import com.example.voyagetime.ui.screens.Trips
import com.example.voyagetime.ui.screens.Preferences
import com.example.voyagetime.ui.theme.VoyageTimeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoyageTimeTheme {
                VoyageTimeApp()
            }
        }
    }
}

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

@PreviewScreenSizes
@Composable
fun VoyageTimeApp() {
    val navController = rememberNavController()

    val items = listOf(
        NavItem(Routes.HOME, "Home", Icons.Default.Home),
        NavItem(Routes.TRIPS, "Trips", Icons.Default.Place),
        NavItem(Routes.ITINERARY, "Itinerary", Icons.Default.DateRange),
        NavItem(Routes.PREFERENCES, "Preferences", Icons.Default.AccountBox),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            items.forEach { item ->
                val selected = currentDestination
                    ?.hierarchy
                    ?.any { it.route == item.route } == true

                item(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    selected = selected,
                    onClick = {
                        navController.navigate(item.route) {
                            // Avoid building a huge back stack when clicking bottom items
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Routes.HOME) {
                    Home()
                }
                composable(Routes.TRIPS) {
                    Trips()
                }
                composable (Routes.ITINERARY){
                    Itinerary()
                }
                composable(Routes.PREFERENCES) {
                    Preferences()
                }
            }
        }
    }
}

//
object Routes {
    const val HOME = "home"
    const val TRIPS = "trips"
    const val ITINERARY = "itinerary"
    const val PREFERENCES = "preferences"
}
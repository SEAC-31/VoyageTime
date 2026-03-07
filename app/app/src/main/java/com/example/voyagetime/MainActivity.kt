package com.example.voyagetime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.voyagetime.ui.screens.Gallery
import com.example.voyagetime.ui.screens.Home
import com.example.voyagetime.ui.screens.Itinerary
import com.example.voyagetime.ui.screens.Preferences
import com.example.voyagetime.ui.screens.Trips
import com.example.voyagetime.ui.screens.AboutUs
import com.example.voyagetime.ui.screens.SplashScreen
import com.example.voyagetime.ui.theme.VoyageTimeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoyageTimeTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(onFinished = { showSplash = false })
                } else {
                    VoyageTimeApp()
                }
            }
        }
    }
}

data class NavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun VoyageTimeApp() {
    val navController = rememberNavController()

    val items = listOf(
        NavItem(Routes.HOME, "Home", Icons.Default.Home),
        NavItem(Routes.TRIPS, "Trips", Icons.Default.Place),
        NavItem(Routes.GALLERY, "Gallery", Icons.Default.PhotoLibrary),
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
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = false
                                inclusive = false
                            }
                            launchSingleTop = true
                            restoreState = false
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
                    Trips(
                        onTripClick = { tripId ->
                            navController.navigate("${Routes.ITINERARY}/$tripId")
                        }
                    )
                }

                composable(
                    route = "${Routes.ITINERARY}/{tripId}",
                    arguments = listOf(navArgument("tripId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
                    Itinerary(tripId = tripId)
                }

                composable(Routes.GALLERY) {
                    Gallery()
                }

                composable(Routes.PREFERENCES) {
                    Preferences(onNavigateToAboutUs = {
                        navController.navigate(Routes.ABOUT_US)
                    })
                }
                composable(Routes.ABOUT_US) {
                    AboutUs(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

object Routes {
    const val HOME = "home"
    const val TRIPS = "trips"
    const val ITINERARY = "itinerary"
    const val GALLERY = "gallery"
    const val PREFERENCES = "preferences"
    const val ABOUT_US = "about_us"
}
package com.example.voyagetime

import android.content.Context
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.voyagetime.ui.screens.AboutUs
import com.example.voyagetime.ui.screens.DepartureCityScreen
import com.example.voyagetime.ui.screens.Gallery
import com.example.voyagetime.ui.screens.Home
import com.example.voyagetime.ui.screens.Itinerary
import com.example.voyagetime.ui.screens.LanguageManager
import com.example.voyagetime.ui.screens.Preferences
import com.example.voyagetime.ui.screens.PreferencesManager
import com.example.voyagetime.ui.screens.SplashScreen
import com.example.voyagetime.ui.screens.TermsAcceptanceScreen
import com.example.voyagetime.ui.screens.TermsAndConditions
import com.example.voyagetime.ui.screens.TravelStyleScreen
import com.example.voyagetime.ui.screens.Trips
import com.example.voyagetime.ui.screens.CreateTripScreen
import com.example.voyagetime.ui.theme.VoyageTimeTheme

// Global dark mode state accessible anywhere in the composition tree
val LocalDarkMode = compositionLocalOf { false }
val LocalOnDarkModeChange = compositionLocalOf<(Boolean) -> Unit> { {} }

const val EXTRA_START_AFTER_SPLASH = "start_after_splash"

enum class AppScreen { SPLASH, TERMS_ACCEPTANCE, MAIN }

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageManager.applyLanguage(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val startAfterSplash = intent
            .getStringExtra(EXTRA_START_AFTER_SPLASH)
            ?.let { AppScreen.valueOf(it) }
            ?: AppScreen.TERMS_ACCEPTANCE

        setContent {
            val context = this

            // darkMode survives recomposition and navigation
            var darkMode by rememberSaveable {
                mutableStateOf(PreferencesManager.getDarkMode(context))
            }

            // Provide dark mode state to entire tree via CompositionLocal
            CompositionLocalProvider(
                LocalDarkMode provides darkMode,
                LocalOnDarkModeChange provides { enabled: Boolean ->
                    darkMode = enabled
                    PreferencesManager.saveDarkMode(context, enabled)
                }
            ) {
                VoyageTimeTheme(darkTheme = darkMode) {
                    var currentScreen by rememberSaveable {
                        mutableStateOf(AppScreen.SPLASH)
                    }

                    when (currentScreen) {
                        AppScreen.SPLASH -> {
                            SplashScreen(onFinished = {
                                currentScreen = startAfterSplash
                            })
                        }
                        AppScreen.TERMS_ACCEPTANCE -> {
                            TermsAcceptanceScreen(
                                onAccept = { currentScreen = AppScreen.MAIN },
                                onReject = { currentScreen = AppScreen.MAIN }
                            )
                        }
                        AppScreen.MAIN -> {
                            VoyageTimeApp()
                        }
                    }
                }
            }
        }
    }
}

data class NavItem(val route: String, val label: String, val icon: ImageVector)

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
                val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                item(
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) },
                    selected = selected,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = false; inclusive = false
                            }
                            launchSingleTop = true; restoreState = false
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
                    Home(
                        onTripClick = { navController.navigate("${Routes.ITINERARY}/$it") },
                        onDepartureCityClick = { navController.navigate(Routes.DEPARTURE_CITY) },
                        onTravelStyleClick = { navController.navigate(Routes.TRAVEL_STYLE) },
                        onAddNewTripClick = { navController.navigate(Routes.CREATE_TRIP) }
                    )
                }
                composable(Routes.CREATE_TRIP) {
                    CreateTripScreen(onCancel = { navController.popBackStack() })
                }
                composable(Routes.TRIPS) {
                    Trips(onTripClick = { navController.navigate("${Routes.ITINERARY}/$it") })
                }
                composable(
                    route = "${Routes.ITINERARY}/{tripId}",
                    arguments = listOf(navArgument("tripId") { type = NavType.StringType })
                ) {
                    Itinerary(tripId = it.arguments?.getString("tripId") ?: "")
                }
                composable(Routes.DEPARTURE_CITY) { DepartureCityScreen() }
                composable(Routes.TRAVEL_STYLE) { TravelStyleScreen() }
                composable(Routes.GALLERY) { Gallery() }
                composable(Routes.PREFERENCES) {
                    Preferences(
                        onNavigateToAboutUs = { navController.navigate(Routes.ABOUT_US) },
                        onNavigateToTerms = { navController.navigate(Routes.TERMS) }
                    )
                }
                composable(Routes.ABOUT_US) {
                    AboutUs(onBack = { navController.popBackStack() })
                }
                composable(Routes.TERMS) {
                    TermsAndConditions(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

object Routes {
    const val HOME = "home"
    const val CREATE_TRIP = "create_trip"
    const val TRIPS = "trips"
    const val ITINERARY = "itinerary"
    const val DEPARTURE_CITY = "departure_city"
    const val TRAVEL_STYLE = "travel_style"
    const val GALLERY = "gallery"
    const val PREFERENCES = "preferences"
    const val ABOUT_US = "about_us"
    const val TERMS = "terms"
}
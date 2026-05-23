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
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.res.stringResource
import com.example.voyagetime.data.remote.HotelDto
import com.example.voyagetime.ui.screens.AboutUs
import com.example.voyagetime.ui.screens.DepartureCityScreen
import com.example.voyagetime.ui.screens.ForgotPasswordScreen
import com.example.voyagetime.ui.screens.Gallery
import com.example.voyagetime.ui.screens.Home
import com.example.voyagetime.ui.screens.HotelDetailScreen
import com.example.voyagetime.ui.screens.Itinerary
import com.example.voyagetime.ui.screens.LanguageManager
import com.example.voyagetime.ui.screens.LoginScreen
import com.example.voyagetime.ui.screens.Preferences
import com.example.voyagetime.ui.screens.PreferencesManager
import com.example.voyagetime.ui.screens.RegisterScreen
import com.example.voyagetime.ui.screens.ReservationsScreen
import com.example.voyagetime.ui.screens.SplashScreen
import com.example.voyagetime.ui.screens.TermsAcceptanceScreen
import com.example.voyagetime.ui.screens.TermsAndConditions
import com.example.voyagetime.ui.screens.TravelStyleScreen
import com.example.voyagetime.ui.screens.Trips
import com.example.voyagetime.ui.screens.CreateTripScreen
import com.example.voyagetime.ui.theme.VoyageTimeTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import com.example.voyagetime.ui.screens.TripGallery

val LocalDarkMode = compositionLocalOf { false }
val LocalOnDarkModeChange = compositionLocalOf<(Boolean) -> Unit> { {} }

const val EXTRA_START_AFTER_SPLASH = "start_after_splash"

enum class AppScreen { SPLASH, TERMS_ACCEPTANCE, MAIN, AUTH }

@AndroidEntryPoint
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
            var darkMode by rememberSaveable {
                mutableStateOf(PreferencesManager.getDarkMode(context))
            }

            CompositionLocalProvider(
                LocalDarkMode provides darkMode,
                LocalOnDarkModeChange provides { enabled: Boolean ->
                    darkMode = enabled
                    PreferencesManager.saveDarkMode(context, enabled)
                }
            ) {
                VoyageTimeTheme(darkTheme = darkMode) {
                    var currentScreen by rememberSaveable { mutableStateOf(AppScreen.SPLASH) }

                    when (currentScreen) {
                        AppScreen.SPLASH ->
                            SplashScreen(onFinished = { currentScreen = startAfterSplash })
                        AppScreen.TERMS_ACCEPTANCE ->
                            TermsAcceptanceScreen(
                                onAccept = { currentScreen = AppScreen.AUTH },
                                onReject = { currentScreen = AppScreen.AUTH }
                            )
                        AppScreen.AUTH ->
                            LoginScreen(
                                onLoginSuccess = { currentScreen = AppScreen.MAIN },
                                onRegisterClick = { currentScreen = AppScreen.MAIN },
                                onForgotPasswordClick = { currentScreen = AppScreen.MAIN }
                            )
                        AppScreen.MAIN ->
                            VoyageTimeApp(
                                onLogout = {
                                    FirebaseAuth.getInstance().signOut()
                                    currentScreen = AppScreen.AUTH
                                }
                            )
                    }
                }
            }
        }
    }
}

data class NavItem(val route: String, @StringRes val labelRes: Int, val icon: ImageVector)

@Composable
fun VoyageTimeApp(onLogout: () -> Unit = {}) {
    val navController = rememberNavController()

    val items = listOf(
        NavItem(Routes.HOME,         R.string.nav_home,         Icons.Default.Home),
        NavItem(Routes.TRIPS,        R.string.nav_trips,        Icons.Default.Place),
        NavItem(Routes.RESERVATIONS, R.string.nav_reservations, Icons.Default.Hotel),
        NavItem(Routes.GALLERY,      R.string.nav_gallery,      Icons.Default.PhotoLibrary),
        NavItem(Routes.PREFERENCES,  R.string.nav_preferences,  Icons.Default.AccountBox),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val routesWithoutNavBar = setOf(Routes.REGISTER, Routes.FORGOT_PASSWORD, Routes.HOTEL_DETAIL)
    val showNavBar = currentDestination?.route !in routesWithoutNavBar

    if (showNavBar) {
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                items.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    item(
                        icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                        label = { Text(stringResource(item.labelRes)) },
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
        ) { AppNavHost(navController, onLogout) }
    } else {
        AppNavHost(navController, onLogout)
    }
}

@Composable
private fun AppNavHost(
    navController: androidx.navigation.NavHostController,
    onLogout: () -> Unit = {}
) {
    var pendingHotel by remember { mutableStateOf<HotelDto?>(null) }
    var pendingTripId by remember { mutableStateOf(0L) }
    var pendingStartDate by remember { mutableStateOf("") }
    var pendingEndDate by remember { mutableStateOf("") }

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
                CreateTripScreen(
                    onCancel = { navController.popBackStack() },
                    onTripCreated = { navController.popBackStack() }
                )
            }
            composable(Routes.TRIPS) {
                Trips(
                    onTripClick = { navController.navigate("${Routes.ITINERARY}/$it") },
                    onGalleryClick = { tripId ->
                        // Aquí necesitamos el nombre del viaje — pasamos el ID y dejamos que TripGallery lo muestre
                        navController.navigate("${Routes.TRIP_GALLERY}/$tripId/$tripId")
                    }
                )
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
            composable(
                route = "${Routes.TRIP_GALLERY}/{tripId}/{tripName}",
                arguments = listOf(
                    navArgument("tripId") { type = NavType.StringType },
                    navArgument("tripName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                TripGallery(
                    tripId = backStackEntry.arguments?.getString("tripId") ?: "",
                    tripName = backStackEntry.arguments?.getString("tripName") ?: "",
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.RESERVATIONS) { ReservationsScreen() }
            composable(Routes.PREFERENCES) {
                Preferences(
                    onNavigateToAboutUs = { navController.navigate(Routes.ABOUT_US) },
                    onNavigateToTerms = { navController.navigate(Routes.TERMS) },
                    onLogout = onLogout
                )
            }
            composable(Routes.ABOUT_US) {
                AboutUs(onBack = { navController.popBackStack() })
            }
            composable(Routes.TERMS) {
                TermsAndConditions(onBack = { navController.popBackStack() })
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    viewModel = hiltViewModel(),
                    onRegisterSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }
            composable(Routes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(
                    viewModel = hiltViewModel(),
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.HOTEL_DETAIL) {
                val hotel = pendingHotel
                if (hotel != null) {
                    HotelDetailScreen(
                        hotel     = hotel,
                        tripId    = pendingTripId,
                        startDate = pendingStartDate,
                        endDate   = pendingEndDate,
                        onBack    = { navController.popBackStack() },
                        onBookingSuccess = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

object Routes {
    const val HOME             = "home"
    const val CREATE_TRIP      = "create_trip"
    const val TRIPS            = "trips"
    const val ITINERARY        = "itinerary"
    const val DEPARTURE_CITY   = "departure_city"
    const val TRAVEL_STYLE     = "travel_style"
    const val GALLERY          = "gallery"
    const val RESERVATIONS     = "reservations"
    const val PREFERENCES      = "preferences"
    const val ABOUT_US         = "about_us"
    const val TERMS            = "terms"
    const val REGISTER         = "register"
    const val FORGOT_PASSWORD  = "forgot_password"
    const val HOTEL_DETAIL     = "hotel_detail"

    const val TRIP_GALLERY = "trip_gallery"
}
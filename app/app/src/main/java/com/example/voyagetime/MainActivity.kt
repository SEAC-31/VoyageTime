package com.example.voyagetime

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Place
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.voyagetime.data.local.database.VoyageTimeDatabase
import com.example.voyagetime.data.repository.FirebaseAuthRepositoryImpl
import com.example.voyagetime.data.repository.UserRepositoryImpl
import com.example.voyagetime.ui.screens.AboutUs
import com.example.voyagetime.ui.screens.CreateTripScreen
import com.example.voyagetime.ui.screens.DepartureCityScreen
import com.example.voyagetime.ui.screens.ForgotPasswordScreen
import com.example.voyagetime.ui.screens.Gallery
import com.example.voyagetime.ui.screens.Home
import com.example.voyagetime.ui.screens.Itinerary
import com.example.voyagetime.ui.screens.LanguageManager
import com.example.voyagetime.ui.screens.LoginScreen
import com.example.voyagetime.ui.screens.Preferences
import com.example.voyagetime.ui.screens.PreferencesManager
import com.example.voyagetime.ui.screens.RegisterScreen
import com.example.voyagetime.ui.screens.SplashScreen
import com.example.voyagetime.ui.screens.TermsAcceptanceScreen
import com.example.voyagetime.ui.screens.TermsAndConditions
import com.example.voyagetime.ui.screens.TravelStyleScreen
import com.example.voyagetime.ui.screens.Trips
import com.example.voyagetime.ui.theme.VoyageTimeTheme
import kotlinx.coroutines.launch

val LocalDarkMode = compositionLocalOf { false }
val LocalOnDarkModeChange = compositionLocalOf<(Boolean) -> Unit> { {} }

enum class AppScreen {
    SPLASH,
    TERMS_ACCEPTANCE,
    LOGIN,
    REGISTER,
    TERMS_FROM_REGISTER,
    FORGOT_PASSWORD,
    MAIN
}

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val localizedContext = LanguageManager.applyLanguage(newBase)
        super.attachBaseContext(localizedContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = this@MainActivity
            val authRepository = remember { FirebaseAuthRepositoryImpl() }
            val database = remember { VoyageTimeDatabase.getDatabase(context) }
            val userRepository = remember {
                UserRepositoryImpl(database.userDao(), database.accessLogDao())
            }
            val coroutineScope = rememberCoroutineScope()

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
                    var currentScreen by rememberSaveable {
                        mutableStateOf(AppScreen.SPLASH)
                    }

                    when (currentScreen) {
                        AppScreen.SPLASH -> {
                            SplashScreen(
                                onFinished = {
                                    currentScreen = when {
                                        !PreferencesManager.hasAcceptedTerms(context) -> {
                                            Log.i(TAG, "Navigation event: terms required")
                                            AppScreen.TERMS_ACCEPTANCE
                                        }

                                        authRepository.isUserLoggedIn() -> {
                                            Log.i(TAG, "Navigation event: user already logged in")
                                            AppScreen.MAIN
                                        }

                                        else -> {
                                            Log.i(TAG, "Navigation event: login required")
                                            AppScreen.LOGIN
                                        }
                                    }
                                }
                            )
                        }

                        AppScreen.TERMS_ACCEPTANCE -> {
                            TermsAcceptanceScreen(
                                onAccept = {
                                    PreferencesManager.saveTermsAccepted(context, true)

                                    currentScreen = if (authRepository.isUserLoggedIn()) {
                                        Log.i(TAG, "Navigation event: terms accepted, opening main")
                                        AppScreen.MAIN
                                    } else {
                                        Log.i(TAG, "Navigation event: terms accepted, opening login")
                                        AppScreen.LOGIN
                                    }
                                },
                                onReject = {
                                    Log.w(TAG, "Navigation event: terms rejected")
                                    currentScreen = AppScreen.TERMS_ACCEPTANCE
                                }
                            )
                        }

                        AppScreen.LOGIN -> {
                            LoginScreen(
                                onLoginSuccess = {
                                    Log.i(TAG, "Navigation event: login success")
                                    authRepository.currentUserId()?.let { uid ->
                                        coroutineScope.launch {
                                            userRepository.logAccess(uid, UserRepositoryImpl.EVENT_LOGIN)
                                        }
                                    }
                                    currentScreen = AppScreen.MAIN
                                },
                                onRegisterClick = {
                                    Log.i(TAG, "Navigation event: open register")
                                    currentScreen = AppScreen.REGISTER
                                },
                                onForgotPasswordClick = {
                                    Log.i(TAG, "Navigation event: open forgot password")
                                    currentScreen = AppScreen.FORGOT_PASSWORD
                                }
                            )
                        }

                        AppScreen.REGISTER -> {
                            RegisterScreen(
                                onBackToLogin = {
                                    Log.i(TAG, "Navigation event: back to login")
                                    currentScreen = AppScreen.LOGIN
                                },
                                onTermsClick = {
                                    Log.i(TAG, "Navigation event: open terms from register")
                                    currentScreen = AppScreen.TERMS_FROM_REGISTER
                                }
                            )
                        }

                        AppScreen.TERMS_FROM_REGISTER -> {
                            TermsAndConditions(
                                onBack = {
                                    Log.i(TAG, "Navigation event: back to register from terms")
                                    currentScreen = AppScreen.REGISTER
                                }
                            )
                        }

                        AppScreen.FORGOT_PASSWORD -> {
                            ForgotPasswordScreen(
                                onBack = {
                                    Log.i(TAG, "Navigation event: back to login from forgot password")
                                    currentScreen = AppScreen.LOGIN
                                }
                            )
                        }

                        AppScreen.MAIN -> {
                            VoyageTimeApp(
                                onLogout = {
                                    val uidBeforeLogout = authRepository.currentUserId()
                                    coroutineScope.launch {
                                        uidBeforeLogout?.let { uid ->
                                            userRepository.logAccess(uid, UserRepositoryImpl.EVENT_LOGOUT)
                                        }
                                        authRepository.logout()
                                        currentScreen = AppScreen.LOGIN
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

data class NavItem(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector
)

@Composable
fun VoyageTimeApp(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    val items = listOf(
        NavItem(
            route = Routes.HOME,
            labelRes = R.string.nav_home,
            icon = Icons.Default.Home
        ),
        NavItem(
            route = Routes.TRIPS,
            labelRes = R.string.nav_trips,
            icon = Icons.Default.Place
        ),
        NavItem(
            route = Routes.GALLERY,
            labelRes = R.string.nav_gallery,
            icon = Icons.Default.PhotoLibrary
        ),
        NavItem(
            route = Routes.PREFERENCES,
            labelRes = R.string.nav_preferences,
            icon = Icons.Default.AccountBox
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            items.forEach { item ->
                val selected = currentDestination?.hierarchy?.any { destination ->
                    destination.route == item.route
                } == true

                item(
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(item.labelRes)
                        )
                    },
                    label = {
                        Text(text = stringResource(item.labelRes))
                    },
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
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Routes.HOME) {
                    Home(
                        onTripClick = { tripId ->
                            navController.navigate("${Routes.ITINERARY}/$tripId")
                        },
                        onDepartureCityClick = {
                            navController.navigate(Routes.DEPARTURE_CITY)
                        },
                        onTravelStyleClick = {
                            navController.navigate(Routes.TRAVEL_STYLE)
                        },
                        onAddNewTripClick = {
                            navController.navigate(Routes.CREATE_TRIP)
                        }
                    )
                }

                composable(Routes.CREATE_TRIP) {
                    CreateTripScreen(
                        onCancel = {
                            navController.popBackStack()
                        },
                        onTripCreated = {
                            navController.popBackStack()
                        }
                    )
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
                    arguments = listOf(
                        navArgument("tripId") {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    Itinerary(
                        tripId = backStackEntry.arguments?.getString("tripId") ?: ""
                    )
                }

                composable(Routes.DEPARTURE_CITY) {
                    DepartureCityScreen()
                }

                composable(Routes.TRAVEL_STYLE) {
                    TravelStyleScreen()
                }

                composable(Routes.GALLERY) {
                    Gallery()
                }

                composable(Routes.PREFERENCES) {
                    Preferences(
                        onNavigateToAboutUs = {
                            navController.navigate(Routes.ABOUT_US)
                        },
                        onNavigateToTerms = {
                            navController.navigate(Routes.TERMS)
                        },
                        onLogout = onLogout
                    )
                }

                composable(Routes.ABOUT_US) {
                    AboutUs(
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Routes.TERMS) {
                    TermsAndConditions(
                        onBack = {
                            navController.popBackStack()
                        }
                    )
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

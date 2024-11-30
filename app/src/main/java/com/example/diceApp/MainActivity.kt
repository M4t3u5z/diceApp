package com.example.diceApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diceApp.ui.theme.RollerAppTheme
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    // Deklaracja Firebase Analytics
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicjalizacja Firebase Analytics
        firebaseAnalytics = Firebase.analytics

        val diceLogic = DiceLogic()
        val auth = FirebaseAuth.getInstance()

        setContent {
            RollerAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(auth, diceLogic, firebaseAnalytics)
                }
            }
        }

        // Logowanie zdarzenia otwarcia aplikacji
        logEvent("app_open", null)
    }

    // Funkcja pomocnicza do logowania zdarzeń
    private fun logEvent(eventName: String, params: Bundle?) {
        firebaseAnalytics.logEvent(eventName, params)
    }
}

@Composable
fun MyApp(auth: FirebaseAuth, diceLogic: DiceLogic, firebaseAnalytics: FirebaseAnalytics) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (auth.currentUser != null) "diceSetSelection" else "login"
    ) {
        composable("login") {
            logScreenEvent(firebaseAnalytics, "Login")
            LoginScreen(navController)
        }
        composable("registration") {
            logScreenEvent(firebaseAnalytics, "Registration")
            RegistrationScreen(navController)
        }
        composable("diceSetSelection") {
            logScreenEvent(firebaseAnalytics, "DiceSetSelection")
            DiceSetSelectionScreen(navController)
        }
        composable("roller/{isOneDieGame}") { backStackEntry ->
            val isOneDieGame = backStackEntry.arguments?.getString("isOneDieGame")?.toBoolean() ?: true
            val gameMode = if (isOneDieGame) "OneDieGame" else "TwoDiceGame"
            logScreenEvent(firebaseAnalytics, gameMode)
            DiceScreen(navController = navController, isOneDiceGame = isOneDieGame, diceLogic = diceLogic)
        }
        composable("animation") {
            logScreenEvent(firebaseAnalytics, "Animation")
            AppAnimationScreen(navController)
        }
        composable("profile") {
            logScreenEvent(firebaseAnalytics, "Profile")
            ProfileScreen(navController = navController, diceLogic = diceLogic)
        }
        composable("ranking") {
            logScreenEvent(firebaseAnalytics, "Ranking")
            RankingScreen(navController = navController)
        }
    }
}

// Funkcja pomocnicza do logowania wyświetlania ekranów
fun logScreenEvent(firebaseAnalytics: FirebaseAnalytics, screenName: String) {
    val bundle = Bundle().apply {
        putString("screen_name", screenName)
    }
    firebaseAnalytics.logEvent("screen_view", bundle)
}

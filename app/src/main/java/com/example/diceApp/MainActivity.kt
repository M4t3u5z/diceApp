package com.example.diceApp

import RankingScreen
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
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val diceLogic = DiceLogic()

        val auth = FirebaseAuth.getInstance()

        setContent {
            RollerAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(auth, diceLogic)
                }
            }
        }
    }
}

@Composable
fun MyApp(auth: FirebaseAuth, diceLogic: DiceLogic) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = if (auth.currentUser != null) "diceSetSelection" else "login") {
        composable("login") { LoginScreen(navController) }
        composable("registration") { RegistrationScreen(navController) }
        composable("diceSetSelection") { DiceSetSelectionScreen(navController) }
        composable("roller/{isOneDieGame}") { backStackEntry ->
            val isOneDieGame = backStackEntry.arguments?.getString("isOneDieGame")?.toBoolean() ?: true
            DiceScreen(navController = navController, isOneDiceGame = isOneDieGame, diceLogic = diceLogic)
        }
        composable("animation") { AppAnimationScreen(navController) }
        composable("profile") { ProfileScreen(navController = navController, diceLogic = diceLogic) }
        composable("ranking") { RankingScreen(navController = navController) }
    }
}

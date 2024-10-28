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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.diceApp.ui.theme.RollerAppTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance()

        setContent {
            RollerAppTheme(
                darkTheme = false
            ) {
                MyApp(auth)
            }
        }
    }
}

@Composable
fun MyApp(auth: FirebaseAuth) {
    // Zarządzanie nawigacją między ekranami
    val navController = rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Definicja nawigacji pomiędzy różnymi ekranami
        NavHost(
            navController = navController,
            startDestination = if (auth.currentUser != null) "diceSetSelection" else "login"
        ) {
            composable("login") { LoginScreen(navController) }  // Ekran logowania
            composable("registration") { RegistrationScreen(navController) }  // Ekran rejestracji
            composable("diceSetSelection") { DiceSetSelectionScreen(navController) }  // Ekran wyboru trybu gry po zalogowaniu

            // Ekran gry z parametrem isOneDiceGame
            composable("roller/{isOneDiceGame}") { backStackEntry ->
                val isOneDieGame = backStackEntry.arguments?.getString("isOneDiceGame")?.toBoolean() ?: true
                RollerScreen(navController = navController, isOneDiceGame = isOneDieGame)
            }

            composable("animation") { AppAnimationScreen(navController) }  // Ekran animacji na starcie

            // Dodajemy ekran profilu
            composable("profile") { ProfileScreen(navController) }

            // Dodajemy ekran rankingu globalnego
            composable("ranking") { RankingScreen(navController) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RollerAppTheme {
        MyApp(auth = FirebaseAuth.getInstance())
    }
}

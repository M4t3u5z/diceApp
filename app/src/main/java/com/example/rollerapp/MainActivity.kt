package com.example.rollerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rollerapp.ui.theme.RollerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RollerAppTheme(
                darkTheme = false
            ) {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp() {
    // Zarządzanie nawigacją między ekranami
    val navController = rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        // Definicja nawigacji pomiędzy różnymi ekranami
        NavHost(navController = navController, startDestination = "animation") {
            composable("login") { LoginScreen(navController) }  // Ekran logowania
            composable("registration") { RegistrationScreen(navController) }  // Ekran rejestracji

            // Ekran wyboru trybu gry po zalogowaniu
            composable("diceSetSelection") { DiceSetSelectionScreen(navController) }

            // Ekran gry z parametrem isOneDieGame
            composable("roller/{isOneDieGame}") { backStackEntry ->
                val isOneDieGame = backStackEntry.arguments?.getString("isOneDieGame")?.toBoolean() ?: true
                RollerScreen(navController = navController, isOneDieGame = isOneDieGame)
            }

            composable("animation") { AppAnimationScreen(navController) }  // Ekran animacji na starcie
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RollerAppTheme {
        MyApp()
    }
}

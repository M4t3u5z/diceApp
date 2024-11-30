package com.example.diceApp

import android.app.Activity
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(navController: NavController, diceLogic: DiceLogic) {
    var profileName by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Zmienna do przechowywania adresu email
    val userEmail = remember { mutableStateOf("") }

    // Inicjalizacja Firebase Analytics
    val firebaseAnalytics = Firebase.analytics

    // Logowanie otwarcia ekranu profilu
    LaunchedEffect(Unit) {
        firebaseAnalytics.logEvent("viewed_profile_screen", null)
    }

    // Pobierz adres email zalogowanego użytkownika
    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        userEmail.value = auth.currentUser?.email ?: "Brak zalogowanego użytkownika"
    }

    // Pobierz nazwę użytkownika z Firebase za pomocą diceLogic
    LaunchedEffect(Unit) {
        diceLogic.fetchUserProfileName { fetchedName ->
            profileName = fetchedName ?: "Anonymous"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Profil użytkownika",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Pole edycji nazwy użytkownika
        if (isEditing) {
            TextField(
                value = profileName,
                onValueChange = { profileName = it },
                label = { Text("Nazwa użytkownika") }
            )
        } else {
            Text(
                text = profileName,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Zalogowany na: ${userEmail.value}")

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk do zmiany lub zapisania nazwy profilu
        Button(
            onClick = {
                if (isEditing) {
                    diceLogic.updateUserProfileName(profileName)
                    diceLogic.updateLeaderboardAfterProfileNameChange()

                    // Logowanie zmiany nazwy użytkownika
                    firebaseAnalytics.logEvent("updated_profile_name", Bundle().apply {
                        putString("new_profile_name", profileName)
                    })
                }
                isEditing = !isEditing
            }
        ) {
            Text(text = if (isEditing) "Zapisz nazwę profilu" else "Zmień nazwę profilu")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk do wylogowania
        Button(
            onClick = {
                diceLogic.logoutAndExit(navController)

                // Logowanie wylogowania użytkownika
                firebaseAnalytics.logEvent("logged_out", null)
            }
        ) {
            Text(text = "Wyloguj")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk do wyjścia z aplikacji
        Button(
            onClick = {
                (context as? Activity)?.finish()

                // Logowanie wyjścia z aplikacji
                firebaseAnalytics.logEvent("exited_app", null)
            }
        ) {
            Text(text = "Wyjdź z aplikacji")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk powrotu do ekranu wyboru gry
        Button(
            onClick = {
                navController.navigate("diceSetSelection")

                // Logowanie powrotu do wyboru gry
                firebaseAnalytics.logEvent("back_to_game_selection", null)
            }
        ) {
            Text(text = "Powrót do wyboru gry")
        }
    }
}

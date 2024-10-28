package com.example.diceApp

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(navController: NavController, diceLogic: DiceLogic) {
    var profileName by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Zmienna do przechowywania adresu email
    val userEmail = remember { mutableStateOf("") }

    // Pobierz adres email zalogowanego użytkownika
    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        userEmail.value = auth.currentUser?.email ?: "Brak zalogowanego użytkownika"
    }

    // Pobierz nazwę użytkownika z Firebase
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
        Text(text = "Profil użytkownika", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Pole edycji nazwy użytkownika
        if (isEditing) {
            TextField(
                value = profileName,
                onValueChange = { profileName = it },
                label = { Text("Nazwa użytkownika") }
            )
        } else {
            Text(text = profileName, style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
        }

        // Adres email pod nazwą użytkownika
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Zalogowany na: ${userEmail.value}")

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk do zmiany lub zapisania nazwy profilu
        Button(
            onClick = {
                if (isEditing) {
                    diceLogic.updateUserProfileName(profileName)
                    diceLogic.updateLeaderboardAfterProfileNameChange()
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
            }
        ) {
            Text(text = "Wyloguj")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk do wyjścia z aplikacji
        Button(
            onClick = {
                (context as? Activity)?.finish()
            }
        ) {
            Text(text = "Wyjdź z aplikacji")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk powrotu do ekranu wyboru gry
        Button(
            onClick = {
                navController.navigate("diceSetSelection")
            }
        ) {
            Text(text = "Powrót do wyboru gry")
        }
    }
}

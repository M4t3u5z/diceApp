package com.example.diceApp

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var profileName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(user?.email ?: "Nieznany") }
    var isEditing by remember { mutableStateOf(false) }

    // Pobieranie nazwy profilu z Firebase Database
    val database = FirebaseDatabase.getInstance().getReference("users/${user?.uid}")

    LaunchedEffect(user) {
        database.child("profileName").get().addOnSuccessListener { dataSnapshot ->
            profileName = dataSnapshot.getValue(String::class.java) ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Wyświetlanie nazwy profilu powyżej adresu e-mail
        Text(text = "Nazwa profilu: $profileName")

        Spacer(modifier = Modifier.height(16.dp))

        // Wyświetlanie adresu e-mail
        Text(text = "Zalogowany jako: $email")

        Spacer(modifier = Modifier.height(16.dp))

        // Pole do edycji nazwy profilu, które pojawia się po kliknięciu
        if (isEditing) {
            OutlinedTextField(
                value = profileName,
                onValueChange = { profileName = it },
                label = { Text("Nazwa profilu") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Przycisk zapisu nazwy profilu
            Button(
                onClick = {
                    // Zapisz zmienioną nazwę profilu do Firebase Database
                    database.child("profileName").setValue(profileName)

                    // Aktualizacja nazwy profilu na tablicy wyników
                    val leaderboardRef = FirebaseDatabase.getInstance().getReference("leaderboard/${user?.uid}")
                    leaderboardRef.child("profileName").setValue(profileName)

                    // Pokaż toast z komunikatem o sukcesie
                    Toast.makeText(context, "Nazwa została zmieniona", Toast.LENGTH_SHORT).show()

                    isEditing = false // Ukryj pole edycji po zapisaniu
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Zapisz nazwę profilu")
            }

            Spacer(modifier = Modifier.height(16.dp))
        } else {
            // Przycisk zmiany nazwy profilu
            Button(
                onClick = {
                    isEditing = true // Wyświetl pole do edycji
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Zmień nazwę profilu")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk do wylogowania
        Button(
            onClick = {
                // Wylogowanie użytkownika i powrót do ekranu logowania
                auth.signOut()
                navController.navigate("login") {
                    popUpTo("profile") { inclusive = true }  // Czyścimy historię nawigacji
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Wyloguj się")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk powrotu do wyboru gry
        Button(
            onClick = {
                navController.navigate("diceSetSelection")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Powrót do wyboru gry")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk do wyjścia z aplikacji
        Button(onClick = {
            (context as? Activity)?.finish()
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Wyjdź z aplikacji")
        }
    }
}

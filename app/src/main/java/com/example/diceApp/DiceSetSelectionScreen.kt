package com.example.diceApp

import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rollerapp.R
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

@Composable
fun DiceSetSelectionScreen(navController: NavController) {
    var isOneDiceSelected by remember { mutableStateOf(true) }

    // Inicjalizacja Firebase Analytics
    val firebaseAnalytics = Firebase.analytics

    // Funkcja pomocnicza do logowania zdarzeń
    fun logEvent(eventName: String, params: Bundle? = null) {
        firebaseAnalytics.logEvent(eventName, params)
    }

    // Logowanie wyświetlenia ekranu
    logEvent("screen_view", Bundle().apply {
        putString("screen_name", "DiceSetSelection")
    })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Wybór jednej kostki z grafiką
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 2.dp), // Grafika 2dp od lewej krawędzi
            verticalAlignment = Alignment.CenterVertically, // Wyśrodkuj tekst względem grafiki
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.d1),
                contentDescription = "One Dice",
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Jedna Kostka", modifier = Modifier.weight(1f)) // Wyrównanie tekstu
            Checkbox(
                checked = isOneDiceSelected,
                onCheckedChange = { isOneDiceSelected = true }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Wybór dwóch kostek z grafiką
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 2.dp), // Grafika 2dp od lewej krawędzi
            verticalAlignment = Alignment.CenterVertically, // Wyśrodkuj tekst względem grafiki
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.roller),
                contentDescription = "Two Dice",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Dwie Kostki", modifier = Modifier.weight(1f)) // Wyrównanie tekstu
            Checkbox(
                checked = !isOneDiceSelected,
                onCheckedChange = { isOneDiceSelected = false }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Przycisk do zatwierdzenia trybu gry
        Button(
            onClick = {
                // Logowanie wybranego trybu gry
                logEvent("game_mode_selected", Bundle().apply {
                    putString("mode", if (isOneDiceSelected) "one_die" else "two_dice")
                })

                // Przekazanie wybranego trybu gry do ekranu gry
                navController.navigate("roller/${isOneDiceSelected}")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Graj")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk do nawigacji do profilu
        Button(
            onClick = {
                logEvent("navigate_to_profile")
                navController.navigate("profile")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Profil")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk do nawigacji do rankingu
        Button(
            onClick = {
                logEvent("navigate_to_ranking")
                navController.navigate("ranking")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Tablica Wyników")
        }
    }
}

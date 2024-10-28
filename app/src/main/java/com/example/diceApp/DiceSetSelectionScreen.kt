package com.example.diceApp

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

@Composable
fun DiceSetSelectionScreen(navController: NavController) {
    var isOneDieSelected by remember { mutableStateOf(true) }

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
                checked = isOneDieSelected,
                onCheckedChange = { isOneDieSelected = true }
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
                checked = !isOneDieSelected,
                onCheckedChange = { isOneDieSelected = false }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Przycisk do zatwierdzenia trybu gry
        Button(
            onClick = {
                // Przekazanie wybranego trybu gry do ekranu gry
                navController.navigate("roller/${isOneDieSelected}")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Graj")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk do nawigacji do profilu
        Button(
            onClick = {
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
                navController.navigate("ranking")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Tablica Wyników")
        }
    }
}

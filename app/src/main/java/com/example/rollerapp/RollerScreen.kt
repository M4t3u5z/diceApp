package com.example.rollerapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun RollerScreen(navController: NavController, isOneDieGame: Boolean) {
    val rollerLogic = remember { RollerLogic() }

    var diceRoll1 by remember { mutableStateOf(1) }
    var diceRoll2 by remember { mutableStateOf(1) }
    var isRolling by remember { mutableStateOf(false) }
    var rollCount by remember { mutableStateOf(0) } // Licznik kliknięć "Roll the dice!"
    var duplicateCount by remember { mutableStateOf(0) } // Licznik zduplikowanych wyników
    val coroutineScope = rememberCoroutineScope()

    // Lista wyników rzutów, które będą wyświetlane w formacie normalnych liczb
    var rollResults by remember { mutableStateOf(listOf<String>()) }

    fun rollDice() {
        rollCount++ // Liczymy kliknięcie przycisku przed rozpoczęciem animacji
        isRolling = true
        coroutineScope.launch {
            animateDiceRoll {
                if (isOneDieGame) {
                    diceRoll1 = rollerLogic.rollOneDie()
                } else {
                    val (roll1, roll2) = rollerLogic.rollTwoDice()
                    diceRoll1 = roll1
                    diceRoll2 = roll2
                }
            }
            // Po zakończeniu animacji zapisujemy wynik i zliczamy duplikaty
            if (isOneDieGame) {
                rollResults = rollResults + diceRoll1.toString() // Dodajemy wynik rzutu do listy
            } else {
                rollResults = rollResults + "$diceRoll1:$diceRoll2" // Wyniki rzutów w formacie 1:1, 2:6, bez nawiasów
                if (diceRoll1 == diceRoll2) {
                    duplicateCount++ // Zwiększamy licznik zduplikowanych wyników po zakończeniu animacji
                }
            }
            isRolling = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Licznik zduplikowanych wartości w trybie dwóch kostek
        if (!isOneDieGame) {
            Text(text = "Duplicate values count: $duplicateCount")
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Licznik kliknięć nad grafiką kostek
        Text(text = "Roll count: $rollCount")

        Spacer(modifier = Modifier.height(16.dp))

        if (isOneDieGame) {
            DiceAnimationScreen(diceValue1 = diceRoll1)
        } else {
            DiceAnimationScreen(diceValue1 = diceRoll1, diceValue2 = diceRoll2)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (!isRolling) {
                    rollDice() // Kliknięcie przycisku liczone i rozpoczyna się animacja
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isRolling // Przycisk dostępny po zakończeniu animacji
        ) {
            Text(text = if (isRolling) "Rolling..." else "Roll the dice!")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk powrotu do wyboru trybu gry
        Button(
            onClick = {
                rollCount = 0 // Resetujemy licznik przy wyjściu do wyboru trybu gry
                duplicateCount = 0 // Resetujemy licznik zduplikowanych wyników
                rollResults = listOf() // Resetujemy listę wyników
                navController.navigate("diceSetSelection")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Back to Game Mode Selection")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pole wyświetlające wyniki rzutów kostek pod przyciskiem powrotu
        Text(
            text = "Roll results: ${rollResults.joinToString(", ")}", // Wyświetlenie wyników w formacie 1:1, 2:6
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

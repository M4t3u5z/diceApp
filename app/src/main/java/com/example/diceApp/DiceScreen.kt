package com.example.diceApp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun DiceScreen(navController: NavController, isOneDiceGame: Boolean, diceLogic: DiceLogic) {
    var diceRoll1 by remember { mutableStateOf(1) }
    var diceRoll2 by remember { mutableStateOf(1) }
    var isRolling by remember { mutableStateOf(false) }
    var rollCount by remember { mutableStateOf(0) }
    var duplicateCount by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    var rollResults by remember { mutableStateOf(listOf<String>()) }

    fun rollDice() {
        rollCount++  // Zwiększamy licznik kliknięć
        isRolling = true  // Blokujemy możliwość rzutu kostką podczas animacji
        coroutineScope.launch {
            animateDiceRoll {
                if (isOneDiceGame) {
                    diceRoll1 = diceLogic.rollOneDice()  // Wynik dla jednej kostki
                } else {
                    val (roll1, roll2) = diceLogic.rollTwoDice()  // Wyniki dla dwóch kostek
                    diceRoll1 = roll1
                    diceRoll2 = roll2
                }
            }

            // Zapisujemy wynik dopiero po zakończeniu animacji
            if (isOneDiceGame) {
                rollResults = rollResults + diceRoll1.toString()  // Wynik dla jednej kostki
            } else {
                rollResults = rollResults + "$diceRoll1:$diceRoll2"  // Wyniki dla dwóch kostek
                if (diceRoll1 == diceRoll2) {
                    duplicateCount++  // Zwiększamy licznik powtórzeń
                }
            }

            // Po zakończeniu animacji odblokowujemy przycisk
            isRolling = false
        }
    }

    fun saveAndExit() {
        diceLogic.saveUserScore(rollResults, isOneDiceGame)  // Zapisujemy wynik do odpowiedniego leaderboarda
        rollCount = 0  // Resetujemy licznik rzutów
        duplicateCount = 0  // Resetujemy licznik powtórzeń
        rollResults = listOf()  // Czyścimy wyniki rzutów
        diceLogic.resetSameRollCount()  // Resetujemy logikę powtórzeń
        navController.navigate("diceSetSelection")  // Powrót do ekranu wyboru trybu gry
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Jeśli gra toczy się na dwie kostki, wyświetlamy licznik powtórzeń
        if (!isOneDiceGame) {
            Text(text = "Licznik duplikujących się wartości: $duplicateCount")
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(text = "Ilość rzutów: $rollCount")
        Spacer(modifier = Modifier.height(16.dp))

        // Wyświetlanie animacji kostek
        if (isOneDiceGame) {
            Image(
                painter = painterResource(id = getDiceImage(diceRoll1)),
                contentDescription = "Dice Roll 1",
                modifier = Modifier.size(100.dp)
            )
        } else {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = getDiceImage(diceRoll1)),
                    contentDescription = "Dice Roll 1",
                    modifier = Modifier.size(100.dp)
                )
                Image(
                    painter = painterResource(id = getDiceImage(diceRoll2)),
                    contentDescription = "Dice Roll 2",
                    modifier = Modifier.size(100.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Przycisk "Rzuć kostką"
        Button(
            onClick = { if (!isRolling) rollDice() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isRolling
        ) {
            Text(text = if (isRolling) "w trakcie rzutu..." else "Rzuć kostką")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk powrotu do ekranu wyboru trybu gry
        Button(
            onClick = { saveAndExit() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Powrót do wyboru gry")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pole wyświetlające wyniki rzutów
        Text(text = "Wyniki rzutów: ${rollResults.joinToString(", ")}")
    }
}

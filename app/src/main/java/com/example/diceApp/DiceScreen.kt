package com.example.diceApp

import android.os.Bundle
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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
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

    // Inicjalizacja Firebase Analytics
    val firebaseAnalytics = Firebase.analytics

    // Funkcja pomocnicza do logowania zdarzeń
    fun logEvent(eventName: String, params: Bundle? = null) {
        firebaseAnalytics.logEvent(eventName, params)
    }

    // Logowanie otwarcia ekranu gry
    logEvent("screen_view", Bundle().apply {
        putString("screen_name", if (isOneDiceGame) "OneDiceGame" else "TwoDiceGame")
    })

    fun rollDice() {
        rollCount++  // Zwiększamy licznik kliknięć
        isRolling = true  // Blokujemy możliwość rzutu kostką podczas animacji

        // Logowanie zdarzenia kliknięcia przycisku
        logEvent("roll_button_clicked")

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

                // Logowanie wyniku dla jednej kostki
                logEvent("dice_roll", Bundle().apply {
                    putInt("dice_value", diceRoll1)
                })
            } else {
                rollResults = rollResults + "$diceRoll1:$diceRoll2"  // Wyniki dla dwóch kostek

                // Logowanie wyników dla dwóch kostek
                logEvent("dice_roll", Bundle().apply {
                    putInt("dice_value_1", diceRoll1)
                    putInt("dice_value_2", diceRoll2)
                })

                if (diceRoll1 == diceRoll2) {
                    duplicateCount++  // Zwiększamy licznik powtórzeń

                    // Logowanie zdarzenia powtórzenia wyników
                    logEvent("duplicate_roll", Bundle().apply {
                        putInt("duplicate_value", diceRoll1)
                    })
                }
            }

            // Po zakończeniu animacji odblokowujemy przycisk
            isRolling = false
        }
    }

    fun saveAndExit() {
        diceLogic.saveUserScore(rollResults, isOneDiceGame)  // Zapisujemy wynik do odpowiedniego leaderboarda

        // Logowanie zdarzenia wyjścia z ekranu gry
        logEvent("exit_game_screen", Bundle().apply {
            putInt("total_rolls", rollCount)
            putInt("duplicates_count", duplicateCount)
        })

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

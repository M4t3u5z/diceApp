package com.example.diceApp

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
    val diceLogic = remember { DiceLogic() }
    var diceRoll1 by remember { mutableStateOf(1) }
    var diceRoll2 by remember { mutableStateOf(1) }
    var isRolling by remember { mutableStateOf(false) }
    var rollCount by remember { mutableStateOf(0) }
    var duplicateCount by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    var rollResults by remember { mutableStateOf(listOf<String>()) }

    fun rollDice() {
        rollCount++
        isRolling = true
        coroutineScope.launch {
            animateDiceRoll {
                if (isOneDieGame) {
                    diceRoll1 = diceLogic.rollOneDice()
                } else {
                    val (roll1, roll2) = diceLogic.rollTwoDice()
                    diceRoll1 = roll1
                    diceRoll2 = roll2
                }
            }
            if (isOneDieGame) {
                rollResults = rollResults + diceRoll1.toString()
            } else {
                rollResults = rollResults + "$diceRoll1:$diceRoll2"
                duplicateCount = diceLogic.sameRollCount
            }
            isRolling = false
        }
    }

    fun saveAndExit() {
        diceLogic.saveUserScore(rollResults, isOneDieGame) // Zapisuje wynik do odpowiedniego leaderboardu
        rollCount = 0
        duplicateCount = 0
        rollResults = listOf()
        diceLogic.resetSameRollCount()
        navController.navigate("diceSetSelection")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isOneDieGame) {
            Text(text = "Duplicate values count: $duplicateCount")
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(text = "Roll count: $rollCount")
        Spacer(modifier = Modifier.height(16.dp))

        if (isOneDieGame) {
            DiceAnimationScreen(diceValue1 = diceRoll1)
        } else {
            DiceAnimationScreen(diceValue1 = diceRoll1, diceValue2 = diceRoll2)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { if (!isRolling) rollDice() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isRolling
        ) {
            Text(text = if (isRolling) "Rolling..." else "Roll the dice!")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { saveAndExit() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Back to Game Mode Selection")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Roll results: ${rollResults.joinToString(", ")}")
    }
}


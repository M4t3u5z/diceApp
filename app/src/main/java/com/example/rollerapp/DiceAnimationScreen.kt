package com.example.rollerapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun DiceAnimationScreen(diceValue1: Int, diceValue2: Int? = null) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animacja pierwszej kostki
        Image(
            painter = painterResource(id = getDiceImage(diceValue1)),
            contentDescription = "Dice 1",
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.FillHeight
        )

        // Animacja drugiej kostki (jeśli jest gra na dwie kostki)
        diceValue2?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = getDiceImage(it)),
                contentDescription = "Dice 2",
                modifier = Modifier.size(150.dp),
                contentScale = ContentScale.FillHeight
            )
        }
    }
}


// Funkcja do animowania kostek przez określony czas, z efektem spowalniania
suspend fun animateDiceRoll(updateDice: () -> Unit) {
    val totalDuration = 4000L // 3 sekundy
    var elapsedTime = 0L
    val firstIntervals = listOf(100L, 200L, 250L) // Szybsze interwały na początku
    val slowIntervals = listOf(300L, 400L, 500L, 600L) // Wolniejsze interwały po 1,5 sekundy

    while (elapsedTime < totalDuration) {
        updateDice() // Aktualizacja wartości kostek

        // Sprawdzenie, czy upłynęło więcej niż 1,5 sekundy (1500 ms)
        val interval = if (elapsedTime < 2000L) {
            // Szybsza zmiana przed 1,5 sekundy
            firstIntervals.minOf { it + (elapsedTime / totalDuration.toFloat() * (it / 2)).toLong() }
        } else {
            // Wolniejsza zmiana po 1,5 sekundy
            slowIntervals.minOf { it + (elapsedTime / totalDuration.toFloat() * (it / 2)).toLong() }
        }

        delay(interval) // Opóźnienie między kolejnymi zmianami wartości kostek
        elapsedTime += interval // Aktualizacja czasu trwania animacji
    }
}


// Funkcja zwracająca odpowiedni obraz dla wyniku kostki
@Composable
fun getDiceImage(diceValue: Int): Int {
    return when (diceValue) {
        1 -> R.drawable.d1
        2 -> R.drawable.d2
        3 -> R.drawable.d3
        4 -> R.drawable.d4
        5 -> R.drawable.d5
        else -> R.drawable.d6
    }
}

package com.example.rollerapp

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun AppAnimationScreen(navController: NavController) {
    // Używamy animacji rotacji dla obrazu
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Automatyczne przejście do ekranu logowania po 5 sekundach
    LaunchedEffect(Unit) {
        delay(3000) // Czekaj 5 sekund
        navController.navigate("login") {
            popUpTo("appAnimation") { inclusive = true } // Usuń ekran animacji z backstacku
        }
    }

    // Wyświetlamy animowany obraz
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.roller), // Obraz logo
                contentDescription = "Animated Logo",
                modifier = Modifier
                    .size(340.dp)
                    .rotate(rotation) // Dodanie animacji rotacji
            )
        }
    }
}

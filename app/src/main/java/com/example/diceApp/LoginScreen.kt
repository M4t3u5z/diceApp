package com.example.diceApp

import android.os.Bundle
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rollerapp.R
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    // Inicjalizacja Firebase Analytics
    val firebaseAnalytics = Firebase.analytics

    // Logowanie wyświetlenia ekranu
    firebaseAnalytics.logEvent("screen_view", Bundle().apply {
        putString("screen_name", "Login")
    })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Logo
        Image(
            painter = painterResource(id = R.drawable.roller),
            contentDescription = "Roller Logo",
            modifier = Modifier
                .size(200.dp)
                .offset(y = (-20).dp)
                .padding(bottom = 16.dp)
        )

        // Pole e-mail
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            isError = email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusRequester.requestFocus() }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pole hasło z możliwością podglądu
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    attemptLogin(auth, email, password, navController, firebaseAnalytics) { error ->
                        errorMessage = error ?: ""
                    }
                }
            ),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Wyświetlanie komunikatu błędu (jeśli wystąpi)
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Przycisk logowania
        Button(
            onClick = {
                keyboardController?.hide()
                attemptLogin(auth, email, password, navController, firebaseAnalytics) { error ->
                    errorMessage = error ?: ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Log In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk do rejestracji
        TextButton(
            onClick = {
                // Logowanie nawigacji do rejestracji
                firebaseAnalytics.logEvent("navigate_to_registration", null)
                navController.navigate("registration")
            }
        ) {
            Text(text = "Don't have an account? Register here")
        }
    }
}

// Funkcja pomocnicza do obsługi logowania użytkownika
private fun attemptLogin(
    auth: FirebaseAuth,
    email: String,
    password: String,
    navController: NavController,
    firebaseAnalytics: FirebaseAnalytics,
    onError: (String?) -> Unit
) {
    if (email.isBlank() || password.isBlank()) {
        onError("Please enter your email and password.")
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Logowanie zdarzenia pomyślnego logowania
                firebaseAnalytics.logEvent("login_attempt", Bundle().apply {
                    putString("status", "success")
                    putString("method", "email_password")
                })
                navController.navigate("diceSetSelection")
            } else {
                // Logowanie zdarzenia nieudanego logowania
                firebaseAnalytics.logEvent("login_attempt", Bundle().apply {
                    putString("status", "failure")
                    putString("method", "email_password")
                    putString("error_message", task.exception?.message)
                })
                onError(task.exception?.message ?: "Login failed.")
            }
        }
}

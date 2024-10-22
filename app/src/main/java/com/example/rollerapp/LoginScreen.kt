package com.example.rollerapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import android.util.Patterns
import androidx.compose.ui.Alignment


@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp)) // Dodaj trochę odstępu nad logo

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
            singleLine = true,  // Wyłącza dodawanie nowej linii
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next // Enter przejdzie do następnego pola
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusRequester.requestFocus() } // Przeniesienie do pola hasła
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
            singleLine = true,  // Wyłącza dodawanie nowej linii
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done // Enter wykonuje akcję zatwierdzenia (Done)
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide() // Ukryj klawiaturę
                    if (email.isNotBlank() && password.isNotBlank()) {
                        // Logowanie po wciśnięciu Enter (Done)
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    navController.navigate("diceSetSelection") // Zamiast roller
                                } else {
                                    errorMessage = task.exception?.message ?: "Login failed."
                                }
                            }
                    } else {
                        errorMessage = "Please enter your email and password."
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
                keyboardController?.hide() // Ukryj klawiaturę po kliknięciu przycisku logowania
                if (email.isBlank()) {
                    errorMessage = "Please enter a valid email."
                } else if (password.isBlank()) {
                    errorMessage = "Please enter a password."
                } else {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                navController.navigate("diceSetSelection") // Zamiast roller
                            } else {
                                errorMessage = task.exception?.message ?: "Login failed."
                            }
                        }
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
                navController.navigate("registration")
            }
        ) {
            Text(text = "Don't have an account? Register here")
        }
    }
}

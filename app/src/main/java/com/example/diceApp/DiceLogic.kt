package com.example.diceApp

import android.os.Bundle
import androidx.navigation.NavController
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class DiceLogic {

    var sameRollCount = 0
        private set

    // Inicjalizacja Firebase Analytics
    private val firebaseAnalytics = Firebase.analytics

    // Funkcja do rzucania jedną kostką
    fun rollOneDice(): Int {
        val result = (1..6).random()

        // Logowanie zdarzenia rzutu jedną kostką
        firebaseAnalytics.logEvent("roll_one_dice", Bundle().apply {
            putInt("dice_value", result)
        })

        return result
    }

    // Funkcja do rzucania dwiema kostkami
    fun rollTwoDice(): Pair<Int, Int> {
        val roll1 = (1..6).random()
        val roll2 = (1..6).random()

        // Logowanie zdarzenia rzutu dwiema kostkami
        firebaseAnalytics.logEvent("roll_two_dice", Bundle().apply {
            putInt("dice_value_1", roll1)
            putInt("dice_value_2", roll2)
        })

        if (roll1 == roll2) {
            sameRollCount++

            // Logowanie zdarzenia duplikacji rzutów
            firebaseAnalytics.logEvent("duplicate_roll", Bundle().apply {
                putInt("duplicate_value", roll1)
            })
        }

        return Pair(roll1, roll2)
    }

    // Funkcja obliczająca sumę wyników z rollResults
    fun calculateTotalScore(rollResults: List<String>, isOneDiceGame: Boolean): Int {
        var totalScore = 0
        rollResults.forEach { result ->
            if (isOneDiceGame) {
                totalScore += result.toInt()
            } else {
                val rolls = result.split(":").map { it.toInt() }
                totalScore += rolls.sum()
            }
        }

        // Logowanie zdarzenia obliczenia wyniku
        firebaseAnalytics.logEvent("calculate_score", Bundle().apply {
            putInt("total_score", totalScore)
        })

        return totalScore
    }

    // Zapisz wynik użytkownika w Firebase
    fun saveUserScore(rollResults: List<String>, isOneDiceGame: Boolean) {
        val totalScore = calculateTotalScore(rollResults, isOneDiceGame)
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid
            val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

            databaseRef.child("profileName").get().addOnSuccessListener { snapshot ->
                val profileName = snapshot.getValue(String::class.java) ?: "Anonymous"

                val leaderboardRef = if (isOneDiceGame) {
                    FirebaseDatabase.getInstance().getReference("one_die_leaderboard").child(userId)
                } else {
                    FirebaseDatabase.getInstance().getReference("two_dice_leaderboard").child(userId)
                }

                leaderboardRef.child("score").get().addOnSuccessListener { scoreSnapshot ->
                    val currentScore = scoreSnapshot.getValue(Int::class.java) ?: 0
                    val newTotalScore = currentScore + totalScore

                    leaderboardRef.setValue(mapOf("profileName" to profileName, "score" to newTotalScore))
                        .addOnSuccessListener {
                            // Logowanie zdarzenia zapisu wyniku
                            firebaseAnalytics.logEvent("save_score", Bundle().apply {
                                putString("profile_name", profileName)
                                putInt("new_score", newTotalScore)
                            })
                        }
                        .addOnFailureListener { error ->
                            println("Błąd podczas zapisywania wyniku: ${error.message}")
                        }
                }
            }.addOnFailureListener {
                println("Błąd podczas pobierania nazwy użytkownika: ${it.message}")
            }
        }
    }

    // Funkcja do aktualizacji nazwy użytkownika w Firebase
    fun updateUserProfileName(newProfileName: String) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if (user != null) {
            val userId = user.uid
            val userRef = FirebaseDatabase.getInstance().getReference("users").child(userId)

            userRef.child("profileName").setValue(newProfileName).addOnSuccessListener {
                println("Nazwa użytkownika została zaktualizowana.")

                // Logowanie zdarzenia aktualizacji nazwy użytkownika
                firebaseAnalytics.logEvent("update_profile_name", Bundle().apply {
                    putString("new_profile_name", newProfileName)
                })
            }.addOnFailureListener { error ->
                println("Błąd przy aktualizacji nazwy użytkownika: ${error.message}")
            }
        }
    }

    // Funkcja pobierająca nazwę użytkownika z Firebase
    fun fetchUserProfileName(onComplete: (String?) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
            databaseRef.child("profileName").get().addOnSuccessListener { snapshot ->
                val profileName = snapshot.getValue(String::class.java)
                onComplete(profileName)
            }.addOnFailureListener {
                onComplete(null)
            }
        } else {
            onComplete(null)
        }
    }

    // Funkcja do aktualizacji leaderboard po zmianie nazwy profilu
    fun updateLeaderboardAfterProfileNameChange() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid

            fetchUserProfileName { profileName ->
                if (profileName != null) {
                    val databaseOneDie = FirebaseDatabase.getInstance().getReference("one_die_leaderboard").child(userId)
                    val databaseTwoDice = FirebaseDatabase.getInstance().getReference("two_dice_leaderboard").child(userId)

                    databaseOneDie.child("profileName").setValue(profileName)
                    databaseTwoDice.child("profileName").setValue(profileName)
                }
            }
        }
    }

    // Funkcja do wylogowania użytkownika
    fun logoutAndExit(navController: NavController) {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()

        // Logowanie zdarzenia wylogowania
        firebaseAnalytics.logEvent("user_logout", null)

        navController.navigate("login")
    }

    // Funkcja resetowania licznika duplikatów
    fun resetSameRollCount() {
        sameRollCount = 0

        // Logowanie zdarzenia resetu licznika
        firebaseAnalytics.logEvent("reset_duplicate_count", null)
    }
}

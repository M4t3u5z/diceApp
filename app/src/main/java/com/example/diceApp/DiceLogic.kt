package com.example.diceApp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DiceLogic {
    var sameRollCount = 0
        private set

    // Logika rzutu jedną kostką
    fun rollOneDice(): Int {
        return (1..6).random()  // Losowy wynik rzutu jedną kostką
    }

    // Logika rzutu dwiema kostkami
    fun rollTwoDice(): Pair<Int, Int> {
        val roll1 = (1..6).random()
        val roll2 = (1..6).random()

        if (roll1 == roll2) {
            sameRollCount++  // Zwiększamy licznik powtórzeń
        }

        return Pair(roll1, roll2)  // Zwraca wynik rzutów dwiema kostkami
    }

    // Funkcja obliczająca sumę wyników z rollResults
    fun calculateTotalScore(rollResults: List<String>, isOneDieGame: Boolean): Int {
        var totalScore = 0
        rollResults.forEach { result ->
            if (isOneDieGame) {
                totalScore += result.toInt()  // Dodajemy wynik dla jednej kostki
            } else {
                val rolls = result.split(":").map { it.toInt() }
                totalScore += rolls.sum()  // Dodajemy wynik dla dwóch kostek
            }
        }
        return totalScore
    }

    // Zapisywanie wyniku użytkownika do odpowiedniej kolumny w Firebase
    fun saveUserScore(rollResults: List<String>, isOneDieGame: Boolean) {
        val totalScore = calculateTotalScore(rollResults, isOneDieGame)
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val databaseRef = if (isOneDieGame) {
                // Jeśli gracz gra jedną kostką, zapis do one_die_leaderboard
                FirebaseDatabase.getInstance().getReference("one_die_leaderboard").child(userId)
            } else {
                // Jeśli gracz gra dwiema kostkami, zapis do two_dice_leaderboard
                FirebaseDatabase.getInstance().getReference("two_dice_leaderboard").child(userId)
            }

            // Pobieranie nazwy użytkownika, aby upewnić się, że nie jest nadpisana pustą wartością
            fetchUserProfileName { profileName ->
                val finalProfileName = profileName ?: "Anonymous"  // Jeśli brak nazwy, ustaw "Anonymous"

                // Zapisywanie wyniku do odpowiedniego leaderboard z poprawną nazwą użytkownika
                val scoreMap = mapOf(
                    "profileName" to finalProfileName,
                    "score" to totalScore
                )

                databaseRef.setValue(scoreMap).addOnSuccessListener {
                    println("Wynik został zapisany pomyślnie")
                }.addOnFailureListener {
                    println("Błąd podczas zapisywania wyniku: ${it.message}")
                }
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
            databaseRef.child("profileName").get().addOnSuccessListener {
                val profileName = it.getValue(String::class.java)
                onComplete(profileName)
            }.addOnFailureListener {
                onComplete(null)
            }
        } else {
            onComplete(null)
        }
    }

    // Resetowanie licznika powtórzeń
    fun resetSameRollCount() {
        sameRollCount = 0
    }
}

package com.example.diceApp

import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DiceLogic {

    var sameRollCount = 0
        private set

    // Funkcja do rzucania jedną kostką
    fun rollOneDice(): Int {
        return (1..6).random()  // Losowy wynik rzutu jedną kostką
    }

    // Funkcja do rzucania dwiema kostkami
    fun rollTwoDice(): Pair<Int, Int> {
        val roll1 = (1..6).random()
        val roll2 = (1..6).random()

        if (roll1 == roll2) {
            sameRollCount++  // Zwiększamy licznik powtórzeń dla wyników, które są takie same
        }

        return Pair(roll1, roll2)
    }

    // Funkcja obliczająca sumę wyników z rollResults
    fun calculateTotalScore(rollResults: List<String>, isOneDiceGame: Boolean): Int {
        var totalScore = 0
        rollResults.forEach { result ->
            if (isOneDiceGame) {
                totalScore += result.toInt()  // Dodajemy wynik dla jednej kostki
            } else {
                val rolls = result.split(":").map { it.toInt() }
                totalScore += rolls.sum()  // Dodajemy wynik dla dwóch kostek
            }
        }
        return totalScore
    }

    // Zapisz wynik użytkownika, upewniając się, że profileName jest prawidłowo pobrany
    fun saveUserScore(rollResults: List<String>, isOneDiceGame: Boolean) {
        val totalScore = calculateTotalScore(rollResults, isOneDiceGame)
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid

            // Pobierz profileName z bazy danych (jeśli displayName jest null)
            val databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
            databaseRef.child("profileName").get().addOnSuccessListener { snapshot ->
                val profileName = snapshot.getValue(String::class.java) ?: "Anonymous"

                val leaderboardRef = if (isOneDiceGame) {
                    FirebaseDatabase.getInstance().getReference("one_die_leaderboard").child(userId)
                } else {
                    FirebaseDatabase.getInstance().getReference("two_dice_leaderboard").child(userId)
                }

                // Pobieramy aktualny wynik użytkownika i dodajemy nowy wynik
                leaderboardRef.child("score").get().addOnSuccessListener { scoreSnapshot ->
                    val currentScore = scoreSnapshot.getValue(Int::class.java) ?: 0
                    val newTotalScore = currentScore + totalScore

                    // Zapisujemy zaktualizowany wynik
                    leaderboardRef.setValue(mapOf("profileName" to profileName, "score" to newTotalScore))
                        .addOnSuccessListener {
                            println("Wynik został zaktualizowany pomyślnie.")
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

            // Zaktualizuj nazwę użytkownika w bazie danych Firebase
            userRef.child("profileName").setValue(newProfileName).addOnSuccessListener {
                println("Nazwa użytkownika została zaktualizowana.")
            }.addOnFailureListener { error ->
                println("Błąd przy aktualizacji nazwy użytkownika: ${error.message}")
            }
        }
    }

    // Funkcja do aktualizacji leaderboarda po zmianie nazwy użytkownika
    fun updateLeaderboardAfterProfileNameChange() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid

            // Pobierz nazwę profilu użytkownika
            fetchUserProfileName { profileName ->
                if (profileName != null) {
                    updateLeaderboardProfileName(userId, profileName)
                } else {
                    println("Błąd: Nie można pobrać nazwy profilu.")
                }
            }
        }
    }

    // Aktualizacja nazwy w leaderboardach
    private fun updateLeaderboardProfileName(userId: String, newProfileName: String) {
        val databaseOneDie = FirebaseDatabase.getInstance().getReference("one_die_leaderboard").child(userId)
        val databaseTwoDice = FirebaseDatabase.getInstance().getReference("two_dice_leaderboard").child(userId)

        // Zaktualizuj nazwę w leaderboardzie dla jednej kostki
        databaseOneDie.child("profileName").setValue(newProfileName).addOnSuccessListener {
            println("Nazwa użytkownika zaktualizowana w leaderboardzie dla jednej kostki.")
        }.addOnFailureListener { error ->
            println("Błąd podczas aktualizacji nazwy użytkownika w leaderboardzie dla jednej kostki: ${error.message}")
        }

        // Zaktualizuj nazwę w leaderboardzie dla dwóch kostek
        databaseTwoDice.child("profileName").setValue(newProfileName).addOnSuccessListener {
            println("Nazwa użytkownika zaktualizowana w leaderboardzie dla dwóch kostek.")
        }.addOnFailureListener { error ->
            println("Błąd podczas aktualizacji nazwy użytkownika w leaderboardzie dla dwóch kostek: ${error.message}")
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

    // Funkcja do wylogowania i zamknięcia aplikacji
    fun logoutAndExit(navController: NavController) {
        val auth = FirebaseAuth.getInstance()
        auth.signOut()  // Wylogowanie użytkownika
        navController.navigate("login")  // Powrót do ekranu logowania
    }

    // Resetowanie licznika powtórzeń
    fun resetSameRollCount() {
        sameRollCount = 0
    }
}

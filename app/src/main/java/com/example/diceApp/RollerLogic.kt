package com.example.diceApp

class RollerLogic {
    var sameRollCount = 0
        private set

    // Logika rzutu jedną kostką
    fun rollOneDie(): Int {
        return (1..6).random()
    }

    // Logika rzutu dwiema kostkami
    fun rollTwoDice(): Pair<Int, Int> {
        val roll1 = (1..6).random()
        val roll2 = (1..6).random()

        if (roll1 == roll2) {
            sameRollCount++
        }

        return Pair(roll1, roll2)
    }

    // Resetowanie licznika powtórzeń
    fun resetSameRollCount() {
        sameRollCount = 0
    }
}

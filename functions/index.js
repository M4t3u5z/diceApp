const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

// Resetowanie wyników każdego dnia o 24:00 czasu polskiego
exports.resetLeaderboard = functions.pubsub.schedule("0 0 * * *")
    .timeZone("Europe/Warsaw") // Czas polski
    .onRun(async (context) => {
      const leaderboardRefOneDie=admin.database().ref("one_die_leaderboard");
      const leaderboardRefTwoDice=admin.database().ref("two_dice_leaderboard");
      try {
        // Resetowanie wyników dla jednej kostki
        const oneDieSnapshot = await leaderboardRefOneDie.once("value");
        oneDieSnapshot.forEach((userSnapshot) => {
          userSnapshot.ref.child("score").set(0); // Ustawienie wyniku na 0
        });
        console.log("Wyniki dla jednej kostki zresetowane.");

        // Resetowanie wyników dla dwóch kostek
        const twoDiceSnapshot=await leaderboardRefTwoDice.once("value");
        twoDiceSnapshot.forEach((userSnapshot) => {
          userSnapshot.ref.child("score").set(0); // Ustawienie wyniku na 0
        });
        console.log("Wyniki dla dwóch kostek zresetowane.");
      } catch (error) {
        console.error("Błąd podczas resetowania wyników:", error);
      }
      return null;
    });

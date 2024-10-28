import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.*

data class UserScore(val username: String = "", val score: Int = 0)

@Composable
fun RankingScreen(navController: NavController) {
    var oneDiceScores by remember { mutableStateOf(listOf<UserScore>()) }
    var twoDiceScores by remember { mutableStateOf(listOf<UserScore>()) }

    // Pobieranie danych dla jednej kostki z Firebase
    val databaseOneDice = FirebaseDatabase.getInstance().getReference("one_die_leaderboard")
    val oneDiceListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val scores = mutableListOf<UserScore>()
            for (userSnapshot in snapshot.children) {
                val username = userSnapshot.child("profileName").getValue(String::class.java) ?: "Nieznany"
                val score = userSnapshot.child("score").getValue(Int::class.java) ?: 0
                scores.add(UserScore(username, score))
            }
            oneDiceScores = scores.sortedByDescending { it.score }
        }

        override fun onCancelled(error: DatabaseError) {
            // Obsługa błędów
        }
    }

    // Pobieranie danych dla dwóch kostek z Firebase
    val databaseTwoDice = FirebaseDatabase.getInstance().getReference("two_dice_leaderboard")
    val twoDiceListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val scores = mutableListOf<UserScore>()
            for (userSnapshot in snapshot.children) {
                val username = userSnapshot.child("profileName").getValue(String::class.java) ?: "Nieznany"
                val score = userSnapshot.child("score").getValue(Int::class.java) ?: 0
                scores.add(UserScore(username, score))
            }
            twoDiceScores = scores.sortedByDescending { it.score }
        }

        override fun onCancelled(error: DatabaseError) {
            // Obsługa błędów
        }
    }

    LaunchedEffect(Unit) {
        databaseOneDice.addValueEventListener(oneDiceListener)
        databaseTwoDice.addValueEventListener(twoDiceListener)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Nagłówek "Ranking Globalny"
        Text(
            text = "Ranking Globalny",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Layout podzielony na dwie kolumny
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Tabela dla gry jedną kostką
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Jedna kostka",
                    fontSize = 12.sp,  // Zmniejszenie rozmiaru tekstu
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(oneDiceScores) { userScore ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = userScore.username)
                            Text(text = userScore.score.toString())
                        }
                        Divider()
                    }
                }
            }

            // Tabela dla gry dwiema kostkami
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Dwie kostki",
                    fontSize = 12.sp,  // Zmniejszenie rozmiaru tekstu
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(twoDiceScores) { userScore ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = userScore.username)
                            Text(text = userScore.score.toString())
                        }
                        Divider()
                    }
                }
            }
        }

        // Przycisk powrotu do ekranu wyboru gry
        Button(
            onClick = {
                navController.navigate("diceSetSelection")
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Powrót do wyboru gry")
        }
    }
}

@Preview
@Composable
fun RankingScreenPreview() {
    val navController = rememberNavController()
    RankingScreen(navController = navController)
}

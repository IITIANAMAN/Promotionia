package com.amanmeena.promotionia.Screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


data class LeaderboardEntry(
    val name: String,
    val coins: Int
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(modifier: Modifier,navController: NavController) {
    val dummyData = listOf(
        LeaderboardEntry("Aman", 950),
        LeaderboardEntry("Rohan", 870),
        LeaderboardEntry("Priya", 820),
        LeaderboardEntry("Arjun", 780),
        LeaderboardEntry("Simran", 750),
        LeaderboardEntry("Vivek", 700),
        LeaderboardEntry("Isha", 660),
        LeaderboardEntry("Karan", 620),
        LeaderboardEntry("Sneha", 580),
        LeaderboardEntry("Rahul", 540)
    )
    Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FB))

                .padding(16.dp)
        ) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                itemsIndexed(dummyData) { index, item ->
                    LeaderboardRow(rank = index + 1, name = item.name, coins = item.coins)
                }
            }
        }
    }


@Composable
fun LeaderboardRow(rank: Int, name: String, coins: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            when (rank) {
                                1 -> Color(0xFFFFD700) // Gold
                                2 -> Color(0xFFC0C0C0) // Silver
                                3 -> Color(0xFFCD7F32) // Bronze
                                else -> Color.LightGray
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("#$rank", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(name, style = MaterialTheme.typography.titleMedium)
            }
            Text(
                text = "${coins} Coins",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
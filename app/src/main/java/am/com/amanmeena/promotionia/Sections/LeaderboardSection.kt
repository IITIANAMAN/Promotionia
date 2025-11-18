package com.amanmeena.promotionia.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LeaderboardSection(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically ) {
            Text("All India Leaderboard",
                fontWeight = FontWeight.Bold,

            )
            IconButton(onClick = {
                navController.navigate("leader")
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    "Move to leaderboard",
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("No rankings available yet. Start completing tasks to join the leaderboard!")
    }
}
package com.amanmeena.promotionia.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LeaderboardSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("All India Leaderboard")
        Spacer(modifier = Modifier.height(8.dp))
        Text("No rankings available yet. Start completing tasks to join the leaderboard!")
    }
}
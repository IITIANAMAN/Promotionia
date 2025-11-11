package com.amanmeena.promotionia.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amanmeena.promotionia.ui.components.InfoCard

@Composable
fun StatsSection() {
    val statsList = listOf(
        Triple("Current Rank", "-", "All India Rank"),
        Triple("Tasks Completed", "8", "Completed successfully"),
        Triple("Pending Tasks", "2", "Waiting to be done"),
        Triple("Daily Streak", "5", "Consecutive days")
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Title
        Text(
            text = "Your Stats",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Responsive Grid Layout for Cards
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 140.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp) // prevents infinite height growth
        ) {
            items(statsList) { (title, value, subtitle) ->
                InfoCard(title = title, value = value, subtitle = subtitle)
            }
        }
    }
}
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

fun StatsSection(user: Map<String, Any>?) {

    val totalCoin = user?.get("totalCoin") as? Long ?: 0L
    val completedTasks = user?.get("completedTasks") as? Map<*, *> ?: emptyMap<Any, Any>()

    // Count tasks across all platforms
    val totalTasksCompleted = completedTasks.values.sumOf { platformData ->
        val platformMap = platformData as? Map<*, *> ?: emptyMap<Any, Any>()
        platformMap.values.sumOf { taskList ->
            (taskList as? List<*>)?.size ?: 0
        }
    }

    val statsList = listOf(
        Triple("Tasks Completed", "$totalTasksCompleted", "Completed successfully"),
        Triple("Coins", totalCoin.toString(), "Total coins")
    )

    Column(modifier = Modifier.fillMaxWidth()) {

        Text(
            text = "Your Stats",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,   // FIXED
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 140.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp)
        ) {
            items(statsList) { (title, value, subtitle) ->
                InfoCard(
                    title = title,
                    value = value,
                    subtitle = subtitle
                )
            }
        }
    }
}
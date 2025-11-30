package com.amanmeena.promotionia.Screens

import am.com.amanmeena.promotionia.Viewmodels.MainViewModel
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.amanmeena.promotionia.ui.components.FollowCard
import kotlinx.coroutines.delay

@Composable
fun FollowSection(viewModel: MainViewModel) {

    val user = viewModel.userData.value
    val followMap = user?.get("followTasks") as? Map<String, Boolean> ?: emptyMap()
    val completedCount = followMap.values.count { it }

    val context = LocalContext.current

    var cooldownActive by remember { mutableStateOf(false) }
    var cooldownTime by remember { mutableStateOf(0) }

    val socialLinks = listOf(
        Triple("youtube", "Promotionia Official", "https://www.youtube.com/@promotionia"),
        Triple("instagram", "@promotionia_official", "https://www.instagram.com/promotionia_official"),
        Triple("facebook", "Promotionia", "https://www.facebook.com/promotionia"),
        Triple("twitter", "@promotionia", "https://twitter.com/promotionia")
    )

    // Cooldown Timer
    LaunchedEffect(cooldownActive) {
        if (cooldownActive) {
            cooldownTime = 5
            while (cooldownTime > 0) {
                delay(1000)
                cooldownTime--
            }
            cooldownActive = false
        }
    }

    val colors = MaterialTheme.colorScheme

    Column(Modifier.fillMaxWidth()) {

        // Title
        Text(
            "Follow Promotionia",
            style = MaterialTheme.typography.titleMedium,
            color = colors.onBackground
        )

        Spacer(Modifier.height(6.dp))

        Text(
            "$completedCount / ${socialLinks.size} tasks completed",
            style = MaterialTheme.typography.bodySmall,
            color = colors.primary
        )

        Spacer(Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            socialLinks.forEach { (key, handle, url) ->

                val done = followMap[key] == true

                FollowCard(
                    platform = key,
                    handle = handle,
                    isEnabled = !cooldownActive && !done,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)

                        viewModel.markFollowTaskDone(key)

                        cooldownActive = true
                    }
                )
            }
        }

        if (cooldownActive) {
            Spacer(Modifier.height(10.dp))
            Text(
                "Please wait ${cooldownTime}s before next action…",
                style = MaterialTheme.typography.labelSmall,
                color = colors.error
            )
        }
    }
}
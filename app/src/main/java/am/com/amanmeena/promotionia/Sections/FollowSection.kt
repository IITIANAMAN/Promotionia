package com.amanmeena.promotionia.Screens

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
fun FollowSection() {
    val context = LocalContext.current

    // 🧠 States
    var cooldownActive by remember { mutableStateOf(false) }
    var cooldownTime by remember { mutableStateOf(0) }
    var completedCount by remember { mutableStateOf(0) }

    // ✅ All tasks
    val socialLinks = listOf(
        Triple("YouTube", "Promotionia Official", "https://www.youtube.com/@promotionia"),
        Triple("Instagram", "@promotionia_official", "https://www.instagram.com/promotionia_official"),
        Triple("Facebook", "Promotionia", "https://www.facebook.com/promotionia"),
        Triple("X (Twitter)", "@promotionia", "https://twitter.com/promotionia")
    )

    // ⏱️ Cooldown timer coroutine
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

    // 🌐 UI
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Follow Promotionia",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${completedCount} / ${socialLinks.size} tasks completed",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            socialLinks.forEach { (platform, handle, link) ->
                FollowCard(
                    platform = platform,
                    handle = handle,
                    onClick = {
                        if (!cooldownActive) {
                            // Open link instantly
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                            context.startActivity(intent)

                            // Increment completed tasks
                            completedCount++

                            // Start cooldown
                            cooldownActive = true
                        }
                    },
                    isEnabled = !cooldownActive
                )
            }
        }

        // Optional visual indicator for cooldown
        if (cooldownActive) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Please wait ${cooldownTime}s before next action…",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
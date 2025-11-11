package com.amanmeena.promotionia.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FollowCard(
    platform: String,
    handle: String,
    onClick: () -> Unit,
    isEnabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Use of icon here
        Column {
            Text(handle)
            Text(platform)
        }
        Button(
            onClick = onClick,
            enabled = isEnabled
        ) {
            Text(if (isEnabled) "Follow" else "Wait…")
        }
    }
}
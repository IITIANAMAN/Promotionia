package com.amanmeena.promotionia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InfoCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .width(120.dp)
            .height(110.dp)
            .shadow(3.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface       // THEMED CARD BG
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            colors.surface,                     // TOP GRADIENT
                            colors.surfaceVariant               // bottom gradient (dynamic)
                        )
                    )
                )
                .padding(12.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                // Optional icon box
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(
                                colors.secondaryContainer,          // themed background
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        icon()
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 13.sp,
                        color = colors.onSurfaceVariant          // THEMED
                    )
                )

                // Value
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        color = colors.primary                    // THEMED
                    )
                )

                // Subtitle
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant           // THEMED
                    )
                )
            }
        }
    }
}
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
    icon: @Composable (() -> Unit)? = null // optional icon slot for flexibility
) {
    Card(
        modifier = modifier
            .width(120.dp)
            .height(110.dp)
            .shadow(3.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFFDFDFD), Color(0xFFF7F8FB))
                    )
                )
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                // Icon Row (optional)
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(
                                Color(0xFFFFF4D1),
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
                        color = Color(0xFF757575)
                    )
                )

                // Value (Main Focus)
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 22.sp,
                        color = Color(0xFF212121)
                    )
                )

                // Subtitle
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E)
                    )
                )
            }
        }
    }
}
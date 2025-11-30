package com.amanmeena.promotionia.ui.components

import am.com.amanmeena.promotionia.R
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SocialCard(
    platform: String,
    navController: NavController,
    comingSoon: Boolean = false
) {

    // Platform → image file
    val iconRes = when (platform.lowercase()) {
        "facebook" -> R.drawable.fb
        "instagram" -> R.drawable.insta
        "x" -> R.drawable.x
        "youtube" -> R.drawable.yt
        "linkedin" -> R.drawable.`in`
        else -> R.drawable.map
    }
    val context = LocalContext.current

    val iconAlpha = if (comingSoon) 0.4f else 1f

    Box(
        modifier = Modifier
            .padding(6.dp)
            .size(55.dp)
            .graphicsLayer(alpha = iconAlpha)
            .clickable {
                if(platform == "Map"){
                    navController.navigate("google_review_tasks")
                }else if(comingSoon == true){
                    Toast.makeText(context,"Coming soon",Toast.LENGTH_LONG).show()
                }else{
                    navController.navigate("acc/$platform")
                }

            },
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(iconRes),
            contentDescription = "$platform icon",
            modifier = Modifier.fillMaxSize()
        )


    }
}
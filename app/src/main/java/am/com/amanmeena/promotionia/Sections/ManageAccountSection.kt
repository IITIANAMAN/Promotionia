package com.amanmeena.promotionia.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amanmeena.promotionia.ui.components.SocialCard

@Composable
fun ManageAccountsSection(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Manage Social Media Accounts")
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SocialCard(platform = "Facebook", navController)
            SocialCard(platform = "Instagram", navController )
            SocialCard(platform = "X (Twitter)", navController)
        }
    }
}
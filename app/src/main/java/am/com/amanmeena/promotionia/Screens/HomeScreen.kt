package com.amanmeena.promotionia.Screens

import am.com.amanmeena.promotionia.AuthClient
import am.com.amanmeena.promotionia.Viewmodels.MainViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavHostController, viewModel: MainViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadCurrentUser()
    }
    val user = viewModel.userData.value
    Surface(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        color = Color.White // light background like website
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {


            BorderedSection {
                ProfileSection(user)
            }

            BorderedSection {
                StatsSection()
            }

            BorderedSection {
                ManageAccountsSection(navController)
            }

            BorderedSection {
                LeaderboardSection()
            }

            BorderedSection {
                FollowSection()
            }
            Button(onClick = {
                navController.navigate("leader")
            },
                ) {
                Text("Just use me")
            }
            val auth = AuthClient()
            Button(onClick = {
                auth.logout()
                navController.navigate("login")
            },
            ) {
                Text("Just use me")
            }

        }
    }
}
@Composable
fun BorderedSection(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            content()
            Divider(color = Color(0xFFF1F1F1), thickness = 0.8.dp)
        }
    }
}
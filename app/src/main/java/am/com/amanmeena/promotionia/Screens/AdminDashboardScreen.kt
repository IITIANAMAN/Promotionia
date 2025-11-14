package am.com.amanmeena.promotionia.Screens

import am.com.amanmeena.promotionia.Viewmodels.AdminViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AdminViewModel
) {
    LaunchedEffect(Unit) { viewModel.loadStatsOnce() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("admin_tasks/add") },
                containerColor = Color.Black,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF2F2F2))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // ---- SUMMARY CARDS ----
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(
                    title = "Users",
                    value = viewModel.totalUsers.value.toString(),
                    icon = Icons.Default.People,
                    modifier = Modifier.weight(1f)
                )

                SummaryCard(
                    title = "Tasks",
                    value = viewModel.totalTasks.value.toString(),
                    icon = Icons.Default.Task,
                    modifier = Modifier.weight(1f)
                )
            }

            SummaryCard(
                title = "Coins Distributed",
                value = viewModel.totalCoinsDistributed.value.toString(),
                icon = Icons.Default.Star,
                modifier = Modifier.fillMaxWidth()
            )

            // ---- MANAGE BUTTONS ----
            AdminActionButton(
                label = "Manage Tasks",
                icon = Icons.Default.List,
                color = Color.Black
            ) { navController.navigate("admin_tasks") }

            AdminActionButton(
                label = "View Users",
                icon = Icons.Default.People,
                color = Color.DarkGray
            ) { navController.navigate("admin_users") }
        }
    }
}

@Composable
fun SummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Circle Icon Badge
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .background(Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(title, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                Text(value, style = MaterialTheme.typography.headlineSmall, color = Color.Black)
            }
        }
    }
}

@Composable
fun AdminActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Icon(icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text(label)
    }
}
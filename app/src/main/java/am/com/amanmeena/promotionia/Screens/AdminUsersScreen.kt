package am.com.amanmeena.promotionia.Screens

import PersonData

import am.com.amanmeena.promotionia.Viewmodels.AdminViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(navController: NavController, viewModel: AdminViewModel) {
    var query by remember { mutableStateOf("") }
    var selectedState by remember { mutableStateOf("All") }
    val states = listOf("All") + viewModel.statesList

    val bgGradient = Brush.verticalGradient(colors = listOf(Color(0xFF0C0C0C), Color(0xFF1A1A1A)))

    Scaffold(topBar = { TopAppBar(title = { Text("Users", color = Color.White) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.DarkGray)) }) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(bgGradient).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            OutlinedTextField(value = query, onValueChange = { query = it }, label = { Text("Search") }, leadingIcon = { Icon(Icons.Default.PersonSearch, contentDescription = null) }, modifier = Modifier.fillMaxWidth())

            ExposedDropdownMenuBox(expanded = viewModel.stateDropdownExpanded.value, onExpandedChange = { viewModel.stateDropdownExpanded.value = !viewModel.stateDropdownExpanded.value }) {
                OutlinedTextField(value = selectedState, onValueChange = {}, readOnly = true, label = { Text("Filter by state") }, modifier = Modifier.menuAnchor().fillMaxWidth(), trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(viewModel.stateDropdownExpanded.value) })
                ExposedDropdownMenu(expanded = viewModel.stateDropdownExpanded.value, onDismissRequest = { viewModel.stateDropdownExpanded.value = false }) {
                    states.forEach { st ->
                        DropdownMenuItem(text = { Text(st) }, onClick = { selectedState = st; viewModel.stateDropdownExpanded.value = false })
                    }
                }
            }

            val filtered = viewModel.users.filter { u ->
                val matchesQuery = u.name.contains(query, ignoreCase = true) || u.email.contains(query, ignoreCase = true)
                val matchesState = selectedState == "All" || u.state == selectedState
                matchesQuery && matchesState
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filtered) { user ->
                    UserCard(user)
                }
            }
        }
    }
}

@Composable
fun UserCard(user: PersonData) {
    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(user.name, color = Color.White, style = MaterialTheme.typography.titleMedium)
            Text(user.email, color = Color.Gray)
            Text("State: ${user.state}", color = Color.Gray)
            Text("Coins: ${user.totalCoin}", color = Color(0xFFFFD700))
        }
    }
}
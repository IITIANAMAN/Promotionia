package am.com.amanmeena.promotionia.AdminPanel

import PersonData
import am.com.amanmeena.promotionia.Viewmodels.AdminViewModel
import am.com.amanmeena.promotionia.utils.TopAppBarPromotionia
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsersScreen(
    navController: NavController,
    viewModel: AdminViewModel
) {
    var query by remember { mutableStateOf("") }
    var selectedState by remember { mutableStateOf("All") }

    val states = remember(viewModel.statesList) {
        listOf("All") + viewModel.statesList.sorted()
    }




    Scaffold (
        topBar = {
            TopAppBarPromotionia(modifier = Modifier,"User Screen",navController)
        }
    ){ it->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White )
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search users") },
                leadingIcon = {
                    Icon(Icons.Default.PersonSearch, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),

                )
            ExposedDropdownMenuBox(
                expanded = viewModel.stateDropdownExpanded.value,
                onExpandedChange = {
                    viewModel.stateDropdownExpanded.value = !viewModel.stateDropdownExpanded.value
                }
            ) {

                OutlinedTextField(
                    value = selectedState,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filter by state") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = viewModel.stateDropdownExpanded.value
                        )
                    },

                    )

                ExposedDropdownMenu(
                    expanded = viewModel.stateDropdownExpanded.value,
                    onDismissRequest = {
                        viewModel.stateDropdownExpanded.value = false
                    }
                ) {
                    states.forEach { st ->
                        DropdownMenuItem(
                            text = { Text(st) },
                            onClick = {
                                selectedState = st
                                viewModel.stateDropdownExpanded.value = false
                            }
                        )
                    }
                }
            }


            val filteredUsers = viewModel.users.filter { user ->
                val matchesQuery =
                    user.name.contains(query, ignoreCase = true) ||
                            user.email.contains(query, ignoreCase = true)

                val matchesState =
                    (selectedState == "All") || (user.state == selectedState)

                matchesQuery && matchesState
            }
            Text("Total number of user ${filteredUsers.size}")

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredUsers) { user ->
                    UserCard(user,navController, adminViewModel = viewModel)
                }
            }
        }
    }

    }


@Composable
fun UserCard(
    user: PersonData,
    navController: NavController,
    adminViewModel: AdminViewModel
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3F6FB)
        ),
        modifier = Modifier.fillMaxWidth().clickable{
            adminViewModel.selectedUser.value = user
            navController.navigate("userprofile")
        }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = user.name,
                color = Color(0xFF1A1F36),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = user.email,
                color = Color(0xFF5A6476)
            )
            Text(
                text = "State: ${user.state}",
                color = Color(0xFF5A6476)
            )
            Text(
                text = "Coins: ${user.totalCoin}",
                color = Color(0xFF5A6476)
            )
            Text(
                text =  user.number,
                color = Color(0xFF5A6476)
            )
        }
    }
}
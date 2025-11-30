package am.com.amanmeena.promotionia.AdminPanel

import TopAppBarPromotionia
import am.com.amanmeena.promotionia.Viewmodels.AdminViewModel

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController, viewModel: AdminViewModel) {

    var title by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var reward by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }
    var click by remember { mutableStateOf("") }

    // 🔥 NEW PLATFORM ADDED HERE
    val platforms = listOf(
        "Instagram",
        "Facebook",
        "X",
        "GoogleReview"        // 👈 NEW MAP REVIEW PLATFORM
    )

    Scaffold(
        topBar = {
            TopAppBarPromotionia(
                modifier = Modifier,
                route = "Add task",
                navController = navController
            )
        }
    ) { it ->

        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Title") }
            )

            OutlinedTextField(
                value = link,
                onValueChange = { link = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Link (Google Maps / Insta / FB / X)") }
            )

            OutlinedTextField(
                value = reward,
                onValueChange = { reward = it.filter { ch -> ch.isDigit() } },
                label = { Text("Reward (coins)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )

            // ▼ Dropdown for platform
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = platform,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Platform") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    platforms.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = {
                                platform = it
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = click,
                onValueChange = { click = it.filter { ch -> ch.isDigit() } },
                label = { Text("Number of clicks") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description (Optional)") }
            )

            Button(
                onClick = {

                    if (title.isBlank() || link.isBlank() || platform.isBlank() || reward.isBlank()) {
                        return@Button
                    }

                    viewModel.addTask(
                        title = title,
                        link = link,
                        platform = platform,
                        reward = reward.toInt(),
                        description = description,
                        click = click.toIntOrNull() ?: 0
                    ) { ok, err ->
                        if (ok) {
                            navController.popBackStack()
                        } else {
                            Log.e("ADD_TASK_ERROR", err ?: "Unknown error")
                        }
                    }

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Task")
            }
        }
    }
}
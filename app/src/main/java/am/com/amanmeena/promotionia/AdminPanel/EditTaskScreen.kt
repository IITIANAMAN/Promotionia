package am.com.amanmeena.promotionia.AdminPanel

import am.com.amanmeena.promotionia.Viewmodels.AdminViewModel
import am.com.amanmeena.promotionia.utils.TopAppBarPromotionia
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskScreen(
    taskId: String,
    navController: NavController,
    viewModel: AdminViewModel
) {
    val task = viewModel.tasks.find { it.id == taskId }

    var title by remember { mutableStateOf(task?.title ?: "") }
    var link by remember { mutableStateOf(task?.link ?: "") }
    var reward by remember { mutableStateOf(task?.reward?.toString() ?: "0") }
    var platform by remember { mutableStateOf(task?.platform ?: "") }
    var isActive by remember { mutableStateOf(task?.isActive ?: true) }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var click by remember { mutableStateOf(task?.click?.toString() ?: "0") }

    val platforms = listOf("Instagram", "Facebook", "X")
    var expanded by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBarPromotionia(
                modifier = Modifier,
                route = "Edit Task",
                navController = navController
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(scrollState),   // <-- Scrollable!!
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // TITLE
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            // LINK
            OutlinedTextField(
                value = link,
                onValueChange = { link = it },
                label = { Text("Link") },
                modifier = Modifier.fillMaxWidth()
            )

            // REWARD
            OutlinedTextField(
                value = reward,
                onValueChange = { reward = it.filter(Char::isDigit) },
                label = { Text("Reward (coins)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // PLATFORM DROPDOWN
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
                    platforms.forEach { p ->
                        DropdownMenuItem(
                            text = { Text(p) },
                            onClick = {
                                platform = p
                                expanded = false
                            }
                        )
                    }
                }
            }

            // CLICK LIMIT
            OutlinedTextField(
                value = click,
                onValueChange = { click = it.filter(Char::isDigit) },
                label = { Text("Number of clicks") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // DESCRIPTION
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            // ACTIVE SWITCH
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Active")
                Switch(
                    checked = isActive,
                    onCheckedChange = { isActive = it }
                )
            }

            // UPDATE BUTTON
            Button(
                onClick = {
                    val updates = mapOf(
                        "title" to title,
                        "link" to link,
                        "platform" to platform,
                        "reward" to (reward.toIntOrNull() ?: 0),
                        "isActive" to isActive,
                        "description" to description,
                        "click" to (click.toIntOrNull() ?: 0)
                    )

                    viewModel.updateTask(taskId, updates) { ok, _ ->
                        if (ok) navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Check, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Update Task")
            }
        }
    }
}
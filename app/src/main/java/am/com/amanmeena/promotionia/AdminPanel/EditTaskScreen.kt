package am.com.amanmeena.promotionia.AdminPanel

import TopAppBarPromotionia
import am.com.amanmeena.promotionia.Viewmodels.AdminViewModel

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

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

    // 👇 States declared empty first
    var title by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var reward by remember { mutableStateOf("0") }
    var platform by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }
    var description by remember { mutableStateOf("") }
    var click by remember { mutableStateOf("0") }

    val context = LocalContext.current

    // 🟢 FILL DATA ONLY AFTER TASK IS LOADED
    LaunchedEffect(task) {
        if (task != null) {
            title = task.title
            link = task.link
            reward = task.reward.toString()
            platform = task.platform
            isActive = task.isActive
            description = task.description ?: ""
            click = task.click.toString()
        }
    }

    // 🟡 Task Not Loaded Yet → Show Loading
    if (task == null) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val scrollState = rememberScrollState()
    var expanded by remember { mutableStateOf(false) }
    val platforms = listOf("Instagram", "Facebook", "X")

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
                .verticalScroll(scrollState)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = link,
                onValueChange = { link = it },
                label = { Text("Link") },
                modifier = Modifier.fillMaxWidth()
            )

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
                    label = { Text("Platform") },
                    readOnly = true,
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

            OutlinedTextField(
                value = click,
                onValueChange = { click = it.filter(Char::isDigit) },
                label = { Text("Number of clicks") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

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
                        if (ok) {
                            Toast.makeText(context, "Task updated successfully", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Check, null)
                Spacer(Modifier.width(8.dp))
                Text("Update Task")
            }
        }
    }
}
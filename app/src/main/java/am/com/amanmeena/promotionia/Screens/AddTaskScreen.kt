package am.com.amanmeena.promotionia.Screens


import am.com.amanmeena.promotionia.Viewmodels.AdminViewModel
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController, viewModel: AdminViewModel) {
    var title by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var reward by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var description by remember { mutableStateOf("") }

    val platforms = listOf("Instagram", "Facebook", "X")

    Scaffold(topBar = { TopAppBar(title = { Text("Add Task") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                title,
                { title = it },
                Modifier.fillMaxWidth(),
                label = { Text("Title") })
            OutlinedTextField(
                link,
                { link = it },
                Modifier.fillMaxWidth(),
                label = { Text("Link") })
            OutlinedTextField(value = reward, onValueChange = { reward = it.filter { ch -> ch.isDigit() } }, label = { Text("Reward (coins)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number))
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(value = platform, onValueChange = {}, readOnly = true, label = { Text("Platform") }, modifier = Modifier.menuAnchor().fillMaxWidth())
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    platforms.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { platform = it; expanded = false })
                    }
                }
            }
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description (optional)") }, modifier = Modifier.fillMaxWidth(), maxLines = 4)

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                if (title.isBlank() || link.isBlank() || platform.isBlank() || reward.isBlank()) return@Button
                viewModel.addTask(title = title, link = link, platform = platform, reward = reward.toInt(), description = description) { ok, err ->
                    if (ok) {
                        navController.popBackStack()
                    } else {
                        // handle error - show snackbar in your app
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Task")
            }
        }
    }
}
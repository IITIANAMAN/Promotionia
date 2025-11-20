package am.com.amanmeena.promotionia.AdminPanel



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
fun EditTaskScreen(
    taskId: String,
    navController: NavController,
    viewModel: AdminViewModel
) {
    // find task from viewmodel list
    val task = viewModel.tasks.find { it.id == taskId }
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf(task?.title ?: "") }
    var link by remember { mutableStateOf(task?.link ?: "") }
    var reward by remember { mutableStateOf(task?.reward?.toString() ?: "0") }
    var platform by remember { mutableStateOf(task?.platform ?: "") }
    var isActive by remember { mutableStateOf(task?.isActive ?: true) }
    var description by remember { mutableStateOf(task?.description ?: "") }
    var click by remember { mutableStateOf(task?.click?.toString()?:"0") }
    val platforms = listOf("Instagram", "Facebook", "X")
    var expanded by remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("Edit Task") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = link, onValueChange = { link = it }, label = { Text("Link") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = reward, onValueChange = { reward = it.filter { ch -> ch.isDigit() } }, label = { Text("Reward (coins)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number))
            //OutlinedTextField(value = platform, onValueChange = { platform = it }, label = { Text("Platform") }, modifier = Modifier.fillMaxWidth())

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(value = platform, onValueChange = {}, readOnly = true, label = { Text("Platform") }, modifier = Modifier.menuAnchor().fillMaxWidth())
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    platforms.forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { platform = it; expanded = false })
                    }
                }
            }
            OutlinedTextField(value = click, onValueChange = { click = it.filter { ch -> ch.isDigit() } }, label = { Text("Number of clicks") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())

//            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//                Text("Active")
//                Switch(checked = isActive, onCheckedChange = { isActive = it })
//            }


            Button(onClick = {
                val updates = mapOf(
                    "title" to title,
                    "link" to link,
                    "platform" to platform,
                    "reward" to (if (reward.isBlank()) 0 else reward.toInt()),
                    "isActive" to isActive,
                    "description" to description,
                    "click" to click
                )
                viewModel.updateTask(taskId, updates) { ok, err ->
                    if (ok) navController.popBackStack()
                    else {
                        // show error
                    }
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Update Task")
            }
        }
    }
}
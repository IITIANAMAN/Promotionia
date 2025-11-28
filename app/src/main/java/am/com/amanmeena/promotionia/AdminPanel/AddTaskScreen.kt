package am.com.amanmeena.promotionia.AdminPanel


import am.com.amanmeena.promotionia.Viewmodels.AdminViewModel
import am.com.amanmeena.promotionia.utils.TopAppBarPromotionia
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


    val platforms = listOf("Instagram", "Facebook", "X")

        Scaffold (topBar = { TopAppBarPromotionia(modifier = Modifier,"Add task",navController) }){ it->
            Column(modifier = Modifier.padding(it).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                OutlinedTextField(value = click, onValueChange = { click = it.filter { ch -> ch.isDigit() } }, label = { Text("Number of clicks") }, modifier = Modifier.fillMaxWidth())

                Button(onClick = {
                    if (title.isBlank() || link.isBlank() || platform.isBlank() || reward.isBlank()) return@Button
                    viewModel.addTask(title = title, link = link, platform = platform, reward = reward.toInt(), description = description,click=click.toInt()) { ok, err ->
                        if (ok) {
                            navController.popBackStack()
                        } else {
                            Log.e("ADD_TASK_ERROR", err ?: "Unknown error")
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



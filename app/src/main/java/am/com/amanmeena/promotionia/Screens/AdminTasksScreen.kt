package am.com.amanmeena.promotionia.Screens



import am.com.amanmeena.promotionia.Viewmodels.AdminViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTasksScreen(navController: NavController, viewModel: AdminViewModel) {

    Scaffold(topBar = { TopAppBar(title = { Text("Tasks") }) }) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(viewModel.tasks) { task ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(6.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(task.title, style = MaterialTheme.typography.titleMedium)
                                Text("${task.platform} • ${task.reward} coins", style = MaterialTheme.typography.bodySmall)
                            }
                            Row {
                                IconButton(onClick = {
                                    // navigate to edit screen
                                    navController.navigate("admin_tasks/edit/${task.id}")
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                                IconButton(onClick = {
                                    viewModel.deleteTask(task.id) { ok, err ->
                                        // optionally show toast/snackbar (not included)
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
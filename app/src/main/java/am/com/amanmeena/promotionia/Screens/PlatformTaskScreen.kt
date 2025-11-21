package am.com.amanmeena.promotionia.Screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import am.com.amanmeena.promotionia.Viewmodels.MainViewModel
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PlatformTaskScreen(
    platform: String,
    accountHandle: String,
    navController: NavController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    var tasks by remember { mutableStateOf<List<Pair<String, Map<String, Any>>>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    val completedTasks = viewModel.completedTasksForScreen.value
    var disableUntil by remember { mutableStateOf(0L) }
    val scope = rememberCoroutineScope()

    // load completed tasks for this account
    LaunchedEffect(accountHandle, platform) {
        viewModel.listenCompletedTasksForAccount(platform, accountHandle)
    }

    // load tasks
    LaunchedEffect(platform) {
        FirebaseFirestore.getInstance()
            .collection("tasks")
            .whereEqualTo("platform", platform)
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snap, _ ->
                tasks = snap?.documents?.map {
                    it.id to (it.data ?: emptyMap())
                } ?: emptyList()
                loading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "$platform — $accountHandle",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        if (loading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        val visibleTasks = tasks.filter { (id, _) ->
            id !in completedTasks
        }

        if (visibleTasks.isEmpty()) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("No tasks available for this account")
            }
            return
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(visibleTasks) { (taskId, taskData) ->

                val disabled = System.currentTimeMillis() < disableUntil

                TaskItemCard(
                    taskId = taskId,
                    task = taskData,
                    disabled = disabled,
                    onStart = { reward ->

                        disableUntil = System.currentTimeMillis() + 2_000L

                        scope.launch {
                            delay(5_000L)
                            disableUntil = 0L
                        }

                        viewModel.markTaskCompletedForAccount(
                            platform = platform,
                            account = accountHandle,
                            taskId = taskId,
                            reward = reward.toLong()
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun TaskItemCard(
    taskId: String,
    task: Map<String, Any>,
    disabled: Boolean,
    onStart: (reward: Int) -> Unit
) {
    val context = LocalContext.current

    val title = task["title"]?.toString() ?: ""
    val desc = task["description"]?.toString() ?: ""
    val reward = (task["reward"] as? Long ?: 0L).toInt()
    val link = task["link"]?.toString() ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (disabled) 0.4f else 1f),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(title, style = MaterialTheme.typography.titleMedium)

            if (desc.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(desc, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(10.dp))
            Text("Reward: $reward coins", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    enabled = !disabled,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                        context.startActivity(intent)
                        onStart(reward)
                    }
                ) {
                    Icon(Icons.Default.OpenInNew, null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (disabled) "Please wait…" else "Start")
                }
            }
        }
    }
}
package am.com.amanmeena.promotionia.SocialMediaSections

import am.com.amanmeena.promotionia.Viewmodels.MainViewModel
import TopAppBarPromotionia
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GoogleMapReviewTask(
    navController: NavController,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var tasks by remember { mutableStateOf<List<Pair<String, Map<String, Any>>>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var disableUntil by remember { mutableStateOf(0L) }

    // load completed google review tasks
    LaunchedEffect(Unit) {
        viewModel.ensureGoogleMapStructureExists()
        viewModel.listenGoogleReviewCompletedTasks()
    }

    val completedTasks = viewModel.completedTasksForScreen.value

    // load tasks
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance()
            .collection("tasks")
            .whereEqualTo("platform", "GoogleReview")
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snap, _ ->
                tasks = snap?.documents?.map {
                    it.id to (it.data ?: emptyMap())
                } ?: emptyList()
                loading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBarPromotionia(
                modifier = Modifier,
                route = "Google Review Tasks",
                navController = navController
            )
        }
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text("Google Map Review Tasks", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            if (loading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
                return@Column
            }

            val visibleTasks = tasks.filter { (taskId, _) -> taskId !in completedTasks }

            if (visibleTasks.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("No Google review tasks available")
                }
                return@Column
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(visibleTasks) { (taskId, taskData) ->

                    val disabled = System.currentTimeMillis() < disableUntil

                    GoogleReviewTaskCard(
                        taskId = taskId,
                        task = taskData,
                        disabled = disabled,
                        onStart = { reward ->

                            disableUntil = System.currentTimeMillis() + 2000L

                            scope.launch {
                                delay(4000L)
                                disableUntil = 0L
                            }

                            // 1️⃣ Add coins + mark task as completed
                            viewModel.markTaskCompletedForAccount(
                                platform = "GoogleReview",
                                account = "global",
                                taskId = taskId,
                                reward = reward.toLong()
                            )

                            // 2️⃣ Add reward history
                            viewModel.saveRewardHistory(taskId, reward.toLong())
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun GoogleReviewTaskCard(
    taskId: String,
    task: Map<String, Any>,
    disabled: Boolean,
    onStart: (reward: Int) -> Unit
) {
    val context = LocalContext.current

    val title = task["title"]?.toString() ?: ""
    val desc = task["description"]?.toString()
        ?: "Open the location and submit review on Google Maps."
    val reward = (task["reward"] as? Long ?: 0L).toInt()
    val link = task["link"]?.toString() ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (disabled) 0.4f else 1f),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.RateReview, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.height(8.dp))
            Text(desc, style = MaterialTheme.typography.bodySmall)

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
                        onStart(reward)  // ← Immediately triggers remove + reward

                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                        context.startActivity(intent)
                    }
                ) {
                    Icon(Icons.Default.OpenInNew, null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (disabled) "Please wait…" else "Open Map")
                }
            }
        }
    }
}
package am.com.amanmeena.promotionia.AdminPanel.util

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

data class RewardHistoryItem(
    val platform: String,
    val account: String,
    val title: String,
    val reward: Int,
    val time: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewardHistoryScreen(uid: String) {
    var history by remember { mutableStateOf<List<RewardHistoryItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(uid) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { snap ->
                val completedTasks = snap.get("completedTasks") as? Map<*, *> ?: emptyMap<Any, Any>()
                val temp = mutableListOf<RewardHistoryItem>()

                completedTasks.forEach { (platformKey, accountsMap) ->

                    val platform = platformKey.toString()
                    val accounts = accountsMap as? Map<*, *> ?: return@forEach

                    accounts.forEach { (accountKey, taskListObj) ->

                        val account = accountKey.toString()
                        val taskList = taskListObj as? List<*> ?: emptyList<Any>()

                        taskList.forEach { itemObj ->
                            val item = itemObj as? Map<*, *> ?: return@forEach

                            val taskId = item["taskId"]?.toString() ?: return@forEach
                            val reward = (item["reward"] as? Long ?: 0L).toInt()
                            val time = item["time"] as? Long ?: 0L

                            // fetch task title from Firestore
                            db.collection("tasks").document(taskId).get()
                                .addOnSuccessListener { taskSnap ->
                                    val title = taskSnap.getString("title") ?: "Unknown Task"

                                    temp.add(
                                        RewardHistoryItem(
                                            platform = platform,
                                            account = account,
                                            title = title,
                                            reward = reward,
                                            time = time
                                        )
                                    )

                                    history = temp.sortedByDescending { it.time } // sort by newest
                                    loading = false
                                }
                        }
                    }
                }
            }
    }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reward History") }
            )
        }
    ) { padding ->

        if (loading) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (history.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No reward history available")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(history) { item ->
                RewardHistoryCard(item)
            }
        }
    }
}

@Composable
fun RewardHistoryCard(item: RewardHistoryItem) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(item.time))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(6.dp))

            Text("Platform: ${item.platform}")
            Text("Account: ${item.account}")

            Spacer(Modifier.height(6.dp))

            Text("Reward Earned: ${item.reward} coins", fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(6.dp))

            Text("Completed on: $formattedDate")
        }
    }
}
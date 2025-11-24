package am.com.amanmeena.promotionia.Viewmodels

import PersonData
import am.com.amanmeena.promotionia.Data.TaskItem
import am.com.amanmeena.promotionia.Data.Values.ADMIN_UID
import android.R
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.util.CollectionUtils.mapOf
import kotlin.collections.mapOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await



class AdminViewModel(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {
    val pendingWithdrawals = mutableStateOf(0)
    // Tasks
    val tasks = mutableStateListOf<TaskItem>()
    val isLoading = mutableStateOf(false)
    val totalTasks = mutableStateOf(0)
    val totalUsers = mutableStateOf(0)
    val totalCoinsDistributed = mutableStateOf(0L)
    val errorMessage = mutableStateOf<String?>(null)
    val totalSocialMediaAccount = mutableStateOf(0)
    val totalFBAccount = mutableStateOf(0L)
    val totalInstaAccount = mutableStateOf(0L)
    val totalLinkedInAccount = mutableStateOf(0L)
    val totalXAccount = mutableStateOf(0L)
    val totalYoutubeAccount = mutableStateOf(0L)

    // Users
    val users = mutableStateListOf<PersonData>()
    val statesList = mutableStateListOf<String>()
    var stateDropdownExpanded = mutableStateOf(false)

    private var tasksListener: com.google.firebase.firestore.ListenerRegistration? = null
    private var usersListener: com.google.firebase.firestore.ListenerRegistration? = null

    fun startAdminRealtime() {
        listenUsersRealtime()
        listenTasksRealtime()
        loadStatsOnce()
    }
    val pendingSocialRequests = mutableStateOf(0)
    // Count of number of request
    fun listenPendingSocialRequests() {
        FirebaseFirestore.getInstance()
            .collection("requests")
            .whereEqualTo("isAccepted", false)
            .addSnapshotListener { snap, _ ->
                pendingSocialRequests.value = snap?.size() ?: 0
            }
    }
    // Tasks realtime

    fun listenTasksRealtime() {
        tasksListener?.remove()
        tasks.clear()
        tasksListener = firestore.collection("tasks")
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    errorMessage.value = e.message
                    return@addSnapshotListener
                }
                tasks.clear()
                snap?.documents?.forEach { doc ->
                    val d = doc.data ?: return@forEach
                    tasks.add(
                        TaskItem(
                            id = doc.id,
                            title = d["title"] as? String ?: "",
                            link = d["link"] as? String ?: "",
                            platform = d["platform"] as? String ?: "",
                            reward = (d["reward"] as? Long ?: 0L).toInt(),
                            isActive = d["isActive"] as? Boolean ?: true,
                            description = d["description"] as? String
                        )
                    )
                }
                totalTasks.value = tasks.size
            }
    }

    // Users realtime (admin excluded)
    fun listenUsersRealtime() {
        usersListener?.remove()
        users.clear()
        statesList.clear()
        usersListener = firestore.collection("users")
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    errorMessage.value = e.message
                    return@addSnapshotListener
                }

                users.clear()
                statesList.clear()
                snap?.documents?.forEach { doc ->
                    val uid = doc.id
                    if (uid == ADMIN_UID) return@forEach
                    try {
                        val name = doc.getString("name") ?: ""
                        val email = doc.getString("email") ?: ""
                        val number = doc.getString("number") ?: ""
                        val state = doc.getString("state") ?: ""
                        val totalCoin = (doc.getLong("totalCoin") ?: 0L).toInt()
                        val totalCoinFb = (doc.getLong("totalCoinFb") ?: 0L).toInt()
                        val totalCoinInsta = (doc.getLong("totalCoinInsta") ?: 0L).toInt()
                        val totalCoinX = (doc.getLong("totalCoinX") ?: 0L).toInt()

                        val p = PersonData(
                            name = name,
                            email = email,
                            number = number,
                            state = state,
                            accountFB = listOf(),
                            accountInsta = listOf(),
                            accountX = listOf(),
                            totalCoin = totalCoin,
                            totalCoinFb = totalCoinFb,
                            totalCoinInsta = totalCoinInsta,
                            totalCoinX = totalCoinX
                        )
                        users.add(p)
                        if (state.isNotBlank() && !statesList.contains(state)) {
                            statesList.add(state)
                        }
                    } catch (ex: Exception) {

                    }
                }
                totalUsers.value = users.size
            }
    }

    // load stats once (coins sum)
    fun loadStatsOnce() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val usersSnap = firestore.collection("users").get().await()
                totalUsers.value = usersSnap.size()

                var sum = 0L
                var fb = 0L
                var insta = 0L
                var x = 0L

                usersSnap.documents.forEach { d ->
                    val coin = d.getLong("totalCoin") ?: 0L
                    sum += coin
                    val fbList = d.get("accountFB") as? List<String> ?: emptyList()
                    val InstaList = d.get("accountInsta") as? List<String> ?: emptyList()
                    val XList = d.get("accountX") as? List<String> ?: emptyList()
                    fb += fbList.size
                    insta += InstaList.size
                    x += XList.size

                }

                totalFBAccount.value = fb
                totalXAccount.value = x
                totalInstaAccount.value = insta
                totalCoinsDistributed.value = sum
                totalTasks.value = firestore.collection("tasks").get().await().size()

            } catch (e: Exception) {
                errorMessage.value = e.message
            } finally {
                isLoading.value = false
            }
        }
    }

    // Add task (admin only)
    fun addTask(
        title: String,
        link: String,
        platform: String,
        reward: Int,
        description: String?,
        click: Int,
        onDone: (Boolean, String?) -> Unit
    ) {
        val uid = auth.currentUser?.uid
        if (uid != ADMIN_UID) {
            onDone(false, "Not authorized")
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            try {
                val data = mapOf(
                    "title" to title,
                    "link" to link,
                    "platform" to platform,
                    "reward" to reward,
                    "isActive" to true,
                    ("description" to description)
                )
                firestore.collection("tasks").add(data).await()
                onDone(true, null)
            } catch (e: Exception) {
                onDone(false, e.message)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateTask(taskId: String, updates: Map<String, Any>, onDone: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid != ADMIN_UID) {
            onDone(false, "Not authorized")
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            try {
                firestore.collection("tasks").document(taskId).update(updates).await()
                onDone(true, null)
            } catch (e: Exception) {
                onDone(false, e.message)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteTask(taskId: String, onDone: (Boolean, String?) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid != ADMIN_UID) {
            onDone(false, "Not authorized")
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            try {
                firestore.collection("tasks").document(taskId).delete().await()
                onDone(true, null)
            } catch (e: Exception) {
                onDone(false, e.message)
            } finally {
                isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        tasksListener?.remove()
        usersListener?.remove()
    }
}
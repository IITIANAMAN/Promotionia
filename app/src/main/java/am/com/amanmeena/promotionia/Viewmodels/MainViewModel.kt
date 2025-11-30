package am.com.amanmeena.promotionia.Viewmodels

import am.com.amanmeena.promotionia.Data.Values.ADMIN_UID
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class MainViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val userDeletedState = mutableStateOf(false)

    // Accounts
    val accountsFacebook = mutableStateListOf<String>()
    val accountsInstagram = mutableStateListOf<String>()
    val accountsX = mutableStateListOf<String>()

    // Completed tasks for the UI screen
    val completedTasksForScreen = mutableStateOf<List<String>>(emptyList())

    // Full user data
    val userData = mutableStateOf<Map<String, Any>?>(null)

    private var userDocListener: ListenerRegistration? = null
    var currentUid: String? = null

    init {
        // Auto detect logged-in user
        auth.currentUser?.uid?.let { uid ->
            reInitForNewUser(uid)
        }

        // Detect login/logout
        auth.addAuthStateListener { fa ->
            val uid = fa.currentUser?.uid
            if (uid != null) {
                reInitForNewUser(uid)
            } else {
                userDocListener?.remove()
                clearLocalData()
            }
        }
    }

    //-----------------------------------------------------
    // USER INIT
    //-----------------------------------------------------
    fun reInitForNewUser(uid: String) {
        if (currentUid == uid) return
        currentUid = uid
        clearLocalData()
        ensureGoogleCoinFieldExists()
        startUserListeners(uid)
    }

    private fun clearLocalData() {
        accountsFacebook.clear()
        accountsInstagram.clear()
        accountsX.clear()
        completedTasksForScreen.value = emptyList()
        userData.value = null
    }
    fun ensureGoogleCoinFieldExists() {
        val uid = currentUid ?: return
        val userRef = firestore.collection("users").document(uid)

        val defaults = mapOf(
            "totalCoinMap" to 0L,
            "completedTasks" to mapOf(
                "GoogleReview" to mapOf(
                    "global" to emptyList<String>()
                )
            )
        )

        userRef.set(defaults, SetOptions.merge())
    }

    //-----------------------------------------------------
    // FCM TOKEN
    //-----------------------------------------------------
    fun saveFcmToken() {
        val uid = currentUid ?: return
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            if (token.isNullOrBlank()) return@addOnSuccessListener
            firestore.collection("users").document(uid)
                .set(mapOf("fcmToken" to token), SetOptions.merge())
        }
    }

    //-----------------------------------------------------
    // User Document Listener
    //-----------------------------------------------------
    private fun startUserListeners(uid: String) {
        ensureUserDocExists(uid) {
            attachUserSnapshotListener(uid)
        }
    }

    private fun ensureUserDocExists(uid: String, onDone: () -> Unit) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { onDone() }
    }

    private fun attachUserSnapshotListener(uid: String) {
        userDocListener?.remove()

        userDocListener = firestore.collection("users")
            .document(uid)
            .addSnapshotListener { snap, error ->
                if (error != null) return@addSnapshotListener

                if (snap == null || !snap.exists()) {
                    FirebaseAuth.getInstance().signOut()
                    clearLocalData()
                    userDeletedState.value = true
                    userDocListener?.remove()
                    return@addSnapshotListener
                }

                userData.value = snap.data

                accountsFacebook.apply {
                    clear()
                    addAll((snap.get("accountFB") as? List<String>) ?: emptyList())
                }

                accountsInstagram.apply {
                    clear()
                    addAll((snap.get("accountInsta") as? List<String>) ?: emptyList())
                }

                accountsX.apply {
                    clear()
                    addAll((snap.get("accountX") as? List<String>) ?: emptyList())
                }

                if (userDeletedState.value) userDeletedState.value = false
            }
    }

    //-----------------------------------------------------
    // ACCOUNT REMOVE
    //-----------------------------------------------------
    fun removeAccount(platform: String, rawItem: String) {
        val uid = currentUid ?: return
        val field = when (platform) {
            "Facebook" -> "accountFB"
            "Instagram" -> "accountInsta"
            else -> "accountX"
        }

        firestore.collection("users").document(uid)
            .update(field, FieldValue.arrayRemove(rawItem))
    }

    //-----------------------------------------------------
    // SAVE REWARD HISTORY
    //-----------------------------------------------------
    fun saveRewardHistory(taskId: String, reward: Long) {
        val uid = currentUid ?: return

        val entry = mapOf(
            "uid" to uid,
            "taskId" to taskId,
            "reward" to reward,
            "time" to System.currentTimeMillis()
        )

        firestore.collection("reward_history").add(entry)
    }

    //-----------------------------------------------------
    // MARK TASK COMPLETED
    //-----------------------------------------------------
    fun markTaskCompletedForAccount(platform: String, account: String, taskId: String, reward: Long) {
        val uid = currentUid ?: return
        val userRef = firestore.collection("users").document(uid)

        val coinField = when (platform) {
            "Facebook" -> "totalCoinFb"
            "Instagram" -> "totalCoinInsta"
            "GoogleReview" -> "totalCoinMap"
            else -> "totalCoinX"
        }

        val fullPath = "completedTasks.$platform.$account"

        val updates = mapOf(
            "totalCoin" to FieldValue.increment(reward),
            coinField to FieldValue.increment(reward),
            fullPath to FieldValue.arrayUnion(taskId)
        )

        userRef.update(updates)
            .addOnFailureListener {
                // fallback if the map does not exist
                userRef.set(
                    mapOf(
                        "completedTasks" to mapOf(
                            platform to mapOf(
                                account to listOf(taskId)
                            )
                        )
                    ),
                    SetOptions.merge()
                )
            }
    }
    fun ensureGoogleMapStructureExists() {
        val uid = currentUid ?: return
        val userRef = firestore.collection("users").document(uid)

        val defaultMap = mapOf(
            "completedTasks" to mapOf(
                "GoogleReview" to mapOf(
                    "global" to emptyList<String>()
                )
            )
        )

        userRef.set(defaultMap, SetOptions.merge())
    }
    fun currentUserToken(): String? {
        return userData.value?.get("fcmToken") as? String
    }

    //-----------------------------------------------------
    // LISTEN COMPLETED TASKS (Social Media)
    //-----------------------------------------------------
    fun listenCompletedTasksForAccount(platform: String, account: String) {
        val uid = currentUid ?: return
        firestore.collection("users").document(uid)
            .addSnapshotListener { snap, _ ->
                val list =
                    (snap?.get("completedTasks.$platform.$account") as? List<String>) ?: emptyList()
                completedTasksForScreen.value = list
            }
    }

    //-----------------------------------------------------
    // LISTEN COMPLETED GOOGLE REVIEW TASKS
    //-----------------------------------------------------
    fun listenGoogleReviewCompletedTasks() {
        val uid = currentUid ?: return
        firestore.collection("users").document(uid)
            .addSnapshotListener { snap, _ ->
                val list =
                    (snap?.get("completedTasks.GoogleReview.global") as? List<String>)
                        ?: emptyList()
                completedTasksForScreen.value = list
            }
    }

    //-----------------------------------------------------
    // FOLLOW TASK REWARD
    //-----------------------------------------------------
    fun markFollowTaskDone(taskKey: String, reward: Long = 20) {
        val uid = currentUid ?: return
        val userRef = firestore.collection("users").document(uid)

        firestore.runTransaction { tr ->
            val snap = tr.get(userRef)

            val oldMap = snap.get("followTasks") as? Map<String, Boolean> ?: emptyMap()
            val newMap = oldMap.toMutableMap()
            newMap[taskKey] = true

            val allDone = (newMap.values.size == 4 && newMap.values.all { it })
            val rewardGiven = snap.getBoolean("followRewardGiven") ?: false

            if (allDone && !rewardGiven) {
                tr.update(
                    userRef,
                    "totalCoin", FieldValue.increment(reward),
                    "followRewardGiven", true
                )
            }

            tr.update(userRef, "followTasks", newMap)
        }
    }

    override fun onCleared() {
        userDocListener?.remove()
        super.onCleared()
    }
}
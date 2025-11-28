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

    companion object {
        private const val TAG = "APP_VM"
        private const val FIRE_TAG = "APP_FIRE"
    }
    val userDeletedState = mutableStateOf(false)
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Account lists
    val accountsFacebook = mutableStateListOf<String>()
    val accountsInstagram = mutableStateListOf<String>()
    val accountsX = mutableStateListOf<String>()

    // Completed tasks (per account)
    val completedTasksForScreen = mutableStateOf<List<String>>(emptyList())

    // Full user data
    val userData = mutableStateOf<Map<String, Any>?>(null)

    private var userDocListener: ListenerRegistration? = null
    var currentUid: String? = null

    fun reInitForNewUser(uid: String) {
        if (currentUid == uid) return // already same user

        currentUid = uid
        clearLocalData()
        startUserListeners(uid)
    }

    fun saveFcmToken() {
        val uid = currentUid ?: return
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                if (token.isNullOrBlank()) return@addOnSuccessListener
                val userRef = firestore.collection("users").document(uid)
                userRef.update("fcmToken", token)
                    .addOnSuccessListener { /* ok */ }
                    .addOnFailureListener { e ->
                        userRef.set(mapOf("fcmToken" to token), SetOptions.merge())
                    }
            }
    }
    fun currentUserToken(): String? {
        return userData.value?.get("fcmToken") as? String
    }
    // Clear old user cache
    private fun clearLocalData() {
        accountsFacebook.clear()
        accountsInstagram.clear()
        accountsX.clear()
        completedTasksForScreen.value = emptyList()
        userData.value = null
    }

    init {
        // Initial auto-detection (important for app restart)
        auth.currentUser?.uid?.let {
            Log.d(TAG, "App started with logged-in UID = $it")
            reInitForNewUser(it)
        }

        // Detect login/logout events
        auth.addAuthStateListener { firebaseAuth ->
            val newUid = firebaseAuth.currentUser?.uid
            if (newUid != null) {
                reInitForNewUser(newUid)
            } else {
                // user logged out → remove listener + clear data
                userDocListener?.remove()
                clearLocalData()
            }
        }
    }

    // -------- Start Firestore user listener --------
    private fun startUserListeners(uid: String) {
        userDocListener?.remove()

        ensureUserDocExists(uid) {
            attachUserSnapshotListener(uid)
        }
    }

    private fun ensureUserDocExists(uid: String, onDone: () -> Unit) {
        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { snap ->
                // If doc exists → OK
                // If doc doesn't exist → DO NOTHING (AuthClient will create)
                onDone()
            }
    }

    private fun attachUserSnapshotListener(uid: String) {
        userDocListener?.remove()

        userDocListener = firestore.collection("users")
            .document(uid)
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    // you can log or handle error
                    return@addSnapshotListener
                }

                // If document doesn't exist => admin deleted it
                if (snap == null || !snap.exists()) {
                    // Sign out user and flag deletion for UI
                    try {
                        FirebaseAuth.getInstance().signOut()
                    } catch (ex: Exception) {
                        // ignore
                    }

                    // Clear local cached data
                    clearLocalData()
                    userDeletedState.value = true

                    // remove listener to avoid repeated triggers
                    userDocListener?.remove()
                    userDocListener = null
                    return@addSnapshotListener
                }

                // Normal behaviour when doc exists
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

                // reset deletion flag if it was set previously and doc re-created
                if (userDeletedState.value) userDeletedState.value = false
            }
    }

    // -------- Add / Remove account ----------
    fun addAccount(platform: String, name: String, link: String) {
        val uid = currentUid ?: return

        val field = when (platform) {
            "Facebook" -> "accountFB"
            "Instagram" -> "accountInsta"
            else -> "accountX"
        }

        val item = "$name|$link"

        firestore.collection("users").document(uid)
            .update(field, FieldValue.arrayUnion(item))
    }

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

    // -------- Task completion ----------
    fun markTaskCompletedForAccount(platform: String, account: String, taskId: String, reward: Long) {
        val uid = currentUid ?: return

        val coinField = when (platform) {
            "Facebook" -> "totalCoinFb"
            "Instagram" -> "totalCoinInsta"
            else -> "totalCoinX"
        }

        val updates = mapOf(
            "totalCoin" to FieldValue.increment(reward),
            coinField to FieldValue.increment(reward),
            "completedTasks.$platform.$account" to FieldValue.arrayUnion(taskId)
        )

        firestore.collection("users").document(uid).update(updates)
    }

    // -------- Listen completed tasks for UI ----------
    fun listenCompletedTasksForAccount(platform: String, account: String) {
        val uid = currentUid ?: return
        firestore.collection("users").document(uid)
            .addSnapshotListener { snap, _ ->
                val list = (snap?.get("completedTasks.$platform.$account") as? List<String>) ?: emptyList()
                completedTasksForScreen.value = list
            }
    }

    override fun onCleared() {
        userDocListener?.remove()
        super.onCleared()
    }
    fun markFollowTaskDone(taskKey: String, reward: Long = 20) {
        val uid = currentUid ?: return
        val userRef = firestore.collection("users").document(uid)

        firestore.runTransaction { tr ->
            val snap = tr.get(userRef)

            // read old map or empty
            val followMap = snap.get("followTasks") as? Map<String, Boolean> ?: emptyMap()
            val updatedMap = followMap.toMutableMap()
            updatedMap[taskKey] = true

            // check if all tasks completed
            val allDone = updatedMap.values.size == 4 && updatedMap.values.all { it }

            val rewardGiven = snap.getBoolean("followRewardGiven") ?: false

            if (allDone && !rewardGiven) {
                tr.update(userRef, mapOf(
                    "totalCoin" to FieldValue.increment(reward),
                    "followRewardGiven" to true
                ))
            }

            // always save progress
            tr.update(userRef, "followTasks", updatedMap)
        }
    }


}
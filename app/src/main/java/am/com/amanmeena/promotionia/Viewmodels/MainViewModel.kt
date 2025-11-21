package am.com.amanmeena.promotionia.Viewmodels

import am.com.amanmeena.promotionia.Data.Values.ADMIN_UID
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class MainViewModel : ViewModel() {

    companion object {
        private const val TAG = "APP_VM"
        private const val FIRE_TAG = "APP_FIRE"
    }

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

    // ---------------------------
    // FIX: Call this after LOGIN
    // ---------------------------
    fun reInitForNewUser(uid: String) {
        if (currentUid == uid) return // already same user

        Log.d(TAG, "Reinitializing ViewModel for new UID = $uid")

        currentUid = uid
        clearLocalData()
        startUserListeners(uid)
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
        val docRef = firestore.collection("users").document(uid)

        docRef.get().addOnSuccessListener { snap ->
            if (snap.exists()) {
                onDone()
            } else {
                // Create new user doc
                val default = mapOf(
                    "uid" to uid,
                    "name" to "",
                    "email" to auth.currentUser?.email.orEmpty(),
                    "number" to "",
                    "state" to "",
                    "accountFB" to listOf<String>(),
                    "accountInsta" to listOf<String>(),
                    "accountX" to listOf<String>(),
                    "totalCoin" to 0,
                    "totalCoinFb" to 0,
                    "totalCoinInsta" to 0,
                    "totalCoinX" to 0,
                    "completedTasks" to mapOf<String, Any>(),
                    "createdAt" to System.currentTimeMillis()
                )

                docRef.set(default).addOnSuccessListener { onDone() }
            }
        }
    }

    private fun attachUserSnapshotListener(uid: String) {
        userDocListener?.remove()

        userDocListener = firestore.collection("users")
            .document(uid)
            .addSnapshotListener { snap, error ->
                if (error != null || snap == null) return@addSnapshotListener

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
    fun sendAccountRequest(platform: String, name: String, link: String) {
        val uid = auth.currentUser?.uid ?: return

        val data = mapOf(
            "uid" to uid,
            "platform" to platform,
            "accountHandel" to name,
            "accountLink" to link,
            "isAccepted" to false,
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("requests")
            .add(data)
            .addOnSuccessListener {
                Log.d("REQ", "Request sent")
            }
            .addOnFailureListener {
                Log.e("REQ", "Failed: ${it.message}")
            }
    }

}
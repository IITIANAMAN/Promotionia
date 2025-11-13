package am.com.amanmeena.promotionia.Viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    var userData = mutableStateOf<Map<String, Any>?>(null)
        private set

    fun loadCurrentUser() {
        val uid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    userData.value = doc.data
                }
                .addOnFailureListener {
                    userData.value = null
                }
        }
    }
}
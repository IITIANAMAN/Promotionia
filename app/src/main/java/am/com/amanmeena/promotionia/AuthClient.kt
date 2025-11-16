package am.com.amanmeena.promotionia

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthClient {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    suspend fun signUp(
        name: String,
        email: String,
        password: String,
        number: String,
        state: String
    ): Result<Boolean> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("Invalid UID"))

            val userData = mapOf(
                "uid" to uid,
                "name" to name,
                "email" to email,
                "number" to number,
                "state" to state,
                "accountFB" to emptyList<String>(),
                "accountInsta" to emptyList<String>(),
                "accountX" to emptyList<String>(),
                "totalCoin" to 0,
                "totalCoinFb" to 0,
                "totalCoinInsta" to 0,
                "totalCoinX" to 0,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("users").document(uid).set(userData).await()

            Result.success(true)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // ---------------------------
    // LOGIN (Email + Password)
    // ---------------------------
    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // ---------------------------
    // LOAD USER PROFILE
    // ---------------------------
    suspend fun loadUser(uid: String): Result<Map<String, Any>?> {
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            Result.success(snapshot.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    // ---------------------------
    // LOGOUT
    // ---------------------------
    fun logout() {
        auth.signOut()
    }


    // ---------------------------
    // CURRENT USER
    // ---------------------------
    fun currentUser() = auth.currentUser
}
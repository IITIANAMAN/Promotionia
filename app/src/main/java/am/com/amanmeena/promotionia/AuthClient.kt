package am.com.amanmeena.promotionia

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class AuthClient {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // -----------------------------------------
    // SIGNUP (Creates FULL Firestore user data)
    // -----------------------------------------
    suspend fun signUp(
        name: String,
        email: String,
        password: String,
        number: String,
        state: String
    ): Result<Boolean> {
        return try {

            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("User creation failed"))

            // FULL Firestore doc creation
            val userData = mapOf(
                "uid" to user.uid,
                "name" to name,
                "email" to email,
                "number" to number,
                "state" to state,
                "accountFB" to emptyList<String>(),
                "accountInsta" to emptyList<String>(),
                "accountX" to emptyList<String>(),
                "completedTasks" to emptyMap<String, Any>(),
                "rewardHistory" to emptyList<Map<String, Any>>(),   // ← ADD THIS
                "totalCoin" to 0,
                "totalCoinFb" to 0,
                "totalCoinInsta" to 0,
                "totalCoinX" to 0,
                "totalCoinMap" to 0,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(user.uid)
                .set(userData, SetOptions.merge())
                .await()

            user.sendEmailVerification().await()
            auth.signOut()

            Result.success(true)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------------------------------------------------
    // Create FULL doc (if login happens before signup finishes)
    // ---------------------------------------------------------
    private suspend fun createUserDocIfNotExists(
        uid: String,
        email: String
    ) {
        val doc = firestore.collection("users").document(uid).get().await()

        if (!doc.exists()) {

            val defaultData = mapOf(
                "uid" to uid,
                "email" to email,
                "name" to "",
                "number" to "",
                "state" to "",
                "accountFB" to emptyList<String>(),
                "accountInsta" to emptyList<String>(),
                "accountX" to emptyList<String>(),
                "completedTasks" to emptyMap<String, Any>(),
                "rewardHistory" to emptyList<Map<String, Any>>(),   // ← ADD THIS
                "totalCoin" to 0,
                "totalCoinFb" to 0,
                "totalCoinInsta" to 0,
                "totalCoinX" to 0,
                "totalCoinMap" to 0,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("users")
                .document(uid)
                .set(defaultData, SetOptions.merge())
                .await()
        }
    }

    // -----------------------------------------
    // LOGIN (make sure Firestore doc exists)
    // -----------------------------------------
    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("Login failed"))

            if (!user.isEmailVerified) {
                user.sendEmailVerification()
                auth.signOut()
                return Result.failure(Exception("Please verify your email first."))
            }

            // ensure Firestore doc exists PROPERLY
            createUserDocIfNotExists(user.uid, user.email ?: "")

            Result.success(true)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -----------------------------------------
    // RESEND EMAIL
    // -----------------------------------------
    suspend fun resendVerification(): Result<Boolean> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("Not logged in"))
            if (user.isEmailVerified)
                return Result.failure(Exception("Email already verified"))
            user.sendEmailVerification().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() = auth.signOut()
    fun currentUser(): FirebaseUser? = auth.currentUser
}
package am.com.amanmeena.promotionia

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthClient {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // SIGNUP
    suspend fun signUp(
        name: String,
        email: String,
        password: String,
        number: String,
        state: String
    ): Result<Boolean> {
        return try {

            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("User creation failed"))

            firebaseUser.sendEmailVerification().await()


            auth.signOut()

            Result.success(true)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun createUserDocIfNotExists(
        uid: String,
        name: String,
        email: String,
        number: String,
        state: String
    ) {
        val docRef = firestore.collection("users").document(uid)
        val doc = docRef.get().await()

        if (!doc.exists()) {
            val data = mapOf(
                "uid" to uid,
                "name" to name,
                "email" to email,
                "number" to number,
                "state" to state,
                "accountFB" to emptyList<String>(),
                "accountInsta" to emptyList<String>(),
                "accountX" to emptyList<String>(),
                "completedTasks" to emptyMap<String, Any>(),
                "totalCoin" to 0,
                "totalCoinFb" to 0,
                "totalCoinInsta" to 0,
                "totalCoinX" to 0,
                "createdAt" to System.currentTimeMillis()
            )

            docRef.set(data).await()
        }
    }

    // LOGIN — BLOCK UNVERIFIED USERS
    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: return Result.failure(Exception("Login failed"))

            if (!user.isEmailVerified) {
                user.sendEmailVerification()
                auth.signOut()
                return Result.failure(Exception("Please verify your email first."))
            }

            // 🔥 Create Firestore document now only once after verification
            createUserDocIfNotExists(
                uid = user.uid,
                name = user.displayName ?: "",
                email = user.email ?: "",
                number = "",
                state = ""
            )

            Result.success(true)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // RESEND VERIFICATION
    suspend fun resendVerification(): Result<Boolean> {
        val user = auth.currentUser ?: return Result.failure(Exception("Not logged in"))
        return try {
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
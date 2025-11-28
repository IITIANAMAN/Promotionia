package am.com.amanmeena.promotionia

import android.app.Application
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class PromotioniaApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        FirebaseFirestore.getInstance().firestoreSettings = settings

        Log.d("FIRESTORE_CACHE", "🔥 Firestore persistence enabled")
    }
}
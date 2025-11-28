package am.com.amanmeena.promotionia

import am.com.amanmeena.promotionia.Viewmodels.MainViewModel
import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import am.com.amanmeena.promotionia.ui.theme.PromotioniaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 🚀 Initialize Firebase
        FirebaseApp.initializeApp(this)

        // 🚀 Firestore offline persistence (works!)
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()

        // 🚀 Ask for notification permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                    // Optional: Handle result
                }

            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // 🚀 Compose UI
        setContent {

            val vm = viewModel<MainViewModel>(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return MainViewModel() as T
                    }
                }
            )

            PromotioniaTheme {
                MyAppNavigation(modifier = Modifier, sharedViewModel = vm)
            }
        }
    }
}
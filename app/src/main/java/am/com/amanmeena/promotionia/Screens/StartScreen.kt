package am.com.amanmeena.promotionia.Screens

import am.com.amanmeena.promotionia.Data.Values.ADMIN_UID
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable


fun StartScreen(navController: NavController) {

    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        if (user != null) {
            if (user.uid == ADMIN_UID) {
                navController.navigate("admin") {
                    popUpTo("start") { inclusive = true }
                }
            } else {
                navController.navigate("home") {
                    popUpTo("start") { inclusive = true }
                }
            }
        } else {
            navController.navigate("login") {
                popUpTo("start") { inclusive = true }
            }
        }
    }
}
package am.com.amanmeena.promotionia.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarPromotionia(modifier: Modifier = Modifier, route: String,navController: NavController) {
    TopAppBar(
        title = {
            Text(route)
            },
        navigationIcon = {
            if (route !in listOf("Login", "signup", "home", "admin", "verify_email")) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            }
        }
    )
}
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarPromotionia(
    modifier: Modifier = Modifier,
    route: String,
    navController: NavController
) {
    val isDark = isSystemInDarkTheme()

    // SMOOTH COLORS (Not glossy black, not bright white)
    val background = if (isDark) Color(0xFF121212) else Color(0xFFFFFFFF)
    val contentColor = if (isDark) Color(0xFFEAEAEA) else Color(0xFF1A1A1A)

    TopAppBar(
        title = {
            Text(
                text = route,
                color = contentColor
            )
        },
        navigationIcon = {
            if (route !in listOf("Login", "signup", "home", "admin", "verify_email")) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = contentColor
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = background,
            titleContentColor = contentColor,
            navigationIconContentColor = contentColor,
            actionIconContentColor = contentColor
        )
    )
}
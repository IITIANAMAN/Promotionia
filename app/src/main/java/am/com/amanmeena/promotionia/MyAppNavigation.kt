package am.com.amanmeena.promotionia

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.amanmeena.promotionia.Screens.HomeScreen
import com.amanmeena.promotionia.Screens.LeaderboardScreen

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController,"home") {
        composable ("home"){
            HomeScreen(modifier,navController)
        }
        composable("leader"){
            LeaderboardScreen(modifier,navController)
        }
    }
}



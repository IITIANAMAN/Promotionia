package am.com.amanmeena.promotionia

import am.com.amanmeena.promotionia.Screens.LoginScreen
import am.com.amanmeena.promotionia.Screens.SignUpScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amanmeena.promotionia.Screens.FacebookAccountsScreen
import com.amanmeena.promotionia.Screens.HomeScreen
import com.amanmeena.promotionia.Screens.LeaderboardScreen

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Scaffold(
        topBar = { PromotioniaTopAppBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AppNavGraph(navController = navController, modifier = modifier)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotioniaTopAppBar(navController: NavHostController) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    TopAppBar(
        title = {
            Text(
                text = when (currentRoute) {
                    "home" -> "Promotionia"
                    "leader" -> "Leaderboard"
                    "fb" -> "Facebook Accounts"
                    else -> "Promotionia"
                },
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            if (currentRoute != "home") {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        }
    )
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = "login") {
        composable("home") {
            HomeScreen(modifier = modifier, navController = navController)
        }
        composable("leader") {
            LeaderboardScreen(modifier = modifier, navController = navController)
        }
        composable("acc/{an}", arguments = listOf(navArgument("an"){
            type = NavType.StringType
        })) {
            val an = it.arguments?.getString("an")
            FacebookAccountsScreen(modifier = modifier,an)
        }
        composable("login") {
            LoginScreen(modifier,navController)
        }
        composable("signup") {
            SignUpScreen(modifier)
        }
    }
}
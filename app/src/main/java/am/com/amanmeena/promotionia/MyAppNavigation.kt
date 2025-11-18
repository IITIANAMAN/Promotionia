package am.com.amanmeena.promotionia

import am.com.amanmeena.promotionia.AdminPanel.AddTaskScreen
import am.com.amanmeena.promotionia.AdminPanel.AdminDashboardScreen
import am.com.amanmeena.promotionia.AdminPanel.AdminTasksScreen
import am.com.amanmeena.promotionia.AdminPanel.AdminUsersScreen
import am.com.amanmeena.promotionia.AdminPanel.EditTaskScreen
import am.com.amanmeena.promotionia.Screens.*
import am.com.amanmeena.promotionia.Viewmodels.AdminViewModel
import am.com.amanmeena.promotionia.Viewmodels.MainViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.amanmeena.promotionia.Screens.SocialMedia
import com.amanmeena.promotionia.Screens.HomeScreen
import com.amanmeena.promotionia.Screens.LeaderboardScreen

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val navController = rememberNavController()
    // SINGLE shared AdminViewModel for all admin screens
    val adminVm = remember { AdminViewModel() }

    Scaffold(
        topBar = { PromotioniaTopAppBar(navController) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            AppNavGraph(navController = navController, modifier = modifier, viewModel = viewModel, adminVm = adminVm)
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
            if (currentRoute != "login" && currentRoute != "signup") {
                Text(
                    text = when (currentRoute) {
                        "home" -> "Promotionia"
                        "leader" -> "Leaderboard"
                        "fb" -> "Facebook Accounts"
                        "admin_users"->"Manage User"
                        "admin_tasks/add" -> "Add task"
                        else -> "Promotionia"
                    },
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        navigationIcon = {
            if (currentRoute != "home" && currentRoute != "login" && currentRoute != "signup" && currentRoute != "admin") {
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
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    adminVm: AdminViewModel
) {
    NavHost(navController = navController, startDestination = "start") {
        composable("start") { StartScreen(navController) }
        composable("login") { LoginScreen(modifier = modifier, navController = navController) }
        composable("signup") { SignUpScreen(modifier = modifier, navController = navController) }
        composable("home") { HomeScreen(modifier = modifier, navController = navController, viewModel = viewModel) }
        composable("leader") { LeaderboardScreen(modifier = modifier, navController = navController) }
        composable("acc/{an}", arguments = listOf(navArgument("an") { type = NavType.StringType })) {
            val an = it.arguments?.getString("an")
            SocialMedia(modifier = modifier, an)
        }

        // ADMIN / dashboard routes - use shared adminVm
        composable("admin") {
            AdminDashboardScreen(modifier, navController = navController, viewModel = adminVm)
        }

        composable("admin_tasks") {
            AdminTasksScreen(navController = navController, viewModel = adminVm)
        }

        composable("admin_tasks/add") {
            AddTaskScreen(navController = navController, viewModel = adminVm)
        }

        composable("admin_tasks/edit/{taskId}", arguments = listOf(navArgument("taskId") { type = NavType.StringType })) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("taskId") ?: ""
            EditTaskScreen(taskId = id, navController = navController, viewModel = adminVm)
        }

        composable("admin_users") {
            AdminUsersScreen(navController = navController, viewModel = adminVm)
        }
    }
}
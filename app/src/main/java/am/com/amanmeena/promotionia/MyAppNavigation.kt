package am.com.amanmeena.promotionia

import am.com.amanmeena.promotionia.AdminPanel.AddTaskScreen
import am.com.amanmeena.promotionia.AdminPanel.AdminDashboardScreen
import am.com.amanmeena.promotionia.AdminPanel.AdminTasksScreen
import am.com.amanmeena.promotionia.AdminPanel.AdminUsersScreen
import am.com.amanmeena.promotionia.AdminPanel.EditTaskScreen
import am.com.amanmeena.promotionia.AdminPanel.SocialMediaApprovalScreen
import am.com.amanmeena.promotionia.AdminPanel.UserProfile
import am.com.amanmeena.promotionia.AdminPanel.util.RewardHistoryScreen
import am.com.amanmeena.promotionia.Data.Values.ADMIN_UID
import am.com.amanmeena.promotionia.Screens.*
import am.com.amanmeena.promotionia.SocialMediaSections.GoogleMapReviewTask
import am.com.amanmeena.promotionia.Viewmodels.AdminViewModel
import am.com.amanmeena.promotionia.Viewmodels.MainViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.amanmeena.promotionia.Screens.SocialMedia
import com.amanmeena.promotionia.Screens.HomeScreen

import com.amanmeena.promotionia.Screens.LeaderboardScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, sharedViewModel: MainViewModel) {

    val navController = rememberNavController()
    val adminVm = remember { AdminViewModel() }
    val mainVm = sharedViewModel
    // In order to load data to admin panel
    LaunchedEffect(Unit) {
        val uid = sharedViewModel.currentUid ?: FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null && uid == ADMIN_UID) {
            adminVm.startAdminRealtime()
        }
    }
//    Scaffold(
//        topBar = { PromotioniaTopAppBar(navController) }
//    ) { innerPadding ->
//        Box(modifier = Modifier.padding(innerPadding)) {

            AppNavGraph(
                navController = navController,
                modifier = modifier,
                viewModel = mainVm,
                adminVm = adminVm
            )
        }
//    }
//}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PromotioniaTopAppBar(navController: NavHostController) {
//
//    val backStack by navController.currentBackStackEntryAsState()
//    val route = backStack?.destination?.route ?: ""
//
//    TopAppBar(
//        title = {
//            if (route !in listOf("login", "signup", "verify_email","home")) {
//                Text(
//                    text = when {
//                        route.startsWith("acc/") -> "Social Media Accounts"
//                        route.startsWith("tasks/") -> "Tasks"
//                        route == "leader" -> "Leaderboard"
//                        route == "admin_users" -> "Manage Users"
//                        route == "admin_tasks/add" -> "Add Task"
//                        route == "admin_tasks" ->"Tasks"
//                        else -> "Promotionia"
//                    }
//                )
//            }
//        },
//        navigationIcon = {
//            if (route !in listOf("login", "signup", "home", "admin", "verify_email")) {
//                IconButton(onClick = { navController.popBackStack() }) {
//                    Icon(Icons.Default.ArrowBack, "Back")
//                }
//            }
//        }
//    )
//}

// -------------------- NAV GRAPH -----------------------
@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    adminVm: AdminViewModel
) {
    val userDeleted by viewModel.userDeletedState

    if (userDeleted) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Account Deleted") },
            text = { Text("Your account has been deleted by the Admin.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.userDeletedState.value = false
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
    NavHost(navController, startDestination = "start") {

        composable("start") { StartScreen(navController) }
        composable("login") { LoginScreen(modifier, navController,adminVm,viewModel) }
        composable("signup") { SignUpScreen(modifier, navController) }
        composable("verify_email") { VerifyEmailScreen(navController) }
        composable("admin_social_approval") {
            SocialMediaApprovalScreen(modifier,navController)
        }
        composable("home") {
            HomeScreen( navController, viewModel)
        }

        composable("leader") {
            LeaderboardScreen(modifier, navController)
        }

        composable("acc/{an}",
            arguments = listOf(navArgument("an") { type = NavType.StringType })
        ) {
            val an = it.arguments?.getString("an") ?: ""
            SocialMedia(modifier, an, viewModel, navController)
        }



        composable("admin") {
            AdminDashboardScreen(modifier, navController, adminVm)
        }

        composable("admin_tasks") {

            AdminTasksScreen(navController, adminVm)
        }

        composable("admin_tasks/add") {
            AddTaskScreen(navController, adminVm)
        }

        composable("admin_tasks/edit/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("taskId") ?: ""
            EditTaskScreen(id, navController, adminVm)
        }

        composable("admin_users") {
            AdminUsersScreen(navController, adminVm)
        }
        composable ("userprofile",){
            val data = adminVm.selectedUser
            UserProfile(data,navController)
        }


        composable(
            "tasks/{platform}/{account}",
            arguments = listOf(
                navArgument("platform") { type = NavType.StringType },
                navArgument("account") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val platform = backStackEntry.arguments?.getString("platform") ?: ""
            val account = backStackEntry.arguments?.getString("account") ?: ""

            // IMPORTANT FIX — USE SHARED MAIN VIEWMODEL
            PlatformTaskScreen(
                platform = platform,
                accountHandle = account,
                navController = navController,
                viewModel = viewModel
            )
        }
        composable("reward_history/{uid}") { backStack ->
            val uid = backStack.arguments?.getString("uid") ?: ""
            RewardHistoryScreen(uid)
        }
        composable("google_review_tasks") {
            GoogleMapReviewTask(navController, viewModel)
        }
        composable("about_app") {
            AboutApp(modifier,navController)
        }
        composable("user_reward_history") {
            UserRewardScreen(modifier)
        }
        composable("user_transaction_history") {
            UserTransactionScreen(modifier)
        }
        composable ("user_account_history"){
            UserAccountRequestHistory(modifier,navController)
        }
    }
}
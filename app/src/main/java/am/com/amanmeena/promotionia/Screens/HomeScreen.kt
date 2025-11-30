package com.amanmeena.promotionia.Screens

import am.com.amanmeena.promotionia.AuthClient
import am.com.amanmeena.promotionia.Components.UpdatesPager
import am.com.amanmeena.promotionia.R
import am.com.amanmeena.promotionia.Viewmodels.MainViewModel
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch


// --------------------------------------------------
// HOME SCREEN
// --------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()

    val userDeleted by viewModel.userDeletedState

    // If admin deleted the account
    if (userDeleted) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Account Deleted") },
            text = { Text("Your account has been deleted by the admin.") },
            confirmButton = {
                Button(onClick = {
                    viewModel.userDeletedState.value = false
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }) { Text("OK") }
            }
        )
    }

    PromoNavDrawer(
        navController = navController,
        viewModel = viewModel,
        drawerState = drawerState
    ) {

        Scaffold(
            topBar = {
                val isDark = isSystemInDarkTheme()

                TopAppBar(
                    title = {
                        Text(
                            "Promotionia",
                            color = if (isDark) Color.White else Color.Black
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = if (isDark) Color.White else Color.Black
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isDark) Color.Black else Color.White,
                        titleContentColor = if (isDark) Color.White else Color.Black,
                        navigationIconContentColor = if (isDark) Color.White else Color.Black
                    )
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                BorderedSection { ProfileSection(viewModel.userData.value) }

                BorderedSection { StatsSection(viewModel.userData.value) }

                UpdatesPager()

                BorderedSection { ManageAccountsSection(navController, viewModel) }

                BorderedSection { LeaderboardSection(navController) }

                BorderedSection { FollowSection(viewModel) }
            }
        }
    }
}


// --------------------------------------------------
// BORDERED SECTION (THEME AWARE)
// --------------------------------------------------

@Composable
fun BorderedSection(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        shape = RoundedCornerShape(16.dp),

        border = BorderStroke(
            1.dp,
            if (isDark) Color(0xFF2A2A2A) else Color(0xFFE0E0E0)
        ),

        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),

        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            content()

            Divider(
                color = if (isDark) Color(0xFF3A3A3A) else Color(0xFFF1F1F1),
                thickness = 0.8.dp
            )
        }
    }
}


// --------------------------------------------------
// DRAWER
// --------------------------------------------------

@Composable
fun PromoNavDrawer(
    navController: NavHostController,
    viewModel: MainViewModel,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    content: @Composable () -> Unit
) {

    val colors = MaterialTheme.colorScheme

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            ModalDrawerSheet(
                modifier = Modifier.width(260.dp),
                drawerContainerColor = colors.surface,
                drawerContentColor = colors.onSurface
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.`in`),
                        contentDescription = "Profile Logo",
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(colors.surfaceVariant)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Promotionia",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.onSurface
                    )
                }

                Divider(color = colors.outlineVariant)

                DrawerItem("About app", navController)
                DrawerItem("Contact Us", navController)
                DrawerItem("Logout", navController)
                DrawerItem("Reward History", navController)
                DrawerItem("Account request history", navController)
                DrawerItem("Withdrawal request", navController)
            }
        },
        content = content
    )
}


// --------------------------------------------------
// DRAWER ITEM
// --------------------------------------------------

@Composable
fun DrawerItem(text: String, navController: NavController) {

    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    Text(
        text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {

                when (text) {

                    "About app" -> {
                        navController.navigate("about_app")
                    }

                    "Logout" -> {
                        AuthClient().logout()
                        navController.navigate("login") { popUpTo(0) }
                    }

                    "Contact Us" -> {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:")
                            putExtra(Intent.EXTRA_EMAIL, arrayOf("promotionia.support@gmail.com"))
                            putExtra(Intent.EXTRA_SUBJECT, "Support Request – Promotionia")
                        }
                        context.startActivity(intent)
                    }

                    "Reward History" -> navController.navigate("user_reward_history")
                    "Withdrawal request" -> navController.navigate("user_transaction_history")
                    "Account request history" -> navController.navigate("user_account_history")
                }
            },
        color = colors.onSurface
    )
}
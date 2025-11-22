package com.amanmeena.promotionia.Screens

import am.com.amanmeena.promotionia.AuthClient
import am.com.amanmeena.promotionia.R
import am.com.amanmeena.promotionia.Viewmodels.MainViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun HomeScreen(
    navController: NavHostController,
    viewModel: MainViewModel
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    PromoNavDrawer(
        navController = navController,
        viewModel = viewModel,
        drawerState = drawerState
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Promotionia") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }   // ⭐ Opens same drawer
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
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

                BorderedSection { FollowSection() }


            }
        }
    }
}
@Composable
fun BorderedSection(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            content()
            Divider(color = Color(0xFFF1F1F1), thickness = 0.8.dp)
        }
    }
}
@Composable
fun PromoNavDrawer(
    navController: NavHostController,
    viewModel: MainViewModel,
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    content: @Composable () -> Unit
) {

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(260.dp)
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
                            .background(Color.LightGray, CircleShape)
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Promotionia", style = MaterialTheme.typography.titleMedium)
                }

                Divider()

                DrawerItem("Help", navController)
                DrawerItem("Contact Us", navController)
                DrawerItem("Logout", navController)
            }
        },
        content = content
    )
}

@Composable
fun DrawerItem(text: String,navController: NavController) {
    Text(
        text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                if(text == "help"){
                    navController.navigate("help")
                } else if(text == "Logout"){
                    AuthClient().logout()
                    navController.navigate("login") { popUpTo(0) }
                }else{
                    navController.navigate("Contact")
                }
                       },
        color = Color.Black
    )
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UpdatesPager() {

    val pages = listOf(
        R.drawable.sample1,
        R.drawable.sample2,
        R.drawable.sample3
    )

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)    // NO GAP
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)    // Set to your actual image height
        ) { page ->
            Image(
                painter = painterResource(id = pages[page]),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            repeat(pages.size) { index ->
                val color = if (pagerState.currentPage == index) Color.Black else Color.LightGray
                Box(
                    Modifier
                        .size(8.dp)
                        .padding(3.dp)
                        .background(color, CircleShape)
                )
            }
        }
    }
}
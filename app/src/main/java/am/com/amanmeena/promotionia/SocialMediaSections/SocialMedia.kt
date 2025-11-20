package com.amanmeena.promotionia.Screens

import am.com.amanmeena.promotionia.Viewmodels.MainViewModel
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialMedia(
    modifier: Modifier = Modifier,
    an: String = "Facebook",
    viewModel: MainViewModel,
    navController: NavController
) {
    // local input states
    var accountName by remember { mutableStateOf("") }
    var accountLink by remember { mutableStateOf("") }
    var isAdding by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // ensure accounts are loaded (if viewModel needs an explicit call)


    // choose the appropriate source list from ViewModel
    val accounts = when (an) {
        "Facebook" -> viewModel.accountsFacebook
        "Instagram" -> viewModel.accountsInstagram
        "X", "X (Twitter)", "Twitter", "X (twitter)" -> viewModel.accountsX
        else -> viewModel.accountsFacebook
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // header
        Text(
            text = "Manage your $an accounts",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(20.dp))

        // add card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(3.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FB))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "➕ Add New Account",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Add a new $an account to receive tasks",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = accountName,
                    onValueChange = { accountName = it },
                    label = { Text("Account Name (optional)") },
                    placeholder = { Text("e.g., @username or display name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = accountLink,
                    onValueChange = { accountLink = it },
                    label = { Text("Account Link / Handle") },
                    placeholder = { Text("") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            // validation
                            if (accountLink.isBlank()) return@Button
                            isAdding = true
                            // call VM to add
                            viewModel.addAccount(an, accountName.trim(), accountLink.trim())
                            accountName = ""
                            accountLink = ""
                            isAdding = false
                        },
                        enabled = !isAdding,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = if (isAdding) "Adding…" else "Add Account")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // accounts list header
        if (accounts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No $an accounts added yet. Add your first account above!",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            Text(
                text = "Added Accounts (${accounts.size})",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                accounts.forEach { raw ->
                    // stored format can be "name|link" or a plain string
                    val name: String
                    val link: String
                    if (raw.contains("|")) {
                        val parts = raw.split("|", limit = 2)
                        name = parts.getOrNull(0).orEmpty()
                        link = parts.getOrNull(1).orEmpty()
                    } else {
                        // attempt to heuristically decide
                        if (raw.startsWith("@") || raw.contains("http")) {
                            name = ""
                            link = raw
                        } else {
                            name = raw
                            link = ""
                        }
                    }

                    AccountItem(
                        name = name,
                        link = link,
                        platform = an,
                        onRemove = {
                            viewModel.removeAccount(an, raw)
                        },
                        navController
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun AccountItem(
    name: String,
    link: String,
    platform: String,
    onRemove: () -> Unit = {},
    navController: NavController
) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable{
                navController.navigate("tasks/${platform}/${Uri.encode(link)}")
            },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (name.isNotBlank()) {
                    Text(text = name, style = MaterialTheme.typography.titleSmall)
                }
                if (link.isNotBlank()) {
                    Text(text = link, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                }
            }

            // remove button (small)
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete,"Delete")
            }
        }
    }
}
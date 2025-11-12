package com.amanmeena.promotionia.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FacebookAccountsScreen(
    modifier: Modifier,
    an: String? ="Facebook"
) {
    var accountName by remember { mutableStateOf("") }
    var accountLink by remember { mutableStateOf("") }
    var accounts by remember { mutableStateOf(listOf<Pair<String, String>>()) }


        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // Page Header
            Text(
                text = "Manage your ${an} accounts",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Add New Account Section
            AddAccountSection(
                accountName = accountName,
                accountLink = accountLink,
                onNameChange = { accountName = it },
                onLinkChange = { accountLink = it },
                onAddClick = {
                    if (accountName.isNotBlank() && accountLink.isNotBlank()) {
                        accounts = accounts + (accountName to accountLink)
                        accountName = ""
                        accountLink = ""
                    }
                },
                an
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Display Added Accounts
            if (accounts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No ${an} accounts added yet. Add your first account above!",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Text(
                    text = "Added Accounts (${accounts.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    accounts.forEach { (name, link) ->
                        AccountItem(name, link)
                    }
                }
            }
        }
    }


@Composable
fun AddAccountSection(
    accountName: String,
    accountLink: String,
    onNameChange: (String) -> Unit,
    onLinkChange: (String) -> Unit,
    onAddClick: () -> Unit,
    an: String?,
) {
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
            Text(
                text = "Add a new ${an} account to receive tasks",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Input fields (Name + Link)
            OutlinedTextField(
                value = accountName,
                onValueChange = onNameChange,
                label = { Text("Account Name") },
                placeholder = { Text("e.g., @username or display name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = accountLink,
                onValueChange = onLinkChange,
                label = { Text("Account Link") },
                placeholder = { Text("https://facebook.com/username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAddClick,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,       // background color
                    contentColor = Color.White          // text/icon color
                )
            ) {
                Text("Add Account")
            }
        }
    }
}

@Composable
fun AccountItem(name: String, link: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(name, style = MaterialTheme.typography.titleSmall)
            Text(link, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
    }
}
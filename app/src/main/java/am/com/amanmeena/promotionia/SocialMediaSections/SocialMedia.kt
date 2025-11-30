package com.amanmeena.promotionia.Screens

import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import am.com.amanmeena.promotionia.Viewmodels.MainViewModel
import TopAppBarPromotionia
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialMedia(
    modifier: Modifier = Modifier,
    an: String = "Facebook",
    viewModel: MainViewModel,
    navController: NavController
) {
    var username by remember { mutableStateOf("") }
    var link by remember { mutableStateOf("") }
    var isAdding by remember { mutableStateOf(false) }

    val scroll = rememberScrollState()
    val context = LocalContext.current
    val colors = MaterialTheme.colorScheme

    val approvedAccounts = when (an) {
        "Facebook" -> viewModel.accountsFacebook
        "Instagram" -> viewModel.accountsInstagram
        "X", "X (Twitter)", "Twitter" -> viewModel.accountsX
        else -> viewModel.accountsFacebook
    }

    val requestsMap = remember { mutableStateMapOf<String, Pair<String, Boolean>>() }

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    DisposableEffect(uid, an) {
        var listener: ListenerRegistration? = null

        if (uid != null) {
            listener = db.collection("requests")
                .whereEqualTo("uid", uid)
                .whereEqualTo("platform", an)
                .whereEqualTo("isAccepted", false)
                .addSnapshotListener { snap, _ ->
                    requestsMap.clear()
                    snap?.documents?.forEach { d ->
                        val rLink = d.getString("accountLink") ?: ""
                        val isAccepted = d.getBoolean("isAccepted") ?: false
                        requestsMap[rLink] = d.id to isAccepted
                    }
                }
        }

        onDispose { listener?.remove() }
    }

    Scaffold(
        topBar = { TopAppBarPromotionia(modifier, "Your $an accounts", navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scroll)
                .padding(16.dp)
        ) {

            Text(
                "Manage your $an accounts",
                fontSize = 20.sp,
                color = colors.onBackground
            )

            Spacer(Modifier.height(20.dp))

            // ADD ACCOUNT CARD
            Card(
                colors = CardDefaults.cardColors(colors.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {

                    Text("➕ Add New Account", fontSize = 18.sp, color = colors.onSurface)
                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = link,
                        onValueChange = { link = it },
                        label = { Text("Account Link") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (link.isBlank()) {
                                Toast.makeText(context, "Enter link", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isAdding = true

                            val normalizedPlatform = when (an.lowercase()) {
                                "facebook" -> "Facebook"
                                "instagram" -> "Instagram"
                                "x", "twitter", "x (twitter)" -> "X"
                                else -> an
                            }

                            val data = mapOf(
                                "uid" to uid,
                                "platform" to normalizedPlatform,
                                "accountHandel" to username.trim(),
                                "accountLink" to link.trim(),
                                "userToken" to viewModel.currentUserToken(),
                                "isAccepted" to false,
                                "createdAt" to System.currentTimeMillis()
                            )

                            db.collection("requests")
                                .add(data)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Request sent", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                                }
                                .addOnCompleteListener {
                                    username = ""
                                    link = ""
                                    isAdding = false
                                }
                        },
                        enabled = !isAdding,
                        colors = ButtonDefaults.buttonColors(colors.primary)
                    ) {
                        Text(
                            if (isAdding) "Sending…" else "Send Request",
                            color = colors.onPrimary
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Accounts", fontSize = 18.sp, color = colors.onBackground)
            Spacer(Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                approvedAccounts.forEach { raw ->
                    val (name, lnk) = safeSplit(raw)

                    AccountItem(
                        name = name,
                        link = lnk,
                        platform = an,
                        isAccepted = true,
                        navController = navController,
                        onRemove = { viewModel.removeAccount(an, raw) },
                        onCancelRequest = {}
                    )
                }

                requestsMap.forEach { (rLink, pair) ->
                    val (reqId, accepted) = pair

                    AccountItem(
                        name = "",
                        link = rLink,
                        platform = an,
                        isAccepted = accepted,
                        navController = navController,
                        onRemove = {},
                        onCancelRequest = {
                            if (!accepted) db.collection("requests").document(reqId).delete()
                        }
                    )
                }
            }

            Spacer(Modifier.height(50.dp))
        }
    }
}

fun safeSplit(raw: String): Pair<String, String> {
    val parts = raw.split("|")
    val name = parts.getOrNull(0)?.trim().orEmpty()
    val link = parts.getOrNull(1)?.trim().orEmpty()
    return name to link
}

@Composable
fun AccountItem(
    name: String,
    link: String,
    platform: String,
    isAccepted: Boolean,
    navController: NavController,
    onRemove: () -> Unit,
    onCancelRequest: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val clickable = isAccepted && link.isNotEmpty()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isAccepted) 1f else 0.5f)
            .then(
                if (clickable) Modifier.clickable {
                    navController.navigate("tasks/$platform/${Uri.encode(link)}")
                } else Modifier
            ),
        colors = CardDefaults.cardColors(colors.surface)
    ) {

        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(Modifier.weight(1f)) {
                if (name.isNotEmpty())
                    Text(name, fontSize = 16.sp, color = colors.onSurface)

                Text(link, color = colors.onSurfaceVariant, fontSize = 14.sp)

                if (!isAccepted)
                    Text("Pending approval", color = colors.error, fontSize = 12.sp)
            }

            if (isAccepted) {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, null, tint = colors.error)
                }
            } else {
                TextButton(onClick = onCancelRequest) {
                    Text("Cancel", color = colors.error)
                }
            }
        }
    }
}
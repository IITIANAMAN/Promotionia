package am.com.amanmeena.promotionia.AdminPanel

import TopAppBarPromotionia
import am.com.amanmeena.promotionia.Data.SocialRequest

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialMediaApprovalScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val tabs = listOf("Facebook", "Instagram", "X")
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { TopAppBarPromotionia(modifier, "Pending social media approval", navController) }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            // -------------------- TABS ---------------------
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                tabs.forEachIndexed { index, text ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(text) }
                    )
                }
            }

            // Show requests for selected tab
            PlatformRequestList(platform = tabs[selectedTab])
        }
    }
}

@Composable

fun PlatformRequestList(platform: String) {
    val db = FirebaseFirestore.getInstance()
    var requests by remember { mutableStateOf<List<SocialRequest>>(emptyList()) }
    DisposableEffect(platform) {
        val listener = db.collection("requests")
            .whereEqualTo("platform", platform)
            .whereEqualTo("isAccepted", false)
            .addSnapshotListener { snap, _ ->

                if (snap == null) return@addSnapshotListener

                val docs = snap.documents
                val temp = mutableListOf<SocialRequest>()
                docs.forEach { d ->
                    val uid = d.getString("uid") ?: ""

                    db.collection("users").document(uid)
                        .get()
                        .addOnSuccessListener { userSnap ->

                            val userName = userSnap.getString("name") ?: "Unknown"

                            temp.add(
                                SocialRequest(
                                    id = d.id,
                                    uid = uid,
                                    platform = platform,
                                    name = d.getString("accountHandel") ?: "",
                                    link = d.getString("accountLink") ?: "",
                                    userName = userName,
                                    userToken = d.getString("userToken") ?: ""   // ⭐ ADDED
                                )
                            )

                            // Update UI only when all docs are processed
                            if (temp.size == docs.size) {
                                requests = temp.toList()
                            }
                        }
                }

                // If no docs
                if (docs.isEmpty()) {
                    requests = emptyList()
                }
            }

        onDispose { listener.remove() }
    }
    if (requests.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No pending $platform requests")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(requests) { req ->
            ApprovalRequestCard(req)
        }
    }
}

@Composable
fun ApprovalRequestCard(req: SocialRequest) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(Modifier.padding(16.dp)) {

            Text("${req.platform} Request", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            Text("User Name: ${req.userName}", color = Color.Black)
            Text("Requested Handle: ${req.name}")
            Text("Link: ${req.link}")

            Spacer(Modifier.height(12.dp))

            // ---- Open link ----
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(req.link))
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(Color(0xFF0277BD))
            ) {
                Icon(Icons.Default.OpenInNew, null)
                Spacer(Modifier.width(8.dp))
                Text("Open Link")
            }

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ---- Approve ----
                Button(
                    onClick = { approveRequest(req) },
                    colors = ButtonDefaults.buttonColors(Color(0xFF00C853)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Approve")
                }

                // ---- Reject ----
                Button(
                    onClick = { db.collection("requests").document(req.id).delete() },
                    colors = ButtonDefaults.buttonColors(Color(0xFFD50000)),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Close, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Reject")
                }
            }
        }
    }
}

// -------------------------
// APPROVAL LOGIC
// -------------------------
fun approveRequest(req: SocialRequest) {
    val db = FirebaseFirestore.getInstance()

    val field = when (req.platform) {
        "Facebook" -> "accountFB"
        "Instagram" -> "accountInsta"
        else -> "accountX"
    }

    // 1️⃣ Add to approved accounts
    db.collection("users").document(req.uid)
        .update(field, FieldValue.arrayUnion("${req.name}|${req.link}"))

    // 2️⃣ Delete request
    db.collection("requests").document(req.id).delete()

    // 3️⃣ Send notification
    if (req.userToken.isNotEmpty()) {

    }
}
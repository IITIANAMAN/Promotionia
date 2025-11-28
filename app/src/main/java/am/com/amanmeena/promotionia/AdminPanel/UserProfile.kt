package am.com.amanmeena.promotionia.AdminPanel

import PersonData
import am.com.amanmeena.promotionia.R
import am.com.amanmeena.promotionia.utils.TopAppBarPromotionia
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UserProfile(
    data: MutableState<PersonData?>,
    navController: NavController
) {
    val user = data.value ?: return

    Scaffold(
        topBar = {
            TopAppBarPromotionia(
                modifier = Modifier,
                route = "${user.name}'s Profile",
                navController = navController
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item { ProfileInfoCard(user) }

            item { CoinSummaryCard(user) }


            item { SectionTitle("Facebook Accounts") }
            items(user.accountFB) { acc ->
                SocialAccountCard("Facebook", acc)
            }


            item { SectionTitle("Instagram Accounts") }
            items(user.accountInsta) { acc ->
                SocialAccountCard("Instagram", acc)
            }

            // X LIST
            item { SectionTitle("X (Twitter) Accounts") }
            items(user.accountX) { acc ->
                SocialAccountCard("X", acc)
            }
            item {
                DeleteUserSection(
                    uid = user.uid,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
fun ProfileInfoCard(user: PersonData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(50.dp)
                )
                Spacer(Modifier.width(12.dp))

                Column {
                    Text(user.name, fontWeight = FontWeight.Bold)
                    Text(user.email, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(12.dp))

            Text("Number: ${user.number}")
            Text("State: ${user.state}")
        }
    }
}

@Composable
fun CoinSummaryCard(user: PersonData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Text(
                "Coins Earned",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            SummaryRowVector("Total Coins", user.totalCoin, Icons.Default.TrendingUp)

            SummaryRowDrawable("Facebook Coins", user.totalCoinFb, R.drawable.fb)
            SummaryRowDrawable("Instagram Coins", user.totalCoinInsta, R.drawable.insta)
            SummaryRowDrawable("X Coins", user.totalCoinX, R.drawable.x)
        }
    }
}

@Composable
fun SummaryRowVector(
    label: String,
    value: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.DarkGray)
            Spacer(Modifier.width(8.dp))
            Text(label)
        }
        Text(value.toString(), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SummaryRowDrawable(
    label: String,
    value: Int,
    iconRes: Int
) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )

            Spacer(Modifier.width(8.dp))
            Text(label)
        }
        Text(value.toString(), fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SocialAccountCard(platform: String, value: String) {
    val (username, link) = safeSplit(value)
    val context = LocalContext.current

    if (link.isBlank()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = getPlatformIcon(platform)),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("$platform Account", fontWeight = FontWeight.Bold)

                    if (username.isNotEmpty())
                        Text("Name: $username", color = Color.DarkGray)
                }
            }
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open Link")
            }
        }
    }
}

fun safeSplit(raw: String): Pair<String, String> {
    val parts = raw.split("|")
    return parts.getOrNull(0).orEmpty() to parts.getOrNull(1).orEmpty()
}

@Composable
fun getPlatformIcon(platform: String): Int {
    return when (platform) {
        "Facebook" -> R.drawable.fb
        "Instagram" -> R.drawable.insta
        "X" -> R.drawable.x
        else -> R.drawable.`in`     // fallback
    }
}
@Composable
fun DeleteUserSection(uid: String, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Account") },
            text = {
                Text("Are you sure you want to delete this user's account?\n\nThis cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false

                        // Delete user document
                        db.collection("users").document(uid)
                            .delete()
                            .addOnSuccessListener {
                                navController.popBackStack() // go back to users list
                            }
                    },
                    colors = ButtonDefaults.buttonColors(Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Button(
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(Color(0xFFD32F2F)), // red button
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text("Delete User Account", color = Color.White)
    }
}
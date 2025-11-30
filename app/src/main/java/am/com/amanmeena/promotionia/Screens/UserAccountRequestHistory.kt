package am.com.amanmeena.promotionia.Screens

import am.com.amanmeena.promotionia.utils.formatMemberSince
import TopAppBarPromotionia
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class RequestEntry(
    val id: String = "",
    val platform: String = "",
    val accountHandel: String = "",
    val createdAt: Long = 0L,
    val isAccepted: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAccountRequestHistory(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    val colors = MaterialTheme.colorScheme
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    var requests by remember { mutableStateOf<List<RequestEntry>>(emptyList()) }
    var selectedFilter by remember { mutableStateOf("All") }

    // REAL-TIME FIRESTORE LISTENER
    LaunchedEffect(uid) {
        if (uid == null) return@LaunchedEffect

        FirebaseFirestore.getInstance()
            .collection("requests")
            .whereEqualTo("uid", uid)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.map { doc ->
                    RequestEntry(
                        id = doc.id,
                        platform = doc.getString("platform") ?: "",
                        accountHandel = doc.getString("accountHandel") ?: "",
                        createdAt = doc.getLong("createdAt") ?: 0L,
                        isAccepted = doc.getBoolean("isAccepted") ?: false
                    )
                } ?: emptyList()

                requests = list.sortedByDescending { it.createdAt }
            }
    }

    // FILTER LOGIC
    val filteredList = when (selectedFilter) {
        "Facebook" -> requests.filter { it.platform.equals("Facebook", true) }
        "Instagram" -> requests.filter { it.platform.equals("Instagram", true) }
        "X" -> requests.filter { it.platform.equals("X", true) }
        else -> requests
    }

    Scaffold(
        topBar = {
            TopAppBarPromotionia(
                modifier = modifier,
                route = "Request History",
                navController = navController
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colors.background)
                .padding(16.dp)
        ) {

            // FILTER CHIPS
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {

                FilterChipItem("Facebook", selectedFilter) { selectedFilter = it }
                FilterChipItem("Instagram", selectedFilter) { selectedFilter = it }
                FilterChipItem("X", selectedFilter) { selectedFilter = it }
            }

            // LIST OF REQUESTS
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredList) { item ->
                    RequestHistoryCard(item)
                }
            }
        }
    }
}

@Composable
fun FilterChipItem(label: String, selected: String, onSelect: (String) -> Unit) {
    FilterChip(
        selected = selected == label,
        onClick = { onSelect(label) },
        label = { Text(label) }
    )
}

@Composable
fun RequestHistoryCard(item: RequestEntry) {

    val colors = MaterialTheme.colorScheme

    Card(
        colors = CardDefaults.cardColors(colors.surface),
        elevation = CardDefaults.cardElevation(3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {

            // Platform Title
            Text(
                text = item.platform,
                style = MaterialTheme.typography.titleMedium,
                color = colors.primary
            )

            Spacer(Modifier.height(6.dp))

            // Handle
            Text(
                text = "Handle: ${item.accountHandel}",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurface
            )

            Spacer(Modifier.height(6.dp))

            // Created Date
            Text(
                text = "Created: ${formatMemberSince(item.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = colors.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))

            // Status Chip
            SuggestionChip(
                onClick = {},
                label = {
                    Text(
                        if (item.isAccepted) "Accepted" else "Pending",
                        color = if (item.isAccepted)
                            colors.onPrimary
                        else
                            colors.onSurface
                    )
                },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = if (item.isAccepted)
                        colors.primary
                    else
                        colors.surfaceVariant
                )
            )
        }
    }
}
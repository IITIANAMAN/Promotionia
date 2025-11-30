package am.com.amanmeena.promotionia.Screens

import TopAppBarPromotionia
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutApp(modifier: Modifier = Modifier,navController: NavController) {

    Scaffold(
        topBar = {
            TopAppBarPromotionia(modifier,"About The App", navController = navController)
        }
    ) { innerPadding ->

        Column(
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = "A modern reward-earning task app. Complete tasks → earn coins → compete on leaderboard.",
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ======================
            // EXPANDABLE CARDS
            // ======================
            ExpandableCard(
                title = "How The App Works",
                content = """
                    • You receive tasks from the admin.
                    • Each task contains a title and a link.
                    • Click the link → complete the action.
                    • Return to the app — task completion is recorded.
                    • Coins are automatically added.
                """.trimIndent()
            )

            ExpandableCard(
                title = "How To Complete a Task",
                content = """
                    1. Open the Tasks section.
                    2. Choose any available task.
                    3. Tap the provided link.
                    4. Perform the required action (follow, like, view, etc.).
                    5. Return to the app — your completion count increases.
                """.trimIndent()
            )

            ExpandableCard(
                title = "Earning Coins",
                content = """
                    • Every completed task gives coins.
                    • More tasks = more coins.
                    • Coins accumulate over time and are visible on your profile.
                """.trimIndent()
            )

            ExpandableCard(
                title = "Leaderboard System",
                content = """
                    • Updated frequently.
                    • Ranks all users based on total coins.
                    • Compete with others and reach the top.
                """.trimIndent()
            )

            ExpandableCard(
                title = "Your Account & Data Safety",
                content = """
                    • Your name, phone number, and email are securely stored.
                    • Your total coins and completion history stay synced.
                    • You can log in from any device.
                    • High-level security keeps your data safe.
                """.trimIndent()
            )

            ExpandableCard(
                title = "Why Use Promotionia?",
                content = """
                    Promotionia helps creators and brands promote their content, 
                    and rewards users for completing simple tasks. 
                    
                    A fast, smooth, and modern 2025-ready experience.
                """.trimIndent()
            )
            ExpandableCard(
                title = "Security & Anti-Cheating Policy",
                content = """
        To keep Promotionia fair for every user, strict security rules are followed:

        • Cheating or performing fake tasks is strictly prohibited.
        • We regularly monitor user activity to ensure tasks are genuinely completed.
        • If a task is opened but not actually performed (follow, like, visit, etc.), 
          coins will NOT be added to the account.
        • Repeated suspicious activity, fake interactions, or misuse of tasks 
          can lead to temporary suspension.
        • Multiple violations will result in permanent account deletion.

        Promotionia maintains a safe, fair, and trusted experience for all users.
    """.trimIndent()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Enjoy using Promotionia!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ExpandableCard(
    title: String,
    content: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .animateContentSize(animationSpec = spring())
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(8.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = content,
                    fontSize = 15.sp
                )
            }
        }
    }
}
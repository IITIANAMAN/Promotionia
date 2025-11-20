package am.com.amanmeena.promotionia.Screens

import am.com.amanmeena.promotionia.AuthClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun VerifyEmailScreen(
    navController: NavController,
    auth: AuthClient = AuthClient()
) {

    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    // 🔥 Correct coroutine scope
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Verify Your Email", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))

        Text(
            "A verification link has been sent to your email.\nPlease check your inbox.",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(32.dp))

        if (message.isNotEmpty()) {
            Text(message, color = Color.Red, fontSize = 14.sp)
            Spacer(Modifier.height(10.dp))
        }

        // 🔥 Resend Email Button
        Button(
            onClick = {
                loading = true
                message = ""

                scope.launch {
                    val result = auth.resendVerification()
                    loading = false

                    message = if (result.isSuccess) {
                        "Verification email sent!"
                    } else {
                        result.exceptionOrNull()?.message ?: "Failed"
                    }
                }
            },
            enabled = !loading,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            if (loading) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Resend Email")
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "Already verified? Login",
            color = Color(0xFF007AFF),
            modifier = Modifier.clickable {
                navController.navigate("login") {
                    popUpTo("verify_email") { inclusive = true }
                }
            }
        )
    }
}
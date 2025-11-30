package am.com.amanmeena.promotionia.Screens

import am.com.amanmeena.promotionia.AuthClient
import am.com.amanmeena.promotionia.Data.Values.ADMIN_UID
import am.com.amanmeena.promotionia.Viewmodels.AdminViewModel
import am.com.amanmeena.promotionia.Viewmodels.MainViewModel
import TopAppBarPromotionia
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    adminViewModel: AdminViewModel,
    mainViewModel: MainViewModel
) {

    val authClient = remember { AuthClient() }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (!isDark)
                    MaterialTheme.colorScheme.background
                else
                    Color(0xFF0E0E0E) // improved AMOLED dark background
            ),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(10.dp),
            colors = CardDefaults.cardColors(
                if (!isDark)
                    MaterialTheme.colorScheme.surface
                else
                    Color(0xFF1A1A1A) // dark but contrasting card
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // APP TITLE
                Text(
                    text = "Promotionia",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary   // THEMED
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Welcome Back!",
                    fontSize = 16.sp,
                    color = colors.onSurfaceVariant   // THEMED
                )

                Spacer(modifier = Modifier.height(24.dp))

                // EMAIL FIELD
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        focusedLabelColor = colors.primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // PASSWORD FIELD
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation =
                        if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            if (passwordVisible) "Hide" else "Show",
                            color = colors.primary,
                            modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        focusedLabelColor = colors.primary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Forgot Password?",
                    color = colors.primary,
                    modifier = Modifier.align(Alignment.End)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ERROR MESSAGE
                if (errorMessage.isNotEmpty()) {
                    Text(
                        errorMessage,
                        color = colors.error,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // LOGIN BUTTON
                Button(
                    onClick = {

                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Please fill all fields"
                            return@Button
                        }

                        scope.launch {
                            isLoading = true
                            errorMessage = ""

                            val result = authClient.login(email, password)
                            isLoading = false

                            if (result.isSuccess) {
                                val uid = authClient.currentUser()?.uid

                                mainViewModel.reInitForNewUser(uid!!)
                                mainViewModel.saveFcmToken()

                                if (uid == ADMIN_UID) {
                                    adminViewModel.startAdminRealtime()
                                    navController.navigate("admin") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            } else {
                                val err = result.exceptionOrNull()?.message ?: "Login failed"
                                if (err.contains("verify", ignoreCase = true)) {
                                    navController.navigate("verify_email") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    errorMessage = err
                                }
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,       // THEMED
                        contentColor = colors.onPrimary        // THEMED
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = colors.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Login", fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "New to the app? Register here",
                    color = colors.primary,
                    modifier = Modifier.clickable {
                        navController.navigate("signup")
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}
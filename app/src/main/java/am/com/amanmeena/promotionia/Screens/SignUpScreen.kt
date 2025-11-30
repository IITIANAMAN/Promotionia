package am.com.amanmeena.promotionia.Screens

import am.com.amanmeena.promotionia.AuthClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val auth = remember { AuthClient() }
    val scope = rememberCoroutineScope()

    val colors = MaterialTheme.colorScheme
    val isDark = isSystemInDarkTheme()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val statesOfIndia = listOf(
        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
        "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand",
        "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur",
        "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab",
        "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
        "Uttar Pradesh", "Uttarakhand", "West Bengal", "Delhi", "Puducherry"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF0E0E0E) else colors.background)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                if (isDark) Color(0xFF1A1A1A) else colors.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // TITLE
                Text(
                    "Create Account",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // FULL NAME
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        focusedLabelColor = colors.primary
                    )
                )
                Spacer(Modifier.height(12.dp))

                // EMAIL
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        focusedLabelColor = colors.primary
                    )
                )
                Spacer(Modifier.height(12.dp))

                // PHONE
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, null) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        focusedLabelColor = colors.primary
                    )
                )
                Spacer(Modifier.height(12.dp))

                // STATE DROPDOWN
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = state,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select State") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            focusedLabelColor = colors.primary
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statesOfIndia.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    state = it
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // PASSWORD
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
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
                Spacer(Modifier.height(12.dp))

                // CONFIRM PASSWORD
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    visualTransformation =
                        if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            if (confirmPasswordVisible) "Hide" else "Show",
                            color = colors.primary,
                            modifier = Modifier.clickable { confirmPasswordVisible = !confirmPasswordVisible }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        focusedLabelColor = colors.primary
                    )
                )

                Spacer(Modifier.height(16.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(errorMessage, color = colors.error, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                }

                // CREATE ACCOUNT BUTTON
                Button(
                    onClick = {
                        if (name.isBlank() || email.isBlank() || phone.isBlank() ||
                            state.isBlank() || password.isBlank() ||
                            confirmPassword.isBlank()
                        ) {
                            errorMessage = "Please fill all fields"
                            return@Button
                        }

                        if (password != confirmPassword) {
                            errorMessage = "Passwords do not match"
                            return@Button
                        }

                        scope.launch {
                            isLoading = true
                            val result = auth.signUp(name, email, password, phone, state)
                            isLoading = false

                            if (result.isSuccess) {
                                navController.navigate("verify_email")
                            } else {
                                errorMessage =
                                    result.exceptionOrNull()?.message ?: "Signup failed"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = colors.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Create Account", fontSize = 18.sp)
                    }
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    "Already have an account? Login",
                    color = colors.primary,
                    modifier = Modifier.clickable {
                        navController.navigate("login") {
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
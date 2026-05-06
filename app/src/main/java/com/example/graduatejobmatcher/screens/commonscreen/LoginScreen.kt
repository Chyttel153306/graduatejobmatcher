 package com.example.graduatejobmatcher.screens.commonscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.viewmodel.AppViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: AppViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            JobMatcherLogo()

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Email",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE8E8E8),
                        unfocusedContainerColor = Color(0xFFE8E8E8),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Password",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = Color.Black)
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE8E8E8),
                        unfocusedContainerColor = Color(0xFFE8E8E8),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Black,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Please enter email and password"
                        return@Button
                    }
                    isLoading = true
                    errorMessage = ""
                    viewModel.login(email, password) { success, role, errMsg ->
                        isLoading = false
                        if (success) {
                            val dest = when (role) {
                                "employer" -> "employer_dashboard"
                                "admin" -> "admin_dashboard"
                                else -> "student_dashboard"
                            }
                            navController.navigate(dest) {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            errorMessage = errMsg
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B71CA)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Login", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("register") }) {
                Text(
                    text = "Create Account",
                    color = Color(0xFF4A69AD),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun JobMatcherLogo(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(92.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFE9F1FF)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Work,
                    contentDescription = "JobMatcher Logo Placeholder",
                    tint = Color(0xFF1E5BB8),
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "JobMatcher",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E5BB8)
        )

        // Replace this placeholder with:
        // Image(
        //     painter = painterResource(id = R.drawable.jobmatcher_logo),
        //     contentDescription = "JobMatcher Logo",
        //     modifier = Modifier.size(140.dp)
        // )
    }
}

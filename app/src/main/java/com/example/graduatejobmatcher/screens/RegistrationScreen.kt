package com.example.graduatejobmatcher.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
fun RegistrationScreen(navController: NavController, viewModel: AppViewModel) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("student") }
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
            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Name Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Full Name", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE8E8E8),
                        unfocusedContainerColor = Color(0xFFE8E8E8),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.LightGray,
                        cursorColor = Color.Black
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Email Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Email", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE8E8E8),
                        unfocusedContainerColor = Color(0xFFE8E8E8),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.LightGray,
                        cursorColor = Color.Black
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Password Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Password", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = Color.Gray)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE8E8E8),
                        unfocusedContainerColor = Color(0xFFE8E8E8),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.LightGray,
                        cursorColor = Color.Black
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Confirm Password Field
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Confirm Password", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE8E8E8),
                        unfocusedContainerColor = Color(0xFFE8E8E8),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.LightGray,
                        cursorColor = Color.Black
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Role Selection: Student, then Admin, then Employer (vertical)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Register as", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                // Student
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = role == "student",
                        onClick = { role = "student" }
                    )
                    Text("Student", modifier = Modifier.padding(start = 8.dp))
                }

                // Admin (below Student)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = role == "admin",
                        onClick = { role = "admin" }
                    )
                    Text("Admin", modifier = Modifier.padding(start = 8.dp))
                }

                // Employer (below Admin)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = role == "employer",
                        onClick = { role = "employer" }
                    )
                    Text("Employer", modifier = Modifier.padding(start = 8.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register Button
            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || password.isBlank()) {
                        errorMessage = "Please fill all fields"
                        return@Button
                    }
                    if (password != confirmPassword) {
                        errorMessage = "Passwords do not match"
                        return@Button
                    }
                    isLoading = true
                    errorMessage = ""
                    viewModel.register(name, email, password, role) { success, errMsg ->
                        isLoading = false
                        if (success) {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
                        } else {
                            errorMessage = errMsg
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B71CA)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Already have an account? Login", color = Color(0xFF4A69AD))
            }
        }
    }
}
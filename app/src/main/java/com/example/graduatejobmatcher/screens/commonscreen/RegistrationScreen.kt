package com.example.graduatejobmatcher.screens.commonscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
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

    // Core fields
    var name            by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var role            by remember { mutableStateOf("student") }
    var adminCreationPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage    by remember { mutableStateOf("") }
    var isLoading       by remember { mutableStateOf(false) }

    // Student-specific fields
    var degree         by remember { mutableStateOf("") }
    var institution    by remember { mutableStateOf("") }
    var graduationDate by remember { mutableStateOf("") }
    var skillInput     by remember { mutableStateOf("") }
    val skills         = remember { mutableStateListOf<String>() }

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor   = Color(0xFFE8E8E8),
        unfocusedContainerColor = Color(0xFFE8E8E8),
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor   = Color.LightGray,
        cursorColor             = Color.Black,
        focusedTextColor        = Color.Black,
        unfocusedTextColor      = Color.Black
    )

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF5F5F5)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Full Name
            RegField("Full Name", name, { name = it }, fieldColors)
            Spacer(modifier = Modifier.height(12.dp))

            // ── Email
            RegField("Email", email, { email = it }, fieldColors)
            Spacer(modifier = Modifier.height(12.dp))

            // ── Password
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Password", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Filled.Visibility
                                else Icons.Filled.VisibilityOff,
                                contentDescription = null, tint = Color.Black
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = fieldColors
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // ── Confirm Password
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Confirm Password", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    visualTransformation = if (passwordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = fieldColors
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // ── Role Selection
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Register as", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                listOf("student", "admin", "employer").forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = role == option,
                            onClick  = { role = option }
                        )
                        Text(
                            option.replaceFirstChar { it.uppercase() },
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // ── Student-only: Education & Skills
            if (role == "admin") {
                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Create Admin Password", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    OutlinedTextField(
                        value = adminCreationPassword,
                        onValueChange = { adminCreationPassword = it },
                        visualTransformation = if (passwordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = fieldColors,
                        supportingText = {
                            Text("Required to create an admin account")
                        }
                    )
                }
            }

            if (role == "student") {
                Spacer(modifier = Modifier.height(20.dp))

                SectionHeader("Education")
                Spacer(modifier = Modifier.height(8.dp))

                RegField("Degree (e.g. B.S. Computer Science)", degree,
                    { degree = it }, fieldColors)
                Spacer(modifier = Modifier.height(8.dp))

                RegField("Institution / University", institution,
                    { institution = it }, fieldColors)
                Spacer(modifier = Modifier.height(8.dp))

                RegField("Graduation Date (e.g. June 2025)", graduationDate,
                    { graduationDate = it }, fieldColors)

                Spacer(modifier = Modifier.height(20.dp))

                SectionHeader("Skills")
                Spacer(modifier = Modifier.height(8.dp))

                // Skill input + Add button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = skillInput,
                        onValueChange = { skillInput = it },
                        placeholder = { Text("e.g. Python", color = Color.Gray) },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = fieldColors
                    )
                    IconButton(
                        onClick = {
                            val trimmed = skillInput.trim()
                            if (trimmed.isNotEmpty() && !skills.contains(trimmed)) {
                                skills.add(trimmed)
                                skillInput = ""
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Add skill",
                            tint = Color(0xFF3B71CA))
                    }
                }

                // Skill chips
                if (skills.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.foundation.layout.FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        skills.forEach { skill ->
                            InputChip(
                                selected = false,
                                onClick  = {},
                                label    = { Text(skill, fontSize = 13.sp) },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { skills.remove(skill) },
                                        modifier = Modifier.size(16.dp)
                                    ) {
                                        Icon(Icons.Filled.Close, contentDescription = "Remove",
                                            modifier = Modifier.size(14.dp))
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Sign Up Button
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
                    if (role == "admin" && adminCreationPassword.isBlank()) {
                        errorMessage = "Please enter the admin password"
                        return@Button
                    }
                    if (role == "student" && degree.isBlank()) {
                        errorMessage = "Please enter your degree"
                        return@Button
                    }
                    isLoading = true
                    errorMessage = ""
                    viewModel.register(
                        name            = name,
                        email           = email,
                        password        = password,
                        role            = role,
                        adminCreationPassword = if (role == "admin") adminCreationPassword else "",
                        degree          = if (role == "student") degree else "",
                        institution     = if (role == "student") institution else "",
                        graduationDate  = if (role == "student") graduationDate else "",
                        skills          = if (role == "student") skills.toList() else emptyList()
                    ) { success, errMsg ->
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
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ── Helpers ──────────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = Color(0xFF3B71CA)
    )
    HorizontalDivider(color = Color(0xFFDDDDDD), thickness = 1.dp)
}

@Composable
private fun RegField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    colors: TextFieldColors
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = colors,
            singleLine = true
        )
    }
}

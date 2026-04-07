package com.example.graduatejobmatcher.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.User
import com.example.graduatejobmatcher.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(navController: NavController, viewModel: AppViewModel) {
    var users by remember { mutableStateOf<List<User>>(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel.getAllUsers { userList ->
            users = userList
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Manage Users") }) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(users) { user ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Name: ${user.name}")
                        Text("Email: ${user.email}")
                        Text("Role: ${user.role}")
                        if (user.role != "admin") {
                            Button(
                                onClick = {
                                    val newRole = if (user.role == "student") "employer" else "student"
                                    viewModel.updateUserRole(user.userId, newRole)
                                    users = users.map {
                                        if (it.userId == user.userId) it.copy(role = newRole) else it
                                    }
                                }
                            ) {
                                Text("Toggle Role")
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.example.graduatejobmatcher.screens.employer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.Application
import com.example.graduatejobmatcher.model.User
import com.example.graduatejobmatcher.viewmodel.AppViewModel

@Composable
fun ViewApplicantsScreen(
    navController: NavController,
    viewModel: AppViewModel,
    applicationId: String
) {
    var application by remember { mutableStateOf<Application?>(null) }
    var user by remember { mutableStateOf<User?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(applicationId) {
        viewModel.getApplicationById(applicationId) { app ->
            application = app

            if (app != null) {
                viewModel.getUserById(app.studentId) { u ->
                    user = u
                    loading = false
                }
            } else {
                loading = false
            }
        }
    }

    if (loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(Modifier.padding(16.dp)) {
        Text("Applicant Details", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Text("Name: ${user?.name ?: "N/A"}")
        Text("Email: ${user?.email ?: "N/A"}")
        Text("Degree: ${user?.degree ?: "N/A"}")
        Text("Institution: ${user?.institution ?: "N/A"}")

        Spacer(Modifier.height(12.dp))

        Text("Status: ${application?.status ?: "N/A"}")
    }
}
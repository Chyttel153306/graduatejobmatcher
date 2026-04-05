package com.example.graduatejobmatcher.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.Application
import com.example.graduatejobmatcher.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewApplicantsScreen(
    navController: NavController,
    viewModel: AppViewModel,
    jobId: String
) {
    var applicants by remember { mutableStateOf<List<Application>>(emptyList()) }

    LaunchedEffect(jobId) {
        viewModel.getApplicationsForJob(jobId) { apps ->
            applicants = apps
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Applicants") }) }) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(applicants) { app ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Student ID: ${app.studentId}")
                        Text("Status: ${app.status}")
                        Row {
                            if (app.status == "pending") {
                                Button(
                                    onClick = {
                                        viewModel.updateApplicationStatus(app.applicationId, "accepted")
                                        applicants = applicants.map {
                                            if (it.applicationId == app.applicationId) it.copy(status = "accepted")
                                            else it
                                        }
                                    }
                                ) {
                                    Text("Accept")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.updateApplicationStatus(app.applicationId, "rejected")
                                        applicants = applicants.map {
                                            if (it.applicationId == app.applicationId) it.copy(status = "rejected")
                                            else it
                                        }
                                    }
                                ) {
                                    Text("Reject")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
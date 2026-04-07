package com.example.graduatejobmatcher.screens.student


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.Application
import com.example.graduatejobmatcher.viewmodel.AppViewModel

@Composable
fun ApplyJobScreen(
    navController: NavController,
    viewModel: AppViewModel,
    jobId: String
) {
    var coverLetter by remember { mutableStateOf("") }
    var applied by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Apply for Job", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = coverLetter,
            onValueChange = { coverLetter = it },
            label = { Text("Cover Letter (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val studentId = viewModel.getCurrentUserId() ?: return@Button
                val application = Application(
                    jobId = jobId,
                    studentId = studentId,
                    status = "pending"
                )
                viewModel.applyJob(application)
                applied = true
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !applied
        ) {
            Text(if (applied) "Application Submitted" else "Submit Application")
        }

        if (applied) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go Back")
            }
        }
    }
}
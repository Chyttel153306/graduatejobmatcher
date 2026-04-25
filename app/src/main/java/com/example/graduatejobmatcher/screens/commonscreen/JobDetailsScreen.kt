package com.example.graduatejobmatcher.screens.commonscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.Application
import com.example.graduatejobmatcher.model.Job
import com.example.graduatejobmatcher.model.User
import com.example.graduatejobmatcher.ui.theme.components.RoundedAvatarShape
import com.example.graduatejobmatcher.ui.theme.components.UserAvatar
import com.example.graduatejobmatcher.viewmodel.AppViewModel

@Composable
fun JobDetailsScreen(
    navController: NavController,
    viewModel: AppViewModel,
    jobId: String
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var job by remember(jobId) { mutableStateOf<Job?>(viewModel.jobs.find { it.jobId == jobId }) }
    val primaryBlue = Color(0xFF3F51B5)
    var existingApplication by remember { mutableStateOf<Application?>(null) }
    var employer by remember { mutableStateOf<User?>(null) }
    val isStudent = currentUser?.role == "student"

    LaunchedEffect(Unit) {
        if (currentUser == null) {
            viewModel.fetchCurrentUser()
        }
    }

    LaunchedEffect(jobId) {
        if (job == null) {
            viewModel.getJobById(jobId) { fetchedJob ->
                job = fetchedJob
            }
        }
    }

    LaunchedEffect(jobId, currentUser?.userId, isStudent) {
        val studentId = currentUser?.userId.orEmpty()
        if (!isStudent || studentId.isBlank()) {
            existingApplication = null
        } else {
            viewModel.getApplicationForJobAndStudent(jobId, studentId) { application ->
                existingApplication = application
            }
        }
    }

    DisposableEffect(job?.employerId) {
        val employerId = job?.employerId.orEmpty()
        if (employerId.isBlank()) {
            employer = null
            onDispose { }
        } else {
            val registration = viewModel.listenUserById(employerId) { updatedEmployer ->
                employer = updatedEmployer
            }
            onDispose { registration.remove() }
        }
    }

    val currentJob = job
    val employerName = employer?.name?.ifBlank { null } ?: currentJob?.company.orEmpty()
    val employerImageBase64 = employer?.profileImageBase64.orEmpty()

    if (currentJob == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = primaryBlue)
        }
        return
    }

    Scaffold(
        bottomBar = {
            if (isStudent) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { navController.navigate("apply_job/${currentJob.jobId}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                    ) {
                        Text(
                            text = if (existingApplication != null) "Edit Application" else "Apply Now",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(Color.White)
        ) {
            // Hero header with overlapping logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.75f)
                        .background(primaryBlue)
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(top = 12.dp, start = 8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }

                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.BottomCenter),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        UserAvatar(
                            name = employerName.ifBlank { currentJob.company },
                            imageBase64 = employerImageBase64,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            shape = RoundedAvatarShape,
                            backgroundColor = Color(0xFFE8EAF6),
                            contentColor = primaryBlue,
                            textSize = 32.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Job title & info row
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentJob.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = employerName.ifBlank { "Employer" },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Location – black text
                InfoRow(
                    icon = Icons.Default.LocationOn,
                    text = currentJob.location.ifBlank { "Location not specified" },
                    textColor = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Salary – real field from job
                InfoRow(
                    icon = Icons.Default.MonetizationOn,
                    text = currentJob.salary.ifBlank { "Salary not specified" },
                    textColor = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Job description
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Job Description",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = currentJob.description.ifBlank { "No description provided." },
                    color = Color.Black,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Required skills – real list from job
                Text(
                    text = "Required Skills",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (currentJob.requiredSkills.isEmpty()) {
                    Text(
                        text = "No specific skills listed.",
                        color = Color.Black,
                        fontSize = 14.sp
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        currentJob.requiredSkills.forEach { skill ->
                            DetailSkillChip(skill)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String, textColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, color = textColor, fontSize = 14.sp)
    }
}

@Composable
fun DetailSkillChip(label: String) {
    Surface(
        color = Color(0xFFE8EAF6),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

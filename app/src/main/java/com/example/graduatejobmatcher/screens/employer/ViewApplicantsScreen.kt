package com.example.graduatejobmatcher.screens.employer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.viewmodel.AppViewModel

// Temporary data class – replace with real joined data from your repository
data class Applicant(
    val applicationId: String,
    val studentId: String,
    val name: String,
    val statusBadge: String, // "New", "Shortlisted", "Rejected"
    val university: String,
    val skills: List<String>,
    val degree: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewApplicantsScreen(
    navController: NavController,
    viewModel: AppViewModel,
    jobId: String,
    jobTitle: String = "Junior Developer",
    companyName: String = "TechCorp Inc.",
    postedDate: String = "May 20, 2024"
) {
    var applicants by remember { mutableStateOf<List<Applicant>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredApplicants = remember(searchQuery, applicants) {
        if (searchQuery.isBlank()) applicants
        else applicants.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    // Fetch real applicants for this job
    LaunchedEffect(jobId) {
        viewModel.getApplicationsForJob(jobId) { applications ->
            // You need to enrich each application with student details (name, skills, university, degree)
            // For demonstration, we map to dummy data. Replace with actual mapping.
            val enriched = applications.map { app ->
                Applicant(
                    applicationId = app.applicationId,
                    studentId = app.studentId,
                    name = "Student ${app.studentId.take(4)}", // replace with real name
                    statusBadge = when (app.status) {
                        "pending" -> "New"
                        "accepted" -> "Shortlisted"
                        "rejected" -> "Rejected"
                        else -> "New"
                    },
                    university = "University Name", // fetch from student profile
                    skills = listOf("Kotlin", "Java"), // fetch from student profile
                    degree = "BS Computer Science"
                )
            }
            applicants = enriched
        }
    }

    val badgeColors = mapOf(
        "New" to Color(0xFF4CAF50),
        "Shortlisted" to Color(0xFFFF9800),
        "Rejected" to Color(0xFFF44336)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Applicants", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            // Job header card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(jobTitle, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(companyName, fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("📅 Posted $postedDate", fontSize = 12.sp, color = Color.Gray)
                        Text("👥 ${applicants.size} Applicants", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search applicants...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3F51B5),
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Applicants list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredApplicants) { applicant ->
                    ApplicantCard(
                        applicant = applicant,
                        badgeColor = badgeColors[applicant.statusBadge] ?: Color.Gray,
                        onViewProfile = {
                            navController.navigate("student_profile/${applicant.studentId}")
                        },
                        onReview = {
                            // Update status (e.g., from "New" to "Shortlisted")
                            val newStatus = when (applicant.statusBadge) {
                                "New" -> "accepted"
                                "Shortlisted" -> "accepted"
                                else -> applicant.statusBadge.lowercase()
                            }
                            viewModel.updateApplicationStatus(applicant.applicationId, newStatus)
                            // Refresh list after update
                            applicants = applicants.map {
                                if (it.applicationId == applicant.applicationId) {
                                    it.copy(statusBadge = if (newStatus == "accepted") "Shortlisted" else "Rejected")
                                } else it
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ApplicantCard(
    applicant: Applicant,
    badgeColor: Color,
    onViewProfile: () -> Unit,
    onReview: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    applicant.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = badgeColor.copy(alpha = 0.2f),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        applicant.statusBadge,
                        color = badgeColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(applicant.degree, fontSize = 14.sp, color = Color.Gray)
            Text(applicant.university, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val displayedSkills = if (applicant.skills.size > 3) applicant.skills.take(3) else applicant.skills
                displayedSkills.forEach { skill ->
                    SkillChip(skill)
                }
                if (applicant.skills.size > 3) {
                    SkillChip("+${applicant.skills.size - 3}")
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onViewProfile,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("View Profile")
                }
                Button(
                    onClick = onReview,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
                ) {
                    Text("Review →")
                }
            }
        }
    }
}

@Composable
fun SkillChip(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFE0E0E0)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
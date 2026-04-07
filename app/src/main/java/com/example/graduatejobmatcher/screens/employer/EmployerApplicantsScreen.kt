package com.example.graduatejobmatcher.screens.employer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.Job
import com.example.graduatejobmatcher.viewmodel.AppViewModel

// --- LOCAL DATA MODEL (renamed to avoid conflict with existing Applicant in other files) ---
data class ApplicantItem(
    val applicationId: String,
    val studentId: String,
    val name: String,
    val statusBadge: String,
    val university: String,
    val skills: List<String>,
    val degree: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerApplicantsScreen(navController: NavController, viewModel: AppViewModel) {
    var jobs by remember { mutableStateOf<List<Job>>(emptyList()) }
    var selectedJob by remember { mutableStateOf<Job?>(null) }
    var applicants by remember { mutableStateOf<List<ApplicantItem>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val employerId = viewModel.getCurrentUserId() ?: ""

    val primaryBlue = Color(0xFF3B69E4)
    val backgroundColor = Color(0xFFF7F8FA)

    // --- FETCH DATA LOGIC ---
    LaunchedEffect(employerId) {
        viewModel.getJobsForEmployer(employerId) { jobList ->
            jobs = jobList
            if (jobList.isNotEmpty() && selectedJob == null) {
                selectedJob = jobList.first()
            }
        }
    }

    LaunchedEffect(selectedJob) {
        selectedJob?.let { job ->
            viewModel.getApplicationsForJob(job.jobId) { applications ->
                applicants = applications.map { app ->
                    ApplicantItem(
                        applicationId = app.applicationId,
                        studentId = app.studentId,
                        name = "Student ${app.studentId.take(4)}",
                        statusBadge = when (app.status) {
                            "pending" -> "New"
                            "accepted" -> "Shortlisted"
                            "rejected" -> "Rejected"
                            else -> "New"
                        },
                        university = "Cebu Technological University",
                        skills = listOf("Kotlin", "Java", "Firebase", "SQL"),
                        degree = "BS Information Technology"
                    )
                }
            }
        }
    }

    val filteredApplicants = remember(searchQuery, applicants) {
        if (searchQuery.isBlank()) applicants
        else applicants.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    val badgeColors = mapOf(
        "New" to Color(0xFF4CAF50),
        "Shortlisted" to Color(0xFFFF9800),
        "Rejected" to Color(0xFFF44336)
    )

    // --- UI LAYOUT ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Applicants", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 12.dp)) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                        Surface(
                            color = Color.Red,
                            shape = CircleShape,
                            modifier = Modifier.size(10.dp).align(Alignment.TopEnd)
                        ) {}
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryBlue)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor)
        ) {
            if (selectedJob != null) {
                // 1. Job Details Card
                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            color = Color(0xFFE8EFFF),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.BusinessCenter, null, tint = primaryBlue, modifier = Modifier.padding(10.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(selectedJob!!.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusLabel("Active", Color(0xFF4CAF50))
                            }
                            Text(selectedJob!!.company ?: "TechCorp Inc.", color = Color.Gray, fontSize = 14.sp)
                            Row(modifier = Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DateRange, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                Text(" Posted May 20, 2024", fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(Icons.Default.People, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                Text(" ${applicants.size} Applicants", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                // 2. Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    placeholder = { Text("Search applicants...", fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFEEF1F6),
                        focusedContainerColor = Color(0xFFEEF1F6),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )

                // 3. Filter Row
                val filterOptions = listOf("All (${applicants.size})", "New (12)", "Shortlisted (8)", "Rejected (5)")
                LazyRow(
                    modifier = Modifier.padding(vertical = 16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterOptions) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = primaryBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // 4. Applicants List (using renamed composable)
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(filteredApplicants) { applicant ->
                        EmployerApplicantCard(
                            applicant = applicant,
                            badgeColor = badgeColors[applicant.statusBadge] ?: Color.Gray,
                            onViewProfile = { navController.navigate("student_profile/${applicant.studentId}") },
                            onReview = {
                                viewModel.updateApplicationStatus(applicant.applicationId, "accepted")
                            }
                        )
                    }
                }
            }
        }
    }
}

// --- SUB-COMPONENTS (renamed to avoid conflicts) ---

@Composable
fun EmployerApplicantCard(applicant: ApplicantItem, badgeColor: Color, onViewProfile: () -> Unit, onReview: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                Surface(modifier = Modifier.size(50.dp), shape = CircleShape, color = Color.LightGray) {}
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(applicant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusLabel(applicant.statusBadge, badgeColor)
                    }
                    Text(applicant.degree, fontSize = 13.sp, color = Color.DarkGray)
                    Text(applicant.university, fontSize = 12.sp, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Applied", fontSize = 10.sp, color = Color.Gray)
                    Text("May 28, 2024", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }

            Row(modifier = Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Skills:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(8.dp))
                applicant.skills.take(3).forEach { skill ->
                    EmployerSkillChip(skill)
                    Spacer(modifier = Modifier.width(4.dp))
                }
                if (applicant.skills.size > 3) {
                    EmployerSkillChip("+${applicant.skills.size - 3}")
                }
            }

            Row {
                Button(
                    onClick = onViewProfile,
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF1F6)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Visibility, null, modifier = Modifier.size(16.dp), tint = Color(0xFF3B69E4))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("View Profile", color = Color(0xFF3B69E4), fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onReview,
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B69E4)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Review", fontSize = 13.sp, color = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(16.dp), tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {},
                    modifier = Modifier.size(40.dp).border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.BookmarkBorder, null, tint = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun StatusLabel(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EmployerSkillChip(skill: String) {
    Surface(
        color = Color(0xFFEEF1F6),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = skill,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            color = Color.DarkGray
        )
    }
}
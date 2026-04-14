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
import com.example.graduatejobmatcher.model.User
import com.example.graduatejobmatcher.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

// FIX: Use a function so Locale is resolved at call time, not captured at class-load time
private fun dateFormatter() = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

data class ApplicantItem(
    val applicationId: String,
    val studentId: String,
    val name: String,
    val statusBadge: String,
    val university: String,
    val skills: List<String>,
    val degree: String,
    val appliedDate: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerApplicantsScreen(navController: NavController, viewModel: AppViewModel) {
    var jobs by remember { mutableStateOf<List<Job>>(emptyList()) }
    var selectedJob by remember { mutableStateOf<Job?>(null) }
    var applicants by remember { mutableStateOf<List<ApplicantItem>>(emptyList()) }
    var isLoadingApplicants by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val employerId = viewModel.getCurrentUserId() ?: ""

    val primaryBlue = Color(0xFF3B69E4)
    val backgroundColor = Color(0xFFF7F8FA)

    LaunchedEffect(employerId) {
        viewModel.getJobsForEmployer(employerId) { jobList ->
            jobs = jobList
            if (selectedJob == null && jobList.isNotEmpty()) {
                selectedJob = jobList.first()
            }
        }
    }

    LaunchedEffect(selectedJob) {
        val job = selectedJob ?: return@LaunchedEffect
        isLoadingApplicants = true
        applicants = emptyList()

        viewModel.getApplicationsForJob(job.jobId) { applications ->
            if (applications.isEmpty()) {
                isLoadingApplicants = false
                return@getApplicationsForJob
            }

            val enriched = mutableListOf<ApplicantItem>()
            var pending = applications.size

            applications.forEach { app ->
                viewModel.getUserById(app.studentId) { user: User? ->
                    val item = ApplicantItem(
                        applicationId = app.applicationId,
                        studentId     = app.studentId,
                        name          = user?.name?.takeIf { it.isNotBlank() } ?: "Unknown Applicant",
                        statusBadge   = when (app.status.lowercase()) {
                            "accepted" -> "Shortlisted"
                            "rejected" -> "Rejected"
                            else -> "New"
                        },
                        university    = user?.institution?.takeIf { it.isNotBlank() } ?: "Institution not set",
                        skills        = user?.skills ?: emptyList(),
                        degree        = user?.degree?.takeIf { it.isNotBlank() } ?: "Degree not set",
                        appliedDate   = app.appliedDate?.let { dateFormatter().format(it) } ?: "—"
                    )
                    enriched.add(item)
                    pending--
                    if (pending == 0) {
                        applicants = enriched.sortedByDescending { it.appliedDate }
                        isLoadingApplicants = false
                    }
                }
            }
        }
    }

    val displayedApplicants = remember(searchQuery, selectedFilter, applicants) {
        applicants
            .filter { a ->
                when (selectedFilter) {
                    "New" -> a.statusBadge == "New"
                    "Shortlisted" -> a.statusBadge == "Shortlisted"
                    "Rejected" -> a.statusBadge == "Rejected"
                    else -> true
                }
            }
            .filter { a ->
                searchQuery.isBlank() ||
                        a.name.contains(searchQuery, ignoreCase = true) ||
                        a.degree.contains(searchQuery, ignoreCase = true) ||
                        a.university.contains(searchQuery, ignoreCase = true) ||
                        a.skills.any { it.contains(searchQuery, ignoreCase = true) }
            }
    }

    val countNew         = applicants.count { it.statusBadge == "New" }
    val countShortlisted = applicants.count { it.statusBadge == "Shortlisted" }
    val countRejected    = applicants.count { it.statusBadge == "Rejected" }

    val badgeColors = mapOf(
        "New"         to Color(0xFF4CAF50),
        "Shortlisted" to Color(0xFFFF9800),
        "Rejected"    to Color(0xFFF44336)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Applicants", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 12.dp)) {
                        Icon(Icons.Default.Notifications, null, tint = Color.White)
                        Surface(
                            color = Color.Red,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.TopEnd)
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
            selectedJob?.let { job ->

                Card(
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            color = Color(0xFFE8EFFF),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                Icons.Default.BusinessCenter, null,
                                tint = primaryBlue,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(job.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                                Spacer(Modifier.width(8.dp))
                                StatusLabel(
                                    text  = job.status.replaceFirstChar { it.uppercase() },
                                    color = if (job.status == "active") Color(0xFF4CAF50) else Color(0xFFFF9800)
                                )
                            }
                            Text(job.company.ifBlank { "Company not set" }, color = Color.Gray, fontSize = 14.sp)
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.DateRange, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                Text(
                                    " Posted ${job.postedDate?.let { dateFormatter().format(it) } ?: "—"}",
                                    fontSize = 12.sp, color = Color.Gray
                                )
                                Spacer(Modifier.width(12.dp))
                                Icon(Icons.Default.People, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                Text(" ${applicants.size} Applicants", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }

                if (jobs.size > 1) {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        items(jobs) { j ->
                            FilterChip(
                                selected = j.jobId == job.jobId,
                                onClick  = { selectedJob = j },
                                label    = {
                                    Text(
                                        j.title,
                                        color = if (j.jobId == job.jobId) Color.White else Color.Black,
                                        fontSize = 12.sp
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text("Search by name, skill, degree…", fontSize = 14.sp, color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFEEF1F6),
                        focusedContainerColor   = Color(0xFFEEF1F6),
                        unfocusedBorderColor    = Color.Transparent,
                        focusedBorderColor      = primaryBlue,
                        focusedTextColor        = Color.Black,
                        unfocusedTextColor      = Color.Black
                    ),
                    singleLine = true
                )

                val filterOptions = listOf(
                    "All"         to applicants.size,
                    "New"         to countNew,
                    "Shortlisted" to countShortlisted,
                    "Rejected"    to countRejected
                )
                LazyRow(
                    modifier = Modifier.padding(vertical = 12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterOptions) { (label, count) ->
                        val isSelected = selectedFilter == label
                        FilterChip(
                            selected = isSelected,
                            onClick  = { selectedFilter = label },
                            label    = {
                                Text(
                                    "$label ($count)",
                                    color = if (isSelected) Color.White else Color.Black
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = primaryBlue,
                                selectedLabelColor     = Color.White
                            )
                        )
                    }
                }

                when {
                    isLoadingApplicants -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = primaryBlue)
                        }
                    }
                    displayedApplicants.isEmpty() -> {
                        Box(
                            Modifier.fillMaxSize().padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (applicants.isEmpty()) "No applications yet for this job."
                                else "No applicants match your search.",
                                color = Color.Gray, fontSize = 14.sp
                            )
                        }
                    }
                    else -> {
                        LazyColumn(Modifier.fillMaxSize()) {
                            items(
                                items = displayedApplicants,
                                key   = { it.applicationId }
                            ) { applicant ->
                                EmployerApplicantCard(
                                    applicant     = applicant,
                                    badgeColor    = badgeColors[applicant.statusBadge] ?: Color.Gray,
                                    // ── CONNECTED: tapping "Profile" navigates to ApplicantDetailsScreen ──
                                    onViewProfile = {
                                        navController.navigate(
                                            "applicant_details/${applicant.applicationId}"
                                        )
                                    },
                                    onShortlist = {
                                        viewModel.updateApplicationStatus(applicant.applicationId, "accepted")
                                        applicants = applicants.map {
                                            if (it.applicationId == applicant.applicationId)
                                                it.copy(statusBadge = "Shortlisted")
                                            else it
                                        }
                                    },
                                    onReject = {
                                        viewModel.updateApplicationStatus(applicant.applicationId, "rejected")
                                        applicants = applicants.map {
                                            if (it.applicationId == applicant.applicationId)
                                                it.copy(statusBadge = "Rejected")
                                            else it
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (jobs.isEmpty() && !isLoadingApplicants) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("You have not posted any jobs yet.", color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun EmployerApplicantCard(
    applicant    : ApplicantItem,
    badgeColor   : Color,
    onViewProfile: () -> Unit,
    onShortlist  : () -> Unit,
    onReject     : () -> Unit
) {
    val primaryBlue = Color(0xFF3B69E4)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape  = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row {
                Surface(
                    modifier = Modifier.size(50.dp),
                    shape = CircleShape,
                    color = Color(0xFFE8EFFF)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text       = applicant.name.firstOrNull()?.uppercase() ?: "?",
                            color      = primaryBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 20.sp
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(applicant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                        Spacer(Modifier.width(8.dp))
                        StatusLabel(applicant.statusBadge, badgeColor)
                    }
                    Text(applicant.degree,     fontSize = 13.sp, color = Color.DarkGray)
                    Text(applicant.university, fontSize = 12.sp, color = Color.Gray)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Applied",             fontSize = 10.sp, color = Color.Gray)
                    Text(applicant.appliedDate, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                }
            }

            if (applicant.skills.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Skills:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                    Spacer(Modifier.width(8.dp))
                    applicant.skills.take(3).forEach { skill ->
                        EmployerSkillChip(skill)
                        Spacer(Modifier.width(4.dp))
                    }
                    if (applicant.skills.size > 3) {
                        EmployerSkillChip("+${applicant.skills.size - 3}")
                    }
                }
            } else {
                Spacer(Modifier.height(12.dp))
            }

            Row {
                Button(
                    onClick  = onViewProfile,
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF1F6)),
                    shape    = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Visibility, null, modifier = Modifier.size(16.dp), tint = Color.Black)
                    Spacer(Modifier.width(6.dp))
                    Text("Profile", color = Color.Black, fontSize = 13.sp)
                }
                Spacer(Modifier.width(6.dp))
                Button(
                    onClick  = onShortlist,
                    modifier = Modifier.weight(1f).height(40.dp),
                    enabled  = applicant.statusBadge != "Shortlisted",
                    colors   = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    shape    = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(Modifier.width(4.dp))
                    Text("Shortlist", fontSize = 12.sp, color = Color.White)
                }
                Spacer(Modifier.width(6.dp))
                IconButton(
                    onClick  = onReject,
                    modifier = Modifier
                        .size(40.dp)
                        .border(1.dp, Color(0xFFF44336), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Cancel, null, tint = Color(0xFFF44336))
                }
            }
        }
    }
}

@Composable
fun StatusLabel(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text       = text,
            color      = color,
            fontSize   = 10.sp,
            modifier   = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
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
            text     = skill,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            color    = Color(0xFF3B69E4)
        )
    }
}
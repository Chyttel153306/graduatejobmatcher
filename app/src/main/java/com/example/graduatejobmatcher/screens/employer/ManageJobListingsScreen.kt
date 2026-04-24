package com.example.graduatejobmatcher.screens.employer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.Job
import com.example.graduatejobmatcher.navigation.Screen
import com.example.graduatejobmatcher.ui.theme.components.UserAvatar
import com.example.graduatejobmatcher.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Locale

private fun manageJobDateFormatter() = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageJobListingsScreen(
    navController: NavController,
    viewModel: AppViewModel,
    onViewClick: (Job) -> Unit = { job ->
        navController.navigate(Screen.EmployerJobDetails.passJobId(job.jobId))
    },
    onUpdateClick: (Job) -> Unit = { job ->
        navController.navigate(Screen.EmployerUpdateJob.passJobId(job.jobId))
    }
) {
    val currentUser by viewModel.currentUser.collectAsState()
    var searchText by remember { mutableStateOf("") }
    var jobs by remember { mutableStateOf<List<Job>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var jobToDelete by remember { mutableStateOf<Job?>(null) }

    val bgColor = Color(0xFFF5F7FB)
    val appBarBlue = Color(0xFF3E73E9)
    val employerId = viewModel.getCurrentUserId().orEmpty()

    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            viewModel.fetchCurrentUser()
        }
    }

    DisposableEffect(employerId) {
        if (employerId.isBlank()) {
            isLoading = false
            jobs = emptyList()
            onDispose { }
        } else {
            isLoading = true
            val registration = viewModel.listenJobsForEmployer(employerId) { employerJobs ->
                jobs = employerJobs
                isLoading = false
            }
            onDispose { registration.remove() }
        }
    }

    val filteredJobs = remember(jobs, searchText) {
        jobs.filter {
            it.title.contains(searchText, ignoreCase = true) ||
                it.company.contains(searchText, ignoreCase = true)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Manage Job Listings",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    UserAvatar(
                        user = currentUser,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(34.dp),
                        backgroundColor = Color.White.copy(alpha = 0.2f),
                        textSize = 12.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = appBarBlue
                )
            )
        },
        containerColor = bgColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            SearchBox(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredJobs.size} job listings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B1F2A)
                )

                Text(
                    text = "Sort: Newest",
                    color = Color(0xFF4B5563),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = appBarBlue)
                    }
                }

                filteredJobs.isEmpty() -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Text(
                            text = if (jobs.isEmpty()) {
                                "No job listings found. Tap 'Post Job' on the dashboard to create one."
                            } else {
                                "No job listings match your search."
                            },
                            modifier = Modifier.padding(18.dp),
                            color = Color(0xFF6B7280),
                            fontSize = 14.sp
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredJobs, key = { it.jobId }) { job ->
                            ManageJobCard(
                                job = job,
                                employerName = currentUser?.name.orEmpty(),
                                employerProfileImageBase64 = currentUser?.profileImageBase64.orEmpty(),
                                onViewClick = { onViewClick(job) },
                                onUpdateClick = { onUpdateClick(job) },
                                onDeleteClick = { jobToDelete = job }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }

    val pendingDelete = jobToDelete
    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { jobToDelete = null },
            containerColor = Color.White,
            title = { Text("Are you sure you want to delete it?") },
            text = {
                Text(
                    "Deleting ${pendingDelete.title.ifBlank { "this job" }} will also remove its applications, interviews, and related notifications from Firebase."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteJob(pendingDelete.jobId) { success, _ ->
                            if (success) jobToDelete = null
                        }
                    }
                ) {
                    Text("Yes", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { jobToDelete = null }) {
                    Text("No", color = Color(0xFF356EEA), fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun SearchBox(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFEFF2F8))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFF1B1F2A)
            ),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = "Search jobs or companies...",
                        color = Color(0xFF8A94A6),
                        fontSize = 16.sp
                    )
                }
                innerTextField()
            }
        )
    }
}

@Composable
private fun ManageJobCard(
    job: Job,
    employerName: String,
    employerProfileImageBase64: String,
    onViewClick: () -> Unit,
    onUpdateClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                JobBadge(
                    job = job,
                    employerName = employerName,
                    employerProfileImageBase64 = employerProfileImageBase64
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = job.title.ifBlank { "Untitled Job" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF151A24),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = job.company.ifBlank { "Company not set" },
                        color = Color(0xFF5B6472),
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))
                StatusChip(status = job.status)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoText(
                    text = "Posted: ${job.postedDate?.let { manageJobDateFormatter().format(it) } ?: "Not available"}",
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                JobTypeChip(
                    type = job.jobType.ifBlank { "Not set" }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Location: ${job.location.ifBlank { "Not provided" }}",
                color = Color(0xFF5B6472),
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onViewClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF356EEA),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "View",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = onUpdateClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF37BA6B),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Update",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Update",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                OutlinedButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFDC2626)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
private fun JobBadge(
    job: Job,
    employerName: String,
    employerProfileImageBase64: String
) {
    val initials = remember(job.title, job.company) {
        val source = job.company.ifBlank { job.title }.trim()
        source
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.take(1).uppercase() }
            .ifBlank { "J" }
    }

    UserAvatar(
        name = employerName.ifBlank { initials },
        imageBase64 = employerProfileImageBase64,
        modifier = Modifier.size(62.dp),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = badgeColorForJob(job),
        textSize = 20.sp
    )
}

@Composable
private fun StatusChip(status: String) {
    val normalizedStatus = status.ifBlank { "Unknown" }
    val bg = when (normalizedStatus.lowercase()) {
        "active", "approved" -> Color(0xFFE9F8EF)
        "draft", "pending" -> Color(0xFFF8F0DA)
        "paused" -> Color(0xFFEAF1FF)
        "rejected" -> Color(0xFFFDECEC)
        else -> Color(0xFFF1F5F9)
    }

    val text = when (normalizedStatus.lowercase()) {
        "active", "approved" -> Color(0xFF1F9D55)
        "draft", "pending" -> Color(0xFFC58A08)
        "paused" -> Color(0xFF3A67D6)
        "rejected" -> Color(0xFFDC2626)
        else -> Color(0xFF64748B)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = normalizedStatus.replaceFirstChar { it.uppercase() },
            color = text,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun JobTypeChip(type: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF1F4FA))
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = type,
            color = Color(0xFF6B7280),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun InfoText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = Color(0xFF5B6472),
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

private fun badgeColorForJob(job: Job): Color {
    val palette = listOf(
        Color(0xFF3E73E9),
        Color(0xFFF0C323),
        Color(0xFF36B96D),
        Color(0xFF9B72E9),
        Color(0xFFE96B6B)
    )

    val key = job.company.ifBlank { job.title }
    val index = key.hashCode().mod(palette.size)
    return palette[index]
}

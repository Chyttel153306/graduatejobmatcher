package com.example.graduatejobmatcher.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.Job
import com.example.graduatejobmatcher.navigation.Screen
import com.example.graduatejobmatcher.viewmodel.AppViewModel

private val ApprovedPrimaryBlue = Color(0xFF3F51B5)
private val ApprovedGreen = Color(0xFF4CAF50)
private val ApprovedRejectRed = Color(0xFFE53935)
private val ApprovedBackgroundGrey = Color(0xFFF5F7FA)
private val ApprovedCardWhite = Color(0xFFFFFFFF)

private val ApprovedAvatarColors = listOf(
    Color(0xFF3F51B5), Color(0xFF009688), Color(0xFF9C27B0),
    Color(0xFFFF5722), Color(0xFF607D8B), Color(0xFFE91E63),
    Color(0xFF4CAF50), Color(0xFFFF9800)
)

private fun approvedAvatarColor(text: String): Color =
    ApprovedAvatarColors[text.firstOrNull()?.code?.rem(ApprovedAvatarColors.size) ?: 0]

private fun approvedFormatDate(job: Job): String = job.postedDate?.let {
    java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(it)
} ?: "-"

private fun approvedDeadline(job: Job): String = job.deadline?.let {
    java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(it)
} ?: "-"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApprovedJobsScreen(
    navController: NavController,
    viewModel: AppViewModel
) {
    var approvedJobs by remember { mutableStateOf<List<Job>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var sortNewest by remember { mutableStateOf(true) }
    var showSortMenu by remember { mutableStateOf(false) }
    var jobToReject by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getApprovedJobs { approvedJobs = it }
    }

    val filtered = approvedJobs
        .filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                it.company.contains(searchQuery, ignoreCase = true)
        }
        .let { list ->
            if (sortNewest) list.sortedByDescending { it.postedDate } else list.sortedBy { it.postedDate }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Approved Job Posts",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ApprovedPrimaryBlue)
            )
        },
        containerColor = ApprovedBackgroundGrey
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text("Search jobs or companies...", color = Color.Black, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black)
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedBorderColor = ApprovedPrimaryBlue,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = ApprovedCardWhite,
                    unfocusedContainerColor = ApprovedCardWhite
                ),
                singleLine = true
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filtered.size} approved post${if (filtered.size != 1) "s" else ""}",
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
                Box {
                    TextButton(onClick = { showSortMenu = true }) {
                        Text(
                            text = "Sort: ${if (sortNewest) "Newest" else "Oldest"}",
                            fontSize = 13.sp,
                            color = ApprovedPrimaryBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    androidx.compose.material3.DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text("Newest") },
                            onClick = { sortNewest = true; showSortMenu = false }
                        )
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text("Oldest") },
                            onClick = { sortNewest = false; showSortMenu = false }
                        )
                    }
                }
            }

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.WorkOff,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No approved jobs", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.jobId }) { job ->
                        ApprovedJobCard(
                            job = job,
                            onClick = { navController.navigate(Screen.JobDetails.passJobId(job.jobId)) },
                            onReject = { jobToReject = job }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }

    jobToReject?.let { job ->
        AlertDialog(
            onDismissRequest = { jobToReject = null },
            title = { Text("Reject approved job?") },
            text = {
                Text(
                    "This will move \"${job.title.ifBlank { "this job" }}\" to rejected jobs and hide it from students."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateJobStatus(job.jobId, "rejected") {
                            approvedJobs = approvedJobs.filterNot { it.jobId == job.jobId }
                            jobToReject = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ApprovedRejectRed)
                ) {
                    Text("Reject Job")
                }
            },
            dismissButton = {
                TextButton(onClick = { jobToReject = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ApprovedJobCard(
    job: Job,
    onClick: () -> Unit,
    onReject: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ApprovedCardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(approvedAvatarColor(job.title), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = job.title.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.padding(horizontal = 6.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                    Text(
                        text = job.company,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = job.jobType.ifBlank { "Full-time" },
                        fontSize = 11.sp,
                        color = ApprovedGreen,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                    Text(
                        text = approvedFormatDate(job),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                if (job.deadline != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Event,
                            contentDescription = null,
                            tint = Color(0xFFFBC02D),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                        Text(
                            text = approvedDeadline(job),
                            fontSize = 12.sp,
                            color = Color(0xFFFBC02D),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = ApprovedGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.padding(horizontal = 3.dp))
                Text(
                    text = "Approved and visible to students",
                    color = ApprovedGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Job Details",
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                DetailRow(
                    label = "Location",
                    value = job.location.ifBlank { "Not provided" },
                    icon = Icons.Default.LocationOn
                )
                DetailRow(
                    label = "Salary",
                    value = job.salary.ifBlank { "Not provided" },
                    icon = Icons.Default.CheckCircle
                )
                DetailRow(
                    label = "Required Skills",
                    value = job.requiredSkills.takeIf { it.isNotEmpty() }?.joinToString(", ") ?: "Not provided",
                    icon = Icons.Default.CheckCircle
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = job.description.ifBlank { "No description provided." },
                    color = Color.DarkGray,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Open Full Details", color = ApprovedPrimaryBlue, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ApprovedRejectRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reject")
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.padding(horizontal = 3.dp))
        Text(
            text = "$label: ",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            color = Color.DarkGray,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

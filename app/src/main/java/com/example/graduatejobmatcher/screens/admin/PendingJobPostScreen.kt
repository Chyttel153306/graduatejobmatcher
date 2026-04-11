package com.example.graduatejobmatcher.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.Job
import com.example.graduatejobmatcher.navigation.Screen
import com.example.graduatejobmatcher.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.*

private val PrimaryBlue    = Color(0xFF3F51B5)
private val ApproveGreen   = Color(0xFF4CAF50)
private val RejectRed      = Color(0xFFF44336)
private val BackgroundGrey = Color(0xFFF5F7FA)
private val CardWhite      = Color(0xFFFFFFFF)

private val AvatarColors = listOf(
    Color(0xFF3F51B5), Color(0xFF009688), Color(0xFF9C27B0),
    Color(0xFFFF5722), Color(0xFF607D8B), Color(0xFFE91E63),
    Color(0xFF4CAF50), Color(0xFFFF9800)
)

private fun avatarColor(text: String): Color =
    AvatarColors[text.firstOrNull()?.code?.rem(AvatarColors.size) ?: 0]

private fun formatDate(date: Date?): String =
    date?.let { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(it) } ?: "—"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingJobsScreen(
    navController: NavController,
    viewModel: AppViewModel          // ✅ now received from NavGraph
) {
    var pendingJobs  by remember { mutableStateOf<List<Job>>(emptyList()) }
    var searchQuery  by remember { mutableStateOf("") }
    var sortNewest   by remember { mutableStateOf(true) }
    var showSortMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getPendingJobs { pendingJobs = it }
    }

    val filtered = pendingJobs
        .filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.company.contains(searchQuery, ignoreCase = true)
        }
        .let { list ->
            if (sortNewest) list.sortedByDescending { it.postedDate }
            else            list.sortedBy { it.postedDate }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Pending Job Posts",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                    }
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        },
        containerColor = BackgroundGrey
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ── Search Bar ────────────────────────────────────────────
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text("Search jobs or companies...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                },
                trailingIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = PrimaryBlue)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = CardWhite,
                    unfocusedContainerColor = CardWhite
                ),
                singleLine = true
            )

            // ── Count + Sort ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filtered.size} pending post${if (filtered.size != 1) "s" else ""}",
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
                Box {
                    TextButton(onClick = { showSortMenu = true }) {
                        Text(
                            text = "Sort: ${if (sortNewest) "Newest" else "Oldest"}",
                            fontSize = 13.sp,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = PrimaryBlue
                        )
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Newest") },
                            onClick = { sortNewest = true; showSortMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Oldest") },
                            onClick = { sortNewest = false; showSortMenu = false }
                        )
                    }
                }
            }

            // ── List ──────────────────────────────────────────────────
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
                        Text("No pending jobs", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.jobId }) { job ->
                        PendingJobCard(
                            job = job,
                            onView = {
                                // ✅ uses correct route from Screen sealed class
                                navController.navigate(Screen.JobDetails.passJobId(job.jobId))
                            },
                            onApprove = {
                                viewModel.updateJobStatus(job.jobId, "approved") {
                                    viewModel.getPendingJobs { pendingJobs = it }
                                }
                            },
                            onReject = {
                                viewModel.updateJobStatus(job.jobId, "rejected") {
                                    viewModel.getPendingJobs { pendingJobs = it }
                                }
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
fun PendingJobCard(
    job: Job,
    onView: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Top Row: Avatar + Title + Type ────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(avatarColor(job.title)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = job.title.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

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
                    color = Color(0xFFE8EAF6)
                ) {
                    Text(
                        text = job.jobType.ifBlank { "Full-time" },
                        fontSize = 11.sp,
                        color = PrimaryBlue,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Date Row ──────────────────────────────────────────────
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
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formatDate(job.postedDate),
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
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatDate(job.deadline),
                            fontSize = 12.sp,
                            color = Color(0xFFFBC02D),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(12.dp))

            // ── Action Buttons ────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.People,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(20.dp)
                )

                OutlinedButton(
                    onClick = onView,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    border = BorderStroke(1.dp, PrimaryBlue),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
                ) {
                    Text("View", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onApprove,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ApproveGreen)
                ) {
                    Text("Approve", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onReject,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RejectRed)
                ) {
                    Text("Reject", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
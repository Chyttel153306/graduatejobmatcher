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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.AdminReport
import com.example.graduatejobmatcher.model.Job
import com.example.graduatejobmatcher.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Locale

private fun reportDateFormatter() = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportScreen(
    navController: NavController,
    viewModel: AppViewModel
) {
    var report by remember { mutableStateOf(AdminReport()) }
    val primaryBlue = Color(0xFF3F51B5)
    val pageBg = Color(0xFFF5F7FA)

    DisposableEffect(Unit) {
        val registrations = viewModel.listenAdminReport { report = it }
        onDispose { registrations.forEach { registration -> registration.remove() } }
    }

    Scaffold(
        containerColor = pageBg,
        topBar = {
            TopAppBar(
                title = { Text("All Report", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryBlue)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(pageBg),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ReportHeader(totalUsers = report.users.size)
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ReportMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Pending Jobs",
                        count = report.pendingJobs.size,
                        icon = Icons.Default.PendingActions,
                        color = Color(0xFFFBC02D)
                    )
                    ReportMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Approved Jobs",
                        count = report.approvedJobs.size,
                        icon = Icons.Default.CheckCircle,
                        color = Color(0xFF4CAF50)
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ReportMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Rejected Jobs",
                        count = report.rejectedJobs.size,
                        icon = Icons.Default.Cancel,
                        color = Color(0xFFF44336)
                    )
                    ReportMetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Total Users",
                        count = report.users.size,
                        icon = Icons.Default.Group,
                        color = Color(0xFF9C27B0)
                    )
                }
            }

            reportSection("Pending Jobs", report.pendingJobs, Color(0xFFFBC02D))
            reportSection("Approved Jobs", report.approvedJobs, Color(0xFF4CAF50))
            reportSection("Rejected Jobs", report.rejectedJobs, Color(0xFFF44336))
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.reportSection(
    title: String,
    jobs: List<Job>,
    accentColor: Color
) {
    item {
        Text(
            text = title,
            color = Color(0xFF111827),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }

    if (jobs.isEmpty()) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = "No ${title.lowercase()} found.",
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    } else {
        items(jobs, key = { "${title}_${it.jobId}" }) { job ->
            ReportJobRow(job = job, accentColor = accentColor)
        }
    }
}

@Composable
private fun ReportHeader(totalUsers: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8EEFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Assessment, contentDescription = null, tint = Color(0xFF3F51B5))
            }
            Spacer(modifier = Modifier.padding(start = 14.dp))
            Column {
                Text("Generated Platform Report", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("$totalUsers total registered users", color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun ReportMetricCard(
    modifier: Modifier,
    title: String,
    count: Int,
    icon: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier.height(116.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(26.dp))
            Column {
                Text(count.toString(), fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color.Black)
                Text(title, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun ReportJobRow(job: Job, accentColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = job.title.take(1).uppercase().ifBlank { "J" },
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.padding(start = 12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = job.title.ifBlank { "Untitled Job" },
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = job.company.ifBlank { "Company not set" },
                    color = Color.Gray,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Posted: ${job.postedDate?.let { reportDateFormatter().format(it) } ?: "Not available"}",
                    color = Color(0xFF6B7280),
                    fontSize = 12.sp
                )
            }
        }
    }
}

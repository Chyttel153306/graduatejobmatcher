package com.example.graduatejobmatcher.screens.employer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.Job
import com.example.graduatejobmatcher.navigation.Screen
import com.example.graduatejobmatcher.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Locale

private fun employerDetailsDateFormatter() = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

@Composable
fun EmployerJobDetailsScreen(
    navController: NavController,
    viewModel: AppViewModel,
    jobId: String
) {
    var job by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(jobId) {
        viewModel.getJobById(jobId) { loadedJob ->
            job = loadedJob
        }
    }

    val currentJob = job
    val primaryBlue = Color(0xFF3F51B5)

    if (currentJob == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Job not found")
        }
        return
    }

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { navController.navigate(Screen.EmployerUpdateJob.passJobId(currentJob.jobId)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Update job",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text(
                        text = "Update Job",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .background(Color(0xFFD9DBE1))
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
                        Text(
                            text = currentJob.company.take(1).uppercase().ifBlank { "J" },
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryBlue
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentJob.title.ifBlank { "Untitled Job" },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                StatusPill(currentJob.status)
                Spacer(modifier = Modifier.height(12.dp))

                EmployerDetailInfoRow(
                    icon = Icons.Default.LocationOn,
                    text = currentJob.location.ifBlank { "Location not specified" }
                )
                Spacer(modifier = Modifier.height(8.dp))

                EmployerDetailInfoRow(
                    icon = Icons.Default.MonetizationOn,
                    text = currentJob.salary.ifBlank { "Salary not specified" }
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Posted ${currentJob.postedDate?.let { employerDetailsDateFormatter().format(it) } ?: "Not available"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                EmployerDetailsSectionTitle("Job Description")
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = currentJob.description.ifBlank { "No description provided." },
                    color = Color.Black,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                EmployerDetailsSectionTitle("Required Skills")
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
                            EmployerDetailsSkillChip(skill)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                EmployerDetailsSectionTitle("Job Summary")
                Spacer(modifier = Modifier.height(12.dp))
                EmployerSummaryCard(
                    company = currentJob.company.ifBlank { "Company not set" },
                    jobType = currentJob.jobType.ifBlank { "Not set" },
                    employerId = currentJob.employerId.ifBlank { "Not available" }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun EmployerDetailsSectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = Color.Black
    )
}

@Composable
private fun EmployerDetailInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.padding(start = 6.dp))
        Text(text = text, color = Color.Black, fontSize = 14.sp)
    }
}

@Composable
private fun EmployerDetailsSkillChip(label: String) {
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

@Composable
private fun EmployerSummaryCard(
    company: String,
    jobType: String,
    employerId: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FA))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Company: $company", color = Color.Black, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Job Type: $jobType", color = Color.Black, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Employer ID: $employerId", color = Color.Gray, fontSize = 13.sp)
        }
    }
}

@Composable
private fun StatusPill(status: String) {
    val normalizedStatus = status.ifBlank { "Unknown" }
    val containerColor = when (normalizedStatus.lowercase()) {
        "approved", "active" -> Color(0xFFE9F8EF)
        "pending", "draft" -> Color(0xFFFFF4DB)
        "rejected" -> Color(0xFFFDECEC)
        else -> Color(0xFFF1F5F9)
    }
    val contentColor = when (normalizedStatus.lowercase()) {
        "approved", "active" -> Color(0xFF1F9D55)
        "pending", "draft" -> Color(0xFFC58A08)
        "rejected" -> Color(0xFFDC2626)
        else -> Color(0xFF64748B)
    }

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = normalizedStatus.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            color = contentColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

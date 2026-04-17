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
import com.example.graduatejobmatcher.viewmodel.AppViewModel

@Composable
fun JobDetailsScreen(
    navController: NavController,
    viewModel: AppViewModel,
    jobId: String
) {
    // Find job from the ViewModel's live data (real data)
    val job = viewModel.jobs.find { it.jobId == jobId }
    val primaryBlue = Color(0xFF3F51B5)

    if (job == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Job not found")
        }
        return
    }

    Scaffold(
        bottomBar = {
            // White background for bottom bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = { navController.navigate("apply_job/${job.jobId}") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                ) {
                    Text(
                        text = "Apply Now",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White   // white text on blue button
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
                        Text(
                            text = job.company.take(1).uppercase(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryBlue
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
                    text = job.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Location – black text
                InfoRow(
                    icon = Icons.Default.LocationOn,
                    text = job.location.ifBlank { "Location not specified" },
                    textColor = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Salary – real field from job
                InfoRow(
                    icon = Icons.Default.MonetizationOn,
                    text = job.salary.ifBlank { "Salary not specified" },
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
                    text = job.description.ifBlank { "No description provided." },
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

                if (job.requiredSkills.isEmpty()) {
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
                        job.requiredSkills.forEach { skill ->
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

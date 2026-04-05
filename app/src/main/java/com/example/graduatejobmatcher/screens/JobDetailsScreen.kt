package com.example.graduatejobmatcher.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            Button(
                onClick = { navController.navigate("apply_job/${job.jobId}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
            ) {
                Text("Apply Now", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
            // --- HERO HEADER & OVERLAPPING LOGO ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // Grey Top Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.75f)
                        .background(Color(0xFFD9DBE1))
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(top = 12.dp, start = 8.dp)
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }

                // Company Logo Card (Floating)
                Card(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.BottomCenter),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        // Logo Placeholder
                        Text(job.company.take(1), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = primaryBlue)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- JOB HEADER INFO ---
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

                InfoRow(icon = Icons.Default.LocationOn, text = job.location)
                Spacer(modifier = Modifier.height(8.dp))
                // Assuming salary exists in your model, or use placeholder
                InfoRow(icon = Icons.Default.MonetizationOn, text = "$155,000 - $185,000")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- DESCRIPTION SECTION ---
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text("Job Description", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = job.description,
                    color = Color.DarkGray,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- SKILLS SECTION ---
                Text("Required Skills", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Placeholder skills - you can link this to your model later
                    DetailSkillChip("Python")
                    DetailSkillChip("SQL")
                    DetailSkillChip("Java")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun DetailSkillChip(label: String) {
    Surface(
        color = Color(0xFFE8EAF6), // Light blue-grey
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
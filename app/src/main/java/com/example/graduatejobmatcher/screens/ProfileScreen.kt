package com.example.graduatejobmatcher.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.viewmodel.AppViewModel

@Composable
fun ProfileScreen(navController: NavController, viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val primaryBlue = Color(0xFF3F51B5)

    Scaffold(
        bottomBar = {
            Button(
                onClick = { /* Handle Update */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
            ) {
                Text("Update Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // --- BLUE HEADER & PROFILE IMAGE ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                // Blue background with rounded bottom corners
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f) // Takes up 80% of the Box height
                        .align(Alignment.TopCenter)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                        .background(primaryBlue)
                )

                // Profile Picture (Circular with Border)
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .border(4.dp, Color.White, CircleShape)
                ) {
                    // Replace with actual image loading logic (e.g., Coil)
                    // Image(painter = ..., contentDescription = null, contentScale = ContentScale.Crop)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- USER INFO ---
            Text(
                text = currentUser?.name ?: "Alex Chen",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = currentUser?.email ?: "arcrem@gmail.com",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- SKILLS SECTION ---
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text("Skills", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SkillChip("C++")
                    SkillChip("Python")
                    SkillChip("Figma")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- EDUCATION SECTION ---
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text("Education", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))

                EducationItem("B.S. Computer Science", "June 2024")
                Spacer(modifier = Modifier.height(16.dp))
                EducationItem("B.S. Computer Science", "June 2024")
            }
        }
    }
}

@Composable
fun SkillChip(label: String) {
    Surface(
        color = Color(0xFFE0E0E0),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
fun EducationItem(degree: String, date: String) {
    Column {
        Text(text = degree, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Text(text = date, color = Color.Gray, fontSize = 14.sp)
    }
}
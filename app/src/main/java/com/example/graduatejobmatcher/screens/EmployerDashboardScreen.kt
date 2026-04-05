package com.example.graduatejobmatcher.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun EmployerDashboardScreen(navController: NavController, viewModel: AppViewModel) {
    val primaryBlue = Color(0xFF3F51B5)
    val lightGrayBg = Color(0xFFF5F7FA)

    Scaffold(
        bottomBar = { EmployerBottomNav(navController) },
        containerColor = lightGrayBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // --- HEADER SECTION ---
            Box(modifier = Modifier.height(260.dp)) {
                // Blue Background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.75f)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                        .background(primaryBlue)
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Spacer(modifier = Modifier.width(1.dp))
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "Welcome ABC Corp",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterStart).padding(bottom = 40.dp)
                    )
                }

                // --- OVERLAPPING STATS CARD ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .align(Alignment.BottomCenter),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem("Active Jobs", "12")
                            VerticalDivider(modifier = Modifier.height(40.dp).width(1.dp))
                            StatItem("Applications", "45")
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { navController.navigate("post_job") },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                        ) {
                            Text("Post Job", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- MENU CARDS ---
            MenuSectionCard(
                title = "View Applicants",
                items = listOf("View Applicants", "Manage Listings"),
                icon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(16.dp))

            MenuSectionCard(
                title = "Manage Job Listings",
                items = listOf("Software Intern", "Graphic Designer"),
                icon = Icons.AutoMirrored.Filled.List
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun StatItem(label: String, count: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray)
        Text(text = count, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
    }
}

@Composable
fun MenuSectionCard(title: String, items: List<String>, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
            Spacer(modifier = Modifier.height(8.dp))
            items.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = item, fontSize = 14.sp, color = Color.DarkGray)
                }
            }
        }
    }
}

@Composable
fun EmployerBottomNav(navController: NavController) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") },
            selected = true,
            onClick = { /* Stay on dashboard */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, null) },
            label = { Text("View") },
            selected = false,
            onClick = { /* Navigate to job list or applicants */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Email, null) },
            label = { Text("Apps") },
            selected = false,
            onClick = { /* Navigate to applications */ }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AccountCircle, null) },
            label = { Text("Profile") },
            selected = false,
            onClick = { navController.navigate("profile") }  // ✅ Connects to ProfileScreen
        )
    }
}
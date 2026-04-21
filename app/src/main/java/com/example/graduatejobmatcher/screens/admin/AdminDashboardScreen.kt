package com.example.graduatejobmatcher.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.graduatejobmatcher.navigation.Screen
import com.example.graduatejobmatcher.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    navController: NavController,
    viewModel: AppViewModel
) {
    val primaryBlue    = Color(0xFF3F51B5)
    val backgroundColor = Color(0xFFF5F7FA)

    var pendingCount   by remember { mutableStateOf("...") }
    var approvedCount  by remember { mutableStateOf("...") }
    var rejectedCount  by remember { mutableStateOf("...") }
    var usersCount by remember { mutableStateOf("...") }

    DisposableEffect(Unit) {
        val registrations = viewModel.listenAdminReport { report ->
            pendingCount = report.pendingJobs.size.toString()
            approvedCount = report.approvedJobs.size.toString()
            rejectedCount = report.rejectedJobs.size.toString()
            usersCount = report.users.size.toString()
        }
        onDispose { registrations.forEach { it.remove() } }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard", color = Color.White, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                    }
                },
                actions = {
                    // ✅ Avatar — tap to go to Profile
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF9C27B0))
                            .clickable { navController.navigate(Screen.Profile.route) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = primaryBlue)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(backgroundColor),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "Welcome back, Admin!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.Black
                )
                Text(
                    text = "Here's what's happening with the platform.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Pending Jobs",
                            count = pendingCount,
                            subtext = "Needs review",
                            icon = Icons.Default.PendingActions,
                            containerColor = Color(0xFFFFF9C4),
                            iconColor = Color(0xFFFBC02D),
                            onClick = { navController.navigate(Screen.PendingJobs.route) }
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Approved Jobs",
                            count = approvedCount,
                            subtext = "Live on platform",
                            icon = Icons.Default.CheckCircle,
                            containerColor = Color(0xFFE8F5E9),
                            iconColor = Color(0xFF4CAF50),
                            onClick = { navController.navigate(Screen.ApprovedJobs.route) }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Rejected",
                            count = rejectedCount,
                            subtext = "This month",
                            icon = Icons.Default.Cancel,
                            containerColor = Color(0xFFFFEBEE),
                            iconColor = Color(0xFFF44336),
                            onClick = { navController.navigate(Screen.RejectedJobs.route) }
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            title = "Total Users",
                            count = usersCount,
                            subtext = "Registered",
                            icon = Icons.Default.Group,
                            containerColor = Color(0xFFF3E5F5),
                            iconColor = Color(0xFF9C27B0),
                            onClick = { navController.navigate(Screen.ManageUsers.route) }
                        )
                    }

                    GenerateReportCard(
                        title = "Generate All Report",
                        subtext = "Pending, approved, rejected jobs and total users",
                        icon = Icons.Default.Assessment,
                        containerColor = Color.White,
                        iconColor = primaryBlue,
                        onClick = { navController.navigate(Screen.AdminReport.route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GenerateReportCard(
    title: String,
    subtext: String,
    icon: ImageVector,
    containerColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8EEFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtext, color = Color.Gray, fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    count: String,
    subtext: String,
    icon: ImageVector,
    containerColor: Color,
    iconColor: Color,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
            Column {
                Text(
                    text = count,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Text(
                    text = subtext,
                    fontSize = 11.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

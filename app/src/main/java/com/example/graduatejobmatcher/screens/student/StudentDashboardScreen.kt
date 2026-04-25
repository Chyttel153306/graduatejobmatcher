package com.example.graduatejobmatcher.screens.student

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.User
import com.example.graduatejobmatcher.navigation.Screen
import com.example.graduatejobmatcher.ui.theme.components.JobCard
import com.example.graduatejobmatcher.ui.theme.components.UserAvatar
import com.example.graduatejobmatcher.viewmodel.AppViewModel

@Composable
fun StudentDashboardScreen(navController: NavController, viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val primaryBlue = Color(0xFF3F51B5)

    var selectedItem by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var unreadNotificationCount by remember { mutableIntStateOf(0) }
    var employerProfiles by remember { mutableStateOf<Map<String, User>>(emptyMap()) }

    LaunchedEffect(Unit) {
        if (currentUser == null) {
            viewModel.fetchCurrentUser()
        }
    }

    DisposableEffect(Unit) {
        val registration = viewModel.listenJobsByStatus("approved") { approvedJobs ->
            viewModel.jobs.clear()
            viewModel.jobs.addAll(approvedJobs)
        }
        onDispose { registration.remove() }
    }

    val studentId = viewModel.getCurrentUserId().orEmpty()

    DisposableEffect(studentId) {
        if (studentId.isBlank()) {
            onDispose { }
        } else {
            val registration = viewModel.listenNotificationsForUser(studentId) { notifications ->
                unreadNotificationCount = notifications.count { !it.isRead }
            }
            onDispose { registration.remove() }
        }
    }

    LaunchedEffect(viewModel.jobs.toList()) {
        val employerIds = viewModel.jobs
            .map { it.employerId }
            .filter { it.isNotBlank() }
            .distinct()

        employerIds.forEach { employerId ->
            if (!employerProfiles.containsKey(employerId)) {
                viewModel.getUserById(employerId) { employer ->
                    if (employer != null) {
                        employerProfiles = employerProfiles + (employerId to employer)
                    }
                }
            }
        }
    }

    val filteredJobs = remember(viewModel.jobs, searchQuery) {
        if (searchQuery.isBlank()) {
            viewModel.jobs
        } else {
            viewModel.jobs.filter { job ->
                job.title.contains(searchQuery, ignoreCase = true) ||
                    job.company.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = selectedItem == 1,
                    onClick = {
                        selectedItem = 1
                        navController.navigate("profile")
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(primaryBlue)
                    .padding(24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UserAvatar(
                            user = currentUser,
                            modifier = Modifier.size(40.dp),
                            backgroundColor = Color.White.copy(alpha = 0.2f),
                            textSize = 14.sp
                        )

                        Box {
                            IconButton(
                                onClick = { navController.navigate(Screen.StudentNotifications.route) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White
                                )
                            }

                            if (unreadNotificationCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .align(Alignment.TopEnd)
                                        .clip(CircleShape)
                                        .background(Color.Red)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Hello, ${currentUser?.name ?: "Student"}!",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Job Recommendations",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    text = "Job recommendation is alive",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Job", color = Color.Black) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredJobs) { job ->
                        val employerName = employerProfiles[job.employerId]?.name ?: job.company

                        JobCard(
                            title = job.title,
                            company = job.company,
                            employerName = employerName,
                            employerProfileImageBase64 = employerProfiles[job.employerId]?.profileImageBase64.orEmpty(),
                            modifier = Modifier.fillMaxWidth(),
                            color = primaryBlue,
                            onClick = {
                                navController.navigate("job_details/${job.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

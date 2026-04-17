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
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.ReportGmailerrorred
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WorkOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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

private val RejectedPrimaryBlue = Color(0xFF3F51B5)
private val RejectedRed = Color(0xFFE53935)
private val RejectedBackgroundGrey = Color(0xFFF5F7FA)
private val RejectedCardWhite = Color(0xFFFFFFFF)

private val RejectedAvatarColors = listOf(
    Color(0xFFEF5350), Color(0xFFE57373), Color(0xFF8E24AA),
    Color(0xFF5C6BC0), Color(0xFF00897B), Color(0xFFF4511E),
    Color(0xFF6D4C41), Color(0xFF546E7A)
)

private fun rejectedAvatarColor(text: String): Color =
    RejectedAvatarColors[text.firstOrNull()?.code?.rem(RejectedAvatarColors.size) ?: 0]

private fun rejectedFormatDate(job: Job): String = job.postedDate?.let {
    java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(it)
} ?: "-"

private fun rejectedDeadline(job: Job): String = job.deadline?.let {
    java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(it)
} ?: "-"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RejectedJobsScreen(
    navController: NavController,
    viewModel: AppViewModel
) {
    var rejectedJobs by remember { mutableStateOf<List<Job>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var sortNewest by remember { mutableStateOf(true) }
    var showSortMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getRejectedJobs { rejectedJobs = it }
    }

    val filtered = rejectedJobs
        .filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                it.company.contains(searchQuery, ignoreCase = true)
        }
        .let { jobs ->
            if (sortNewest) jobs.sortedByDescending { it.postedDate } else jobs.sortedBy { it.postedDate }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Rejected Job Posts",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = RejectedPrimaryBlue)
            )
        },
        containerColor = RejectedBackgroundGrey
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
                placeholder = { Text("Search rejected jobs...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RejectedRed,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = RejectedCardWhite,
                    unfocusedContainerColor = RejectedCardWhite,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
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
                    text = "${filtered.size} rejected post${if (filtered.size != 1) "s" else ""}",
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )
                Box {
                    TextButton(onClick = { showSortMenu = true }) {
                        Text(
                            text = "Sort: ${if (sortNewest) "Newest" else "Oldest"}",
                            fontSize = 13.sp,
                            color = RejectedRed,
                            fontWeight = FontWeight.SemiBold
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

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.ReportGmailerrorred,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No rejected jobs", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered, key = { it.jobId }) { job ->
                        RejectedJobCard(
                            job = job,
                            onClick = { navController.navigate(Screen.JobDetails.passJobId(job.jobId)) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }
}

@Composable
private fun RejectedJobCard(
    job: Job,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RejectedCardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(rejectedAvatarColor(job.title), RoundedCornerShape(12.dp)),
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
                    color = Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = job.jobType.ifBlank { "Full-time" },
                        fontSize = 11.sp,
                        color = RejectedRed,
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
                        text = rejectedFormatDate(job),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                if (job.deadline != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.EventBusy,
                            contentDescription = null,
                            tint = RejectedRed,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 2.dp))
                        Text(
                            text = rejectedDeadline(job),
                            fontSize = 12.sp,
                            color = RejectedRed,
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
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    tint = RejectedRed,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.padding(horizontal = 3.dp))
                Text(
                    text = "Rejected and hidden from students",
                    color = RejectedRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

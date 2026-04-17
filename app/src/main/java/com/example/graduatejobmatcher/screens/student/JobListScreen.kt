package com.example.graduatejobmatcher.screens.student

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.ui.theme.components.JobCard
import com.example.graduatejobmatcher.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobListScreen(navController: NavController, viewModel: AppViewModel) {
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadJobs()
    }

    Scaffold(
        containerColor = Color(0xFFFBFBFB) // Slightly off-white background from image
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- SEARCH BAR ---
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search Bar", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF1F1F1),
                    unfocusedContainerColor = Color(0xFFF1F1F1),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- FILTER SECTION ---
            Text(
                text = "Filter by",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips Row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterDropdownChip("Job Type")
                FilterDropdownChip("Location")
            }
            Spacer(modifier = Modifier.height(8.dp))
            FilterDropdownChip("Industry")

            Spacer(modifier = Modifier.height(24.dp))

            // --- JOB LIST ---
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(viewModel.jobs) { job ->
                    // We use the modifier to make the card clickable
                    // and fill width as seen in the image
                    JobCard(
                        title = job.title,
                        company = job.company,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp), // Height adjusted for row-style card
                        color = Color(0xFF3F51B5)
                    )
                }
            }
        }
    }
}

@Composable
fun FilterDropdownChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        border = AssistChipDefaults.assistChipBorder(enabled = true),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

package com.example.graduatejobmatcher.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.Job
import com.example.graduatejobmatcher.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(navController: NavController, viewModel: AppViewModel) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    val primaryBlue = Color(0xFF3F51B5)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Post New Job", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = primaryBlue)
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    val job = Job(
                        title = title,
                        description = description,
                        company = "Your Company", // Or get from viewModel
                        location = location,
                        employerId = viewModel.getCurrentUserId() ?: ""
                    )
                    viewModel.postJob(job)
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
            ) {
                Text("Submit Job Post", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Reusable field component to match the "Label above Field" style
            PostJobField(label = "Job Title", value = title, onValueChange = { title = it })

            PostJobField(
                label = "Job Description",
                value = description,
                onValueChange = { description = it },
                singleLine = false,
                modifier = Modifier.height(120.dp)
            )

            PostJobField(label = "Required Skills", value = skills, onValueChange = { skills = it })

            PostJobField(label = "Salary", value = salary, onValueChange = { salary = it })

            PostJobField(
                label = "Location",
                value = location,
                onValueChange = { location = it },
                trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(80.dp)) // Padding for the bottom button
        }
    }
}

@Composable
fun PostJobField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F1F1),
                unfocusedContainerColor = Color(0xFFF1F1F1),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = singleLine,
            trailingIcon = trailingIcon
        )
    }
}
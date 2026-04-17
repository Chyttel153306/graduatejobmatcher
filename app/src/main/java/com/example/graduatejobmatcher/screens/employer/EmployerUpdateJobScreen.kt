package com.example.graduatejobmatcher.screens.employer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.Job
import com.example.graduatejobmatcher.viewmodel.AppViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerUpdateJobScreen(
    navController: NavController,
    viewModel: AppViewModel,
    jobId: String
) {
    var existingJob by remember { mutableStateOf<Job?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var skillsInput by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val primaryBlue = Color(0xFF3F51B5)

    LaunchedEffect(jobId) {
        viewModel.getJobById(jobId) { job ->
            existingJob = job
            if (job != null) {
                title = job.title
                description = job.description
                skillsInput = job.requiredSkills.joinToString(", ")
                salary = job.salary
                location = job.location
            }
        }
    }

    val job = existingJob
    if (job == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = primaryBlue)
        }
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Update Job",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = primaryBlue
                )
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        val updatedJob = job.copy(
                            title = title.trim(),
                            description = description.trim(),
                            salary = salary.trim(),
                            location = location.trim(),
                            requiredSkills = skillsInput
                                .split(",")
                                .map { it.trim() }
                                .filter { it.isNotEmpty() }
                        )

                        isSaving = true
                        viewModel.updateJob(updatedJob) {
                            isSaving = false
                            navController.popBackStack()
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue
                    )
                ) {
                    Text(
                        if (isSaving) "Saving..." else "Save Job Updates",
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
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            PostJobField(
                label = "Job Title",
                value = title,
                onValueChange = { title = it }
            )

            PostJobField(
                label = "Job Description",
                value = description,
                onValueChange = { description = it },
                singleLine = false,
                modifier = Modifier.height(120.dp)
            )

            PostJobField(
                label = "Required Skills (comma-separated)",
                value = skillsInput,
                onValueChange = { skillsInput = it }
            )

            PostJobField(
                label = "Salary",
                value = salary,
                onValueChange = { salary = it }
            )

            PostJobField(
                label = "Location",
                value = location,
                onValueChange = { location = it },
                trailingIcon = {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                }
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

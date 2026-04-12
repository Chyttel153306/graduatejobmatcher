package com.example.graduatejobmatcher.screens.employer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(navController: NavController, viewModel: AppViewModel) {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var skillsInput by remember { mutableStateOf("") }  // comma-separated string
    var salary by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    val primaryBlue = Color(0xFF3F51B5)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Post New Job",
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
        bottomBar = {
            // ✅ White background for the whole bottom bar area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            // Convert comma-separated skills string to List<String>
                            val skillsList = skillsInput.split(",")
                                .map { it.trim() }
                                .filter { it.isNotEmpty() }

                            val job = Job(
                                jobId = "",          // Firestore will auto-generate
                                title = title,
                                description = description,
                                company = viewModel.currentUser.value?.name ?: "Your Company",
                                location = location,
                                salary = salary,
                                requiredSkills = skillsList,
                                jobType = "Full-time",   // default, can be extended
                                status = "pending",
                                postedDate = Date(),
                                deadline = null,
                                employerId = viewModel.getCurrentUserId() ?: ""
                            )

                            viewModel.postJob(job)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue
                    )
                ) {
                    Text(
                        "Submit Job Post",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White   // ✅ explicit white text
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
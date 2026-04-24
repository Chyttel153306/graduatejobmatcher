package com.example.graduatejobmatcher.screens.employer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.Application
import com.example.graduatejobmatcher.model.InterviewSchedule
import com.example.graduatejobmatcher.model.Job
import com.example.graduatejobmatcher.model.User
import com.example.graduatejobmatcher.navigation.Screen
import com.example.graduatejobmatcher.ui.theme.components.UserAvatar
import com.example.graduatejobmatcher.viewmodel.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleInterviewScreen(
    navController: NavController,
    viewModel: AppViewModel,
    applicationId: String
) {
    var application by remember { mutableStateOf<Application?>(null) }
    var candidate by remember { mutableStateOf<User?>(null) }
    var job by remember { mutableStateOf<Job?>(null) }
    var existingInterview by remember { mutableStateOf<InterviewSchedule?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    var date by remember {
        mutableStateOf(SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(calendar.time))
    }
    var time by remember {
        mutableStateOf(SimpleDateFormat("h:mm a", Locale.getDefault()).format(calendar.time))
    }
    var interviewType by remember { mutableStateOf("Online (Google Meet)") }
    var meetingLink by remember { mutableStateOf("meet.google.com/abc-defg-hij") }
    var message by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(applicationId) {
        viewModel.getApplicationById(applicationId) { app ->
            application = app

            if (app == null) {
                isLoading = false
                return@getApplicationById
            }

            var pendingLoads = 3

            fun finishLoad() {
                pendingLoads--
                if (pendingLoads == 0) {
                    isLoading = false
                }
            }

            viewModel.getUserById(app.studentId) { user ->
                candidate = user
                if (message.isBlank()) {
                    val candidateName = user?.name?.substringBefore(" ") ?: "Candidate"
                    message = "Hi $candidateName, we would like to invite you for an interview. Please let us know if this time works for you."
                }
                finishLoad()
            }

            viewModel.getJobById(app.jobId) { fetchedJob ->
                job = fetchedJob
                finishLoad()
            }

            viewModel.getInterviewByApplicationId(app.applicationId) { interview ->
                existingInterview = interview
                if (interview != null) {
                    date = interview.interviewDate
                    time = interview.interviewTime
                    interviewType = interview.interviewType
                    meetingLink = interview.meetingLink
                    message = interview.message
                }
                finishLoad()
            }
        }
    }

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            delay(1200)
            navController.popBackStack()
        }
    }

    val appBlue = Color(0xFF4677EA)
    val pageBg = Color(0xFFF4F6FB)
    val cardBg = Color.White
    val softBlue = Color(0xFFF1F5FF)
    val textDark = Color(0xFF1F2937)
    val textGray = Color(0xFF6B7280)
    val isEditingSchedule = existingInterview != null

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = appBlue)
        }
        return
    }

    val currentApplication = application
    val currentCandidate = candidate
    val currentJob = job

    if (currentApplication == null || currentCandidate == null || currentJob == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Unable to load interview details.")
        }
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = pageBg,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isEditingSchedule) "Edit Interview Schedule" else "Schedule Interview",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = appBlue
                )
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(pageBg)
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            UserAvatar(
                                user = currentCandidate,
                                modifier = Modifier.size(78.dp),
                                backgroundColor = Color(0xFFD9D9D9),
                                textSize = 28.sp
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentCandidate.name.ifBlank { "Candidate" },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    color = textDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = currentCandidate.degree.ifBlank { "Degree not provided" },
                                    color = textGray,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = currentCandidate.institution.ifBlank { "Institution not provided" },
                                    color = textGray,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                navController.navigate(Screen.ApplicantDetails.passApplicationId(currentApplication.applicationId))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = softBlue,
                                contentColor = appBlue
                            )
                        ) {
                            Text(
                                text = "View Profile",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = softBlue)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.BusinessCenter,
                            contentDescription = null,
                            tint = appBlue,
                            modifier = Modifier.size(28.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Position",
                                color = textGray,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = currentJob.title.ifBlank { "Untitled Job" },
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = textDark
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = buildString {
                                    append(currentJob.company.ifBlank { "Company not set" })
                                    append("  •  ")
                                    append(currentJob.jobType.ifBlank { "Not set" })
                                    append("  •  ")
                                    append(currentJob.location.ifBlank { "Location not provided" })
                                },
                                color = textGray,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                SectionTitle("Interview Details")

                Spacer(modifier = Modifier.height(12.dp))

                DetailField(
                    icon = Icons.Default.CalendarMonth,
                    title = "Date",
                    value = date,
                    trailingIcon = Icons.Default.CalendarMonth,
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _, year, month, dayOfMonth ->
                                calendar.set(Calendar.YEAR, year)
                                calendar.set(Calendar.MONTH, month)
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                date = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(calendar.time)
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                DetailField(
                    icon = Icons.Default.Schedule,
                    title = "Time",
                    value = time,
                    trailingIcon = Icons.Default.KeyboardArrowDown,
                    onClick = {
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                calendar.set(Calendar.MINUTE, minute)
                                time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(calendar.time)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                        ).show()
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                DetailField(
                    icon = Icons.Default.VideoCall,
                    title = "Interview Type",
                    value = interviewType,
                    trailingIcon = Icons.Default.KeyboardArrowDown,
                    onValueChange = { interviewType = it }
                )

                Spacer(modifier = Modifier.height(10.dp))

                InputField(
                    icon = Icons.Default.Link,
                    title = "Meeting Link",
                    value = meetingLink,
                    onValueChange = { meetingLink = it },
                    hint = "Add the link where the interview will take place"
                )

                Spacer(modifier = Modifier.height(20.dp))

                SectionTitle("Message to Candidate")

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        BasicTextField(
                            value = message,
                            onValueChange = {
                                if (it.length <= 200) message = it
                            },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                color = textDark,
                                lineHeight = 24.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            decorationBox = { innerTextField ->
                                if (message.isEmpty()) {
                                    Text(
                                        text = "Type your message here...",
                                        color = textGray
                                    )
                                }
                                innerTextField()
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${message.length}/200",
                            modifier = Modifier.align(Alignment.End),
                            color = textGray,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = softBlue)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = appBlue,
                            modifier = Modifier.size(22.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = if (isEditingSchedule) {
                                "Updating the interview will refresh the existing student notification instead of creating another one."
                            } else {
                                "The candidate will receive one notification with the interview details."
                            },
                            color = textDark,
                            fontSize = 16.sp,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val employerId = viewModel.getCurrentUserId().orEmpty()
                        if (employerId.isBlank()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Unable to identify employer.")
                            }
                        } else {
                            isSaving = true
                            viewModel.scheduleInterview(
                                InterviewSchedule(
                                    interviewId = existingInterview?.interviewId.orEmpty(),
                                    applicationId = currentApplication.applicationId,
                                    jobId = currentJob.jobId,
                                    employerId = employerId,
                                    studentId = currentApplication.studentId,
                                    interviewDate = date,
                                    interviewTime = time,
                                    interviewType = interviewType,
                                    meetingLink = meetingLink,
                                    message = message,
                                    createdAt = existingInterview?.createdAt
                                )
                            ) { success, errorMessage ->
                                isSaving = false
                                if (success) {
                                    successMessage = if (isEditingSchedule) {
                                        "Successfully updated interview schedule"
                                    } else {
                                        "Successfully scheduled interview"
                                    }
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(errorMessage)
                                    }
                                }
                            }
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appBlue,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isEditingSchedule) "Edit Schedule Interview" else "Schedule Interview",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (isSaving || successMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.96f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (successMessage == null) {
                            CircularProgressIndicator(color = appBlue)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (isEditingSchedule) "Updating interview schedule..." else "Scheduling interview...",
                                color = Color(0xFF1F2937),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF22C55E),
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = successMessage.orEmpty(),
                                color = Color(0xFF1F2937),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            tint = Color(0xFF4677EA),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF1F2937)
        )
    }
}

@Composable
private fun DetailField(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    onValueChange: ((String) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF4677EA)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color(0xFF6B7280),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (onValueChange == null) {
                    Text(
                        text = value,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                } else {
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
private fun InputField(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F5FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF4677EA)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color(0xFF6B7280),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            Text(
                                text = hint,
                                color = Color(0xFF9CA3AF)
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = hint,
                    color = Color(0xFF9CA3AF),
                    fontSize = 13.sp
                )
            }
        }
    }
}

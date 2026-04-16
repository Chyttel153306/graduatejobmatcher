package com.example.graduatejobmatcher.screens.student

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.Application
import com.example.graduatejobmatcher.navigation.Screen
import com.example.graduatejobmatcher.viewmodel.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Tracks the three possible submission states
private enum class SubmitState { IDLE, LOADING, SUCCESS, FAILED }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyJobScreen(
    navController: NavController,
    viewModel: AppViewModel,
    jobId: String,
    jobTitle: String = "Job"
) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val scope   = rememberCoroutineScope()

    var resumeUri      by remember { mutableStateOf<Uri?>(null) }
    var portfolioUri   by remember { mutableStateOf<Uri?>(null) }
    var coverLetterUri by remember { mutableStateOf<Uri?>(null) }
    var resumeFileName by remember { mutableStateOf<String?>(null) }
    var portfolioFileName by remember { mutableStateOf<String?>(null) }
    var coverLetterFileName by remember { mutableStateOf<String?>(null) }

    var submitState by remember { mutableStateOf(SubmitState.IDLE) }
    var errorMessage by remember { mutableStateOf("") }

    val resumeLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            runCatching {
                contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            resumeUri = it
            resumeFileName = it.displayName(contentResolver)
        }
    }

    val portfolioLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            runCatching {
                contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            portfolioUri = it
            portfolioFileName = it.displayName(contentResolver)
        }
    }

    val coverLetterLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            runCatching {
                contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            coverLetterUri = it
            coverLetterFileName = it.displayName(contentResolver)
        }
    }

    val primaryBlue = Color(0xFF1A73E8)
    val green       = Color(0xFF00C853)
    val red         = Color(0xFFD32F2F)

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        enabled = submitState != SubmitState.LOADING
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Apply for $jobTitle",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            UploadButton(
                label = "Upload Resume",
                selectedUri = resumeUri,
                fileName = resumeFileName,
                accentColor = primaryBlue,
                successColor = green,
                enabled = submitState == SubmitState.IDLE,
                onClick = { resumeLauncher.launch(arrayOf("*/*")) }
            )

            UploadButton(
                label = "Upload Portfolio",
                selectedUri = portfolioUri,
                fileName = portfolioFileName,
                accentColor = primaryBlue,
                successColor = green,
                enabled = submitState == SubmitState.IDLE,
                onClick = { portfolioLauncher.launch(arrayOf("*/*")) }
            )

            UploadButton(
                label = "Upload Cover Letter",
                selectedUri = coverLetterUri,
                fileName = coverLetterFileName,
                accentColor = primaryBlue,
                successColor = green,
                enabled = submitState == SubmitState.IDLE,
                onClick = { coverLetterLauncher.launch(arrayOf("*/*")) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Status feedback card (hidden when IDLE)
            if (submitState != SubmitState.IDLE) {
                StatusCard(
                    submitState = submitState,
                    green = green,
                    red = red,
                    failureMessage = errorMessage
                )
            }

            // ── Submit button
            Button(
                onClick = {
                    val studentId = viewModel.getCurrentUserId()
                    if (studentId == null) {
                        Toast.makeText(context, "Not logged in", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (resumeUri == null) {
                        Toast.makeText(context, "Please upload your resume", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    scope.launch {
                        submitState = SubmitState.LOADING
                        errorMessage = ""
                        try {
                            val application = Application(
                                jobId          = jobId,
                                studentId      = studentId,
                                status         = "pending",
                                resumeUrl      = resumeUri.toString(),
                                portfolioUrl   = portfolioUri?.toString() ?: "",
                                coverLetterUrl = coverLetterUri?.toString() ?: ""
                            )
                            viewModel.applyJob(application)
                            submitState = SubmitState.SUCCESS

                            // Show success briefly, then go to student dashboard
                            delay(1500)
                            navController.navigate(Screen.StudentDashboard.route) {
                                popUpTo(Screen.StudentDashboard.route) { inclusive = true }
                            }
                        } catch (e: Exception) {
                            submitState = SubmitState.FAILED
                            errorMessage = e.message ?: "Submission failed. Please try again."
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                enabled = submitState == SubmitState.IDLE
            ) {
                if (submitState == SubmitState.LOADING) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Submit Application",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
            }

            // ── Retry button shown only on failure
            if (submitState == SubmitState.FAILED) {
                OutlinedButton(
                    onClick = { submitState = SubmitState.IDLE },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = red),
                    border = BorderStroke(1.5.dp, red)
                ) {
                    Text("Try Again", fontWeight = FontWeight.SemiBold, color = red)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Inline status card
private fun Uri.displayName(contentResolver: android.content.ContentResolver): String {
    return runCatching {
        contentResolver.query(this, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0 && cursor.moveToFirst()) {
                    cursor.getString(nameIndex)
                } else {
                    lastPathSegment?.substringAfterLast('/') ?: "File Selected"
                }
            } ?: (lastPathSegment?.substringAfterLast('/') ?: "File Selected")
    }.getOrDefault("File Selected")
}

@Composable
private fun StatusCard(
    submitState: SubmitState,
    green: Color,
    red: Color,
    failureMessage: String
) {
    val bgColor   = when (submitState) {
        SubmitState.LOADING -> Color(0xFFE3F2FD)
        SubmitState.SUCCESS -> Color(0xFFE8F5E9)
        SubmitState.FAILED  -> Color(0xFFFFEBEE)
        else                -> Color.Transparent
    }
    val textColor = when (submitState) {
        SubmitState.LOADING -> Color(0xFF1A73E8)
        SubmitState.SUCCESS -> green
        SubmitState.FAILED  -> red
        else                -> Color.Transparent
    }
    val message = when (submitState) {
        SubmitState.LOADING -> "Submitting your application..."
        SubmitState.SUCCESS -> "Application submitted successfully!"
        SubmitState.FAILED  -> failureMessage.ifBlank { "Submission failed. Please try again." }
        else                -> ""
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (submitState) {
                SubmitState.LOADING ->
                    CircularProgressIndicator(
                        color = textColor,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                SubmitState.SUCCESS ->
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = green)
                SubmitState.FAILED ->
                    Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = red)
                else -> {}
            }
            Text(
                text = message,
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun UploadButton(
    label: String,
    selectedUri: Uri?,
    fileName: String?,
    accentColor: Color,
    successColor: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val color = if (selectedUri != null) successColor else accentColor

    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color),
        border = BorderStroke(1.5.dp, color)
    ) {
        Icon(
            imageVector = if (selectedUri != null) Icons.Filled.CheckCircle else Icons.Filled.FileUpload,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (selectedUri != null) (fileName ?: "File Selected") else label,
            color = color,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

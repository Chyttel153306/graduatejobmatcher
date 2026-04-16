package com.example.graduatejobmatcher.screens.employer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.Application
import com.example.graduatejobmatcher.navigation.Screen
import com.example.graduatejobmatcher.model.User
import com.example.graduatejobmatcher.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewApplicantsScreen(
    navController: NavController,
    viewModel: AppViewModel,
    applicationId: String
) {
    var application by remember { mutableStateOf<Application?>(null) }
    var user by remember { mutableStateOf<User?>(null) }
    var loading by remember { mutableStateOf(true) }
    var currentStatus by remember { mutableStateOf("pending") }

    LaunchedEffect(applicationId) {
        viewModel.getApplicationById(applicationId) { app ->
            application = app
            currentStatus = app?.status ?: "pending"

            if (app != null) {
                viewModel.getUserById(app.studentId) { u ->
                    user = u
                    loading = false
                }
            } else {
                loading = false
            }
        }
    }

    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val applicantName = user?.name?.ifBlank { "Applicant Name" } ?: "Applicant Name"
    val applicantEmail = user?.email?.ifBlank { "No email provided" } ?: "No email provided"
    val applicantDegree = user?.degree?.ifBlank { "Degree not provided" } ?: "Degree not provided"
    val applicantInstitution = user?.institution?.ifBlank { "Institution not provided" } ?: "Institution not provided"
    val applicantLocation = user?.location?.ifBlank { "Location not provided" } ?: "Location not provided"
    val applicantAbout = user?.bio?.ifBlank {
        "No applicant summary has been added yet."
    } ?: "No applicant summary has been added yet."
    val applicantSkills = if (user?.skills.isNullOrEmpty()) {
        listOf("No skills added")
    } else {
        user?.skills ?: emptyList()
    }
    val educationSubtitle = user?.graduationDate?.ifBlank { "Graduation date not provided" }
        ?: "Graduation date not provided"
    val applicantExperience = user?.experience?.ifBlank { "Experience not provided" } ?: "Experience not provided"
    val resumeLabel = if (application?.resumeUrl.isNullOrBlank()) "Resume not uploaded" else "Resume link available"
    val coverLetterLabel = if (application?.coverLetterUrl.isNullOrBlank()) "Cover letter not uploaded" else "Cover letter link available"
    val portfolioLabel = if (application?.portfolioUrl.isNullOrBlank()) "Portfolio not uploaded" else "Portfolio link available"

    val primaryBlue = Color(0xFF3D73E6)
    val pageBg = Color(0xFFF5F7FB)
    val cardBg = Color.White
    val grayText = Color(0xFF6B7280)
    val greenColor = Color(0xFF16A34A)
    val redColor = Color(0xFFDC2626)
    val lightGreenBg = Color(0xFFECFDF3)
    val lightRedBg = Color(0xFFFEF2F2)

    Scaffold(
        containerColor = pageBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Applicant Details",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryBlue
                )
            )
        },
        bottomBar = {
            Surface(
                color = cardBg,
                tonalElevation = 6.dp,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedActionButton(
                        modifier = Modifier.weight(1f),
                        text = "Reject",
                        borderColor = redColor,
                        textColor = redColor,
                        backgroundColor = lightRedBg
                    ) {
                        currentStatus = "Rejected"
                        viewModel.updateApplicationStatus(applicationId, "rejected")
                    }

                    OutlinedActionButton(
                        modifier = Modifier.weight(1f),
                        text = "Accept",
                        borderColor = greenColor,
                        textColor = greenColor,
                        backgroundColor = lightGreenBg
                    ) {
                        currentStatus = "Accepted"
                        viewModel.updateApplicationStatus(applicationId, "accepted")
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .background(pageBg)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    ApplicantHeader(
                        applicantName = applicantName,
                        applicantDegree = applicantDegree,
                        applicantInstitution = applicantInstitution
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoRow(
                            icon = Icons.Default.LocationOn,
                            text = applicantLocation,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        InfoRow(
                            icon = Icons.Default.Email,
                            text = applicantEmail,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    SectionTitle("About")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = applicantAbout,
                        color = Color(0xFF374151),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    SectionTitle("Skills")
                    Spacer(modifier = Modifier.height(10.dp))
                    SkillsSection(skills = applicantSkills)

                    Spacer(modifier = Modifier.height(22.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DetailCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.School,
                            title = "Education",
                            line1 = applicantDegree,
                            line2 = applicantInstitution,
                            line3 = educationSubtitle,
                            iconColor = primaryBlue
                        )

                        DetailCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.Work,
                            title = "Experience",
                            line1 = applicantExperience,
                            line2 = applicantLocation,
                            line3 = "Status: ${currentStatus.replaceFirstChar { it.uppercase() }}",
                            iconColor = primaryBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    SectionTitle("Resume")
                    Spacer(modifier = Modifier.height(10.dp))
                    FileCard(
                        title = "Resume",
                        fileLabel = resumeLabel,
                        fileInfo = application?.resumeUrl?.ifBlank { "No URL available" } ?: "No URL available",
                        onViewClick = {
                            application?.resumeUrl
                                ?.takeIf { it.isNotBlank() }
                                ?.let { navController.navigate(Screen.ResumePreview.passResumeUrl(it)) }
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    SectionTitle("Cover Letter")
                    Spacer(modifier = Modifier.height(10.dp))
                    FileCard(
                        title = "Cover Letter",
                        fileLabel = coverLetterLabel,
                        fileInfo = application?.coverLetterUrl?.ifBlank { "No URL available" } ?: "No URL available",
                        onViewClick = {
                            application?.coverLetterUrl
                                ?.takeIf { it.isNotBlank() }
                                ?.let { navController.navigate(Screen.ResumePreview.passResumeUrl(it)) }
                        }
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    SectionTitle("Portfolio")
                    Spacer(modifier = Modifier.height(10.dp))
                    FileCard(
                        title = "Portfolio",
                        fileLabel = portfolioLabel,
                        fileInfo = application?.portfolioUrl?.ifBlank { "No URL available" } ?: "No URL available",
                        onViewClick = {
                            application?.portfolioUrl
                                ?.takeIf { it.isNotBlank() }
                                ?.let { navController.navigate(Screen.ResumePreview.passResumeUrl(it)) }
                        }
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    Text(
                        text = "Status: ${currentStatus.replaceFirstChar { it.uppercase() }}",
                        color = when (currentStatus.lowercase()) {
                            "accepted" -> greenColor
                            "interview_scheduled" -> primaryBlue
                            "rejected" -> redColor
                            else -> grayText
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ApplicantHeader(
    applicantName: String,
    applicantDegree: String,
    applicantInstitution: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFF3D73E6)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = applicantName.take(1).uppercase(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = applicantName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = applicantDegree,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = applicantInstitution,
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color(0xFF6B7280),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SkillsSection(skills: List<String>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        skills.forEach { skill ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF3F4F6))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = skill,
                    color = Color(0xFF374151),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun DetailCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    line1: String,
    line2: String,
    line3: String,
    iconColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = line1, color = Color(0xFF374151), fontSize = 14.sp)
            Text(text = line2, color = Color(0xFF374151), fontSize = 14.sp)
            Text(text = line3, color = Color(0xFF374151), fontSize = 14.sp)
        }
    }
}

@Composable
private fun FileCard(
    title: String,
    fileLabel: String,
    fileInfo: String,
    onViewClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = Color(0xFF3D73E6),
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF111827)
                )
                Text(
                    text = fileLabel,
                    color = Color(0xFF374151),
                    fontSize = 13.sp
                )
                Text(
                    text = fileInfo,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            TextButton(
                onClick = onViewClick,
                enabled = fileInfo != "No URL available"
            ) {
                Text("View")
            }
        }
    }
}

@Composable
private fun OutlinedActionButton(
    modifier: Modifier = Modifier,
    text: String,
    borderColor: Color,
    textColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold
        )
    }
}

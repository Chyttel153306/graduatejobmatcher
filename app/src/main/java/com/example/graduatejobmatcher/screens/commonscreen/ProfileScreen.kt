package com.example.graduatejobmatcher.screens.commonscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.navigation.Screen
import com.example.graduatejobmatcher.viewmodel.AppViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: AppViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val primaryBlue = Color(0xFF3F51B5)
    val role = currentUser?.role?.trim()?.lowercase() ?: ""
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showEditDialog by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var degree by remember { mutableStateOf("") }
    var institution by remember { mutableStateOf("") }
    var graduationDate by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var skillsInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.fetchCurrentUser() }

    fun startEditing() {
        currentUser?.let { user ->
            fullName = user.name
            degree = user.degree
            institution = user.institution
            graduationDate = user.graduationDate
            location = user.location
            bio = user.bio
            experience = user.experience
            skillsInput = user.skills.joinToString(", ")
            showEditDialog = true
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    if (role == "student" || role == "employer") {
                        IconButton(onClick = { startEditing() }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryBlue,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->

        if (currentUser == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryBlue)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                        .align(Alignment.TopCenter)
                        .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                        .background(primaryBlue)
                )
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFBBDEFB))
                        .border(4.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser?.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = currentUser?.name ?: "User",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = currentUser?.email ?: "",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val (badgeBg, badgeFg) = when (role) {
                    "admin" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
                    "employer" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                    else -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
                }
                Surface(shape = RoundedCornerShape(50), color = badgeBg) {
                    Text(
                        text = (currentUser?.role ?: "").replaceFirstChar { it.uppercase() },
                        color = badgeFg,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (role) {
                "student" -> {
                    ProfileCard {
                        ProfileSectionTitle(Icons.Default.Person, "Personal Info")
                        Spacer(Modifier.height(10.dp))
                        ProfileInfoRow("Full Name", currentUser?.name ?: "-")
                        ProfileInfoRow("Email", currentUser?.email ?: "-")
                        ProfileInfoRow("User ID", currentUser?.userId ?: "-")
                        ProfileInfoRow("Location", currentUser?.location.ifBlankOrDash())
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileCard {
                        ProfileSectionTitle(Icons.Default.School, "Education")
                        Spacer(Modifier.height(10.dp))
                        ProfileInfoRow("Degree", currentUser?.degree.ifBlankOrDash())
                        ProfileInfoRow("Institution", currentUser?.institution.ifBlankOrDash())
                        ProfileInfoRow("Graduation", currentUser?.graduationDate.ifBlankOrDash())
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileCard {
                        ProfileSectionTitle(Icons.Default.Work, "About")
                        Spacer(Modifier.height(10.dp))
                        ProfileInfoRow("Experience", currentUser?.experience.ifBlankOrDash())
                        ProfileInfoRow("Bio", currentUser?.bio.ifBlankOrDash())
                    }

                    val studentSkills = currentUser?.skills ?: emptyList()
                    if (studentSkills.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        ProfileCard {
                            ProfileSectionTitle(Icons.Default.Work, "Skills")
                            Spacer(Modifier.height(10.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                studentSkills.forEach { skill ->
                                    SkillChip(label = skill, textColor = Color.Black)
                                }
                            }
                        }
                    }
                }

                "employer" -> {
                    ProfileCard {
                        ProfileSectionTitle(Icons.Default.Person, "Personal Info")
                        Spacer(Modifier.height(10.dp))
                        ProfileInfoRow("Full Name", currentUser?.name ?: "-")
                        ProfileInfoRow("Email", currentUser?.email ?: "-")
                        ProfileInfoRow("User ID", currentUser?.userId ?: "-")
                        ProfileInfoRow("Location", currentUser?.location.ifBlankOrDash())
                        ProfileInfoRow("Experience", currentUser?.experience.ifBlankOrDash())
                        ProfileInfoRow("Bio", currentUser?.bio.ifBlankOrDash())
                    }

                    val employerSkills = currentUser?.skills ?: emptyList()
                    if (employerSkills.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        ProfileCard {
                            ProfileSectionTitle(Icons.Default.Business, "Professional Skills")
                            Spacer(Modifier.height(10.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                employerSkills.forEach { skill ->
                                    SkillChip(label = skill, textColor = Color.Black)
                                }
                            }
                        }
                    }
                }

                "admin" -> {
                    ProfileCard {
                        ProfileSectionTitle(Icons.Default.Shield, "Admin Info")
                        Spacer(Modifier.height(10.dp))
                        ProfileInfoRow("Name", currentUser?.name ?: "-")
                        ProfileInfoRow("Email", currentUser?.email ?: "-")
                        ProfileInfoRow("Admin ID", currentUser?.userId ?: "-")
                        ProfileInfoRow("Access Level", "Full Access")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    ProfileCard {
                        ProfileSectionTitle(Icons.Default.Work, "System")
                        Spacer(Modifier.height(10.dp))
                        ProfileInfoRow("Role", "System Administrator")
                        ProfileInfoRow("Status", "Active")
                    }
                }

                else -> {
                    ProfileCard {
                        Text(
                            "Unable to determine role. Please log in again.",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Text(
                    "Log Out",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showEditDialog) {
            EditProfileDialog(
                role = role,
                fullName = fullName,
                onFullNameChange = { fullName = it },
                degree = degree,
                onDegreeChange = { degree = it },
                institution = institution,
                onInstitutionChange = { institution = it },
                graduationDate = graduationDate,
                onGraduationDateChange = { graduationDate = it },
                location = location,
                onLocationChange = { location = it },
                bio = bio,
                onBioChange = { bio = it },
                experience = experience,
                onExperienceChange = { experience = it },
                skillsInput = skillsInput,
                onSkillsInputChange = { skillsInput = it },
                onDismiss = { showEditDialog = false },
                onSave = {
                    val parsedSkills = skillsInput
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .distinct()

                    viewModel.updateCurrentUserProfile(
                        name = fullName,
                        degree = degree,
                        institution = institution,
                        graduationDate = graduationDate,
                        location = location,
                        bio = bio,
                        experience = experience,
                        skills = parsedSkills
                    ) { success, message ->
                        if (success) {
                            showEditDialog = false
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Profile updated successfully")
                            }
                        } else {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    }
                }
            )
        }
    }
}

private fun String?.ifBlankOrDash(): String = if (this.isNullOrBlank()) "-" else this

@Composable
fun ProfileCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun ProfileSectionTitle(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF3F51B5),
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.End
        )
    }
    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
}

@Composable
fun SkillChip(label: String, textColor: Color) {
    Surface(color = Color(0xFFE8EAF6), shape = RoundedCornerShape(16.dp)) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = textColor
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileDialog(
    role: String,
    fullName: String,
    onFullNameChange: (String) -> Unit,
    degree: String,
    onDegreeChange: (String) -> Unit,
    institution: String,
    onInstitutionChange: (String) -> Unit,
    graduationDate: String,
    onGraduationDateChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    bio: String,
    onBioChange: (String) -> Unit,
    experience: String,
    onExperienceChange: (String) -> Unit,
    skillsInput: String,
    onSkillsInputChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = onFullNameChange,
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = onLocationChange,
                    label = { Text("Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (role == "student") {
                    OutlinedTextField(
                        value = degree,
                        onValueChange = onDegreeChange,
                        label = { Text("Degree") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = institution,
                        onValueChange = onInstitutionChange,
                        label = { Text("Institution") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = graduationDate,
                        onValueChange = onGraduationDateChange,
                        label = { Text("Graduation Date") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                OutlinedTextField(
                    value = experience,
                    onValueChange = onExperienceChange,
                    label = { Text("Experience") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = onBioChange,
                    label = { Text("Bio") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = skillsInput,
                    onValueChange = onSkillsInputChange,
                    label = { Text("Skills") },
                    supportingText = { Text("Separate skills with commas") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Only your personal details can be changed here. Email, role, and user ID are locked.",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

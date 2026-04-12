package com.example.graduatejobmatcher.screens.commonscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: AppViewModel) {

    val currentUser by viewModel.currentUser.collectAsState()
    val primaryBlue = Color(0xFF3F51B5)
    val role = currentUser?.role?.trim()?.lowercase() ?: ""

    LaunchedEffect(Unit) { viewModel.fetchCurrentUser() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = Color.White)
                    }
                },
                actions = {
                    if (role == "student" || role == "employer") {
                        IconButton(onClick = { /* TODO: EditProfileScreen */ }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit",
                                tint = Color.White)
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
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = primaryBlue) }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
        ) {

            // ── Avatar header
            Box(
                modifier = Modifier.fillMaxWidth().height(180.dp),
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

            // ── Role badge
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center) {
                val (badgeBg, badgeFg) = when (role) {
                    "admin"    -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
                    "employer" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                    else       -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
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

            // ── Role-specific real data
            when (role) {

                "student" -> {
                    ProfileCard {
                        ProfileSectionTitle(Icons.Default.Person, "Personal Info")
                        Spacer(Modifier.height(10.dp))
                        ProfileInfoRow("Full Name", currentUser?.name  ?: "—")
                        ProfileInfoRow("Email",     currentUser?.email ?: "—")
                        ProfileInfoRow("User ID",   currentUser?.userId ?: "—")
                    }

                    Spacer(Modifier.height(12.dp))

                    ProfileCard {
                        ProfileSectionTitle(Icons.Default.School, "Education")
                        Spacer(Modifier.height(10.dp))
                        ProfileInfoRow(
                            "Degree",
                            currentUser?.degree?.ifBlank { "—" } ?: "—"
                        )
                        ProfileInfoRow(
                            "Institution",
                            currentUser?.institution?.ifBlank { "—" } ?: "—"
                        )
                        ProfileInfoRow(
                            "Graduation",
                            currentUser?.graduationDate?.ifBlank { "—" } ?: "—"
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    val studentSkills = currentUser?.skills ?: emptyList()
                    if (studentSkills.isNotEmpty()) {
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
                        Spacer(Modifier.height(12.dp))
                    }
                }

                "employer" -> {
                    ProfileCard {
                        ProfileSectionTitle(Icons.Default.Business, "Company Info")
                        Spacer(Modifier.height(10.dp))
                        ProfileInfoRow("Company",  "—")
                        ProfileInfoRow("Industry", "—")
                        ProfileInfoRow("Location", "—")
                        ProfileInfoRow("Website",  "—")
                    }
                    Spacer(Modifier.height(12.dp))
                    ProfileCard {
                        ProfileSectionTitle(Icons.Default.Person, "Contact Details")
                        Spacer(Modifier.height(10.dp))
                        ProfileInfoRow("Contact Person", currentUser?.name  ?: "—")
                        ProfileInfoRow("Email",          currentUser?.email ?: "—")
                        ProfileInfoRow("User ID",        currentUser?.userId ?: "—")
                    }
                }

                "admin" -> {
                    ProfileCard {
                        ProfileSectionTitle(Icons.Default.Shield, "Admin Info")
                        Spacer(Modifier.height(10.dp))
                        ProfileInfoRow("Name",         currentUser?.name   ?: "—")
                        ProfileInfoRow("Email",        currentUser?.email  ?: "—")
                        ProfileInfoRow("Admin ID",     currentUser?.userId ?: "—")
                        ProfileInfoRow("Access Level", "Full Access")
                    }
                    Spacer(Modifier.height(12.dp))
                    ProfileCard {
                        ProfileSectionTitle(Icons.Default.Work, "System")
                        Spacer(Modifier.height(10.dp))
                        ProfileInfoRow("Role",   "System Administrator")
                        ProfileInfoRow("Status", "Active")
                    }
                }

                else -> {
                    ProfileCard {
                        Text("Unable to determine role. Please log in again.",
                            color = Color.Gray, fontSize = 14.sp,
                            modifier = Modifier.padding(8.dp))
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
                    contentColor   = Color.Black
                ),
                border = ButtonDefaults.outlinedButtonBorder
            ) {
                Text("Log Out", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Reusable composables (unchanged signatures so nothing else breaks) ────────

@Composable
fun ProfileCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
        Icon(icon, contentDescription = null, tint = Color(0xFF3F51B5),
            modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black,
            modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
    }
    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
}

@Composable
fun ProfileSkillsRow(skills: List<String>) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        skills.take(5).forEach { skill -> SkillChip(label = skill, textColor = Color.Black) }
    }
}

@Composable
fun SkillChip(label: String, textColor: Color) {
    Surface(color = Color(0xFFE8EAF6), shape = RoundedCornerShape(16.dp)) {
        Text(label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            fontWeight = FontWeight.Medium, fontSize = 14.sp, color = textColor)
    }
}

@Composable
fun EducationItem(degree: String, date: String, dateColor: Color) {
    Column {
        Text(degree, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.Black)
        Text(date, fontSize = 14.sp, color = dateColor)
    }
}
package com.example.graduatejobmatcher.commonscreen

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

    // Normalise role — safe even if null
    val role = currentUser?.role?.trim()?.lowercase() ?: ""

    // Calls viewModel.fetchCurrentUser() which already exists in your ViewModel
    LaunchedEffect(Unit) {
        viewModel.fetchCurrentUser()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Admins manage via the admin dashboard — no profile edit button needed
                    if (role == "student" || role == "employer") {
                        IconButton(onClick = { /* TODO: navigate to EditProfileScreen */ }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = Color.White)
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

        // Show spinner while currentUser is still null (fetching from Firebase)
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

            // ── Blue Header + Avatar initial ──────────────────────────
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

            // ── Name ──────────────────────────────────────────────────
            Text(
                text = currentUser?.name ?: "User",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // ── Email ─────────────────────────────────────────────────
            Text(
                text = currentUser?.email ?: "",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                fontSize = 14.sp
            )

            // ── Role Badge ────────────────────────────────────────────
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val (badgeBg, badgeFg) = when (role) {
                    "admin"    -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
                    "employer" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                    else       -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)  // student default
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

            // ══════════════════════════════════════════════════════════
            //  ROLE-SPECIFIC CONTENT
            //  Uses only fields confirmed in your AppViewModel:
            //    currentUser.userId, .name, .email, .role
            //  Placeholders marked with TODO comments for future fields
            // ══════════════════════════════════════════════════════════

            when (role) {

                // ── STUDENT ──────────────────────────────────────────
                "student" -> {
                    ProfileCard {
                        ProfileSectionTitle(icon = Icons.Default.Person, title = "Personal Info")
                        Spacer(modifier = Modifier.height(10.dp))
                        ProfileInfoRow(label = "Full Name", value = currentUser?.name  ?: "—")
                        ProfileInfoRow(label = "Email",     value = currentUser?.email ?: "—")
                        ProfileInfoRow(label = "User ID",   value = currentUser?.userId ?: "—")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileCard {
                        ProfileSectionTitle(icon = Icons.Default.School, title = "Education")
                        Spacer(modifier = Modifier.height(10.dp))
                        // TODO: replace with currentUser?.degree and currentUser?.graduationDate
                        //       once you add those fields to your User data class
                        EducationItem(
                            degree = "B.S. Computer Science",
                            date   = "June 2024",
                            dateColor = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileCard {
                        ProfileSectionTitle(icon = Icons.Default.Work, title = "Skills")
                        Spacer(modifier = Modifier.height(10.dp))
                        // TODO: replace with currentUser?.skills ?: emptyList()
                        //       once you add a skills: List<String> field to your User data class
                        ProfileSkillsRow(skills = listOf("C++", "Python", "Figma"))
                    }
                }

                // ── EMPLOYER ─────────────────────────────────────────
                "employer" -> {
                    ProfileCard {
                        ProfileSectionTitle(icon = Icons.Default.Business, title = "Company Info")
                        Spacer(modifier = Modifier.height(10.dp))
                        // TODO: replace "—" with currentUser?.company, ?.industry, etc.
                        //       once you add those fields to your User data class
                        ProfileInfoRow(label = "Company",  value = "—")
                        ProfileInfoRow(label = "Industry", value = "—")
                        ProfileInfoRow(label = "Location", value = "—")
                        ProfileInfoRow(label = "Website",  value = "—")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileCard {
                        ProfileSectionTitle(icon = Icons.Default.Person, title = "Contact Details")
                        Spacer(modifier = Modifier.height(10.dp))
                        ProfileInfoRow(label = "Contact Person", value = currentUser?.name  ?: "—")
                        ProfileInfoRow(label = "Email",          value = currentUser?.email ?: "—")
                        ProfileInfoRow(label = "User ID",        value = currentUser?.userId ?: "—")
                    }
                }

                // ── ADMIN ────────────────────────────────────────────
                "admin" -> {
                    ProfileCard {
                        ProfileSectionTitle(icon = Icons.Default.Shield, title = "Admin Info")
                        Spacer(modifier = Modifier.height(10.dp))
                        ProfileInfoRow(label = "Name",         value = currentUser?.name   ?: "—")
                        ProfileInfoRow(label = "Email",        value = currentUser?.email  ?: "—")
                        ProfileInfoRow(label = "Admin ID",     value = currentUser?.userId ?: "—")
                        ProfileInfoRow(label = "Access Level", value = "Full Access")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileCard {
                        ProfileSectionTitle(icon = Icons.Default.Work, title = "System")
                        Spacer(modifier = Modifier.height(10.dp))
                        ProfileInfoRow(label = "Role",   value = "System Administrator")
                        ProfileInfoRow(label = "Status", value = "Active")
                    }
                }

                // ── Fallback (unknown role) ────────────────────────────
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

            // ── Log Out ───────────────────────────────────────────────
            // Calls viewModel.logout() which clears _currentUser + jobs — matches your ViewModel exactly
            Button(
                onClick = {
                    viewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }  // clears entire back stack
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
                Text("Log Out", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Reusable card wrapper ─────────────────────────────────────────────────────
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

// ── Section title with icon ───────────────────────────────────────────────────
@Composable
fun ProfileSectionTitle(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF3F51B5),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
    }
}

// ── Label / value row with divider ────────────────────────────────────────────
@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.weight(1f))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1.5f),
            textAlign = TextAlign.End
        )
    }
    HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
}

// ── Skills chip row ───────────────────────────────────────────────────────────
@Composable
fun ProfileSkillsRow(skills: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        skills.take(5).forEach { skill ->
            SkillChip(label = skill, textColor = Color.Black)
        }
    }
}

// ── Kept exactly as your original ────────────────────────────────────────────
@Composable
fun SkillChip(label: String, textColor: Color) {
    Surface(
        color = Color(0xFFE8EAF6),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = textColor
        )
    }
}

@Composable
fun EducationItem(degree: String, date: String, dateColor: Color) {
    Column {
        Text(text = degree, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.Black)
        Text(text = date, fontSize = 14.sp, color = dateColor)
    }
}
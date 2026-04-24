package com.example.graduatejobmatcher.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.User
import com.example.graduatejobmatcher.ui.theme.components.UserAvatar
import com.example.graduatejobmatcher.viewmodel.AppViewModel

// ── Colours ──────────────────────────────────────────────────────────────────
private val PrimaryBlue   = Color(0xFF3F51B5)
private val BackgroundGrey = Color(0xFFF5F7FA)
private val ChipSelected  = Color(0xFF3F51B5)
private val ChipUnselected = Color(0xFFEEEEEE)

// Role badge colours
private val StudentColor  = Color(0xFF1976D2)
private val EmployerColor = Color(0xFF388E3C)
private val AdminColor    = Color(0xFF7B1FA2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageUsersScreen(navController: NavController, viewModel: AppViewModel) {

    var users       by remember { mutableStateOf<List<User>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var activeTab   by remember { mutableStateOf("All") }

    // Dialog state
    var showDialog  by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getAllUsers { userList -> users = userList }
    }

    // Filtered list
    val filteredUsers = users.filter { user ->
        val matchesSearch = user.name.contains(searchQuery, ignoreCase = true) ||
                user.email.contains(searchQuery, ignoreCase = true)
        val matchesTab = when (activeTab) {
            "Students"  -> user.role == "student"
            "Employers" -> user.role == "employer"
            "Admins"    -> user.role == "admin"
            else        -> true
        }
        matchesSearch && matchesTab
    }

    // Tab counts
    val tabCounts = mapOf(
        "All"       to users.size,
        "Students"  to users.count { it.role == "student" },
        "Employers" to users.count { it.role == "employer" },
        "Admins"    to users.count { it.role == "admin" }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage Users", color = Color.White, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {},
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        },
        containerColor = BackgroundGrey
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // ── Summary Header ────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Group,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Platform Users",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            SummaryChip(
                                icon = Icons.Default.People,
                                label = "${users.size} Total",
                                color = PrimaryBlue
                            )
                            SummaryChip(
                                icon = Icons.Default.School,
                                label = "${users.count { it.role == "student" }} Students",
                                color = StudentColor
                            )
                            SummaryChip(
                                icon = Icons.Default.Business,
                                label = "${users.count { it.role == "employer" }} Employers",
                                color = EmployerColor
                            )
                        }
                    }
                }
            }

            // ── Search Bar ────────────────────────────────────────────────
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search users...", color = Color.Gray) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Filter Tabs ───────────────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf("All", "Students", "Employers", "Admins")) { tab ->
                    val isSelected = activeTab == tab
                    FilterChip(
                        selected = isSelected,
                        onClick = { activeTab = tab },
                        label = {
                            Text(
                                text = "$tab (${tabCounts[tab] ?: 0})",
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ChipSelected,
                            selectedLabelColor = Color.White,
                            containerColor = ChipUnselected,
                            labelColor = Color.DarkGray
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color.Transparent,
                            selectedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── User List ─────────────────────────────────────────────────
            if (filteredUsers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No users found", color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredUsers, key = { it.userId }) { user ->
                        UserCard(
                            user = user,
                            onViewProfile = {
                                selectedUser = user
                                showDialog = true
                            },
                            onToggleRole = {
                                val newRole = if (user.role == "student") "employer" else "student"
                                viewModel.updateUserRole(user.userId, newRole)
                                users = users.map {
                                    if (it.userId == user.userId) it.copy(role = newRole) else it
                                }
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }
            }
        }
    }

    // ── View Profile Dialog ───────────────────────────────────────────────────
    if (showDialog && selectedUser != null) {
        UserProfileDialog(
            user = selectedUser!!,
            onDismiss = { showDialog = false },
            onToggleRole = {
                val user = selectedUser!!
                if (user.role != "admin") {
                    val newRole = if (user.role == "student") "employer" else "student"
                    viewModel.updateUserRole(user.userId, newRole)
                    users = users.map {
                        if (it.userId == user.userId) it.copy(role = newRole) else it
                    }
                    selectedUser = user.copy(role = newRole)
                }
            }
        )
    }
}

// ── User Card ─────────────────────────────────────────────────────────────────
@Composable
fun UserCard(
    user: User,
    onViewProfile: () -> Unit,
    onToggleRole: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Top Row: Avatar + Info + Badge
            Row(verticalAlignment = Alignment.CenterVertically) {

                // Avatar with initials
                UserAvatar(
                    user = user,
                    modifier = Modifier.size(52.dp),
                    backgroundColor = avatarColor(user.role),
                    textSize = 20.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        RoleBadge(role = user.role)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = user.email,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                // View Profile
                OutlinedButton(
                    onClick = onViewProfile,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(PrimaryBlue)
                    )
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View Profile", fontSize = 13.sp)
                }

                // Toggle Role (hidden for admin)
                if (user.role != "admin") {
                    Button(
                        onClick = onToggleRole,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (user.role == "student") EmployerColor else StudentColor
                        )
                    ) {
                        Icon(
                            Icons.Default.SwapHoriz,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (user.role == "student") "→ Employer" else "→ Student",
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

// ── View Profile Dialog ───────────────────────────────────────────────────────
@Composable
fun UserProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    onToggleRole: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UserAvatar(
                    user = user,
                    modifier = Modifier.size(48.dp),
                    backgroundColor = avatarColor(user.role),
                    textSize = 20.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    RoleBadge(role = user.role)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                ProfileRow(icon = Icons.Default.Email, label = "Email", value = user.email)
                ProfileRow(icon = Icons.Default.Badge, label = "User ID", value = user.userId)
                ProfileRow(
                    icon = Icons.Default.ManageAccounts,
                    label = "Role",
                    value = user.role.replaceFirstChar { it.uppercase() }
                )
            }
        },
        confirmButton = {
            if (user.role != "admin") {
                Button(
                    onClick = {
                        onToggleRole()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (user.role == "student") EmployerColor else StudentColor
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        if (user.role == "student") "Switch to Employer" else "Switch to Student"
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.Gray)
            }
        }
    )
}

// ── Small Helpers ─────────────────────────────────────────────────────────────

@Composable
private fun RoleBadge(role: String) {
    val (bgColor, label) = when (role) {
        "student"  -> Color(0xFFE3F2FD) to "Student"
        "employer" -> Color(0xFFE8F5E9) to "Employer"
        "admin"    -> Color(0xFFF3E5F5) to "Admin"
        else       -> Color(0xFFEEEEEE) to role
    }
    val textColor = when (role) {
        "student"  -> StudentColor
        "employer" -> EmployerColor
        "admin"    -> AdminColor
        else       -> Color.Gray
    }
    Surface(
        shape = RoundedCornerShape(50),
        color = bgColor
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun SummaryChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, fontSize = 12.sp, color = color, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ProfileRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(label, fontSize = 11.sp, color = Color.Gray)
            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

private fun avatarColor(role: String): Color = when (role) {
    "student"  -> StudentColor
    "employer" -> EmployerColor
    "admin"    -> AdminColor
    else       -> Color.Gray
}

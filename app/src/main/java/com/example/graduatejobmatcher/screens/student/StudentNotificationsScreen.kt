package com.example.graduatejobmatcher.screens.student

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.graduatejobmatcher.model.AppNotification
import com.example.graduatejobmatcher.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Locale

private fun studentNotificationDateFormatter() = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentNotificationsScreen(
    navController: NavController,
    viewModel: AppViewModel
) {
    var notifications by remember { mutableStateOf<List<AppNotification>>(emptyList()) }
    val studentId = viewModel.getCurrentUserId().orEmpty()

    DisposableEffect(studentId) {
        if (studentId.isBlank()) {
            onDispose { }
        } else {
            val registration = viewModel.listenNotificationsForUser(studentId) { fetchedNotifications ->
                notifications = fetchedNotifications
            }
            onDispose { registration.remove() }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FB),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Notifications",
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
                    containerColor = Color(0xFF3F51B5)
                )
            )
        }
    ) { innerPadding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No notifications yet.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications, key = { it.notificationId }) { notification ->
                    NotificationCard(
                        notification = notification,
                        onMarkRead = {
                            if (!notification.isRead) {
                                viewModel.markNotificationAsRead(notification.notificationId)
                                notifications = notifications.map {
                                    if (it.notificationId == notification.notificationId) it.copy(isRead = true) else it
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: AppNotification,
    onMarkRead: () -> Unit
) {
    val context = LocalContext.current
    var expanded by remember(notification.notificationId) { mutableStateOf(false) }
    val hasInterviewDetails = notification.type == "interview_scheduled" ||
        notification.meetingLink.isNotBlank() ||
        notification.interviewDate.isNotBlank()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expanded = !expanded
                onMarkRead()
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White else Color(0xFFF7FAFF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFE8EEFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color(0xFF3F51B5)
                    )
                }

                Spacer(modifier = Modifier.padding(start = 12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = notification.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = notification.message,
                        fontSize = 14.sp,
                        color = Color(0xFF4B5563)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = notification.createdAt?.let { studentNotificationDateFormatter().format(it) } ?: "Just now",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            if (expanded && hasInterviewDetails) {
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFE5E7EB))
                Spacer(modifier = Modifier.height(12.dp))

                NotificationDetailRow(
                    icon = Icons.Default.BusinessCenter,
                    label = "Employer",
                    value = buildString {
                        append(notification.employerName.ifBlank { "Employer" })
                        if (notification.company.isNotBlank()) {
                            append(" - ")
                            append(notification.company)
                        }
                    }
                )
                NotificationDetailRow(
                    icon = Icons.Default.BusinessCenter,
                    label = "Job",
                    value = notification.jobTitle.ifBlank { "Job details not provided" }
                )
                NotificationDetailRow(
                    icon = Icons.Default.CalendarToday,
                    label = "Date",
                    value = notification.interviewDate.ifBlank { "Date not provided" }
                )
                NotificationDetailRow(
                    icon = Icons.Default.Schedule,
                    label = "Time",
                    value = notification.interviewTime.ifBlank { "Time not provided" }
                )
                NotificationDetailRow(
                    icon = Icons.Default.VideoCall,
                    label = "Type",
                    value = notification.interviewType.ifBlank { "Interview type not provided" }
                )
                NotificationDetailRow(
                    icon = Icons.Default.Link,
                    label = "Meeting Link",
                    value = notification.meetingLink.ifBlank { "Meeting link not provided" }
                )

                if (notification.detailMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = notification.detailMessage,
                        color = Color(0xFF374151),
                        fontSize = 14.sp
                    )
                }

                if (notification.meetingLink.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            val uri = Uri.parse(notification.meetingLink.withWebScheme())
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = Color(0xFF3F51B5),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.padding(start = 6.dp))
                        Text("Open Meeting Link", color = Color(0xFF3F51B5))
                    }
                }
            } else if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No extra details were added to this notification.",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun NotificationDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF3F51B5),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.padding(start = 8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = Color.Gray, fontSize = 12.sp)
            Text(value, color = Color(0xFF111827), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun String.withWebScheme(): String {
    val trimmed = trim()
    return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) trimmed else "https://$trimmed"
}

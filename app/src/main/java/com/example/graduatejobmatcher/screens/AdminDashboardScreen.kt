package com.example.graduatejobmatcher.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun AdminDashboardScreen(navController: NavController) {
    val primaryBlue = Color(0xFF3F51B5)
    val backgroundColor = Color(0xFFF5F7FA)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // --- ADMIN PANEL HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(primaryBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Admin Panel",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // --- GRID CONTENT ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                AdminCard(
                    title = "Manage Users",
                    icon = Icons.Default.AccountCircle,
                    onClick = { navController.navigate("manage_users") }
                )
            }
            item {
                AdminCard(
                    title = "Manage Job Posts",
                    icon = Icons.Default.BusinessCenter,
                    onClick = { /* Navigate to jobs */ }
                )
            }
            item {
                AdminCard(
                    title = "Reports",
                    icon = Icons.Default.BarChart,
                    onClick = { /* Navigate to reports */ }
                )
            }
            item {
                AdminCard(
                    title = "System Analytics",
                    icon = Icons.Default.PieChart,
                    onClick = { /* Navigate to analytics */ }
                )
            }
        }
    }
}

@Composable
fun AdminCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f) // Makes the card square
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFF3F51B5) // Matching the header blue
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp),
                color = Color.Black
            )
        }
    }
}
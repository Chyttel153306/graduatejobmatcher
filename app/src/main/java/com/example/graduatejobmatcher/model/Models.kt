package com.example.graduatejobmatcher.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "" // "student", "employer", "admin"
)

data class Job(
    val jobId: String = "",
    val title: String = "",
    val description: String = "",
    val company: String = "",
    val location: String = "",
    val postedDate: Date? = null, // Firebase timestamp
    val employerId: String = ""
)

data class Application(
    val applicationId: String = "",
    val jobId: String = "",
    val studentId: String = "",
    val status: String = "pending", // pending, accepted, rejected
    val appliedDate: Date? = null
)
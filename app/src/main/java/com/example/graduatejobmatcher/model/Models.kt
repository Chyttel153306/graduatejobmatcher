package com.example.graduatejobmatcher.model

import java.util.*

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "" // student, employer, admin
)

data class Job(
    val jobId: String = "",
    val title: String = "",
    val description: String = "",
    val company: String = "",
    val location: String = "",
    val jobType: String = "Full-time", // Full-time, Part-time, Internship, Remote
    val status: String = "pending",    // pending, approved, rejected
    val postedDate: Date? = null,
    val deadline: Date? = null,
    val employerId: String = ""
)

data class Application(
    val applicationId: String = "",
    val jobId: String = "",
    val applicantId: String = "",      // renamed from studentId for consistency
    val studentId: String = "",        // kept for backward compatibility
    val status: String = "pending",    // pending, approved, rejected
    val appliedDate: Date? = null
)
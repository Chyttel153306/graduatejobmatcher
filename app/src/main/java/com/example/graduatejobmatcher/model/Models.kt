package com.example.graduatejobmatcher.model

import java.util.*

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "", // student, employer, admin
    val degree: String = "",          // for students
    val institution: String = "",     // for students
    val graduationDate: String = "",  // for students
    val skills: List<String> = emptyList() // for students
)

data class Job(
    val jobId: String = "",
    val title: String = "",
    val description: String = "",
    val company: String = "",
    val location: String = "",
    val jobType: String = "Full-time",
    val status: String = "pending",
    val postedDate: Date? = null,
    val deadline: Date? = null,
    val employerId: String = "",
    val salary: String = "",
    val requiredSkills: List<String> = emptyList()
) {
    val id: String get() = jobId
}

data class Application(
    val applicationId: String = "",
    val jobId: String = "",
    val applicantId: String = "",
    val studentId: String = "",
    val status: String = "pending",
    val appliedDate: Date? = null,
    val resumeUrl: String = "",
    val portfolioUrl: String = "",
    val coverLetterUrl: String = ""
)
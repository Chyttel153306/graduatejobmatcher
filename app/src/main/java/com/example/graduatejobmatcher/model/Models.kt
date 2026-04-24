package com.example.graduatejobmatcher.model

import java.util.*

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "", // student, employer, admin
    val profileImageBase64: String = "",

    //  Student Info
    val degree: String = "",
    val institution: String = "",
    val graduationDate: String = "",


    val location: String = "",
    val bio: String = "",
    val experience: String = "",

    val skills: List<String> = emptyList()
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

    // 📄 Files
    val resumeUrl: String = "",
    val portfolioUrl: String = "",
    val coverLetterUrl: String = ""
)

data class InterviewSchedule(
    val interviewId: String = "",
    val applicationId: String = "",
    val jobId: String = "",
    val employerId: String = "",
    val studentId: String = "",
    val interviewDate: String = "",
    val interviewTime: String = "",
    val interviewType: String = "",
    val meetingLink: String = "",
    val message: String = "",
    val createdAt: Date? = null
)

data class AppNotification(
    val notificationId: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val createdAt: Date? = null,
    val isRead: Boolean = false,
    val type: String = "",
    val applicationId: String = "",
    val jobId: String = "",
    val interviewId: String = "",
    val employerId: String = "",
    val employerName: String = "",
    val company: String = "",
    val jobTitle: String = "",
    val interviewDate: String = "",
    val interviewTime: String = "",
    val interviewType: String = "",
    val meetingLink: String = "",
    val detailMessage: String = ""
)

data class AdminReport(
    val pendingJobs: List<Job> = emptyList(),
    val approvedJobs: List<Job> = emptyList(),
    val rejectedJobs: List<Job> = emptyList(),
    val users: List<User> = emptyList()
)

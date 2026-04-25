package com.example.graduatejobmatcher.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.graduatejobmatcher.data.FirebaseRepository
import com.example.graduatejobmatcher.model.*
import com.google.firebase.firestore.ListenerRegistration

class AppViewModel : ViewModel() {

    private val repo = FirebaseRepository()
    var jobs = mutableStateListOf<Job>()
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // ---------- Auth ----------
    fun register(
        name: String,
        email: String,
        password: String,
        role: String,
        adminCreationPassword: String = "",
        degree: String = "",
        institution: String = "",
        graduationDate: String = "",
        skills: List<String> = emptyList(),
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repo.register(
                    name = name,
                    email = email,
                    password = password,
                    role = role,
                    adminCreationPassword = adminCreationPassword,
                    degree = degree,
                    institution = institution,
                    graduationDate = graduationDate,
                    skills = skills
                )
                onResult(true, "")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Registration failed")
            }
        }
    }

    fun login(email: String, password: String, callback: (Boolean, String, String) -> Unit) {
        viewModelScope.launch {
            try {
                val user = repo.login(email, password)
                _currentUser.value = user
                callback(true, user.role, "")
            } catch (e: Exception) {
                callback(false, "", e.message ?: "Login failed")
            }
        }
    }

    suspend fun fetchCurrentUser() { _currentUser.value = repo.getCurrentUser() }
    fun getCurrentUserId(): String? = _currentUser.value?.userId
    fun logout() {
        viewModelScope.launch {
            repo.logout()
            _currentUser.value = null
            jobs.clear()
        }
    }

    fun updateCurrentUserProfile(
        name: String,
        degree: String,
        institution: String,
        graduationDate: String,
        location: String,
        bio: String,
        experience: String,
        skills: List<String>,
        adminCreationPassword: String = "",
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val existingUser = _currentUser.value ?: throw Exception("User not found")
                val updatedFields = buildMap<String, Any> {
                    put("name", name.trim())
                    put("location", location.trim())
                    put("bio", bio.trim())
                    put("experience", experience.trim())
                    put("skills", skills)

                    if (existingUser.role == "student") {
                        put("degree", degree.trim())
                        put("institution", institution.trim())
                        put("graduationDate", graduationDate.trim())
                    }
                }

                repo.updateCurrentUserProfile(updatedFields)
                if (existingUser.role == "admin" && adminCreationPassword.isNotBlank()) {
                    repo.updateAdminCreationPassword(adminCreationPassword)
                }
                _currentUser.value = existingUser.copy(
                    name = name.trim(),
                    degree = if (existingUser.role == "student") degree.trim() else existingUser.degree,
                    institution = if (existingUser.role == "student") institution.trim() else existingUser.institution,
                    graduationDate = if (existingUser.role == "student") graduationDate.trim() else existingUser.graduationDate,
                    location = location.trim(),
                    bio = bio.trim(),
                    experience = experience.trim(),
                    skills = skills
                )
                onResult(true, "")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Profile update failed")
            }
        }
    }

    fun getAdminCreationPassword(onResult: (String) -> Unit) {
        viewModelScope.launch { onResult(repo.getAdminCreationPassword().orEmpty()) }
    }

    fun uploadCurrentUserProfileImage(
        imageBytes: ByteArray,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val existingUser = _currentUser.value ?: throw Exception("User not found")
                val imageBase64 = repo.uploadCurrentUserProfileImage(imageBytes)
                _currentUser.value = existingUser.copy(profileImageBase64 = imageBase64)
                onResult(true, "")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Image upload failed")
            }
        }
    }

    // ---------- Job Methods ----------
    fun loadJobs() {
        viewModelScope.launch {
            try {
                jobs.clear()
                jobs.addAll(repo.getApprovedJobs())
            } catch (_: Exception) { }
        }
    }

    fun postJob(job: Job) {
        viewModelScope.launch {
            repo.postJob(job)
            loadJobs()
        }
    }

    fun getApprovedJobs(onResult: (List<Job>) -> Unit) {
        viewModelScope.launch { onResult(repo.getApprovedJobs()) }
    }

    fun getRejectedJobs(onResult: (List<Job>) -> Unit) {
        viewModelScope.launch { onResult(repo.getRejectedJobs()) }
    }

    fun getJobsForEmployer(employerId: String, onResult: (List<Job>) -> Unit) {
        viewModelScope.launch { onResult(repo.getJobsForEmployer(employerId)) }
    }

    fun listenJobsForEmployer(employerId: String, onResult: (List<Job>) -> Unit): ListenerRegistration {
        return repo.listenJobsForEmployer(employerId, onResult)
    }

    fun getTotalApplicationsForEmployer(employerId: String, onResult: (Int) -> Unit) {
        viewModelScope.launch { onResult(repo.getTotalApplicationsForEmployer(employerId)) }
    }

    fun getJobById(jobId: String, onResult: (Job?) -> Unit) {
        viewModelScope.launch { onResult(repo.getJobById(jobId)) }
    }

    fun updateJob(job: Job, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repo.updateJob(job)
            loadJobs()
            onComplete()
        }
    }

    fun deleteJob(jobId: String, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            try {
                repo.deleteJob(jobId)
                loadJobs()
                onResult(true, "")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Delete failed")
            }
        }
    }

    // ---------- Application Methods ----------
    fun applyJob(application: Application, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                repo.applyJob(application)
                onResult(true, "")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Submission failed. Please try again.")
            }
        }
    }

    fun getApplicationForJobAndStudent(
        jobId: String,
        studentId: String,
        onResult: (Application?) -> Unit
    ) {
        viewModelScope.launch { onResult(repo.getApplicationForJobAndStudent(jobId, studentId)) }
    }

    fun updateApplication(application: Application, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                repo.updateApplication(application)
                onResult(true, "")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Application update failed. Please try again.")
            }
        }
    }

    fun getApplicationsForJob(jobId: String, onResult: (List<Application>) -> Unit) {
        viewModelScope.launch { onResult(repo.getApplicationsForJob(jobId)) }
    }

    fun listenApplicationsForJob(jobId: String, onResult: (List<Application>) -> Unit): ListenerRegistration {
        return repo.listenApplicationsForJob(jobId, onResult)
    }

    fun getApplicationsForApplicant(applicantId: String, onResult: (List<Application>) -> Unit) {
        viewModelScope.launch { onResult(repo.getApplicationsForApplicant(applicantId)) }
    }

    fun getApplicationById(applicationId: String, onResult: (Application?) -> Unit) {
        viewModelScope.launch { onResult(repo.getApplicationById(applicationId)) }
    }

    fun getInterviewByApplicationId(applicationId: String, onResult: (InterviewSchedule?) -> Unit) {
        viewModelScope.launch { onResult(repo.getInterviewByApplicationId(applicationId)) }
    }

    fun updateApplicationStatus(applicationId: String, newStatus: String) {
        viewModelScope.launch { repo.updateApplicationStatus(applicationId, newStatus) }
    }

    fun scheduleInterview(
        interview: InterviewSchedule,
        onResult: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            try {
                repo.scheduleInterview(interview)
                onResult(true, "")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Interview scheduling failed")
            }
        }
    }

    fun getNotificationsForUser(userId: String, onResult: (List<AppNotification>) -> Unit) {
        viewModelScope.launch { onResult(repo.getNotificationsForUser(userId)) }
    }

    fun listenNotificationsForUser(userId: String, onResult: (List<AppNotification>) -> Unit): ListenerRegistration {
        return repo.listenNotificationsForUser(userId, onResult)
    }

    fun markNotificationAsRead(notificationId: String) {
        viewModelScope.launch { repo.markNotificationAsRead(notificationId) }
    }

    // ---------- Admin ----------
    fun getPendingJobs(onResult: (List<Job>) -> Unit) {
        viewModelScope.launch { onResult(repo.getPendingJobs()) }
    }

    fun listenJobsByStatus(status: String, onResult: (List<Job>) -> Unit): ListenerRegistration {
        return repo.listenJobsByStatus(status, onResult)
    }

    fun updateJobStatus(jobId: String, status: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repo.updateJobStatus(jobId, status)
            onComplete()
        }
    }

    fun getPendingJobsCount(onResult: (Int) -> Unit) {
        viewModelScope.launch { onResult(repo.getPendingJobs().size) }
    }

    fun getApprovedJobsCount(onResult: (Int) -> Unit) {
        viewModelScope.launch { onResult(repo.getApprovedJobs().size) }
    }

    fun getRejectedJobsCount(onResult: (Int) -> Unit) {
        viewModelScope.launch { onResult(repo.getRejectedJobs().size) }
    }

    fun getTotalEmployersCount(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            onResult(repo.getAllUsers().count { it.role == "employer" })
        }
    }

    fun getTotalUsersCount(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            onResult(repo.getAllUsers().size)
        }
    }

    fun getAllUsers(onResult: (List<User>) -> Unit) {
        viewModelScope.launch { onResult(repo.getAllUsers()) }
    }

    fun listenAllUsers(onResult: (List<User>) -> Unit): ListenerRegistration {
        return repo.listenAllUsers(onResult)
    }

    fun listenAdminReport(onResult: (AdminReport) -> Unit): List<ListenerRegistration> {
        var pendingJobs = emptyList<Job>()
        var approvedJobs = emptyList<Job>()
        var rejectedJobs = emptyList<Job>()
        var users = emptyList<User>()

        fun emit() {
            onResult(
                AdminReport(
                    pendingJobs = pendingJobs,
                    approvedJobs = approvedJobs,
                    rejectedJobs = rejectedJobs,
                    users = users
                )
            )
        }

        return listOf(
            repo.listenJobsByStatus("pending") {
                pendingJobs = it
                emit()
            },
            repo.listenJobsByStatus("approved") {
                approvedJobs = it
                emit()
            },
            repo.listenJobsByStatus("rejected") {
                rejectedJobs = it
                emit()
            },
            repo.listenAllUsers {
                users = it
                emit()
            }
        )
    }

    fun getUserById(userId: String, onResult: (User?) -> Unit) {
        viewModelScope.launch { onResult(repo.getUserById(userId)) }
    }

    fun listenUserById(userId: String, onResult: (User?) -> Unit): ListenerRegistration {
        return repo.listenUserById(userId, onResult)
    }

    fun updateUserRole(userId: String, newRole: String) {
        viewModelScope.launch { repo.updateUserRole(userId, newRole) }
    }
}

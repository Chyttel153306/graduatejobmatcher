package com.example.graduatejobmatcher.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.graduatejobmatcher.data.FirebaseRepository
import com.example.graduatejobmatcher.model.*

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
        degree: String = "",
        institution: String = "",
        graduationDate: String = "",
        skills: List<String> = emptyList(),
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repo.register(name, email, password, role, degree, institution, graduationDate, skills)
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

    // ---------- Job Methods ----------
    fun loadJobs() {
        viewModelScope.launch {
            try {
                jobs.clear()
                jobs.addAll(repo.getJobs())
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

    fun getJobsForEmployer(employerId: String, onResult: (List<Job>) -> Unit) {
        viewModelScope.launch { onResult(repo.getJobsForEmployer(employerId)) }
    }

    fun getTotalApplicationsForEmployer(employerId: String, onResult: (Int) -> Unit) {
        viewModelScope.launch { onResult(repo.getTotalApplicationsForEmployer(employerId)) }
    }

    fun getJobById(jobId: String, onResult: (Job?) -> Unit) {
        viewModelScope.launch { onResult(repo.getJobById(jobId)) }
    }

    // ---------- Application Methods ----------
    fun applyJob(application: Application) {
        viewModelScope.launch { repo.applyJob(application) }
    }

    fun getApplicationsForJob(jobId: String, onResult: (List<Application>) -> Unit) {
        viewModelScope.launch { onResult(repo.getApplicationsForJob(jobId)) }
    }

    fun getApplicationsForApplicant(applicantId: String, onResult: (List<Application>) -> Unit) {
        viewModelScope.launch { onResult(repo.getApplicationsForApplicant(applicantId)) }
    }

    fun getApplicationById(applicationId: String, onResult: (Application?) -> Unit) {
        viewModelScope.launch { onResult(repo.getApplicationById(applicationId)) }
    }

    fun updateApplicationStatus(applicationId: String, newStatus: String) {
        viewModelScope.launch { repo.updateApplicationStatus(applicationId, newStatus) }
    }

    // ---------- Admin ----------
    fun getPendingJobs(onResult: (List<Job>) -> Unit) {
        viewModelScope.launch { onResult(repo.getPendingJobs()) }
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

    fun getAllUsers(onResult: (List<User>) -> Unit) {
        viewModelScope.launch { onResult(repo.getAllUsers()) }
    }

    fun getUserById(userId: String, onResult: (User?) -> Unit) {
        viewModelScope.launch { onResult(repo.getUserById(userId)) }
    }

    fun updateUserRole(userId: String, newRole: String) {
        viewModelScope.launch { repo.updateUserRole(userId, newRole) }
    }
}
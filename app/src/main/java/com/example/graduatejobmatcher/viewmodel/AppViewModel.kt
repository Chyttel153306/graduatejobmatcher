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
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repo.register(name, email, password, role)
                onResult(true, "")
            } catch (e: Exception) {
                onResult(false, e.message ?: "Registration failed")
            }
        }
    }

    fun login(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
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

    suspend fun fetchCurrentUser() {
        _currentUser.value = repo.getCurrentUser()
    }

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
            } catch (e: Exception) {
                // log if needed
            }
        }
    }

    fun postJob(job: Job) {
        viewModelScope.launch {
            try {
                repo.postJob(job)
                loadJobs()
            } catch (e: Exception) {
                // log if needed
            }
        }
    }

    fun getApprovedJobs(onResult: (List<Job>) -> Unit) {
        viewModelScope.launch {
            try {
                val jobs = repo.getApprovedJobs()
                onResult(jobs)
            } catch (e: Exception) {
                onResult(emptyList())
            }
        }
    }

    fun getJobsForEmployer(employerId: String, onResult: (List<Job>) -> Unit) {
        viewModelScope.launch {
            try {
                val jobs = repo.getJobsForEmployer(employerId)
                onResult(jobs)
            } catch (e: Exception) {
                onResult(emptyList())
            }
        }
    }

    fun getTotalApplicationsForEmployer(employerId: String, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                val total = repo.getTotalApplicationsForEmployer(employerId)
                onResult(total)
            } catch (e: Exception) {
                onResult(0)
            }
        }
    }

    // ---------- Application Methods ----------

    fun applyJob(application: Application) {
        viewModelScope.launch {
            try {
                repo.applyJob(application)
            } catch (e: Exception) {
                // log if needed
            }
        }
    }

    fun getApplicationsForJob(jobId: String, onResult: (List<Application>) -> Unit) {
        viewModelScope.launch {
            try {
                val apps = repo.getApplicationsForJob(jobId)
                onResult(apps)
            } catch (e: Exception) {
                onResult(emptyList())
            }
        }
    }

    fun getApplicationsForApplicant(applicantId: String, onResult: (List<Application>) -> Unit) {
        viewModelScope.launch {
            try {
                val apps = repo.getApplicationsForApplicant(applicantId)
                onResult(apps)
            } catch (e: Exception) {
                onResult(emptyList())
            }
        }
    }

    fun updateApplicationStatus(applicationId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                repo.updateApplicationStatus(applicationId, newStatus)
            } catch (e: Exception) {
                // log if needed
            }
        }
    }

    // ---------- Admin: Job Moderation ----------

    fun getPendingJobs(onResult: (List<Job>) -> Unit) {
        viewModelScope.launch {
            try {
                val jobs = repo.getPendingJobs()
                onResult(jobs)
            } catch (e: Exception) {
                onResult(emptyList())
            }
        }
    }

    fun updateJobStatus(jobId: String, status: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repo.updateJobStatus(jobId, status)
                onComplete()
            } catch (e: Exception) {
                onComplete()
            }
        }
    }

    // ---------- Admin: Dashboard Stats ----------

    fun getPendingJobsCount(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                val count = repo.getPendingJobs().size
                onResult(count)
            } catch (e: Exception) {
                onResult(0)
            }
        }
    }

    fun getApprovedJobsCount(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                val count = repo.getApprovedJobs().size
                onResult(count)
            } catch (e: Exception) {
                onResult(0)
            }
        }
    }

    fun getRejectedJobsCount(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                val count = repo.getRejectedJobs().size
                onResult(count)
            } catch (e: Exception) {
                onResult(0)
            }
        }
    }

    fun getTotalEmployersCount(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            try {
                val count = repo.getAllUsers().count { it.role == "employer" }
                onResult(count)
            } catch (e: Exception) {
                onResult(0)
            }
        }
    }

    // ---------- Admin: User Management ----------

    fun getAllUsers(onResult: (List<User>) -> Unit) {
        viewModelScope.launch {
            try {
                val users = repo.getAllUsers()
                onResult(users)
            } catch (e: Exception) {
                onResult(emptyList())
            }
        }
    }

    fun updateUserRole(userId: String, newRole: String) {
        viewModelScope.launch {
            try {
                repo.updateUserRole(userId, newRole)
            } catch (e: Exception) {
                // log if needed
            }
        }
    }
}
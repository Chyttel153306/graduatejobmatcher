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

    // Observable list of jobs
    var jobs = mutableStateListOf<Job>()

    // Current logged-in user (as StateFlow)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    /**
     * Register new user.
     * @param onResult (success: Boolean, errorMessage: String)
     */
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

    /**
     * Login user.
     * @param callback (success: Boolean, role: String, errorMessage: String)
     */
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
                // silently fail or log
            }
        }
    }

    fun postJob(job: Job) {
        viewModelScope.launch {
            try {
                repo.postJob(job)
                loadJobs() // refresh
            } catch (e: Exception) {
                // handle error if needed
            }
        }
    }

    // ---------- Application Methods ----------
    fun applyJob(application: Application) {
        viewModelScope.launch {
            try {
                repo.applyJob(application)
            } catch (e: Exception) {
                // handle
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

    fun updateApplicationStatus(applicationId: String, newStatus: String) {
        viewModelScope.launch {
            try {
                repo.updateApplicationStatus(applicationId, newStatus)
            } catch (e: Exception) {
                // handle
            }
        }
    }

    // ---------- Admin Methods ----------
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
                // handle
            }
        }
    }
}
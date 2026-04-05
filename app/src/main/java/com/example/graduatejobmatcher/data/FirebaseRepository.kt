package com.example.graduatejobmatcher.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.example.graduatejobmatcher.model.*
import kotlinx.coroutines.tasks.await
import java.util.*

class FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    /**
     * Register a new user.
     * Throws exception with readable message on failure.
     */
    suspend fun register(name: String, email: String, password: String, role: String) {
        try {
            // 1. Create auth user
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("Authentication failed: user is null")

            // 2. Create User object
            val user = User(
                userId = uid,
                name = name,
                email = email,
                role = role
            )

            // 3. Save to Firestore "users" collection
            db.collection("users").document(uid).set(user).await()
        } catch (e: FirebaseAuthException) {
            // Convert Firebase errors to readable messages
            throw Exception(when (e.errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> "Email already registered"
                "ERROR_WEAK_PASSWORD" -> "Password too weak (min 6 characters)"
                "ERROR_INVALID_EMAIL" -> "Invalid email format"
                else -> "Registration failed: ${e.message}"
            })
        }
    }

    /**
     * Login existing user.
     * Returns User object from Firestore.
     */
    suspend fun login(email: String, password: String): User {
        try {
            // 1. Sign in with email/password
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("Login failed: user is null")

            // 2. Fetch user document from Firestore
            val doc = db.collection("users").document(uid).get().await()
            val user = doc.toObject(User::class.java)
            if (user == null) {
                // Fallback: create user document if missing (should not happen)
                val fallbackUser = User(uid, "", email, "student")
                db.collection("users").document(uid).set(fallbackUser).await()
                return fallbackUser
            }
            return user
        } catch (e: FirebaseAuthException) {
            throw Exception(when (e.errorCode) {
                "ERROR_USER_NOT_FOUND" -> "User not found"
                "ERROR_WRONG_PASSWORD" -> "Wrong password"
                "ERROR_INVALID_EMAIL" -> "Invalid email format"
                else -> "Login failed: ${e.message}"
            })
        }
    }

    /**
     * Get currently logged-in user from Firestore.
     */
    suspend fun getCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            db.collection("users").document(uid).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        auth.signOut()
    }

    // ---------- Job Methods ----------
    suspend fun postJob(job: Job) {
        val id = db.collection("jobs").document().id
        val jobWithId = job.copy(jobId = id, postedDate = Date())
        db.collection("jobs").document(id).set(jobWithId).await()
    }

    suspend fun getJobs(): List<Job> {
        return db.collection("jobs").get().await().toObjects(Job::class.java)
    }

    // ---------- Application Methods ----------
    suspend fun applyJob(application: Application) {
        val id = db.collection("applications").document().id
        val appWithId = application.copy(applicationId = id, appliedDate = Date())
        db.collection("applications").document(id).set(appWithId).await()
    }

    suspend fun getApplicationsForJob(jobId: String): List<Application> {
        return db.collection("applications")
            .whereEqualTo("jobId", jobId)
            .get().await()
            .toObjects(Application::class.java)
    }

    suspend fun updateApplicationStatus(applicationId: String, status: String) {
        db.collection("applications").document(applicationId)
            .update("status", status).await()
    }

    // ---------- Admin Methods ----------
    suspend fun getAllUsers(): List<User> {
        return db.collection("users").get().await().toObjects(User::class.java)
    }

    suspend fun updateUserRole(userId: String, newRole: String) {
        db.collection("users").document(userId).update("role", newRole).await()
    }
}
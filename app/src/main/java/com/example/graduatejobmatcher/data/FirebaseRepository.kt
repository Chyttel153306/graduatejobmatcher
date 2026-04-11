package com.example.graduatejobmatcher.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.graduatejobmatcher.model.*
import kotlinx.coroutines.tasks.await
import java.util.*

class FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // ---------- AUTH ----------

    suspend fun register(name: String, email: String, password: String, role: String) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("User null")

        val user = User(uid, name, email, role)
        db.collection("users").document(uid).set(user).await()
    }

    suspend fun login(email: String, password: String): User {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("User null")

        val doc = db.collection("users").document(uid).get().await()
        return doc.toObject(User::class.java)!!
    }

    suspend fun getCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        val doc = db.collection("users").document(uid).get().await()
        return doc.toObject(User::class.java)
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun logout() = auth.signOut()

    // ---------- JOBS ----------

    suspend fun postJob(job: Job) {
        val id = db.collection("jobs").document().id

        val jobWithId = job.copy(
            jobId = id,
            postedDate = Date(),
            status = "pending"
        )

        db.collection("jobs").document(id).set(jobWithId).await()
    }

    suspend fun getJobs(): List<Job> {
        return db.collection("jobs")
            .get()
            .await()
            .toObjects(Job::class.java)
    }

    suspend fun getPendingJobs(): List<Job> {
        return db.collection("jobs")
            .whereEqualTo("status", "pending")
            .get()
            .await()
            .toObjects(Job::class.java)
    }

    suspend fun getApprovedJobs(): List<Job> {
        return db.collection("jobs")
            .whereEqualTo("status", "approved")
            .get()
            .await()
            .toObjects(Job::class.java)
    }

    suspend fun getRejectedJobs(): List<Job> {
        return db.collection("jobs")
            .whereEqualTo("status", "rejected")
            .get()
            .await()
            .toObjects(Job::class.java)
    }

    suspend fun updateJobStatus(jobId: String, status: String) {
        db.collection("jobs")
            .document(jobId)
            .update("status", status)
            .await()
    }

    suspend fun getJobsForEmployer(employerId: String): List<Job> {
        return db.collection("jobs")
            .whereEqualTo("employerId", employerId)
            .get()
            .await()
            .toObjects(Job::class.java)
    }

    // ---------- APPLICATIONS ----------

    suspend fun applyJob(application: Application) {
        val id = db.collection("applications").document().id

        val appWithId = application.copy(
            applicationId = id,
            appliedDate = Date(),
            status = "pending"
        )

        db.collection("applications").document(id).set(appWithId).await()
    }

    suspend fun getApplicationsForJob(jobId: String): List<Application> {
        return db.collection("applications")
            .whereEqualTo("jobId", jobId)
            .get()
            .await()
            .toObjects(Application::class.java)
    }

    suspend fun getApplicationsForApplicant(applicantId: String): List<Application> {
        return db.collection("applications")
            .whereEqualTo("applicantId", applicantId)
            .get()
            .await()
            .toObjects(Application::class.java)
    }

    suspend fun updateApplicationStatus(applicationId: String, newStatus: String) {
        db.collection("applications")
            .document(applicationId)
            .update("status", newStatus)
            .await()
    }

    suspend fun getTotalApplicationsForEmployer(employerId: String): Int {
        val jobs = getJobsForEmployer(employerId)
        var total = 0
        for (job in jobs) {
            val snapshot = db.collection("applications")
                .whereEqualTo("jobId", job.jobId)
                .get()
                .await()
            total += snapshot.size()
        }
        return total
    }

    // ---------- ADMIN ----------

    suspend fun getAllUsers(): List<User> {
        return db.collection("users")
            .get()
            .await()
            .toObjects(User::class.java)
    }

    suspend fun updateUserRole(userId: String, newRole: String) {
        db.collection("users")
            .document(userId)
            .update("role", newRole)
            .await()
    }
}
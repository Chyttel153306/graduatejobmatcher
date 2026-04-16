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

    // accepts all student fields and stores them in Firestore
    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: String,
        degree: String = "",
        institution: String = "",
        graduationDate: String = "",
        skills: List<String> = emptyList()
    ) {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("User creation failed")

        val user = User(
            userId = uid,
            name = name,
            email = email,
            role = role,
            degree = degree,
            institution = institution,
            graduationDate = graduationDate,
            skills = skills
        )
        db.collection("users").document(uid).set(user).await()
    }

    suspend fun login(email: String, password: String): User {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw Exception("Login failed")
        val doc = db.collection("users").document(uid).get().await()
        return doc.toObject(User::class.java) ?: throw Exception("User data missing")
    }

    suspend fun getCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        val doc = db.collection("users").document(uid).get().await()
        return doc.toObject(User::class.java)
    }

    suspend fun updateCurrentUserProfile(updatedFields: Map<String, Any>) {
        val uid = auth.currentUser?.uid ?: throw Exception("User not logged in")
        db.collection("users").document(uid).update(updatedFields).await()
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid
    fun logout() = auth.signOut()

    // ---------- USER LOOKUP (added) ----------
    suspend fun getUserById(userId: String): User? {
        return try {
            db.collection("users").document(userId).get().await().toObject(User::class.java)
        } catch (_: Exception) { null }
    }

    // ---------- JOB METHODS ----------
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
        return db.collection("jobs").get().await().toObjects(Job::class.java)
    }

    suspend fun getPendingJobs(): List<Job> {
        return db.collection("jobs").whereEqualTo("status", "pending").get().await().toObjects(Job::class.java)
    }

    suspend fun getApprovedJobs(): List<Job> {
        return db.collection("jobs").whereEqualTo("status", "approved").get().await().toObjects(Job::class.java)
    }

    suspend fun getRejectedJobs(): List<Job> {
        return db.collection("jobs").whereEqualTo("status", "rejected").get().await().toObjects(Job::class.java)
    }

    suspend fun updateJobStatus(jobId: String, status: String) {
        db.collection("jobs").document(jobId).update("status", status).await()
    }

    suspend fun getJobsForEmployer(employerId: String): List<Job> {
        return db.collection("jobs").whereEqualTo("employerId", employerId).get().await().toObjects(Job::class.java)
    }

    suspend fun getJobById(jobId: String): Job? {
        return try {
            db.collection("jobs").document(jobId).get().await().toObject(Job::class.java)
        } catch (_: Exception) { null }
    }

    suspend fun updateJob(job: Job) {
        if (job.jobId.isBlank()) throw Exception("Job ID is required")
        db.collection("jobs").document(job.jobId).set(job).await()
    }

    // ---------- APPLICATION METHODS ----------
    suspend fun applyJob(application: Application) {
        val existingApplication = db.collection("applications")
            .whereEqualTo("jobId", application.jobId)
            .whereEqualTo("studentId", application.studentId)
            .get()
            .await()

        if (!existingApplication.isEmpty) {
            throw Exception("You have already applied for this job.")
        }

        val id = db.collection("applications").document().id
        val appWithId = application.copy(
            applicationId = id,
            appliedDate = Date(),
            status = "pending"
        )
        db.collection("applications").document(id).set(appWithId).await()
    }

    suspend fun getApplicationsForJob(jobId: String): List<Application> {
        return db.collection("applications").whereEqualTo("jobId", jobId).get().await().toObjects(Application::class.java)
    }

    suspend fun getApplicationsForApplicant(applicantId: String): List<Application> {
        return db.collection("applications").whereEqualTo("studentId", applicantId).get().await().toObjects(Application::class.java)
    }

    suspend fun getApplicationById(applicationId: String): Application? {
        return try {
            db.collection("applications").document(applicationId).get().await().toObject(Application::class.java)
        } catch (_: Exception) { null }
    }

    suspend fun updateApplicationStatus(applicationId: String, newStatus: String) {
        db.collection("applications").document(applicationId).update("status", newStatus).await()
    }

    suspend fun scheduleInterview(interview: InterviewSchedule) {
        val interviewId = db.collection("interviews").document().id
        val interviewWithId = interview.copy(
            interviewId = interviewId,
            createdAt = Date()
        )

        db.collection("interviews").document(interviewId).set(interviewWithId).await()
        db.collection("applications").document(interview.applicationId)
            .update("status", "interview_scheduled")
            .await()

        val notificationId = db.collection("notifications").document().id
        val notification = AppNotification(
            notificationId = notificationId,
            userId = interview.studentId,
            title = "Interview Scheduled",
            message = "Your interview is set for ${interview.interviewDate} at ${interview.interviewTime}.",
            createdAt = Date(),
            isRead = false
        )
        db.collection("notifications").document(notificationId).set(notification).await()
    }

    suspend fun getNotificationsForUser(userId: String): List<AppNotification> {
        return db.collection("notifications")
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects(AppNotification::class.java)
            .sortedByDescending { it.createdAt }
    }

    suspend fun markNotificationAsRead(notificationId: String) {
        db.collection("notifications").document(notificationId).update("isRead", true).await()
    }

    suspend fun getTotalApplicationsForEmployer(employerId: String): Int {
        val jobs = getJobsForEmployer(employerId)
        var total = 0
        for (job in jobs) {
            val snapshot = db.collection("applications").whereEqualTo("jobId", job.jobId).get().await()
            total += snapshot.size()
        }
        return total
    }

    // ---------- ADMIN ----------
    suspend fun getAllUsers(): List<User> {
        return db.collection("users").get().await().toObjects(User::class.java)
    }

    suspend fun updateUserRole(userId: String, newRole: String) {
        db.collection("users").document(userId).update("role", newRole).await()
    }
}

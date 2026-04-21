package com.example.graduatejobmatcher.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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
        auth.createUserWithEmailAndPassword(email, password).await()
        val uid = auth.uid ?: throw Exception("User creation failed")

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
        auth.signInWithEmailAndPassword(email, password).await()
        val uid = auth.uid ?: throw Exception("Login failed")
        val doc = db.collection("users").document(uid).get().await()
        return doc.toObject(User::class.java) ?: throw Exception("User data missing")
    }

    suspend fun getCurrentUser(): User? {
        val uid = auth.uid ?: return null
        val doc = db.collection("users").document(uid).get().await()
        return doc.toObject(User::class.java)
    }

    suspend fun updateCurrentUserProfile(updatedFields: Map<String, Any>) {
        val uid = auth.uid ?: throw Exception("User not logged in")
        db.collection("users").document(uid).update(updatedFields).await()
    }

    fun getCurrentUserId(): String? = auth.uid
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

    fun listenJobsByStatus(status: String, onResult: (List<Job>) -> Unit): ListenerRegistration {
        return db.collection("jobs")
            .whereEqualTo("status", status)
            .addSnapshotListener { snapshot, _ ->
                val jobs = snapshot
                    ?.toObjects(Job::class.java)
                    ?.sortedByDescending { it.postedDate }
                    ?: emptyList()
                onResult(jobs)
            }
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

    fun listenJobsForEmployer(employerId: String, onResult: (List<Job>) -> Unit): ListenerRegistration {
        return db.collection("jobs")
            .whereEqualTo("employerId", employerId)
            .addSnapshotListener { snapshot, _ ->
                val jobs = snapshot
                    ?.toObjects(Job::class.java)
                    ?.sortedByDescending { it.postedDate }
                    ?: emptyList()
                onResult(jobs)
            }
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

    suspend fun deleteJob(jobId: String) {
        if (jobId.isBlank()) throw Exception("Job ID is required")

        val applications = db.collection("applications")
            .whereEqualTo("jobId", jobId)
            .get()
            .await()
        val interviews = db.collection("interviews")
            .whereEqualTo("jobId", jobId)
            .get()
            .await()
        val notifications = db.collection("notifications")
            .whereEqualTo("jobId", jobId)
            .get()
            .await()

        val batch = db.batch()
        batch.delete(db.collection("jobs").document(jobId))
        applications.documents.forEach { batch.delete(it.reference) }
        interviews.documents.forEach { batch.delete(it.reference) }
        notifications.documents.forEach { batch.delete(it.reference) }
        batch.commit().await()
    }

    // ---------- APPLICATION METHODS ----------
    suspend fun applyJob(application: Application) {
        val existingApplication = db.collection("applications")
            .whereEqualTo("jobId", application.jobId)
            .whereEqualTo("studentId", application.studentId)
            .get()
            .await()

        if (!existingApplication.isEmpty) {
            throw Exception("Already submitted.")
        }

        val id = db.collection("applications").document().id
        val appWithId = application.copy(
            applicationId = id,
            applicantId = application.applicantId.ifBlank { application.studentId },
            appliedDate = Date(),
            status = "pending"
        )
        db.collection("applications").document(id).set(appWithId).await()
    }

    suspend fun getApplicationsForJob(jobId: String): List<Application> {
        return db.collection("applications").whereEqualTo("jobId", jobId).get().await().toObjects(Application::class.java)
    }

    fun listenApplicationsForJob(jobId: String, onResult: (List<Application>) -> Unit): ListenerRegistration {
        return db.collection("applications")
            .whereEqualTo("jobId", jobId)
            .addSnapshotListener { snapshot, _ ->
                val applications = snapshot
                    ?.toObjects(Application::class.java)
                    ?.sortedByDescending { it.appliedDate }
                    ?: emptyList()
                onResult(applications)
            }
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
        val job = getJobById(interview.jobId)
        val employer = getUserById(interview.employerId)

        db.collection("interviews").document(interviewId).set(interviewWithId).await()
        db.collection("applications").document(interview.applicationId)
            .update("status", "interview_scheduled")
            .await()

        val notificationId = db.collection("notifications").document().id
        val notification = AppNotification(
            notificationId = notificationId,
            userId = interview.studentId,
            title = "Interview Scheduled",
            message = "Your interview for ${job?.title?.ifBlank { "your application" } ?: "your application"} is set for ${interview.interviewDate} at ${interview.interviewTime}.",
            createdAt = Date(),
            isRead = false,
            type = "interview_scheduled",
            applicationId = interview.applicationId,
            jobId = interview.jobId,
            interviewId = interviewId,
            employerId = interview.employerId,
            employerName = employer?.name.orEmpty(),
            company = job?.company.orEmpty(),
            jobTitle = job?.title.orEmpty(),
            interviewDate = interview.interviewDate,
            interviewTime = interview.interviewTime,
            interviewType = interview.interviewType,
            meetingLink = interview.meetingLink,
            detailMessage = interview.message
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

    fun listenNotificationsForUser(userId: String, onResult: (List<AppNotification>) -> Unit): ListenerRegistration {
        return db.collection("notifications")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, _ ->
                val notifications = snapshot
                    ?.toObjects(AppNotification::class.java)
                    ?.sortedByDescending { it.createdAt }
                    ?: emptyList()
                onResult(notifications)
            }
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

    fun listenAllUsers(onResult: (List<User>) -> Unit): ListenerRegistration {
        return db.collection("users")
            .addSnapshotListener { snapshot, _ ->
                onResult(snapshot?.toObjects(User::class.java) ?: emptyList())
            }
    }

    suspend fun updateUserRole(userId: String, newRole: String) {
        db.collection("users").document(userId).update("role", newRole).await()
    }
}

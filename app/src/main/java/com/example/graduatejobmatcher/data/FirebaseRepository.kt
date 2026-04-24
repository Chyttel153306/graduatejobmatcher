package com.example.graduatejobmatcher.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.example.graduatejobmatcher.model.*
import android.util.Base64
import kotlinx.coroutines.tasks.await
import java.util.*

class FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val adminConfigDoc = db.collection("config").document("admin")

    // ---------- AUTH ----------

    // accepts all student fields and stores them in Firestore
    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: String,
        adminCreationPassword: String = "",
        degree: String = "",
        institution: String = "",
        graduationDate: String = "",
        skills: List<String> = emptyList()
    ) {
        val trimmedRole = role.trim().lowercase()
        val normalizedAdminPassword = adminCreationPassword.trim()
        var shouldSeedAdminPassword = false

        if (trimmedRole == "admin") {
            val existingAdmins = db.collection("users")
                .whereEqualTo("role", "admin")
                .limit(1)
                .get()
                .await()
            val configuredPassword = getAdminCreationPassword().orEmpty()

            if (existingAdmins.isEmpty) {
                if (normalizedAdminPassword.isBlank()) {
                    throw Exception("Create Admin Password is required.")
                }
                shouldSeedAdminPassword = true
            } else {
                if (configuredPassword.isBlank()) {
                    throw Exception("Admin password is not configured.")
                }
                if (normalizedAdminPassword != configuredPassword) {
                    throw Exception("Incorrect admin password.")
                }
            }
        }

        auth.createUserWithEmailAndPassword(email, password).await()
        val uid = auth.uid ?: throw Exception("User creation failed")

        val user = User(
            userId = uid,
            name = name,
            email = email,
            role = trimmedRole,
            degree = degree,
            institution = institution,
            graduationDate = graduationDate,
            skills = skills
        )
        db.collection("users").document(uid).set(user).await()

        if (trimmedRole == "admin" && shouldSeedAdminPassword) {
            adminConfigDoc.set(
                mapOf(
                    "createAdminPassword" to normalizedAdminPassword,
                    "updatedAt" to Date()
                )
            ).await()
        }
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

    suspend fun getAdminCreationPassword(): String? {
        return try {
            adminConfigDoc.get().await().getString("createAdminPassword")
        } catch (_: Exception) {
            null
        }
    }

    suspend fun updateAdminCreationPassword(newPassword: String) {
        val uid = auth.uid ?: throw Exception("User not logged in")
        val currentUser = getCurrentUser() ?: throw Exception("User not found")
        if (currentUser.role != "admin") {
            throw Exception("Only admins can update the admin password.")
        }
        adminConfigDoc.set(
            mapOf(
                "createAdminPassword" to newPassword.trim(),
                "updatedAt" to Date(),
                "updatedBy" to uid
            )
        ).await()
    }

    suspend fun uploadCurrentUserProfileImage(imageBytes: ByteArray): String {
        val uid = auth.uid ?: throw Exception("User not logged in")
        val encodedImage = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
        db.collection("users").document(uid)
            .update("profileImageBase64", encodedImage)
            .await()
        return encodedImage
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

    suspend fun getInterviewByApplicationId(applicationId: String): InterviewSchedule? {
        return try {
            db.collection("interviews")
                .whereEqualTo("applicationId", applicationId)
                .get()
                .await()
                .toObjects(InterviewSchedule::class.java)
                .firstOrNull()
        } catch (_: Exception) { null }
    }

    suspend fun updateApplicationStatus(applicationId: String, newStatus: String) {
        db.collection("applications").document(applicationId).update("status", newStatus).await()
    }

    suspend fun scheduleInterview(interview: InterviewSchedule) {
        val existingInterview = getInterviewByApplicationId(interview.applicationId)
        val interviewId = existingInterview?.interviewId
            ?.takeIf { it.isNotBlank() }
            ?: db.collection("interviews").document().id
        val now = Date()
        val interviewWithId = interview.copy(
            interviewId = interviewId,
            createdAt = existingInterview?.createdAt ?: now
        )
        val job = getJobById(interview.jobId)
        val employer = getUserById(interview.employerId)

        db.collection("interviews").document(interviewId).set(interviewWithId).await()
        db.collection("applications").document(interview.applicationId)
            .update("status", "interview_scheduled")
            .await()

        val existingNotifications = db.collection("notifications")
            .whereEqualTo("applicationId", interview.applicationId)
            .whereEqualTo("userId", interview.studentId)
            .whereEqualTo("type", "interview_scheduled")
            .get()
            .await()

        val primaryNotificationDoc = existingNotifications.documents.firstOrNull()
        val notificationId = primaryNotificationDoc?.id ?: db.collection("notifications").document().id
        val notification = AppNotification(
            notificationId = notificationId,
            userId = interview.studentId,
            title = if (primaryNotificationDoc == null) "Interview Scheduled" else "Interview Updated",
            message = "Your interview for ${job?.title?.ifBlank { "your application" } ?: "your application"} is set for ${interview.interviewDate} at ${interview.interviewTime}.",
            createdAt = now,
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

        val batch = db.batch()
        batch.set(db.collection("notifications").document(notificationId), notification)
        existingNotifications.documents
            .drop(1)
            .forEach { batch.delete(it.reference) }
        batch.commit().await()
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

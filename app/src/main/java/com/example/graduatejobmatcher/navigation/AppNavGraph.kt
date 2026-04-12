package com.example.graduatejobmatcher.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.graduatejobmatcher.screens.commonscreen.JobDetailsScreen
import com.example.graduatejobmatcher.screens.commonscreen.LoginScreen
import com.example.graduatejobmatcher.screens.commonscreen.ProfileScreen
import com.example.graduatejobmatcher.screens.commonscreen.RegistrationScreen
import com.example.graduatejobmatcher.screens.admin.AdminDashboardScreen
import com.example.graduatejobmatcher.screens.admin.ManageUsersScreen
import com.example.graduatejobmatcher.screens.admin.PendingJobsScreen
import com.example.graduatejobmatcher.screens.employer.EmployerApplicantsScreen
import com.example.graduatejobmatcher.screens.employer.EmployerDashboardScreen
import com.example.graduatejobmatcher.screens.employer.PostJobScreen
import com.example.graduatejobmatcher.screens.employer.ViewApplicantsScreen
import com.example.graduatejobmatcher.screens.student.ApplyJobScreen
import com.example.graduatejobmatcher.screens.student.JobListScreen
import com.example.graduatejobmatcher.screens.student.StudentDashboardScreen
import com.example.graduatejobmatcher.viewmodel.AppViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object StudentDashboard : Screen("student_dashboard")
    object EmployerDashboard : Screen("employer_dashboard")
    object AdminDashboard : Screen("admin_dashboard")
    object PostJob : Screen("post_job")
    object JobList : Screen("job_list")
    object JobDetails : Screen("job_details/{jobId}") {
        fun passJobId(jobId: String) = "job_details/$jobId"
    }
    object ApplyJob : Screen("apply_job/{jobId}") {
        fun passJobId(jobId: String) = "apply_job/$jobId"
    }
    object ViewApplicants : Screen("view_applicants/{jobId}") {
        fun passJobId(jobId: String) = "view_applicants/$jobId"
    }
    object ManageUsers : Screen("manage_users")
    object Profile : Screen("profile")
    object EmployerApplicants : Screen("employer_applicants")
    object EmployerJobs : Screen("employer_jobs")
    object PendingJobs : Screen("pending_jobs")
}

@Composable
fun AppNavGraph(navController: NavHostController, viewModel: AppViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController, viewModel)
        }
        composable(Screen.Register.route) {
            RegistrationScreen(navController, viewModel)
        }
        composable(Screen.StudentDashboard.route) {
            StudentDashboardScreen(navController, viewModel)
        }
        composable(Screen.EmployerDashboard.route) {
            EmployerDashboardScreen(navController, viewModel)
        }
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(navController)
        }
        composable(Screen.PostJob.route) {
            PostJobScreen(navController, viewModel)
        }
        composable(Screen.JobList.route) {
            JobListScreen(navController, viewModel)
        }
        composable(Screen.JobDetails.route) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            JobDetailsScreen(navController, viewModel, jobId)
        }
        composable(Screen.ApplyJob.route) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            ApplyJobScreen(navController, viewModel, jobId)
        }
        composable(Screen.ViewApplicants.route) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            ViewApplicantsScreen(navController, viewModel, jobId)
        }
        composable(Screen.ManageUsers.route) {
            ManageUsersScreen(navController, viewModel)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController, viewModel)
        }
        composable(Screen.EmployerApplicants.route) {
            EmployerApplicantsScreen(navController, viewModel)
        }
        composable(Screen.EmployerJobs.route) {
            JobListScreen(navController, viewModel)
        }
        composable(Screen.PendingJobs.route) {
            // ✅ viewModel now passed so approve/reject/view all work
            PendingJobsScreen(navController, viewModel)
        }
    }
}
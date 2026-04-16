package com.example.graduatejobmatcher.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.graduatejobmatcher.screens.commonscreen.*
import com.example.graduatejobmatcher.screens.admin.*
import com.example.graduatejobmatcher.screens.employer.*
import com.example.graduatejobmatcher.screens.student.*
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

    object ApplicantDetails : Screen("applicant_details/{applicationId}") {
        fun passApplicationId(id: String) = "applicant_details/$id"
    }

    object EmployerJobDetails : Screen("employer_job_details/{jobId}") {
        fun passJobId(jobId: String) = "employer_job_details/$jobId"
    }

    object EmployerUpdateJob : Screen("employer_update_job/{jobId}") {
        fun passJobId(jobId: String) = "employer_update_job/$jobId"
    }

    object ManageUsers : Screen("manage_users")
    object Profile : Screen("profile")
    object EmployerApplicants : Screen("employer_applicants")
    object ManageJobListings : Screen("manage_job_listings")
    object EmployerJobs : Screen("employer_jobs")
    object PendingJobs : Screen("pending_jobs")
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    viewModel: AppViewModel
) {
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

        composable(Screen.ApplicantDetails.route) { backStackEntry ->
            val applicationId =
                backStackEntry.arguments?.getString("applicationId") ?: ""

            ViewApplicantsScreen(navController, viewModel, applicationId)
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

        composable(Screen.ManageJobListings.route) {
            ManageJobListingsScreen(navController, viewModel)
        }

        composable(Screen.EmployerJobDetails.route) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            EmployerJobDetailsScreen(navController, viewModel, jobId)
        }

        composable(Screen.EmployerUpdateJob.route) { backStackEntry ->
            val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
            EmployerUpdateJobScreen(navController, viewModel, jobId)
        }

        composable(Screen.EmployerJobs.route) {
            JobListScreen(navController, viewModel)
        }

        composable(Screen.PendingJobs.route) {
            PendingJobsScreen(navController, viewModel)
        }
    }
}

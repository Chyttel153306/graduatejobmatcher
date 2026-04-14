package com.example.graduatejobmatcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.graduatejobmatcher.navigation.AppNavGraph
import com.example.graduatejobmatcher.ui.theme.GraduateJobMatcherTheme
import com.example.graduatejobmatcher.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GraduateJobMatcherTheme {

                val navController = rememberNavController()
                val viewModel: AppViewModel = viewModel()

                AppNavGraph(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}
package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.MainViewModel
import com.example.ui.screens.WelcomeScreen
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.admin.CreateElectionScreen
import com.example.ui.screens.admin.LiveResultsScreen
import com.example.ui.screens.admin.AddCandidatesScreen
import com.example.ui.screens.admin.AddStudentsScreen
import com.example.ui.screens.voter.VoterLoginScreen
import com.example.ui.screens.voter.VotingBoothScreen
import com.example.ui.screens.voter.VotingSuccessScreen

@Composable
fun EduVoteNavGraph(startDestination: String = "welcome") {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()

    NavHost(navController = navController, startDestination = startDestination) {
        composable("welcome") {
            WelcomeScreen(
                onAdminClick = { navController.navigate("admin_dashboard") },
                onVoterClick = { navController.navigate("voter_login") }
            )
        }
        composable("admin_dashboard") {
            AdminDashboardScreen(
                viewModel = viewModel,
                onCreateElection = { navController.navigate("create_election") },
                onAddCandidates = { navController.navigate("add_candidates") },
                onAddStudents = { navController.navigate("add_students") },
                onViewResults = { electionId -> navController.navigate("live_results/$electionId") },
                onVoterMode = { navController.navigate("voter_login") }
            )
        }
        composable("create_election") {
            CreateElectionScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("add_candidates") {
            AddCandidatesScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("add_students") {
            AddStudentsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable("voter_login") {
            VoterLoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { electionId -> 
                    navController.navigate("voting_booth/$electionId")
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            "voting_booth/{electionId}",
            arguments = listOf(navArgument("electionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val electionId = backStackEntry.arguments?.getString("electionId") ?: ""
            VotingBoothScreen(
                electionId = electionId,
                viewModel = viewModel,
                onVoteSuccess = {
                    navController.navigate("voting_success") {
                        popUpTo("voting_booth/$electionId") { inclusive = true }
                    }
                }
            )
        }
        composable("voting_success") {
            VotingSuccessScreen(
                onComplete = {
                    navController.navigate("voter_login") {
                        popUpTo("welcome") { inclusive = false }
                    }
                }
            )
        }
        composable(
            "live_results/{electionId}",
            arguments = listOf(navArgument("electionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val electionId = backStackEntry.arguments?.getString("electionId") ?: ""
            LiveResultsScreen(
                electionId = electionId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

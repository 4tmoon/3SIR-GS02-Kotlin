package com.github.atmoon.zeloapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.atmoon.zeloapp.ui.screens.CreateIssueScreen
import com.github.atmoon.zeloapp.ui.screens.IssueDetailScreen
import com.github.atmoon.zeloapp.ui.screens.MainScreen
import com.github.atmoon.zeloapp.ui.screens.WelcomeScreen
import com.github.atmoon.zeloapp.ui.viewmodel.ZeloViewModel

object Routes {
    const val WELCOME = "welcome"
    const val MAIN = "main"
    const val CREATE = "create"
    const val DETAIL = "detail/{issueId}"

    fun detail(issueId: String) = "detail/$issueId"
}

@Composable
fun ZeloNavGraph(viewModel: ZeloViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.WELCOME) {
        composable(Routes.WELCOME) {
            WelcomeScreen(
                onStartClick = {
                    navController.navigate(Routes.MAIN) {
                        popUpTo(Routes.WELCOME) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.MAIN) {
            MainScreen(
                viewModel = viewModel,
                onOpenCreate = { navController.navigate(Routes.CREATE) },
                onOpenDetail = { issueId -> navController.navigate(Routes.detail(issueId)) }
            )
        }
        composable(Routes.CREATE) {
            CreateIssueScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("issueId") { type = NavType.StringType })
        ) { backStackEntry ->
            val issueId = backStackEntry.arguments?.getString("issueId").orEmpty()
            IssueDetailScreen(
                issueId = issueId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
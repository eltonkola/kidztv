package com.mrkola.kidztv.ui


import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mrkola.kidztv.data.VideoRepository
import org.koin.compose.koinInject

@Composable
fun KidzTVApp(
) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                onVideoClick = { video ->
                    navController.navigate("player/${video.id}")
                },
                onParentalControlsClick = {
                    navController.navigate("math_challenge")
                }
            )
        }

        composable("player/{videoId}") { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId")?.toLongOrNull()
            videoId?.let {
                PlayerScreen(
                    initialVideoId = it,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable("math_challenge") {
            MathChallengeScreen(
                onSuccess = {
                    navController.navigate("parental") {
                        popUpTo("math_challenge") { inclusive = true }
                    }
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        composable("parental") {
            ParentalControlsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("about") {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
package com.mrkola.kidztv.ui


import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mrkola.kidztv.data.VideoRepository

@Composable
fun KidzTVApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val videoRepository = remember { VideoRepository(context) }

    LaunchedEffect(Unit) {
        val videos = videoRepository.getAllVideos()
        if (videos.isEmpty()) {
            navController.navigate("math_challenge") {
                popUpTo("main") { inclusive = true }
            }
        }
    }

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                videoRepository = videoRepository,
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
                    videoRepository = videoRepository,
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
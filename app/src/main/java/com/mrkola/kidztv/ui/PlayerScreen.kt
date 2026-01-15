package com.mrkola.kidztv.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Forward30
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.mrkola.kidztv.data.Video
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PlayerScreen(
    initialVideoId: Long,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = koinViewModel(parameters = { parametersOf(initialVideoId) })
) {
    val uiState by viewModel.uiState.collectAsState()

    // Auto-hide controls
    LaunchedEffect((uiState as? PlayerUiState.Ready)?.showControls) {
        val showControls = (uiState as? PlayerUiState.Ready)?.showControls ?: false
        val isLocked = (uiState as? PlayerUiState.Ready)?.isLocked ?: false
        if (showControls && !isLocked) {
            delay(5000)
            viewModel.toggleControls()
        }
    }

    // Handle back press
    val isLocked = (uiState as? PlayerUiState.Ready)?.isLocked ?: false
    BackHandler(enabled = true) {
        if (isLocked) {
            // Do nothing when locked - prevent back navigation
            return@BackHandler
        }
        onBack()
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gbGradient)
    ) {

        when (val state = uiState) {
            is PlayerUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            is PlayerUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is PlayerUiState.Ready -> {

                val state = uiState as PlayerUiState.Ready

                Column(modifier = Modifier.fillMaxSize()) {
                    // Player Container
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(if (state.showControls) 0.6f else 1f)
                            .background(Color.Black)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        if (!isLocked) {
                                            viewModel.toggleControls()
                                        }
                                    },
                                    onDoubleTap = {
                                        if (!isLocked) {
                                            if (!state.isLocked) {
                                                viewModel.togglePlayPause()
                                            }
                                        }
                                    }
                                )
                            }
                    ) {
                        // Video Player
                        AndroidView(
                            factory = { ctx ->
                                PlayerView(ctx).apply {
                                    player = viewModel.exoPlayer
                                    useController = false

                                    layoutParams = FrameLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Top Controls
                        androidx.compose.animation.AnimatedVisibility(
                            visible = state.showControls && !state.isLocked,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Black.copy(alpha = 0.8f),
                                                Color.Black.copy(alpha = 0.2f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            ) {
                                IconButton(
                                    onClick = onBack,
                                    modifier = Modifier
                                        .align(Alignment.CenterStart)
                                        .padding(start = 16.dp)
                                        .size(48.dp)
                                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                state.currentVideo?.let { video ->
                                    Text(
                                        text = video.title,
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(horizontal = 80.dp)
                                    )
                                }

                                IconButton(
                                    onClick = { viewModel.toggleLock() },
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .padding(end = 16.dp, top = 16.dp)
                                        .size(48.dp)
                                        .background(
                                            if (isLocked) Color(0xFFFF5252) else Color(0xFF4CAF50),
                                            CircleShape
                                        )
                                ) {
                                    Icon(
                                        if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                                        contentDescription = if (isLocked) "Unlock" else "Lock",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        // Center Playback Controls
                        androidx.compose.animation.AnimatedVisibility(
                            visible = state.showControls && !state.isLocked,
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut(),
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(32.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Replay 30 seconds
                                ControlButton(
                                    icon = Icons.Default.Replay,
                                    onClick = {
                                        val newPosition =
                                            (viewModel.exoPlayer.currentPosition - 30000).coerceAtLeast(
                                                0
                                            )
                                        viewModel.exoPlayer.seekTo(newPosition)
                                    },
                                    color = Color(0xFFFF9800)
                                )

                                ControlButton(
                                    icon = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    onClick = {
                                        viewModel.togglePlayPause()
                                    },
                                    size = 72.dp,
                                    iconSize = 36.dp,
                                    color = Color(0xFF4CAF50)
                                )

                                // Forward 30 seconds
                                ControlButton(
                                    icon = Icons.Default.Forward30,
                                    onClick = {
                                        val newPosition =
                                            (viewModel.exoPlayer.currentPosition + 30000).coerceAtMost(
                                                state.duration
                                            )
                                        viewModel.exoPlayer.seekTo(newPosition)
                                    },
                                    color = Color(0xFF2196F3)
                                )
                            }
                        }

                        // Bottom Controls with Progress Bar
                        androidx.compose.animation.AnimatedVisibility(
                            visible = state.showControls && !isLocked,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                            exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.2f),
                                                Color.Black.copy(alpha = 0.8f)
                                            )
                                        )
                                    )
                                    .padding(16.dp)
                            ) {
                                // Progress Bar
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Slider(
                                        value = if (state.duration > 0) state.currentPosition.toFloat() else 0f,
                                        onValueChange = { newValue ->
                                            viewModel.exoPlayer.seekTo(newValue.toLong())
                                        },
                                        valueRange = 0f..state.duration.toFloat().coerceAtLeast(1f),
                                        colors = SliderDefaults.colors(
                                            thumbColor = Color(0xFFFF9800),
                                            activeTrackColor = Color(0xFFFF9800),
                                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                                        )
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = formatDuration(state.currentPosition),
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = formatDuration(state.duration),
                                            color = Color.White,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Locked Indicator - Make it tappable to unlock
                        if (isLocked) {
                            // Lock indicator in top right
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(32.dp)
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onDoubleTap = {
                                                viewModel.toggleLock()
                                            }
                                        )
                                    }
                            ) {
                                Surface(
                                    color = Color(0xFFFF5252).copy(alpha = 0.9f),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp, 12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            "Double tap to unlock",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Loading Indicator
                        if (state.isBuffering) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(80.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFFFF9800),
                                    strokeWidth = 4.dp,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    // Video List - Only show when controls are visible or minimized
                    AnimatedVisibility(
                        visible = !isLocked && (state.showControls),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.videos) { video ->
                                VideoThumbnail(
                                    video = video,
                                    isSelected = video.id == state.currentVideo?.id,
                                    duration = video.duration,
                                    onClick = {
                                        viewModel.changeVideo(video.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }

        }
    }

}

@Composable
private fun ControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    size: Dp = 56.dp,
    iconSize: Dp = 28.dp,
    color: Color = Color.White
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(size)
            .shadow(8.dp, CircleShape)
            .background(color, CircleShape)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun VideoThumbnail(video: Video, isSelected: Boolean, duration: Long, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(12.dp))
                .shadow(
                    elevation = if (isSelected) 8.dp else 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    spotColor = if (isSelected) Color(0xFFFF9800) else Color(0x1AFFFFFF)
                )
        ) {
            if (video.thumbnailPath != null) {
                Image(
                    painter = rememberAsyncImagePainter(video.thumbnailPath),
                    contentDescription = video.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF2196F3),
                                    Color(0xFF1976D2)
                                )
                            ),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayCircle,
                    contentDescription = "Play",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(36.dp)
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x80FF9800))
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Text(
                text = formatDuration(duration),
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.align (Alignment.BottomEnd).padding(16.dp)
            )
        }

        Text(
            text = video.title,
            modifier = Modifier
                .padding(top = 6.dp)
                .fillMaxWidth(),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

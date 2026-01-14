package com.mrkola.kidztv.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.mrkola.kidztv.data.Video
import com.mrkola.kidztv.data.VideoRepository
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.Dp
import androidx.media3.ui.AspectRatioFrameLayout

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PlayerScreen(
    videoRepository: VideoRepository,
    initialVideoId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val videos = remember { videoRepository.getAllVideos() }
    var currentVideo by remember { mutableStateOf(videos.find { it.id == initialVideoId }) }
    var isMinimized by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }
    var isLocked by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }
    var isBuffering by remember { mutableStateOf(false) }

    // Intercept back press when locked
    BackHandler(enabled = isLocked) {
        // Do nothing when locked - prevent back navigation
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            currentVideo?.let {
                setMediaItem(MediaItem.fromUri(it.filePath))
                prepare()
                playWhenReady = true
            }
            repeatMode = Player.REPEAT_MODE_ONE

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        duration = this@apply.duration
                    }
                    isPlaying = playbackState == Player.STATE_READY && this@apply.playWhenReady
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    isPlaying = playing
                }
            })
        }
    }

    // Update progress
    LaunchedEffect(Unit) {
        while (true) {
            currentPosition = exoPlayer.currentPosition
            duration = exoPlayer.duration
            delay(100)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(showControls) {
        if (showControls && !isLocked && !isMinimized) {
            delay(10_000)
            showControls = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gbGradient)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            // Player Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (isMinimized || showControls) 0.6f else 1f)
                    .background(Color.Black)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                if (!isLocked) {
                                    showControls = !showControls
                                }
                            },
                            onDoubleTap = {
                                if (!isLocked) {
                                    if (exoPlayer.isPlaying) {
                                        exoPlayer.pause()
                                        isPlaying = false
                                    } else {
                                        exoPlayer.play()
                                        isPlaying = true
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
                            player = exoPlayer
                            useController = false

                            layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    update = {
                        it.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT)
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Top Controls
                androidx.compose.animation.AnimatedVisibility(
                    visible = showControls && !isLocked,
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
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        currentVideo?.let { video ->
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
                            onClick = { isLocked = !isLocked },
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
                    visible = showControls && !isLocked,
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
                                    (exoPlayer.currentPosition - 30000).coerceAtLeast(0)
                                exoPlayer.seekTo(newPosition)
                            },
                            color = Color(0xFFFF9800)
                        )

                        ControlButton(
                            icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            onClick = {
                                if (exoPlayer.isPlaying) {
                                    exoPlayer.pause()
                                    isPlaying = false
                                } else {
                                    exoPlayer.play()
                                    isPlaying = true
                                }
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
                                    (exoPlayer.currentPosition + 30000).coerceAtMost(duration)
                                exoPlayer.seekTo(newPosition)
                            },
                            color = Color(0xFF2196F3)
                        )
                    }
                }

                // Bottom Controls with Progress Bar
                androidx.compose.animation.AnimatedVisibility(
                    visible = showControls && !isLocked,
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
                                value = if (duration > 0) currentPosition.toFloat() else 0f,
                                onValueChange = { newValue ->
                                    exoPlayer.seekTo(newValue.toLong())
                                },
                                valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
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
                                    text = formatDuration(currentPosition),
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = formatDuration(duration),
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
                                        isLocked = false
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
                if (isBuffering) {
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
                visible = !isLocked && (isMinimized || showControls),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(videos) { video ->
                        VideoThumbnail(
                            video = video,
                            isSelected = video.id == currentVideo?.id,
                            onClick = {
                                currentVideo = video
                                exoPlayer.setMediaItem(MediaItem.fromUri(video.filePath))
                                exoPlayer.prepare()
                                exoPlayer.play()
                                isPlaying = true
                                showControls = false
                            }
                        )
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
fun VideoThumbnail(video: Video, isSelected: Boolean, onClick: () -> Unit) {
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

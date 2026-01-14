package com.mrkola.kidztv.ui

import android.view.ViewGroup
import android.widget.FrameLayout
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
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.mrkola.kidztv.data.Video
import com.mrkola.kidztv.data.VideoRepository
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.unit.Dp

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

    val screenWidth = LocalResources.current.displayMetrics.widthPixels.dp
    val screenHeight = LocalResources.current.displayMetrics.heightPixels.dp

    // Animation values for minimized state
    val playerWidth by animateDpAsState(
        targetValue = if (isMinimized) 280.dp else screenWidth,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
    )
    val playerHeight by animateDpAsState(
        targetValue = if (isMinimized) 180.dp else screenHeight,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
    )
    val playerCornerRadius by animateDpAsState(
        targetValue = if (isMinimized) 16.dp else 0.dp,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
    )
    val playerElevation by animateFloatAsState(
        targetValue = if (isMinimized) 16f else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
    )

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            currentVideo?.let {
                setMediaItem(MediaItem.fromUri(it.filePath))
                prepare()
                playWhenReady = true
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(showControls) {
        if (showControls && !isLocked && !isMinimized) {
            delay(5000)
            showControls = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E),  // Dark Blue
                        Color(0xFF283593),  // Blue
                        Color(0xFF3949AB)   // Light Blue
                    )
                )
            )
    ) {
        // Background pattern for kid-friendly look
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x20FFFFFF),
                            Color(0x08FFFFFF),
                            Color(0x00FFFFFF)
                        ),
                        radius = 800f
                    )
                )
        )

        // Animated Player Container
        Box(
            modifier = Modifier
                .align(
                    if (isMinimized) Alignment.TopStart else Alignment.Center
                )
                .offset(
                    x = if (isMinimized) 16.dp else 0.dp,
                    y = if (isMinimized) 16.dp else 0.dp
                )
                .width(playerWidth)
                .height(playerHeight)
                .shadow(
                    elevation = playerElevation.dp,
                    shape = RoundedCornerShape(playerCornerRadius)
                )
                .clip(RoundedCornerShape(playerCornerRadius))
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            if (!isLocked) {
                                if (isMinimized) {
                                    isMinimized = false
                                } else {
                                    showControls = !showControls
                                }
                            }
                        },
                        onDoubleTap = {
                            if (!isLocked && !isMinimized) {
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
                modifier = Modifier.fillMaxSize()
            )

            // Top Controls
            AnimatedVisibility(
                visible = showControls && !isLocked && !isMinimized,
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
                    // Back Button
                    IconButton(
                        onClick = {
                            if (isMinimized) {
                                onBack()
                            } else {
                                onBack()
                            }
                        },
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

                    // Video Title
                    currentVideo?.let { video ->
                        Text(
                            text = video.title,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 8.dp)
                        )
                    }

                    // Lock Button
                    IconButton(
                        onClick = { isLocked = !isLocked },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 16.dp)
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
            AnimatedVisibility(
                visible = showControls && !isLocked && !isMinimized,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Button
                    ControlButton(
                        icon = Icons.Default.SkipPrevious,
                        onClick = {
                            val currentIndex = videos.indexOf(currentVideo)
                            if (currentIndex > 0) {
                                currentVideo = videos[currentIndex - 1]
                                currentVideo?.let {
                                    exoPlayer.setMediaItem(MediaItem.fromUri(it.filePath))
                                    exoPlayer.prepare()
                                    exoPlayer.play()
                                    isPlaying = true
                                }
                            }
                        },
                        color = Color(0xFFFF9800) // Orange
                    )

                    // Play/Pause Button
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
                        color = Color(0xFF4CAF50) // Green
                    )

                    // Next Button
                    ControlButton(
                        icon = Icons.Default.SkipNext,
                        onClick = {
                            val currentIndex = videos.indexOf(currentVideo)
                            if (currentIndex < videos.size - 1) {
                                currentVideo = videos[currentIndex + 1]
                                currentVideo?.let {
                                    exoPlayer.setMediaItem(MediaItem.fromUri(it.filePath))
                                    exoPlayer.prepare()
                                    exoPlayer.play()
                                    isPlaying = true
                                }
                            }
                        },
                        color = Color(0xFF2196F3) // Blue
                    )
                }
            }

            // Minimize Button (when in fullscreen)
            if (!isMinimized && showControls && !isLocked) {
                IconButton(
                    onClick = { isMinimized = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.ArrowDownward,
                        contentDescription = "Minimize",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Locked Indicator
            AnimatedVisibility(
                visible = isLocked,
                modifier = Modifier.align(Alignment.Center),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Surface(
                    color = Color(0xFFFF5252).copy(alpha = 0.9f),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp, 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Screen Locked",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Restore Button (when minimized)
        AnimatedVisibility(
            visible = isMinimized,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically(),
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            IconButton(
                onClick = { isMinimized = false },
                modifier = Modifier
                    .padding(start = 24.dp, bottom = 24.dp)
                    .size(56.dp)
                    .background(Color(0xFFFF9800), CircleShape)
                    .shadow(8.dp, CircleShape)
            ) {
                Icon(
                    Icons.Default.Fullscreen,
                    contentDescription = "Restore",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Video List at Bottom (only when minimized or showing controls)
        if (!isLocked && (isMinimized || showControls)) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Color(0xAA1A237E), // Semi-transparent dark blue
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Section Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "More Videos",
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        if (isMinimized) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Video Thumbnails Row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
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
                                    if (isMinimized) {
                                        isMinimized = false
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Loading Indicator
        if (!exoPlayer.isPlaying && isPlaying) {
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
            .width(180.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(16.dp))
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = if (isSelected) Color(0xFFFF9800) else Color(0x1AFFFFFF)
                )
        ) {
            if (video.thumbnailPath != null) {
                Image(
                    painter = rememberAsyncImagePainter(video.thumbnailPath),
                    contentDescription = video.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
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
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            // Play Button Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clip(RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayCircle,
                    contentDescription = "Play",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(48.dp)
                )
            }

            // Selection Indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x80FF9800))
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        }

        // Video Title
        Text(
            text = video.title,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2
        )

        // Video Duration
        Text(
            text = formatDuration(video.duration),
            modifier = Modifier.padding(top = 4.dp),
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp
        )
    }
}


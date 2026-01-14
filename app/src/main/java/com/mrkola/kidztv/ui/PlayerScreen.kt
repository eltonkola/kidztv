package com.mrkola.kidztv.ui


import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.mrkola.kidztv.data.Video
import com.mrkola.kidztv.data.VideoRepository
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    videoRepository: VideoRepository,
    initialVideoId: Long,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val videos = remember { videoRepository.getAllVideos() }
    var currentVideo by remember { mutableStateOf(videos.find { it.id == initialVideoId }) }
    var showControls by remember { mutableStateOf(false) }
    var isLocked by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }

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
        if (showControls && !isLocked) {
            delay(5000)
            showControls = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
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
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = !isLocked) {
                    showControls = !showControls
                }
        )

        // Lock/Unlock Button
        IconButton(
            onClick = { isLocked = !isLocked },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(
                    if (isLocked) Color.Red else Color.Green,
                    CircleShape
                )
        ) {
            Icon(
                if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
                contentDescription = null,
                tint = Color.White
            )
        }

        // Back Button (only when not locked)
        if (!isLocked) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
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
                color = Color.Red,
                shape = RoundedCornerShape(50.dp)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp, 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Locked", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                }
            }
        }

        // Playback Controls
        AnimatedVisibility(
            visible = showControls && !isLocked,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                IconButton(
                    onClick = {
                        val currentIndex = videos.indexOf(currentVideo)
                        if (currentIndex > 0) {
                            currentVideo = videos[currentIndex - 1]
                            currentVideo?.let {
                                exoPlayer.setMediaItem(MediaItem.fromUri(it.filePath))
                                exoPlayer.prepare()
                                exoPlayer.play()
                            }
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = null, modifier = Modifier.size(32.dp))
                }

                IconButton(
                    onClick = {
                        if (exoPlayer.isPlaying) {
                            exoPlayer.pause()
                            isPlaying = false
                        } else {
                            exoPlayer.play()
                            isPlaying = true
                        }
                    },
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }

                IconButton(
                    onClick = {
                        val currentIndex = videos.indexOf(currentVideo)
                        if (currentIndex < videos.size - 1) {
                            currentVideo = videos[currentIndex + 1]
                            currentVideo?.let {
                                exoPlayer.setMediaItem(MediaItem.fromUri(it.filePath))
                                exoPlayer.prepare()
                                exoPlayer.play()
                            }
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = null, modifier = Modifier.size(32.dp))
                }
            }
        }

        // Video List at Bottom
        if (!isLocked) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                color = Color.Black.copy(alpha = 0.8f)
            ) {
                LazyRow(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VideoThumbnail(video: Video, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .then(
                    if (isSelected) Modifier.background(Color.Yellow)
                    else Modifier
                )
        ) {
            if (video.thumbnailPath != null) {
                Image(
                    painter = rememberAsyncImagePainter(video.thumbnailPath),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                }
            }
        }
        Text(
            text = video.title,
            modifier = Modifier.padding(top = 4.dp),
            color = Color.White,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 2
        )
    }
}
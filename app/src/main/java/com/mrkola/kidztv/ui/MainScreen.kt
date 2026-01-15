package com.mrkola.kidztv.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.mrkola.kidztv.R
import com.mrkola.kidztv.data.Video

val gbGradient =  Brush.verticalGradient(
    colors = listOf(
        Color(0xFFAB47BC),
        Color(0xFFEC407A),
        Color(0xFFEF5350)
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onVideoClick: (Video) -> Unit,
    onParentalControlsClick: () -> Unit,
    viewModel: MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState is MainUiState.Loading
    val alpha by animateFloatAsState(if (isLoading) 0.5f else 1f, label = "")

    LaunchedEffect(uiState) {
        if (uiState is MainUiState.Success && (uiState as MainUiState.Success).videos.isEmpty()) {
            onParentalControlsClick()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gbGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.tv_logo_horizontal),
                    contentDescription = "KidzTV",
                    modifier = Modifier.height(40.dp)
                )

                Row {

                    
                    IconButton(
                        onClick = onParentalControlsClick,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White
                        ),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(alpha)
            ) {
                when (val state = uiState) {
                    is MainUiState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    }
                    is MainUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Error loading videos",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = state.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                IconButton(
                                    onClick = { viewModel.loadVideos() },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Retry",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                    is MainUiState.Success -> {
                        if (state.videos.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No videos available",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(minSize = 180.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.videos) { video ->
                                    VideoCard(
                                        video = video,
                                        onClick = { onVideoClick(video) },
                                        isLoading = isLoading
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoCard(
    video: Video,
    onClick: () -> Unit,
    isLoading: Boolean = false
) {
    val painter = rememberAsyncImagePainter(
        model = video.thumbnailPath,
        contentScale = ContentScale.Crop
    )
    val isImageLoading = painter.state is AsyncImagePainter.State.Loading
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable(enabled = !isLoading && !isImageLoading, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (isLoading || isImageLoading) {
                    // Shimmer effect for loading state
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    )
                }
                
                if (video.thumbnailPath != null) {
                    Image(
                        painter = painter,
                        contentDescription = video.title,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(if (isImageLoading) 0f else 1f),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFE1BEE7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.White
                        )
                    }
                }

                if (!isLoading && !isImageLoading) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = formatDuration(video.duration),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            if (isLoading) {
                // Shimmer effect for title
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .padding(16.dp, 8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp)
                        )
                )
            } else {
                Text(
                    text = video.title,
                    modifier = Modifier.padding(16.dp, 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    color = if (isImageLoading) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}

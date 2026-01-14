package com.mrkola.kidztv.ui


import android.graphics.Shader
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.mrkola.kidztv.R
import com.mrkola.kidztv.data.Video
import com.mrkola.kidztv.data.VideoRepository

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
    videoRepository: VideoRepository,
    onVideoClick: (Video) -> Unit,
    onParentalControlsClick: () -> Unit
) {
    val videos by remember { mutableStateOf(videoRepository.getAllVideos()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gbGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.tv_logo_horizontal),
                    contentDescription = "KidzTV",
                    modifier = Modifier
                )

                IconButton (
                    onClick = onParentalControlsClick,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (videos.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No videos available",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 180.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(videos) { video ->
                        VideoCard(video = video, onClick = { onVideoClick(video) })
                    }
                }
            }
        }
    }
}

@Composable
fun VideoCard(video: Video, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (video.thumbnailPath != null) {
                    Image(
                        painter = rememberAsyncImagePainter(video.thumbnailPath),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                            .aspectRatio(16f / 9f)
                        ,
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

            Text(
                text = video.title,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
        }
    }
}

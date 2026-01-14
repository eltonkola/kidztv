package com.mrkola.kidztv.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.mrkola.kidztv.data.Video
import org.koin.androidx.compose.koinViewModel
import java.io.File
import kotlin.math.log10
import kotlin.math.pow

data class YouTubeSearchResult(
    val title: String,
    val url: String,
    val thumbnailUrl: String,
    val duration: Long,
    val uploader: String,
    val viewCount: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentalControlsScreen(
    onBack: () -> Unit,
    viewModel: ParentalControlsViewModel = koinViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    var showAbout by remember { mutableStateOf(false) }
    val selectedVideos by viewModel.selectedVideos.collectAsState()
    val downloadingUrls by viewModel.downloadingUrls.collectAsState()
    val videos by viewModel.videos.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Detect orientation
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Handle error messages from ViewModel
    val errorMessage by viewModel.errorMessage.collectAsState()
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gbGradient)
    ) {
        if (isLandscape) {
            // Landscape Mode - Side by Side
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 30.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Side - Downloaded Videos
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onBack,
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            }
                            Text(
                                text = "Downloaded Videos (${videos.size})",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = { showAbout = true },
                                modifier = Modifier.background(
                                    Color(0xFF1A237E).copy(alpha = 0.1f),
                                    RoundedCornerShape(50)
                                )
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "About",
                                    tint = Color(0xFF1A237E)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (videos.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.VideoLibrary,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "No videos downloaded yet",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(videos) { video ->
                                    DownloadedVideoCard(
                                        video = video,
                                        onDelete = {
                                            viewModel.deleteVideo(video)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Right Side - YouTube Search & Results
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Search Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search videos...") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSearching,
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            trailingIcon = {
                                Button(
                                    onClick = {
                                        if(!isSearching) {
                                            viewModel.searchVideos(searchQuery)
                                        }
                                    },
                                    enabled = searchQuery.isNotBlank() && !isSearching,
                                  //  modifier = Modifier.height(56.dp)
                                ) {
                                    if (isSearching) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Color.White
                                        )
                                    } else {
                                        Icon(Icons.Default.Search, contentDescription = null)
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Selected count and download button
                        if (selectedVideos.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${selectedVideos.size} video(s) selected",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF1A237E),
                                    fontWeight = FontWeight.Bold
                                )

                                Button(
                                    onClick = { viewModel.downloadSelected() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50)
                                    )
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Download Selected")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        HorizontalDivider()

                        Spacer(modifier = Modifier.height(8.dp))

                        // Search Results
                        if (searchResults.isEmpty() && !isSearching) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Search for videos to download",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        } else {
                            val downloadProgress by viewModel.downloadProgress.collectAsState()

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(searchResults) { result ->
                                    val progress = downloadProgress[result.url] ?: 0
                                    YouTubeResultCard(
                                        result = result,
                                        isSelected = selectedVideos.contains(result.url),
                                        isDownloading = downloadingUrls.contains(result.url),
                                        downloadProgress = progress,
                                        onToggleSelect = {
                                            viewModel.toggleVideoSelection(result.url)
                                        },
                                        onDownload = { viewModel.downloadVideo(result.url) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Portrait Mode - Stacked Vertically
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 30.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Parental Controls",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(
                        onClick = { showAbout = true },
                        modifier = Modifier.background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "About",
                            tint = Color.White
                        )
                    }
                }

                // YouTube Search & Results Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Search Videos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Search Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            label = { Text("Search videos...") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isSearching,
                            singleLine = true,
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = null)
                            },
                            trailingIcon = {
                                Button(
                                    onClick = {
                                        if(!isSearching) {
                                            viewModel.searchVideos(searchQuery)
                                        }
                                    },
                                    enabled = searchQuery.isNotBlank() && !isSearching,
                                    modifier = Modifier.height(56.dp)
                                ) {
                                    if (isSearching) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Color.White
                                        )
                                    } else {
                                        Icon(Icons.Default.Search, contentDescription = null)
                                    }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Selected count and download button
                        if (selectedVideos.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${selectedVideos.size} video(s) selected",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF1A237E),
                                    fontWeight = FontWeight.Bold
                                )

                                Button(
                                    onClick = { viewModel.downloadSelected() },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50)
                                    )
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Download")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        HorizontalDivider()

                        Spacer(modifier = Modifier.height(8.dp))

                        // Search Results
                        if (searchResults.isEmpty() && !isSearching) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Search for videos to download",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        } else {
                            val downloadProgress by viewModel.downloadProgress.collectAsState()

                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(searchResults) { result ->
                                    val progress = downloadProgress[result.url] ?: 0
                                    YouTubeResultCard(
                                        result = result,
                                        isSelected = selectedVideos.contains(result.url),
                                        isDownloading = downloadingUrls.contains(result.url),
                                        downloadProgress = progress,
                                        onToggleSelect = {
                                            viewModel.toggleVideoSelection(result.url)
                                        },
                                        onDownload = { viewModel.downloadVideo(result.url) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Downloaded Videos Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Downloaded Videos (${videos.size})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        if (videos.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.VideoLibrary,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = Color.Gray
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No videos downloaded yet",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(videos) { video ->
                                    DownloadedVideoCard(
                                        video = video,
                                        onDelete = {
                                            viewModel.deleteVideo(video)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Snackbar for showing error messages
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    if (showAbout) {
        AboutDialog(onDismiss = { showAbout = false })
    }
}

@Composable
fun YouTubeResultCard(
    result: YouTubeSearchResult,
    isSelected: Boolean,
    isDownloading: Boolean,
    downloadProgress: Int = 0,
    onToggleSelect: () -> Unit,
    onDownload: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val thumbnailWidth = if (isLandscape) 160.dp else 120.dp
    val thumbnailHeight = if (isLandscape) 90.dp else 68.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isDownloading) { onToggleSelect() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1A237E).copy(alpha = 0.1f) else Color.White
        ),
        border = if (isSelected) CardDefaults.outlinedCardBorder() else null,
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .width(thumbnailWidth)
                    .height(thumbnailHeight)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray)
            ) {
                if (result.thumbnailUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(result.thumbnailUrl),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Duration overlay
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp),
                    color = Color.Black.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = formatDuration(result.duration * 1000),
                        modifier = Modifier.padding(horizontal = if (isLandscape) 6.dp else 4.dp, vertical = 2.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // Checkbox
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelect() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(if (isLandscape) 4.dp else 2.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                )
            }

            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = result.title,
                    style = if (isLandscape) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = result.uploader,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(if (isLandscape) 8.dp else 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(if (isLandscape) 16.dp else 14.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = formatViewCount(result.viewCount),
                        style = if (isLandscape) MaterialTheme.typography.bodySmall else MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                if (isDownloading) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LinearProgressIndicator(
                            progress = { downloadProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (isLandscape) 4.dp else 3.dp)
                        )
                        Text(
                            text = "$downloadProgress%",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = if (isLandscape) Modifier.align(Alignment.CenterHorizontally) else Modifier,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Download Button
            if (isDownloading) {
                CircularProgressIndicator(modifier = Modifier.size(if (isLandscape) 40.dp else 32.dp))
            } else {
                IconButton(
                    onClick = onDownload,
                    modifier = Modifier.background(
                        Color(0xFF4CAF50).copy(alpha = 0.1f),
                        CircleShape
                    )
                ) {
                    Icon(
                        Icons.Default.Download,
                        contentDescription = "Download",
                        tint = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadedVideoCard(
    video: Video,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val fileSize = remember {
        File(video.filePath).length()
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val thumbnailWidth = if (isLandscape) 160.dp else 120.dp
    val thumbnailHeight = if (isLandscape) 90.dp else 68.dp

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .width(thumbnailWidth)
                    .height(thumbnailHeight)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray)
            ) {
                if (video.thumbnailPath != null) {
                    Image(
                        painter = rememberAsyncImagePainter(video.thumbnailPath),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.VideoLibrary,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(if (isLandscape) 24.dp else 20.dp),
                        tint = Color.White
                    )
                }

                // Duration overlay
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp),
                    color = Color.Black.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = formatDuration(video.duration),
                        modifier = Modifier.padding(horizontal = if (isLandscape) 6.dp else 4.dp, vertical = 2.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            // Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = video.title,
                    style = if (isLandscape) MaterialTheme.typography.titleSmall else MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(if (isLandscape) 12.dp else 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(if (isLandscape) 4.dp else 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Storage,
                            contentDescription = null,
                            modifier = Modifier.size(if (isLandscape) 16.dp else 14.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = formatFileSize(fileSize),
                            style = if (isLandscape) MaterialTheme.typography.bodySmall else MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(if (isLandscape) 4.dp else 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(if (isLandscape) 16.dp else 14.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = formatDuration(video.duration),
                            style = if (isLandscape) MaterialTheme.typography.bodySmall else MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            // Delete Button
            IconButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.background(
                    Color.Red.copy(alpha = 0.1f),
                    CircleShape
                )
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red) },
            title = { Text("Delete Video?") },
            text = {
                Text("Are you sure you want to delete \"${video.title}\"? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

fun formatDuration(millis: Long): String {
    val seconds = (millis / 1000).toInt()
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%d:%02d", minutes, secs)
}

fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB")
    val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
    return String.format(
        "%.1f %s",
        bytes / 1024.0.pow(digitGroups.toDouble()),
        units[digitGroups]
    )
}

fun formatViewCount(count: Long): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM views", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK views", count / 1_000.0)
        else -> "$count views"
    }
}

package com.mrkola.kidztv.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudDownload
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.mrkola.kidztv.R
import com.mrkola.kidztv.data.Video
import org.koin.androidx.compose.koinViewModel
import java.io.File
import kotlin.math.log10
import kotlin.math.pow

data class YouTubeSearchResult(
    val title: String,
    val url: String,
    val videoId: String,
    val thumbnailUrl: String,
    val duration: Long,
    val uploader: String,
    val viewCount: Long,
    val isPlaylist: Boolean = false,
    val itemCount: Long = 0
)

@Composable
fun PlaylistItemsDialog(
    playlistTitle: String,
    items: List<YouTubeSearchResult>,
    isItemDownloaded: (String) -> Boolean,
    onDismiss: () -> Unit,
    onDownloadSelected: (List<String>) -> Unit
) {
    var selectedUrls by remember { mutableStateOf(items.map { it.url }.toSet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(playlistTitle, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        text = {
            Column(modifier = Modifier.heightIn(max = 400.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${selectedUrls.size} selected")
                    TextButton(onClick = {
                        selectedUrls = if (selectedUrls.size == items.size) emptySet() else items.map { it.url }.toSet()
                    }) {
                        Text(if (selectedUrls.size == items.size) "Deselect All" else "Select All")
                    }
                }
                HorizontalDivider()
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { item ->
                        val isDownloaded = isItemDownloaded(item.videoId)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isDownloaded) {
                                    selectedUrls = if (item.url in selectedUrls) selectedUrls - item.url else selectedUrls + item.url
                                }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = item.url in selectedUrls || isDownloaded,
                                onCheckedChange = {
                                    if (!isDownloaded) {
                                        selectedUrls = if (item.url in selectedUrls) selectedUrls - item.url else selectedUrls + item.url
                                    }
                                },
                                enabled = !isDownloaded
                            )
                            Spacer(Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
                                if (isDownloaded) {
                                    Text("Already downloaded", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDownloadSelected(selectedUrls.toList())
                    onDismiss()
                },
                enabled = selectedUrls.isNotEmpty()
            ) {
                Text(stringResource(R.string.download))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

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

    val playlistItems by viewModel.playlistItems.collectAsState()
    val isLoadingPlaylist by viewModel.isLoadingPlaylist.collectAsState()
    val batchDownloadState by viewModel.batchDownloadState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Tab state
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.search_videos_placeholder).replace("…", ""),
        stringResource(R.string.downloaded_videos, videos.size)
    )

    // Handle error messages from ViewModel
    val errorMessage by viewModel.errorMessage.collectAsState()
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(gbGradient)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = Color.White
                        )
                    }
                    Text(
                        text = stringResource(R.string.parental_controls),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showAbout = true }) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = stringResource(R.string.about),
                            tint = Color.White
                        )
                    }
                }

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    contentColor = Color.White,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color.White
                        )
                    },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (batchDownloadState.totalItems > 0) {
                DownloadStatusManager(batchDownloadState)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gbGradient)
                .padding(paddingValues)
        ) {
            when (selectedTabIndex) {
                0 -> SearchTab(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    isSearching = isSearching,
                    searchResults = searchResults,
                    selectedVideos = selectedVideos,
                    downloadingUrls = downloadingUrls,
                    isLoadingPlaylist = isLoadingPlaylist,
                    viewModel = viewModel
                )
                1 -> DownloadsTab(
                    videos = videos,
                    viewModel = viewModel
                )
            }
        }
    }

    // Playlist Dialogs
    playlistItems.forEach { (url, items) ->
        val playlistTitle = searchResults.find { it.url == url }?.title ?: "Playlist"
        PlaylistItemsDialog(
            playlistTitle = playlistTitle,
            items = items,
            isItemDownloaded = { videoId -> viewModel.isVideoDownloaded(videoId) },
            onDismiss = { viewModel.clearPlaylistItems(url) },
            onDownloadSelected = { urls ->
                urls.forEach { viewModel.downloadVideo(it) }
            }
        )
    }

    if (showAbout) {
        AboutDialog(onDismiss = { showAbout = false })
    }
}

@Composable
fun DownloadStatusManager(state: BatchDownloadState) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = Color.White,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CloudDownload,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Downloading ${state.totalItems} videos",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${state.activeItems} active, ${state.pendingItems} pending",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                Text(
                    text = formatFileSize(state.totalSpeed) + "/s",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            LinearProgressIndicator(
                progress = { state.totalProgress / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
            )
            
            Spacer(Modifier.height(4.dp))
            
            Text(
                text = "Total Progress: ${state.totalProgress}%",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun SearchTab(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearching: Boolean,
    searchResults: List<YouTubeSearchResult>,
    selectedVideos: Set<String>,
    downloadingUrls: Set<String>,
    isLoadingPlaylist: String?,
    viewModel: ParentalControlsViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Modern Search Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                Spacer(Modifier.width(12.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text(stringResource(R.string.search_videos_placeholder)) },
                    modifier = Modifier.weight(1f),
                    enabled = !isSearching,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (searchQuery.isNotBlank() && !isSearching) {
                                viewModel.searchVideos(searchQuery)
                                keyboardController?.hide()
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent
                    )
                )
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        if (searchQuery.isNotBlank() && !isSearching) {
                            viewModel.searchVideos(searchQuery)
                            keyboardController?.hide()
                        }
                    }) {
                        if (isSearching) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Search")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedVideos.isNotEmpty()) {
            Button(
                onClick = { viewModel.downloadSelected() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.download_selected) + " (${selectedVideos.size})")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (searchResults.isEmpty() && !isSearching) {
            EmptyState(
                icon = Icons.Default.Search,
                message = stringResource(R.string.search_for_videos)
            )
        } else {
            val downloadProgress by viewModel.downloadProgress.collectAsState()
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(searchResults) { result ->
                    val progress = downloadProgress[result.url] ?: 0
                    YouTubeResultCard(
                        result = result,
                        isSelected = selectedVideos.contains(result.url),
                        isDownloading = downloadingUrls.contains(result.url),
                        isLoadingPlaylist = isLoadingPlaylist == result.url,
                        downloadProgress = progress,
                        onToggleSelect = {
                            if (result.isPlaylist) {
                                viewModel.loadPlaylist(result.url)
                            } else {
                                viewModel.toggleVideoSelection(result.url)
                            }
                        },
                        onDownload = {
                            if (result.isPlaylist) {
                                viewModel.loadPlaylist(result.url)
                            } else {
                                viewModel.downloadVideo(result.url)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DownloadsTab(
    videos: List<Video>,
    viewModel: ParentalControlsViewModel
) {
    if (videos.isEmpty()) {
        EmptyState(
            icon = Icons.Default.VideoLibrary,
            message = stringResource(R.string.no_videos_downloaded)
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(videos) { video ->
                DownloadedVideoCard(
                    video = video,
                    onDelete = { viewModel.deleteVideo(video) }
                )
            }
        }
    }
}

@Composable
fun EmptyState(icon: androidx.compose.ui.graphics.vector.ImageVector, message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.White.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = message, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun YouTubeResultCard(
    result: YouTubeSearchResult,
    isSelected: Boolean,
    isDownloading: Boolean,
    isLoadingPlaylist: Boolean = false,
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
                        text = if (result.isPlaylist) "${result.itemCount} videos" else formatDuration(result.duration * 1000),
                        modifier = Modifier.padding(horizontal = if (isLandscape) 6.dp else 4.dp, vertical = 2.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // Checkbox
                if (!result.isPlaylist) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleSelect() },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(if (isLandscape) 4.dp else 2.dp)
                            .background(Color.White.copy(alpha = 0.8f), CircleShape)
                    )
                } else {
                    Icon(
                        Icons.Default.VideoLibrary,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .size(20.dp)
                            .shadow(4.dp, CircleShape)
                    )
                }
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
                                .clip(CircleShape)
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

            // Download Button - Only show if not downloading and not already downloaded
            if (!isDownloading && downloadProgress == 0) {
                IconButton(
                    onClick = onDownload,
                    modifier = Modifier.background(
                        Color(0xFF4CAF50).copy(alpha = 0.1f),
                        CircleShape
                    )
                ) {
                    if (isLoadingPlaylist) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(
                            if (result.isPlaylist) Icons.Default.VideoLibrary else Icons.Default.Download,
                            contentDescription = "Download",
                            tint = Color(0xFF4CAF50)
                        )
                    }
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
    val fileSize = remember(video.filePath) {
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
                    contentDescription = stringResource(R.string.delete),
                    tint = Color.Red
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red) },
            title = { Text(stringResource(R.string.delete_video_title)) },
            text = {
                Text(stringResource(R.string.delete_video_message, video.title))
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
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

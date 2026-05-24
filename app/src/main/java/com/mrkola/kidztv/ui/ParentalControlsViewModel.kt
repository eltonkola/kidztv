package com.mrkola.kidztv.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.mrkola.kidztv.data.DownloadWorker
import com.mrkola.kidztv.data.Video
import com.mrkola.kidztv.data.VideoRepository
import com.mrkola.kidztv.data.extractYouTubeVideoId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.playlist.PlaylistInfoItem
import org.schabi.newpipe.extractor.search.SearchExtractor
import org.schabi.newpipe.extractor.stream.StreamInfoItem

data class BatchDownloadState(
    val totalItems: Int = 0,
    val pendingItems: Int = 0,
    val activeItems: Int = 0,
    val totalProgress: Int = 0,
    val totalSpeed: Long = 0
)

class ParentalControlsViewModel(
    private val videoRepository: VideoRepository,
    application: Application
) : ViewModel() {
    private val workManager = WorkManager.getInstance(application)

    private val _searchResults = MutableStateFlow<List<YouTubeSearchResult>>(emptyList())
    val searchResults: StateFlow<List<YouTubeSearchResult>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>> = _videos.asStateFlow()

    private val _downloadingUrls = MutableStateFlow<Set<String>>(emptySet())
    val downloadingUrls: StateFlow<Set<String>> = _downloadingUrls.asStateFlow()

    private val _selectedVideos = MutableStateFlow<Set<String>>(emptySet())
    val selectedVideos: StateFlow<Set<String>> = _selectedVideos.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _downloadProgress = MutableStateFlow<Map<String, Int>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, Int>> = _downloadProgress.asStateFlow()

    private val _playlistItems = MutableStateFlow<Map<String, List<YouTubeSearchResult>>>(emptyMap())
    val playlistItems: StateFlow<Map<String, List<YouTubeSearchResult>>> = _playlistItems.asStateFlow()

    private val _isLoadingPlaylist = MutableStateFlow<String?>(null)
    val isLoadingPlaylist: StateFlow<String?> = _isLoadingPlaylist.asStateFlow()

    private val _batchDownloadState = MutableStateFlow(BatchDownloadState())
    val batchDownloadState: StateFlow<BatchDownloadState> = _batchDownloadState.asStateFlow()

    init {
        loadVideos()
        observeWorkManager()
    }

    private fun observeWorkManager() {
        viewModelScope.launch {
            workManager.getWorkInfosByTagFlow("download").collect { workInfos ->
                val progressMap = mutableMapOf<String, Int>()
                val activeUrls = mutableSetOf<String>()
                
                var pendingCount = 0
                var activeCount = 0
                var totalProgressSum = 0
                var totalSpeedSum = 0L

                workInfos.forEach { workInfo ->
                    val url = workInfo.tags.find { it.startsWith("url:") }?.removePrefix("url:")
                    if (url != null) {
                        when (workInfo.state) {
                            WorkInfo.State.ENQUEUED -> {
                                pendingCount++
                                activeUrls.add(url)
                            }
                            WorkInfo.State.RUNNING -> {
                                activeCount++
                                activeUrls.add(url)
                                val progress = workInfo.progress.getInt(DownloadWorker.KEY_PROGRESS, 0)
                                val speed = workInfo.progress.getLong(DownloadWorker.KEY_SPEED, 0L)
                                progressMap[url] = progress
                                totalProgressSum += progress
                                totalSpeedSum += speed
                            }
                            WorkInfo.State.SUCCEEDED -> {
                                loadVideos()
                            }
                            else -> {}
                        }
                    }
                }
                
                _downloadProgress.value = progressMap
                _downloadingUrls.value = activeUrls
                
                _batchDownloadState.value = BatchDownloadState(
                    totalItems = pendingCount + activeCount,
                    pendingItems = pendingCount,
                    activeItems = activeCount,
                    totalProgress = if (activeCount > 0) totalProgressSum / activeCount else 0,
                    totalSpeed = totalSpeedSum
                )
            }
        }
    }

    fun isVideoDownloaded(videoId: String): Boolean {
        return _videos.value.any { video ->
            video.id == videoId
        }
    }

    private var currentSearchExtractor: SearchExtractor? = null

    fun searchVideos(query: String, loadNextPage: Boolean = false) {
        if (query.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            _isSearching.value = true
            try {
                val extractor = if (loadNextPage) {
                    currentSearchExtractor?.let { currentExtractor ->
                        ServiceList.YouTube.getSearchExtractor(
                            ServiceList.YouTube.searchQHFactory.fromQuery(query)
                        ).also {
                            it.fetchPage()
                            currentSearchExtractor = it
                        }
                    }
                } else {
                    ServiceList.YouTube.getSearchExtractor(
                        ServiceList.YouTube.searchQHFactory.fromQuery(query)
                    ).also {
                        it.fetchPage()
                        currentSearchExtractor = it
                    }
                } ?: return@launch

                val searchInfo = extractor.initialPage

                val newResults = searchInfo.items
                    .mapNotNull { item ->
                        when (item) {
                            is StreamInfoItem -> YouTubeSearchResult(
                                title = item.name,
                                url = item.url,
                                videoId = extractYouTubeVideoId(item.url),
                                thumbnailUrl = item.thumbnails.firstOrNull()?.url ?: "",
                                duration = item.duration,
                                uploader = item.uploaderName,
                                viewCount = item.viewCount,
                                isPlaylist = false
                            )
                            is PlaylistInfoItem -> YouTubeSearchResult(
                                title = item.name,
                                url = item.url,
                                videoId = "",
                                thumbnailUrl = item.thumbnails.firstOrNull()?.url ?: "",
                                duration = 0,
                                uploader = item.uploaderName ?: "",
                                viewCount = 0,
                                isPlaylist = true,
                                itemCount = item.streamCount
                            )
                            else -> null
                        }
                    }
                    .filterNot { result ->
                        !result.isPlaylist && (_videos.value.any { it.id == result.videoId } ||
                                _downloadingUrls.value.contains(result.url))
                    }

                _searchResults.value = if (loadNextPage) {
                    _searchResults.value + newResults
                } else {
                    newResults
                }

                if (newResults.isEmpty() && searchInfo.items.isNotEmpty() && !loadNextPage) {
                    searchVideos(query, loadNextPage = true)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun loadPlaylist(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingPlaylist.value = url
            try {
                val extractor = ServiceList.YouTube.getPlaylistExtractor(url)
                extractor.fetchPage()
                val playlistInfo = extractor.initialPage
                val items = playlistInfo.items
                    .filterIsInstance<StreamInfoItem>()
                    .map { item ->
                        YouTubeSearchResult(
                            title = item.name,
                            url = item.url,
                            videoId = extractYouTubeVideoId(item.url),
                            thumbnailUrl = item.thumbnails.firstOrNull()?.url ?: "",
                            duration = item.duration,
                            uploader = item.uploaderName,
                            viewCount = item.viewCount
                        )
                    }
                _playlistItems.update { it + (url to items) }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoadingPlaylist.value = null
            }
        }
    }

    fun clearPlaylistItems(url: String) {
        _playlistItems.update { it - url }
    }


    fun toggleVideoSelection(url: String) {
        _selectedVideos.value = if (url in _selectedVideos.value) {
            _selectedVideos.value - url
        } else {
            _selectedVideos.value + url
        }
    }

    fun downloadVideo(url: String) {
        _errorMessage.value = null
        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(workDataOf(DownloadWorker.KEY_VIDEO_URL to url))
            .addTag("download")
            .addTag("url:$url")
            .build()

        workManager.enqueue(workRequest)
        _selectedVideos.value = _selectedVideos.value - url
    }

    fun downloadSelected() {
        _selectedVideos.value.forEach { url ->
            downloadVideo(url)
        }
        _selectedVideos.value = emptySet()
    }

    fun deleteVideo(video: Video) {
        viewModelScope.launch {
            videoRepository.deleteVideo(video)
            loadVideos()
        }
    }

    private fun loadVideos() {
        viewModelScope.launch {
            _videos.value = videoRepository.getAllVideos()
            _searchResults.value = _searchResults.value.filterNot { result ->
                !result.isPlaylist && _videos.value.any { it.id == result.videoId }
            }
        }
    }
}

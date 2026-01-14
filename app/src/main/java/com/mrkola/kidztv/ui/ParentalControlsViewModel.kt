package com.mrkola.kidztv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrkola.kidztv.data.Video
import com.mrkola.kidztv.data.VideoDownloader
import com.mrkola.kidztv.data.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.search.SearchInfo
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import kotlin.String

class ParentalControlsViewModel(
    private val videoRepository: VideoRepository
) : ViewModel() {
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

    init {
        loadVideos()

    }

    fun searchVideos(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            _isSearching.value = true
            try {
                val searchInfo = SearchInfo.getInfo(
                    ServiceList.YouTube,
                    ServiceList.YouTube.searchQHFactory.fromQuery(query)
                )

                _searchResults.value = searchInfo.relatedItems
                    .filterIsInstance<StreamInfoItem>()
                    .map { item ->
                        YouTubeSearchResult(
                            title = item.name,
                            url = item.url,
                            thumbnailUrl = item.thumbnails.firstOrNull()?.url ?: "",
                            duration = item.duration,
                            uploader = item.uploaderName,
                            viewCount = item.viewCount

                        )
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun toggleVideoSelection(url: String) {
        _selectedVideos.value = if (url in _selectedVideos.value) {
            _selectedVideos.value - url
        } else {
            _selectedVideos.value + url
        }
    }

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _downloadProgress = MutableStateFlow<Map<String, Int>>(emptyMap())
    val downloadProgress: StateFlow<Map<String, Int>> = _downloadProgress.asStateFlow()

    fun downloadVideo(url: String) {
        viewModelScope.launch {
            _downloadingUrls.value = _downloadingUrls.value + url
            _errorMessage.value = null
            try {
                videoRepository.downloadVideo(
                    url = url,
                    onProgress = { progress ->
                        _downloadProgress.value = _downloadProgress.value + (url to progress)
                    }
                ).onSuccess {
                    loadVideos()
                    _downloadProgress.value = _downloadProgress.value - url
                }.onFailure {
                    _errorMessage.value = "Failed to download video: ${it.message ?: "Unknown error"}"
                    _downloadProgress.value = _downloadProgress.value - url
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error during download: ${e.message ?: "Unknown error"}"
                _downloadProgress.value = _downloadProgress.value - url
            } finally {
                _downloadingUrls.value = _downloadingUrls.value - url
                _selectedVideos.value = _selectedVideos.value - url
            }
        }
    }

    fun downloadSelected() {
        _selectedVideos.value.forEach { url ->
            downloadVideo(url)
        }
    }

    fun deleteVideo(video: Video) {
        viewModelScope.launch {
            videoRepository.deleteVideo(video)
            loadVideos()
        }
    }

    private fun loadVideos() {
        _videos.value = videoRepository.getAllVideos()
    }
}
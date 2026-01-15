package com.mrkola.kidztv.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.mrkola.kidztv.data.Video
import com.mrkola.kidztv.data.VideoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlayerViewModel(
    application: Application,
    private val videoRepository: VideoRepository,
    initialVideoId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    val exoPlayer: ExoPlayer =
        ExoPlayer.Builder(application.applicationContext).build()

    private var released = false
    private var updateJob: Job? = null
    private var currentVideo: Video? = null

    private var playerListener: Player.Listener? = null

    init {
        initializePlayer(initialVideoId)
    }

    private fun initializePlayer(videoId: String) {

        viewModelScope.launch {
            val videos = videoRepository.getAllVideos()

            val video = videos.find { it.id == videoId }
            if (video == null) {
                _uiState.value = PlayerUiState.Error("Video not found")
                return@launch
            }

            currentVideo = video
            exoPlayer.clearMediaItems()

            playerListener = object : Player.Listener {

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (released) return

                    _uiState.update { state ->
                        when (playbackState) {
                            Player.STATE_READY -> {
                                PlayerUiState.Ready(
                                    currentVideo = video,
                                    videos = videos,
                                    isPlaying = exoPlayer.isPlaying,
                                    isLocked = false,
                                    currentPosition = exoPlayer.currentPosition,
                                    duration = exoPlayer.duration,
                                    isBuffering = false,
                                    showControls = true
                                )
                            }

                            Player.STATE_BUFFERING -> {
                                if (state is PlayerUiState.Ready) {
                                    state.copy(isBuffering = true)
                                } else state
                            }

                            else -> state
                        }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (released) return
                    _uiState.update {
                        if (it is PlayerUiState.Ready) {
                            it.copy(isPlaying = isPlaying)
                        } else it
                    }
                }
            }

            exoPlayer.apply {
                addListener(playerListener!!)
                setMediaItem(MediaItem.fromUri(video.filePath))
                repeatMode = Player.REPEAT_MODE_ONE
                prepare()
                playWhenReady = true
            }

            startProgressUpdates()

        }


    }

    private fun startProgressUpdates() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (isActive && !released) {
                _uiState.update {
                    if (it is PlayerUiState.Ready) {
                        it.copy(
                            currentPosition = exoPlayer.currentPosition,
                            duration = exoPlayer.duration
                        )
                    } else it
                }
                delay(250)
            }
        }
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }

    fun toggleLock() {
        _uiState.update {
            if (it is PlayerUiState.Ready) it.copy(isLocked = !it.isLocked) else it
        }
    }

    fun toggleControls() {
        _uiState.update {
            if (it is PlayerUiState.Ready) it.copy(showControls = !it.showControls) else it
        }
    }

    fun changeVideo(videoId: String) {

        viewModelScope.launch {
            val videos = (uiState.value as PlayerUiState.Ready).videos

        val video = videos.find { it.id == videoId } ?: return@launch

        currentVideo = video

        exoPlayer.apply {
            stop()
            clearMediaItems()
            setMediaItem(MediaItem.fromUri(video.filePath))
            prepare()
            playWhenReady = true
        }

        _uiState.value = PlayerUiState.Ready(
            currentVideo = video,
            videos = videos,
            isPlaying = true,
            isLocked = false,
            currentPosition = 0L,
            duration = exoPlayer.duration,
            isBuffering = false,
            showControls = true
        )

        }
    }

    private fun cleanup() {
        if (released) return
        released = true

        updateJob?.cancel()
        updateJob = null

        playerListener?.let {
            exoPlayer.removeListener(it)
            playerListener = null
        }

        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        exoPlayer.release()
    }

    override fun onCleared() {
        cleanup()
        super.onCleared()
    }
}

/* ---------------- UI STATE ---------------- */

sealed interface PlayerUiState {
    data object Loading : PlayerUiState

    data class Ready(
        val currentVideo: Video?,
        val videos: List<Video>,
        val isPlaying: Boolean,
        val isLocked: Boolean,
        val currentPosition: Long,
        val duration: Long,
        val isBuffering: Boolean,
        val showControls: Boolean
    ) : PlayerUiState

    data class Error(val message: String) : PlayerUiState
}
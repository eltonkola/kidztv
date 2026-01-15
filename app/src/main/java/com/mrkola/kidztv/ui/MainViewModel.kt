package com.mrkola.kidztv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mrkola.kidztv.data.Video
import com.mrkola.kidztv.data.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class MainViewModel(
    private val videoRepository: VideoRepository
) : ViewModel(), KoinComponent {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadVideos()
    }

    fun loadVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = MainUiState.Loading
            try {
                //delay(5_000)
                val videos = videoRepository.getAllVideos()
                _uiState.value = MainUiState.Success(videos)
            } catch (e: Exception) {
                _uiState.value = MainUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

}

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val videos: List<Video>) : MainUiState
    data class Error(val message: String) : MainUiState
}

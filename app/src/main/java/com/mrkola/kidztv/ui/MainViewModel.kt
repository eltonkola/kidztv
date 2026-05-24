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

    fun reset(){
        _uiState.value = MainUiState.Loading
    }

    fun loadVideos() {
        viewModelScope.launch {
            _uiState.value = MainUiState.Loading
            videoRepository.getVideosFlow().collect { videos ->
                _uiState.value = MainUiState.Success(videos)
            }
        }
    }

}

sealed interface MainUiState {
    data object Loading : MainUiState
    data class Success(val videos: List<Video>) : MainUiState
    data class Error(val message: String) : MainUiState
}

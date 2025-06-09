package com.example.multimodelai.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multimodelai.data.models.VideoSummaryResponse
import com.example.multimodelai.data.repository.GeminiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

data class VideoUiState(
    val isLoading: Boolean = false,
    val response: String = "",
    val summaryResponse: VideoSummaryResponse? = null,
    val error: String? = null,
    val selectedVideoUri: Uri? = null
)

@HiltViewModel
class VideoViewModel @Inject constructor(
    private val repository: GeminiRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoUiState())
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    fun selectVideo(uri: Uri) {
        _uiState.value = _uiState.value.copy(
            selectedVideoUri = uri,
            error = null
        )
    }

    fun analyzeVideo(question: String) {
        val videoUri = _uiState.value.selectedVideoUri ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                response = ""
            )

            try {
                val (videoData, mimeType) = convertUriToByteArray(videoUri)
                
                repository.analyzeVideoStream(videoData, mimeType, question)
                    .catch { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to analyze video: ${exception.message}"
                        )
                    }
                    .collect { chunk ->
                        val currentResponse = _uiState.value.response
                        _uiState.value = _uiState.value.copy(
                            response = currentResponse + chunk,
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to analyze video: ${e.message}"
                )
            }
        }
    }

    fun getVideoSummary() {
        val videoUri = _uiState.value.selectedVideoUri ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                summaryResponse = null
            )

            try {
                val (videoData, mimeType) = convertUriToByteArray(videoUri)
                
                repository.getVideoSummary(videoData, mimeType)
                    .onSuccess { summaryResponse ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            summaryResponse = summaryResponse
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to get video summary: ${error.message}"
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to get video summary: ${e.message}"
                )
            }
        }
    }

    private fun convertUriToByteArray(uri: Uri): Pair<ByteArray, String> {
        val inputStream = context.contentResolver.openInputStream(uri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        inputStream?.copyTo(byteArrayOutputStream)
        inputStream?.close()
        
        val mimeType = context.contentResolver.getType(uri) ?: "video/mp4"
        
        return Pair(byteArrayOutputStream.toByteArray(), mimeType)
    }

    fun clearResponse() {
        _uiState.value = _uiState.value.copy(
            response = "",
            summaryResponse = null,
            error = null
        )
    }
} 
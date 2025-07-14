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
    val chaptersResponse: VideoSummaryResponse? = null,
    val highlightsResponse: VideoSummaryResponse? = null,
    val error: String? = null,
    val selectedVideoUri: Uri? = null,
    val currentAnalysisType: String = ""
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
            error = null,
            response = "",
            summaryResponse = null,
            chaptersResponse = null,
            highlightsResponse = null
        )
    }

    fun analyzeVideo(question: String) {
        val videoUri = _uiState.value.selectedVideoUri ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                response = "",
                summaryResponse = null,
                chaptersResponse = null,
                highlightsResponse = null,
                currentAnalysisType = "Q&A Analysis"
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
                response = "",
                summaryResponse = null,
                chaptersResponse = null,
                highlightsResponse = null,
                currentAnalysisType = "Summary Generation"
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

    fun getVideoChapters() {
        val videoUri = _uiState.value.selectedVideoUri ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                response = "",
                summaryResponse = null,
                chaptersResponse = null,
                highlightsResponse = null,
                currentAnalysisType = "Chapter Generation"
            )

            try {
                val (videoData, mimeType) = convertUriToByteArray(videoUri)
                
                repository.getVideoChapters(videoData, mimeType)
                    .onSuccess { chaptersResponse ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            chaptersResponse = chaptersResponse
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to get video chapters: ${error.message}"
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to get video chapters: ${e.message}"
                )
            }
        }
    }

    fun getVideoHighlights() {
        val videoUri = _uiState.value.selectedVideoUri ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                response = "",
                summaryResponse = null,
                chaptersResponse = null,
                highlightsResponse = null,
                currentAnalysisType = "Highlights Generation"
            )

            try {
                val (videoData, mimeType) = convertUriToByteArray(videoUri)
                
                repository.getVideoHighlights(videoData, mimeType)
                    .onSuccess { highlightsResponse ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            highlightsResponse = highlightsResponse
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to get video highlights: ${error.message}"
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to get video highlights: ${e.message}"
                )
            }
        }
    }

    fun performAdvancedAnalysis(analysisType: String) {
        val videoUri = _uiState.value.selectedVideoUri ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                response = "",
                summaryResponse = null,
                chaptersResponse = null,
                highlightsResponse = null,
                currentAnalysisType = analysisType
            )

            try {
                val (videoData, mimeType) = convertUriToByteArray(videoUri)
                
                // Create specific prompts based on analysis type
                val prompt = when (analysisType) {
                    "🎭 Emotion Detection" -> "Analyze the emotions and facial expressions of people in this video. Describe the predominant emotions, mood changes, and emotional context throughout the video."
                    "👥 Person Tracking" -> "Identify and describe all the people visible in this video. Note their appearances, actions, interactions, and any changes in their presence throughout the video."
                    "🏞️ Scene Analysis" -> "Analyze the scenes, settings, and environments in this video. Describe the locations, lighting, composition, and how the visual elements contribute to the overall narrative."
                    "🎵 Audio Analysis" -> "Focus on the audio elements of this video. Describe the music, sound effects, dialogue quality, background noise, and how audio enhances the content."
                    "📊 Content Metrics" -> "Provide detailed metrics about this video content including pacing, visual complexity, engagement factors, and content structure analysis."
                    "🔤 Text Extraction" -> "Extract and transcribe any visible text, captions, titles, or written content appearing in this video. Include timestamps where possible."
                    else -> "Provide a comprehensive analysis of this video focusing on $analysisType."
                }
                
                repository.analyzeVideoStream(videoData, mimeType, prompt)
                    .catch { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to perform $analysisType: ${exception.message}"
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
                    error = "Failed to perform $analysisType: ${e.message}"
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
            chaptersResponse = null,
            highlightsResponse = null,
            error = null
        )
    }
} 
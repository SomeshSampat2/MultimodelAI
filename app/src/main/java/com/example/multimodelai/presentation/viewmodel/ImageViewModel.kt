package com.example.multimodelai.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multimodelai.data.models.ImageLabelsResponse
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

data class ImageUiState(
    val isLoading: Boolean = false,
    val response: String = "",
    val labelsResponse: ImageLabelsResponse? = null,
    val error: String? = null,
    val selectedImageUri: Uri? = null
)

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val repository: GeminiRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImageUiState())
    val uiState: StateFlow<ImageUiState> = _uiState.asStateFlow()

    fun selectImage(uri: Uri) {
        _uiState.value = _uiState.value.copy(
            selectedImageUri = uri,
            error = null
        )
    }

    fun analyzeImage(question: String) {
        val imageUri = _uiState.value.selectedImageUri ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                response = ""
            )

            try {
                val (imageData, mimeType) = convertUriToByteArray(imageUri)
                
                repository.analyzeImageStream(imageData, mimeType, question)
                    .catch { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to analyze image: ${exception.message}"
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
                    error = "Failed to analyze image: ${e.message}"
                )
            }
        }
    }

    fun getImageLabels() {
        val imageUri = _uiState.value.selectedImageUri ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                labelsResponse = null
            )

            try {
                val (imageData, mimeType) = convertUriToByteArray(imageUri)
                
                repository.getImageLabels(imageData, mimeType)
                    .onSuccess { labelsResponse ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            labelsResponse = labelsResponse
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to get image labels: ${error.message}"
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to get image labels: ${e.message}"
                )
            }
        }
    }

    private fun convertUriToByteArray(uri: Uri): Pair<ByteArray, String> {
        val inputStream = context.contentResolver.openInputStream(uri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        inputStream?.copyTo(byteArrayOutputStream)
        inputStream?.close()
        
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        
        return Pair(byteArrayOutputStream.toByteArray(), mimeType)
    }

    fun clearResponse() {
        _uiState.value = _uiState.value.copy(
            response = "",
            labelsResponse = null,
            error = null
        )
    }
} 
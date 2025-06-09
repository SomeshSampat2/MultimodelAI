package com.example.multimodelai.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multimodelai.data.models.DocumentSummaryResponse
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

data class DocumentUiState(
    val isLoading: Boolean = false,
    val response: String = "",
    val summaryResponse: DocumentSummaryResponse? = null,
    val error: String? = null,
    val selectedDocumentUri: Uri? = null
)

@HiltViewModel
class DocumentViewModel @Inject constructor(
    private val repository: GeminiRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocumentUiState())
    val uiState: StateFlow<DocumentUiState> = _uiState.asStateFlow()

    fun selectDocument(uri: Uri) {
        _uiState.value = _uiState.value.copy(
            selectedDocumentUri = uri,
            error = null
        )
    }

    fun analyzeDocument(question: String) {
        val documentUri = _uiState.value.selectedDocumentUri ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                response = ""
            )

            try {
                val (documentData, mimeType) = convertUriToByteArray(documentUri)
                
                repository.analyzeDocumentStream(documentData, mimeType, question)
                    .catch { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to analyze document: ${exception.message}"
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
                    error = "Failed to analyze document: ${e.message}"
                )
            }
        }
    }

    fun getDocumentSummary() {
        val documentUri = _uiState.value.selectedDocumentUri ?: return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                summaryResponse = null
            )

            try {
                val (documentData, mimeType) = convertUriToByteArray(documentUri)
                
                repository.getDocumentSummary(documentData, mimeType)
                    .onSuccess { summaryResponse ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            summaryResponse = summaryResponse
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to get document summary: ${error.message}"
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to get document summary: ${e.message}"
                )
            }
        }
    }

    private fun convertUriToByteArray(uri: Uri): Pair<ByteArray, String> {
        val inputStream = context.contentResolver.openInputStream(uri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        inputStream?.copyTo(byteArrayOutputStream)
        inputStream?.close()
        
        val mimeType = context.contentResolver.getType(uri) ?: "application/pdf"
        
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
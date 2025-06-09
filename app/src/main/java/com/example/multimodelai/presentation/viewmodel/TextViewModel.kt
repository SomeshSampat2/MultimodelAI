package com.example.multimodelai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multimodelai.data.repository.GeminiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TextUiState(
    val isLoading: Boolean = false,
    val response: String = "",
    val error: String? = null
)

@HiltViewModel
class TextViewModel @Inject constructor(
    private val repository: GeminiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TextUiState())
    val uiState: StateFlow<TextUiState> = _uiState.asStateFlow()

    fun askQuestion(question: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                response = ""
            )

            try {
                repository.generateContentStream(question)
                    .catch { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Failed to generate response: ${exception.message}"
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
                    error = "Failed to generate response: ${e.message}"
                )
            }
        }
    }

    fun clearResponse() {
        _uiState.value = TextUiState()
    }
} 
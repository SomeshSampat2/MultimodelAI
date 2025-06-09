package com.example.multimodelai.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Base64
import com.example.multimodelai.BuildConfig
import com.example.multimodelai.data.api.GeminiApiService
import com.example.multimodelai.data.models.*
import com.example.multimodelai.utils.Constants
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.io.StringReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRepository @Inject constructor(
    private val apiService: GeminiApiService,
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val apiKey = Constants.GEMINI_API_KEY
    
    // Streaming text generation
    suspend fun generateContentStream(prompt: String): Flow<String> = flow {
        try {
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        role = "user",
                        parts = listOf(Part(text = prompt))
                    )
                ),
                generationConfig = GenerationConfig()
            )
            
            val response = apiService.streamGenerateContent(
                apiKey = Constants.GEMINI_API_KEY,
                request = request
            )
            
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    val reader = BufferedReader(responseBody.charStream())
                    var line: String?
                    
                    while (reader.readLine().also { line = it } != null) {
                        line?.let { currentLine ->
                            if (currentLine.startsWith("data: ") && !currentLine.contains("[DONE]")) {
                                try {
                                    val jsonData = currentLine.substring(6)
                                    val streamResponse = gson.fromJson(jsonData, GeminiResponse::class.java)
                                    streamResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.let { text ->
                                        emit(text)
                                    }
                                } catch (e: Exception) {
                                    // Skip malformed JSON chunks
                                }
                            }
                        }
                    }
                }
            } else {
                throw Exception("API Error: ${response.code()}")
            }
        } catch (e: Exception) {
            throw Exception("Failed to generate content: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)
    
    // Regular text generation (non-streaming)
    suspend fun generateContent(prompt: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val request = GeminiRequest(
                    contents = listOf(
                        Content(
                            role = "user",
                            parts = listOf(Part(text = prompt))
                        )
                    ),
                    generationConfig = GenerationConfig()
                )
                
                val response = apiService.generateContent(
                    apiKey = Constants.GEMINI_API_KEY,
                    request = request
                )
                
                if (response.isSuccessful) {
                    val responseText = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (responseText != null) {
                        Result.success(responseText)
                    } else {
                        Result.failure(Exception("No response text received"))
                    }
                } else {
                    Result.failure(Exception("API Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to generate content: ${e.message}"))
            }
        }
    }
    
    // Image analysis with streaming
    suspend fun analyzeImageStream(
        imageData: ByteArray,
        mimeType: String,
        question: String
    ): Flow<String> = flow {
        try {
            val base64Image = Base64.encodeToString(imageData, Base64.NO_WRAP)
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        role = "user",
                        parts = listOf(
                            Part(text = question),
                            Part(inlineData = InlineData(mimeType = mimeType, data = base64Image))
                        )
                    )
                ),
                generationConfig = GenerationConfig()
            )
            
            val response = apiService.streamGenerateContent(
                apiKey = Constants.GEMINI_API_KEY,
                request = request
            )
            
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    val reader = BufferedReader(responseBody.charStream())
                    var line: String?
                    
                    while (reader.readLine().also { line = it } != null) {
                        line?.let { currentLine ->
                            if (currentLine.startsWith("data: ") && !currentLine.contains("[DONE]")) {
                                try {
                                    val jsonData = currentLine.substring(6)
                                    val streamResponse = gson.fromJson(jsonData, GeminiResponse::class.java)
                                    streamResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.let { text ->
                                        emit(text)
                                    }
                                } catch (e: Exception) {
                                    // Skip malformed JSON chunks
                                }
                            }
                        }
                    }
                }
            } else {
                throw Exception("API Error: ${response.code()}")
            }
        } catch (e: Exception) {
            throw Exception("Failed to analyze image: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)
    
    // Get structured image labels
    suspend fun getImageLabels(
        imageData: ByteArray,
        mimeType: String
    ): Result<ImageLabelsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val base64Image = Base64.encodeToString(imageData, Base64.NO_WRAP)
                val request = StructuredRequest(
                    contents = listOf(
                        Content(
                            role = "user",
                            parts = listOf(
                                Part(text = "Analyze this image and provide detailed labels for objects, concepts, and elements you can identify. Include confidence scores and descriptions."),
                                Part(inlineData = InlineData(mimeType = mimeType, data = base64Image))
                            )
                        )
                    ),
                    generationConfig = StructuredGenerationConfig(
                        responseSchema = SchemaDefinitions.imageLabelsSchema
                    )
                )
                
                val response = apiService.generateStructuredContent(
                    apiKey = Constants.GEMINI_API_KEY,
                    request = request
                )
                
                if (response.isSuccessful) {
                    val responseText = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (responseText != null) {
                        val labelsResponse = gson.fromJson(responseText, ImageLabelsResponse::class.java)
                        Result.success(labelsResponse)
                    } else {
                        Result.failure(Exception("No response received"))
                    }
                } else {
                    Result.failure(Exception("API Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to get image labels: ${e.message}"))
            }
        }
    }
    
    // Analyze video with streaming
    suspend fun analyzeVideoStream(
        videoData: ByteArray,
        mimeType: String,
        question: String
    ): Flow<String> = flow {
        try {
            val base64Video = Base64.encodeToString(videoData, Base64.NO_WRAP)
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        role = "user",
                        parts = listOf(
                            Part(text = question),
                            Part(inlineData = InlineData(mimeType = mimeType, data = base64Video))
                        )
                    )
                ),
                generationConfig = GenerationConfig()
            )
            
            val response = apiService.streamGenerateContent(
                apiKey = Constants.GEMINI_API_KEY,
                request = request
            )
            
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    val reader = BufferedReader(responseBody.charStream())
                    var line: String?
                    
                    while (reader.readLine().also { line = it } != null) {
                        line?.let { currentLine ->
                            if (currentLine.startsWith("data: ") && !currentLine.contains("[DONE]")) {
                                try {
                                    val jsonData = currentLine.substring(6)
                                    val streamResponse = gson.fromJson(jsonData, GeminiResponse::class.java)
                                    streamResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.let { text ->
                                        emit(text)
                                    }
                                } catch (e: Exception) {
                                    // Skip malformed JSON chunks
                                }
                            }
                        }
                    }
                }
            } else {
                throw Exception("API Error: ${response.code()}")
            }
        } catch (e: Exception) {
            throw Exception("Failed to analyze video: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)
    
    // Get structured video summary
    suspend fun getVideoSummary(
        videoData: ByteArray,
        mimeType: String
    ): Result<VideoSummaryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val base64Video = Base64.encodeToString(videoData, Base64.NO_WRAP)
                val request = StructuredRequest(
                    contents = listOf(
                        Content(
                            role = "user",
                            parts = listOf(
                                Part(text = "Analyze this video and provide a comprehensive summary with key moments and timestamps. Focus on important events, scenes, and content."),
                                Part(inlineData = InlineData(mimeType = mimeType, data = base64Video))
                            )
                        )
                    ),
                    generationConfig = StructuredGenerationConfig(
                        responseSchema = SchemaDefinitions.videoSummarySchema
                    )
                )
                
                val response = apiService.generateStructuredContent(
                    apiKey = Constants.GEMINI_API_KEY,
                    request = request
                )
                
                if (response.isSuccessful) {
                    val responseText = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (responseText != null) {
                        val summaryResponse = gson.fromJson(responseText, VideoSummaryResponse::class.java)
                        Result.success(summaryResponse)
                    } else {
                        Result.failure(Exception("No response received"))
                    }
                } else {
                    Result.failure(Exception("API Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to get video summary: ${e.message}"))
            }
        }
    }
    
    // Analyze audio with streaming
    suspend fun analyzeAudioStream(
        audioData: ByteArray,
        mimeType: String,
        question: String
    ): Flow<String> = flow {
        try {
            val base64Audio = Base64.encodeToString(audioData, Base64.NO_WRAP)
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        role = "user",
                        parts = listOf(
                            Part(text = question),
                            Part(inlineData = InlineData(mimeType = mimeType, data = base64Audio))
                        )
                    )
                ),
                generationConfig = GenerationConfig()
            )
            
            val response = apiService.streamGenerateContent(
                apiKey = Constants.GEMINI_API_KEY,
                request = request
            )
            
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    val reader = BufferedReader(responseBody.charStream())
                    var line: String?
                    
                    while (reader.readLine().also { line = it } != null) {
                        line?.let { currentLine ->
                            if (currentLine.startsWith("data: ") && !currentLine.contains("[DONE]")) {
                                try {
                                    val jsonData = currentLine.substring(6)
                                    val streamResponse = gson.fromJson(jsonData, GeminiResponse::class.java)
                                    streamResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.let { text ->
                                        emit(text)
                                    }
                                } catch (e: Exception) {
                                    // Skip malformed JSON chunks
                                }
                            }
                        }
                    }
                }
            } else {
                throw Exception("API Error: ${response.code()}")
            }
        } catch (e: Exception) {
            throw Exception("Failed to analyze audio: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)
    
    // Get structured audio summary (similar to video)
    suspend fun getAudioSummary(
        audioData: ByteArray,
        mimeType: String
    ): Result<VideoSummaryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val base64Audio = Base64.encodeToString(audioData, Base64.NO_WRAP)
                val request = StructuredRequest(
                    contents = listOf(
                        Content(
                            role = "user",
                            parts = listOf(
                                Part(text = "Analyze this audio and provide a comprehensive summary with key moments and timestamps. Focus on important content, topics discussed, and significant events."),
                                Part(inlineData = InlineData(mimeType = mimeType, data = base64Audio))
                            )
                        )
                    ),
                    generationConfig = StructuredGenerationConfig(
                        responseSchema = SchemaDefinitions.videoSummarySchema // Using same schema as video
                    )
                )
                
                val response = apiService.generateStructuredContent(
                    apiKey = Constants.GEMINI_API_KEY,
                    request = request
                )
                
                if (response.isSuccessful) {
                    val responseText = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (responseText != null) {
                        val summaryResponse = gson.fromJson(responseText, VideoSummaryResponse::class.java)
                        Result.success(summaryResponse)
                    } else {
                        Result.failure(Exception("No response received"))
                    }
                } else {
                    Result.failure(Exception("API Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to get audio summary: ${e.message}"))
            }
        }
    }
    
    // Analyze document with streaming
    suspend fun analyzeDocumentStream(
        documentData: ByteArray,
        mimeType: String,
        question: String
    ): Flow<String> = flow {
        try {
            val base64Document = Base64.encodeToString(documentData, Base64.NO_WRAP)
            val request = GeminiRequest(
                contents = listOf(
                    Content(
                        role = "user",
                        parts = listOf(
                            Part(text = question),
                            Part(inlineData = InlineData(mimeType = mimeType, data = base64Document))
                        )
                    )
                ),
                generationConfig = GenerationConfig()
            )
            
            val response = apiService.streamGenerateContent(
                apiKey = Constants.GEMINI_API_KEY,
                request = request
            )
            
            if (response.isSuccessful) {
                response.body()?.let { responseBody ->
                    val reader = BufferedReader(responseBody.charStream())
                    var line: String?
                    
                    while (reader.readLine().also { line = it } != null) {
                        line?.let { currentLine ->
                            if (currentLine.startsWith("data: ") && !currentLine.contains("[DONE]")) {
                                try {
                                    val jsonData = currentLine.substring(6)
                                    val streamResponse = gson.fromJson(jsonData, GeminiResponse::class.java)
                                    streamResponse.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text?.let { text ->
                                        emit(text)
                                    }
                                } catch (e: Exception) {
                                    // Skip malformed JSON chunks
                                }
                            }
                        }
                    }
                }
            } else {
                throw Exception("API Error: ${response.code()}")
            }
        } catch (e: Exception) {
            throw Exception("Failed to analyze document: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)
    
    // Get structured document summary
    suspend fun getDocumentSummary(
        documentData: ByteArray,
        mimeType: String
    ): Result<DocumentSummaryResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val base64Document = Base64.encodeToString(documentData, Base64.NO_WRAP)
                val request = StructuredRequest(
                    contents = listOf(
                        Content(
                            role = "user",
                            parts = listOf(
                                Part(text = "Analyze this document and provide a comprehensive summary with key points, document type, and main topics. Extract the most important information and themes."),
                                Part(inlineData = InlineData(mimeType = mimeType, data = base64Document))
                            )
                        )
                    ),
                    generationConfig = StructuredGenerationConfig(
                        responseSchema = SchemaDefinitions.documentSummarySchema
                    )
                )
                
                val response = apiService.generateStructuredContent(
                    apiKey = Constants.GEMINI_API_KEY,
                    request = request
                )
                
                if (response.isSuccessful) {
                    val responseText = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (responseText != null) {
                        val summaryResponse = gson.fromJson(responseText, DocumentSummaryResponse::class.java)
                        Result.success(summaryResponse)
                    } else {
                        Result.failure(Exception("No response received"))
                    }
                } else {
                    Result.failure(Exception("API Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Failed to get document summary: ${e.message}"))
            }
        }
    }
    
    private suspend fun convertImageToBase64(uri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
    
    private suspend fun convertVideoToBase64(uri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
    
    private suspend fun convertAudioToBase64(uri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
    
    private suspend fun convertDocumentToBase64(uri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
    
    private fun getMimeType(uri: Uri): String? {
        return context.contentResolver.getType(uri)
    }
} 
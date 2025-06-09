package com.example.multimodelai.data.api

import com.example.multimodelai.data.models.GeminiRequest
import com.example.multimodelai.data.models.GeminiResponse
import com.example.multimodelai.data.models.StructuredRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming

interface GeminiApiService {
    
    // Regular API for non-streaming
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
    
    // Streaming API
    @Streaming
    @Headers("Accept: text/event-stream")
    @POST("v1beta/models/gemini-2.0-flash:streamGenerateContent")
    suspend fun streamGenerateContent(
        @Query("key") apiKey: String,
        @Query("alt") alt: String = "sse",
        @Body request: GeminiRequest
    ): Response<ResponseBody>
    
    // Structured output API (for labels, summaries)
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    suspend fun generateStructuredContent(
        @Query("key") apiKey: String,
        @Body request: StructuredRequest
    ): Response<GeminiResponse>
} 
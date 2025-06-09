package com.example.multimodelai.utils

import com.example.multimodelai.BuildConfig

object Constants {
    // Base URL for Gemini API
    const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"
    
    // API Key from BuildConfig (reads from local.properties)
    val GEMINI_API_KEY: String
        get() = BuildConfig.GEMINI_API_KEY
    
    // File type constants
    object FileTypes {
        // Image types
        val SUPPORTED_IMAGE_TYPES = setOf(
            "image/jpeg",
            "image/jpg", 
            "image/png",
            "image/gif",
            "image/webp"
        )
        
        // Video types  
        val SUPPORTED_VIDEO_TYPES = setOf(
            "video/mp4",
            "video/mpeg",
            "video/quicktime",
            "video/avi",
            "video/x-msvideo"
        )
        
        // Audio types
        val SUPPORTED_AUDIO_TYPES = setOf(
            "audio/mpeg",
            "audio/mp3",
            "audio/wav",
            "audio/aac",
            "audio/ogg"
        )
        
        // Document types
        val SUPPORTED_DOCUMENT_TYPES = setOf(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
            "application/msword", // .doc
            "text/plain" // .txt
        )
    }
    
    // File size limits (in bytes)
    object FileLimits {
        const val MAX_IMAGE_SIZE = 20 * 1024 * 1024 // 20MB
        const val MAX_VIDEO_SIZE = 100 * 1024 * 1024 // 100MB  
        const val MAX_AUDIO_SIZE = 50 * 1024 * 1024 // 50MB
        const val MAX_DOCUMENT_SIZE = 30 * 1024 * 1024 // 30MB
    }
    
    // API Configuration
    object ApiConfig {
        const val REQUEST_TIMEOUT_SECONDS = 120L
        const val CONNECT_TIMEOUT_SECONDS = 60L
        const val WRITE_TIMEOUT_SECONDS = 120L
        
        const val DEFAULT_TEMPERATURE = 0.7f
        const val DEFAULT_TOP_K = 40
        const val DEFAULT_TOP_P = 0.95f
        const val DEFAULT_MAX_OUTPUT_TOKENS = 2048
        
        // Structured output config
        const val STRUCTURED_TEMPERATURE = 0.1f
        const val STRUCTURED_RESPONSE_MIME_TYPE = "application/json"
    }
} 
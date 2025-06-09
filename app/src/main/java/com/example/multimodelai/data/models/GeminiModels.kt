package com.example.multimodelai.data.models

import com.google.gson.annotations.SerializedName

// Basic request/response models
data class GeminiRequest(
    val contents: List<Content>,
    @SerializedName("systemInstruction")
    val systemInstruction: Content? = null,
    @SerializedName("generationConfig")
    val generationConfig: GenerationConfig? = null
)

// Structured request for JSON schema responses
data class StructuredRequest(
    val contents: List<Content>,
    @SerializedName("systemInstruction")
    val systemInstruction: Content? = null,
    @SerializedName("generationConfig")
    val generationConfig: StructuredGenerationConfig
)

data class Content(
    val role: String = "user",
    val parts: List<Part>
)

data class Part(
    val text: String? = null,
    @SerializedName("inline_data")
    val inlineData: InlineData? = null
)

data class InlineData(
    @SerializedName("mime_type")
    val mimeType: String,
    val data: String
)

data class GenerationConfig(
    val temperature: Float = 0.7f,
    val topK: Int = 40,
    val topP: Float = 0.95f,
    val maxOutputTokens: Int = 2048,
    val stopSequences: List<String>? = null
)

data class StructuredGenerationConfig(
    val temperature: Float = 0.1f,
    val topK: Int = 40,
    val topP: Float = 0.95f,
    val maxOutputTokens: Int = 2048,
    @SerializedName("response_mime_type")
    val responseMimeType: String = "application/json",
    @SerializedName("response_schema")
    val responseSchema: JsonSchema
)

data class JsonSchema(
    val type: String = "object",
    val properties: Map<String, SchemaProperty>,
    val required: List<String>? = null
)

data class SchemaProperty(
    val type: String,
    val description: String? = null,
    val items: SchemaProperty? = null,
    val properties: Map<String, SchemaProperty>? = null,
    val required: List<String>? = null
)

// Response models
data class GeminiResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val content: Content,
    val finishReason: String? = null,
    val safetyRatings: List<SafetyRating>? = null
)

data class SafetyRating(
    val category: String,
    val probability: String
)

// Structured response models for specific features
data class ImageLabelsResponse(
    val labels: List<ImageLabel>
)

data class ImageLabel(
    val name: String,
    val confidence: Float,
    val description: String
)

data class VideoSummaryResponse(
    val summary: String,
    @SerializedName("key_moments")
    val keyMoments: List<VideoMoment>
)

data class VideoMoment(
    val timestamp: String,
    val description: String,
    val importance: Float
)

data class DocumentSummaryResponse(
    val summary: String,
    @SerializedName("key_points")
    val keyPoints: List<String>,
    @SerializedName("document_type")
    val documentType: String,
    @SerializedName("main_topics")
    val mainTopics: List<String>
)

// JSON Schema definitions for structured outputs
object SchemaDefinitions {
    
    val imageLabelsSchema = JsonSchema(
        type = "object",
        properties = mapOf(
            "labels" to SchemaProperty(
                type = "array",
                description = "List of detected labels in the image",
                items = SchemaProperty(
                    type = "object",
                    properties = mapOf(
                        "name" to SchemaProperty(
                            type = "string",
                            description = "Name of the detected object or concept"
                        ),
                        "confidence" to SchemaProperty(
                            type = "number",
                            description = "Confidence score between 0 and 1"
                        ),
                        "description" to SchemaProperty(
                            type = "string",
                            description = "Detailed description of what was detected"
                        )
                    ),
                    required = listOf("name", "confidence", "description")
                )
            )
        ),
        required = listOf("labels")
    )
    
    val videoSummarySchema = JsonSchema(
        type = "object",
        properties = mapOf(
            "summary" to SchemaProperty(
                type = "string",
                description = "Overall summary of the video content"
            ),
            "key_moments" to SchemaProperty(
                type = "array",
                description = "Important moments in the video with timestamps",
                items = SchemaProperty(
                    type = "object",
                    properties = mapOf(
                        "timestamp" to SchemaProperty(
                            type = "string",
                            description = "Timestamp in MM:SS format"
                        ),
                        "description" to SchemaProperty(
                            type = "string",
                            description = "Description of what happens at this moment"
                        ),
                        "importance" to SchemaProperty(
                            type = "number",
                            description = "Importance score between 0 and 1"
                        )
                    ),
                    required = listOf("timestamp", "description", "importance")
                )
            )
        ),
        required = listOf("summary", "key_moments")
    )
    
    val documentSummarySchema = JsonSchema(
        type = "object",
        properties = mapOf(
            "summary" to SchemaProperty(
                type = "string",
                description = "Comprehensive summary of the document"
            ),
            "key_points" to SchemaProperty(
                type = "array",
                description = "Main key points extracted from the document",
                items = SchemaProperty(
                    type = "string",
                    description = "A key point from the document"
                )
            ),
            "document_type" to SchemaProperty(
                type = "string",
                description = "Type of document (e.g., research paper, report, article)"
            ),
            "main_topics" to SchemaProperty(
                type = "array",
                description = "Main topics covered in the document",
                items = SchemaProperty(
                    type = "string",
                    description = "A main topic"
                )
            )
        ),
        required = listOf("summary", "key_points", "document_type", "main_topics")
    )
} 
# 🤖 **MultiModel AI Android App**

A modern Android application that leverages **Google's Gemini 2.0 Flash** model to provide AI-powered features across multiple media types.

## ✨ **Features**

- 📝 **Text Generation** - Interactive AI chat with streaming responses
- 🖼️ **Image Analysis** - AI-powered image labeling and description
- 🎥 **Video Summarization** - Extract key insights from video content
- 🎵 **Audio Analysis** - Transcription and summarization of audio files
- 📄 **Document Processing** - Extract and analyze text from documents

## 🔧 **Setup Instructions**

### 1. **Get Gemini API Key**
1. Visit [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Create a new API key for Gemini
3. Copy the generated API key

### 2. **Configure API Key**
1. Open the `local.properties` file in the project root
2. Replace `YOUR_GEMINI_API_KEY_HERE` with your actual API key:
   ```properties
   GEMINI_API_KEY=your_actual_api_key_here
   ```

### 3. **Build and Run**
```bash
./gradlew assembleDebug
```

## 🏗️ **Architecture**

- **MVVM Architecture** with Jetpack Compose
- **Hilt** for Dependency Injection
- **Retrofit** for API communication
- **Flow** for reactive programming and streaming responses
- **Material Design 3** for modern UI

## 🚀 **Key Technologies**

- **Kotlin** - Primary language
- **Jetpack Compose** - Modern UI toolkit
- **Gemini 2.0 Flash** - Google's latest AI model
- **Server-Sent Events** - Real-time streaming responses
- **Structured Output** - JSON schema-based responses

## 📱 **Screens**

1. **Text Screen** - Chat interface with streaming AI responses
2. **Image Screen** - Upload and analyze images
3. **Video Screen** - Upload videos for AI summarization
4. **Audio Screen** - Process audio files for transcription
5. **Document Screen** - Extract text and insights from documents

## 🔒 **Security**

- API keys are stored in `local.properties` (gitignored)
- No hardcoded secrets in source code
- Secure API communication with HTTPS

## 🎨 **UI Features**

- **Modern Material Design 3** interface
- **Markdown formatting** for AI responses
- **Real-time streaming** responses
- **Dark/Light theme** support
- **Responsive layouts** for different screen sizes

## 🛠️ **Development**

### Requirements
- Android Studio Hedgehog or newer
- Kotlin 1.9+
- Android API 24+ (Android 7.0)

### Building
```bash
# Debug build
./gradlew assembleDebug

# Release build  
./gradlew assembleRelease
```

## 📝 **License**

This project is for educational and demonstration purposes.

---

**💡 Tip:** Make sure to add your Gemini API key to `local.properties` before building the app! 
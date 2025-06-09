# 🚀 Quick Setup Guide

## Prerequisites
- Android Studio (latest version)
- Android SDK API level 24+
- A Google account for Gemini API access

## Step-by-Step Setup

### 1. Get Your Gemini API Key
1. Go to [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Sign in with your Google account
3. Click "Create API Key"
4. Copy the generated API key

### 2. Configure the App
1. Open `app/src/main/java/com/example/multimodelai/utils/Constants.kt`
2. Replace this line:
   ```kotlin
   const val GEMINI_API_KEY = "YOUR_GEMINI_API_KEY_HERE"
   ```
   with:
   ```kotlin
   const val GEMINI_API_KEY = "your_actual_api_key_here"
   ```

### 3. Build and Run
1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Click the green Run button or press Ctrl+R (Cmd+R on Mac)
4. Select your device/emulator
5. Grant permissions when prompted

## 🎯 What You'll Get

✅ **5 Powerful AI Tabs:**
- 📝 Text: Ask any questions to Gemini AI
- 🖼️ Image: Upload and analyze images with Q&A or auto-labeling
- 🎵 Audio: Upload audio files for analysis and summarization
- 🎬 Video: Upload videos for analysis with timestamps
- 📄 Document: Upload documents (PDF, DOCX, TXT) for analysis

✅ **Modern Features:**
- Beautiful Material Design 3 UI
- MVVM architecture with Hilt DI
- Structured outputs with JSON responses
- File upload with multiple format support
- Loading states and error handling

## 🔧 Troubleshooting

**Build Issues?**
- Make sure you have the latest Android Studio
- Check your internet connection for dependency downloads
- Clean and rebuild the project

**API Issues?**
- Verify your API key is correct
- Check your internet connection
- Ensure the API key has proper permissions

**Permission Issues?**
- Grant all requested permissions in device settings
- For API 33+, grant specific media permissions

## 📱 Test the App

1. **Text Tab**: Ask "What is artificial intelligence?"
2. **Image Tab**: Upload a photo and ask "What's in this image?"
3. **Audio Tab**: Upload an audio file and click "Summarize"
4. **Video Tab**: Upload a video and get a summary with timestamps
5. **Document Tab**: Upload a PDF and ask questions about it

That's it! You now have a fully functional AI-powered multimedia analysis app! 🎉 
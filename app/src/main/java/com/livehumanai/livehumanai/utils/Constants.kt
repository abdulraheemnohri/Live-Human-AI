package com.livehumanai.livehumanai.utils

/**
 * Constants defines application-wide constants and configuration values.
 */
object Constants {

    // App info
    const val APP_NAME = "Live Human AI"
    const val APP_VERSION = "1.0.0"
    const val APP_PACKAGE = "com.livehumanai.livehumanai"

    // Database
    const val DATABASE_NAME = "live_human_ai_db"
    const val DATABASE_VERSION = 1

    // Model directories
    const val MODEL_DIR = "models"
    const val LLM_MODEL_DIR = "llm"
    const val STT_MODEL_DIR = "stt"
    const val TTS_MODEL_DIR = "tts"
    const val VISION_MODEL_DIR = "vision"

    // Model file extensions
    const val MODEL_EXT_GGUF = ".gguf"
    const val MODEL_EXT_ONNX = ".onnx"
    const val MODEL_EXT_BIN = ".bin"

    // Audio configuration
    const val AUDIO_SAMPLE_RATE = 16000
    const val AUDIO_CHANNELS = 1
    const val AUDIO_FORMAT = android.media.AudioFormat.ENCODING_PCM_16BIT
    const val AUDIO_BUFFER_SIZE = 4096

    // Camera configuration
    const val CAMERA_RESOLUTION_WIDTH = 1280
    const val CAMERA_RESOLUTION_HEIGHT = 720
    const val CAMERA_FPS = 30

    // Performance thresholds
    const val THERMAL_THRESHOLD_WARM = 40.0f
    const val THERMAL_THRESHOLD_HOT = 50.0f
    const val THERMAL_THRESHOLD_CRITICAL = 60.0f
    const val BATTERY_THRESHOLD_LOW = 20.0f
    const val RAM_USAGE_THRESHOLD_HIGH = 90.0f

    // Model recommendations
    val RECOMMENDED_6GB_MODELS = listOf(
        "qwen3-0.6b-q4",
        "whisper-tiny",
        "piper-en",
        "yolo-nano"
    )

    val RECOMMENDED_16GB_MODELS = listOf(
        "qwen3-4b-q4",
        "whisper-base",
        "piper-en",
        "mobilenet-v3"
    )

    // Performance modes
    const val PERFORMANCE_MODE_BATTERY_SAVER = "Battery Saver"
    const val PERFORMANCE_MODE_BALANCED = "Balanced"
    const val PERFORMANCE_MODE_PERFORMANCE = "Performance"
    const val PERFORMANCE_MODE_MAXIMUM = "Maximum"

    // AI configuration
    const val DEFAULT_TEMPERATURE = 0.7f
    const val DEFAULT_TOP_P = 0.9f
    const val DEFAULT_MAX_TOKENS = 512
    const val DEFAULT_CONTEXT_SIZE = 2048

    // Network
    const val NETWORK_TIMEOUT = 30000 // 30 seconds
    const val MODEL_DOWNLOAD_CHUNK_SIZE = 1024 * 1024 // 1MB

    // File paths
    const val FILE_PROVIDER_AUTHORITY = "${APP_PACKAGE}.fileprovider"

    // Notification channels
    const val NOTIFICATION_CHANNEL_AI = "ai_processing"
    const val NOTIFICATION_CHANNEL_DOWNLOAD = "model_download"
    const val NOTIFICATION_CHANNEL_GENERAL = "general"

    // Shared preferences
    const val PREFS_NAME = "LiveHumanAIPrefs"
    const val PREFS_KEY_FIRST_LAUNCH = "first_launch"
    const val PREFS_KEY_ONBOARDING_COMPLETE = "onboarding_complete"

    // Error messages
    const val ERROR_MODEL_NOT_LOADED = "Model not loaded"
    const val ERROR_MODEL_LOAD_FAILED = "Failed to load model"
    const val ERROR_PERMISSION_DENIED = "Permission denied"
    const val ERROR_DEVICE_NOT_SUPPORTED = "Device not supported"
    const val ERROR_INSUFFICIENT_MEMORY = "Insufficient memory"
    const val ERROR_INSUFFICIENT_STORAGE = "Insufficient storage"

    // Success messages
    const val SUCCESS_MODEL_LOADED = "Model loaded successfully"
    const val SUCCESS_MODEL_DOWNLOADED = "Model downloaded successfully"
    const val SUCCESS_SETTINGS_SAVED = "Settings saved successfully"

    // URLs
    const val GITHUB_REPO_URL = "https://github.com/abdulraheemnohri/Live-Human-AI"
    const val MODEL_BASE_URL = "https://huggingface.co/models"

    // Timeouts
    const val AI_GENERATION_TIMEOUT = 60000 // 60 seconds
    const val STT_RECOGNITION_TIMEOUT = 30000 // 30 seconds
    const val TTS_SYNTHESIS_TIMEOUT = 60000 // 60 seconds

    // Memory limits
    const val MAX_CONVERSATION_HISTORY = 100
    const val MAX_MEMORY_ENTRIES = 1000
    const val MAX_MODEL_CACHE_SIZE = 5 // Number of models to keep in memory

    // Language support
    val SUPPORTED_LANGUAGES = listOf(
        "en", // English
        "ur", // Urdu
        "hi", // Hindi
        "ar", // Arabic
        "fr", // French
        "de", // German
        "es"  // Spanish
    )

    // Vision tasks
    val VISION_TASKS = listOf(
        "object_detection",
        "face_detection",
        "ocr",
        "scene_analysis",
        "document_scan",
        "qr_code"
    )
}

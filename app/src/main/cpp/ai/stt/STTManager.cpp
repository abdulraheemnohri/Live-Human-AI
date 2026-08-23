#include "STTManager.h"
#include <algorithm>
#include <thread>
#include <chrono>

STTManager::STTManager()
    : m_defaultSampleRate(16000),
      m_defaultAudioChannels(1),
      m_isRecognizing(false),
      m_stopRequested(false) {

    initializeAvailableModels();
}

STTManager::~STTManager() {
    shutdown();
}

bool STTManager::initialize() {
    initializeAvailableModels();
    return true;
}

void STTManager::shutdown() {
    std::lock_guard<std::mutex> lock(m_mutex);

    for (const auto& model : m_loadedModels) {
        unloadModelInternal(model);
    }
    m_loadedModels.clear();
    m_modelStates.clear();
}

bool STTManager::loadModel(const std::string& modelName) {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (isModelLoaded(modelName)) {
        return true;
    }

    if (!loadModelFromFile(modelName)) {
        return false;
    }

    m_loadedModels.push_back(modelName);

    ModelState state;
    state.loaded = true;
    state.context = nullptr;
    state.sampleRate = m_defaultSampleRate;
    state.audioChannels = m_defaultAudioChannels;
    m_modelStates[modelName] = state;

    return true;
}

bool STTManager::unloadModel(const std::string& modelName) {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (!isModelLoaded(modelName)) {
        return false;
    }

    unloadModelInternal(modelName);

    m_loadedModels.erase(
        std::remove(m_loadedModels.begin(), m_loadedModels.end(), modelName),
        m_loadedModels.end()
    );

    m_modelStates.erase(modelName);

    return true;
}

bool STTManager::isModelLoaded(const std::string& modelName) const {
    std::lock_guard<std::mutex> lock(m_mutex);
    return std::find(m_loadedModels.begin(), m_loadedModels.end(), modelName) != m_loadedModels.end();
}

std::vector<std::string> STTManager::getLoadedModels() const {
    std::lock_guard<std::mutex> lock(m_mutex);
    return m_loadedModels;
}

std::string STTManager::recognizeSpeech(
    const std::vector<float>& audioData,
    const std::string& modelName,
    const std::string& language
) {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (m_isRecognizing) {
        return "";
    }

    std::string selectedModel = modelName.empty() ? m_loadedModels.front() : modelName;
    if (!isModelLoaded(selectedModel)) {
        return "Model not loaded: " + selectedModel;
    }

    m_isRecognizing = true;
    m_stopRequested = false;

    // In a real implementation, this would process the audio data
    // and return the recognized text
    std::string result = "[Recognized text from " + selectedModel + ": " + std::to_string(audioData.size()) + " samples]";

    m_isRecognizing = false;
    return result;
}

void STTManager::recognizeSpeechStreaming(
    const std::vector<float>& audioChunk,
    std::function<void(const std::string&)> onPartialResult,
    std::function<void(const std::string&)> onFinalResult,
    const std::string& modelName,
    const std::string& language
) {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (m_isRecognizing) {
        if (onFinalResult) onFinalResult("");
        return;
    }

    std::string selectedModel = modelName.empty() ? m_loadedModels.front() : modelName;
    if (!isModelLoaded(selectedModel)) {
        if (onFinalResult) onFinalResult("");
        return;
    }

    m_isRecognizing = true;
    m_stopRequested = false;

    // Simulate streaming recognition
    std::string partialResult = "[Partial result from " + selectedModel + "]";
    if (onPartialResult) {
        onPartialResult(partialResult);
    }

    // Small delay to simulate processing
    std::this_thread::sleep_for(std::chrono::milliseconds(100));

    std::string finalResult = "[Final result from " + selectedModel + ": " + std::to_string(audioChunk.size()) + " samples]";
    m_isRecognizing = false;
    if (onFinalResult) onFinalResult(finalResult);
}

void STTManager::stopRecognition() {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_stopRequested = true;
    m_isRecognizing = false;
}

STTManager::ModelInfo STTManager::getModelInfo(const std::string& modelName) const {
    for (const auto& model : m_availableModels) {
        if (model.name == modelName) {
            return model;
        }
    }
    return ModelInfo{};
}

std::vector<STTManager::ModelInfo> STTManager::getAvailableModels() const {
    return m_availableModels;
}

float STTManager::benchmarkModel(const std::string& modelName) {
    // Placeholder for benchmarking
    return 5.0f; // Tokens per second
}

void STTManager::setSampleRate(int sampleRate) {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_defaultSampleRate = sampleRate;
    for (auto& pair : m_modelStates) {
        pair.second.sampleRate = sampleRate;
    }
}

int STTManager::getSampleRate() const {
    return m_defaultSampleRate;
}

void STTManager::setAudioChannels(int channels) {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_defaultAudioChannels = channels;
    for (auto& pair : m_modelStates) {
        pair.second.audioChannels = channels;
    }
}

int STTManager::getAudioChannels() const {
    return m_defaultAudioChannels;
}

bool STTManager::loadModelFromFile(const std::string& modelName) {
    // Placeholder for actual model loading
    return true;
}

void STTManager::unloadModelInternal(const std::string& modelName) {
    auto it = m_modelStates.find(modelName);
    if (it != m_modelStates.end()) {
        it->second.context = nullptr;
    }
}

void STTManager::initializeAvailableModels() {
    m_availableModels = {
        {
            "whisper-tiny",
            "1.0",
            50000000, // ~50MB
            "GGUF",
            {"en", "ur", "hi", "ar", "fr", "de", "es"},
            "MIT",
            "OpenAI",
            "abc123",
            false
        },
        {
            "whisper-base",
            "1.0",
            100000000, // ~100MB
            "GGUF",
            {"en", "ur", "hi", "ar", "fr", "de", "es"},
            "MIT",
            "OpenAI",
            "def456",
            false
        },
        {
            "whisper-small",
            "1.0",
            250000000, // ~250MB
            "GGUF",
            {"en", "ur", "hi", "ar", "fr", "de", "es"},
            "MIT",
            "OpenAI",
            "ghi789",
            false
        }
    };
}

#include "LLMManager.h"
#include "../../utils/Logger.h"
#include <algorithm>
#include <thread>
#include <chrono>

LLMManager::LLMManager()
    : m_defaultTemperature(0.7f),
      m_defaultTopP(0.9f),
      m_defaultMaxTokens(512),
      m_isGenerating(false),
      m_stopRequested(false) {

    initializeAvailableModels();
}

LLMManager::~LLMManager() {
    shutdown();
}

bool LLMManager::initialize() {
    // Initialize available models
    initializeAvailableModels();
    return true;
}

void LLMManager::shutdown() {
    std::lock_guard<std::mutex> lock(m_mutex);

    // Unload all models
    for (const auto& model : m_loadedModels) {
        unloadModelInternal(model);
    }
    m_loadedModels.clear();
    m_modelStates.clear();
}

bool LLMManager::loadModel(const std::string& modelName) {
    std::lock_guard<std::mutex> lock(m_mutex);

    // Check if already loaded
    if (isModelLoaded(modelName)) {
        return true;
    }

    // Load the model
    if (!loadModelFromFile(modelName)) {
        return false;
    }

    // Add to loaded models
    m_loadedModels.push_back(modelName);

    // Initialize model state
    ModelState state;
    state.loaded = true;
    state.context = nullptr; // Placeholder for actual model context
    state.contextString = "";
    state.temperature = m_defaultTemperature;
    state.topP = m_defaultTopP;
    state.maxTokens = m_defaultMaxTokens;
    m_modelStates[modelName] = state;

    return true;
}

bool LLMManager::unloadModel(const std::string& modelName) {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (!isModelLoaded(modelName)) {
        return false;
    }

    unloadModelInternal(modelName);

    // Remove from loaded models
    m_loadedModels.erase(
        std::remove(m_loadedModels.begin(), m_loadedModels.end(), modelName),
        m_loadedModels.end()
    );

    // Remove from model states
    m_modelStates.erase(modelName);

    return true;
}

bool LLMManager::isModelLoaded(const std::string& modelName) const {
    std::lock_guard<std::mutex> lock(m_mutex);
    return std::find(m_loadedModels.begin(), m_loadedModels.end(), modelName) != m_loadedModels.end();
}

std::vector<std::string> LLMManager::getLoadedModels() const {
    std::lock_guard<std::mutex> lock(m_mutex);
    return m_loadedModels;
}

std::string LLMManager::generate(
    const std::string& prompt,
    const std::string& modelName,
    float temperature,
    int maxTokens
) {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (m_isGenerating) {
        return "";
    }

    // Select model
    std::string selectedModel = modelName.empty() ? m_loadedModels.front() : modelName;
    if (!isModelLoaded(selectedModel)) {
        return "Model not loaded: " + selectedModel;
    }

    m_isGenerating = true;
    m_stopRequested = false;

    // In a real implementation, this would call the actual LLM inference
    // For now, return a placeholder response
    std::string response = "[Generated response from " + selectedModel + ": " + prompt + "]";

    m_isGenerating = false;
    return response;
}

void LLMManager::generateStreaming(
    const std::string& prompt,
    std::function<void(const std::string&)> onToken,
    std::function<void()> onComplete,
    const std::string& modelName,
    float temperature,
    int maxTokens
) {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (m_isGenerating) {
        if (onComplete) onComplete();
        return;
    }

    // Select model
    std::string selectedModel = modelName.empty() ? m_loadedModels.front() : modelName;
    if (!isModelLoaded(selectedModel)) {
        if (onComplete) onComplete();
        return;
    }

    m_isGenerating = true;
    m_stopRequested = false;

    // Simulate streaming generation
    std::string response = "[Streaming response from " + selectedModel + "]";
    for (size_t i = 0; i < response.size() && !m_stopRequested; ++i) {
        if (onToken) {
            onToken(response.substr(i, 1));
        }
        // Small delay to simulate streaming
        std::this_thread::sleep_for(std::chrono::milliseconds(50));
    }

    m_isGenerating = false;
    if (onComplete) onComplete();
}

void LLMManager::stopGeneration() {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_stopRequested = true;
    m_isGenerating = false;
}

void LLMManager::resetContext(const std::string& modelName) {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (modelName.empty()) {
        for (auto& pair : m_modelStates) {
            pair.second.contextString = "";
        }
    } else if (m_modelStates.find(modelName) != m_modelStates.end()) {
        m_modelStates[modelName].contextString = "";
    }
}

void LLMManager::setContext(const std::string& context, const std::string& modelName) {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (modelName.empty()) {
        for (auto& pair : m_modelStates) {
            pair.second.contextString = context;
        }
    } else if (m_modelStates.find(modelName) != m_modelStates.end()) {
        m_modelStates[modelName].contextString = context;
    }
}

std::string LLMManager::getContext(const std::string& modelName) const {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (modelName.empty() && !m_modelStates.empty()) {
        return m_modelStates.begin()->second.contextString;
    }

    auto it = m_modelStates.find(modelName);
    if (it != m_modelStates.end()) {
        return it->second.contextString;
    }

    return "";
}

LLMManager::ModelInfo LLMManager::getModelInfo(const std::string& modelName) const {
    for (const auto& model : m_availableModels) {
        if (model.name == modelName) {
            return model;
        }
    }
    return ModelInfo{};
}

std::vector<LLMManager::ModelInfo> LLMManager::getAvailableModels() const {
    return m_availableModels;
}

float LLMManager::benchmarkModel(const std::string& modelName) {
    // In a real implementation, this would run a benchmark on the model
    // and return the performance metric (e.g., tokens per second)
    return 10.0f; // Placeholder value
}

void LLMManager::setTemperature(float temperature) {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_defaultTemperature = temperature;
    for (auto& pair : m_modelStates) {
        pair.second.temperature = temperature;
    }
}

float LLMManager::getTemperature() const {
    return m_defaultTemperature;
}

void LLMManager::setTopP(float topP) {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_defaultTopP = topP;
    for (auto& pair : m_modelStates) {
        pair.second.topP = topP;
    }
}

float LLMManager::getTopP() const {
    return m_defaultTopP;
}

void LLMManager::setMaxTokens(int maxTokens) {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_defaultMaxTokens = maxTokens;
    for (auto& pair : m_modelStates) {
        pair.second.maxTokens = maxTokens;
    }
}

int LLMManager::getMaxTokens() const {
    return m_defaultMaxTokens;
}

bool LLMManager::loadModelFromFile(const std::string& modelName) {
    // In a real implementation, this would load the model file
    // and initialize the model context
    // For now, just return true to simulate success
    return true;
}

void LLMManager::unloadModelInternal(const std::string& modelName) {
    // In a real implementation, this would clean up the model context
    auto it = m_modelStates.find(modelName);
    if (it != m_modelStates.end()) {
        // Clean up the model context
        it->second.context = nullptr;
    }
}

void LLMManager::initializeAvailableModels() {
    // Initialize with some default models
    m_availableModels = {
        {
            "qwen3-0.6b-q4",
            "1.0",
            400000000, // ~400MB
            "GGUF",
            "Q4",
            1000000000, // ~1GB RAM requirement
            {"en", "ur", "hi", "ar"},
            false,
            false,
            "Apache 2.0",
            "Qwen",
            "abc123",
            false
        },
        {
            "qwen3-1.7b-q4",
            "1.0",
            1000000000, // ~1GB
            "GGUF",
            "Q4",
            2000000000, // ~2GB RAM requirement
            {"en", "ur", "hi", "ar"},
            false,
            false,
            "Apache 2.0",
            "Qwen",
            "def456",
            false
        },
        {
            "qwen3-4b-q4",
            "1.0",
            2000000000, // ~2GB
            "GGUF",
            "Q4",
            4000000000, // ~4GB RAM requirement
            {"en", "ur", "hi", "ar"},
            false,
            false,
            "Apache 2.0",
            "Qwen",
            "ghi789",
            false
        }
    };
}

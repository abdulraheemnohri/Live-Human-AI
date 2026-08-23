#ifndef LIVE_HUMAN_AI_STT_MANAGER_H
#define LIVE_HUMAN_AI_STT_MANAGER_H

#include <string>
#include <vector>
#include <map>
#include <memory>
#include <mutex>
#include <functional>

// STTManager handles speech-to-text operations
class STTManager {
public:
    STTManager();
    ~STTManager();

    // Initialization and shutdown
    bool initialize();
    void shutdown();

    // Model management
    bool loadModel(const std::string& modelName);
    bool unloadModel(const std::string& modelName);
    bool isModelLoaded(const std::string& modelName) const;
    std::vector<std::string> getLoadedModels() const;

    // Speech recognition
    std::string recognizeSpeech(
        const std::vector<float>& audioData,
        const std::string& modelName = "",
        const std::string& language = "en"
    );

    // Streaming speech recognition
    void recognizeSpeechStreaming(
        const std::vector<float>& audioChunk,
        std::function<void(const std::string&)> onPartialResult,
        std::function<void(const std::string&)> onFinalResult,
        const std::string& modelName = "",
        const std::string& language = "en"
    );

    // Stop recognition
    void stopRecognition();

    // Model information
    struct ModelInfo {
        std::string name;
        std::string version;
        size_t size; // in bytes
        std::string format;
        std::vector<std::string> supportedLanguages;
        std::string license;
        std::string source;
        std::string checksum;
        bool isInstalled;
    };

    ModelInfo getModelInfo(const std::string& modelName) const;
    std::vector<ModelInfo> getAvailableModels() const;

    // Benchmarking
    float benchmarkModel(const std::string& modelName);

    // Audio configuration
    void setSampleRate(int sampleRate);
    int getSampleRate() const;

    void setAudioChannels(int channels);
    int getAudioChannels() const;

private:
    struct ModelState {
        bool loaded;
        void* context; // Opaque pointer to the model context
        int sampleRate;
        int audioChannels;
    };

    mutable std::mutex m_mutex;
    std::vector<std::string> m_loadedModels;
    std::map<std::string, ModelState> m_modelStates;
    std::vector<ModelInfo> m_availableModels;

    int m_defaultSampleRate;
    int m_defaultAudioChannels;

    bool m_isRecognizing;
    bool m_stopRequested;

    // Internal methods
    bool loadModelFromFile(const std::string& modelName);
    void unloadModelInternal(const std::string& modelName);
    void initializeAvailableModels();
};

#endif // LIVE_HUMAN_AI_STT_MANAGER_H

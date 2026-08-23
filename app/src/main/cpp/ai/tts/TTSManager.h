#ifndef LIVE_HUMAN_AI_TTS_MANAGER_H
#define LIVE_HUMAN_AI_TTS_MANAGER_H

#include <string>
#include <vector>
#include <memory>
#include <mutex>
#include <functional>

// TTSManager handles text-to-speech operations
class TTSManager {
public:
    TTSManager();
    ~TTSManager();

    // Initialization and shutdown
    bool initialize();
    void shutdown();

    // Model management
    bool loadModel(const std::string& modelName);
    bool unloadModel(const std::string& modelName);
    bool isModelLoaded(const std::string& modelName) const;
    std::vector<std::string> getLoadedModels() const;

    // Speech synthesis
    std::vector<float> synthesizeSpeech(
        const std::string& text,
        const std::string& modelName = "",
        const std::string& language = "en",
        float speed = 1.0f,
        float pitch = 1.0f
    );

    // Streaming speech synthesis
    void synthesizeSpeechStreaming(
        const std::string& text,
        std::function<void(const std::vector<float>&)> onAudioChunk,
        std::function<void()> onComplete,
        const std::string& modelName = "",
        const std::string& language = "en",
        float speed = 1.0f,
        float pitch = 1.0f
    );

    // Stop synthesis
    void stopSynthesis();

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

    // Voice configuration
    void setVoice(const std::string& voice);
    std::string getVoice() const;

    void setSpeed(float speed);
    float getSpeed() const;

    void setPitch(float pitch);
    float getPitch() const;

private:
    struct ModelState {
        bool loaded;
        void* context; // Opaque pointer to the model context
        std::string voice;
        float speed;
        float pitch;
    };

    std::mutex m_mutex;
    std::vector<std::string> m_loadedModels;
    std::map<std::string, ModelState> m_modelStates;
    std::vector<ModelInfo> m_availableModels;

    std::string m_defaultVoice;
    float m_defaultSpeed;
    float m_defaultPitch;

    bool m_isSynthesizing;
    bool m_stopRequested;

    // Internal methods
    bool loadModelFromFile(const std::string& modelName);
    void unloadModelInternal(const std::string& modelName);
    void initializeAvailableModels();
};

#endif // LIVE_HUMAN_AI_TTS_MANAGER_H

#ifndef LIVE_HUMAN_AI_AI_ENGINE_H
#define LIVE_HUMAN_AI_AI_ENGINE_H

#include <string>
#include <vector>
#include <memory>
#include <mutex>

// Forward declarations
class LLMManager;
class STTManager;
class TTSManager;
class VisionManager;
class ModelRouter;

// AIEngine manages all AI-related operations
class AIEngine {
public:
    AIEngine();
    ~AIEngine();

    // Initialization and shutdown
    bool initialize();
    void shutdown();

    // Model management
    bool loadModel(const std::string& modelName);
    bool unloadModel(const std::string& modelName);
    bool switchModel(const std::string& fromModel, const std::string& toModel);
    std::vector<std::string> getLoadedModels() const;

    // AI generation
    std::string generate(
        const std::string& prompt,
        const std::string& modelName = "",
        float temperature = 0.7f,
        int maxTokens = 512
    );

    // Streaming generation
    void generateStreaming(
        const std::string& prompt,
        std::function<void(const std::string&)> onToken,
        std::function<void()> onComplete,
        const std::string& modelName = "",
        float temperature = 0.7f,
        int maxTokens = 512
    );

    // Stop generation
    void stopGeneration();

    // Context management
    void resetContext(const std::string& modelName = "");
    void setContext(const std::string& context, const std::string& modelName = "");

    // Model information
    struct ModelInfo {
        std::string name;
        std::string version;
        size_t size; // in bytes
        std::string format;
        std::string quantization;
        size_t ramRequirement;
        std::vector<std::string> supportedLanguages;
        bool supportsVision;
        bool supportsAudio;
        std::string license;
        std::string source;
        std::string checksum;
        bool isInstalled;
    };

    ModelInfo getModelInfo(const std::string& modelName) const;
    std::vector<ModelInfo> getAvailableModels() const;

    // Benchmarking
    float benchmarkModel(const std::string& modelName);

    // AI Engine status
    enum class Status {
        IDLE,
        LOADING_MODEL,
        GENERATING,
        STREAMING,
        ERROR
    };

    Status getStatus() const;

private:
    std::unique_ptr<LLMManager> m_llmManager;
    std::unique_ptr<STTManager> m_sttManager;
    std::unique_ptr<TTSManager> m_ttsManager;
    std::unique_ptr<VisionManager> m_visionManager;
    std::unique_ptr<ModelRouter> m_modelRouter;

    Status m_status;
    mutable std::mutex m_mutex;
};

#endif // LIVE_HUMAN_AI_AI_ENGINE_H

#ifndef LIVE_HUMAN_AI_LLM_MANAGER_H
#define LIVE_HUMAN_AI_LLM_MANAGER_H

#include <string>
#include <vector>
#include <map>
#include <memory>
#include <mutex>
#include <functional>

// LLMManager handles loading, unloading, and running LLM models
class LLMManager {
public:
    LLMManager();
    ~LLMManager();

    // Initialization and shutdown
    bool initialize();
    void shutdown();

    // Model management
    bool loadModel(const std::string& modelName);
    bool unloadModel(const std::string& modelName);
    bool isModelLoaded(const std::string& modelName) const;
    std::vector<std::string> getLoadedModels() const;

    // Generation
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
    std::string getContext(const std::string& modelName = "") const;

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

    // Model configuration
    void setTemperature(float temperature);
    float getTemperature() const;

    void setTopP(float topP);
    float getTopP() const;

    void setMaxTokens(int maxTokens);
    int getMaxTokens() const;

private:
    struct ModelState {
        bool loaded;
        void* context; // Opaque pointer to the model context
        std::string contextString;
        float temperature;
        float topP;
        int maxTokens;
    };

    mutable std::mutex m_mutex;
    std::vector<std::string> m_loadedModels;
    std::map<std::string, ModelState> m_modelStates;
    std::vector<ModelInfo> m_availableModels;

    float m_defaultTemperature;
    float m_defaultTopP;
    int m_defaultMaxTokens;

    bool m_isGenerating;
    bool m_stopRequested;

    // Internal methods
    bool loadModelFromFile(const std::string& modelName);
    void unloadModelInternal(const std::string& modelName);
    void initializeAvailableModels();
};

#endif // LIVE_HUMAN_AI_LLM_MANAGER_H

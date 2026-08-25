#ifndef LIVE_HUMAN_AI_LLM_MANAGER_H
#define LIVE_HUMAN_AI_LLM_MANAGER_H

#include "LLMBackend.h"
#include <string>
#include <vector>
#include <map>
#include <memory>
#include <mutex>
#include <functional>

class LLMManager {
public:
    LLMManager();
    ~LLMManager();
    bool initialize();
    void shutdown();
    bool loadModel(const std::string& modelName);
    bool unloadModel(const std::string& modelName);
    bool isModelLoaded(const std::string& modelName) const;
    std::vector<std::string> getLoadedModels() const;
    std::string generate(const std::string& prompt, const std::string& modelName = "", float temperature = 0.7f, int maxTokens = 512);
    void generateStreaming(const std::string& prompt, std::function<void(const std::string&)> onToken, std::function<void()> onComplete, const std::string& modelName = "", float temperature = 0.7f, int maxTokens = 512);
    void stopGeneration();
    void resetContext(const std::string& modelName = "");
    void setContext(const std::string& context, const std::string& modelName = "");
    std::string getContext(const std::string& modelName = "") const;

    struct ModelInfo {
        std::string name, version, format, quantization, license, source, checksum;
        size_t size = 0, ramRequirement = 0;
        std::vector<std::string> supportedLanguages;
        bool supportsVision = false, supportsAudio = false, isInstalled = false;
    };
    ModelInfo getModelInfo(const std::string& modelName) const;
    std::vector<ModelInfo> getAvailableModels() const;
    float benchmarkModel(const std::string& modelName);
    void setTemperature(float temperature);
    float getTemperature() const;
    void setTopP(float topP);
    float getTopP() const;
    void setMaxTokens(int maxTokens);
    int getMaxTokens() const;

private:
    struct ModelState {
        bool loaded = false;
        std::string contextString;
        float temperature = 0.7f;
        float topP = 0.9f;
        int maxTokens = 512;
        std::unique_ptr<LLMBackend> backend;
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

    bool loadModelFromFile(const std::string& modelName);
    void unloadModelInternal(const std::string& modelName);
    void initializeAvailableModels();
};

#endif

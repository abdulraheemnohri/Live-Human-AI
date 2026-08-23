#ifndef LIVE_HUMAN_AI_MODEL_ROUTER_H
#define LIVE_HUMAN_AI_MODEL_ROUTER_H

#include <string>
#include <vector>
#include <map>

// ModelRouter selects the appropriate model based on task and device capabilities
class ModelRouter {
public:
    ModelRouter();
    ~ModelRouter();

    // Initialization and shutdown
    bool initialize();
    void shutdown();

    // Model selection
    std::string selectModel(
        const std::string& taskType,
        const std::string& language = "",
        bool requiresVision = false,
        bool requiresAudio = false
    ) const;

    // Model type checking
    bool isLLMModel(const std::string& modelName) const;
    bool isSTTModel(const std::string& modelName) const;
    bool isTTSModel(const std::string& modelName) const;
    bool isVisionModel(const std::string& modelName) const;

    // Device profile-based selection
    enum class DeviceProfile {
        LOW_END,      // < 4GB RAM
        MID_RANGE,     // 4GB - 8GB RAM
        HIGH_END,     // 8GB - 16GB RAM
        FLAGSHIP      // > 16GB RAM
    };

    void setDeviceProfile(DeviceProfile profile);
    DeviceProfile getDeviceProfile() const;

    // Task complexity levels
    enum class TaskComplexity {
        SIMPLE,       // Simple commands, no LLM needed
        BASIC,        // Basic conversation, small LLM
        MODERATE,     // Complex reasoning, medium LLM
        COMPLEX,      // Advanced tasks, large LLM
        VISION,       // Vision tasks
        AUDIO,        // Audio tasks
        MULTIMODAL    // Combined vision + audio + text
    };

    // Model categories
    struct ModelCategory {
        std::string name;
        std::vector<std::string> models;
        std::string defaultModel;
    };

    const ModelCategory& getLLMModels() const;
    const ModelCategory& getSTTModels() const;
    const ModelCategory& getTTSModels() const;
    const ModelCategory& getVisionModels() const;

private:
    DeviceProfile m_deviceProfile;
    ModelCategory m_llmModels;
    ModelCategory m_sttModels;
    ModelCategory m_ttsModels;
    ModelCategory m_visionModels;

    // Internal selection logic
    std::string selectLLMModel(TaskComplexity complexity) const;
    std::string selectSTTModel(const std::string& language) const;
    std::string selectTTSModel(const std::string& language) const;
    std::string selectVisionModel() const;
};

#endif // LIVE_HUMAN_AI_MODEL_ROUTER_H

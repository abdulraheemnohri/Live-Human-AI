#ifndef LIVEHUMANAI_MODELMANAGER_H
#define LIVEHUMANAI_MODELMANAGER_H

#include <string>
#include <vector>
#include <memory>
#include <unordered_map>

namespace livehumanai {
namespace models {

enum class ModelType {
    LLM, STT, TTS, VISION, EMBEDDING, WAKE_WORD, VAD, OCR
};

enum class ModelState {
    NOT_INSTALLED, INSTALLED, LOADED, UNLOADING, ERROR
};

struct ModelInfo {
    std::string id;
    std::string name;
    ModelType type;
    ModelState state;
    std::string path;
    size_t sizeBytes;
    std::string profile;  // lite, standard, pro, ultra
    bool verified;
};

class ModelManager {
public:
    explicit ModelManager(const std::string& basePath);
    ~ModelManager();
    
    bool initialize();
    void shutdown();
    
    std::vector<ModelInfo> getInstalledModels() const;
    ModelInfo getModelInfo(const std::string& modelId) const;
    
    bool isModelInstalled(const std::string& modelId) const;
    bool isModelLoaded(const std::string& modelId) const;
    
    std::string getModelsDirectory() const { return modelsPath_; }
    std::string getModelPath(const std::string& modelId) const;
    
private:
    std::string modelsPath_;
    std::unordered_map<std::string, ModelInfo> models_;
    bool initialized_;
};

} // namespace models
} // namespace livehumanai

#endif

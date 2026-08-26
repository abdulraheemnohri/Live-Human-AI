#include "ModelManager.h"
#include "../utils/Logger.h"
#include "../utils/FileUtils.h"

namespace livehumanai {
namespace models {

using namespace utils;

ModelManager::ModelManager(const std::string& basePath)
    : modelsPath_(basePath + "/models"), initialized_(false) {}

ModelManager::~ModelManager() {
    shutdown();
}

bool ModelManager::initialize() {
    if (initialized_) return true;

    LOGI("ModelManager: Initializing with base path");

    // Create directories if they don't exist
    utils::FileUtils::createDirectory(modelsPath_);
    utils::FileUtils::createDirectory(modelsPath_ + "/llm");
    utils::FileUtils::createDirectory(modelsPath_ + "/stt");
    utils::FileUtils::createDirectory(modelsPath_ + "/tts");
    utils::FileUtils::createDirectory(modelsPath_ + "/vision");
    utils::FileUtils::createDirectory(modelsPath_ + "/manifests");

    initialized_ = true;
    LOGI("ModelManager: Initialized successfully");
    return true;
}

void ModelManager::shutdown() {
    models_.clear();
    initialized_ = false;
    LOGI("ModelManager: Shutdown complete");
}

std::vector<ModelInfo> ModelManager::getInstalledModels() const {
    std::vector<ModelInfo> result;
    for (const auto& pair : models_) {
        if (pair.second.state == ModelState::INSTALLED ||
            pair.second.state == ModelState::LOADED) {
            result.push_back(pair.second);
        }
    }
    return result;
}

ModelInfo ModelManager::getModelInfo(const std::string& modelId) const {
    auto it = models_.find(modelId);
    if (it != models_.end()) {
        return it->second;
    }
    return ModelInfo{};
}

bool ModelManager::isModelInstalled(const std::string& modelId) const {
    auto it = models_.find(modelId);
    return it != models_.end() &&
           (it->second.state == ModelState::INSTALLED ||
            it->second.state == ModelState::LOADED);
}

bool ModelManager::isModelLoaded(const std::string& modelId) const {
    auto it = models_.find(modelId);
    return it != models_.end() && it->second.state == ModelState::LOADED;
}

std::string ModelManager::getModelPath(const std::string& modelId) const {
    return modelsPath_ + "/" + modelId;
}

} // namespace models
} // namespace livehumanai

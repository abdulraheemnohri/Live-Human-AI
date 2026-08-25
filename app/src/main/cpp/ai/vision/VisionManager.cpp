#include "VisionManager.h"
#include <algorithm>
#include <fstream>

VisionManager::VisionManager() { initializeAvailableModels(); }
VisionManager::~VisionManager() { shutdown(); }

bool VisionManager::initialize() { initializeAvailableModels(); return true; }

void VisionManager::shutdown() {
    std::lock_guard<std::mutex> lock(m_mutex);
    for (auto& entry : m_modelStates) {
        if (entry.second.backend) entry.second.backend->unload();
    }
    m_modelStates.clear();
    m_loadedModels.clear();
}

bool VisionManager::loadModel(const std::string& name) {
    std::lock_guard<std::mutex> lock(m_mutex);
    if (name.empty()) return false;
    if (m_modelStates.count(name) && m_modelStates.at(name).loaded) return true;
    return loadModelFromFile(name);
}

bool VisionManager::unloadModel(const std::string& name) {
    std::lock_guard<std::mutex> lock(m_mutex);
    auto it = m_modelStates.find(name);
    if (it == m_modelStates.end()) return false;
    if (it->second.backend) it->second.backend->unload();
    m_modelStates.erase(it);
    m_loadedModels.erase(std::remove(m_loadedModels.begin(), m_loadedModels.end(), name), m_loadedModels.end());
    return true;
}

bool VisionManager::isModelLoaded(const std::string& name) const {
    std::lock_guard<std::mutex> lock(m_mutex);
    auto it = m_modelStates.find(name);
    return it != m_modelStates.end() && it->second.loaded && it->second.backend && it->second.backend->isLoaded();
}

std::vector<std::string> VisionManager::getLoadedModels() const {
    std::lock_guard<std::mutex> lock(m_mutex);
    return m_loadedModels;
}

std::vector<std::string> VisionManager::detectObjects(const cv::Mat& image, const std::string& name) {
    std::lock_guard<std::mutex> lock(m_mutex);
    const std::string model = name.empty() ? (m_loadedModels.empty() ? std::string{} : m_loadedModels.front()) : name;
    auto it = m_modelStates.find(model);
    if (model.empty() || it == m_modelStates.end() || !it->second.backend || !it->second.backend->isLoaded()) return {};
    std::vector<std::string> result;
    for (const auto& detection : it->second.backend->detectObjects(image)) {
        if (detection.confidence >= 0.0f && !detection.label.empty()) result.push_back(detection.label);
    }
    return result;
}

std::vector<std::string> VisionManager::detectFaces(const cv::Mat& image, const std::string& name) {
    std::lock_guard<std::mutex> lock(m_mutex);
    const std::string model = name.empty() ? (m_loadedModels.empty() ? std::string{} : m_loadedModels.front()) : name;
    auto it = m_modelStates.find(model);
    if (model.empty() || it == m_modelStates.end() || !it->second.backend || !it->second.backend->isLoaded()) return {};
    std::vector<std::string> result;
    for (const auto& detection : it->second.backend->detectFaces(image)) {
        if (!detection.label.empty()) result.push_back(detection.label);
    }
    return result;
}

std::string VisionManager::detectText(const cv::Mat& image, const std::string& name) {
    std::lock_guard<std::mutex> lock(m_mutex);
    const std::string model = name.empty() ? (m_loadedModels.empty() ? std::string{} : m_loadedModels.front()) : name;
    auto it = m_modelStates.find(model);
    if (model.empty() || it == m_modelStates.end() || !it->second.backend || !it->second.backend->isLoaded()) return {};
    return it->second.backend->detectText(image);
}

std::string VisionManager::analyzeScene(const cv::Mat& image, const std::string& name) {
    std::lock_guard<std::mutex> lock(m_mutex);
    const std::string model = name.empty() ? (m_loadedModels.empty() ? std::string{} : m_loadedModels.front()) : name;
    auto it = m_modelStates.find(model);
    if (model.empty() || it == m_modelStates.end() || !it->second.backend || !it->second.backend->isLoaded()) return {};
    return it->second.backend->analyzeScene(image);
}

VisionManager::ModelInfo VisionManager::getModelInfo(const std::string& name) const {
    std::lock_guard<std::mutex> lock(m_mutex);
    for (const auto& model : m_availableModels) if (model.name == name) return model;
    return {};
}

std::vector<VisionManager::ModelInfo> VisionManager::getAvailableModels() const {
    std::lock_guard<std::mutex> lock(m_mutex);
    return m_availableModels;
}

float VisionManager::benchmarkModel(const std::string& name) { return isModelLoaded(name) ? 0.0f : 0.0f; }

bool VisionManager::loadModelFromFile(const std::string& name) {
    std::ifstream file(name, std::ios::binary | std::ios::ate);
    if (!file.good() || file.tellg() <= 0) return false;
    auto backend = createOnnxVisionBackend();
    if (!backend || !backend->load(name)) return false;
    ModelState state;
    state.loaded = true;
    state.backend = std::move(backend);
    m_modelStates[name] = std::move(state);
    m_loadedModels.push_back(name);
    return true;
}

void VisionManager::unloadModelInternal(const std::string& name) {
    auto it = m_modelStates.find(name);
    if (it != m_modelStates.end() && it->second.backend) it->second.backend->unload();
}

void VisionManager::initializeAvailableModels() {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_availableModels.clear();
    m_availableModels.push_back({"yolo-nano", "1.0", 0, "ONNX", "object_detection", {"object_detection"}, "", "", "", false});
    m_availableModels.push_back({"mobilenet-v3", "1.0", 0, "ONNX", "image_classification", {"image_classification"}, "", "", "", false});
    m_availableModels.push_back({"ocr-lightweight", "1.0", 0, "ONNX", "ocr", {"ocr", "text_detection"}, "", "", "", false});
}

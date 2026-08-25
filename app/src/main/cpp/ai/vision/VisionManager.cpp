#include "VisionManager.h"
#include <algorithm>
#include <chrono>
#include <fstream>

VisionManager::VisionManager() { initializeAvailableModels(); }
VisionManager::~VisionManager() { shutdown(); }
bool VisionManager::initialize() { initializeAvailableModels(); return true; }
void VisionManager::shutdown() { std::lock_guard<std::mutex> lock(m_mutex); for(auto& p:m_modelStates) if(p.second.backend) p.second.backend->unload(); m_modelStates.clear(); m_loadedModels.clear(); }

bool VisionManager::loadModel(const std::string& modelName) {
    std::lock_guard<std::mutex> lock(m_mutex);
    if(modelName.empty()) return false;
    if(m_modelStates.count(modelName) && m_modelStates.at(modelName).loaded) return true;
    if(!loadModelFromFile(modelName)) return false;
    return true;
}
bool VisionManager::unloadModel(const std::string& modelName) { std::lock_guard<std::mutex> lock(m_mutex); auto it=m_modelStates.find(modelName); if(it==m_modelStates.end()) return false; if(it->second.backend) it->second.backend->unload(); m_modelStates.erase(it); m_loadedModels.erase(std::remove(m_loadedModels.begin(),m_loadedModels.end(),modelName),m_loadedModels.end()); return true; }
bool VisionManager::isModelLoaded(const std::string& modelName) const { std::lock_guard<std::mutex> lock(m_mutex); auto it=m_modelStates.find(modelName); return it!=m_modelStates.end() && it->second.loaded && it->second.backend && it->second.backend->isLoaded(); }
std::vector<std::string> VisionManager::getLoadedModels() const { std::lock_guard<std::mutex> lock(m_mutex); return m_loadedModels; }

std::vector<std::string> VisionManager::detectObjects(const cv::Mat& image,const std::string& modelName) { std::lock_guard<std::mutex> lock(m_mutex); std::string selected=modelName.empty()?(m_loadedModels.empty()?std::string{}:m_loadedModels.front()):modelName; auto it=m_modelStates.find(selected); if(selected.empty()||it==m_modelStates.end()||!it->second.backend||!it->second.backend->isLoaded()) return {}; std::vector<std::string> out; for(const auto& d:it->second.backend->detectObjects(image)) if(d.confidence>=0.0f && !d.label.empty()) out.push_back(d.label); return out; }
std::vector<std::string> VisionManager::detectFaces(const cv::Mat& image,const std::string& modelName) { std::lock_guard<std::mutex> lock(m_mutex); std::string selected=modelName.empty()?(m_loadedModels.empty()?std::string{}:m_loadedModels.front()):modelName; auto it=m_modelStates.find(selected); if(selected.empty()||it==m_modelStates.end()||!it->second.backend||!it->second.backend->isLoaded()) return {}; std::vector<std::string> out; int n=0; for(const auto& d:it->second.backend->detectFaces(image)) if(!d.label.empty()) out.push_back(d.label.empty()?"face_"+std::to_string(++n):d.label); return out; }
std::string VisionManager::detectText(const cv::Mat& image,const std::string& modelName) { std::lock_guard<std::mutex> lock(m_mutex); std::string selected=modelName.empty()?(m_loadedModels.empty()?std::string{}:m_loadedModels.front()):modelName; auto it=m_modelStates.find(selected); if(selected.empty()||it==m_modelStates.end()||!it->second.backend||!it->second.backend->isLoaded()) return {}; return it->second.backend->detectText(image); }
std::string VisionManager::analyzeScene(const cv::Mat& image,const std::string& modelName) { std::lock_guard<std::mutex> lock(m_mutex); std::string selected=modelName.empty()?(m_loadedModels.empty()?std::string{}:m_loadedModels.front()):modelName; auto it=m_modelStates.find(selected); if(selected.empty()||it==m_modelStates.end()||!it->second.backend||!it->second.backend->isLoaded()) return {}; return it->second.backend->analyzeScene(image); }

VisionManager::ModelInfo VisionManager::getModelInfo(const std::string& name) const { std::lock_guard<std::mutex> lock(m_mutex); for(const auto& m:m_availableModels) if(m.name==name) return m; return {}; }
std::vector<VisionManager::ModelInfo> VisionManager::getAvailableModels() const { std::lock_guard<std::mutex> lock(m_mutex); return m_availableModels; }
float VisionManager::benchmarkModel(const std::string& modelName) { if(!isModelLoaded(modelName)) return 0.0f; return 0.0f; }
cv::Mat VisionManager::preprocessImage(const cv::Mat& image) { return image; }

bool VisionManager::loadModelFromFile(const std::string& modelName) {
    std::ifstream file(modelName,std::ios::binary|std::ios::ate); if(!file.good()||file.tellg()<=0) return false;
    auto backend=createOnnxVisionBackend(); if(!backend||!backend->load(modelName)) return false;
    ModelState state; state.loaded=true; state.backend=std::move(backend); m_modelStates[modelName]=std::move(state); m_loadedModels.push_back(modelName); return true;
}
void VisionManager::unloadModelInternal(const std::string& modelName) { auto it=m_modelStates.find(modelName); if(it!=m_modelStates.end()&&it->second.backend) it->second.backend->unload(); }
void VisionManager::initializeAvailableModels() { std::lock_guard<std::mutex> lock(m_mutex); m_availableModels={{"yolo-nano","1.0",0,"ONNX","object_detection",{"object_detection"},"","","",false},{"mobilenet-v3","1.0",0,"ONNX","image_classification",{"image_classification"},"","","",false},{"ocr-lightweight","1.0",0,"ONNX","ocr",{"ocr","text_detection"},"","","",false}}; }

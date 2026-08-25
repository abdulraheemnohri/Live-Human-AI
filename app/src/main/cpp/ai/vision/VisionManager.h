#ifndef LIVE_HUMAN_AI_VISION_MANAGER_H
#define LIVE_HUMAN_AI_VISION_MANAGER_H
#include "VisionBackend.h"
#include <opencv2/core.hpp>
#include <string>
#include <vector>
#include <map>
#include <memory>
#include <mutex>
class VisionManager { public:
 VisionManager(); ~VisionManager(); bool initialize(); void shutdown(); bool loadModel(const std::string&); bool unloadModel(const std::string&); bool isModelLoaded(const std::string&) const; std::vector<std::string> getLoadedModels() const; std::vector<std::string> detectObjects(const cv::Mat&,const std::string&=""); std::vector<std::string> detectFaces(const cv::Mat&,const std::string&=""); std::string detectText(const cv::Mat&,const std::string&=""); std::string analyzeScene(const cv::Mat&,const std::string&="");
 struct ModelInfo { std::string name,version; size_t size=0; std::string format,type; std::vector<std::string> supportedTasks; std::string license,source,checksum; bool isInstalled=false; };
 ModelInfo getModelInfo(const std::string&) const; std::vector<ModelInfo> getAvailableModels() const; float benchmarkModel(const std::string&); cv::Mat preprocessImage(const cv::Mat&);
 private: struct ModelState{bool loaded=false;std::unique_ptr<VisionBackend> backend;}; mutable std::mutex m_mutex; std::vector<std::string> m_loadedModels; std::map<std::string,ModelState> m_modelStates; std::vector<ModelInfo> m_availableModels; bool loadModelFromFile(const std::string&); void unloadModelInternal(const std::string&); void initializeAvailableModels();
};
#endif

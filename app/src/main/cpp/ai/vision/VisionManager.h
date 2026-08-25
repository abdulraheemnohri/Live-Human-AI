#ifndef LIVE_HUMAN_AI_VISION_MANAGER_H
#define LIVE_HUMAN_AI_VISION_MANAGER_H

#include "VisionBackend.h"
#include <string>
#include <vector>
#include <map>
#include <memory>
#include <mutex>

#ifdef HAVE_OPENCV
#include <opencv2/core.hpp>
#else
namespace cv { class Mat; }
#endif

class VisionManager {
public:
    VisionManager();
    ~VisionManager();
    bool initialize();
    void shutdown();
    bool loadModel(const std::string& modelName);
    bool unloadModel(const std::string& modelName);
    bool isModelLoaded(const std::string& modelName) const;
    std::vector<std::string> getLoadedModels() const;
    std::vector<std::string> detectObjects(const cv::Mat& image, const std::string& modelName = "");
    std::vector<std::string> detectFaces(const cv::Mat& image, const std::string& modelName = "");
    std::string detectText(const cv::Mat& image, const std::string& modelName = "");
    std::string analyzeScene(const cv::Mat& image, const std::string& modelName = "");
    struct ModelInfo {
        std::string name, version; size_t size = 0; std::string format, type;
        std::vector<std::string> supportedTasks; std::string license, source, checksum; bool isInstalled = false;
    };
    ModelInfo getModelInfo(const std::string& modelName) const;
    std::vector<ModelInfo> getAvailableModels() const;
    float benchmarkModel(const std::string& modelName);
    cv::Mat preprocessImage(const cv::Mat& image);
private:
    struct ModelState { bool loaded = false; std::unique_ptr<VisionBackend> backend; };
    mutable std::mutex m_mutex;
    std::vector<std::string> m_loadedModels;
    std::map<std::string, ModelState> m_modelStates;
    std::vector<ModelInfo> m_availableModels;
    bool loadModelFromFile(const std::string& modelName);
    void unloadModelInternal(const std::string& modelName);
    void initializeAvailableModels();
};
#endif

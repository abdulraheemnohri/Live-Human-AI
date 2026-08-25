#ifndef LIVE_HUMAN_AI_VISION_MANAGER_H
#define LIVE_HUMAN_AI_VISION_MANAGER_H

#include "VisionBackend.h"
#include <string>
#include <vector>
#include <map>
#include <memory>
#include <mutex>

namespace cv { class Mat; }

class VisionManager {
public:
    VisionManager();
    ~VisionManager();
    bool initialize();
    void shutdown();
    bool loadModel(const std::string& name);
    bool unloadModel(const std::string& name);
    bool isModelLoaded(const std::string& name) const;
    std::vector<std::string> getLoadedModels() const;
    std::vector<std::string> detectObjects(const cv::Mat& image, const std::string& modelName = "");
    std::vector<std::string> detectFaces(const cv::Mat& image, const std::string& modelName = "");
    std::string detectText(const cv::Mat& image, const std::string& modelName = "");
    std::string analyzeScene(const cv::Mat& image, const std::string& modelName = "");

    struct ModelInfo {
        std::string name;
        std::string version;
        size_t size = 0;
        std::string format;
        std::string type;
        std::vector<std::string> supportedTasks;
        std::string license;
        std::string source;
        std::string checksum;
        bool isInstalled = false;
    };

    ModelInfo getModelInfo(const std::string& name) const;
    std::vector<ModelInfo> getAvailableModels() const;
    float benchmarkModel(const std::string& name);

private:
    struct ModelState { bool loaded = false; std::unique_ptr<VisionBackend> backend; };
    mutable std::mutex m_mutex;
    std::vector<std::string> m_loadedModels;
    std::map<std::string, ModelState> m_modelStates;
    std::vector<ModelInfo> m_availableModels;
    bool loadModelFromFile(const std::string& name);
    void unloadModelInternal(const std::string& name);
    void initializeAvailableModels();
};

#endif

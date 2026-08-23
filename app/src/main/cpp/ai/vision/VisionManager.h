#ifndef LIVE_HUMAN_AI_VISION_MANAGER_H
#define LIVE_HUMAN_AI_VISION_MANAGER_H

#include <string>
#include <vector>
#include <memory>
#include <mutex>
#include <functional>
#include <opencv2/core.hpp>

// VisionManager handles computer vision operations
class VisionManager {
public:
    VisionManager();
    ~VisionManager();

    // Initialization and shutdown
    bool initialize();
    void shutdown();

    // Model management
    bool loadModel(const std::string& modelName);
    bool unloadModel(const std::string& modelName);
    bool isModelLoaded(const std::string& modelName) const;
    std::vector<std::string> getLoadedModels() const;

    // Vision tasks
    std::vector<std::string> detectObjects(
        const cv::Mat& image,
        const std::string& modelName = ""
    );

    std::vector<std::string> detectFaces(
        const cv::Mat& image,
        const std::string& modelName = ""
    );

    std::string detectText(
        const cv::Mat& image,
        const std::string& modelName = ""
    );

    std::string analyzeScene(
        const cv::Mat& image,
        const std::string& modelName = ""
    );

    // Model information
    struct ModelInfo {
        std::string name;
        std::string version;
        size_t size; // in bytes
        std::string format;
        std::string type; // "object_detection", "face_detection", "ocr", etc.
        std::vector<std::string> supportedTasks;
        std::string license;
        std::string source;
        std::string checksum;
        bool isInstalled;
    };

    ModelInfo getModelInfo(const std::string& modelName) const;
    std::vector<ModelInfo> getAvailableModels() const;

    // Benchmarking
    float benchmarkModel(const std::string& modelName);

    // Image preprocessing
    cv::Mat preprocessImage(const cv::Mat& image);

private:
    struct ModelState {
        bool loaded;
        void* context; // Opaque pointer to the model context
    };

    std::mutex m_mutex;
    std::vector<std::string> m_loadedModels;
    std::map<std::string, ModelState> m_modelStates;
    std::vector<ModelInfo> m_availableModels;

    // Internal methods
    bool loadModelFromFile(const std::string& modelName);
    void unloadModelInternal(const std::string& modelName);
    void initializeAvailableModels();
};

#endif // LIVE_HUMAN_AI_VISION_MANAGER_H

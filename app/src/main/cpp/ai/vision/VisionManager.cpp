#include "VisionManager.h"
#include <algorithm>

VisionManager::VisionManager() {
    initializeAvailableModels();
}

VisionManager::~VisionManager() {
    shutdown();
}

bool VisionManager::initialize() {
    initializeAvailableModels();
    return true;
}

void VisionManager::shutdown() {
    std::lock_guard<std::mutex> lock(m_mutex);

    for (const auto& model : m_loadedModels) {
        unloadModelInternal(model);
    }
    m_loadedModels.clear();
    m_modelStates.clear();
}

bool VisionManager::loadModel(const std::string& modelName) {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (isModelLoaded(modelName)) {
        return true;
    }

    if (!loadModelFromFile(modelName)) {
        return false;
    }

    m_loadedModels.push_back(modelName);

    ModelState state;
    state.loaded = true;
    state.context = nullptr;
    m_modelStates[modelName] = state;

    return true;
}

bool VisionManager::unloadModel(const std::string& modelName) {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (!isModelLoaded(modelName)) {
        return false;
    }

    unloadModelInternal(modelName);

    m_loadedModels.erase(
        std::remove(m_loadedModels.begin(), m_loadedModels.end(), modelName),
        m_loadedModels.end()
    );

    m_modelStates.erase(modelName);

    return true;
}

bool VisionManager::isModelLoaded(const std::string& modelName) const {
    std::lock_guard<std::mutex> lock(m_mutex);
    return std::find(m_loadedModels.begin(), m_loadedModels.end(), modelName) != m_loadedModels.end();
}

std::vector<std::string> VisionManager::getLoadedModels() const {
    std::lock_guard<std::mutex> lock(m_mutex);
    return m_loadedModels;
}

std::vector<std::string> VisionManager::detectObjects(
    const cv::Mat& image,
    const std::string& modelName
) {
    std::lock_guard<std::mutex> lock(m_mutex);

    std::string selectedModel = modelName.empty() ? m_loadedModels.front() : modelName;
    if (!isModelLoaded(selectedModel)) {
        return {};
    }

    // In a real implementation, this would run object detection on the image
    // and return the detected objects as a vector of strings
    std::vector<std::string> objects = {"person", "bottle", "car"}; // Placeholder
    return objects;
}

std::vector<std::string> VisionManager::detectFaces(
    const cv::Mat& image,
    const std::string& modelName
) {
    std::lock_guard<std::mutex> lock(m_mutex);

    std::string selectedModel = modelName.empty() ? m_loadedModels.front() : modelName;
    if (!isModelLoaded(selectedModel)) {
        return {};
    }

    // In a real implementation, this would run face detection on the image
    std::vector<std::string> faces = {"face_1", "face_2"}; // Placeholder
    return faces;
}

std::string VisionManager::detectText(
    const cv::Mat& image,
    const std::string& modelName
) {
    std::lock_guard<std::mutex> lock(m_mutex);

    std::string selectedModel = modelName.empty() ? m_loadedModels.front() : modelName;
    if (!isModelLoaded(selectedModel)) {
        return "";
    }

    // In a real implementation, this would run OCR on the image
    return "Detected text from image"; // Placeholder
}

std::string VisionManager::analyzeScene(
    const cv::Mat& image,
    const std::string& modelName
) {
    std::lock_guard<std::mutex> lock(m_mutex);

    std::string selectedModel = modelName.empty() ? m_loadedModels.front() : modelName;
    if (!isModelLoaded(selectedModel)) {
        return "";
    }

    // In a real implementation, this would analyze the scene
    return "This is a scene with multiple objects and people."; // Placeholder
}

VisionManager::ModelInfo VisionManager::getModelInfo(const std::string& modelName) const {
    for (const auto& model : m_availableModels) {
        if (model.name == modelName) {
            return model;
        }
    }
    return ModelInfo{};
}

std::vector<VisionManager::ModelInfo> VisionManager::getAvailableModels() const {
    return m_availableModels;
}

float VisionManager::benchmarkModel(const std::string& modelName) {
    // Placeholder for benchmarking
    return 15.0f; // Frames per second
}

cv::Mat VisionManager::preprocessImage(const cv::Mat& image) {
    // Common preprocessing steps:
    // 1. Convert to RGB if needed
    // 2. Resize to model input size
    // 3. Normalize pixel values

    cv::Mat processedImage = image;

#ifdef HAVE_OPENCV
    // Convert to RGB if image is in BGR format
    if (image.channels() == 3) {
        cv::cvtColor(image, processedImage, cv::COLOR_BGR2RGB);
    } else {
        processedImage = image.clone();
    }

    // Resize to a common input size (e.g., 224x224)
    cv::resize(processedImage, processedImage, cv::Size(224, 224));

    // Normalize to [0, 1] range
    processedImage.convertTo(processedImage, CV_32F, 1.0 / 255.0);
#endif

    return processedImage;
}

bool VisionManager::loadModelFromFile(const std::string& modelName) {
    // Placeholder for actual model loading
    return true;
}

void VisionManager::unloadModelInternal(const std::string& modelName) {
    auto it = m_modelStates.find(modelName);
    if (it != m_modelStates.end()) {
        it->second.context = nullptr;
    }
}

void VisionManager::initializeAvailableModels() {
    m_availableModels = {
        {
            "yolo-nano",
            "1.0",
            5000000, // ~5MB
            "ONNX",
            "object_detection",
            {"object_detection"},
            "Apache 2.0",
            "Ultralytics",
            "abc123",
            false
        },
        {
            "mobilenet-v3",
            "1.0",
            10000000, // ~10MB
            "ONNX",
            "image_classification",
            {"image_classification", "object_detection"},
            "Apache 2.0",
            "TensorFlow",
            "def456",
            false
        },
        {
            "ocr-lightweight",
            "1.0",
            2000000, // ~2MB
            "ONNX",
            "ocr",
            {"ocr", "text_detection"},
            "MIT",
            "EasyOCR",
            "ghi789",
            false
        }
    };
}

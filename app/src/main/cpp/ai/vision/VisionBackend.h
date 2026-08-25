#ifndef LIVE_HUMAN_AI_VISION_BACKEND_H
#define LIVE_HUMAN_AI_VISION_BACKEND_H

#include <string>
#include <vector>
#include <cstddef>

#ifdef HAVE_OPENCV
#include <opencv2/core.hpp>
#else
namespace cv { class Mat; }
#endif

struct VisionDetection {
    std::string label;
    float confidence = 0.0f;
    int x = 0;
    int y = 0;
    int width = 0;
    int height = 0;
};

class VisionBackend {
public:
    virtual ~VisionBackend() = default;
    virtual bool load(const std::string& path) = 0;
    virtual void unload() = 0;
    virtual bool isLoaded() const = 0;
    virtual std::vector<VisionDetection> detectObjects(const cv::Mat& image) = 0;
    virtual std::vector<VisionDetection> detectFaces(const cv::Mat& image) = 0;
    virtual std::string detectText(const cv::Mat& image) = 0;
    virtual std::string analyzeScene(const cv::Mat& image) = 0;
    virtual void stop() = 0;
};

std::unique_ptr<VisionBackend> createOnnxVisionBackend();

#endif

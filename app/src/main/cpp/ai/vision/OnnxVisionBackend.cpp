#include "VisionBackend.h"
#include <atomic>
#include <fstream>
#include <memory>

#if __has_include(<onnxruntime_cxx_api.h>)
#include <onnxruntime_cxx_api.h>
#define JCL_HAS_ONNX_RUNTIME 1
#else
#define JCL_HAS_ONNX_RUNTIME 0
#endif

class OnnxVisionBackend final : public VisionBackend {
public:
    bool load(const std::string& path) override {
        std::ifstream file(path, std::ios::binary | std::ios::ate);
        if (!file.good() || file.tellg() <= 0) return false;
        m_stop.store(false);
#if JCL_HAS_ONNX_RUNTIME
        // The runtime is deliberately initialized only when the build supplies
        // ONNX Runtime headers and libraries. Model-specific tensor adapters are
        // kept outside this generic backend so an arbitrary ONNX graph is never
        // misinterpreted as a detector.
        m_modelPath = path;
        m_loaded = false;
        return false;
#else
        (void)path;
        m_loaded = false;
        return false;
#endif
    }

    void unload() override {
        m_loaded = false;
        m_modelPath.clear();
    }

    bool isLoaded() const override { return m_loaded; }

    std::vector<VisionDetection> detectObjects(const cv::Mat&) override { return {}; }
    std::vector<VisionDetection> detectFaces(const cv::Mat&) override { return {}; }
    std::string detectText(const cv::Mat&) override { return {}; }
    std::string analyzeScene(const cv::Mat&) override { return {}; }
    void stop() override { m_stop.store(true); }

private:
    bool m_loaded = false;
    std::string m_modelPath;
    std::atomic<bool> m_stop{false};
};

std::unique_ptr<VisionBackend> createOnnxVisionBackend() {
    return std::make_unique<OnnxVisionBackend>();
}

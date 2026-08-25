#include "VisionBackend.h"
#include <fstream>
#include <atomic>

#if __has_include(<onnxruntime_cxx_api.h>)
#include <onnxruntime_cxx_api.h>
#define JCL_HAS_ONNX_RUNTIME 1
#else
#define JCL_HAS_ONNX_RUNTIME 0
#endif

class OnnxVisionBackend final : public VisionBackend {
public:
    bool load(const std::string& path) override {
        m_stop.store(false);
        std::ifstream file(path, std::ios::binary | std::ios::ate);
        if (!file.good() || file.tellg() <= 0) return false;
#if JCL_HAS_ONNX_RUNTIME
        // Runtime/session construction is intentionally isolated here. Model-specific
        // tensor names and post-processing belong to the concrete model adapter.
        m_loaded = false;
        return false;
#else
        return false;
#endif
    }

    void unload() override { m_loaded = false; }
    bool isLoaded() const override { return m_loaded; }
    std::vector<VisionDetection> detectObjects(const cv::Mat&) override { return {}; }
    std::vector<VisionDetection> detectFaces(const cv::Mat&) override { return {}; }
    std::string detectText(const cv::Mat&) override { return {}; }
    std::string analyzeScene(const cv::Mat&) override { return {}; }
    void stop() override { m_stop.store(true); }

private:
    bool m_loaded = false;
    std::atomic<bool> m_stop{false};
};

std::unique_ptr<VisionBackend> createOnnxVisionBackend() {
    return std::make_unique<OnnxVisionBackend>();
}

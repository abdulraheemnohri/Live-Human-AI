#ifndef LIVEHUMANAI_MODELROUTER_H
#define LIVEHUMANAI_MODELROUTER_H

#include "ModelManager.h"
#include "../hardware/HardwareProfiler.h"
#include <string>

namespace livehumanai {
namespace models {

struct ModelSelection {
    std::string modelId;
    std::string backend;
    std::string quantization;
    size_t contextSize;
    int threadCount;
    bool useGPU;
    bool useVulkan;
};

class ModelRouter {
public:
    ModelRouter(hardware::HardwareProfiler& profiler, ModelManager& modelManager);
    ~ModelRouter() = default;

    ModelSelection selectModel(ModelType type, const std::string& taskHint = "");
    ModelSelection selectLLM(const std::string& taskHint = "");
    ModelSelection selectSTT();
    ModelSelection selectTTS();
    ModelSelection selectVision();

private:
    hardware::HardwareProfiler& profiler_;
    ModelManager& modelManager_;
};

} // namespace models
} // namespace livehumanai

#endif

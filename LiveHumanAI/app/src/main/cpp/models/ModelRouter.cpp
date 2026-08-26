#include "ModelRouter.h"
#include "../utils/Logger.h"

namespace livehumanai {
namespace models {

using namespace utils;

ModelRouter::ModelRouter(hardware::HardwareProfiler& profiler, ModelManager& modelManager)
    : profiler_(profiler), modelManager_(modelManager) {}

ModelSelection ModelRouter::selectModel(ModelType type, const std::string& taskHint) {
    ModelSelection selection;

    auto profile = profiler_.getModelProfile();
    selection.threadCount = profiler_.getRecommendedThreads();
    selection.contextSize = profiler_.getRecommendedContextSize();
    selection.useGPU = profiler_.shouldUseGPU();
    selection.useVulkan = profiler_.isVulkanSupported();

    switch (type) {
        case ModelType::LLM:
            return selectLLM(taskHint);
        case ModelType::STT:
            return selectSTT();
        case ModelType::TTS:
            return selectTTS();
        case ModelType::VISION:
            return selectVision();
        default:
            selection.modelId = "default";
            selection.backend = "native";
            return selection;
    }
}

ModelSelection ModelRouter::selectLLM(const std::string& taskHint) {
    ModelSelection selection;
    selection.threadCount = profiler_.getRecommendedThreads();
    selection.useGPU = profiler_.shouldUseGPU();
    selection.useVulkan = profiler_.isVulkanSupported();

    auto profile = profiler_.getModelProfile();

    if (profile == "lite") {
        selection.modelId = "qwen3-0.6b-q4";
        selection.contextSize = 2048;
        selection.quantization = "Q4_K_M";
    } else if (profile == "standard") {
        selection.modelId = "qwen3-1.7b-q4";
        selection.contextSize = 4096;
        selection.quantization = "Q4_K_M";
    } else if (profile == "pro") {
        selection.modelId = "qwen3-4b-q4";
        selection.contextSize = 8192;
        selection.quantization = "Q4_K_M";
    } else {
        selection.modelId = "qwen3-7b-q4";
        selection.contextSize = 16384;
        selection.quantization = "Q4_K_M";
    }

    selection.backend = "llama.cpp";

    LOGI("ModelRouter: Selected LLM %s for profile %s",
         selection.modelId.c_str(), profile.c_str());

    return selection;
}

ModelSelection ModelRouter::selectSTT() {
    ModelSelection selection;
    auto profile = profiler_.getModelProfile();

    if (profile == "lite") {
        selection.modelId = "whisper-tiny";
    } else if (profile == "standard") {
        selection.modelId = "whisper-base";
    } else {
        selection.modelId = "whisper-small";
    }

    selection.backend = "whisper.cpp";
    selection.threadCount = 2;

    return selection;
}

ModelSelection ModelRouter::selectTTS() {
    ModelSelection selection;
    auto profile = profiler_.getModelProfile();

    if (profile == "lite" || profile == "standard") {
        selection.modelId = "piper-tts-small";
    } else {
        selection.modelId = "piper-tts-medium";
    }

    selection.backend = "piper";

    return selection;
}

ModelSelection ModelRouter::selectVision() {
    ModelSelection selection;
    auto profile = profiler_.getModelProfile();

    if (profile == "lite") {
        selection.modelId = "mobilenet-lite";
    } else if (profile == "standard") {
        selection.modelId = "efficientdet-lite";
    } else {
        selection.modelId = "yolo-nano";
    }

    selection.backend = "tflite";
    selection.useGPU = profiler_.shouldUseGPU();

    return selection;
}

} // namespace models
} // namespace livehumanai

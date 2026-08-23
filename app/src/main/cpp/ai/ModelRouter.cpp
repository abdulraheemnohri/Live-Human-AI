#include "ModelRouter.h"

ModelRouter::ModelRouter()
    : m_deviceProfile(DeviceProfile::MID_RANGE) {

    // Initialize LLM models
    m_llmModels.name = "LLM";
    m_llmModels.models = {
        "qwen3-0.6b-q4",
        "qwen3-1.7b-q4",
        "qwen3-4b-q4",
        "qwen3-4b-q5",
        "qwen3-7b-q4",
        "qwen3-8b-q4"
    };
    m_llmModels.defaultModel = "qwen3-1.7b-q4";

    // Initialize STT models
    m_sttModels.name = "STT";
    m_sttModels.models = {
        "whisper-tiny",
        "whisper-base",
        "whisper-small"
    };
    m_sttModels.defaultModel = "whisper-base";

    // Initialize TTS models
    m_ttsModels.name = "TTS";
    m_ttsModels.models = {
        "piper-en",
        "piper-ur",
        "coqui-tts"
    };
    m_ttsModels.defaultModel = "piper-en";

    // Initialize Vision models
    m_visionModels.name = "Vision";
    m_visionModels.models = {
        "yolo-nano",
        "mobilenet-v3",
        "ocr-lightweight"
    };
    m_visionModels.defaultModel = "yolo-nano";
}

ModelRouter::~ModelRouter() {
    shutdown();
}

bool ModelRouter::initialize() {
    // Additional initialization if needed
    return true;
}

void ModelRouter::shutdown() {
    // Cleanup if needed
}

std::string ModelRouter::selectModel(
    const std::string& taskType,
    const std::string& language,
    bool requiresVision,
    bool requiresAudio
) const {
    if (requiresVision) {
        return selectVisionModel();
    } else if (requiresAudio) {
        return selectSTTModel(language);
    }

    // Determine task complexity based on task type
    TaskComplexity complexity = TaskComplexity::BASIC;
    if (taskType == "simple_command") {
        complexity = TaskComplexity::SIMPLE;
    } else if (taskType == "basic_conversation") {
        complexity = TaskComplexity::BASIC;
    } else if (taskType == "complex_reasoning") {
        complexity = TaskComplexity::MODERATE;
    } else if (taskType == "advanced_analysis") {
        complexity = TaskComplexity::COMPLEX;
    }

    return selectLLMModel(complexity);
}

bool ModelRouter::isLLMModel(const std::string& modelName) const {
    for (const auto& model : m_llmModels.models) {
        if (model == modelName) {
            return true;
        }
    }
    return false;
}

bool ModelRouter::isSTTModel(const std::string& modelName) const {
    for (const auto& model : m_sttModels.models) {
        if (model == modelName) {
            return true;
        }
    }
    return false;
}

bool ModelRouter::isTTSModel(const std::string& modelName) const {
    for (const auto& model : m_ttsModels.models) {
        if (model == modelName) {
            return true;
        }
    }
    return false;
}

bool ModelRouter::isVisionModel(const std::string& modelName) const {
    for (const auto& model : m_visionModels.models) {
        if (model == modelName) {
            return true;
        }
    }
    return false;
}

void ModelRouter::setDeviceProfile(DeviceProfile profile) {
    m_deviceProfile = profile;
}

ModelRouter::DeviceProfile ModelRouter::getDeviceProfile() const {
    return m_deviceProfile;
}

const ModelRouter::ModelCategory& ModelRouter::getLLMModels() const {
    return m_llmModels;
}

const ModelRouter::ModelCategory& ModelRouter::getSTTModels() const {
    return m_sttModels;
}

const ModelRouter::ModelCategory& ModelRouter::getTTSModels() const {
    return m_ttsModels;
}

const ModelRouter::ModelCategory& ModelRouter::getVisionModels() const {
    return m_visionModels;
}

std::string ModelRouter::selectLLMModel(TaskComplexity complexity) const {
    switch (m_deviceProfile) {
        case DeviceProfile::LOW_END:
            // For low-end devices, use the smallest model
            if (complexity == TaskComplexity::SIMPLE) {
                return ""; // No LLM needed for simple commands
            }
            return "qwen3-0.6b-q4";

        case DeviceProfile::MID_RANGE:
            // For mid-range devices
            switch (complexity) {
                case TaskComplexity::SIMPLE:
                    return ""; // No LLM needed
                case TaskComplexity::BASIC:
                    return "qwen3-0.6b-q4";
                case TaskComplexity::MODERATE:
                case TaskComplexity::COMPLEX:
                    return "qwen3-1.7b-q4";
                default:
                    return m_llmModels.defaultModel;
            }

        case DeviceProfile::HIGH_END:
            // For high-end devices (8GB-16GB RAM)
            switch (complexity) {
                case TaskComplexity::SIMPLE:
                    return "";
                case TaskComplexity::BASIC:
                    return "qwen3-1.7b-q4";
                case TaskComplexity::MODERATE:
                    return "qwen3-4b-q4";
                case TaskComplexity::COMPLEX:
                    return "qwen3-4b-q5";
                default:
                    return "qwen3-4b-q4";
            }

        case DeviceProfile::FLAGSHIP:
            // For flagship devices (>16GB RAM)
            switch (complexity) {
                case TaskComplexity::SIMPLE:
                    return "";
                case TaskComplexity::BASIC:
                    return "qwen3-4b-q4";
                case TaskComplexity::MODERATE:
                    return "qwen3-4b-q5";
                case TaskComplexity::COMPLEX:
                    return "qwen3-7b-q4";
                default:
                    return "qwen3-8b-q4";
            }

        default:
            return m_llmModels.defaultModel;
    }
}

std::string ModelRouter::selectSTTModel(const std::string& language) const {
    // For now, just return the default STT model
    // In a real implementation, you might select based on language support
    return m_sttModels.defaultModel;
}

std::string ModelRouter::selectTTSModel(const std::string& language) const {
    // Select TTS model based on language
    if (language == "ur" || language == "ur-PK") {
        return "piper-ur";
    }
    return m_ttsModels.defaultModel;
}

std::string ModelRouter::selectVisionModel() const {
    switch (m_deviceProfile) {
        case DeviceProfile::LOW_END:
            return "yolo-nano";
        case DeviceProfile::MID_RANGE:
            return "yolo-nano";
        case DeviceProfile::HIGH_END:
        case DeviceProfile::FLAGSHIP:
            return "mobilenet-v3";
        default:
            return m_visionModels.defaultModel;
    }
}

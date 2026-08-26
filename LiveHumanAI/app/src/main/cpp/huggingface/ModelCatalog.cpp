#include "ModelCatalog.h"
#include "../utils/Logger.h"

namespace livehumanai {
namespace huggingface {

using namespace utils;

std::vector<ModelEntry> ModelCatalog::getAvailableModels() {
    std::vector<ModelEntry> models;

    // LLM Models
    models.push_back({"qwen3-0.6b-q4", "Qwen3 0.6B Q4", "Qwen/Qwen3-0.6B", "main", "llm", "gguf", "Q4_K_M", 500000000, 4.0f, 6.0f, "lite", false});
    models.push_back({"qwen3-1.7b-q4", "Qwen3 1.7B Q4", "Qwen/Qwen3-1.7B", "main", "llm", "gguf", "Q4_K_M", 1200000000, 6.0f, 8.0f, "standard", false});
    models.push_back({"qwen3-4b-q4", "Qwen3 4B Q4", "Qwen/Qwen3-4B", "main", "llm", "gguf", "Q4_K_M", 2800000000, 8.0f, 12.0f, "pro", false});

    // STT Models
    models.push_back({"whisper-tiny", "Whisper Tiny", "ggerganov/whisper.cpp", "main", "stt", "bin", "", 75000000, 2.0f, 4.0f, "lite", false});
    models.push_back({"whisper-base", "Whisper Base", "ggerganov/whisper.cpp", "main", "stt", "bin", "", 140000000, 4.0f, 6.0f, "standard", false});

    return models;
}

ModelEntry ModelCatalog::getModelById(const std::string& id) {
    auto models = getAvailableModels();
    for (const auto& model : models) {
        if (model.id == id) {
            return model;
        }
    }
    return ModelEntry{};
}

} // namespace huggingface
} // namespace livehumanai

#ifndef LIVEHUMANAI_MODELCATALOG_H
#define LIVEHUMANAI_MODELCATALOG_H

#include <string>
#include <vector>

namespace livehumanai {
namespace huggingface {

struct ModelEntry {
    std::string id;
    std::string name;
    std::string repository;
    std::string revision;
    std::string type;  // llm, stt, tts, vision
    std::string format;
    std::string quantization;
    size_t sizeBytes;
    float minimumRamGb;
    float recommendedRamGb;
    std::string profile;  // lite, standard, pro, ultra
    bool verified;
};

class ModelCatalog {
public:
    static std::vector<ModelEntry> getAvailableModels();
    static ModelEntry getModelById(const std::string& id);
};

} // namespace huggingface
} // namespace livehumanai

#endif

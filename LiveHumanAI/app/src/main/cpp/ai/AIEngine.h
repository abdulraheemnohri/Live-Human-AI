#ifndef LIVEHUMANAI_AIENGINE_H
#define LIVEHUMANAI_AIENGINE_H

#include <string>
#include <functional>

namespace livehumanai {
namespace ai {

class AIEngine {
public:
    AIEngine() = default;
    ~AIEngine() = default;
    
    bool initialize();
    void shutdown();
    
    // Model management
    bool loadModel(const std::string& modelId);
    void unloadModel();
    
    // Generation
    std::string generate(const std::string& prompt);
    
private:
    bool initialized_ = false;
};

} // namespace ai
} // namespace livehumanai

#endif

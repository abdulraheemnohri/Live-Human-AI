#ifndef LIVEHUMANAI_JNI_H
#define LIVEHUMANAI_JNI_H

#include <jni.h>
#include <string>

namespace livehumanai {
namespace jni {

class LiveHumanAINative {
public:
    LiveHumanAINative();
    ~LiveHumanAINative();

    bool initialize(void* androidContext);
    void shutdown();

    bool isInitialized() const;

    // Hardware info
    std::string getDeviceProfile();
    std::string getModelRecommendation();

    // Model management
    bool downloadModel(const std::string& modelId);
    bool installModel(const std::string& modelId, const std::string& path);
    bool uninstallModel(const std::string& modelId);
    bool loadModel(const std::string& modelId);
    bool unloadModel(const std::string& modelId);

    // AI inference
    std::string generateResponse(const std::string& prompt);

    // Jalebi loop
    int startJalebiLoop(const std::string& goal);
    void cancelJalebiLoop(int loopId);

    // Diagnostics
    std::string runDiagnostics();

private:
    bool initialized_;
};

} // namespace jni
} // namespace livehumanai

#endif

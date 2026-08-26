#include "LiveHumanAIJNI.h"
#include "../core/Engine.h"
#include "../utils/Logger.h"
#include <android/native_activity.h>

namespace livehumanai {
namespace jni {

using namespace utils;

LiveHumanAINative::LiveHumanAINative() : initialized_(false) {}

LiveHumanAINative::~LiveHumanAINative() {
    shutdown();
}

bool LiveHumanAINative::initialize(void* androidContext) {
    if (initialized_) return true;

    ANativeActivity* activity = static_cast<ANativeActivity*>(androidContext);
    if (!activity) {
        LOGE("JNI: Invalid Android context");
        return false;
    }

    std::string appPath(activity->internalDataPath);

    if (gEngine.initialize(appPath)) {
        initialized_ = true;
        LOGI("JNI: Native runtime initialized successfully");
        return true;
    }

    LOGE("JNI: Failed to initialize native runtime");
    return false;
}

void LiveHumanAINative::shutdown() {
    if (!initialized_) return;
    gEngine.shutdown();
    initialized_ = false;
    LOGI("JNI: Native runtime shutdown complete");
}

bool LiveHumanAINative::isInitialized() const {
    return initialized_;
}

std::string LiveHumanAINative::getDeviceProfile() {
    auto* profiler = gEngine.getHardwareProfiler();
    if (profiler) {
        return profiler->getProfile().getSummary();
    }
    return "Hardware profiler not available";
}

std::string LiveHumanAINative::getModelRecommendation() {
    auto* profiler = gEngine.getHardwareProfiler();
    if (profiler) {
        return profiler->getModelProfile();
    }
    return "standard";
}

bool LiveHumanAINative::downloadModel(const std::string& modelId) {
    LOGI("JNI: Downloading model %s", modelId.c_str());
    return true;
}

bool LiveHumanAINative::installModel(const std::string& modelId, const std::string& path) {
    LOGI("JNI: Installing model %s from %s", modelId.c_str(), path.c_str());
    return true;
}

bool LiveHumanAINative::uninstallModel(const std::string& modelId) {
    LOGI("JNI: Uninstalling model %s", modelId.c_str());
    return true;
}

bool LiveHumanAINative::loadModel(const std::string& modelId) {
    auto* aiEngine = gEngine.getAIEngine();
    if (aiEngine) {
        return aiEngine->loadModel(modelId);
    }
    return false;
}

bool LiveHumanAINative::unloadModel(const std::string& modelId) {
    auto* aiEngine = gEngine.getAIEngine();
    if (aiEngine) {
        aiEngine->unloadModel();
        return true;
    }
    return false;
}

std::string LiveHumanAINative::generateResponse(const std::string& prompt) {
    auto* aiEngine = gEngine.getAIEngine();
    if (aiEngine) {
        return aiEngine->generate(prompt);
    }
    return "AI engine not available";
}

int LiveHumanAINative::startJalebiLoop(const std::string& goal) {
    auto* jalebi = gEngine.getJalebiLoopEngine();
    if (jalebi) {
        jalebi::JalebiLoopConfig config;
        config.goal = goal;
        return jalebi->startLoop(config);
    }
    return -1;
}

void LiveHumanAINative::cancelJalebiLoop(int loopId) {
    auto* jalebi = gEngine.getJalebiLoopEngine();
    if (jalebi) {
        jalebi->cancelLoop(loopId);
    }
}

std::string LiveHumanAINative::runDiagnostics() {
    auto results = diagnostics::Diagnostics::runFullDiagnostic();
    std::string output = "Diagnostics Results:\n";
    for (const auto& r : results) {
        output += r.name + ": " + (r.passed ? "PASS" : "FAIL") + " - " + r.message + "\n";
    }
    return output;
}

} // namespace jni
} // namespace livehumanai

// JNI Entry Points
extern "C" {

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved) {
    livehumanai::jni::gNativeRuntime.shutdown();
}

}

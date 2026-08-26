#include "Engine.h"
#include "ThreadPool.h"
#include "EventBus.h"
#include "../utils/Logger.h"
#include "../hardware/HardwareProfiler.h"
#include "../hardware/ThermalManager.h"
#include "../hardware/BatteryManager.h"
#include "../models/ModelManager.h"
#include "../models/ModelRouter.h"
#include "../ai/AIEngine.h"
#include "../jalebi/JalebiLoopEngine.h"

namespace livehumanai {

using namespace utils;

// Global engine instance
Engine gEngine;

Engine::Engine() : initialized_(false) {}

Engine::~Engine() {
    if (initialized_) {
        shutdown();
    }
}

std::string Engine::getVersion() {
    return "1.0.0";
}

bool Engine::initialize(const std::string& appContext) {
    if (initialized_) {
        LOGW("Engine: Already initialized");
        return true;
    }

    LOGI("Engine: Initializing Live Human AI Native Runtime v%s", getVersion().c_str());
    LOGI("Engine: App context path: %s", appContext.c_str());

    appContextPath_ = appContext;

    // Initialize global logger
    gLogger.setTag("LiveHumanAI_Native");
    gLogger.setMinLevel(Logger::Level::DEBUG);

    try {
        if (!initializeSubsystems()) {
            LOGE("Engine: Failed to initialize subsystems");
            cleanupSubsystems();
            return false;
        }

        // Publish initialization event
        core::EventBus::getInstance().publish(
            core::EventType::INITIALIZED,
            "Engine",
            "Native runtime initialized successfully"
        );

        initialized_ = true;
        LOGI("Engine: Initialization complete");
        return true;

    } catch (const std::exception& e) {
        LOGE("Engine: Exception during initialization: %s", e.what());
        cleanupSubsystems();
        return false;
    } catch (...) {
        LOGE("Engine: Unknown exception during initialization");
        cleanupSubsystems();
        return false;
    }
}

void Engine::shutdown() {
    if (!initialized_) {
        return;
    }

    LOGI("Engine: Shutting down...");

    // Publish shutdown event
    core::EventBus::getInstance().publish(
        core::EventType::SHUTDOWN,
        "Engine",
        "Native runtime shutting down"
    );

    cleanupSubsystems();
    initialized_ = false;

    LOGI("Engine: Shutdown complete");
}

bool Engine::initializeSubsystems() {
    LOGI("Engine: Initializing subsystems...");

    // Thread Pool (core infrastructure)
    threadPool_ = std::make_unique<core::ThreadPool>();
    if (!threadPool_) {
        LOGE("Engine: Failed to create ThreadPool");
        return false;
    }
    LOGI("Engine: ThreadPool created with %zu threads", threadPool_->size());

    // Hardware Profiler
    hardwareProfiler_ = std::make_unique<hardware::HardwareProfiler>();
    if (!hardwareProfiler_->initialize()) {
        LOGE("Engine: Failed to initialize HardwareProfiler");
        return false;
    }
    LOGI("Engine: HardwareProfiler initialized");

    // Thermal Manager
    thermalManager_ = std::make_unique<hardware::ThermalManager>();
    LOGI("Engine: ThermalManager initialized");

    // Battery Manager
    batteryManager_ = std::make_unique<hardware::BatteryManager>();
    LOGI("Engine: BatteryManager initialized");

    // Model Manager
    modelManager_ = std::make_unique<models::ModelManager>(appContextPath_);
    if (!modelManager_->initialize()) {
        LOGE("Engine: Failed to initialize ModelManager");
        return false;
    }
    LOGI("Engine: ModelManager initialized");

    // Model Router
    modelRouter_ = std::make_unique<models::ModelRouter>(*hardwareProfiler_, *modelManager_);
    LOGI("Engine: ModelRouter initialized");

    // AI Engine
    aiEngine_ = std::make_unique<ai::AIEngine>();
    if (!aiEngine_->initialize()) {
        LOGE("Engine: Failed to initialize AIEngine");
        return false;
    }
    LOGI("Engine: AIEngine initialized");

    // Jalebi Loop Engine
    jalebiLoopEngine_ = std::make_unique<jalebi::JalebiLoopEngine>();
    if (!jalebiLoopEngine_->initialize()) {
        LOGE("Engine: Failed to initialize JalebiLoopEngine");
        return false;
    }
    LOGI("Engine: JalebiLoopEngine initialized");

    LOGI("Engine: All subsystems initialized successfully");
    return true;
}

void Engine::cleanupSubsystems() {
    LOGI("Engine: Cleaning up subsystems...");

    // Shutdown in reverse order of initialization

    if (jalebiLoopEngine_) {
        jalebiLoopEngine_->shutdown();
        jalebiLoopEngine_.reset();
    }

    if (aiEngine_) {
        aiEngine_->shutdown();
        aiEngine_.reset();
    }

    if (modelRouter_) {
        modelRouter_.reset();
    }

    if (modelManager_) {
        modelManager_->shutdown();
        modelManager_.reset();
    }

    if (batteryManager_) {
        batteryManager_.reset();
    }

    if (thermalManager_) {
        thermalManager_.reset();
    }

    if (hardwareProfiler_) {
        hardwareProfiler_->shutdown();
        hardwareProfiler_.reset();
    }

    if (threadPool_) {
        threadPool_->stop(true);
        threadPool_.reset();
    }

    // Clear event bus
    core::EventBus::getInstance().clear();

    LOGI("Engine: Cleanup complete");
}

core::ThreadPool* Engine::getThreadPool() {
    return threadPool_.get();
}

hardware::HardwareProfiler* Engine::getHardwareProfiler() {
    return hardwareProfiler_.get();
}

hardware::ThermalManager* Engine::getThermalManager() {
    return thermalManager_.get();
}

hardware::BatteryManager* Engine::getBatteryManager() {
    return batteryManager_.get();
}

models::ModelManager* Engine::getModelManager() {
    return modelManager_.get();
}

models::ModelRouter* Engine::getModelRouter() {
    return modelRouter_.get();
}

ai::AIEngine* Engine::getAIEngine() {
    return aiEngine_.get();
}

jalebi::JalebiLoopEngine* Engine::getJalebiLoopEngine() {
    return jalebiLoopEngine_.get();
}

} // namespace livehumanai

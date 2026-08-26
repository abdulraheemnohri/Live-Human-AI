#ifndef LIVEHUMANAI_ENGINE_H
#define LIVEHUMANAI_ENGINE_H

#include <string>
#include <memory>
#include <atomic>
#include <functional>

namespace livehumanai {
namespace core {

class ThreadPool;

} // namespace core

namespace hardware {

class HardwareProfiler;
class ThermalManager;
class BatteryManager;

} // namespace hardware

namespace models {

class ModelManager;
class ModelRouter;

} // namespace models

namespace ai {

class AIEngine;

} // namespace ai

namespace jalebi {

class JalebiLoopEngine;

} // namespace jalebi

/**
 * Main Engine class - coordinates all subsystems
 * This is the primary entry point for the native runtime
 */
class Engine {
public:
    Engine();
    ~Engine();

    // Prevent copying
    Engine(const Engine&) = delete;
    Engine& operator=(const Engine&) = delete;

    /**
     * Initialize the engine with paths and configuration
     * @param appContext Android application context path
     * @return true if initialization succeeded
     */
    bool initialize(const std::string& appContext);

    /**
     * Shutdown the engine and release all resources
     */
    void shutdown();

    /**
     * Check if engine is initialized
     */
    bool isInitialized() const { return initialized_; }

    /**
     * Get version string
     */
    static std::string getVersion();

    // Subsystem accessors
    core::ThreadPool* getThreadPool();
    hardware::HardwareProfiler* getHardwareProfiler();
    hardware::ThermalManager* getThermalManager();
    hardware::BatteryManager* getBatteryManager();
    models::ModelManager* getModelManager();
    models::ModelRouter* getModelRouter();
    ai::AIEngine* getAIEngine();
    jalebi::JalebiLoopEngine* getJalebiLoopEngine();

private:
    bool initializeSubsystems();
    void cleanupSubsystems();

    std::atomic<bool> initialized_;
    std::string appContextPath_;

    // Subsystem pointers (initialized on demand)
    std::unique_ptr<core::ThreadPool> threadPool_;
    std::unique_ptr<hardware::HardwareProfiler> hardwareProfiler_;
    std::unique_ptr<hardware::ThermalManager> thermalManager_;
    std::unique_ptr<hardware::BatteryManager> batteryManager_;
    std::unique_ptr<models::ModelManager> modelManager_;
    std::unique_ptr<models::ModelRouter> modelRouter_;
    std::unique_ptr<ai::AIEngine> aiEngine_;
    std::unique_ptr<jalebi::JalebiLoopEngine> jalebiLoopEngine_;
};

// Global engine instance
extern Engine gEngine;

} // namespace livehumanai

#endif // LIVEHUMANAI_ENGINE_H

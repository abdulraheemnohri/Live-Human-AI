#ifndef LIVE_HUMAN_AI_NATIVE_CORE_H
#define LIVE_HUMAN_AI_NATIVE_CORE_H

#include <string>
#include <vector>
#include <memory>
#include <mutex>

// Forward declarations
class NativeEngine;
class AIEngine;
class HardwareProfiler;
class ThermalMonitor;
class BatteryMonitor;
class TaskScheduler;
class Logger;

// NativeCore is the main entry point for the native runtime
class NativeCore {
public:
    NativeCore();
    ~NativeCore();

    // Initialization and shutdown
    bool initialize();
    void shutdown();

    // Engine access
    NativeEngine* getEngine() const;
    AIEngine* getAIEngine() const;
    HardwareProfiler* getHardwareProfiler() const;
    ThermalMonitor* getThermalMonitor() const;
    BatteryMonitor* getBatteryMonitor() const;
    TaskScheduler* getTaskScheduler() const;
    Logger* getLogger() const;

    // Version information
    std::string getVersion() const;
    std::string getRuntimeStatus() const;

    // Device profile
    std::string getDeviceProfile() const;

    // Memory status
    size_t getTotalRAM() const;
    size_t getAvailableRAM() const;
    float getRAMUsagePercentage() const;

    // Performance mode
    enum class PerformanceMode {
        BATTERY_SAVER,
        BALANCED,
        PERFORMANCE,
        MAXIMUM
    };

    void setPerformanceMode(PerformanceMode mode);
    PerformanceMode getPerformanceMode() const;

    // Thread safety
    std::mutex& getMutex();

private:
    std::unique_ptr<NativeEngine> m_engine;
    std::unique_ptr<AIEngine> m_aiEngine;
    std::unique_ptr<HardwareProfiler> m_hardwareProfiler;
    std::unique_ptr<ThermalMonitor> m_thermalMonitor;
    std::unique_ptr<BatteryMonitor> m_batteryMonitor;
    std::unique_ptr<TaskScheduler> m_taskScheduler;
    std::unique_ptr<Logger> m_logger;

    PerformanceMode m_performanceMode;
    mutable std::mutex m_mutex;
};

#endif // LIVE_HUMAN_AI_NATIVE_CORE_H

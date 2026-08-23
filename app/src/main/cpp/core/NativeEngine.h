#ifndef LIVE_HUMAN_AI_NATIVE_ENGINE_H
#define LIVE_HUMAN_AI_NATIVE_ENGINE_H

#include <string>
#include <vector>
#include <memory>

// Forward declarations
class AIEngine;
class HardwareProfiler;
class ThermalMonitor;
class BatteryMonitor;

// NativeEngine handles the core native operations
class NativeEngine {
public:
    NativeEngine();
    ~NativeEngine();

    // Initialization and shutdown
    bool initialize();
    void shutdown();

    // Runtime status
    std::string getRuntimeStatus() const;

    // Device profile
    std::string getDeviceProfile() const;

    // Memory management
    size_t getTotalRAM() const;
    size_t getAvailableRAM() const;
    float getRAMUsagePercentage() const;

    // Performance metrics
    float getCPUUsage() const;
    float getGPUUsage() const;
    float getTemperature() const;
    float getBatteryLevel() const;

    // Engine statistics
    struct EngineStats {
        size_t totalRAM;
        size_t availableRAM;
        float cpuUsage;
        float gpuUsage;
        float temperature;
        float batteryLevel;
    };

    EngineStats getEngineStats() const;

private:
    std::unique_ptr<HardwareProfiler> m_hardwareProfiler;
    std::unique_ptr<ThermalMonitor> m_thermalMonitor;
    std::unique_ptr<BatteryMonitor> m_batteryMonitor;
};

#endif // LIVE_HUMAN_AI_NATIVE_ENGINE_H

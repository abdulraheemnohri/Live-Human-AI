#ifndef LIVE_HUMAN_AI_HARDWARE_PROFILER_H
#define LIVE_HUMAN_AI_HARDWARE_PROFILER_H

#include <string>
#include <cstdint>

// HardwareProfiler monitors device hardware capabilities
class HardwareProfiler {
public:
    HardwareProfiler();
    ~HardwareProfiler();

    // Initialization and shutdown
    bool initialize();
    void shutdown();

    // RAM information
    size_t getTotalRAM() const;
    size_t getAvailableRAM() const;
    float getRAMUsagePercentage() const;

    // CPU information
    int getCPUCoreCount() const;
    std::string getCPUModel() const;
    float getCPUUsage() const;
    float getCPUFrequency() const;

    // GPU information
    std::string getGPUModel() const;
    float getGPUUsage() const;

    // Storage information
    size_t getTotalStorage() const;
    size_t getAvailableStorage() const;

    // Hardware acceleration support
    bool supportsNEON() const;
    bool supportsVulkan() const;
    bool supportsOpenGLES() const;

    // Device profile detection
    enum class DeviceProfile {
        LOW_END,      // < 4GB RAM
        MID_RANGE,     // 4GB - 8GB RAM
        HIGH_END,     // 8GB - 16GB RAM
        FLAGSHIP      // > 16GB RAM
    };

    DeviceProfile getDeviceProfile() const;

private:
    // Internal state
    size_t m_totalRAM;
    size_t m_availableRAM;
    int m_cpuCoreCount;
    std::string m_cpuModel;
    std::string m_gpuModel;
    bool m_supportsNEON;
    bool m_supportsVulkan;
    bool m_supportsOpenGLES;
};

#endif // LIVE_HUMAN_AI_HARDWARE_PROFILER_H

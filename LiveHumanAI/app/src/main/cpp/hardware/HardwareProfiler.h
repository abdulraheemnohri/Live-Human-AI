#ifndef LIVEHUMANAI_HARDWAREPROFILER_H
#define LIVEHUMANAI_HARDWAREPROFILER_H

#include "DeviceProfile.h"
#include <string>
#include <functional>

namespace livehumanai {
namespace hardware {

/**
 * Hardware Profiler - Detects and monitors device capabilities
 */
class HardwareProfiler {
public:
    HardwareProfiler();
    ~HardwareProfiler();

    /**
     * Initialize the profiler and detect hardware
     * @return true if initialization succeeded
     */
    bool initialize();

    /**
     * Shutdown and release resources
     */
    void shutdown();

    /**
     * Get current device profile
     */
    const DeviceProfile& getProfile() const { return profile_; }

    /**
     * Refresh hardware information (RAM, thermal, battery)
     */
    void refresh();

    /**
     * Get RAM profile recommendation
     */
    RamProfile getRamProfile() const { return profile_.ramProfile; }

    /**
     * Get recommended model profile name
     */
    std::string getModelProfile() const { return profile_.recommendedModelProfile; }

    /**
     * Check if Vulkan is supported
     */
    bool isVulkanSupported() const { return profile_.vulkanSupported; }

    /**
     * Check if NEON is available
     */
    bool hasNeon() const { return profile_.hasNeon; }

    /**
     * Get recommended thread count
     */
    int getRecommendedThreads() const { return profile_.recommendedThreads; }

    /**
     * Get recommended context size
     */
    size_t getRecommendedContextSize() const { return profile_.recommendedContextSize; }

    /**
     * Check if device should use GPU acceleration
     */
    bool shouldUseGPU() const { return profile_.shouldUseGPU; }

    /**
     * Check if device is ready for heavy computation
     */
    bool isReadyForHeavyComputation() const { return profile_.isReadyForHeavyComputation(); }

    /**
     * Register callback for thermal state changes
     */
    using ThermalCallback = std::function<void(ThermalState)>;
    void registerThermalCallback(ThermalCallback callback);

    /**
     * Register callback for battery state changes
     */
    using BatteryCallback = std::function<void(BatteryState, int)>;
    void registerBatteryCallback(BatteryCallback callback);

private:
    void detectCPUInfo();
    void detectMemoryInfo();
    void detectGPUInfo();
    void detectStorageInfo();
    void updateThermalState();
    void updateBatteryState();
    void computeRecommendations();
    void classifyRamProfile();

    DeviceProfile profile_;
    ThermalCallback thermalCallback_;
    BatteryCallback batteryCallback_;
    bool initialized_;
};

} // namespace hardware
} // namespace livehumanai

#endif // LIVEHUMANAI_HARDWAREPROFILER_H

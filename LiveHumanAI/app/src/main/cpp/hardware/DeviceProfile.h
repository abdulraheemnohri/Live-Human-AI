#ifndef LIVEHUMANAI_DEVICEPROFILE_H
#define LIVEHUMANAI_DEVICEPROFILE_H

#include <string>
#include <vector>
#include <cstdint>

namespace livehumanai {
namespace hardware {

/**
 * Device RAM profile categories
 */
enum class RamProfile {
    LOW,        // < 4 GB
    STANDARD,   // 4-6 GB
    HIGH,       // 8-12 GB
    PREMIUM     // 16+ GB
};

/**
 * Thermal state enumeration
 */
enum class ThermalState {
    NORMAL,
    WARM,
    HOT,
    CRITICAL
};

/**
 * Battery state enumeration
 */
enum class BatteryState {
    UNKNOWN,
    CHARGING,
    DISCHARGING,
    FULL,
    NOT_CHARGING
};

/**
 * Complete device profile information
 */
struct DeviceProfile {
    // Hardware Info
    std::string manufacturer;
    std::string model;
    std::string board;
    std::string abi;
    std::string androidVersion;
    int sdkVersion;

    // CPU Info
    int cpuCores;
    std::string cpuArchitecture;
    bool hasNeon;
    bool hasFp16;

    // GPU Info
    std::string gpuName;
    bool vulkanSupported;
    bool openGLSupported;
    int vulkanVersion;

    // Memory Info
    uint64_t totalRamBytes;
    uint64_t availableRamBytes;
    double totalRamGB() const { return totalRamBytes / (1024.0 * 1024.0 * 1024.0); }
    double availableRamGB() const { return availableRamBytes / (1024.0 * 1024.0 * 1024.0); }
    RamProfile ramProfile;

    // Storage Info
    uint64_t totalStorageBytes;
    uint64_t availableStorageBytes;
    double totalStorageGB() const { return totalStorageBytes / (1024.0 * 1024.0 * 1024.0); }
    double availableStorageGB() const { return availableStorageBytes / (1024.0 * 1024.0 * 1024.0); }

    // Thermal State
    ThermalState thermalState;
    float temperatureCelsius;

    // Battery State
    BatteryState batteryState;
    int batteryLevel;  // 0-100
    bool batterySaverMode;

    // Capabilities
    bool hasCamera;
    bool hasMicrophone;
    bool hasBluetooth;
    bool hasUSBHost;
    bool hasBiometric;

    // Computed recommendations
    int recommendedThreads;
    size_t recommendedContextSize;
    std::string recommendedModelProfile;
    bool shouldUseGPU;
    bool shouldUseVulkan;

    DeviceProfile();

    /**
     * Get human-readable summary
     */
    std::string getSummary() const;

    /**
     * Check if device meets minimum requirements for a profile
     */
    bool canRunProfile(const std::string& profile) const;

    /**
     * Check if device is in good state for heavy computation
     */
    bool isReadyForHeavyComputation() const;
};

} // namespace hardware
} // namespace livehumanai

#endif // LIVEHUMANAI_DEVICEPROFILE_H

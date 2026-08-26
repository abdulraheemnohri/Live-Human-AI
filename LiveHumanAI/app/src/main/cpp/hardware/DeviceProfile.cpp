#include "DeviceProfile.h"
#include "../utils/Logger.h"
#include <sstream>
#include <sys/sysinfo.h>

namespace livehumanai {
namespace hardware {

using namespace utils;

DeviceProfile::DeviceProfile()
    : sdkVersion(0)
    , cpuCores(0)
    , hasNeon(false)
    , hasFp16(false)
    , vulkanSupported(false)
    , openGLSupported(false)
    , vulkanVersion(0)
    , totalRamBytes(0)
    , availableRamBytes(0)
    , totalStorageBytes(0)
    , availableStorageBytes(0)
    , thermalState(ThermalState::NORMAL)
    , temperatureCelsius(0.0f)
    , batteryState(BatteryState::UNKNOWN)
    , batteryLevel(0)
    , batterySaverMode(false)
    , hasCamera(false)
    , hasMicrophone(false)
    , hasBluetooth(false)
    , hasUSBHost(false)
    , hasBiometric(false)
    , recommendedThreads(4)
    , recommendedContextSize(4096)
    , shouldUseGPU(false)
    , shouldUseVulkan(false)
{
    ramProfile = RamProfile::STANDARD;
}

std::string DeviceProfile::getSummary() const {
    std::ostringstream ss;
    ss << "Device: " << manufacturer << " " << model << "\n";
    ss << "RAM: " << totalRamGB() << " GB (Available: " << availableRamGB() << " GB)\n";
    ss << "Storage: " << availableStorageGB() << " GB free\n";
    ss << "CPU: " << cpuCores << " cores, " << cpuArchitecture << "\n";
    ss << "GPU: " << gpuName << (vulkanSupported ? " (Vulkan)" : "") << "\n";
    ss << "Thermal: ";
    switch (thermalState) {
        case ThermalState::NORMAL: ss << "Normal"; break;
        case ThermalState::WARM: ss << "Warm"; break;
        case ThermalState::HOT: ss << "Hot"; break;
        case ThermalState::CRITICAL: ss << "Critical"; break;
    }
    ss << " (" << temperatureCelsius << "°C)\n";
    ss << "Battery: " << batteryLevel << "%";
    if (batteryState == BatteryState::CHARGING) ss << " (Charging)";
    ss << "\n";
    ss << "Profile: " << recommendedModelProfile;
    return ss.str();
}

bool DeviceProfile::canRunProfile(const std::string& profile) const {
    double ramGB = availableRamGB();
    
    if (profile == "lite") {
        return ramGB >= 3.0;  // Minimum for lite profile
    } else if (profile == "standard") {
        return ramGB >= 5.0;  // Minimum for standard
    } else if (profile == "pro") {
        return ramGB >= 8.0;  // Minimum for pro
    } else if (profile == "ultra") {
        return ramGB >= 14.0; // Minimum for ultra
    }
    
    return false;
}

bool DeviceProfile::isReadyForHeavyComputation() const {
    // Check thermal state
    if (thermalState == ThermalState::HOT || thermalState == ThermalState::CRITICAL) {
        return false;
    }
    
    // Check battery
    if (batterySaverMode || batteryLevel < 20) {
        return false;
    }
    
    // Check available RAM
    if (availableRamGB() < 4.0) {
        return false;
    }
    
    // Check storage
    if (availableStorageGB() < 2.0) {
        return false;
    }
    
    return true;
}

} // namespace hardware
} // namespace livehumanai

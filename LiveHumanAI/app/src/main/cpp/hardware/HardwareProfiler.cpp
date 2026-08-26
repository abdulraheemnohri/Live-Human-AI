#include "HardwareProfiler.h"
#include "../utils/Logger.h"
#include <sys/sysinfo.h>
#include <fstream>
#include <sstream>
#include <cstring>

namespace livehumanai {
namespace hardware {

using namespace utils;

HardwareProfiler::HardwareProfiler() : initialized_(false) {}

HardwareProfiler::~HardwareProfiler() {
    shutdown();
}

bool HardwareProfiler::initialize() {
    if (initialized_) {
        return true;
    }

    LOGI("HardwareProfiler: Starting hardware detection...");

    // Detect all hardware components
    detectCPUInfo();
    detectMemoryInfo();
    detectGPUInfo();
    detectStorageInfo();
    updateThermalState();
    updateBatteryState();
    computeRecommendations();
    classifyRamProfile();

    initialized_ = true;

    LOGI("HardwareProfiler: Detection complete");
    LOGI("HardwareProfiler: %s", profile_.getSummary().c_str());

    return true;
}

void HardwareProfiler::shutdown() {
    initialized_ = false;
    LOGI("HardwareProfiler: Shutdown complete");
}

void HardwareProfiler::refresh() {
    if (!initialized_) return;

    detectMemoryInfo();
    updateThermalState();
    updateBatteryState();
    computeRecommendations();
    classifyRamProfile();
}

void HardwareProfiler::detectCPUInfo() {
    // Get CPU cores
    profile_.cpuCores = sysconf(_SC_NPROCESSORS_CONF);
    if (profile_.cpuCores <= 0) {
        profile_.cpuCores = 4; // Default fallback
    }

    // Detect architecture from preprocessor
#if defined(__aarch64__) || defined(_M_ARM64)
    profile_.cpuArchitecture = "arm64-v8a";
    profile_.hasNeon = true;
    profile_.hasFp16 = true;
#elif defined(__arm__)
    profile_.cpuArchitecture = "armeabi-v7a";
    profile_.hasNeon = true;
    profile_.hasFp16 = false;
#elif defined(__x86_64__)
    profile_.cpuArchitecture = "x86_64";
    profile_.hasNeon = false;
    profile_.hasFp16 = false;
#else
    profile_.cpuArchitecture = "unknown";
    profile_.hasNeon = false;
    profile_.hasFp16 = false;
#endif

    // Read CPU info from /proc/cpuinfo
    std::ifstream cpuinfo("/proc/cpuinfo");
    if (cpuinfo.is_open()) {
        std::string line;
        while (std::getline(cpuinfo, line)) {
            if (line.find("Hardware") != std::string::npos) {
                size_t pos = line.find(':');
                if (pos != std::string::npos) {
                    profile_.model = line.substr(pos + 1);
                    // Trim whitespace
                    size_t start = profile_.model.find_first_not_of(" \t");
                    if (start != std::string::npos) {
                        profile_.model = profile_.model.substr(start);
                    }
                }
            }
        }
    }

    profile_.recommendedThreads = profile_.cpuCores;

    LOGD("HardwareProfiler: CPU - %d cores, %s, NEON=%d", 
         profile_.cpuCores, profile_.cpuArchitecture.c_str(), profile_.hasNeon);
}

void HardwareProfiler::detectMemoryInfo() {
    struct sysinfo info;
    if (sysinfo(&info) == 0) {
        profile_.totalRamBytes = static_cast<uint64_t>(info.totalram) * info.mem_unit;
        profile_.availableRamBytes = static_cast<uint64_t>(info.freeram) * info.mem_unit;
    } else {
        // Fallback values
        profile_.totalRamBytes = 6ULL * 1024 * 1024 * 1024;
        profile_.availableRamBytes = 3ULL * 1024 * 1024 * 1024;
    }

    LOGD("HardwareProfiler: RAM - %.2f GB total, %.2f GB available",
         profile_.totalRamGB(), profile_.availableRamGB());
}

void HardwareProfiler::detectGPUInfo() {
    // GPU detection via system properties would require JNI
    // For now, set defaults based on architecture
    profile_.gpuName = "Adreno/Mali (detected via JNI)";
    profile_.openGLSupported = true;
    
    // Vulkan support will be determined via JNI from Android
    profile_.vulkanSupported = false;
    profile_.vulkanVersion = 0;
    profile_.shouldUseVulkan = false;

    LOGD("HardwareProfiler: GPU info pending JNI detection");
}

void HardwareProfiler::detectStorageInfo() {
    struct statfs stats;
    // Check app data directory space
    if (statfs("/data", &stats) == 0) {
        profile_.totalStorageBytes = static_cast<uint64_t>(stats.f_blocks) * stats.f_bsize;
        profile_.availableStorageBytes = static_cast<uint64_t>(stats.f_bavail) * stats.f_bsize;
    } else {
        profile_.totalStorageBytes = 64ULL * 1024 * 1024 * 1024;
        profile_.availableStorageBytes = 32ULL * 1024 * 1024 * 1024;
    }

    LOGD("HardwareProfiler: Storage - %.2f GB available", profile_.availableStorageGB());
}

void HardwareProfiler::updateThermalState() {
    // Thermal state would be read from Android via JNI
    // For now, default to NORMAL
    profile_.thermalState = ThermalState::NORMAL;
    profile_.temperatureCelsius = 35.0f;
}

void HardwareProfiler::updateBatteryState() {
    // Battery state would be read from Android via JNI
    // For now, set defaults
    profile_.batteryState = BatteryState::UNKNOWN;
    profile_.batteryLevel = 100;
    profile_.batterySaverMode = false;
}

void HardwareProfiler::classifyRamProfile() {
    double ramGB = profile_.totalRamGB();

    if (ramGB < 4.0) {
        profile_.ramProfile = RamProfile::LOW;
    } else if (ramGB < 7.0) {
        profile_.ramProfile = RamProfile::STANDARD;
    } else if (ramGB < 14.0) {
        profile_.ramProfile = RamProfile::HIGH;
    } else {
        profile_.ramProfile = RamProfile::PREMIUM;
    }

    LOGD("HardwareProfiler: RAM Profile classified as %d", static_cast<int>(profile_.ramProfile));
}

void HardwareProfiler::computeRecommendations() {
    // Model profile recommendation based on RAM
    double ramGB = profile_.availableRamGB();

    if (ramGB >= 14.0) {
        profile_.recommendedModelProfile = "ultra";
        profile_.recommendedContextSize = 16384;
        profile_.shouldUseGPU = true;
    } else if (ramGB >= 8.0) {
        profile_.recommendedModelProfile = "pro";
        profile_.recommendedContextSize = 8192;
        profile_.shouldUseGPU = profile_.vulkanSupported;
    } else if (ramGB >= 5.0) {
        profile_.recommendedModelProfile = "standard";
        profile_.recommendedContextSize = 4096;
        profile_.shouldUseGPU = false;
    } else {
        profile_.recommendedModelProfile = "lite";
        profile_.recommendedContextSize = 2048;
        profile_.shouldUseGPU = false;
    }

    // Adjust thread count based on thermal and battery
    if (profile_.thermalState == ThermalState::HOT || 
        profile_.thermalState == ThermalState::CRITICAL) {
        profile_.recommendedThreads = std::max(2, profile_.recommendedThreads / 2);
    }

    if (profile_.batterySaverMode || profile_.batteryLevel < 20) {
        profile_.recommendedThreads = std::max(2, profile_.recommendedThreads - 1);
    }

    LOGD("HardwareProfiler: Recommendations - Profile: %s, Threads: %d, Context: %zu",
         profile_.recommendedModelProfile.c_str(), 
         profile_.recommendedThreads,
         profile_.recommendedContextSize);
}

void HardwareProfiler::registerThermalCallback(ThermalCallback callback) {
    thermalCallback_ = callback;
}

void HardwareProfiler::registerBatteryCallback(BatteryCallback callback) {
    batteryCallback_ = callback;
}

} // namespace hardware
} // namespace livehumanai

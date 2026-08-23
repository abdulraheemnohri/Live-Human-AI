#include "HardwareProfiler.h"
#include <fstream>
#include <sstream>
#include <unistd.h>
#include <sys/sysinfo.h>

HardwareProfiler::HardwareProfiler()
    : m_totalRAM(0),
      m_availableRAM(0),
      m_cpuCoreCount(0),
      m_supportsNEON(false),
      m_supportsVulkan(false),
      m_supportsOpenGLES(false) {
}

HardwareProfiler::~HardwareProfiler() {
    shutdown();
}

bool HardwareProfiler::initialize() {
    // Get RAM information
    m_totalRAM = getTotalRAM();
    m_availableRAM = getAvailableRAM();

    // Get CPU information
    m_cpuCoreCount = getCPUCoreCount();
    m_cpuModel = getCPUModel();

    // Get GPU information
    m_gpuModel = getGPUModel();

    // Check hardware acceleration support
    m_supportsNEON = checkNEONSupport();
    m_supportsVulkan = checkVulkanSupport();
    m_supportsOpenGLES = checkOpenGLESSupport();

    return true;
}

void HardwareProfiler::shutdown() {
    // Cleanup if needed
}

size_t HardwareProfiler::getTotalRAM() const {
    // Read from /proc/meminfo
    std::ifstream meminfo("/proc/meminfo");
    std::string line;
    size_t totalRAM = 0;

    while (std::getline(meminfo, line)) {
        if (line.find("MemTotal:") != std::string::npos) {
            std::istringstream iss(line);
            std::string temp;
            size_t value;
            iss >> temp >> value;
            totalRAM = value * 1024; // Convert from kB to bytes
            break;
        }
    }

    return totalRAM;
}

size_t HardwareProfiler::getAvailableRAM() const {
    // Read from /proc/meminfo
    std::ifstream meminfo("/proc/meminfo");
    std::string line;
    size_t availableRAM = 0;

    while (std::getline(meminfo, line)) {
        if (line.find("MemAvailable:") != std::string::npos) {
            std::istringstream iss(line);
            std::string temp;
            size_t value;
            iss >> temp >> value;
            availableRAM = value * 1024; // Convert from kB to bytes
            break;
        }
    }

    return availableRAM;
}

float HardwareProfiler::getRAMUsagePercentage() const {
    if (m_totalRAM == 0) return 0.0f;
    size_t usedRAM = m_totalRAM - m_availableRAM;
    return (static_cast<float>(usedRAM) / static_cast<float>(m_totalRAM)) * 100.0f;
}

int HardwareProfiler::getCPUCoreCount() const {
    return sysconf(_SC_NPROCESSORS_ONLN);
}

std::string HardwareProfiler::getCPUModel() const {
    // Read from /proc/cpuinfo
    std::ifstream cpuinfo("/proc/cpuinfo");
    std::string line;
    std::string cpuModel;

    while (std::getline(cpuinfo, line)) {
        if (line.find("model name") != std::string::npos) {
            size_t colonPos = line.find(':');
            if (colonPos != std::string::npos) {
                cpuModel = line.substr(colonPos + 2);
                break;
            }
        }
    }

    return cpuModel;
}

float HardwareProfiler::getCPUUsage() const {
    // Simplified CPU usage calculation
    // In a real implementation, this would read from /proc/stat
    return 0.0f;
}

float HardwareProfiler::getCPUFrequency() const {
    // Read CPU frequency from sysfs
    std::ifstream freqFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
    float frequency = 0.0f;
    if (freqFile >> frequency) {
        return frequency / 1000.0f; // Convert from kHz to MHz
    }
    return 0.0f;
}

std::string HardwareProfiler::getGPUModel() const {
    // Read from /proc/device-tree/compatible or other GPU info files
    // This is a placeholder - actual implementation depends on the device
    return "Unknown GPU";
}

float HardwareProfiler::getGPUUsage() const {
    // Placeholder - actual implementation would use GPU-specific APIs
    return 0.0f;
}

size_t HardwareProfiler::getTotalStorage() const {
    // Placeholder - read from statfs
    return 0;
}

size_t HardwareProfiler::getAvailableStorage() const {
    // Placeholder - read from statfs
    return 0;
}

bool HardwareProfiler::supportsNEON() const {
    return m_supportsNEON;
}

bool HardwareProfiler::supportsVulkan() const {
    return m_supportsVulkan;
}

bool HardwareProfiler::supportsOpenGLES() const {
    return m_supportsOpenGLES;
}

bool HardwareProfiler::checkNEONSupport() const {
    // Check if the CPU supports ARM NEON
    // This is a simplified check - in practice, you'd use cpufeatures
    return true; // Assume NEON is supported on most modern ARM devices
}

bool HardwareProfiler::checkVulkanSupport() const {
    // Check if Vulkan is supported
    // In practice, you'd check for Vulkan loader library
    return false; // Placeholder
}

bool HardwareProfiler::checkOpenGLESSupport() const {
    // Check if OpenGL ES is supported
    return true; // Assume OpenGL ES is supported on most Android devices
}

HardwareProfiler::DeviceProfile HardwareProfiler::getDeviceProfile() const {
    if (m_totalRAM < 4ULL * 1024 * 1024 * 1024) {
        return DeviceProfile::LOW_END;
    } else if (m_totalRAM < 8ULL * 1024 * 1024 * 1024) {
        return DeviceProfile::MID_RANGE;
    } else if (m_totalRAM < 16ULL * 1024 * 1024 * 1024) {
        return DeviceProfile::HIGH_END;
    } else {
        return DeviceProfile::FLAGSHIP;
    }
}

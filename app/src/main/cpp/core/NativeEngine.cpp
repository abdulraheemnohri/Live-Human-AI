#include "NativeEngine.h"
#include "hardware/HardwareProfiler.h"
#include "hardware/ThermalMonitor.h"
#include "hardware/BatteryMonitor.h"

NativeEngine::NativeEngine()
    : m_hardwareProfiler(std::make_unique<HardwareProfiler>()),
      m_thermalMonitor(std::make_unique<ThermalMonitor>()),
      m_batteryMonitor(std::make_unique<BatteryMonitor>()) {
}

NativeEngine::~NativeEngine() {
    shutdown();
}

bool NativeEngine::initialize() {
    if (!m_hardwareProfiler->initialize()) {
        return false;
    }
    if (!m_thermalMonitor->initialize()) {
        return false;
    }
    if (!m_batteryMonitor->initialize()) {
        return false;
    }
    return true;
}

void NativeEngine::shutdown() {
    if (m_batteryMonitor) m_batteryMonitor->shutdown();
    if (m_thermalMonitor) m_thermalMonitor->shutdown();
    if (m_hardwareProfiler) m_hardwareProfiler->shutdown();
}

std::string NativeEngine::getRuntimeStatus() const {
    return "Running";
}

std::string NativeEngine::getDeviceProfile() const {
    size_t totalRAM = getTotalRAM();
    if (totalRAM <= 6ULL * 1024 * 1024 * 1024) {
        return "6GB Profile";
    } else if (totalRAM <= 16ULL * 1024 * 1024 * 1024) {
        return "16GB Profile";
    } else {
        return "High-End Profile";
    }
}

size_t NativeEngine::getTotalRAM() const {
    return m_hardwareProfiler->getTotalRAM();
}

size_t NativeEngine::getAvailableRAM() const {
    return m_hardwareProfiler->getAvailableRAM();
}

float NativeEngine::getRAMUsagePercentage() const {
    return m_hardwareProfiler->getRAMUsagePercentage();
}

float NativeEngine::getCPUUsage() const {
    return m_hardwareProfiler->getCPUUsage();
}

float NativeEngine::getGPUUsage() const {
    return m_hardwareProfiler->getGPUUsage();
}

float NativeEngine::getTemperature() const {
    return m_thermalMonitor->getTemperature();
}

float NativeEngine::getBatteryLevel() const {
    return m_batteryMonitor->getBatteryLevel();
}

NativeEngine::EngineStats NativeEngine::getEngineStats() const {
    EngineStats stats;
    stats.totalRAM = getTotalRAM();
    stats.availableRAM = getAvailableRAM();
    stats.cpuUsage = getCPUUsage();
    stats.gpuUsage = getGPUUsage();
    stats.temperature = getTemperature();
    stats.batteryLevel = getBatteryLevel();
    return stats;
}

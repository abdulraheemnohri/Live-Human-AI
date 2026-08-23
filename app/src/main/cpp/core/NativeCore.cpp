#include "NativeCore.h"
#include "NativeEngine.h"
#include "ai/AIEngine.h"
#include "hardware/HardwareProfiler.h"
#include "hardware/ThermalMonitor.h"
#include "hardware/BatteryMonitor.h"
#include "scheduler/TaskScheduler.h"
#include "utils/Logger.h"

NativeCore::NativeCore()
    : m_engine(std::make_unique<NativeEngine>()),
      m_aiEngine(std::make_unique<AIEngine>()),
      m_hardwareProfiler(std::make_unique<HardwareProfiler>()),
      m_thermalMonitor(std::make_unique<ThermalMonitor>()),
      m_batteryMonitor(std::make_unique<BatteryMonitor>()),
      m_taskScheduler(std::make_unique<TaskScheduler>()),
      m_logger(std::make_unique<Logger>()),
      m_performanceMode(PerformanceMode::BALANCED) {
}

NativeCore::~NativeCore() {
    shutdown();
}

bool NativeCore::initialize() {
    std::lock_guard<std::mutex> lock(m_mutex);

    // Initialize all subsystems
    if (!m_engine->initialize()) {
        m_logger->log(Logger::Level::ERROR, "Failed to initialize NativeEngine");
        return false;
    }

    if (!m_aiEngine->initialize()) {
        m_logger->log(Logger::Level::ERROR, "Failed to initialize AIEngine");
        return false;
    }

    if (!m_hardwareProfiler->initialize()) {
        m_logger->log(Logger::Level::ERROR, "Failed to initialize HardwareProfiler");
        return false;
    }

    if (!m_thermalMonitor->initialize()) {
        m_logger->log(Logger::Level::ERROR, "Failed to initialize ThermalMonitor");
        return false;
    }

    if (!m_batteryMonitor->initialize()) {
        m_logger->log(Logger::Level::ERROR, "Failed to initialize BatteryMonitor");
        return false;
    }

    if (!m_taskScheduler->initialize()) {
        m_logger->log(Logger::Level::ERROR, "Failed to initialize TaskScheduler");
        return false;
    }

    m_logger->log(Logger::Level::INFO, "NativeCore initialized successfully");
    return true;
}

void NativeCore::shutdown() {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (m_taskScheduler) m_taskScheduler->shutdown();
    if (m_batteryMonitor) m_batteryMonitor->shutdown();
    if (m_thermalMonitor) m_thermalMonitor->shutdown();
    if (m_hardwareProfiler) m_hardwareProfiler->shutdown();
    if (m_aiEngine) m_aiEngine->shutdown();
    if (m_engine) m_engine->shutdown();

    m_logger->log(Logger::Level::INFO, "NativeCore shutdown complete");
}

NativeEngine* NativeCore::getEngine() const {
    return m_engine.get();
}

AIEngine* NativeCore::getAIEngine() const {
    return m_aiEngine.get();
}

HardwareProfiler* NativeCore::getHardwareProfiler() const {
    return m_hardwareProfiler.get();
}

ThermalMonitor* NativeCore::getThermalMonitor() const {
    return m_thermalMonitor.get();
}

BatteryMonitor* NativeCore::getBatteryMonitor() const {
    return m_batteryMonitor.get();
}

TaskScheduler* NativeCore::getTaskScheduler() const {
    return m_taskScheduler.get();
}

Logger* NativeCore::getLogger() const {
    return m_logger.get();
}

std::string NativeCore::getVersion() const {
    return "1.0.0";
}

std::string NativeCore::getRuntimeStatus() const {
    return "Running";
}

std::string NativeCore::getDeviceProfile() const {
    if (getTotalRAM() <= 6ULL * 1024 * 1024 * 1024) {
        return "6GB Profile";
    } else if (getTotalRAM() <= 16ULL * 1024 * 1024 * 1024) {
        return "16GB Profile";
    } else {
        return "High-End Profile";
    }
}

size_t NativeCore::getTotalRAM() const {
    return m_hardwareProfiler->getTotalRAM();
}

size_t NativeCore::getAvailableRAM() const {
    return m_hardwareProfiler->getAvailableRAM();
}

float NativeCore::getRAMUsagePercentage() const {
    return m_hardwareProfiler->getRAMUsagePercentage();
}

void NativeCore::setPerformanceMode(PerformanceMode mode) {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_performanceMode = mode;
    m_logger->log(Logger::Level::INFO, "Performance mode set to: " + std::to_string(static_cast<int>(mode)));
}

NativeCore::PerformanceMode NativeCore::getPerformanceMode() const {
    return m_performanceMode;
}

std::mutex& NativeCore::getMutex() {
    return m_mutex;
}

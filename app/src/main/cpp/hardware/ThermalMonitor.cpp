#include "ThermalMonitor.h"
#include <fstream>
#include <sstream>

ThermalMonitor::ThermalMonitor()
    : m_currentState(ThermalState::NORMAL),
      m_currentTemperature(0.0f) {
}

ThermalMonitor::~ThermalMonitor() {
    shutdown();
}

bool ThermalMonitor::initialize() {
    m_currentTemperature = readTemperature();
    m_currentState = getThermalState();
    return true;
}

void ThermalMonitor::shutdown() {
    // Cleanup if needed
}

ThermalMonitor::ThermalState ThermalMonitor::getThermalState() const {
    if (m_currentTemperature >= THRESHOLD_CRITICAL) {
        return ThermalState::CRITICAL;
    } else if (m_currentTemperature >= THRESHOLD_HOT) {
        return ThermalState::HOT;
    } else if (m_currentTemperature >= THRESHOLD_WARM) {
        return ThermalState::WARM;
    } else {
        return ThermalState::NORMAL;
    }
}

float ThermalMonitor::getTemperature() const {
    return m_currentTemperature;
}

void ThermalMonitor::registerThermalCallback(std::function<void(ThermalState)> callback) {
    m_thermalCallback = callback;
}

void ThermalMonitor::update() {
    float newTemperature = readTemperature();
    if (newTemperature != m_currentTemperature) {
        m_currentTemperature = newTemperature;
        ThermalState newState = getThermalState();
        if (newState != m_currentState) {
            m_currentState = newState;
            if (m_thermalCallback) {
                m_thermalCallback(m_currentState);
            }
        }
    }
}

float ThermalMonitor::readTemperature() const {
    // Try to read temperature from common thermal zones
    const char* thermalZones[] = {
        "/sys/class/thermal/thermal_zone0/temp",
        "/sys/class/thermal/thermal_zone1/temp",
        "/sys/class/thermal/thermal_zone2/temp",
        "/sys/class/hwmon/hwmon0/temp1_input"
    };

    for (const char* zone : thermalZones) {
        std::ifstream tempFile(zone);
        if (tempFile.is_open()) {
            int tempValue;
            if (tempFile >> tempValue) {
                // Temperature is usually in millidegrees Celsius
                return static_cast<float>(tempValue) / 1000.0f;
            }
        }
    }

    // Fallback to a default temperature if reading fails
    return 30.0f; // Assume room temperature
}

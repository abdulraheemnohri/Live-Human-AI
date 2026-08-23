#include "BatteryMonitor.h"
#include <fstream>
#include <sstream>

BatteryMonitor::BatteryMonitor()
    : m_batteryLevel(100.0f),
      m_isCharging(false) {
}

BatteryMonitor::~BatteryMonitor() {
    shutdown();
}

bool BatteryMonitor::initialize() {
    m_batteryLevel = readBatteryLevel();
    m_isCharging = readChargingState();
    return true;
}

void BatteryMonitor::shutdown() {
    // Cleanup if needed
}

float BatteryMonitor::getBatteryLevel() const {
    return m_batteryLevel;
}

bool BatteryMonitor::isCharging() const {
    return m_isCharging;
}

bool BatteryMonitor::isBatteryLow() const {
    return m_batteryLevel <= LOW_BATTERY_THRESHOLD;
}

void BatteryMonitor::registerBatteryCallback(std::function<void(float, bool)> callback) {
    m_batteryCallback = callback;
}

void BatteryMonitor::update() {
    float newLevel = readBatteryLevel();
    bool newChargingState = readChargingState();

    if (newLevel != m_batteryLevel || newChargingState != m_isCharging) {
        m_batteryLevel = newLevel;
        m_isCharging = newChargingState;
        if (m_batteryCallback) {
            m_batteryCallback(m_batteryLevel, m_isCharging);
        }
    }
}

float BatteryMonitor::readBatteryLevel() const {
    // Read battery level from /sys/class/power_supply/BAT0/capacity
    std::ifstream batteryFile("/sys/class/power_supply/BAT0/capacity");
    if (batteryFile.is_open()) {
        int level;
        if (batteryFile >> level) {
            return static_cast<float>(level);
        }
    }

    // Alternative path for some devices
    batteryFile.open("/sys/class/power_supply/battery/capacity");
    if (batteryFile.is_open()) {
        int level;
        if (batteryFile >> level) {
            return static_cast<float>(level);
        }
    }

    // Fallback to a default value if reading fails
    return 100.0f;
}

bool BatteryMonitor::readChargingState() const {
    // Read charging state from /sys/class/power_supply/BAT0/status
    std::ifstream statusFile("/sys/class/power_supply/BAT0/status");
    if (statusFile.is_open()) {
        std::string status;
        if (statusFile >> status) {
            return status == "Charging" || status == "Full";
        }
    }

    // Alternative path for some devices
    statusFile.open("/sys/class/power_supply/battery/status");
    if (statusFile.is_open()) {
        std::string status;
        if (statusFile >> status) {
            return status == "Charging" || status == "Full";
        }
    }

    // Fallback to false if reading fails
    return false;
}

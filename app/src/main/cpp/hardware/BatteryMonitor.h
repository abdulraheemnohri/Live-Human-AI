#ifndef LIVE_HUMAN_AI_BATTERY_MONITOR_H
#define LIVE_HUMAN_AI_BATTERY_MONITOR_H

#include <string>
#include <functional>

// BatteryMonitor tracks battery level and charging state
class BatteryMonitor {
public:
    BatteryMonitor();
    ~BatteryMonitor();

    // Initialization and shutdown
    bool initialize();
    void shutdown();

    // Battery state
    float getBatteryLevel() const;
    bool isCharging() const;
    bool isBatteryLow() const;

    // Battery thresholds
    static constexpr float LOW_BATTERY_THRESHOLD = 20.0f; // 20%

    // Register a callback for battery state changes
    void registerBatteryCallback(std::function<void(float, bool)> callback);

    // Update battery state (called periodically)
    void update();

private:
    float m_batteryLevel;
    bool m_isCharging;
    std::function<void(float, bool)> m_batteryCallback;

    // Internal methods
    float readBatteryLevel() const;
    bool readChargingState() const;
};

#endif // LIVE_HUMAN_AI_BATTERY_MONITOR_H

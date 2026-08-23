#ifndef LIVE_HUMAN_AI_THERMAL_MONITOR_H
#define LIVE_HUMAN_AI_THERMAL_MONITOR_H

#include <string>
#include <functional>

// ThermalMonitor tracks device temperature and thermal state
class ThermalMonitor {
public:
    ThermalMonitor();
    ~ThermalMonitor();

    // Initialization and shutdown
    bool initialize();
    void shutdown();

    // Thermal state
    enum class ThermalState {
        NORMAL,
        WARM,
        HOT,
        CRITICAL
    };

    ThermalState getThermalState() const;
    float getTemperature() const;

    // Thermal thresholds (in Celsius)
    static constexpr float THRESHOLD_WARM = 40.0f;
    static constexpr float THRESHOLD_HOT = 50.0f;
    static constexpr float THRESHOLD_CRITICAL = 60.0f;

    // Register a callback for thermal state changes
    void registerThermalCallback(std::function<void(ThermalState)> callback);

    // Update thermal state (called periodically)
    void update();

private:
    ThermalState m_currentState;
    float m_currentTemperature;
    std::function<void(ThermalState)> m_thermalCallback;

    // Internal methods
    float readTemperature() const;
};

#endif // LIVE_HUMAN_AI_THERMAL_MONITOR_H

#ifndef LIVEHUMANAI_THERMALMANAGER_H
#define LIVEHUMANAI_THERMALMANAGER_H

#include "DeviceProfile.h"

namespace livehumanai {
namespace hardware {

class ThermalManager {
public:
    ThermalManager() : currentState_(ThermalState::NORMAL) {}
    ~ThermalManager() = default;
    
    ThermalState getCurrentState() const { return currentState_; }
    float getTemperature() const { return temperature_; }
    
    void update() {}
    
private:
    ThermalState currentState_;
    float temperature_ = 35.0f;
};

} // namespace hardware
} // namespace livehumanai

#endif

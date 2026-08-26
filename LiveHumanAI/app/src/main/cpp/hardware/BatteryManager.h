#ifndef LIVEHUMANAI_BATTERYMANAGER_H
#define LIVEHUMANAI_BATTERYMANAGER_H

#include "DeviceProfile.h"

namespace livehumanai {
namespace hardware {

class BatteryManager {
public:
    BatteryManager() : level_(100), state_(BatteryState::UNKNOWN), saverMode_(false) {}
    ~BatteryManager() = default;
    
    int getLevel() const { return level_; }
    BatteryState getState() const { return state_; }
    bool isSaverMode() const { return saverMode_; }
    
    void update() {}
    
private:
    int level_;
    BatteryState state_;
    bool saverMode_;
};

} // namespace hardware
} // namespace livehumanai

#endif

#ifndef NATIVE_RESOURCE_CONTROLLER_H
#define NATIVE_RESOURCE_CONTROLLER_H

#include "../ai/JalebiResourcePolicy.h"
#include <algorithm>

namespace LiveHumanAI {

class NativeResourceController {
public:
    void update(const JalebiResourceSnapshot& snapshot) { m_snapshot = snapshot; }
    JalebiResourcePolicy::Decision decision() const { return JalebiResourcePolicy::evaluate(m_snapshot); }
    bool mayRunExpensive() const { return decision() != JalebiResourcePolicy::Decision::PAUSE; }
    bool degraded() const { return decision() == JalebiResourcePolicy::Decision::REDUCE_WORKLOAD; }
    const JalebiResourceSnapshot& snapshot() const { return m_snapshot; }

private:
    JalebiResourceSnapshot m_snapshot{};
};

} // namespace LiveHumanAI
#endif

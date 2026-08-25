#ifndef JALEBI_RESOURCE_POLICY_H
#define JALEBI_RESOURCE_POLICY_H

namespace LiveHumanAI {

struct JalebiResourceSnapshot {
    float ramUsagePercent = 0.0f;
    float cpuUsagePercent = 0.0f;
    float temperatureC = 0.0f;
    float batteryPercent = 100.0f;
};

class JalebiResourcePolicy {
public:
    enum class Decision { ALLOW, REDUCE_WORKLOAD, PAUSE };

    static Decision evaluate(const JalebiResourceSnapshot& resources) {
        if (resources.ramUsagePercent >= 92.0f ||
            resources.temperatureC >= 45.0f) {
            return Decision::PAUSE;
        }
        if (resources.ramUsagePercent >= 82.0f ||
            resources.cpuUsagePercent >= 92.0f ||
            resources.temperatureC >= 40.0f ||
            resources.batteryPercent <= 10.0f) {
            return Decision::REDUCE_WORKLOAD;
        }
        return Decision::ALLOW;
    }
};

} // namespace LiveHumanAI

#endif

#ifndef JALEBI_LIVE_RUNTIME_H
#define JALEBI_LIVE_RUNTIME_H

#include "JalebiPerceptionGate.h"
#include "JalebiModelEscalator.h"
#include "JalebiResourcePolicy.h"
#include "JalebiToolPolicy.h"
#include <string>

namespace LiveHumanAI {

// Policy-only live runtime coordinator. It intentionally accepts semantic
// perception results instead of raw camera/audio data, keeping capture and
// privacy permissions in the Android layer.
class JalebiLiveRuntime {
public:
    struct Decision {
        bool runInference = false;
        bool pause = false;
        bool degraded = false;
        std::string modelTier = "small";
        std::string reason;
    };

    Decision onPerception(const JalebiWorldState& state,
                          const JalebiResourceSnapshot& resources,
                          float confidence,
                          bool flagshipDevice) {
        const bool changed = m_gate.submit(state);
        const auto resourceDecision = JalebiResourcePolicy::evaluate(resources);

        Decision result;
        if (resourceDecision == JalebiResourcePolicy::Decision::PAUSE) {
            result.pause = true;
            result.reason = "resource_limit";
            return result;
        }

        result.runInference = changed;
        result.degraded = resourceDecision == JalebiResourcePolicy::Decision::REDUCE_WORKLOAD;
        const auto model = JalebiModelEscalator::choose(confidence, result.degraded, flagshipDevice);
        result.modelTier = JalebiModelEscalator::name(model.tier);
        if (result.degraded) result.reason = "reduced_workload";
        else if (!changed) result.reason = "scene_unchanged";
        else result.reason = "scene_changed";
        return result;
    }

private:
    JalebiPerceptionGate m_gate;
};

} // namespace LiveHumanAI

#endif

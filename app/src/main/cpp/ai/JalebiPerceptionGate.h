#ifndef JALEBI_PERCEPTION_GATE_H
#define JALEBI_PERCEPTION_GATE_H

#include "JalebiWorldState.h"

namespace LiveHumanAI {

// Cheap gate used before expensive vision/LLM inference. The camera producer
// owns frame capture; this component only decides whether a semantic change
// warrants another expensive JCL iteration.
class JalebiPerceptionGate {
public:
    bool submit(const JalebiWorldState& state) {
        return m_tracker.update(state);
    }

    bool shouldRunExpensiveInference() const {
        return !m_tracker.hasState() || m_tracker.current().sceneChanged ||
               m_tracker.current().taskChanged;
    }

    const JalebiWorldState& state() const { return m_tracker.current(); }

private:
    JalebiWorldStateTracker m_tracker;
};

} // namespace LiveHumanAI

#endif

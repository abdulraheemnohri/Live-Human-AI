#include "JalebiRuntimeCoordinator.h"

namespace LiveHumanAI {

JalebiRuntimeCoordinator::JalebiRuntimeCoordinator(JalebiLoopEngine& engine)
    : m_engine(engine) {}

bool JalebiRuntimeCoordinator::begin(int loopId, const RuntimeInput& input) {
    // Execute the bounded lifecycle first, then replace the placeholder stage
    // evidence with evidence supplied by the actual runtime adapters.
    const auto iteration = m_engine.executeIteration(loopId, input.rawInput);
    if (iteration.iterationId <= 0) return false;

    const auto snapshot = m_engine.getSnapshot(loopId);
    if (snapshot.history.empty()) return false;

    // The engine intentionally owns lifecycle state while adapters own the
    // actual perception/model/tool work. A future engine API can atomically
    // attach these fields; for now evaluation remains the synchronization
    // boundary between the two layers.
    return m_engine.recordEvaluation(
        loopId,
        0.0f,
        false,
        "Runtime evidence collected; awaiting evaluation",
        "EVALUATE",
        "");
}

bool JalebiRuntimeCoordinator::evaluate(int loopId, float confidence,
                                        bool goalCompleted,
                                        const std::string& evidence,
                                        const std::string& nextAction,
                                        const std::string& memoryUpdates) {
    return m_engine.recordEvaluation(
        loopId, confidence, goalCompleted, evidence, nextAction, memoryUpdates);
}

} // namespace LiveHumanAI

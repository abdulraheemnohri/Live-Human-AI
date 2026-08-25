#ifndef JALEBI_NATIVE_RUNTIME_H
#define JALEBI_NATIVE_RUNTIME_H

#include "JalebiLoopEngine.h"
#include "JalebiWorldState.h"
#include "JalebiResourcePolicy.h"
#include "JalebiConfidencePolicy.h"
#include "JalebiRetryPolicy.h"
#include "JalebiToolPolicy.h"
#include "JalebiModelEscalator.h"
#include "JalebiMemoryPolicy.h"
#include <mutex>
#include <string>

namespace LiveHumanAI {

class JalebiNativeRuntime {
public:
    struct Input {
        std::string semanticInput;
        JalebiWorldState world;
        JalebiResourceSnapshot resources;
        float confidence = 0.0f;
        bool evidenceAvailable = false;
        bool flagshipDevice = false;
    };

    struct Decision {
        JalebiConfidencePolicy::Decision confidenceDecision = JalebiConfidencePolicy::Decision::RECHECK;
        JalebiResourcePolicy::Decision resourceDecision = JalebiResourcePolicy::Decision::ALLOW;
        JalebiModelEscalator::Tier modelTier = JalebiModelEscalator::Tier::SMALL;
        bool sceneChanged = false;
        bool runInference = false;
        bool paused = false;
        std::string reason;
    };

    bool initialize() {
        std::lock_guard<std::mutex> lock(m_mutex);
        if (m_initialized) return true;
        m_initialized = m_engine.initialize();
        return m_initialized;
    }

    void shutdown() {
        std::lock_guard<std::mutex> lock(m_mutex);
        if (!m_initialized) return;
        m_engine.shutdown();
        m_activeLoop = 0;
        m_initialized = false;
    }

    int createGoal(const std::string& goal, int maxIterations = 8) {
        std::lock_guard<std::mutex> lock(m_mutex);
        if (!m_initialized) return 0;
        if (m_activeLoop > 0) m_engine.cancelLoop(m_activeLoop);
        m_activeLoop = m_engine.createLoop(goal, maxIterations);
        return m_activeLoop;
    }

    bool start() { std::lock_guard<std::mutex> lock(m_mutex); return m_initialized && m_activeLoop > 0 && m_engine.startLoop(m_activeLoop); }
    bool pause() { std::lock_guard<std::mutex> lock(m_mutex); return m_initialized && m_activeLoop > 0 && m_engine.pauseLoop(m_activeLoop); }
    bool resume() { std::lock_guard<std::mutex> lock(m_mutex); return m_initialized && m_activeLoop > 0 && m_engine.resumeLoop(m_activeLoop); }
    bool cancel() { std::lock_guard<std::mutex> lock(m_mutex); return m_initialized && m_activeLoop > 0 && m_engine.cancelLoop(m_activeLoop); }

    Decision process(const Input& input) {
        std::lock_guard<std::mutex> lock(m_mutex);
        Decision d;
        if (!m_initialized) { d.reason = "runtime_not_initialized"; return d; }
        d.resourceDecision = JalebiResourcePolicy::evaluate(input.resources);
        d.paused = d.resourceDecision == JalebiResourcePolicy::Decision::PAUSE;
        if (d.paused) { d.reason = "resource_limit"; return d; }
        d.sceneChanged = m_world.update(input.world);
        const bool degraded = d.resourceDecision == JalebiResourcePolicy::Decision::REDUCE_WORKLOAD;
        d.confidenceDecision = JalebiConfidencePolicy::decide(input.confidence, input.evidenceAvailable, false, degraded);
        const auto model = JalebiModelEscalator::choose(input.confidence, degraded, input.flagshipDevice);
        d.modelTier = model.tier;
        d.runInference = d.sceneChanged || d.confidenceDecision != JalebiConfidencePolicy::Decision::ACCEPT;
        d.reason = d.runInference ? "recheck_required" : "stable_high_confidence";
        return d;
    }

    JalebiLoopEngine::Iteration execute(const std::string& input) {
        std::lock_guard<std::mutex> lock(m_mutex);
        return m_initialized && m_activeLoop > 0 ? m_engine.executeIteration(m_activeLoop, input) : JalebiLoopEngine::Iteration{};
    }

    bool evaluate(float confidence, bool completed, const std::string& evidence,
                  const std::string& nextAction, const std::string& memoryUpdates = "") {
        std::lock_guard<std::mutex> lock(m_mutex);
        return m_initialized && m_activeLoop > 0 && m_engine.recordEvaluation(m_activeLoop, confidence, completed, evidence, nextAction, memoryUpdates);
    }

    int activeLoop() const { std::lock_guard<std::mutex> lock(m_mutex); return m_activeLoop; }
    JalebiLoopEngine& engine() { return m_engine; }

private:
    mutable std::mutex m_mutex;
    JalebiLoopEngine m_engine;
    JalebiWorldStateTracker m_world;
    int m_activeLoop = 0;
    bool m_initialized = false;
};

} // namespace LiveHumanAI
#endif

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
#include <algorithm>
#include <chrono>
#include <mutex>
#include <sstream>
#include <string>
#include <vector>

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
        if (m_activeLoop > 0) m_engine.cancelLoop(m_activeLoop);
        m_engine.shutdown();
        m_activeLoop = 0;
        m_initialized = false;
        m_world = JalebiWorldStateTracker{};
    }

    int createGoal(const std::string& goal, int maxIterations = 8) {
        std::lock_guard<std::mutex> lock(m_mutex);
        if (!m_initialized || goal.empty()) return 0;
        if (m_activeLoop > 0) m_engine.cancelLoop(m_activeLoop);
        m_activeLoop = m_engine.createLoop(goal, std::max(1, std::min(maxIterations, 64)));
        return m_activeLoop;
    }

    bool start() { std::lock_guard<std::mutex> lock(m_mutex); return m_initialized && m_activeLoop > 0 && m_engine.startLoop(m_activeLoop); }
    bool pause() { std::lock_guard<std::mutex> lock(m_mutex); return m_initialized && m_activeLoop > 0 && m_engine.pauseLoop(m_activeLoop); }
    bool resume() { std::lock_guard<std::mutex> lock(m_mutex); return m_initialized && m_activeLoop > 0 && m_engine.resumeLoop(m_activeLoop); }
    bool cancel() { std::lock_guard<std::mutex> lock(m_mutex); return m_initialized && m_activeLoop > 0 && m_engine.cancelLoop(m_activeLoop); }

    Decision process(const Input& input) {
        std::lock_guard<std::mutex> lock(m_mutex);
        Decision d;
        if (!m_initialized || m_activeLoop <= 0) { d.reason = "runtime_not_ready"; return d; }
        d.resourceDecision = JalebiResourcePolicy::evaluate(input.resources);
        d.paused = d.resourceDecision == JalebiResourcePolicy::Decision::PAUSE;
        if (d.paused) { d.reason = "resource_limit"; m_engine.pauseLoop(m_activeLoop); return d; }
        d.sceneChanged = m_world.update(input.world);
        const bool degraded = d.resourceDecision == JalebiResourcePolicy::Decision::REDUCE_WORKLOAD;
        d.confidenceDecision = JalebiConfidencePolicy::decide(input.confidence, input.evidenceAvailable, false, degraded);
        d.modelTier = JalebiModelEscalator::choose(input.confidence, degraded, input.flagshipDevice).tier;
        d.runInference = d.sceneChanged || d.confidenceDecision != JalebiConfidencePolicy::Decision::ACCEPT;
        d.reason = d.runInference ? "inference_required" : "stable_high_confidence";
        return d;
    }

    std::string submitVision(const std::string& sceneId, const std::string& objects, const std::string& text,
                             float confidence, const JalebiResourceSnapshot& resources, bool flagshipDevice) {
        Input input;
        input.semanticInput = "vision:" + sceneId;
        input.world.timestampMs = nowMs();
        input.world.sceneId = sceneId;
        input.world.detectedObjects = split(objects, '|');
        input.world.detectedText = split(text, '|');
        input.confidence = std::clamp(confidence, 0.0f, 1.0f);
        input.evidenceAvailable = !input.world.detectedObjects.empty() || !input.world.detectedText.empty() || !sceneId.empty();
        input.resources = resources;
        input.flagshipDevice = flagshipDevice;
        const Decision d = process(input);
        if (!d.runInference || d.paused) return decisionJson(d);
        execute(input.semanticInput);
        return decisionJson(d);
    }

    std::string submitSpeech(const std::string& transcript, float confidence, bool isFinal,
                             const JalebiResourceSnapshot& resources, bool flagshipDevice) {
        if (!isFinal || transcript.empty()) return "{\"runInference\":false,\"paused\":false,\"sceneChanged\":false,\"confidence\":\"recheck\",\"resource\":\"allow\",\"model\":\"small\",\"reason\":\"partial_or_empty\"}";
        Input input;
        input.semanticInput = "speech:" + transcript;
        input.world.timestampMs = nowMs();
        input.world.speakerState = "speaking";
        input.world.taskChanged = true;
        input.confidence = std::clamp(confidence, 0.0f, 1.0f);
        input.evidenceAvailable = true;
        input.resources = resources;
        input.flagshipDevice = flagshipDevice;
        const Decision d = process(input);
        if (d.paused) return decisionJson(d);
        execute(input.semanticInput);
        return decisionJson(d);
    }

    JalebiLoopEngine::Iteration execute(const std::string& input) {
        std::lock_guard<std::mutex> lock(m_mutex);
        return m_initialized && m_activeLoop > 0 ? m_engine.executeIteration(m_activeLoop, input) : JalebiLoopEngine::Iteration{};
    }

    bool evaluate(float confidence, bool completed, const std::string& evidence,
                  const std::string& nextAction, const std::string& memoryUpdates = "") {
        std::lock_guard<std::mutex> lock(m_mutex);
        if (!m_initialized || m_activeLoop <= 0) return false;
        return m_engine.recordEvaluation(m_activeLoop, confidence, completed, evidence, nextAction, memoryUpdates);
    }

    int activeLoop() const { std::lock_guard<std::mutex> lock(m_mutex); return m_activeLoop; }
    JalebiLoopEngine& engine() { return m_engine; }

private:
    static long long nowMs() {
        return std::chrono::duration_cast<std::chrono::milliseconds>(
            std::chrono::system_clock::now().time_since_epoch()).count();
    }

    static std::vector<std::string> split(const std::string& value, char delimiter) {
        std::vector<std::string> result;
        std::stringstream stream(value);
        std::string item;
        while (std::getline(stream, item, delimiter)) if (!item.empty()) result.push_back(item);
        return result;
    }

    static const char* confidenceName(JalebiConfidencePolicy::Decision d) {
        switch (d) {
            case JalebiConfidencePolicy::Decision::ACCEPT: return "accept";
            case JalebiConfidencePolicy::Decision::ESCALATE: return "escalate";
            case JalebiConfidencePolicy::Decision::ASK_USER: return "ask_user";
            default: return "recheck";
        }
    }

    static const char* resourceName(JalebiResourcePolicy::Decision d) {
        switch (d) {
            case JalebiResourcePolicy::Decision::REDUCE_WORKLOAD: return "reduce_workload";
            case JalebiResourcePolicy::Decision::PAUSE: return "pause";
            default: return "allow";
        }
    }

    static const char* modelName(JalebiModelEscalator::Tier tier) {
        switch (tier) {
            case JalebiModelEscalator::Tier::MEDIUM: return "medium";
            case JalebiModelEscalator::Tier::LARGE: return "large";
            default: return "small";
        }
    }

    static std::string escape(const std::string& value) {
        std::string out;
        for (char c : value) { if (c == '\\' || c == '"') out += '\\'; out += c; }
        return out;
    }

    static std::string decisionJson(const Decision& d) {
        std::ostringstream out;
        out << "{\"runInference\":" << (d.runInference ? "true" : "false")
            << ",\"paused\":" << (d.paused ? "true" : "false")
            << ",\"sceneChanged\":" << (d.sceneChanged ? "true" : "false")
            << ",\"confidence\":\"" << confidenceName(d.confidenceDecision)
            << "\",\"resource\":\"" << resourceName(d.resourceDecision)
            << "\",\"model\":\"" << modelName(d.modelTier)
            << "\",\"reason\":\"" << escape(d.reason) << "\"}";
        return out.str();
    }

    mutable std::mutex m_mutex;
    JalebiLoopEngine m_engine;
    JalebiWorldStateTracker m_world;
    int m_activeLoop = 0;
    bool m_initialized = false;
};

} // namespace LiveHumanAI
#endif

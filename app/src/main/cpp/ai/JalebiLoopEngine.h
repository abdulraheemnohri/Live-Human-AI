#ifndef JALEBI_LOOP_ENGINE_H
#define JALEBI_LOOP_ENGINE_H

#include <mutex>
#include <string>
#include <unordered_map>
#include <vector>

namespace LiveHumanAI {

class JalebiLoopEngine {
public:
    enum class LoopState {
        IDLE, INITIALIZING, PERCEIVING, INTERPRETING, REASONING, PLANNING,
        ACTING, OBSERVING, EVALUATING, UPDATING_MEMORY, REPLANNING,
        WAITING_USER, COMPLETED, FAILED, CANCELLED, PAUSED, RESOURCE_LIMIT,
        SAFETY_BLOCKED
    };

    struct Goal {
        int id = 0;
        std::string description;
        int priority = 0;
        long long deadlineMs = 0;
        float successConfidence = 0.90f;
        int maxIterations = 8;
        long long maxDurationMs = 60000;
    };

    struct Iteration {
        int iterationId = 0;
        long long timestamp = 0;
        std::string input;
        std::string perception;
        std::string interpretation;
        std::string reasoningSummary;
        std::string plan;
        std::string action;
        std::string observation;
        std::string evaluation;
        float confidence = 0.0f;
        std::vector<std::string> errors;
        std::string memoryUpdates;
        std::string nextAction;
    };

    struct LoopSnapshot {
        int loopId = 0;
        Goal goal;
        LoopState state = LoopState::IDLE;
        int currentIteration = 0;
        float confidence = 0.0f;
        long long createdAtMs = 0;
        long long updatedAtMs = 0;
        std::vector<Iteration> history;
    };

    JalebiLoopEngine();
    ~JalebiLoopEngine();

    bool initialize();
    void shutdown();

    int createLoop(const std::string& goal, int maxIterations = 8, float successConfidence = 0.90f);
    bool startLoop(int loopId);
    bool pauseLoop(int loopId);
    bool resumeLoop(int loopId);
    bool cancelLoop(int loopId);
    bool completeLoop(int loopId);
    bool failLoop(int loopId, const std::string& reason);

    Iteration executeIteration(int loopId, const std::string& currentInput);

    bool recordEvaluation(
        int loopId,
        float confidence,
        bool goalCompleted,
        const std::string& evaluation,
        const std::string& nextAction,
        const std::string& memoryUpdates = ""
    );

    LoopState getLoopState(int loopId) const;
    float getLatestConfidence(int loopId) const;
    int getCurrentIteration(int loopId) const;
    Goal getGoal(int loopId) const;
    LoopSnapshot getSnapshot(int loopId) const;
    std::vector<Iteration> getLoopHistory(int loopId) const;
    std::string getLoopHistoryJson(int loopId) const;
    std::string getStateName(LoopState state) const;

private:
    struct LoopContext {
        Goal goal;
        LoopState state = LoopState::INITIALIZING;
        int currentIteration = 0;
        float confidence = 0.0f;
        long long createdAtMs = 0;
        long long updatedAtMs = 0;
        std::vector<Iteration> history;
    };

    static long long nowMs();
    static std::string jsonEscape(const std::string& value);
    static int clampMaxIterations(int maxIterations);
    static float clampConfidence(float confidence);

    mutable std::mutex m_mutex;
    std::unordered_map<int, LoopContext> m_loops;
    int m_nextLoopId;
    bool m_initialized;
};

} // namespace LiveHumanAI

#endif // JALEBI_LOOP_ENGINE_H

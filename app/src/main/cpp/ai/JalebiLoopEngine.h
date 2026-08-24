#ifndef JALEBI_LOOP_ENGINE_H
#define JALEBI_LOOP_ENGINE_H

#include <string>
#include <vector>
#include <memory>
#include <mutex>

namespace LiveHumanAI {

class JalebiLoopEngine {
public:
    enum class LoopState {
        IDLE,
        INITIALIZING,
        PERCEIVING,
        INTERPRETING,
        REASONING,
        PLANNING,
        ACTING,
        OBSERVING,
        EVALUATING,
        UPDATING_MEMORY,
        REPLANNING,
        WAITING_USER,
        COMPLETED,
        FAILED,
        CANCELLED,
        PAUSED,
        RESOURCE_LIMIT,
        SAFETY_BLOCKED
    };

    struct Iteration {
        int iterationId;
        long long timestamp;
        std::string input;
        std::string perception;
        std::string interpretation;
        std::string reasoningSummary;
        std::string plan;
        std::string action;
        std::string observation;
        std::string evaluation;
        float confidence;
        std::string nextAction;
    };

    JalebiLoopEngine();
    ~JalebiLoopEngine();

    bool initialize();
    void shutdown();

    int createLoop(const std::string& goal, int maxIterations = 8);
    bool startLoop(int loopId);
    void pauseLoop(int loopId);
    void resumeLoop(int loopId);
    void cancelLoop(int loopId);

    Iteration executeIteration(int loopId, const std::string& currentInput);
    LoopState getLoopState(int loopId) const;
    float getLatestConfidence(int loopId) const;
    std::string getStateName(LoopState state) const;

private:
    mutable std::mutex m_mutex;
    LoopState m_currentState;
    int m_activeLoopId;
    int m_currentIteration;
    int m_maxIterations;
    float m_confidence;
    std::string m_goal;
    std::vector<Iteration> m_history;
};

} // namespace LiveHumanAI

#endif // JALEBI_LOOP_ENGINE_H

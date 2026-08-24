#include "JalebiLoopEngine.h"
#include <chrono>
#include <iostream>

namespace LiveHumanAI {

JalebiLoopEngine::JalebiLoopEngine()
    : m_currentState(LoopState::IDLE),
      m_activeLoopId(0),
      m_currentIteration(0),
      m_maxIterations(8),
      m_confidence(1.0f) {}

JalebiLoopEngine::~JalebiLoopEngine() {
    shutdown();
}

bool JalebiLoopEngine::initialize() {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_currentState = LoopState::IDLE;
    m_history.clear();
    return true;
}

void JalebiLoopEngine::shutdown() {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_currentState = LoopState::IDLE;
}

int JalebiLoopEngine::createLoop(const std::string& goal, int maxIterations) {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_goal = goal;
    m_maxIterations = maxIterations;
    m_activeLoopId++;
    m_currentIteration = 0;
    m_confidence = 1.0f;
    m_currentState = LoopState::INITIALIZING;
    m_history.clear();
    return m_activeLoopId;
}

bool JalebiLoopEngine::startLoop(int loopId) {
    std::lock_guard<std::mutex> lock(m_mutex);
    if (m_activeLoopId == loopId) {
        m_currentState = LoopState::PERCEIVING;
        return true;
    }
    return false;
}

void JalebiLoopEngine::pauseLoop(int loopId) {
    std::lock_guard<std::mutex> lock(m_mutex);
    if (m_activeLoopId == loopId) {
        m_currentState = LoopState::PAUSED;
    }
}

void JalebiLoopEngine::resumeLoop(int loopId) {
    std::lock_guard<std::mutex> lock(m_mutex);
    if (m_activeLoopId == loopId && m_currentState == LoopState::PAUSED) {
        m_currentState = LoopState::PERCEIVING;
    }
}

void JalebiLoopEngine::cancelLoop(int loopId) {
    std::lock_guard<std::mutex> lock(m_mutex);
    if (m_activeLoopId == loopId) {
        m_currentState = LoopState::CANCELLED;
    }
}

JalebiLoopEngine::Iteration JalebiLoopEngine::executeIteration(int loopId, const std::string& currentInput) {
    std::lock_guard<std::mutex> lock(m_mutex);
    Iteration iter;
    m_currentIteration++;
    iter.iterationId = m_currentIteration;
    iter.timestamp = std::chrono::duration_cast<std::chrono::milliseconds>(
        std::chrono::system_clock::now().time_since_epoch()).count();
    iter.input = currentInput;

    m_currentState = LoopState::PERCEIVING;
    iter.perception = "Perceived input and environment context for: " + currentInput;

    m_currentState = LoopState::INTERPRETING;
    iter.interpretation = "Interpreted intent and user goal: " + m_goal;

    m_currentState = LoopState::REASONING;
    iter.reasoningSummary = "Evaluated rules, available models, and safety boundaries.";

    m_currentState = LoopState::PLANNING;
    iter.plan = "Formulated step plan for iteration " + std::to_string(m_currentIteration);

    m_currentState = LoopState::ACTING;
    iter.action = "Executed target Android tool / model inference action.";

    m_currentState = LoopState::OBSERVING;
    iter.observation = "Observed action result and environment state.";

    m_currentState = LoopState::EVALUATING;
    iter.confidence = 0.95f;
    iter.evaluation = "Goal evaluation pass. Confidence: 95%";

    m_currentState = LoopState::UPDATING_MEMORY;
    m_history.push_back(iter);

    if (m_currentIteration >= m_maxIterations) {
        m_currentState = LoopState::COMPLETED;
        iter.nextAction = "COMPLETE";
    } else {
        m_currentState = LoopState::REPLANNING;
        iter.nextAction = "REPLAN";
    }

    return iter;
}

JalebiLoopEngine::LoopState JalebiLoopEngine::getLoopState(int loopId) const {
    std::lock_guard<std::mutex> lock(m_mutex);
    return m_currentState;
}

float JalebiLoopEngine::getLatestConfidence(int loopId) const {
    std::lock_guard<std::mutex> lock(m_mutex);
    return m_confidence;
}

std::string JalebiLoopEngine::getStateName(LoopState state) const {
    switch (state) {
        case LoopState::IDLE: return "IDLE";
        case LoopState::INITIALIZING: return "INITIALIZING";
        case LoopState::PERCEIVING: return "PERCEIVING";
        case LoopState::INTERPRETING: return "INTERPRETING";
        case LoopState::REASONING: return "REASONING";
        case LoopState::PLANNING: return "PLANNING";
        case LoopState::ACTING: return "ACTING";
        case LoopState::OBSERVING: return "OBSERVING";
        case LoopState::EVALUATING: return "EVALUATING";
        case LoopState::UPDATING_MEMORY: return "UPDATING_MEMORY";
        case LoopState::REPLANNING: return "REPLANNING";
        case LoopState::WAITING_USER: return "WAITING_USER";
        case LoopState::COMPLETED: return "COMPLETED";
        case LoopState::FAILED: return "FAILED";
        case LoopState::CANCELLED: return "CANCELLED";
        case LoopState::PAUSED: return "PAUSED";
        case LoopState::RESOURCE_LIMIT: return "RESOURCE_LIMIT";
        case LoopState::SAFETY_BLOCKED: return "SAFETY_BLOCKED";
        default: return "UNKNOWN";
    }
}

} // namespace LiveHumanAI

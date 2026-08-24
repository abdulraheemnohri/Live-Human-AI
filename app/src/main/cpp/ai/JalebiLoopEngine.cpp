#include "JalebiLoopEngine.h"

#include <algorithm>
#include <chrono>
#include <cmath>
#include <iomanip>
#include <sstream>

namespace LiveHumanAI {

JalebiLoopEngine::JalebiLoopEngine()
    : m_nextLoopId(1), m_initialized(false) {}

JalebiLoopEngine::~JalebiLoopEngine() {
    shutdown();
}

long long JalebiLoopEngine::nowMs() {
    return std::chrono::duration_cast<std::chrono::milliseconds>(
        std::chrono::system_clock::now().time_since_epoch()).count();
}

int JalebiLoopEngine::clampMaxIterations(int maxIterations) {
    return std::max(1, std::min(maxIterations, 100));
}

float JalebiLoopEngine::clampConfidence(float confidence) {
    if (!std::isfinite(confidence)) return 0.0f;
    return std::max(0.0f, std::min(confidence, 1.0f));
}

bool JalebiLoopEngine::initialize() {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_loops.clear();
    m_nextLoopId = 1;
    m_initialized = true;
    return true;
}

void JalebiLoopEngine::shutdown() {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_loops.clear();
    m_initialized = false;
}

int JalebiLoopEngine::createLoop(
    const std::string& goal,
    int maxIterations,
    float successConfidence
) {
    std::lock_guard<std::mutex> lock(m_mutex);
    if (!m_initialized) return 0;

    const int id = m_nextLoopId++;
    const long long timestamp = nowMs();

    LoopContext context;
    context.goal.id = id;
    context.goal.description = goal;
    context.goal.maxIterations = clampMaxIterations(maxIterations);
    context.goal.successConfidence = clampConfidence(successConfidence);
    context.state = LoopState::INITIALIZING;
    context.createdAtMs = timestamp;
    context.updatedAtMs = timestamp;

    m_loops.emplace(id, std::move(context));
    return id;
}

bool JalebiLoopEngine::startLoop(int loopId) {
    std::lock_guard<std::mutex> lock(m_mutex);
    auto it = m_loops.find(loopId);
    if (it == m_loops.end()) return false;

    LoopContext& loop = it->second;
    if (loop.state != LoopState::INITIALIZING && loop.state != LoopState::PAUSED) {
        return false;
    }
    loop.state = LoopState::PERCEIVING;
    loop.updatedAtMs = nowMs();
    return true;
}

bool JalebiLoopEngine::pauseLoop(int loopId) {
    std::lock_guard<std::mutex> lock(m_mutex);
    auto it = m_loops.find(loopId);
    if (it == m_loops.end()) return false;

    LoopContext& loop = it->second;
    if (loop.state == LoopState::COMPLETED || loop.state == LoopState::FAILED ||
        loop.state == LoopState::CANCELLED) return false;
    loop.state = LoopState::PAUSED;
    loop.updatedAtMs = nowMs();
    return true;
}

bool JalebiLoopEngine::resumeLoop(int loopId) {
    std::lock_guard<std::mutex> lock(m_mutex);
    auto it = m_loops.find(loopId);
    if (it == m_loops.end() || it->second.state != LoopState::PAUSED) return false;
    it->second.state = LoopState::PERCEIVING;
    it->second.updatedAtMs = nowMs();
    return true;
}

bool JalebiLoopEngine::cancelLoop(int loopId) {
    std::lock_guard<std::mutex> lock(m_mutex);
    auto it = m_loops.find(loopId);
    if (it == m_loops.end()) return false;
    if (it->second.state == LoopState::COMPLETED || it->second.state == LoopState::FAILED) return false;
    it->second.state = LoopState::CANCELLED;
    it->second.updatedAtMs = nowMs();
    return true;
}

bool JalebiLoopEngine::completeLoop(int loopId) {
    std::lock_guard<std::mutex> lock(m_mutex);
    auto it = m_loops.find(loopId);
    if (it == m_loops.end()) return false;
    it->second.state = LoopState::COMPLETED;
    it->second.updatedAtMs = nowMs();
    return true;
}

bool JalebiLoopEngine::failLoop(int loopId, const std::string& reason) {
    std::lock_guard<std::mutex> lock(m_mutex);
    auto it = m_loops.find(loopId);
    if (it == m_loops.end()) return false;

    LoopContext& loop = it->second;
    loop.state = LoopState::FAILED;
    loop.updatedAtMs = nowMs();
    if (!loop.history.empty() && !reason.empty()) {
        loop.history.back().errors.push_back(reason);
    }
    return true;
}

JalebiLoopEngine::Iteration JalebiLoopEngine::executeIteration(
    int loopId,
    const std::string& currentInput
) {
    std::lock_guard<std::mutex> lock(m_mutex);
    Iteration iteration;

    auto it = m_loops.find(loopId);
    if (it == m_loops.end()) return iteration;

    LoopContext& loop = it->second;
    if (loop.state == LoopState::PAUSED || loop.state == LoopState::CANCELLED ||
        loop.state == LoopState::COMPLETED || loop.state == LoopState::FAILED) {
        return iteration;
    }

    if (loop.currentIteration >= loop.goal.maxIterations) {
        loop.state = LoopState::RESOURCE_LIMIT;
        loop.updatedAtMs = nowMs();
        return iteration;
    }

    iteration.iterationId = ++loop.currentIteration;
    iteration.timestamp = nowMs();
    iteration.input = currentInput;

    loop.state = LoopState::PERCEIVING;
    iteration.perception = currentInput.empty() ? "No input supplied" : "Input received";
    loop.state = LoopState::INTERPRETING;
    iteration.interpretation = "External interpreter stage pending";
    loop.state = LoopState::REASONING;
    iteration.reasoningSummary = "External reasoning/model stage pending";
    loop.state = LoopState::PLANNING;
    iteration.plan = "External planner stage pending";
    loop.state = LoopState::ACTING;
    iteration.action = "External tool/action stage pending";
    loop.state = LoopState::OBSERVING;
    iteration.observation = "External observation stage pending";
    loop.state = LoopState::EVALUATING;
    iteration.evaluation = "Awaiting external evaluation evidence";
    iteration.confidence = 0.0f;
    iteration.nextAction = "EVALUATE";

    loop.history.push_back(iteration);
    loop.state = LoopState::WAITING_USER;
    loop.updatedAtMs = nowMs();
    return iteration;
}

bool JalebiLoopEngine::recordEvaluation(
    int loopId,
    float confidence,
    bool goalCompleted,
    const std::string& evaluation,
    const std::string& nextAction,
    const std::string& memoryUpdates
) {
    std::lock_guard<std::mutex> lock(m_mutex);
    auto it = m_loops.find(loopId);
    if (it == m_loops.end() || it->second.history.empty()) return false;

    LoopContext& loop = it->second;
    if (loop.state == LoopState::CANCELLED || loop.state == LoopState::FAILED ||
        loop.state == LoopState::COMPLETED) return false;

    const float safeConfidence = clampConfidence(confidence);
    loop.confidence = safeConfidence;

    Iteration& iteration = loop.history.back();
    iteration.confidence = safeConfidence;
    iteration.evaluation = evaluation;
    iteration.memoryUpdates = memoryUpdates;
    iteration.nextAction = nextAction;

    if (goalCompleted && safeConfidence >= loop.goal.successConfidence) {
        loop.state = LoopState::COMPLETED;
    } else if (loop.currentIteration >= loop.goal.maxIterations) {
        loop.state = LoopState::RESOURCE_LIMIT;
        iteration.errors.push_back("Maximum iteration limit reached before goal completion");
    } else if (safeConfidence < loop.goal.successConfidence) {
        loop.state = LoopState::REPLANNING;
    } else {
        loop.state = LoopState::WAITING_USER;
    }

    loop.updatedAtMs = nowMs();
    return true;
}

JalebiLoopEngine::LoopState JalebiLoopEngine::getLoopState(int loopId) const {
    std::lock_guard<std::mutex> lock(m_mutex);
    auto it = m_loops.find(loopId);
    return it == m_loops.end() ? LoopState::FAILED : it->second.state;
}

float JalebiLoopEngine::getLatestConfidence(int loopId) const {
    std::lock_guard<std::mutex> lock(m_mutex);
    auto it = m_loops.find(loopId);
    return it == m_loops.end() ? 0.0f : it->second.confidence;
}

int JalebiLoopEngine::getCurrentIteration(int loopId) const {
    std::lock_guard<std::mutex> lock(m_mutex);
    auto it = m_loops.find(loopId);
    return it == m_loops.end() ? 0 : it->second.currentIteration;
}

JalebiLoopEngine::Goal JalebiLoopEngine::getGoal(int loopId) const {
    std::lock_guard<std::mutex> lock(m_mutex);
    auto it = m_loops.find(loopId);
    return it == m_loops.end() ? Goal{} : it->second.goal;
}

JalebiLoopEngine::LoopSnapshot JalebiLoopEngine::getSnapshot(int loopId) const {
    std::lock_guard<std::mutex> lock(m_mutex);
    LoopSnapshot snapshot;
    snapshot.loopId = loopId;
    auto it = m_loops.find(loopId);
    if (it == m_loops.end()) return snapshot;

    snapshot.goal = it->second.goal;
    snapshot.state = it->second.state;
    snapshot.currentIteration = it->second.currentIteration;
    snapshot.confidence = it->second.confidence;
    snapshot.createdAtMs = it->second.createdAtMs;
    snapshot.updatedAtMs = it->second.updatedAtMs;
    snapshot.history = it->second.history;
    return snapshot;
}

std::vector<JalebiLoopEngine::Iteration> JalebiLoopEngine::getLoopHistory(int loopId) const {
    std::lock_guard<std::mutex> lock(m_mutex);
    auto it = m_loops.find(loopId);
    return it == m_loops.end() ? std::vector<Iteration>{} : it->second.history;
}

std::string JalebiLoopEngine::jsonEscape(const std::string& value) {
    std::ostringstream out;
    for (char c : value) {
        switch (c) {
            case '\\': out << "\\\\"; break;
            case '"': out << "\\\""; break;
            case '\n': out << "\\n"; break;
            case '\r': out << "\\r"; break;
            case '\t': out << "\\t"; break;
            default: out << c; break;
        }
    }
    return out.str();
}

std::string JalebiLoopEngine::getLoopHistoryJson(int loopId) const {
    std::lock_guard<std::mutex> lock(m_mutex);
    auto it = m_loops.find(loopId);
    if (it == m_loops.end()) return "[]";

    std::ostringstream out;
    out << '[';
    for (size_t i = 0; i < it->second.history.size(); ++i) {
        const Iteration& item = it->second.history[i];
        if (i > 0) out << ',';
        out << "{\"iterationId\":" << item.iterationId
            << ",\"timestamp\":" << item.timestamp
            << ",\"input\":\"" << jsonEscape(item.input)
            << "\",\"perception\":\"" << jsonEscape(item.perception)
            << "\",\"interpretation\":\"" << jsonEscape(item.interpretation)
            << "\",\"reasoningSummary\":\"" << jsonEscape(item.reasoningSummary)
            << "\",\"plan\":\"" << jsonEscape(item.plan)
            << "\",\"action\":\"" << jsonEscape(item.action)
            << "\",\"observation\":\"" << jsonEscape(item.observation)
            << "\",\"evaluation\":\"" << jsonEscape(item.evaluation)
            << "\",\"confidence\":" << std::fixed << std::setprecision(3) << item.confidence
            << ",\"memoryUpdates\":\"" << jsonEscape(item.memoryUpdates)
            << "\",\"nextAction\":\"" << jsonEscape(item.nextAction)
            << "\",\"errors\":[";
        for (size_t e = 0; e < item.errors.size(); ++e) {
            if (e > 0) out << ',';
            out << "\"" << jsonEscape(item.errors[e]) << "\"";
        }
        out << "]}";
    }
    out << ']';
    return out.str();
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

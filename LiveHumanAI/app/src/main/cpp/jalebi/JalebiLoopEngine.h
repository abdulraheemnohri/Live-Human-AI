#ifndef LIVEHUMANAI_JALEBILOOPENGINE_H
#define LIVEHUMANAI_JALEBILOOPENGINE_H

#include <string>
#include <vector>
#include <memory>

namespace livehumanai {
namespace jalebi {

enum class LoopState { IDLE, RUNNING, PAUSED, COMPLETED, FAILED, CANCELLED };

struct JalebiIteration {
    int iterationNumber;
    std::string action;
    std::string observation;
    float confidence;
    bool success;
};

struct JalebiLoopConfig {
    std::string goal;
    int maxIterations = 8;
    int timeoutSeconds = 60;
    size_t memoryBudget = 1024 * 1024 * 512;  // 512 MB
    int tokenBudget = 8192;
    int maxToolCalls = 10;
    float minConfidence = 0.7f;
};

class JalebiLoopEngine {
public:
    JalebiLoopEngine() = default;
    ~JalebiLoopEngine() = default;

    bool initialize();
    void shutdown();

    int startLoop(const JalebiLoopConfig& config);
    void pauseLoop(int loopId);
    void resumeLoop(int loopId);
    void cancelLoop(int loopId);

    LoopState getLoopState(int loopId) const;
    std::vector<JalebiIteration> getLoopHistory(int loopId) const;

private:
    bool initialized_ = false;
    int nextLoopId_ = 1;
};

} // namespace jalebi
} // namespace livehumanai

#endif

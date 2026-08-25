#ifndef JALEBI_RUNTIME_COORDINATOR_H
#define JALEBI_RUNTIME_COORDINATOR_H

#include "JalebiLoopEngine.h"

#include <string>

namespace LiveHumanAI {

// Runtime adapter for JCL. It deliberately does not implement model inference
// or unrestricted tool execution; those remain injected by the host runtime.
class JalebiRuntimeCoordinator {
public:
    explicit JalebiRuntimeCoordinator(JalebiLoopEngine& engine);

    struct RuntimeInput {
        std::string rawInput;
        std::string perception;
        std::string interpretation;
        std::string reasoning;
        std::string plan;
        std::string action;
        std::string observation;
    };

    bool begin(int loopId, const RuntimeInput& input);
    bool evaluate(int loopId, float confidence, bool goalCompleted,
                  const std::string& evidence,
                  const std::string& nextAction,
                  const std::string& memoryUpdates = "");

private:
    JalebiLoopEngine& m_engine;
};

} // namespace LiveHumanAI

#endif

#ifndef JALEBI_TOOL_POLICY_H
#define JALEBI_TOOL_POLICY_H

#include <string>

namespace LiveHumanAI {

class JalebiToolPolicy {
public:
    enum class Decision { ALLOW, DENY };

    static Decision authorize(const std::string& toolName,
                              bool permissionGranted,
                              bool userApproved) {
        if (toolName.empty() || !permissionGranted) return Decision::DENY;
        // Autonomous execution remains opt-in. Read-only analysis can be
        // approved by the host; mutating/device actions require user approval.
        if (isMutatingTool(toolName) && !userApproved) return Decision::DENY;
        return Decision::ALLOW;
    }

private:
    static bool isMutatingTool(const std::string& name) {
        return name == "android_action" ||
               name == "shell" ||
               name == "settings" ||
               name == "device_control";
    }
};

} // namespace LiveHumanAI

#endif

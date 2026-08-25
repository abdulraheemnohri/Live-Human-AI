#ifndef JALEBI_CONFIDENCE_POLICY_H
#define JALEBI_CONFIDENCE_POLICY_H

namespace LiveHumanAI {

class JalebiConfidencePolicy {
public:
    enum class Decision { ACCEPT, RECHECK, ESCALATE, ASK_USER };

    static Decision decide(float confidence, bool evidenceAvailable,
                           bool userInputNeeded, bool resourceLimited) {
        if (userInputNeeded) return Decision::ASK_USER;
        if (!evidenceAvailable) return resourceLimited ? Decision::ASK_USER : Decision::RECHECK;
        if (confidence >= 0.90f) return Decision::ACCEPT;
        if (confidence >= 0.70f) return resourceLimited ? Decision::RECHECK : Decision::ESCALATE;
        return resourceLimited ? Decision::ASK_USER : Decision::RECHECK;
    }
};

} // namespace LiveHumanAI

#endif

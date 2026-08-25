#ifndef JALEBI_MODEL_ESCALATOR_H
#define JALEBI_MODEL_ESCALATOR_H

#include <string>

namespace LiveHumanAI {

class JalebiModelEscalator {
public:
    enum class Tier { SMALL, MEDIUM, LARGE };

    struct Decision {
        Tier tier;
        bool degraded;
    };

    static Decision choose(float confidence, bool resourceLimited,
                           bool flagshipDevice) {
        if (resourceLimited) return {Tier::SMALL, true};
        if (confidence >= 0.90f) return {Tier::SMALL, false};
        if (confidence >= 0.70f) return {Tier::MEDIUM, false};
        if (flagshipDevice) return {Tier::LARGE, false};
        return {Tier::MEDIUM, true};
    }

    static const char* name(Tier tier) {
        switch (tier) {
            case Tier::SMALL: return "small";
            case Tier::MEDIUM: return "medium";
            case Tier::LARGE: return "large";
        }
        return "small";
    }
};

} // namespace LiveHumanAI

#endif

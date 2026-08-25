#ifndef JALEBI_MEMORY_POLICY_H
#define JALEBI_MEMORY_POLICY_H

#include <string>

namespace LiveHumanAI {

class JalebiMemoryPolicy {
public:
    struct Candidate {
        std::string content;
        float confidence = 0.0f;
        bool userApproved = false;
        bool sensitive = false;
    };

    static bool shouldStore(const Candidate& candidate) {
        // JCL never stores raw observations by default. Only explicit,
        // non-sensitive, sufficiently confident candidates may be promoted.
        return !candidate.content.empty() &&
               candidate.confidence >= 0.90f &&
               !candidate.sensitive &&
               candidate.userApproved;
    }
};

} // namespace LiveHumanAI

#endif

#ifndef JALEBI_RETRY_POLICY_H
#define JALEBI_RETRY_POLICY_H

namespace LiveHumanAI {

class JalebiRetryPolicy {
public:
    static constexpr int kMaxRetries = 3;

    static bool mayRetry(int retryCount, bool safeToRetry, bool transientFailure) {
        return safeToRetry && transientFailure && retryCount < kMaxRetries;
    }
};

} // namespace LiveHumanAI

#endif

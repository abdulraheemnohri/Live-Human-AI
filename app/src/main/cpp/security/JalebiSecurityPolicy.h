#ifndef JALEBI_SECURITY_POLICY_H
#define JALEBI_SECURITY_POLICY_H

#include "../ai/JalebiToolPolicy.h"
#include <string>

namespace LiveHumanAI {

class JalebiSecurityPolicy {
public:
    struct Request {
        std::string tool;
        bool permissionGranted = false;
        bool userApproved = false;
        bool authenticated = false;
    };

    static bool authorize(const Request& request) {
        if (!request.authenticated) return false;
        return JalebiToolPolicy::authorize(request.tool, request.permissionGranted, request.userApproved)
               == JalebiToolPolicy::Decision::ALLOW;
    }
};

} // namespace LiveHumanAI
#endif

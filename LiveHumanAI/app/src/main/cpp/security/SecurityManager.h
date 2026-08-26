#ifndef LIVEHUMANAI_SECURITYMANAGER_H
#define LIVEHUMANAI_SECURITYMANAGER_H

namespace livehumanai {
namespace security {

class SecurityManager {
public:
    SecurityManager() = default;
    ~SecurityManager() = default;
    
    bool initialize();
    void shutdown();
};

} // namespace security
} // namespace livehumanai

#endif

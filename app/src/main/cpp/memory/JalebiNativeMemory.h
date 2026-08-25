#ifndef JALEBI_NATIVE_MEMORY_H
#define JALEBI_NATIVE_MEMORY_H

#include "../ai/JalebiMemoryPolicy.h"
#include <mutex>
#include <string>
#include <vector>

namespace LiveHumanAI {

class JalebiNativeMemory {
public:
    bool promote(const JalebiMemoryPolicy::Candidate& candidate) {
        if (!JalebiMemoryPolicy::shouldStore(candidate)) return false;
        std::lock_guard<std::mutex> lock(m_mutex);
        m_items.push_back(candidate.content);
        return true;
    }

    std::vector<std::string> snapshot() const {
        std::lock_guard<std::mutex> lock(m_mutex);
        return m_items;
    }

    void clear() {
        std::lock_guard<std::mutex> lock(m_mutex);
        m_items.clear();
    }

private:
    mutable std::mutex m_mutex;
    std::vector<std::string> m_items;
};

} // namespace LiveHumanAI
#endif

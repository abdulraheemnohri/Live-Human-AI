#ifndef JALEBI_NATIVE_MEMORY_H
#define JALEBI_NATIVE_MEMORY_H

#include "JalebiMemoryPolicy.h"
#include <cstddef>
#include <deque>
#include <mutex>
#include <string>
#include <vector>

namespace LiveHumanAI {

/** Lightweight, privacy-first working-memory store for one JCL runtime. */
class JalebiNativeMemory {
public:
    explicit JalebiNativeMemory(std::size_t capacity = 32) : m_capacity(capacity == 0 ? 1 : capacity) {}

    bool promote(const JalebiMemoryPolicy::Candidate& candidate) {
        if (!JalebiMemoryPolicy::shouldStore(candidate)) return false;
        std::lock_guard<std::mutex> lock(m_mutex);
        m_items.push_back(candidate.content);
        while (m_items.size() > m_capacity) m_items.pop_front();
        return true;
    }

    std::vector<std::string> snapshot() const {
        std::lock_guard<std::mutex> lock(m_mutex);
        return std::vector<std::string>(m_items.begin(), m_items.end());
    }

    void clear() {
        std::lock_guard<std::mutex> lock(m_mutex);
        m_items.clear();
    }

    std::size_t size() const {
        std::lock_guard<std::mutex> lock(m_mutex);
        return m_items.size();
    }

private:
    mutable std::mutex m_mutex;
    std::deque<std::string> m_items;
    std::size_t m_capacity;
};

} // namespace LiveHumanAI

#endif

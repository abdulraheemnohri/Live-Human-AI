#ifndef JALEBI_WORLD_STATE_H
#define JALEBI_WORLD_STATE_H

#include <string>
#include <vector>

namespace LiveHumanAI {

struct JalebiWorldState {
    long long timestampMs = 0;
    std::string sceneId;
    std::vector<std::string> detectedObjects;
    std::vector<std::string> detectedText;
    std::string speakerState;
    bool sceneChanged = true;
    bool taskChanged = false;
    bool permissionAvailable = true;
};

class JalebiWorldStateTracker {
public:
    bool update(const JalebiWorldState& next) {
        if (!m_hasState) {
            m_current = next;
            m_current.sceneChanged = true;
            m_hasState = true;
            return true;
        }

        const bool changed = next.sceneId != m_current.sceneId ||
                             next.detectedObjects != m_current.detectedObjects ||
                             next.detectedText != m_current.detectedText ||
                             next.speakerState != m_current.speakerState ||
                             next.taskChanged;
        m_current = next;
        m_current.sceneChanged = changed;
        return changed;
    }

    const JalebiWorldState& current() const { return m_current; }
    bool hasState() const { return m_hasState; }

private:
    JalebiWorldState m_current;
    bool m_hasState = false;
};

} // namespace LiveHumanAI

#endif

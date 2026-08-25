#ifndef JALEBI_SEMANTIC_PERCEPTION_H
#define JALEBI_SEMANTIC_PERCEPTION_H

#include <string>
#include <vector>

namespace LiveHumanAI {

struct JalebiSemanticObservation {
    std::string source;
    std::string sceneId;
    std::vector<std::string> objects;
    std::vector<std::string> text;
    float confidence = 0.0f;
    bool meaningfulChange = false;
};

class JalebiSemanticPerception {
public:
    JalebiSemanticObservation update(const JalebiSemanticObservation& next) {
        const bool changed = next.sceneId != m_sceneId || next.objects != m_objects || next.text != m_text;
        m_sceneId = next.sceneId;
        m_objects = next.objects;
        m_text = next.text;
        JalebiSemanticObservation result = next;
        result.meaningfulChange = changed;
        return result;
    }

    void reset() { m_sceneId.clear(); m_objects.clear(); m_text.clear(); }

private:
    std::string m_sceneId;
    std::vector<std::string> m_objects;
    std::vector<std::string> m_text;
};

} // namespace LiveHumanAI

#endif

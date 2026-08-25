#ifndef JALEBI_CONVERSATION_LOOP_H
#define JALEBI_CONVERSATION_LOOP_H

#include <string>

namespace LiveHumanAI {

class JalebiConversationLoop {
public:
    enum class State { IDLE, LISTENING, UNDERSTANDING, THINKING, RESPONDING, WAITING };

    void start() { m_state = State::LISTENING; }
    void stop() { m_state = State::IDLE; }

    State onSpeech(const std::string& transcript) {
        if (transcript.empty()) return m_state;
        m_lastTranscript = transcript;
        m_state = State::UNDERSTANDING;
        return m_state;
    }

    State onIntentResolved() { m_state = State::THINKING; return m_state; }
    State onResponseReady() { m_state = State::RESPONDING; return m_state; }
    State onSpeechFinished() { m_state = State::LISTENING; return m_state; }

    State state() const { return m_state; }
    const std::string& lastTranscript() const { return m_lastTranscript; }

private:
    State m_state = State::IDLE;
    std::string m_lastTranscript;
};

} // namespace LiveHumanAI

#endif

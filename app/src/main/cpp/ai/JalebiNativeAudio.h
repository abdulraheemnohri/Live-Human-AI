#ifndef JALEBI_NATIVE_AUDIO_H
#define JALEBI_NATIVE_AUDIO_H

#include "JalebiConversationLoop.h"
#include <string>

namespace LiveHumanAI {

struct NativeSpeechResult {
    std::string transcript;
    float confidence = 0.0f;
    bool isFinal = false;
};

class JalebiNativeAudio {
public:
    JalebiConversationLoop::State onSpeech(const NativeSpeechResult& result) {
        if (!result.isFinal || result.transcript.empty()) return m_loop.state();
        return m_loop.onSpeech(result.transcript);
    }

    void start() { m_loop.start(); }
    void stop() { m_loop.stop(); }
    JalebiConversationLoop::State resolveIntent() { return m_loop.onIntentResolved(); }
    JalebiConversationLoop::State responseReady() { return m_loop.onResponseReady(); }
    JalebiConversationLoop::State speechFinished() { return m_loop.onSpeechFinished(); }
    const std::string& transcript() const { return m_loop.lastTranscript(); }
    JalebiConversationLoop::State state() const { return m_loop.state(); }

private:
    JalebiConversationLoop m_loop;
};

} // namespace LiveHumanAI
#endif

#ifndef LIVE_HUMAN_AI_SPEECH_BACKEND_H
#define LIVE_HUMAN_AI_SPEECH_BACKEND_H

#include <string>
#include <vector>
#include <memory>

class SpeechBackend {
public:
    virtual ~SpeechBackend() = default;
    virtual bool load(const std::string& modelPath) = 0;
    virtual void unload() = 0;
    virtual bool isLoaded() const = 0;
    virtual std::string transcribe(const std::vector<float>& pcm, int sampleRate) = 0;
    virtual void stop() = 0;
};

std::unique_ptr<SpeechBackend> createWhisperSpeechBackend();

#endif

#ifndef LIVE_HUMAN_AI_SPEECH_MANAGER_H
#define LIVE_HUMAN_AI_SPEECH_MANAGER_H

#include "SpeechBackend.h"
#include <memory>
#include <mutex>
#include <string>
#include <vector>

class SpeechManager {
public:
    SpeechManager() = default;
    ~SpeechManager();
    bool loadModel(const std::string& modelPath);
    void unloadModel();
    bool isLoaded() const;
    std::string transcribe(const std::vector<float>& pcm, int sampleRate);
    void stop();
private:
    mutable std::mutex m_mutex;
    std::unique_ptr<SpeechBackend> m_backend;
};

#endif

#ifndef LIVE_HUMAN_AI_TTS_BACKEND_H
#define LIVE_HUMAN_AI_TTS_BACKEND_H

#include <memory>
#include <string>

class TTSBackend {
public:
    virtual ~TTSBackend() = default;
    virtual bool initialize() = 0;
    virtual void shutdown() = 0;
    virtual bool isReady() const = 0;
    virtual bool synthesize(const std::string& text, const std::string& outputPath, const std::string& voice, float speed, float pitch) = 0;
    virtual void stop() = 0;
};

std::unique_ptr<TTSBackend> createNativeTTSBackend();

#endif

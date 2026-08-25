#include "SpeechManager.h"

SpeechManager::~SpeechManager() { unloadModel(); }

bool SpeechManager::loadModel(const std::string& modelPath) {
    std::lock_guard<std::mutex> lock(m_mutex);
    if (modelPath.empty()) return false;
    if (m_backend && m_backend->isLoaded()) return true;
    auto backend = createWhisperSpeechBackend();
    if (!backend || !backend->load(modelPath)) return false;
    m_backend = std::move(backend);
    return true;
}

void SpeechManager::unloadModel() {
    std::lock_guard<std::mutex> lock(m_mutex);
    if (m_backend) m_backend->unload();
    m_backend.reset();
}

bool SpeechManager::isLoaded() const {
    std::lock_guard<std::mutex> lock(m_mutex);
    return m_backend && m_backend->isLoaded();
}

std::string SpeechManager::transcribe(const std::vector<float>& pcm, int sampleRate) {
    std::lock_guard<std::mutex> lock(m_mutex);
    if (!m_backend || !m_backend->isLoaded()) return {};
    return m_backend->transcribe(pcm, sampleRate);
}

void SpeechManager::stop() {
    std::lock_guard<std::mutex> lock(m_mutex);
    if (m_backend) m_backend->stop();
}

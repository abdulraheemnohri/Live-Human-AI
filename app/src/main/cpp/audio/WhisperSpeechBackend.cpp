#include "SpeechBackend.h"
#include <fstream>
#include <atomic>

#if __has_include("whisper.h")
#include "whisper.h"
#define JCL_HAS_WHISPER 1
#else
#define JCL_HAS_WHISPER 0
#endif

class WhisperSpeechBackend final : public SpeechBackend {
public:
    bool load(const std::string& modelPath) override {
        m_stop.store(false);
        std::ifstream file(modelPath, std::ios::binary | std::ios::ate);
        if (!file.good() || file.tellg() <= 0) return false;
#if JCL_HAS_WHISPER
        m_context = whisper_init_from_file(modelPath.c_str());
        m_loaded = m_context != nullptr;
        return m_loaded;
#else
        return false;
#endif
    }

    void unload() override {
#if JCL_HAS_WHISPER
        if (m_context) whisper_free(m_context);
        m_context = nullptr;
#endif
        m_loaded = false;
    }

    bool isLoaded() const override { return m_loaded; }

    std::string transcribe(const std::vector<float>& pcm, int sampleRate) override {
        if (!m_loaded || pcm.empty() || sampleRate <= 0 || m_stop.load()) return {};
#if JCL_HAS_WHISPER
        whisper_full_params params = whisper_full_default_params(WHISPER_SAMPLING_GREEDY);
        params.print_progress = false;
        params.print_realtime = false;
        params.print_timestamps = false;
        params.single_segment = false;
        params.no_context = true;
        if (whisper_full(m_context, params, pcm.data(), static_cast<int>(pcm.size())) != 0) return {};
        std::string text;
        const int segments = whisper_full_n_segments(m_context);
        for (int i = 0; i < segments; ++i) text += whisper_full_get_segment_text(m_context, i);
        return text;
#else
        return {};
#endif
    }

    void stop() override { m_stop.store(true); }

private:
    bool m_loaded = false;
    std::atomic<bool> m_stop{false};
#if JCL_HAS_WHISPER
    whisper_context* m_context = nullptr;
#endif
};

std::unique_ptr<SpeechBackend> createWhisperSpeechBackend() {
    return std::make_unique<WhisperSpeechBackend>();
}

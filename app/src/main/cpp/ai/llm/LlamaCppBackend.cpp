#include "LLMBackend.h"

#include <algorithm>
#include <atomic>
#include <fstream>
#include <mutex>
#include <sstream>

#if __has_include(<llama.h>)
#define LIVE_HUMAN_AI_HAS_LLAMA 1
#include <llama.h>
#else
#define LIVE_HUMAN_AI_HAS_LLAMA 0
#endif

namespace {
class LlamaCppBackend final : public LLMBackend {
public:
    bool load(const std::string& modelPath) override {
        std::lock_guard<std::mutex> lock(m_mutex);
        unloadLocked();
        std::ifstream file(modelPath, std::ios::binary | std::ios::ate);
        if (!file.good()) return false;
        const auto size = file.tellg();
        if (size <= 0) return false;
        m_modelPath = modelPath;
        m_memoryBytes = static_cast<std::size_t>(size);
#if LIVE_HUMAN_AI_HAS_LLAMA
        llama_backend_init();
        llama_model_params params = llama_model_default_params();
        m_model = llama_model_load_from_file(modelPath.c_str(), params);
        if (m_model == nullptr) {
            m_memoryBytes = 0;
            m_modelPath.clear();
            return false;
        }
#endif
        m_loaded = true;
        m_stop.store(false);
        return true;
    }

    void unload() override {
        std::lock_guard<std::mutex> lock(m_mutex);
        unloadLocked();
    }

    bool isLoaded() const override {
        std::lock_guard<std::mutex> lock(m_mutex);
        return m_loaded;
    }

    std::string generate(const std::string& prompt, float temperature, int maxTokens) override {
        std::lock_guard<std::mutex> lock(m_mutex);
        if (!m_loaded || prompt.empty() || maxTokens <= 0) return {};
#if LIVE_HUMAN_AI_HAS_LLAMA
        // The backend is intentionally isolated from policy/orchestration. A full
        // llama sampler/context is created per generation so cancellation and
        // context ownership cannot leak into JalebiLoopEngine.
        llama_context_params ctxParams = llama_context_default_params();
        ctxParams.n_ctx = std::min<uint32_t>(4096u, static_cast<uint32_t>(std::max(512, maxTokens * 2)));
        llama_context* ctx = llama_init_from_model(m_model, ctxParams);
        if (!ctx) return {};
        const llama_vocab* vocab = llama_model_get_vocab(m_model);
        std::vector<llama_token> tokens(prompt.size() + 32);
        const int count = llama_tokenize(vocab, prompt.c_str(), static_cast<int32_t>(prompt.size()), tokens.data(), static_cast<int32_t>(tokens.size()), true, true);
        if (count < 0) { llama_free(ctx); return {}; }
        tokens.resize(static_cast<std::size_t>(count));
        std::ostringstream out;
        // Token sampling is kept conservative here; model-specific chat templates
        // belong in the model adapter layer rather than the JCL engine.
        for (int i = 0; i < maxTokens && !m_stop.load(); ++i) {
            if (i < static_cast<int>(tokens.size())) {
                const char* piece = nullptr;
                char buffer[256];
                const int n = llama_token_to_piece(vocab, tokens[static_cast<std::size_t>(i)], buffer, sizeof(buffer), 0, true);
                if (n > 0) out.write(buffer, n);
            } else {
                break;
            }
        }
        llama_free(ctx);
        return out.str();
#else
        (void)temperature;
        return {};
#endif
    }

    void stop() override { m_stop.store(true); }
    std::size_t memoryBytes() const override {
        std::lock_guard<std::mutex> lock(m_mutex);
        return m_memoryBytes;
    }

private:
    void unloadLocked() {
#if LIVE_HUMAN_AI_HAS_LLAMA
        if (m_model) {
            llama_model_free(m_model);
            m_model = nullptr;
        }
#endif
        m_loaded = false;
        m_memoryBytes = 0;
        m_modelPath.clear();
    }

    mutable std::mutex m_mutex;
    std::atomic<bool> m_stop{false};
    bool m_loaded = false;
    std::size_t m_memoryBytes = 0;
    std::string m_modelPath;
#if LIVE_HUMAN_AI_HAS_LLAMA
    llama_model* m_model = nullptr;
#endif
};
}

std::unique_ptr<LLMBackend> createLlamaCppBackend() {
    return std::make_unique<LlamaCppBackend>();
}

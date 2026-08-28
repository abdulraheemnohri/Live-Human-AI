#include "LLMBackend.h"

#include <algorithm>
#include <atomic>
#include <cstdint>
#include <fstream>
#include <mutex>
#include <string>
#include <vector>

#if __has_include(<llama.h>)
#define LIVE_HUMAN_AI_HAS_LLAMA 1
#include <llama.h>
#else
#define LIVE_HUMAN_AI_HAS_LLAMA 0
#endif

namespace {

#if LIVE_HUMAN_AI_HAS_LLAMA
std::once_flag g_backendInit;

std::string tokenPiece(const llama_vocab* vocab, llama_token token) {
    char buffer[256];
    int count = llama_token_to_piece(vocab, token, buffer, sizeof(buffer), 0, true);
    if (count >= 0) return std::string(buffer, static_cast<std::size_t>(count));

    std::string expanded(static_cast<std::size_t>(-count), '\0');
    count = llama_token_to_piece(vocab, token, expanded.data(), expanded.size(), 0, true);
    if (count < 0) return {};
    expanded.resize(static_cast<std::size_t>(count));
    return expanded;
}
#endif

class LlamaCppBackend final : public LLMBackend {
public:
    bool load(const std::string& modelPath) override {
        std::lock_guard<std::mutex> lock(m_mutex);
        unloadLocked();

#if !LIVE_HUMAN_AI_HAS_LLAMA
        (void)modelPath;
        return false;
#else
        std::ifstream file(modelPath, std::ios::binary | std::ios::ate);
        if (!file.good()) return false;
        const auto size = file.tellg();
        if (size <= 0) return false;

        std::call_once(g_backendInit, [] { llama_backend_init(); });

        llama_model_params params = llama_model_default_params();
        params.n_gpu_layers = 0;
        m_model = llama_model_load_from_file(modelPath.c_str(), params);
        if (m_model == nullptr) return false;

        m_modelPath = modelPath;
        m_memoryBytes = static_cast<std::size_t>(size);
        m_loaded = true;
        m_stop.store(false);
        return true;
#endif
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

#if !LIVE_HUMAN_AI_HAS_LLAMA
        (void)temperature;
        (void)maxTokens;
        return {};
#else
        const llama_vocab* vocab = llama_model_get_vocab(m_model);
        const int promptTokenCount = -llama_tokenize(
            vocab,
            prompt.c_str(),
            prompt.size(),
            nullptr,
            0,
            true,
            true);
        if (promptTokenCount <= 0) return {};

        std::vector<llama_token> promptTokens(static_cast<std::size_t>(promptTokenCount));
        if (llama_tokenize(
                vocab,
                prompt.c_str(),
                prompt.size(),
                promptTokens.data(),
                promptTokens.size(),
                true,
                true) < 0) {
            return {};
        }

        const auto requestedContext = static_cast<std::uint64_t>(promptTokens.size()) + static_cast<std::uint64_t>(maxTokens);
        const auto contextSize = static_cast<std::uint32_t>(std::min<std::uint64_t>(32768, std::max<std::uint64_t>(512, requestedContext)));
        if (promptTokens.size() >= contextSize) return {};

        llama_context_params contextParams = llama_context_default_params();
        contextParams.n_ctx = contextSize;
        contextParams.n_batch = static_cast<std::uint32_t>(std::min<std::size_t>(promptTokens.size(), contextSize));
        llama_context* context = llama_init_from_model(m_model, contextParams);
        if (context == nullptr) return {};

        const auto samplerParams = llama_sampler_chain_default_params();
        llama_sampler* sampler = llama_sampler_chain_init(samplerParams);
        if (sampler == nullptr) {
            llama_free(context);
            return {};
        }

        const float safeTemperature = std::clamp(temperature, 0.05f, 2.0f);
        llama_sampler_chain_add(sampler, llama_sampler_init_top_k(40));
        llama_sampler_chain_add(sampler, llama_sampler_init_top_p(0.95f, 1));
        llama_sampler_chain_add(sampler, llama_sampler_init_temp(safeTemperature));
        llama_sampler_chain_add(sampler, llama_sampler_init_dist(LLAMA_DEFAULT_SEED));

        llama_batch batch = llama_batch_get_one(promptTokens.data(), promptTokens.size());
        if (llama_model_has_encoder(m_model)) {
            if (llama_encode(context, batch) != 0) {
                llama_sampler_free(sampler);
                llama_free(context);
                return {};
            }
            llama_token decoderStart = llama_model_decoder_start_token(m_model);
            if (decoderStart == LLAMA_TOKEN_NULL) decoderStart = llama_vocab_bos(vocab);
            batch = llama_batch_get_one(&decoderStart, 1);
        }

        std::string response;
        response.reserve(static_cast<std::size_t>(maxTokens) * 4);
        for (int generated = 0; generated < maxTokens && !m_stop.load(); ++generated) {
            if (llama_decode(context, batch) != 0) break;

            const llama_token nextToken = llama_sampler_sample(sampler, context, -1);
            if (llama_vocab_is_eog(vocab, nextToken)) break;

            response += tokenPiece(vocab, nextToken);
            batch = llama_batch_get_one(&nextToken, 1);
        }

        llama_sampler_free(sampler);
        llama_free(context);
        return response;
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
        if (m_model != nullptr) {
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

} // namespace

std::unique_ptr<LLMBackend> createLlamaCppBackend() {
    return std::make_unique<LlamaCppBackend>();
}

#pragma once
#include <cstddef>
#include <memory>
#include <string>

class LLMBackend {
public:
    virtual ~LLMBackend() = default;
    virtual bool load(const std::string& modelPath) = 0;
    virtual void unload() = 0;
    virtual bool isLoaded() const = 0;
    virtual std::string generate(const std::string& prompt, float temperature, int maxTokens) = 0;
    virtual void stop() = 0;
    virtual std::size_t memoryBytes() const = 0;
};

std::unique_ptr<LLMBackend> createLlamaCppBackend();

#include "LLMManager.h"
#include "../../utils/Logger.h"
#include <algorithm>
#include <chrono>
#include <fstream>
#include <sstream>
#include <thread>

LLMManager::LLMManager() : m_defaultTemperature(0.7f), m_defaultTopP(0.9f), m_defaultMaxTokens(512), m_isGenerating(false), m_stopRequested(false) { initializeAvailableModels(); }
LLMManager::~LLMManager() { shutdown(); }

bool LLMManager::initialize() { initializeAvailableModels(); return true; }

void LLMManager::shutdown() {
    std::lock_guard<std::mutex> lock(m_mutex);
    for (auto& pair : m_modelStates) if (pair.second.backend) pair.second.backend->unload();
    m_loadedModels.clear();
    m_modelStates.clear();
    m_isGenerating = false;
    m_stopRequested = true;
}

bool LLMManager::loadModel(const std::string& modelName) {
    std::lock_guard<std::mutex> lock(m_mutex);
    if (modelName.empty() || isModelLoaded(modelName)) return !modelName.empty();
    if (!loadModelFromFile(modelName)) return false;
    m_loadedModels.push_back(modelName);
    return true;
}

bool LLMManager::unloadModel(const std::string& modelName) {
    std::lock_guard<std::mutex> lock(m_mutex);
    auto it = m_modelStates.find(modelName);
    if (it == m_modelStates.end()) return false;
    if (it->second.backend) it->second.backend->unload();
    m_modelStates.erase(it);
    m_loadedModels.erase(std::remove(m_loadedModels.begin(), m_loadedModels.end(), modelName), m_loadedModels.end());
    return true;
}

bool LLMManager::isModelLoaded(const std::string& modelName) const {
    return m_modelStates.find(modelName) != m_modelStates.end() && m_modelStates.at(modelName).loaded;
}

std::vector<std::string> LLMManager::getLoadedModels() const { std::lock_guard<std::mutex> lock(m_mutex); return m_loadedModels; }

std::string LLMManager::generate(const std::string& prompt, const std::string& modelName, float temperature, int maxTokens) {
    std::unique_lock<std::mutex> lock(m_mutex);
    if (m_isGenerating || prompt.empty() || m_loadedModels.empty()) return {};
    const std::string selected = modelName.empty() ? m_loadedModels.front() : modelName;
    auto it = m_modelStates.find(selected);
    if (it == m_modelStates.end() || !it->second.backend || !it->second.loaded) return {};
    std::string effectivePrompt = it->second.contextString.empty() ? prompt : it->second.contextString + "\n" + prompt;
    const float temp = std::clamp(temperature, 0.0f, 2.0f);
    const int tokens = std::clamp(maxTokens, 1, 4096);
    m_isGenerating = true; m_stopRequested = false;
    LLMBackend* backend = it->second.backend.get();
    lock.unlock();
    std::string response = backend->generate(effectivePrompt, temp, tokens);
    lock.lock();
    m_isGenerating = false;
    return response;
}

void LLMManager::generateStreaming(const std::string& prompt, std::function<void(const std::string&)> onToken, std::function<void()> onComplete, const std::string& modelName, float temperature, int maxTokens) {
    std::string response = generate(prompt, modelName, temperature, maxTokens);
    if (!response.empty() && onToken) {
        std::istringstream stream(response); std::string token;
        while (stream >> token) { if (m_stopRequested) break; onToken(token + " "); }
    }
    if (onComplete) onComplete();
}

void LLMManager::stopGeneration() {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_stopRequested = true;
    for (auto& pair : m_modelStates) if (pair.second.backend) pair.second.backend->stop();
}

void LLMManager::resetContext(const std::string& modelName) { std::lock_guard<std::mutex> lock(m_mutex); if (modelName.empty()) for (auto& p : m_modelStates) p.second.contextString.clear(); else if (m_modelStates.count(modelName)) m_modelStates[modelName].contextString.clear(); }
void LLMManager::setContext(const std::string& context, const std::string& modelName) { std::lock_guard<std::mutex> lock(m_mutex); if (modelName.empty()) for (auto& p : m_modelStates) p.second.contextString = context; else if (m_modelStates.count(modelName)) m_modelStates[modelName].contextString = context; }
std::string LLMManager::getContext(const std::string& modelName) const { std::lock_guard<std::mutex> lock(m_mutex); if (modelName.empty() && !m_modelStates.empty()) return m_modelStates.begin()->second.contextString; auto it=m_modelStates.find(modelName); return it==m_modelStates.end()?std::string{}:it->second.contextString; }

LLMManager::ModelInfo LLMManager::getModelInfo(const std::string& modelName) const { for (const auto& model:m_availableModels) if(model.name==modelName) return model; return {}; }
std::vector<LLMManager::ModelInfo> LLMManager::getAvailableModels() const { std::lock_guard<std::mutex> lock(m_mutex); return m_availableModels; }

float LLMManager::benchmarkModel(const std::string& modelName) {
    if (!isModelLoaded(modelName)) return 0.0f;
    const auto start=std::chrono::steady_clock::now();
    const std::string result=generate("Reply with one short word: OK", modelName, 0.1f, 8);
    if (result.empty()) return 0.0f;
    const double seconds=std::chrono::duration<double>(std::chrono::steady_clock::now()-start).count();
    return seconds > 0.0 ? static_cast<float>(8.0/seconds) : 0.0f;
}

void LLMManager::setTemperature(float temperature) { std::lock_guard<std::mutex> lock(m_mutex); m_defaultTemperature=std::clamp(temperature,0.0f,2.0f); for(auto& p:m_modelStates)p.second.temperature=m_defaultTemperature; }
float LLMManager::getTemperature() const { std::lock_guard<std::mutex> lock(m_mutex); return m_defaultTemperature; }
void LLMManager::setTopP(float topP) { std::lock_guard<std::mutex> lock(m_mutex); m_defaultTopP=std::clamp(topP,0.0f,1.0f); for(auto& p:m_modelStates)p.second.topP=m_defaultTopP; }
float LLMManager::getTopP() const { std::lock_guard<std::mutex> lock(m_mutex); return m_defaultTopP; }
void LLMManager::setMaxTokens(int maxTokens) { std::lock_guard<std::mutex> lock(m_mutex); m_defaultMaxTokens=std::clamp(maxTokens,1,4096); for(auto& p:m_modelStates)p.second.maxTokens=m_defaultMaxTokens; }
int LLMManager::getMaxTokens() const { std::lock_guard<std::mutex> lock(m_mutex); return m_defaultMaxTokens; }

bool LLMManager::loadModelFromFile(const std::string& modelName) {
    std::string path=modelName;
    std::ifstream file(path, std::ios::binary | std::ios::ate);
    if (!file.good() || file.tellg() <= 0) return false;
    auto backend=createLlamaCppBackend();
    if (!backend || !backend->load(path)) return false;
    ModelState state; state.loaded=true; state.temperature=m_defaultTemperature; state.topP=m_defaultTopP; state.maxTokens=m_defaultMaxTokens; state.backend=std::move(backend);
    m_modelStates[modelName]=std::move(state);
    return true;
}
void LLMManager::unloadModelInternal(const std::string& modelName) { auto it=m_modelStates.find(modelName); if(it!=m_modelStates.end() && it->second.backend) it->second.backend->unload(); }

void LLMManager::initializeAvailableModels() {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_availableModels={
        {"qwen3-0.6b-q4","","","GGUF","Q4",0,{"en","ur","hi","ar"},false,false,"","Qwen","",false},
        {"qwen3-1.7b-q4","","","GGUF","Q4",0,{"en","ur","hi","ar"},false,false,"","Qwen","",false},
        {"qwen3-4b-q4","","","GGUF","Q4",0,{"en","ur","hi","ar"},false,false,"","Qwen","",false}
    };
}

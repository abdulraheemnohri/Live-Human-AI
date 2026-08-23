#include "AIEngine.h"
#include "LLMManager.h"
#include "STTManager.h"
#include "TTSManager.h"
#include "VisionManager.h"
#include "ModelRouter.h"

AIEngine::AIEngine()
    : m_llmManager(std::make_unique<LLMManager>()),
      m_sttManager(std::make_unique<STTManager>()),
      m_ttsManager(std::make_unique<TTSManager>()),
      m_visionManager(std::make_unique<VisionManager>()),
      m_modelRouter(std::make_unique<ModelRouter>()),
      m_status(Status::IDLE) {
}

AIEngine::~AIEngine() {
    shutdown();
}

bool AIEngine::initialize() {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (!m_llmManager->initialize()) {
        return false;
    }
    if (!m_sttManager->initialize()) {
        return false;
    }
    if (!m_ttsManager->initialize()) {
        return false;
    }
    if (!m_visionManager->initialize()) {
        return false;
    }
    if (!m_modelRouter->initialize()) {
        return false;
    }

    m_status = Status::IDLE;
    return true;
}

void AIEngine::shutdown() {
    std::lock_guard<std::mutex> lock(m_mutex);

    m_llmManager->shutdown();
    m_sttManager->shutdown();
    m_ttsManager->shutdown();
    m_visionManager->shutdown();
    m_modelRouter->shutdown();

    m_status = Status::ERROR;
}

bool AIEngine::loadModel(const std::string& modelName) {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (m_status == Status::GENERATING || m_status == Status::STREAMING) {
        return false;
    }

    m_status = Status::LOADING_MODEL;

    // Determine model type and route to appropriate manager
    if (m_modelRouter->isLLMModel(modelName)) {
        bool success = m_llmManager->loadModel(modelName);
        m_status = success ? Status::IDLE : Status::ERROR;
        return success;
    } else if (m_modelRouter->isSTTModel(modelName)) {
        bool success = m_sttManager->loadModel(modelName);
        m_status = success ? Status::IDLE : Status::ERROR;
        return success;
    } else if (m_modelRouter->isTTSModel(modelName)) {
        bool success = m_ttsManager->loadModel(modelName);
        m_status = success ? Status::IDLE : Status::ERROR;
        return success;
    } else if (m_modelRouter->isVisionModel(modelName)) {
        bool success = m_visionManager->loadModel(modelName);
        m_status = success ? Status::IDLE : Status::ERROR;
        return success;
    }

    m_status = Status::ERROR;
    return false;
}

bool AIEngine::unloadModel(const std::string& modelName) {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (m_status == Status::GENERATING || m_status == Status::STREAMING) {
        return false;
    }

    // Determine model type and route to appropriate manager
    if (m_modelRouter->isLLMModel(modelName)) {
        return m_llmManager->unloadModel(modelName);
    } else if (m_modelRouter->isSTTModel(modelName)) {
        return m_sttManager->unloadModel(modelName);
    } else if (m_modelRouter->isTTSModel(modelName)) {
        return m_ttsManager->unloadModel(modelName);
    } else if (m_modelRouter->isVisionModel(modelName)) {
        return m_visionManager->unloadModel(modelName);
    }

    return false;
}

bool AIEngine::switchModel(const std::string& fromModel, const std::string& toModel) {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (!unloadModel(fromModel)) {
        return false;
    }

    return loadModel(toModel);
}

std::vector<std::string> AIEngine::getLoadedModels() const {
    std::vector<std::string> models;

    auto llmModels = m_llmManager->getLoadedModels();
    models.insert(models.end(), llmModels.begin(), llmModels.end());

    auto sttModels = m_sttManager->getLoadedModels();
    models.insert(models.end(), sttModels.begin(), sttModels.end());

    auto ttsModels = m_ttsManager->getLoadedModels();
    models.insert(models.end(), ttsModels.begin(), ttsModels.end());

    auto visionModels = m_visionManager->getLoadedModels();
    models.insert(models.end(), visionModels.begin(), visionModels.end());

    return models;
}

std::string AIEngine::generate(
    const std::string& prompt,
    const std::string& modelName,
    float temperature,
    int maxTokens
) {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (m_status != Status::IDLE) {
        return "";
    }

    m_status = Status::GENERATING;

    // Route to the appropriate model type
    std::string selectedModel = modelName.empty() ? m_modelRouter->selectModel("text") : modelName;
    std::string result = m_llmManager->generate(prompt, selectedModel, temperature, maxTokens);

    m_status = Status::IDLE;
    return result;
}

void AIEngine::generateStreaming(
    const std::string& prompt,
    std::function<void(const std::string&)> onToken,
    std::function<void()> onComplete,
    const std::string& modelName,
    float temperature,
    int maxTokens
) {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (m_status != Status::IDLE) {
        if (onComplete) onComplete();
        return;
    }

    m_status = Status::STREAMING;

    // Route to the appropriate model type
    std::string selectedModel = modelName.empty() ? m_modelRouter->selectModel("text") : modelName;
    m_llmManager->generateStreaming(
        prompt,
        [this, onToken](const std::string& token) {
            if (onToken) onToken(token);
        },
        [this, onComplete]() {
            m_status = Status::IDLE;
            if (onComplete) onComplete();
        },
        selectedModel,
        temperature,
        maxTokens
    );
}

void AIEngine::stopGeneration() {
    std::lock_guard<std::mutex> lock(m_mutex);

    if (m_status == Status::GENERATING || m_status == Status::STREAMING) {
        m_llmManager->stopGeneration();
        m_status = Status::IDLE;
    }
}

void AIEngine::resetContext(const std::string& modelName) {
    if (modelName.empty()) {
        m_llmManager->resetContext();
    } else {
        m_llmManager->resetContext(modelName);
    }
}

void AIEngine::setContext(const std::string& context, const std::string& modelName) {
    if (modelName.empty()) {
        m_llmManager->setContext(context);
    } else {
        m_llmManager->setContext(context, modelName);
    }
}

static AIEngine::ModelInfo convertLLMInfo(const LLMManager::ModelInfo& info) {
    AIEngine::ModelInfo res;
    res.name = info.name;
    res.version = info.version;
    res.size = info.size;
    res.format = info.format;
    res.quantization = info.quantization;
    res.ramRequirement = info.ramRequirement;
    res.supportedLanguages = info.supportedLanguages;
    res.supportsVision = info.supportsVision;
    res.supportsAudio = info.supportsAudio;
    res.license = info.license;
    res.source = info.source;
    res.checksum = info.checksum;
    res.isInstalled = info.isInstalled;
    return res;
}

static AIEngine::ModelInfo convertSTTInfo(const STTManager::ModelInfo& info) {
    AIEngine::ModelInfo res;
    res.name = info.name;
    res.version = info.version;
    res.size = info.size;
    res.format = info.format;
    res.supportedLanguages = info.supportedLanguages;
    res.supportsAudio = true;
    res.license = info.license;
    res.source = info.source;
    res.checksum = info.checksum;
    res.isInstalled = info.isInstalled;
    return res;
}

static AIEngine::ModelInfo convertTTSInfo(const TTSManager::ModelInfo& info) {
    AIEngine::ModelInfo res;
    res.name = info.name;
    res.version = info.version;
    res.size = info.size;
    res.format = info.format;
    res.supportedLanguages = info.supportedLanguages;
    res.supportsAudio = true;
    res.license = info.license;
    res.source = info.source;
    res.checksum = info.checksum;
    res.isInstalled = info.isInstalled;
    return res;
}

static AIEngine::ModelInfo convertVisionInfo(const VisionManager::ModelInfo& info) {
    AIEngine::ModelInfo res;
    res.name = info.name;
    res.version = info.version;
    res.size = info.size;
    res.format = info.format;
    res.supportsVision = true;
    res.license = info.license;
    res.source = info.source;
    res.checksum = info.checksum;
    res.isInstalled = info.isInstalled;
    return res;
}

AIEngine::ModelInfo AIEngine::getModelInfo(const std::string& modelName) const {
    if (m_modelRouter->isLLMModel(modelName)) {
        return convertLLMInfo(m_llmManager->getModelInfo(modelName));
    } else if (m_modelRouter->isSTTModel(modelName)) {
        return convertSTTInfo(m_sttManager->getModelInfo(modelName));
    } else if (m_modelRouter->isTTSModel(modelName)) {
        return convertTTSInfo(m_ttsManager->getModelInfo(modelName));
    } else if (m_modelRouter->isVisionModel(modelName)) {
        return convertVisionInfo(m_visionManager->getModelInfo(modelName));
    }

    return ModelInfo{};
}

std::vector<AIEngine::ModelInfo> AIEngine::getAvailableModels() const {
    std::vector<ModelInfo> models;

    for (const auto& m : m_llmManager->getAvailableModels()) {
        models.push_back(convertLLMInfo(m));
    }
    for (const auto& m : m_sttManager->getAvailableModels()) {
        models.push_back(convertSTTInfo(m));
    }
    for (const auto& m : m_ttsManager->getAvailableModels()) {
        models.push_back(convertTTSInfo(m));
    }
    for (const auto& m : m_visionManager->getAvailableModels()) {
        models.push_back(convertVisionInfo(m));
    }

    return models;
}

float AIEngine::benchmarkModel(const std::string& modelName) {
    if (m_modelRouter->isLLMModel(modelName)) {
        return m_llmManager->benchmarkModel(modelName);
    } else if (m_modelRouter->isSTTModel(modelName)) {
        return m_sttManager->benchmarkModel(modelName);
    } else if (m_modelRouter->isTTSModel(modelName)) {
        return m_ttsManager->benchmarkModel(modelName);
    } else if (m_modelRouter->isVisionModel(modelName)) {
        return m_visionManager->benchmarkModel(modelName);
    }

    return 0.0f;
}

AIEngine::Status AIEngine::getStatus() const {
    return m_status;
}

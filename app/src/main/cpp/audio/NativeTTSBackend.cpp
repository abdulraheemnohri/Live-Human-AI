#include "TTSBackend.h"
#include <atomic>
#include <fstream>

class NativeTTSBackend final : public TTSBackend {
public:
    bool initialize() override { m_ready = false; return false; }
    void shutdown() override { m_ready = false; }
    bool isReady() const override { return m_ready; }
    bool synthesize(const std::string&, const std::string&, const std::string&, float, float) override { return false; }
    void stop() override { m_stop.store(true); }
private:
    bool m_ready = false;
    std::atomic<bool> m_stop{false};
};

std::unique_ptr<TTSBackend> createNativeTTSBackend() {
    return std::make_unique<NativeTTSBackend>();
}

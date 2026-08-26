#ifndef LIVEHUMANAI_HUGGINGFACEDOWNLOADER_H
#define LIVEHUMANAI_HUGGINGFACEDOWNLOADER_H

#include <string>
#include <functional>

namespace livehumanai {
namespace huggingface {

enum class DownloadState { IDLE, DOWNLOADING, PAUSED, COMPLETED, FAILED, CANCELLED };

class HuggingFaceDownloader {
public:
    HuggingFaceDownloader() = default;
    ~HuggingFaceDownloader() = default;
    
    bool initialize(const std::string& downloadPath);
    void shutdown();
    
    int startDownload(const std::string& modelId);
    void pauseDownload(int downloadId);
    void resumeDownload(int downloadId);
    void cancelDownload(int downloadId);
    
    DownloadState getState(int downloadId) const;
    float getProgress(int downloadId) const;
    
private:
    bool initialized_ = false;
    std::string downloadPath_;
    int nextDownloadId_ = 1;
};

} // namespace huggingface
} // namespace livehumanai

#endif

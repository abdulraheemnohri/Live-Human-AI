#include "ThreadPool.h"
#include "../utils/Logger.h"
#include <chrono>

namespace livehumanai {
namespace core {

using namespace utils;

ThreadPool::ThreadPool(size_t numThreads)
    : stop_(false), activeTasks_(0) {

    // Use hardware concurrency if not specified
    if (numThreads == 0) {
        numThreads = std::thread::hardware_concurrency();
        if (numThreads == 0) {
            numThreads = 4; // Default fallback
        }
    }

    LOGI("ThreadPool: Creating %zu worker threads", numThreads);

    workers_.reserve(numThreads);
    for (size_t i = 0; i < numThreads; ++i) {
        workers_.emplace_back(&ThreadPool::workerThread, this);
    }
}

ThreadPool::~ThreadPool() {
    stop(false);
}

void ThreadPool::workerThread() {
    while (true) {
        std::function<void()> task;

        {
            std::unique_lock<std::mutex> lock(queueMutex_);

            condition_.wait(lock, [this] {
                return stop_ || !tasks_.empty();
            });

            if (stop_ && tasks_.empty()) {
                return;
            }

            task = std::move(tasks_.front());
            tasks_.pop();
            ++activeTasks_;
        }

        try {
            task();
        } catch (const std::exception& e) {
            LOGE("ThreadPool: Task threw exception: %s", e.what());
        } catch (...) {
            LOGE("ThreadPool: Task threw unknown exception");
        }

        --activeTasks_;
        completionCondition_.notify_all();
    }
}

size_t ThreadPool::pendingTasks() const {
    std::unique_lock<std::mutex> lock(queueMutex_);
    return tasks_.size();
}

void ThreadPool::waitAll() {
    std::unique_lock<std::mutex> lock(queueMutex_);
    completionCondition_.wait(lock, [this] {
        return tasks_.empty() && activeTasks_ == 0;
    });
}

void ThreadPool::stop(bool wait) {
    {
        std::unique_lock<std::mutex> lock(queueMutex_);
        if (stop_) {
            return; // Already stopped
        }
        stop_ = true;
    }

    condition_.notify_all();

    if (wait) {
        for (std::thread& worker : workers_) {
            if (worker.joinable()) {
                worker.join();
            }
        }
        LOGI("ThreadPool: All workers joined");
    } else {
        for (std::thread& worker : workers_) {
            if (worker.joinable()) {
                worker.detach();
            }
        }
        LOGI("ThreadPool: All workers detached");
    }
}

} // namespace core
} // namespace livehumanai

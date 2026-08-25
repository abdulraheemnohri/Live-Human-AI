#ifndef JALEBI_NATIVE_SCHEDULER_H
#define JALEBI_NATIVE_SCHEDULER_H

#include <chrono>
#include <condition_variable>
#include <functional>
#include <mutex>
#include <queue>
#include <thread>
#include <atomic>

namespace LiveHumanAI {

class JalebiNativeScheduler {
public:
    using Task = std::function<void()>;

    JalebiNativeScheduler() = default;
    ~JalebiNativeScheduler() { stop(); }

    void start() {
        bool expected = false;
        if (!m_running.compare_exchange_strong(expected, true)) return;
        m_worker = std::thread([this] { run(); });
    }

    void stop() {
        if (!m_running.exchange(false)) return;
        m_cv.notify_all();
        if (m_worker.joinable()) m_worker.join();
    }

    void post(Task task) {
        if (!task) return;
        { std::lock_guard<std::mutex> lock(m_mutex); m_tasks.push(std::move(task)); }
        m_cv.notify_one();
    }

private:
    void run() {
        while (m_running) {
            Task task;
            { std::unique_lock<std::mutex> lock(m_mutex);
              m_cv.wait(lock, [this] { return !m_running || !m_tasks.empty(); });
              if (!m_running && m_tasks.empty()) break;
              task = std::move(m_tasks.front()); m_tasks.pop(); }
            if (task) task();
        }
    }

    std::atomic<bool> m_running{false};
    std::thread m_worker;
    std::mutex m_mutex;
    std::condition_variable m_cv;
    std::queue<Task> m_tasks;
};

} // namespace LiveHumanAI
#endif

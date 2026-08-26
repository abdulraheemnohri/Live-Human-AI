#ifndef LIVEHUMANAI_THREADPOOL_H
#define LIVEHUMANAI_THREADPOOL_H

#include <vector>
#include <queue>
#include <thread>
#include <mutex>
#include <condition_variable>
#include <functional>
#include <future>
#include <atomic>
#include <memory>

namespace livehumanai {
namespace core {

/**
 * Thread Pool for efficient task execution
 * Manages a pool of worker threads to execute tasks concurrently
 */
class ThreadPool {
public:
    explicit ThreadPool(size_t numThreads = 0);
    ~ThreadPool();

    // Prevent copying
    ThreadPool(const ThreadPool&) = delete;
    ThreadPool& operator=(const ThreadPool&) = delete;

    /**
     * Enqueue a task and get a future for the result
     */
    template<typename F, typename... Args>
    auto enqueue(F&& f, Args&&... args) 
        -> std::future<typename std::invoke_result<F, Args...>::type> {
        
        using ReturnType = typename std::invoke_result<F, Args...>::type;
        
        auto task = std::make_shared<std::packaged_task<ReturnType()>>(
            std::bind(std::forward<F>(f), std::forward<Args>(args)...)
        );
        
        std::future<ReturnType> result = task->get_future();
        
        {
            std::unique_lock<std::mutex> lock(queueMutex_);
            
            if (stop_) {
                throw std::runtime_error("Cannot enqueue on stopped ThreadPool");
            }
            
            tasks_.emplace([task]() { (*task)(); });
        }
        
        condition_.notify_one();
        return result;
    }

    /**
     * Get number of worker threads
     */
    size_t size() const { return workers_.size(); }

    /**
     * Get number of pending tasks
     */
    size_t pendingTasks() const;

    /**
     * Wait for all tasks to complete
     */
    void waitAll();

    /**
     * Stop the thread pool
     * @param wait If true, wait for all tasks to complete before stopping
     */
    void stop(bool wait = true);

    /**
     * Check if pool is stopped
     */
    bool isStopped() const { return stop_; }

private:
    void workerThread();

    std::vector<std::thread> workers_;
    std::queue<std::function<void()>> tasks_;
    
    mutable std::mutex queueMutex_;
    std::condition_variable condition_;
    
    std::atomic<bool> stop_;
    std::atomic<size_t> activeTasks_;
    std::condition_variable completionCondition_;
};

} // namespace core
} // namespace livehumanai

#endif // LIVEHUMANAI_THREADPOOL_H

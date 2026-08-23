#ifndef LIVE_HUMAN_AI_TASK_SCHEDULER_H
#define LIVE_HUMAN_AI_TASK_SCHEDULER_H

#include <string>
#include <vector>
#include <memory>
#include <mutex>
#include <thread>
#include <condition_variable>
#include <queue>
#include <functional>

// TaskScheduler manages the execution of AI tasks
class TaskScheduler {
public:
    // Task priority levels
    enum class Priority {
        CRITICAL,
        HIGH,
        NORMAL,
        LOW,
        BACKGROUND
    };

    // Task status
    enum class Status {
        PENDING,
        RUNNING,
        COMPLETED,
        CANCELLED,
        FAILED
    };

    // Task structure
    struct Task {
        int id;
        std::string name;
        Priority priority;
        Status status;
        std::function<void()> function;
        std::function<void(Status)> callback;
        bool cancelable;
    };

    TaskScheduler();
    ~TaskScheduler();

    // Initialization and shutdown
    bool initialize();
    void shutdown();

    // Task management
    int scheduleTask(
        const std::string& name,
        Priority priority,
        std::function<void()> function,
        std::function<void(Status)> callback = nullptr,
        bool cancelable = true
    );

    bool cancelTask(int taskId);
    bool cancelAllTasks();

    // Task status
    Status getTaskStatus(int taskId) const;
    std::vector<Task> getAllTasks() const;

    // Thread pool management
    void setThreadCount(int count);
    int getThreadCount() const;

    // Pause and resume
    void pause();
    void resume();

private:
    std::vector<std::thread> m_threads;
    std::queue<Task> m_taskQueue;
    std::mutex m_mutex;
    std::condition_variable m_conditionVariable;
    bool m_running;
    bool m_paused;
    int m_nextTaskId;
    int m_threadCount;

    // Internal methods
    void workerThread();
    void processTask(Task& task);
};

#endif // LIVE_HUMAN_AI_TASK_SCHEDULER_H

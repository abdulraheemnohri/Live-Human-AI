#include "TaskScheduler.h"
#include "../utils/Logger.h"

TaskScheduler::TaskScheduler()
    : m_running(false),
      m_paused(false),
      m_nextTaskId(0),
      m_threadCount(std::thread::hardware_concurrency()) {
}

TaskScheduler::~TaskScheduler() {
    shutdown();
}

bool TaskScheduler::initialize() {
    if (m_running) {
        return false;
    }

    m_running = true;
    for (int i = 0; i < m_threadCount; ++i) {
        m_threads.emplace_back(&TaskScheduler::workerThread, this);
    }

    return true;
}

void TaskScheduler::shutdown() {
    {
        std::lock_guard<std::mutex> lock(m_mutex);
        m_running = false;
        m_conditionVariable.notify_all();
    }

    for (auto& thread : m_threads) {
        if (thread.joinable()) {
            thread.join();
        }
    }

    m_threads.clear();
}

int TaskScheduler::scheduleTask(
    const std::string& name,
    Priority priority,
    std::function<void()> function,
    std::function<void(Status)> callback,
    bool cancelable
) {
    std::lock_guard<std::mutex> lock(m_mutex);

    Task task;
    task.id = m_nextTaskId++;
    task.name = name;
    task.priority = priority;
    task.status = Status::PENDING;
    task.function = function;
    task.callback = callback;
    task.cancelable = cancelable;

    // Insert task based on priority (simple priority queue)
    auto it = m_taskQueue.begin();
    while (it != m_taskQueue.end() && static_cast<int>(it->priority) <= static_cast<int>(priority)) {
        ++it;
    }
    m_taskQueue.insert(it, task);

    m_conditionVariable.notify_one();
    return task.id;
}

bool TaskScheduler::cancelTask(int taskId) {
    std::lock_guard<std::mutex> lock(m_mutex);

    for (auto it = m_taskQueue.begin(); it != m_taskQueue.end(); ++it) {
        if (it->id == taskId && it->cancelable) {
            it->status = Status::CANCELLED;
            if (it->callback) {
                it->callback(Status::CANCELLED);
            }
            m_taskQueue.erase(it);
            return true;
        }
    }

    return false;
}

bool TaskScheduler::cancelAllTasks() {
    std::lock_guard<std::mutex> lock(m_mutex);

    bool cancelledAny = false;
    for (auto& task : m_taskQueue) {
        if (task.cancelable) {
            task.status = Status::CANCELLED;
            if (task.callback) {
                task.callback(Status::CANCELLED);
            }
            cancelledAny = true;
        }
    }

    m_taskQueue.clear();
    return cancelledAny;
}

TaskScheduler::Status TaskScheduler::getTaskStatus(int taskId) const {
    std::lock_guard<std::mutex> lock(m_mutex);

    for (const auto& task : m_taskQueue) {
        if (task.id == taskId) {
            return task.status;
        }
    }

    return Status::CANCELLED; // Task not found
}

std::vector<TaskScheduler::Task> TaskScheduler::getAllTasks() const {
    std::lock_guard<std::mutex> lock(m_mutex);
    return std::vector<Task>(m_taskQueue.begin(), m_taskQueue.end());
}

void TaskScheduler::setThreadCount(int count) {
    if (count < 1) count = 1;
    m_threadCount = count;
}

int TaskScheduler::getThreadCount() const {
    return m_threadCount;
}

void TaskScheduler::pause() {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_paused = true;
}

void TaskScheduler::resume() {
    std::lock_guard<std::mutex> lock(m_mutex);
    m_paused = false;
    m_conditionVariable.notify_all();
}

void TaskScheduler::workerThread() {
    while (true) {
        Task task;

        {
            std::unique_lock<std::mutex> lock(m_mutex);
            m_conditionVariable.wait(lock, [this]() {
                return !m_running || !m_taskQueue.empty() || !m_paused;
            });

            if (!m_running) {
                return;
            }

            if (m_paused || m_taskQueue.empty()) {
                continue;
            }

            task = m_taskQueue.front();
            m_taskQueue.pop();
            task.status = Status::RUNNING;
        }

        processTask(task);
    }
}

void TaskScheduler::processTask(Task& task) {
    try {
        if (task.function) {
            task.function();
        }
        task.status = Status::COMPLETED;
    } catch (...) {
        task.status = Status::FAILED;
    }

    if (task.callback) {
        task.callback(task.status);
    }
}

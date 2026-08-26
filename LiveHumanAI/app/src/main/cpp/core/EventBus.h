#ifndef LIVEHUMANAI_EVENTBUS_H
#define LIVEHUMANAI_EVENTBUS_H

#include <string>
#include <unordered_map>
#include <vector>
#include <functional>
#include <mutex>
#include <memory>
#include <atomic>

namespace livehumanai {
namespace core {

/**
 * Event types for the EventBus system
 */
enum class EventType {
    // Core Events
    INITIALIZED,
    SHUTDOWN,

    // Model Events
    MODEL_DOWNLOAD_STARTED,
    MODEL_DOWNLOAD_PROGRESS,
    MODEL_DOWNLOAD_COMPLETED,
    MODEL_DOWNLOAD_FAILED,
    MODEL_INSTALLED,
    MODEL_UNINSTALLED,
    MODEL_LOADED,
    MODEL_UNLOADED,

    // AI Events
    AI_GENERATION_STARTED,
    AI_GENERATION_PROGRESS,
    AI_GENERATION_COMPLETED,
    AI_GENERATION_STOPPED,
    AI_ERROR,

    // Voice Events
    AUDIO_CAPTURE_STARTED,
    AUDIO_CAPTURE_STOPPED,
    SPEECH_RECOGNITION_STARTED,
    SPEECH_RECOGNITION_COMPLETED,
    TTS_STARTED,
    TTS_COMPLETED,
    TTS_INTERRUPTED,

    // Vision Events
    CAMERA_FRAME_ANALYZED,
    OBJECT_DETECTED,
    OCR_COMPLETED,
    VISION_ERROR,

    // Memory Events
    MEMORY_CREATED,
    MEMORY_UPDATED,
    MEMORY_DELETED,
    MEMORY_SEARCH_COMPLETED,

    // Jalebi Loop Events
    JALEBI_LOOP_STARTED,
    JALEBI_LOOP_ITERATION,
    JALEBI_LOOP_COMPLETED,
    JALEBI_LOOP_FAILED,
    JALEBI_LOOP_CANCELLED,

    // Hardware Events
    THERMAL_WARNING,
    THERMAL_CRITICAL,
    BATTERY_LOW,
    MEMORY_PRESSURE,

    // Tool Events
    TOOL_EXECUTION_STARTED,
    TOOL_EXECUTION_COMPLETED,
    TOOL_EXECUTION_FAILED,

    // Permission Events
    PERMISSION_GRANTED,
    PERMISSION_DENIED,

    // Custom/User Events
    CUSTOM
};

/**
 * Event data structure
 */
struct Event {
    EventType type;
    std::string source;
    std::string data;
    int64_t timestamp;
    int priority;

    Event() : type(EventType::CUSTOM), timestamp(0), priority(0) {}
    Event(EventType t, const std::string& src = "", const std::string& d = "")
        : type(t), source(src), data(d), priority(0) {
        timestamp = std::chrono::duration_cast<std::chrono::milliseconds>(
            std::chrono::system_clock::now().time_since_epoch()).count();
    }
};

/**
 * Thread-safe EventBus for inter-component communication
 */
class EventBus {
public:
    using EventHandler = std::function<void(const Event&)>;
    using SubscriptionId = size_t;

private:
    struct Subscription {
        SubscriptionId id;
        EventType eventType;
        EventHandler handler;
        bool active;
    };

    mutable std::mutex mutex_;
    std::unordered_map<EventType, std::vector<Subscription>> subscriptions_;
    std::atomic<SubscriptionId> nextSubscriptionId_;
    std::atomic<bool> active_;

public:
    EventBus();
    ~EventBus();

    /**
     * Subscribe to an event type
     * @return Subscription ID for later unsubscription
     */
    SubscriptionId subscribe(EventType eventType, EventHandler handler);

    /**
     * Unsubscribe from events
     */
    void unsubscribe(SubscriptionId subscriptionId);

    /**
     * Publish an event to all subscribers
     */
    void publish(const Event& event);

    /**
     * Convenience method to create and publish an event
     */
    void publish(EventType type, const std::string& source = "", const std::string& data = "");

    /**
     * Clear all subscriptions
     */
    void clear();

    /**
     * Get singleton instance
     */
    static EventBus& getInstance();
};

} // namespace core
} // namespace livehumanai

#endif // LIVEHUMANAI_EVENTBUS_H

#include "EventBus.h"
#include "../utils/Logger.h"
#include <chrono>

namespace livehumanai {
namespace core {

using namespace utils;

EventBus::EventBus() : nextSubscriptionId_(1), active_(true) {}

EventBus::~EventBus() {
    clear();
}

EventBus& EventBus::getInstance() {
    static EventBus instance;
    return instance;
}

EventBus::SubscriptionId EventBus::subscribe(EventType eventType, EventHandler handler) {
    std::lock_guard<std::mutex> lock(mutex_);

    SubscriptionId id = nextSubscriptionId_++;
    subscriptions_[eventType].push_back({id, eventType, handler, true});

    LOGD("EventBus: Subscribed to event %d with ID %zu", static_cast<int>(eventType), id);
    return id;
}

void EventBus::unsubscribe(SubscriptionId subscriptionId) {
    std::lock_guard<std::mutex> lock(mutex_);

    for (auto& pair : subscriptions_) {
        for (auto& sub : pair.second) {
            if (sub.id == subscriptionId) {
                sub.active = false;
                LOGD("EventBus: Unsubscribed ID %zu", subscriptionId);
                return;
            }
        }
    }
}

void EventBus::publish(const Event& event) {
    if (!active_) {
        return;
    }

    std::lock_guard<std::mutex> lock(mutex_);

    auto it = subscriptions_.find(event.type);
    if (it == subscriptions_.end()) {
        return;
    }

    // Copy handlers to avoid issues with modifications during iteration
    std::vector<EventHandler> handlers;
    for (const auto& sub : it->second) {
        if (sub.active) {
            handlers.push_back(sub.handler);
        }
    }

    // Execute handlers
    for (const auto& handler : handlers) {
        try {
            handler(event);
        } catch (const std::exception& e) {
            LOGE("EventBus: Handler threw exception: %s", e.what());
        } catch (...) {
            LOGE("EventBus: Handler threw unknown exception");
        }
    }
}

void EventBus::publish(EventType type, const std::string& source, const std::string& data) {
    publish(Event(type, source, data));
}

void EventBus::clear() {
    std::lock_guard<std::mutex> lock(mutex_);
    subscriptions_.clear();
    active_ = false;
    LOGI("EventBus: Cleared all subscriptions");
}

} // namespace core
} // namespace livehumanai

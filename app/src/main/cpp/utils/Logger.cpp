#include "Logger.h"

Logger::Logger() : m_minLogLevel(Level::INFO) {
    // Constructor
}

Logger::~Logger() {
    // Destructor
}

void Logger::log(Level level, const std::string& message) const {
    if (level < m_minLogLevel) {
        return;
    }

    int priority = levelToPriority(level);
    __android_log_print(priority, "LiveHumanAI", "%s", message.c_str());
}

void Logger::verbose(const std::string& message) const {
    log(Level::VERBOSE, message);
}

void Logger::debug(const std::string& message) const {
    log(Level::DEBUG, message);
}

void Logger::info(const std::string& message) const {
    log(Level::INFO, message);
}

void Logger::warn(const std::string& message) const {
    log(Level::WARN, message);
}

void Logger::error(const std::string& message) const {
    log(Level::ERROR, message);
}

void Logger::fatal(const std::string& message) const {
    log(Level::FATAL, message);
}

void Logger::setMinLogLevel(Level level) {
    m_minLogLevel = level;
}

Logger::Level Logger::getMinLogLevel() const {
    return m_minLogLevel;
}

int Logger::levelToPriority(Level level) const {
    switch (level) {
        case Level::VERBOSE:
            return ANDROID_LOG_VERBOSE;
        case Level::DEBUG:
            return ANDROID_LOG_DEBUG;
        case Level::INFO:
            return ANDROID_LOG_INFO;
        case Level::WARN:
            return ANDROID_LOG_WARN;
        case Level::ERROR:
            return ANDROID_LOG_ERROR;
        case Level::FATAL:
            return ANDROID_LOG_FATAL;
        default:
            return ANDROID_LOG_DEFAULT;
    }
}

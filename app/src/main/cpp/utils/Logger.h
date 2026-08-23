#ifndef LIVE_HUMAN_AI_LOGGER_H
#define LIVE_HUMAN_AI_LOGGER_H

#include <string>
#include <android/log.h>

// Logger class for native logging
class Logger {
public:
    enum class Level {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        FATAL
    };

    Logger();
    ~Logger();

    // Log a message
    void log(Level level, const std::string& message) const;

    // Convenience methods for each log level
    void verbose(const std::string& message) const;
    void debug(const std::string& message) const;
    void info(const std::string& message) const;
    void warn(const std::string& message) const;
    void error(const std::string& message) const;
    void fatal(const std::string& message) const;

    // Set the minimum log level
    void setMinLogLevel(Level level);

    // Get the current minimum log level
    Level getMinLogLevel() const;

private:
    Level m_minLogLevel;

    // Convert Level to Android log priority
    int levelToPriority(Level level) const;
};

#endif // LIVE_HUMAN_AI_LOGGER_H

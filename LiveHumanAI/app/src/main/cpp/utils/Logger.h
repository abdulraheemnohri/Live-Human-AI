#ifndef LIVEHUMANAI_LOGGER_H
#define LIVEHUMANAI_LOGGER_H

#include <android/log.h>
#include <string>
#include <sstream>

namespace livehumanai {
namespace utils {

/**
 * Logger utility for native C++ code
 * Provides Android logcat integration with multiple log levels
 */
class Logger {
public:
    enum class Level {
        VERBOSE = ANDROID_LOG_VERBOSE,
        DEBUG = ANDROID_LOG_DEBUG,
        INFO = ANDROID_LOG_INFO,
        WARN = ANDROID_LOG_WARN,
        ERROR = ANDROID_LOG_ERROR,
        FATAL = ANDROID_LOG_FATAL
    };

private:
    static const char* DEFAULT_TAG;
    std::string tag_;
    Level minLevel_;

public:
    explicit Logger(const char* tag = nullptr);
    ~Logger() = default;

    void setTag(const char* tag);
    void setMinLevel(Level level);

    void verbose(const char* message);
    void debug(const char* message);
    void info(const char* message);
    void warn(const char* message);
    void error(const char* message);
    void fatal(const char* message);

    // Format string support
    void verbose(const char* format, ...);
    void debug(const char* format, ...);
    void info(const char* format, ...);
    void warn(const char* format, ...);
    void error(const char* format, ...);
    void fatal(const char* format, ...);

    // Stream-style logging
    template<typename T>
    Logger& operator<<(T const& value) {
        logStream_ << value;
        return *this;
    }

    void flush(Level level = Level::INFO);

    // Static convenience methods
    static void v(const char* tag, const char* message);
    static void d(const char* tag, const char* message);
    static void i(const char* tag, const char* message);
    static void w(const char* tag, const char* message);
    static void e(const char* tag, const char* message);
    static void f(const char* tag, const char* message);

private:
    void log(Level level, const char* message);
    std::ostringstream logStream_;
    Level pendingLevel_;
};

// Global logger instance for the library
extern Logger gLogger;

// Convenience macros
#define LOGV(...) livehumanai::utils::gLogger.verbose(__VA_ARGS__)
#define LOGD(...) livehumanai::utils::gLogger.debug(__VA_ARGS__)
#define LOGI(...) livehumanai::utils::gLogger.info(__VA_ARGS__)
#define LOGW(...) livehumanai::utils::gLogger.warn(__VA_ARGS__)
#define LOGE(...) livehumanai::utils::gLogger.error(__VA_ARGS__)
#define LOGF(...) livehumanai::utils::gLogger.fatal(__VA_ARGS__)

} // namespace utils
} // namespace livehumanai

#endif // LIVEHUMANAI_LOGGER_H

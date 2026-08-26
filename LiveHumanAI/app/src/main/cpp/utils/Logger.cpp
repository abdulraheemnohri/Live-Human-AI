#include "Logger.h"
#include <cstdarg>
#include <cstring>

namespace livehumanai {
namespace utils {

const char* Logger::DEFAULT_TAG = "LiveHumanAI_Native";

Logger gLogger("LiveHumanAI");

Logger::Logger(const char* tag)
    : tag_(tag ? tag : DEFAULT_TAG), minLevel_(Level::DEBUG), pendingLevel_(Level::INFO) {}

void Logger::setTag(const char* tag) {
    tag_ = tag;
}

void Logger::setMinLevel(Level level) {
    minLevel_ = level;
}

void Logger::verbose(const char* message) { log(Level::VERBOSE, message); }
void Logger::debug(const char* message) { log(Level::DEBUG, message); }
void Logger::info(const char* message) { log(Level::INFO, message); }
void Logger::warn(const char* message) { log(Level::WARN, message); }
void Logger::error(const char* message) { log(Level::ERROR, message); }
void Logger::fatal(const char* message) { log(Level::FATAL, message); }

void Logger::log(Level level, const char* message) {
    if (level < minLevel_) return;
    __android_log_print(static_cast<android_LogPriority>(level), tag_.c_str(), "%s", message);
}

void Logger::verbose(const char* format, ...) {
    if (Level::VERBOSE < minLevel_) return;
    va_list args;
    va_start(args, format);
    char buffer[4096];
    vsnprintf(buffer, sizeof(buffer), format, args);
    va_end(args);
    log(Level::VERBOSE, buffer);
}

void Logger::debug(const char* format, ...) {
    if (Level::DEBUG < minLevel_) return;
    va_list args;
    va_start(args, format);
    char buffer[4096];
    vsnprintf(buffer, sizeof(buffer), format, args);
    va_end(args);
    log(Level::DEBUG, buffer);
}

void Logger::info(const char* format, ...) {
    if (Level::INFO < minLevel_) return;
    va_list args;
    va_start(args, format);
    char buffer[4096];
    vsnprintf(buffer, sizeof(buffer), format, args);
    va_end(args);
    log(Level::INFO, buffer);
}

void Logger::warn(const char* format, ...) {
    if (Level::WARN < minLevel_) return;
    va_list args;
    va_start(args, format);
    char buffer[4096];
    vsnprintf(buffer, sizeof(buffer), format, args);
    va_end(args);
    log(Level::WARN, buffer);
}

void Logger::error(const char* format, ...) {
    if (Level::ERROR < minLevel_) return;
    va_list args;
    va_start(args, format);
    char buffer[4096];
    vsnprintf(buffer, sizeof(buffer), format, args);
    va_end(args);
    log(Level::ERROR, buffer);
}

void Logger::fatal(const char* format, ...) {
    if (Level::FATAL < minLevel_) return;
    va_list args;
    va_start(args, format);
    char buffer[4096];
    vsnprintf(buffer, sizeof(buffer), format, args);
    va_end(args);
    log(Level::FATAL, buffer);
}

void Logger::flush(Level level) {
    log(level, logStream_.str().c_str());
    logStream_.str("");
    logStream_.clear();
}

void Logger::v(const char* tag, const char* message) {
    __android_log_write(ANDROID_LOG_VERBOSE, tag, message);
}

void Logger::d(const char* tag, const char* message) {
    __android_log_write(ANDROID_LOG_DEBUG, tag, message);
}

void Logger::i(const char* tag, const char* message) {
    __android_log_write(ANDROID_LOG_INFO, tag, message);
}

void Logger::w(const char* tag, const char* message) {
    __android_log_write(ANDROID_LOG_WARN, tag, message);
}

void Logger::e(const char* tag, const char* message) {
    __android_log_write(ANDROID_LOG_ERROR, tag, message);
}

void Logger::f(const char* tag, const char* message) {
    __android_log_write(ANDROID_LOG_FATAL, tag, message);
}

} // namespace utils
} // namespace livehumanai

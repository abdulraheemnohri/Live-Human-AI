#include "FileUtils.h"
#include "Logger.h"
#include <sys/stat.h>
#include <fstream>
#include <sstream>
#include <cstring>
#include <libgen.h>

namespace livehumanai {
namespace utils {

using namespace utils;

bool FileUtils::createDirectory(const std::string& path) {
    struct stat st;
    if (stat(path.c_str(), &st) == 0 && S_ISDIR(st.st_mode)) {
        return true;
    }
    
    if (mkdir(path.c_str(), 0755) == 0) {
        LOGD("FileUtils: Created directory %s", path.c_str());
        return true;
    }
    
    LOGE("FileUtils: Failed to create directory %s: %s", 
         path.c_str(), strerror(errno));
    return false;
}

bool FileUtils::directoryExists(const std::string& path) {
    struct stat st;
    return stat(path.c_str(), &st) == 0 && S_ISDIR(st.st_mode);
}

bool FileUtils::fileExists(const std::string& path) {
    struct stat st;
    return stat(path.c_str(), &st) == 0 && S_ISREG(st.st_mode);
}

bool FileUtils::deleteFile(const std::string& path) {
    if (remove(path.c_str()) == 0) {
        LOGD("FileUtils: Deleted file %s", path.c_str());
        return true;
    }
    LOGE("FileUtils: Failed to delete file %s", path.c_str());
    return false;
}

bool FileUtils::deleteDirectory(const std::string& path) {
    // Simple implementation - would need recursive delete for production
    if (rmdir(path.c_str()) == 0) {
        LOGD("FileUtils: Deleted directory %s", path.c_str());
        return true;
    }
    LOGE("FileUtils: Failed to delete directory %s", path.c_str());
    return false;
}

size_t FileUtils::getFileSize(const std::string& path) {
    struct stat st;
    if (stat(path.c_str(), &st) != 0) {
        return 0;
    }
    return static_cast<size_t>(st.st_size);
}

std::string FileUtils::readFile(const std::string& path) {
    std::ifstream file(path);
    if (!file.is_open()) {
        LOGE("FileUtils: Failed to open file %s", path.c_str());
        return "";
    }
    
    std::stringstream buffer;
    buffer << file.rdbuf();
    return buffer.str();
}

bool FileUtils::writeFile(const std::string& path, const std::string& content) {
    std::ofstream file(path);
    if (!file.is_open()) {
        LOGE("FileUtils: Failed to open file for writing %s", path.c_str());
        return false;
    }
    
    file << content;
    return file.good();
}

bool FileUtils::copyFile(const std::string& src, const std::string& dst) {
    std::ifstream source(src, std::ios::binary);
    std::ofstream dest(dst, std::ios::binary);
    
    if (!source.is_open() || !dest.is_open()) {
        return false;
    }
    
    dest << source.rdbuf();
    return dest.good() && source.good();
}

std::string FileUtils::getFilename(const std::string& path) {
    char* pathCopy = strdup(path.c_str());
    char* filename = basename(pathCopy);
    std::string result(filename);
    free(pathCopy);
    return result;
}

std::string FileUtils::getDirectory(const std::string& path) {
    char* pathCopy = strdup(path.c_str());
    char* dir = dirname(pathCopy);
    std::string result(dir);
    free(pathCopy);
    return result;
}

std::string FileUtils::joinPath(const std::string& base, const std::string& path) {
    if (base.empty()) return path;
    if (path.empty()) return base;
    
    if (base.back() == '/') {
        return base + path;
    }
    return base + "/" + path;
}

} // namespace utils
} // namespace livehumanai

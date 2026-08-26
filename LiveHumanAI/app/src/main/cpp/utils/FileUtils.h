#ifndef LIVEHUMANAI_FILEUTILS_H
#define LIVEHUMANAI_FILEUTILS_H

#include <string>
#include <vector>

namespace livehumanai {
namespace utils {

class FileUtils {
public:
    static bool createDirectory(const std::string& path);
    static bool directoryExists(const std::string& path);
    static bool fileExists(const std::string& path);
    static bool deleteFile(const std::string& path);
    static bool deleteDirectory(const std::string& path);
    static size_t getFileSize(const std::string& path);
    static std::string readFile(const std::string& path);
    static bool writeFile(const std::string& path, const std::string& content);
    static bool copyFile(const std::string& src, const std::string& dst);
    static std::string getFilename(const std::string& path);
    static std::string getDirectory(const std::string& path);
    static std::string joinPath(const std::string& base, const std::string& path);
};

} // namespace utils
} // namespace livehumanai

#endif

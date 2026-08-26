#ifndef LIVEHUMANAI_STRINGUTILS_H
#define LIVEHUMANAI_STRINGUTILS_H

#include <string>
#include <vector>

namespace livehumanai {
namespace utils {

class StringUtils {
public:
    static std::string trim(const std::string& str);
    static std::string toLower(const std::string& str);
    static std::string toUpper(const std::string& str);
    static std::vector<std::string> split(const std::string& str, char delimiter);
    static std::string join(const std::vector<std::string>& parts, const std::string& delimiter);
    static bool startsWith(const std::string& str, const std::string& prefix);
    static bool endsWith(const std::string& str, const std::string& suffix);
    static std::string replaceAll(const std::string& str, const std::string& from, const std::string& to);
};

} // namespace utils
} // namespace livehumanai

#endif

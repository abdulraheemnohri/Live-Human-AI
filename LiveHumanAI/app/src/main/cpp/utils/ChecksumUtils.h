#ifndef LIVEHUMANAI_CHECKSUMUTILS_H
#define LIVEHUMANAI_CHECKSUMUTILS_H

#include <string>

namespace livehumanai {
namespace utils {

class ChecksumUtils {
public:
    static std::string computeSHA256(const std::string& filePath);
    static bool verifySHA256(const std::string& filePath, const std::string& expectedHash);
};

} // namespace utils
} // namespace livehumanai

#endif

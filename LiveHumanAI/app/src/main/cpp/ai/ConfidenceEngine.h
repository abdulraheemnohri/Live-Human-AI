#ifndef LIVEHUMANAI_CONFIDENCEENGINE_H
#define LIVEHUMANAI_CONFIDENCEENGINE_H

namespace livehumanai {
namespace ai {

class ConfidenceEngine {
public:
    static float computeConfidence(const std::string& input);
};

} // namespace ai
} // namespace livehumanai

#endif

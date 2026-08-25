#ifndef JALEBI_NATIVE_PERCEPTION_H
#define JALEBI_NATIVE_PERCEPTION_H

#include "JalebiWorldState.h"
#include <cstdint>
#include <string>
#include <vector>

namespace LiveHumanAI {

struct NativeFrameMetadata {
    int width = 0;
    int height = 0;
    int64_t timestampNs = 0;
    int rotationDegrees = 0;
};

struct NativeVisionObservation {
    NativeFrameMetadata frame;
    std::vector<std::string> objects;
    std::vector<std::string> text;
    std::string scene;
    float confidence = 0.0f;
};

class JalebiNativePerception {
public:
    JalebiWorldState toWorldState(const NativeVisionObservation& observation) const {
        JalebiWorldState state;
        state.timestampMs = observation.frame.timestampNs / 1000000LL;
        state.sceneId = observation.scene;
        state.detectedObjects = observation.objects;
        state.detectedText = observation.text;
        state.permissionAvailable = true;
        return state;
    }

    static bool isMeaningful(const NativeVisionObservation& observation) {
        return !observation.scene.empty() || !observation.objects.empty() || !observation.text.empty();
    }
};

} // namespace LiveHumanAI
#endif

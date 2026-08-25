#include "JNIBridge.h"
#include "../ai/JalebiNativeRuntime.h"
#include <string>

extern LiveHumanAI::JalebiNativeRuntime g_jalebiRuntime;

JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeReplanJalebiLoop(
    JNIEnv* env, jobject, jint loopId, jstring reason) {
    if (loopId <= 0 || loopId != g_jalebiRuntime.activeLoop() || !g_jalebiRuntime.initialized()) {
        return JNI_FALSE;
    }
    const std::string why = reason ? jstringToString(env, reason) : std::string("replan_requested");
    auto& engine = g_jalebiRuntime.engine();
    return engine.replanLoop(loopId, why) ? JNI_TRUE : JNI_FALSE;
}

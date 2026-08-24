#include "JNIBridge.h"
#include "../core/NativeCore.h"
#include "../core/NativeEngine.h"
#include "../ai/AIEngine.h"
#include "../ai/JalebiLoopEngine.h"
#include <string>

static LiveHumanAI::JalebiLoopEngine g_jalebiLoopEngine;
static NativeCore* g_nativeCore = nullptr;

std::string jstringToString(JNIEnv* env, jstring jstr) {
    if (!jstr) return "";
    const char* chars = env->GetStringUTFChars(jstr, nullptr);
    if (!chars) return "";
    std::string str(chars);
    env->ReleaseStringUTFChars(jstr, chars);
    return str;
}

jstring stringToJstring(JNIEnv* env, const std::string& str) {
    return env->NewStringUTF(str.c_str());
}

JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeInitialize(JNIEnv*, jobject) {
    if (!g_jalebiLoopEngine.initialize()) return 0;
    if (!g_nativeCore) {
        g_nativeCore = new NativeCore();
        if (!g_nativeCore->initialize()) {
            delete g_nativeCore;
            g_nativeCore = nullptr;
            return 0;
        }
    }
    return reinterpret_cast<jlong>(g_nativeCore);
}

JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeShutdown(JNIEnv*, jobject, jlong nativeHandle) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (core) {
        core->shutdown();
        delete core;
    }
    g_nativeCore = nullptr;
    g_jalebiLoopEngine.shutdown();
}

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetVersion(JNIEnv* env, jobject, jlong nativeHandle) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    return core ? stringToJstring(env, core->getVersion()) : nullptr;
}

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetRuntimeStatus(JNIEnv* env, jobject, jlong nativeHandle) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    return core ? stringToJstring(env, core->getRuntimeStatus()) : nullptr;
}

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetDeviceProfile(JNIEnv* env, jobject, jlong nativeHandle) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    return core ? stringToJstring(env, core->getDeviceProfile()) : nullptr;
}

JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeLoadModel(JNIEnv* env, jobject, jlong nativeHandle, jstring modelName) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (!core) return JNI_FALSE;
    return core->getAIEngine()->loadModel(jstringToString(env, modelName)) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeUnloadModel(JNIEnv* env, jobject, jlong nativeHandle, jstring modelName) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (!core) return JNI_FALSE;
    return core->getAIEngine()->unloadModel(jstringToString(env, modelName)) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGenerate(JNIEnv* env, jobject, jlong nativeHandle, jstring prompt, jstring modelName, jfloat temperature, jint maxTokens) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (!core) return nullptr;
    return stringToJstring(env, core->getAIEngine()->generate(
        jstringToString(env, prompt), jstringToString(env, modelName), temperature, maxTokens));
}

JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeStopGeneration(JNIEnv*, jobject, jlong nativeHandle) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (core) core->getAIEngine()->stopGeneration();
}

JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetTotalRAM(JNIEnv*, jobject, jlong nativeHandle) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    return core ? static_cast<jlong>(core->getTotalRAM()) : 0;
}

JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetAvailableRAM(JNIEnv*, jobject, jlong nativeHandle) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    return core ? static_cast<jlong>(core->getAvailableRAM()) : 0;
}

JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetRAMUsagePercentage(JNIEnv*, jobject, jlong nativeHandle) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    return core ? core->getRAMUsagePercentage() : 0.0f;
}

JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetCPUUsage(JNIEnv*, jobject, jlong nativeHandle) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    return core ? core->getEngine()->getCPUUsage() : 0.0f;
}

JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetTemperature(JNIEnv*, jobject, jlong nativeHandle) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    return core ? core->getEngine()->getTemperature() : 0.0f;
}

JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetBatteryLevel(JNIEnv*, jobject, jlong nativeHandle) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    return core ? core->getEngine()->getBatteryLevel() : 0.0f;
}

JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeSetPerformanceMode(JNIEnv*, jobject, jlong nativeHandle, jint mode) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (core) core->setPerformanceMode(static_cast<NativeCore::PerformanceMode>(mode));
}

JNIEXPORT jint JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetPerformanceMode(JNIEnv*, jobject, jlong nativeHandle) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    return core ? static_cast<jint>(core->getPerformanceMode()) : -1;
}

JNIEXPORT jint JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeCreateJalebiLoop(JNIEnv* env, jobject, jstring goal, jint maxIterations) {
    return g_jalebiLoopEngine.createLoop(jstringToString(env, goal), maxIterations);
}

JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeStartJalebiLoop(JNIEnv*, jobject, jint loopId) {
    return g_jalebiLoopEngine.startLoop(loopId) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativePauseJalebiLoop(JNIEnv*, jobject, jint loopId) {
    return g_jalebiLoopEngine.pauseLoop(loopId) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeResumeJalebiLoop(JNIEnv*, jobject, jint loopId) {
    return g_jalebiLoopEngine.resumeLoop(loopId) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeCancelJalebiLoop(JNIEnv*, jobject, jint loopId) {
    return g_jalebiLoopEngine.cancelLoop(loopId) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeExecuteJalebiIteration(JNIEnv* env, jobject, jint loopId, jstring input) {
    const auto iteration = g_jalebiLoopEngine.executeIteration(loopId, jstringToString(env, input));
    return stringToJstring(env, g_jalebiLoopEngine.getLoopHistoryJson(loopId));
}

JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeEvaluateJalebiLoop(JNIEnv* env, jobject, jint loopId, jfloat confidence, jboolean goalCompleted, jstring evaluation, jstring nextAction, jstring memoryUpdates) {
    return g_jalebiLoopEngine.recordEvaluation(
        loopId,
        confidence,
        goalCompleted == JNI_TRUE,
        jstringToString(env, evaluation),
        jstringToString(env, nextAction),
        jstringToString(env, memoryUpdates)) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetJalebiLoopState(JNIEnv* env, jobject, jint loopId) {
    return stringToJstring(env, g_jalebiLoopEngine.getStateName(g_jalebiLoopEngine.getLoopState(loopId)));
}

JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetJalebiConfidence(JNIEnv*, jobject, jint loopId) {
    return g_jalebiLoopEngine.getLatestConfidence(loopId);
}

JNIEXPORT jint JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetJalebiIteration(JNIEnv*, jobject, jint loopId) {
    return g_jalebiLoopEngine.getCurrentIteration(loopId);
}

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetJalebiHistory(JNIEnv* env, jobject, jint loopId) {
    return stringToJstring(env, g_jalebiLoopEngine.getLoopHistoryJson(loopId));
}

JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeCompleteJalebiLoop(JNIEnv*, jobject, jint loopId) {
    return g_jalebiLoopEngine.completeLoop(loopId) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeFailJalebiLoop(JNIEnv* env, jobject, jint loopId, jstring reason) {
    return g_jalebiLoopEngine.failLoop(loopId, jstringToString(env, reason)) ? JNI_TRUE : JNI_FALSE;
}

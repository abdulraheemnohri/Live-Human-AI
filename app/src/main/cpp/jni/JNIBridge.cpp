#include "JNIBridge.h"
#include "../core/NativeCore.h"
#include <string>

// Global reference to NativeCore
static NativeCore* g_nativeCore = nullptr;

// Helper function to convert Java string to C++ string
std::string jstringToString(JNIEnv* env, jstring jstr) {
    if (!jstr) return "";
    const char* chars = env->GetStringUTFChars(jstr, nullptr);
    std::string str(chars);
    env->ReleaseStringUTFChars(jstr, chars);
    return str;
}

// Helper function to convert C++ string to Java string
jstring stringToJstring(JNIEnv* env, const std::string& str) {
    return env->NewStringUTF(str.c_str());
}

// NativeCore functions
JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeInitialize(
    JNIEnv* env,
    jobject /* this */
) {
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

JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeShutdown(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (core) {
        core->shutdown();
        delete core;
    }
    g_nativeCore = nullptr;
}

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetVersion(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (!core) return nullptr;
    return stringToJstring(env, core->getVersion());
}

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetRuntimeStatus(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (!core) return nullptr;
    return stringToJstring(env, core->getRuntimeStatus());
}

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetDeviceProfile(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (!core) return nullptr;
    return stringToJstring(env, core->getDeviceProfile());
}

// AI Engine functions
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeLoadModel(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle,
    jstring modelName
) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (!core) return JNI_FALSE;

    std::string model = jstringToString(env, modelName);
    return core->getAIEngine()->loadModel(model) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeUnloadModel(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle,
    jstring modelName
) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (!core) return JNI_FALSE;

    std::string model = jstringToString(env, modelName);
    return core->getAIEngine()->unloadModel(model) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGenerate(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle,
    jstring prompt,
    jstring modelName,
    jfloat temperature,
    jint maxTokens
) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (!core) return nullptr;

    std::string promptStr = jstringToString(env, prompt);
    std::string model = jstringToString(env, modelName);
    std::string result = core->getAIEngine()->generate(promptStr, model, temperature, maxTokens);
    return stringToJstring(env, result);
}

JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeStopGeneration(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (core) {
        core->getAIEngine()->stopGeneration();
    }
}

// Hardware monitoring functions
JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetTotalRAM(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (!core) return 0;
    return static_cast<jlong>(core->getTotalRAM());
}

JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetAvailableRAM(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (!core) return 0;
    return static_cast<jlong>(core->getAvailableRAM());
}

JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetRAMUsagePercentage(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (!core) return 0.0f;
    return core->getRAMUsagePercentage();
}

JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetCPUUsage(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (!core) return 0.0f;
    return core->getEngine()->getCPUUsage();
}

JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetTemperature(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (!core) return 0.0f;
    return core->getEngine()->getTemperature();
}

JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetBatteryLevel(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (!core) return 0.0f;
    return core->getEngine()->getBatteryLevel();
}

// Performance mode functions
JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeSetPerformanceMode(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle,
    jint mode
) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (core) {
        NativeCore::PerformanceMode perfMode = static_cast<NativeCore::PerformanceMode>(mode);
        core->setPerformanceMode(perfMode);
    }
}

JNIEXPORT jint JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetPerformanceMode(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
) {
    NativeCore* core = reinterpret_cast<NativeCore*>(nativeHandle);
    if (!core) return -1;
    return static_cast<jint>(core->getPerformanceMode());
}

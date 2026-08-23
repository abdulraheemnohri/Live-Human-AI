#ifndef LIVE_HUMAN_AI_JNI_BRIDGE_H
#define LIVE_HUMAN_AI_JNI_BRIDGE_H

#include <jni.h>
#include <string>

// Forward declarations
class NativeCore;

// JNIBridge provides the interface between Java/Kotlin and native C++ code
extern "C" {

// NativeCore functions
JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeInitialize(
    JNIEnv* env,
    jobject /* this */
);

JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeShutdown(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
);

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetVersion(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
);

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetRuntimeStatus(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
);

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetDeviceProfile(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
);

// AI Engine functions
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeLoadModel(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle,
    jstring modelName
);

JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeUnloadModel(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle,
    jstring modelName
);

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGenerate(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle,
    jstring prompt,
    jstring modelName,
    jfloat temperature,
    jint maxTokens
);

JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeStopGeneration(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
);

// Hardware monitoring functions
JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetTotalRAM(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
);

JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetAvailableRAM(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
);

JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetRAMUsagePercentage(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
);

JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetCPUUsage(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
);

JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetTemperature(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
);

JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetBatteryLevel(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
);

// Performance mode functions
JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeSetPerformanceMode(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle,
    jint mode
);

JNIEXPORT jint JNICALL Java_com_livehumanai_livehumanai_native_NativeBridge_nativeGetPerformanceMode(
    JNIEnv* env,
    jobject /* this */,
    jlong nativeHandle
);

}

#endif // LIVE_HUMAN_AI_JNI_BRIDGE_H

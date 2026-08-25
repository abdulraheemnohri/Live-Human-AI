#ifndef LIVE_HUMAN_AI_JNI_BRIDGE_H
#define LIVE_HUMAN_AI_JNI_BRIDGE_H

#include <jni.h>

class NativeCore;

extern "C" {

JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeInitialize(JNIEnv*, jobject);
JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeShutdown(JNIEnv*, jobject, jlong);
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetVersion(JNIEnv*, jobject, jlong);
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetRuntimeStatus(JNIEnv*, jobject, jlong);
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetDeviceProfile(JNIEnv*, jobject, jlong);

JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeLoadModel(JNIEnv*, jobject, jlong, jstring);
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeUnloadModel(JNIEnv*, jobject, jlong, jstring);
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGenerate(JNIEnv*, jobject, jlong, jstring, jstring, jfloat, jint);
JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeStopGeneration(JNIEnv*, jobject, jlong);

JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetTotalRAM(JNIEnv*, jobject, jlong);
JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetAvailableRAM(JNIEnv*, jobject, jlong);
JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetRAMUsagePercentage(JNIEnv*, jobject, jlong);
JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetCPUUsage(JNIEnv*, jobject, jlong);
JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetTemperature(JNIEnv*, jobject, jlong);
JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetBatteryLevel(JNIEnv*, jobject, jlong);

JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeSetPerformanceMode(JNIEnv*, jobject, jlong, jint);
JNIEXPORT jint JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetPerformanceMode(JNIEnv*, jobject, jlong);

JNIEXPORT jint JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeCreateJalebiLoop(JNIEnv*, jobject, jstring, jint);
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeStartJalebiLoop(JNIEnv*, jobject, jint);
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativePauseJalebiLoop(JNIEnv*, jobject, jint);
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeResumeJalebiLoop(JNIEnv*, jobject, jint);
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeCancelJalebiLoop(JNIEnv*, jobject, jint);
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeExecuteJalebiIteration(JNIEnv*, jobject, jint, jstring);
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeEvaluateJalebiLoop(JNIEnv*, jobject, jint, jfloat, jboolean, jstring, jstring, jstring);
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetJalebiLoopState(JNIEnv*, jobject, jint);
JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetJalebiConfidence(JNIEnv*, jobject, jint);
JNIEXPORT jint JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetJalebiIteration(JNIEnv*, jobject, jint);
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetJalebiHistory(JNIEnv*, jobject, jint);
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeCompleteJalebiLoop(JNIEnv*, jobject, jint);
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeFailJalebiLoop(JNIEnv*, jobject, jint, jstring);

}

#endif // LIVE_HUMAN_AI_JNI_BRIDGE_H

#ifndef LIVE_HUMAN_AI_NATIVE_SPEECH_BRIDGE_H
#define LIVE_HUMAN_AI_NATIVE_SPEECH_BRIDGE_H

#include <jni.h>

extern "C" {
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeLoadSpeechModel(JNIEnv*, jobject, jlong, jstring);
JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeUnloadSpeechModel(JNIEnv*, jobject, jlong);
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeIsSpeechModelLoaded(JNIEnv*, jobject, jlong);
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeTranscribePcm(JNIEnv*, jobject, jlong, jshortArray, jint, jint);
JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeStopSpeech(JNIEnv*, jobject, jlong);
}

#endif

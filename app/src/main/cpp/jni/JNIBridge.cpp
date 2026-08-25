#include "JNIBridge.h"
#include "../core/NativeCore.h"
#include "../core/NativeEngine.h"
#include "../ai/AIEngine.h"
#include "../ai/JalebiNativeRuntime.h"
#include <string>

static LiveHumanAI::JalebiNativeRuntime g_jalebiRuntime;
static NativeCore* g_nativeCore = nullptr;

std::string jstringToString(JNIEnv* env, jstring jstr) {
    if (!jstr) return {};
    const char* chars = env->GetStringUTFChars(jstr, nullptr);
    if (!chars) return {};
    std::string result(chars);
    env->ReleaseStringUTFChars(jstr, chars);
    return result;
}

jstring stringToJstring(JNIEnv* env, const std::string& value) {
    return env->NewStringUTF(value.c_str());
}

static LiveHumanAI::JalebiResourceSnapshot resources(float ram, float cpu, float temperature, float battery) {
    LiveHumanAI::JalebiResourceSnapshot r;
    r.ramUsagePercent = ram;
    r.cpuUsagePercent = cpu;
    r.temperatureC = temperature;
    r.batteryPercent = battery;
    return r;
}

JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeInitialize(JNIEnv*, jobject) {
    if (!g_jalebiRuntime.initialize()) return 0;
    if (!g_nativeCore) {
        g_nativeCore = new NativeCore();
        if (!g_nativeCore->initialize()) {
            delete g_nativeCore;
            g_nativeCore = nullptr;
            g_jalebiRuntime.shutdown();
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
    g_jalebiRuntime.shutdown();
}

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetVersion(JNIEnv* env, jobject, jlong h) { auto* c = reinterpret_cast<NativeCore*>(h); return c ? stringToJstring(env, c->getVersion()) : nullptr; }
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetRuntimeStatus(JNIEnv* env, jobject, jlong h) { auto* c = reinterpret_cast<NativeCore*>(h); return c ? stringToJstring(env, c->getRuntimeStatus()) : nullptr; }
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetDeviceProfile(JNIEnv* env, jobject, jlong h) { auto* c = reinterpret_cast<NativeCore*>(h); return c ? stringToJstring(env, c->getDeviceProfile()) : nullptr; }
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeLoadModel(JNIEnv* env, jobject, jlong h, jstring n) { auto* c = reinterpret_cast<NativeCore*>(h); return c && c->getAIEngine()->loadModel(jstringToString(env, n)) ? JNI_TRUE : JNI_FALSE; }
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeUnloadModel(JNIEnv* env, jobject, jlong h, jstring n) { auto* c = reinterpret_cast<NativeCore*>(h); return c && c->getAIEngine()->unloadModel(jstringToString(env, n)) ? JNI_TRUE : JNI_FALSE; }
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGenerate(JNIEnv* env, jobject, jlong h, jstring p, jstring n, jfloat t, jint m) { auto* c = reinterpret_cast<NativeCore*>(h); return c ? stringToJstring(env, c->getAIEngine()->generate(jstringToString(env, p), jstringToString(env, n), t, m)) : nullptr; }
JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeStopGeneration(JNIEnv*, jobject, jlong h) { auto* c = reinterpret_cast<NativeCore*>(h); if (c) c->getAIEngine()->stopGeneration(); }
JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetTotalRAM(JNIEnv*, jobject, jlong h) { auto* c = reinterpret_cast<NativeCore*>(h); return c ? static_cast<jlong>(c->getTotalRAM()) : 0; }
JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetAvailableRAM(JNIEnv*, jobject, jlong h) { auto* c = reinterpret_cast<NativeCore*>(h); return c ? static_cast<jlong>(c->getAvailableRAM()) : 0; }
JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetRAMUsagePercentage(JNIEnv*, jobject, jlong h) { auto* c = reinterpret_cast<NativeCore*>(h); return c ? c->getRAMUsagePercentage() : 0.0f; }
JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetCPUUsage(JNIEnv*, jobject, jlong h) { auto* c = reinterpret_cast<NativeCore*>(h); return c ? c->getEngine()->getCPUUsage() : 0.0f; }
JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetTemperature(JNIEnv*, jobject, jlong h) { auto* c = reinterpret_cast<NativeCore*>(h); return c ? c->getEngine()->getTemperature() : 0.0f; }
JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetBatteryLevel(JNIEnv*, jobject, jlong h) { auto* c = reinterpret_cast<NativeCore*>(h); return c ? c->getEngine()->getBatteryLevel() : 0.0f; }
JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeSetPerformanceMode(JNIEnv*, jobject, jlong h, jint m) { auto* c = reinterpret_cast<NativeCore*>(h); if (c) c->setPerformanceMode(static_cast<NativeCore::PerformanceMode>(m)); }
JNIEXPORT jint JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetPerformanceMode(JNIEnv*, jobject, jlong h) { auto* c = reinterpret_cast<NativeCore*>(h); return c ? static_cast<jint>(c->getPerformanceMode()) : -1; }

JNIEXPORT jint JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeCreateJalebiLoop(JNIEnv* env, jobject, jstring goal, jint maxIterations) { return g_jalebiRuntime.createGoal(jstringToString(env, goal), maxIterations); }
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeStartJalebiLoop(JNIEnv*, jobject, jint id) { return id == g_jalebiRuntime.activeLoop() && g_jalebiRuntime.start() ? JNI_TRUE : JNI_FALSE; }
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativePauseJalebiLoop(JNIEnv*, jobject, jint id) { return id == g_jalebiRuntime.activeLoop() && g_jalebiRuntime.pause() ? JNI_TRUE : JNI_FALSE; }
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeResumeJalebiLoop(JNIEnv*, jobject, jint id) { return id == g_jalebiRuntime.activeLoop() && g_jalebiRuntime.resume() ? JNI_TRUE : JNI_FALSE; }
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeCancelJalebiLoop(JNIEnv*, jobject, jint id) { return id == g_jalebiRuntime.activeLoop() && g_jalebiRuntime.cancel() ? JNI_TRUE : JNI_FALSE; }

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeSubmitJalebiVision(
    JNIEnv* env, jobject, jint id, jstring sceneId, jstring objects, jstring text,
    jfloat confidence, jfloat ram, jfloat cpu, jfloat temperature, jfloat battery, jboolean flagship) {
    if (id != g_jalebiRuntime.activeLoop()) return stringToJstring(env, "{\"runInference\":false,\"reason\":\"invalid_loop\"}");
    return stringToJstring(env, g_jalebiRuntime.submitVision(
        jstringToString(env, sceneId), jstringToString(env, objects), jstringToString(env, text),
        confidence, resources(ram, cpu, temperature, battery), flagship == JNI_TRUE));
}

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeSubmitJalebiSpeech(
    JNIEnv* env, jobject, jint id, jstring transcript, jfloat confidence, jboolean isFinal,
    jfloat ram, jfloat cpu, jfloat temperature, jfloat battery, jboolean flagship) {
    if (id != g_jalebiRuntime.activeLoop()) return stringToJstring(env, "{\"runInference\":false,\"reason\":\"invalid_loop\"}");
    return stringToJstring(env, g_jalebiRuntime.submitSpeech(
        jstringToString(env, transcript), confidence, isFinal == JNI_TRUE,
        resources(ram, cpu, temperature, battery), flagship == JNI_TRUE));
}

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeExecuteJalebiIteration(JNIEnv* env, jobject, jint id, jstring input) {
    if (id != g_jalebiRuntime.activeLoop()) return stringToJstring(env, "[]");
    g_jalebiRuntime.execute(jstringToString(env, input));
    return stringToJstring(env, g_jalebiRuntime.engine().getLoopHistoryJson(id));
}

JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeEvaluateJalebiLoop(JNIEnv* env, jobject, jint id, jfloat confidence, jboolean complete, jstring evaluation, jstring nextAction, jstring memoryUpdates) {
    if (id != g_jalebiRuntime.activeLoop()) return JNI_FALSE;
    return g_jalebiRuntime.evaluate(confidence, complete == JNI_TRUE, jstringToString(env, evaluation), jstringToString(env, nextAction), jstringToString(env, memoryUpdates)) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetJalebiLoopState(JNIEnv* env, jobject, jint id) {
    if (id != g_jalebiRuntime.activeLoop()) return stringToJstring(env, "IDLE");
    auto& engine = g_jalebiRuntime.engine();
    return stringToJstring(env, engine.getStateName(engine.getLoopState(id)));
}
JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetJalebiConfidence(JNIEnv*, jobject, jint id) { return id == g_jalebiRuntime.activeLoop() ? g_jalebiRuntime.engine().getLatestConfidence(id) : 0.0f; }
JNIEXPORT jint JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetJalebiIteration(JNIEnv*, jobject, jint id) { return id == g_jalebiRuntime.activeLoop() ? g_jalebiRuntime.engine().getCurrentIteration(id) : 0; }
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetJalebiHistory(JNIEnv* env, jobject, jint id) { return id == g_jalebiRuntime.activeLoop() ? stringToJstring(env, g_jalebiRuntime.engine().getLoopHistoryJson(id)) : stringToJstring(env, "[]"); }
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeCompleteJalebiLoop(JNIEnv*, jobject, jint id) { return id == g_jalebiRuntime.activeLoop() && g_jalebiRuntime.engine().completeLoop(id) ? JNI_TRUE : JNI_FALSE; }
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeFailJalebiLoop(JNIEnv* env, jobject, jint id, jstring reason) { return id == g_jalebiRuntime.activeLoop() && g_jalebiRuntime.engine().failLoop(id, jstringToString(env, reason)) ? JNI_TRUE : JNI_FALSE; }

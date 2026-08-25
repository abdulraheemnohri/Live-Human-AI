#include "JNIBridge.h"
#include "NativeSpeechBridge.h"
#include "../core/NativeCore.h"
#include "../core/NativeEngine.h"
#include "../ai/AIEngine.h"
#include "../ai/JalebiNativeRuntime.h"
#include "../audio/SpeechManager.h"
#include <string>
#include <vector>

static LiveHumanAI::JalebiNativeRuntime g_jalebiRuntime;
static NativeCore* g_nativeCore = nullptr;
static SpeechManager g_speechManager;

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
    r.ramUsagePercent = ram; r.cpuUsagePercent = cpu; r.temperatureC = temperature; r.batteryPercent = battery;
    return r;
}

JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeInitialize(JNIEnv*, jobject) {
    if (!g_jalebiRuntime.initialize()) return 0;
    if (!g_nativeCore) {
        g_nativeCore = new NativeCore();
        if (!g_nativeCore->initialize()) { delete g_nativeCore; g_nativeCore=nullptr; g_jalebiRuntime.shutdown(); return 0; }
    }
    return reinterpret_cast<jlong>(g_nativeCore);
}
JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeShutdown(JNIEnv*, jobject, jlong h) {
    g_speechManager.unloadModel();
    auto* core=reinterpret_cast<NativeCore*>(h); if(core){core->shutdown();delete core;} g_nativeCore=nullptr; g_jalebiRuntime.shutdown();
}
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetVersion(JNIEnv* e,jobject,jlong h){auto*c=reinterpret_cast<NativeCore*>(h);return c?stringToJstring(e,c->getVersion()):nullptr;}
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetRuntimeStatus(JNIEnv* e,jobject,jlong h){auto*c=reinterpret_cast<NativeCore*>(h);return c?stringToJstring(e,c->getRuntimeStatus()):nullptr;}
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetDeviceProfile(JNIEnv* e,jobject,jlong h){auto*c=reinterpret_cast<NativeCore*>(h);return c?stringToJstring(e,c->getDeviceProfile()):nullptr;}
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeLoadModel(JNIEnv* e,jobject,jlong h,jstring n){auto*c=reinterpret_cast<NativeCore*>(h);return c&&c->getAIEngine()->loadModel(jstringToString(e,n))?JNI_TRUE:JNI_FALSE;}
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeUnloadModel(JNIEnv* e,jobject,jlong h,jstring n){auto*c=reinterpret_cast<NativeCore*>(h);return c&&c->getAIEngine()->unloadModel(jstringToString(e,n))?JNI_TRUE:JNI_FALSE;}
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGenerate(JNIEnv* e,jobject,jlong h,jstring p,jstring n,jfloat t,jint m){auto*c=reinterpret_cast<NativeCore*>(h);return c?stringToJstring(e,c->getAIEngine()->generate(jstringToString(e,p),jstringToString(e,n),t,m)):nullptr;}
JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeStopGeneration(JNIEnv*,jobject,jlong h){auto*c=reinterpret_cast<NativeCore*>(h);if(c)c->getAIEngine()->stopGeneration();}
JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetTotalRAM(JNIEnv*,jobject,jlong h){auto*c=reinterpret_cast<NativeCore*>(h);return c?static_cast<jlong>(c->getTotalRAM()):0;}
JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetAvailableRAM(JNIEnv*,jobject,jlong h){auto*c=reinterpret_cast<NativeCore*>(h);return c?static_cast<jlong>(c->getAvailableRAM()):0;}
JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetRAMUsagePercentage(JNIEnv*,jobject,jlong h){auto*c=reinterpret_cast<NativeCore*>(h);return c?c->getRAMUsagePercentage():0.0f;}
JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetCPUUsage(JNIEnv*,jobject,jlong h){auto*c=reinterpret_cast<NativeCore*>(h);return c?c->getEngine()->getCPUUsage():0.0f;}
JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetTemperature(JNIEnv*,jobject,jlong h){auto*c=reinterpret_cast<NativeCore*>(h);return c?c->getEngine()->getTemperature():0.0f;}
JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetBatteryLevel(JNIEnv*,jobject,jlong h){auto*c=reinterpret_cast<NativeCore*>(h);return c?c->getEngine()->getBatteryLevel():0.0f;}
JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeSetPerformanceMode(JNIEnv*,jobject,jlong h,jint m){auto*c=reinterpret_cast<NativeCore*>(h);if(c)c->setPerformanceMode(static_cast<NativeCore::PerformanceMode>(m));}
JNIEXPORT jint JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetPerformanceMode(JNIEnv*,jobject,jlong h){auto*c=reinterpret_cast<NativeCore*>(h);return c?static_cast<jint>(c->getPerformanceMode()):-1;}

JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeLoadSpeechModel(JNIEnv* e,jobject,jlong h,jstring path){if(!h||!g_nativeCore)return JNI_FALSE;return g_speechManager.loadModel(jstringToString(e,path))?JNI_TRUE:JNI_FALSE;}
JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeUnloadSpeechModel(JNIEnv*,jobject,jlong){g_speechManager.unloadModel();}
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeIsSpeechModelLoaded(JNIEnv*,jobject,jlong){return g_speechManager.isLoaded()?JNI_TRUE:JNI_FALSE;}
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeTranscribePcm(JNIEnv* e,jobject,jlong h,jshortArray samples,jint sampleRate,jint offset){
    if(!h||!samples||sampleRate<=0||offset<0)return stringToJstring(e,{});
    const jsize n=e->GetArrayLength(samples); if(offset>=n)return stringToJstring(e,{});
    jsize count=n-offset; std::vector<jshort> pcm16(static_cast<size_t>(count)); e->GetShortArrayRegion(samples,offset,count,pcm16.data());
    std::vector<float> pcm(static_cast<size_t>(count)); for(jsize i=0;i<count;++i) pcm[static_cast<size_t>(i)]=static_cast<float>(pcm16[static_cast<size_t>(i)])/32768.0f;
    return stringToJstring(e,g_speechManager.transcribe(pcm,sampleRate));
}
JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeStopSpeech(JNIEnv*,jobject,jlong){g_speechManager.stop();}

JNIEXPORT jint JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeCreateJalebiLoop(JNIEnv* e,jobject,jstring goal,jint maxIterations){return g_jalebiRuntime.createGoal(jstringToString(e,goal),maxIterations);}
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeStartJalebiLoop(JNIEnv*,jobject,jint id){return id==g_jalebiRuntime.activeLoop()&&g_jalebiRuntime.start()?JNI_TRUE:JNI_FALSE;}
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativePauseJalebiLoop(JNIEnv*,jobject,jint id){return id==g_jalebiRuntime.activeLoop()&&g_jalebiRuntime.pause()?JNI_TRUE:JNI_FALSE;}
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeResumeJalebiLoop(JNIEnv*,jobject,jint id){return id==g_jalebiRuntime.activeLoop()&&g_jalebiRuntime.resume()?JNI_TRUE:JNI_FALSE;}
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeCancelJalebiLoop(JNIEnv*,jobject,jint id){return id==g_jalebiRuntime.activeLoop()&&g_jalebiRuntime.cancel()?JNI_TRUE:JNI_FALSE;}
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeSubmitJalebiVision(JNIEnv* e,jobject,jint id,jstring sceneId,jstring objects,jstring text,jfloat confidence,jfloat ram,jfloat cpu,jfloat temperature,jfloat battery,jboolean flagship){if(id!=g_jalebiRuntime.activeLoop())return stringToJstring(e,"{\"runInference\":false,\"reason\":\"invalid_loop\"}");return stringToJstring(e,g_jalebiRuntime.submitVision(jstringToString(e,sceneId),jstringToString(e,objects),jstringToString(e,text),confidence,resources(ram,cpu,temperature,battery),flagship==JNI_TRUE));}
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeSubmitJalebiSpeech(JNIEnv* e,jobject,jint id,jstring transcript,jfloat confidence,jboolean isFinal,jfloat ram,jfloat cpu,jfloat temperature,jfloat battery,jboolean flagship){if(id!=g_jalebiRuntime.activeLoop())return stringToJstring(e,"{\"runInference\":false,\"reason\":\"invalid_loop\"}");return stringToJstring(e,g_jalebiRuntime.submitSpeech(jstringToString(e,transcript),confidence,isFinal==JNI_TRUE,resources(ram,cpu,temperature,battery),flagship==JNI_TRUE));}
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeExecuteJalebiIteration(JNIEnv* e,jobject,jint id,jstring input){if(id!=g_jalebiRuntime.activeLoop())return stringToJstring(e,"[]");g_jalebiRuntime.execute(jstringToString(e,input));return stringToJstring(e,g_jalebiRuntime.engine().getLoopHistoryJson(id));}
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeEvaluateJalebiLoop(JNIEnv* e,jobject,jint id,jfloat confidence,jboolean complete,jstring evaluation,jstring nextAction,jstring memoryUpdates){if(id!=g_jalebiRuntime.activeLoop())return JNI_FALSE;return g_jalebiRuntime.evaluate(confidence,complete==JNI_TRUE,jstringToString(e,evaluation),jstringToString(e,nextAction),jstringToString(e,memoryUpdates))?JNI_TRUE:JNI_FALSE;}
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetJalebiLoopState(JNIEnv* e,jobject,jint id){if(id!=g_jalebiRuntime.activeLoop())return stringToJstring(e,"IDLE");auto& engine=g_jalebiRuntime.engine();return stringToJstring(e,engine.getStateName(engine.getLoopState(id)));}
JNIEXPORT jfloat JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetJalebiConfidence(JNIEnv*,jobject,jint id){return id==g_jalebiRuntime.activeLoop()?g_jalebiRuntime.engine().getLatestConfidence(id):0.0f;}
JNIEXPORT jint JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetJalebiIteration(JNIEnv*,jobject,jint id){return id==g_jalebiRuntime.activeLoop()?g_jalebiRuntime.engine().getCurrentIteration(id):0;}
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeGetJalebiHistory(JNIEnv* e,jobject,jint id){return id==g_jalebiRuntime.activeLoop()?stringToJstring(e,g_jalebiRuntime.engine().getLoopHistoryJson(id)):stringToJstring(e,"[]");}
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeCompleteJalebiLoop(JNIEnv*,jobject,jint id){return id==g_jalebiRuntime.activeLoop()&&g_jalebiRuntime.engine().completeLoop(id)?JNI_TRUE:JNI_FALSE;}
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeFailJalebiLoop(JNIEnv* e,jobject,jint id,jstring reason){return id==g_jalebiRuntime.activeLoop()&&g_jalebiRuntime.engine().failLoop(id,jstringToString(e,reason))?JNI_TRUE:JNI_FALSE;}

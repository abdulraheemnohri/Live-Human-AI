#include "JNIBridge.h"
#include "NativeSpeechBridge.h"
#include "../core/NativeCore.h"
#include "../core/NativeEngine.h"
#include "../ai/AIEngine.h"
#include "../ai/JalebiNativeRuntime.h"
#include "../ai/vision/VisionManager.h"
#include "../audio/SpeechManager.h"
#include <string>
#include <vector>
#include <algorithm>
#ifdef HAVE_OPENCV
#include <opencv2/core.hpp>
#endif

LiveHumanAI::JalebiNativeRuntime g_jalebiRuntime;
static NativeCore* g_nativeCore = nullptr;
static SpeechManager g_speechManager;
static VisionManager g_visionManager;

std::string jstringToString(JNIEnv* env, jstring jstr) { if (!jstr) return {}; const char* chars=env->GetStringUTFChars(jstr,nullptr); if(!chars)return {}; std::string r(chars); env->ReleaseStringUTFChars(jstr,chars); return r; }
jstring stringToJstring(JNIEnv* env,const std::string& value){return env->NewStringUTF(value.c_str());}
static LiveHumanAI::JalebiResourceSnapshot resources(float ram,float cpu,float temperature,float battery){LiveHumanAI::JalebiResourceSnapshot r;r.ramUsagePercent=ram;r.cpuUsagePercent=cpu;r.temperatureC=temperature;r.batteryPercent=battery;return r;}
static std::string jsonEscape(const std::string& s){std::string o;for(char c:s){switch(c){case '"':o+="\\\"";break;case '\\':o+="\\\\";break;case '\n':o+="\\n";break;case '\r':o+="\\r";break;default:o+=c;}}return o;}
static std::string jsonArray(const std::vector<std::string>& values){std::string o="[";for(size_t i=0;i<values.size();++i){if(i)o+=",";o+="\""+jsonEscape(values[i])+"\"";}return o+"]";}

JNIEXPORT jlong JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeInitialize(JNIEnv*,jobject){if(!g_jalebiRuntime.initialize())return 0;if(!g_nativeCore){g_nativeCore=new NativeCore();if(!g_nativeCore->initialize()){delete g_nativeCore;g_nativeCore=nullptr;g_jalebiRuntime.shutdown();return 0;}g_visionManager.initialize();}return reinterpret_cast<jlong>(g_nativeCore);}
JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeShutdown(JNIEnv*,jobject,jlong h){g_speechManager.unloadModel();g_visionManager.shutdown();auto* core=reinterpret_cast<NativeCore*>(h);if(core){core->shutdown();delete core;}g_nativeCore=nullptr;g_jalebiRuntime.shutdown();}
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
JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeTranscribePcm(JNIEnv* e,jobject,jlong h,jshortArray samples,jint sampleRate,jint offset){if(!h||!samples||sampleRate<=0||offset<0)return stringToJstring(e,{});const jsize n=e->GetArrayLength(samples);if(offset>=n)return stringToJstring(e,{});jsize count=n-offset;std::vector<jshort> pcm16(static_cast<size_t>(count));e->GetShortArrayRegion(samples,offset,count,pcm16.data());std::vector<float> pcm(static_cast<size_t>(count));for(jsize i=0;i<count;++i)pcm[static_cast<size_t>(i)]=static_cast<float>(pcm16[static_cast<size_t>(i)])/32768.0f;return stringToJstring(e,g_speechManager.transcribe(pcm,sampleRate));}
JNIEXPORT void JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeStopSpeech(JNIEnv*,jobject,jlong){g_speechManager.stop();}

JNIEXPORT jstring JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeAnalyzeVisionRgba(JNIEnv* e,jobject,jlong h,jbyteArray rgba,jint width,jint height,jstring model){
#ifdef HAVE_OPENCV
    if(!h||!rgba||width<=0||height<=0||!g_nativeCore)return stringToJstring(e,{});
    const jsize size=e->GetArrayLength(rgba);const jsize expected=width*height*4;if(size<expected)return stringToJstring(e,{});
    jbyte* data=e->GetByteArrayElements(rgba,nullptr);if(!data)return stringToJstring(e,{});
    cv::Mat image(height,width,CV_8UC4,reinterpret_cast<unsigned char*>(data));cv::Mat bgr;cv::cvtColor(image,bgr,cv::COLOR_RGBA2BGR);
    const std::string modelName=jstringToString(e,model);std::vector<std::string> objects=g_visionManager.detectObjects(bgr,modelName);std::string textRaw=g_visionManager.detectText(bgr,modelName);std::string scene=g_visionManager.analyzeScene(bgr,modelName);
    e->ReleaseByteArrayElements(rgba,data,JNI_ABORT);
    std::vector<std::string> text;if(!textRaw.empty())text.push_back(textRaw);
    float confidence=0.0f;if(!objects.empty())confidence=0.80f;if(!text.empty())confidence=std::max(confidence,0.82f);if(objects.empty()&&text.empty()&&!scene.empty())confidence=0.55f;
    return stringToJstring(e,"{\"sceneId\":\""+jsonEscape(scene.empty()?"camera":scene)+"\",\"objects\":"+jsonArray(objects)+",\"text\":"+jsonArray(text)+",\"confidence\":"+std::to_string(confidence)+"}");
#else
    (void)h;(void)rgba;(void)width;(void)height;(void)model;return stringToJstring(e,"{\"sceneId\":\"camera\",\"objects\":[],\"text\":[],\"confidence\":0.0}");
#endif
}

JNIEXPORT jint JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeCreateJalebiLoop(JNIEnv* e,jobject,jstring goal,jint maxIterations){return g_jalebiRuntime.createGoal(jstringToString(e,goal),maxIterations);}
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeStartJalebiLoop(JNIEnv*,jobject,jint id){return id==g_jalebiRuntime.activeLoop()&&g_jalebiRuntime.start()?JNI_TRUE:JNI_FALSE;}
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativePauseJalebiLoop(JNIEnv*,jobject,jint id){return id==g_jalebiRuntime.activeLoop()&&g_jalebiRuntime.pause()?JNI_TRUE:JNI_FALSE;}
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeResumeJalebiLoop(JNIEnv*,jobject,jint id){return id==g_jalebiRuntime.activeLoop()&&g_jalebiRuntime.resume()?JNI_TRUE:JNI_FALSE;}
JNIEXPORT jboolean JNICALL Java_com_livehumanai_livehumanai_nativebridge_NativeBridge_nativeReplanJalebiLoop(JNIEnv* e,jobject,jint id,jstring reason){if(id!=g_jalebiRuntime.activeLoop()||!g_jalebiRuntime.initialized())return JNI_FALSE;return g_jalebiRuntime.engine().replanLoop(id,reason?jstringToString(e,reason):std::string("replan_requested"))?JNI_TRUE:JNI_FALSE;}
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

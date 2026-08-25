#include "JalebiLoopEngine.h"

#include <algorithm>
#include <chrono>
#include <cmath>
#include <iomanip>
#include <sstream>
#include <utility>

namespace LiveHumanAI {

JalebiLoopEngine::JalebiLoopEngine() : m_nextLoopId(1), m_initialized(false) {}
JalebiLoopEngine::~JalebiLoopEngine() { shutdown(); }

long long JalebiLoopEngine::nowMs() { return std::chrono::duration_cast<std::chrono::milliseconds>(std::chrono::system_clock::now().time_since_epoch()).count(); }
int JalebiLoopEngine::clampMaxIterations(int value) { return std::max(1, std::min(value, 100)); }
float JalebiLoopEngine::clampConfidence(float value) { return std::isfinite(value) ? std::max(0.0f, std::min(value, 1.0f)) : 0.0f; }

bool JalebiLoopEngine::initialize() { std::lock_guard<std::mutex> lock(m_mutex); m_loops.clear(); m_nextLoopId = 1; m_initialized = true; return true; }
void JalebiLoopEngine::shutdown() { std::lock_guard<std::mutex> lock(m_mutex); m_loops.clear(); m_initialized = false; }

int JalebiLoopEngine::createLoop(const std::string& goal, int maxIterations, float successConfidence) {
    std::lock_guard<std::mutex> lock(m_mutex); if (!m_initialized || goal.empty()) return 0;
    const int id = m_nextLoopId++; const long long now = nowMs(); LoopContext context;
    context.goal.id=id; context.goal.description=goal; context.goal.maxIterations=clampMaxIterations(maxIterations); context.goal.successConfidence=clampConfidence(successConfidence); context.goal.maxDurationMs=60000; context.goal.deadlineMs=now+context.goal.maxDurationMs; context.state=LoopState::INITIALIZING; context.createdAtMs=now; context.updatedAtMs=now; m_loops.emplace(id,std::move(context)); return id;
}

bool JalebiLoopEngine::startLoop(int loopId) {
    std::lock_guard<std::mutex> lock(m_mutex); auto it=m_loops.find(loopId); if(it==m_loops.end()) return false; auto& loop=it->second;
    if(loop.state!=LoopState::INITIALIZING && loop.state!=LoopState::PAUSED && loop.state!=LoopState::REPLANNING) return false;
    if(nowMs()>=loop.goal.deadlineMs){loop.state=LoopState::RESOURCE_LIMIT;loop.updatedAtMs=nowMs();return false;} loop.state=LoopState::PERCEIVING;loop.updatedAtMs=nowMs();return true;
}

bool JalebiLoopEngine::replanLoop(int loopId, const std::string& reason) {
    std::lock_guard<std::mutex> lock(m_mutex); auto it=m_loops.find(loopId); if(it==m_loops.end()) return false; auto& loop=it->second;
    if(loop.state!=LoopState::REPLANNING && loop.state!=LoopState::WAITING_USER) return false;
    const long long now=nowMs(); if(now>=loop.goal.deadlineMs || loop.currentIteration>=loop.goal.maxIterations){loop.state=LoopState::RESOURCE_LIMIT;loop.updatedAtMs=now;return false;}
    if(!loop.history.empty() && !reason.empty()) loop.history.back().errors.push_back("REPLAN: "+reason);
    if(!loop.history.empty()) loop.history.back().nextAction="REPERCEIVE"; loop.state=LoopState::PERCEIVING; loop.updatedAtMs=now; return true;
}

bool JalebiLoopEngine::pauseLoop(int loopId){std::lock_guard<std::mutex> lock(m_mutex);auto it=m_loops.find(loopId);if(it==m_loops.end())return false;auto& l=it->second;if(l.state==LoopState::COMPLETED||l.state==LoopState::FAILED||l.state==LoopState::CANCELLED||l.state==LoopState::SAFETY_BLOCKED)return false;l.state=LoopState::PAUSED;l.updatedAtMs=nowMs();return true;}
bool JalebiLoopEngine::resumeLoop(int loopId){return startLoop(loopId);}
bool JalebiLoopEngine::cancelLoop(int loopId){std::lock_guard<std::mutex> lock(m_mutex);auto it=m_loops.find(loopId);if(it==m_loops.end())return false;if(it->second.state==LoopState::COMPLETED||it->second.state==LoopState::FAILED||it->second.state==LoopState::SAFETY_BLOCKED)return false;it->second.state=LoopState::CANCELLED;it->second.updatedAtMs=nowMs();return true;}
bool JalebiLoopEngine::completeLoop(int loopId){std::lock_guard<std::mutex> lock(m_mutex);auto it=m_loops.find(loopId);if(it==m_loops.end())return false;if(it->second.state==LoopState::CANCELLED||it->second.state==LoopState::FAILED||it->second.state==LoopState::SAFETY_BLOCKED)return false;it->second.state=LoopState::COMPLETED;it->second.updatedAtMs=nowMs();return true;}
bool JalebiLoopEngine::failLoop(int loopId,const std::string& reason){std::lock_guard<std::mutex> lock(m_mutex);auto it=m_loops.find(loopId);if(it==m_loops.end())return false;auto& l=it->second;if(l.state==LoopState::COMPLETED||l.state==LoopState::CANCELLED||l.state==LoopState::SAFETY_BLOCKED)return false;l.state=LoopState::FAILED;l.updatedAtMs=nowMs();if(!l.history.empty()&&!reason.empty())l.history.back().errors.push_back(reason);return true;}
bool JalebiLoopEngine::safetyBlockLoop(int loopId,const std::string& reason){std::lock_guard<std::mutex> lock(m_mutex);auto it=m_loops.find(loopId);if(it==m_loops.end())return false;auto& l=it->second;if(l.state==LoopState::COMPLETED||l.state==LoopState::FAILED||l.state==LoopState::CANCELLED)return false;l.state=LoopState::SAFETY_BLOCKED;l.updatedAtMs=nowMs();if(!l.history.empty()&&!reason.empty())l.history.back().errors.push_back(reason);return true;}

JalebiLoopEngine::Iteration JalebiLoopEngine::executeIteration(int loopId,const std::string& input){
    std::lock_guard<std::mutex> lock(m_mutex); Iteration iteration; auto it=m_loops.find(loopId); if(it==m_loops.end())return iteration; auto& l=it->second;
    if(l.state==LoopState::PAUSED||l.state==LoopState::CANCELLED||l.state==LoopState::COMPLETED||l.state==LoopState::FAILED||l.state==LoopState::RESOURCE_LIMIT||l.state==LoopState::SAFETY_BLOCKED)return iteration;
    const long long now=nowMs(); if(now>=l.goal.deadlineMs||l.currentIteration>=l.goal.maxIterations){l.state=LoopState::RESOURCE_LIMIT;l.updatedAtMs=now;return iteration;}
    iteration.iterationId=++l.currentIteration;iteration.timestamp=now;iteration.input=input; l.state=LoopState::PERCEIVING;iteration.perception=input.empty()?"No input supplied":"Input received";l.state=LoopState::INTERPRETING;iteration.interpretation="Semantic input accepted";l.state=LoopState::REASONING;iteration.reasoningSummary="Awaiting model reasoning result";l.state=LoopState::PLANNING;iteration.plan="Awaiting planner/tool selection";l.state=LoopState::ACTING;iteration.action="Awaiting authorized action";l.state=LoopState::OBSERVING;iteration.observation="Awaiting post-action observation";l.state=LoopState::EVALUATING;iteration.evaluation="Awaiting evidence evaluation";iteration.confidence=0.0f;iteration.nextAction="EVALUATE";l.history.push_back(iteration);l.state=LoopState::WAITING_USER;l.updatedAtMs=now;return iteration;
}

bool JalebiLoopEngine::recordEvaluation(int loopId,float confidence,bool goalCompleted,const std::string& evaluation,const std::string& nextAction,const std::string& memoryUpdates){
    std::lock_guard<std::mutex> lock(m_mutex);auto it=m_loops.find(loopId);if(it==m_loops.end()||it->second.history.empty())return false;auto& l=it->second;if(l.state==LoopState::CANCELLED||l.state==LoopState::FAILED||l.state==LoopState::COMPLETED||l.state==LoopState::SAFETY_BLOCKED)return false;const long long now=nowMs();if(now>=l.goal.deadlineMs){l.state=LoopState::RESOURCE_LIMIT;l.updatedAtMs=now;l.history.back().errors.push_back("Goal deadline exceeded");return false;}const float c=clampConfidence(confidence);l.confidence=c;auto& i=l.history.back();i.confidence=c;i.evaluation=evaluation;i.memoryUpdates=memoryUpdates;i.nextAction=nextAction;if(goalCompleted&&c>=l.goal.successConfidence)l.state=LoopState::COMPLETED;else if(l.currentIteration>=l.goal.maxIterations){l.state=LoopState::RESOURCE_LIMIT;i.errors.push_back("Maximum iteration limit reached before goal completion");}else if(c<l.goal.successConfidence)l.state=LoopState::REPLANNING;else l.state=LoopState::WAITING_USER;l.updatedAtMs=now;return true;
}

JalebiLoopEngine::LoopState JalebiLoopEngine::getLoopState(int loopId)const{std::lock_guard<std::mutex> lock(m_mutex);auto it=m_loops.find(loopId);return it==m_loops.end()?LoopState::FAILED:it->second.state;}
float JalebiLoopEngine::getLatestConfidence(int loopId)const{std::lock_guard<std::mutex> lock(m_mutex);auto it=m_loops.find(loopId);return it==m_loops.end()?0.0f:it->second.confidence;}
int JalebiLoopEngine::getCurrentIteration(int loopId)const{std::lock_guard<std::mutex> lock(m_mutex);auto it=m_loops.find(loopId);return it==m_loops.end()?0:it->second.currentIteration;}
JalebiLoopEngine::Goal JalebiLoopEngine::getGoal(int loopId)const{std::lock_guard<std::mutex> lock(m_mutex);auto it=m_loops.find(loopId);return it==m_loops.end()?Goal{}:it->second.goal;}
JalebiLoopEngine::LoopSnapshot JalebiLoopEngine::getSnapshot(int loopId)const{std::lock_guard<std::mutex> lock(m_mutex);LoopSnapshot s;s.loopId=loopId;auto it=m_loops.find(loopId);if(it==m_loops.end())return s;s.goal=it->second.goal;s.state=it->second.state;s.currentIteration=it->second.currentIteration;s.confidence=it->second.confidence;s.createdAtMs=it->second.createdAtMs;s.updatedAtMs=it->second.updatedAtMs;s.history=it->second.history;return s;}
std::vector<JalebiLoopEngine::Iteration> JalebiLoopEngine::getLoopHistory(int loopId)const{std::lock_guard<std::mutex> lock(m_mutex);auto it=m_loops.find(loopId);return it==m_loops.end()?std::vector<Iteration>{}:it->second.history;}

std::string JalebiLoopEngine::jsonEscape(const std::string& v){std::ostringstream o;for(char c:v){switch(c){case '\\':o<<"\\\\";break;case '"':o<<"\\\"";break;case '\n':o<<"\\n";break;case '\r':o<<"\\r";break;case '\t':o<<"\\t";break;default:o<<c;}}return o.str();}
std::string JalebiLoopEngine::getLoopHistoryJson(int loopId)const{std::lock_guard<std::mutex> lock(m_mutex);auto it=m_loops.find(loopId);if(it==m_loops.end())return "[]";std::ostringstream o;o<<'[';for(size_t n=0;n<it->second.history.size();++n){const auto&i=it->second.history[n];if(n)o<<',';o<<"{\"iterationId\":"<<i.iterationId<<",\"timestamp\":"<<i.timestamp<<",\"input\":\""<<jsonEscape(i.input)<<"\",\"perception\":\""<<jsonEscape(i.perception)<<"\",\"interpretation\":\""<<jsonEscape(i.interpretation)<<"\",\"reasoningSummary\":\""<<jsonEscape(i.reasoningSummary)<<"\",\"plan\":\""<<jsonEscape(i.plan)<<"\",\"action\":\""<<jsonEscape(i.action)<<"\",\"observation\":\""<<jsonEscape(i.observation)<<"\",\"evaluation\":\""<<jsonEscape(i.evaluation)<<"\",\"confidence\":"<<std::fixed<<std::setprecision(3)<<i.confidence<<",\"memoryUpdates\":\""<<jsonEscape(i.memoryUpdates)<<"\",\"nextAction\":\""<<jsonEscape(i.nextAction)<<"\",\"errors\":[";for(size_t e=0;e<i.errors.size();++e){if(e)o<<',';o<<"\""<<jsonEscape(i.errors[e])<<"\"";}o<<"]}";}o<<']';return o.str();}
std::string JalebiLoopEngine::getStateName(LoopState state)const{switch(state){case LoopState::IDLE:return"IDLE";case LoopState::INITIALIZING:return"INITIALIZING";case LoopState::PERCEIVING:return"PERCEIVING";case LoopState::INTERPRETING:return"INTERPRETING";case LoopState::REASONING:return"REASONING";case LoopState::PLANNING:return"PLANNING";case LoopState::ACTING:return"ACTING";case LoopState::OBSERVING:return"OBSERVING";case LoopState::EVALUATING:return"EVALUATING";case LoopState::UPDATING_MEMORY:return"UPDATING_MEMORY";case LoopState::REPLANNING:return"REPLANNING";case LoopState::WAITING_USER:return"WAITING_USER";case LoopState::COMPLETED:return"COMPLETED";case LoopState::FAILED:return"FAILED";case LoopState::CANCELLED:return"CANCELLED";case LoopState::PAUSED:return"PAUSED";case LoopState::RESOURCE_LIMIT:return"RESOURCE_LIMIT";case LoopState::SAFETY_BLOCKED:return"SAFETY_BLOCKED";default:return"UNKNOWN";}}

} // namespace LiveHumanAI

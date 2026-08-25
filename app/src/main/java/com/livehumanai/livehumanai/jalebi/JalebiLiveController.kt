package com.livehumanai.livehumanai.jalebi

import com.livehumanai.livehumanai.data.repository.AIRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JalebiLiveController @Inject constructor(private val aiRepository: AIRepository, private val resources: JalebiResourceMonitor, private val telemetry: JalebiTelemetryStore) {
    private val scope=CoroutineScope(SupervisorJob()+Dispatchers.Default);private val lock=Any();private var job:Job?=null;private var loopId:Int?=null;private var running=false
    private val _state=kotlinx.coroutines.flow.MutableStateFlow(JalebiSessionState());val state=_state.asStateFlow()
    fun start(goal:String,maxIterations:Int=8):Int?=synchronized(lock){stopLocked(false);val g=goal.trim();if(g.isEmpty())return null;val id=aiRepository.createJalebiLoop(g,maxIterations.coerceIn(1,32));if(id<=0||!aiRepository.startJalebiLoop(id))return null;loopId=id;running=true;_state.value=JalebiSessionState(id,JalebiSessionStatus.RUNNING,0f,g);telemetry.record(JalebiTelemetryEvent(loopId=id,state="RUNNING",iteration=0,confidence=0f,message=g));id}
    suspend fun startAsync(goal:String,maxIterations:Int=8)=withContext(Dispatchers.Default){start(goal,maxIterations)}
    fun stop()=synchronized(lock){stopLocked(true)}
    private fun stopLocked(update:Boolean){val id=loopId;running=false;job?.cancel();job=null;if(id!=null)aiRepository.cancelJalebiLoop(id);loopId=null;if(update)_state.value=_state.value.copy(loopId=null,status=JalebiSessionStatus.CANCELLED)}
    fun pause()=synchronized(lock){loopId?.let{if(aiRepository.pauseJalebiLoop(it)){running=false;_state.value=_state.value.copy(status=JalebiSessionStatus.PAUSED)}}}
    fun resume()=synchronized(lock){loopId?.let{if(aiRepository.resumeJalebiLoop(it)){running=true;_state.value=_state.value.copy(status=JalebiSessionStatus.RUNNING)}}}
    fun currentLoopId()=synchronized(lock){loopId};fun isRunning()=synchronized(lock){running}
    fun submitPerception(input:String,evaluate:suspend(String)->Evaluation){val clean=input.trim();if(clean.isEmpty())return;synchronized(lock){val id=loopId?:return;if(!running)return;job?.cancel();_state.value=_state.value.copy(status=JalebiSessionStatus.PROCESSING);job=scope.launch{processObservation(id,clean,evaluate)}}}
    private suspend fun processObservation(id:Int,input:String,evaluate:suspend(String)->Evaluation){val started=System.currentTimeMillis();try{if(!resources.snapshot().safeForExpensiveInference){aiRepository.pauseJalebiLoop(id);_state.value=_state.value.copy(status=JalebiSessionStatus.RESOURCE_LIMIT,message="Device resources are constrained");return};if(aiRepository.executeJalebiIteration(id,input).isEmpty()){markFailed(id,"JCL iteration rejected");return};val r=evaluate(input);val c=r.confidence.coerceIn(0f,1f);if(!aiRepository.evaluateJalebiLoop(id,c,r.completed,r.evidence,r.nextAction,r.memoryUpdates)){markFailed(id,"JCL evaluation rejected");return};val low=!r.completed&&c<.90f;if(low)aiRepository.replanJalebiLoop(id,"confidence_below_threshold");synchronized(lock){if(loopId!=id)return;running=!r.completed;_state.value=_state.value.copy(status=if(r.completed)JalebiSessionStatus.COMPLETED else JalebiSessionStatus.RUNNING,confidence=c,nextAction=r.nextAction)};telemetry.record(JalebiTelemetryEvent(loopId=id,state=if(r.completed)"COMPLETED" else if(low)"REPLANNING" else "EVALUATING",iteration=aiRepository.getJalebiIteration(id),confidence=c,latencyMs=System.currentTimeMillis()-started,message=r.nextAction))}catch(_:CancellationException){}catch(t:Throwable){markFailed(id,t.message?:"Unexpected JCL error")}}
    private fun markFailed(id:Int,message:String)=synchronized(lock){if(loopId!=id)return;running=false;_state.value=_state.value.copy(status=JalebiSessionStatus.FAILED,message=message)}
    data class Evaluation(val confidence:Float,val completed:Boolean,val evidence:String,val nextAction:String,val memoryUpdates:String="")
}
enum class JalebiSessionStatus{IDLE,RUNNING,PROCESSING,PAUSED,RESOURCE_LIMIT,COMPLETED,FAILED,CANCELLED}
data class JalebiSessionState(val loopId:Int?=null,val status:JalebiSessionStatus=JalebiSessionStatus.IDLE,val confidence:Float=0f,val goal:String="",val nextAction:String="",val message:String="")

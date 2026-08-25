<div align="center">

# 🧠 Live Human AI

### **See. Hear. Understand. Remember. Speak. Act.**

**A privacy-first, offline-capable, real-time multimodal personal AI for Android.**

🧠 **LIVE HUMAN AI** &nbsp;•&nbsp; 👁️ Vision &nbsp;•&nbsp; 🎙️ Voice &nbsp;•&nbsp; 💬 Conversation &nbsp;•&nbsp; 🧠 Memory &nbsp;•&nbsp; ⚙️ Native AI

[![Android](https://img.shields.io/badge/Android-24%2B-3DDC84?logo=android&logoColor=white)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![C%2B%2B](https://img.shields.io/badge/C%2B%2B-17%2F20-00599C?logo=cplusplus&logoColor=white)](https://isocpp.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/compose)
[![NDK](https://img.shields.io/badge/Android%20NDK-Native-3DDC84?logo=android&logoColor=white)](https://developer.android.com/ndk)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

</div>

---

## 🌟 What is Live Human AI?

**Live Human AI** is an ambitious Android-native personal AI designed around a simple idea: an assistant should not merely answer a question and disappear. It should be able to **perceive, understand, reason, act, observe the result, verify it, update context, and continue when the task requires it**.

The project combines a modern Android application layer with C/C++ NDK components so that demanding AI workloads can be moved closer to the device hardware.

The long-term target is a practical **local AI companion** that can work with camera, microphone, conversation, memory, local models, device resources, and controlled tools while keeping the user in charge.

> **Normal AI:** `Question → Answer → Stop`
>
> **Live Human AI:** `Perceive → Interpret → Reason → Plan → Act → Observe → Evaluate → Remember → Replan → Perceive → …`

---

## 🧠 Project Logo / Identity

The project identity is represented by the **🧠 Live Human AI** mark: a human-oriented intelligence layer connected to perception, memory, reasoning, and action.

```text
                    🧠
             LIVE HUMAN AI
          ┌──────────────────┐
          │ SEE  •  HEAR     │
          │ THINK • REMEMBER │
          │ SPEAK • ACT      │
          └────────┬─────────┘
                   ↻
              JCL COGNITION
```

The **↻** represents the project's central continuous cognition concept: the **Jalebi Cognitive Loop (JCL)**.

---

## 🚀 Core Features

### 👁️ Real-Time Vision

- CameraX-based live camera pipeline.
- Scene-change detection to avoid unnecessary expensive inference.
- Object detection pipeline.
- OCR/text extraction pipeline.
- Vision confidence scoring.
- Native C/C++ processing boundary through JNI.
- Resource-aware frame processing.
- Designed to support lightweight Android devices.

### 🎙️ Live Voice

- Microphone input with Android audio APIs.
- Voice activity detection architecture.
- Local speech-to-text integration point.
- Local text-to-speech integration point.
- Conversation loop designed for interruption and follow-up.
- No requirement to permanently upload audio to a cloud service.

### 💬 Human Conversation

The assistant is designed around conversational continuity:

```text
LISTEN
  ↓
UNDERSTAND
  ↓
THINK
  ↓
RESPOND
  ↓
LISTEN AGAIN
  ↓
UPDATE CONTEXT
  ↺
```

### 🧠 Memory

- Working context for the current task.
- Session context for active conversations.
- User-approved long-term memory.
- Memory retrieval for relevant context.
- Memory policy to avoid automatically storing everything.
- Lightweight history for autonomous loops.
- Raw camera/video/audio should not be retained by default.

### ⚙️ Native AI

Performance-sensitive functionality is designed around C/C++ NDK components:

- JNI bridge.
- Native LLM management.
- Native vision management.
- Native audio processing.
- Resource monitoring.
- Model routing.
- Hardware-aware scheduling.
- Safety/policy boundaries.

---

# 🌀 Jalebi Cognitive Loop (JCL)

The signature architecture of Live Human AI is the **Jalebi Cognitive Loop**.

It is intentionally shaped like a loop rather than a one-way pipeline:

```text
              ┌───────────────┐
              │   PERCEIVE    │
              └───────┬───────┘
                      ↓
              ┌───────────────┐
              │   INTERPRET   │
              └───────┬───────┘
                      ↓
              ┌───────────────┐
              │    REASON     │
              └───────┬───────┘
                      ↓
              ┌───────────────┐
              │     PLAN      │
              └───────┬───────┘
                      ↓
              ┌───────────────┐
              │      ACT      │
              └───────┬───────┘
                      ↓
              ┌───────────────┐
              │    OBSERVE    │
              └───────┬───────┘
                      ↓
              ┌───────────────┐
              │   EVALUATE    │
              └───────┬───────┘
                      ↓
              ┌───────────────┐
              │ UPDATE MEMORY │
              └───────┬───────┘
                      ↓
              ┌───────────────┐
              │    REPLAN     │
              └───────┬───────┘
                      │
                      └──────────────↻
```

### JCL responsibilities

- Create and start bounded loops.
- Execute iterations.
- Track state and history.
- Evaluate confidence.
- Replan when evidence is insufficient.
- Pause, resume, cancel, complete, or fail loops.
- Enforce safety policy.
- Enforce resource limits.
- Verify tool results.
- Adapt model selection.
- Avoid infinite autonomous execution.

### JCL states

```text
IDLE
INITIALIZING
PERCEIVING
INTERPRETING
REASONING
PLANNING
ACTING
OBSERVING
EVALUATING
UPDATING_MEMORY
REPLANNING
WAITING_USER
COMPLETED
FAILED
CANCELLED
PAUSED
RESOURCE_LIMIT
SAFETY_BLOCKED
```

### Every iteration has evidence

```text
iterationId
 timestamp
 input
 perception
 interpretation
 reasoningSummary
 plan
 action
 observation
 evaluation
 confidence
 errors
 memoryUpdates
 nextAction
```

---

## 🎯 Goal-Driven Autonomy

Every autonomous JCL session has a defined goal.

Example:

> **Goal:** Determine whether a document is complete.

Possible success criteria:

```text
confidence >= 0.90
AND
required fields evaluated
```

Possible failure criteria:

```text
document unreadable
OR
permission unavailable
OR
resource limit reached
OR
maximum iterations reached
```

This keeps autonomy **bounded, inspectable, and controllable**.

---

## 📈 Confidence & Verification

Live Human AI does not treat every model output as fact.

Confidence can be tracked independently for:

| Signal | Example |
|---|---:|
| Vision | 0.94 |
| OCR | 0.87 |
| Intent | 0.98 |
| Tool result | 1.00 |
| Overall result | Task-dependent |

When confidence is insufficient, the system can:

```text
LOW CONFIDENCE
      ↓
RE-PERCEIVE
      ↓
TRY ANOTHER MODEL
      ↓
VERIFY
      ↓
ASK USER IF NEEDED
```

It should **not silently hallucinate confidence**.

---

## 🔀 Multi-Model Routing

The architecture is designed so every stage does not need the largest model.

```text
Audio
  ↓
STT
  ↓
Intent
  ↓
Small / Medium LLM

Vision
  ↓
Cheap detector
  ↓
OCR
  ↓
Vision reasoning
  ↓
LLM

Complex reasoning
  ↓
Small model
  ↓ confidence low
Medium model
  ↓ confidence low
Larger device-safe model
  ↓
Verification
```

### Model escalation principle

**Escalate only when evidence justifies the additional compute.**

This is especially important on phones with limited RAM, battery, GPU, or thermal headroom.

---

## 📱 Device Profiles

### 6 GB RAM profile

Designed around practical mobile constraints:

- Lightweight quantized LLMs.
- Lower camera inference frequency.
- Smaller image resolution when resources are constrained.
- Aggressive thermal/battery protection.
- Limited model concurrency.
- Conservative context sizes.

Typical target models include:

- Qwen3 0.6B / 1.7B Q4-class models.
- Whisper Tiny/Base-class models.
- Lightweight object detectors.
- Lightweight OCR.

### 16 GB RAM profile

Allows substantially richer local workloads:

- Larger quantized LLMs.
- Larger context windows.
- More model caching.
- Higher-quality vision models.
- Larger speech models.
- More simultaneous pipeline components where thermals permit.

Typical target models include:

- Qwen3 4B Q4/Q5-class models.
- Optional 7B/8B quantized models.
- Whisper Base/Small-class models.
- Higher-quality vision/OCR models.

> Hardware capability is detected dynamically. The app should never assume that a device can safely run a model merely because the model exists.

---

## 🌡️ Resource-Aware AI

Before expensive work, JCL can consider:

- RAM.
- CPU usage.
- GPU availability.
- Battery level.
- Thermal state.
- Network state when network access is explicitly allowed.
- Current model load.
- Token budget.
- Tool-call budget.
- Loop iteration budget.

### Thermal policy

```text
NORMAL
  ↓
Full workload

WARM
  ↓
Reduce workload

HOT
  ↓
Prefer small models

CRITICAL
  ↓
Pause expensive AI

COOLED
  ↓
Resume only when policy allows
```

### Loop limits

Every autonomous loop should have bounded limits such as:

```text
maxIterations
maxTime
maxMemory
maxCPU
maxTokens
maxToolCalls
maxRetries
```

Default examples should be conservative and configurable rather than infinite.

---

## 🛡️ Safety & Privacy

Live Human AI is designed around explicit user control.

The AI must not:

- Bypass Android permissions.
- Secretly access the camera.
- Secretly access the microphone.
- Bypass authentication.
- Extract credentials.
- Execute unrestricted shell commands.
- Modify security settings without authorization.
- Perform unrestricted autonomous actions.

Tools should follow this boundary:

```text
AI
 ↓
Tool Registry
 ↓
Permission Check
 ↓
Safety Policy
 ↓
Execution
 ↓
Actual Result
 ↓
Verification
 ↓
World State Update
```

### Privacy principles

1. **Local-first** where practical.
2. **Explicit permissions** for sensitive hardware.
3. **No hidden recording.**
4. **No automatic memory hoarding.**
5. **Verify before acting on tool results.**
6. **User can stop autonomous activity.**
7. **Resource limits are safety limits too.**

---

## 🌍 World State

JCL maintains a temporary, privacy-controlled world state containing information such as:

```text
currentTime
currentLocation (only if permitted)
cameraState
detectedObjects
detectedText
speakerState
conversationContext
activeTask
deviceState
batteryState
thermalState
networkState
permissions
```

The system compares previous and current state to detect meaningful changes.

Examples:

- New object.
- Removed object.
- Moved object.
- Changed text.
- Speaker started/stopped.
- Scene changed.
- Task changed.

Expensive inference should be triggered by **meaningful change**, not blindly on every frame.

---

## 📷 Live Camera Loop

```text
CameraX
  ↓
Frame
  ↓
Motion / Change Detection
  ↓
Cheap Detection
  ├── No meaningful change → wait
  │
  └── Meaningful change
          ↓
       Vision Analysis
          ↓
       OCR / Objects
          ↓
       JCL Evaluation
          ↓
       Context Update
          ↓
       Replan if required
          ↺
```

The design deliberately avoids sending every camera frame through the largest model.

---

## 🔊 Live Conversation Loop

```text
Microphone
   ↓
VAD
   ↓
Speech-to-Text
   ↓
Intent / Context
   ↓
Reasoning
   ↓
Response
   ↓
Text-to-Speech
   ↓
Listen Again
   ↺
```

The conversation can continue until the user ends live mode or the configured policy stops it.

---

## 🔧 Tool Verification

A requested action is never considered successful merely because a model asked for it.

Example:

```text
AI requests: Turn flashlight ON
        ↓
Tool executes
        ↓
Tool returns SUCCESS
        ↓
WorldState: flashlight = ON
        ↓
Verification
        ↓
Continue JCL
```

If execution fails, JCL can safely retry, select an alternative, or ask the user.

Potentially harmful operations must never enter uncontrolled retry loops.

---

## 🧩 Failure Recovery

The recovery strategy is intentionally bounded:

```text
Action failed
    ↓
Understand failure
    ↓
Is retry safe?
 ┌──┴──┐
YES   NO
 ↓     ↓
Retry  Ask / Stop
 ↓
Alternative method
 ↓
Verify
```

Default retry budgets should remain small.

---

## 🖥️ Developer Mode

The project includes a JCL developer dashboard concept for inspecting live autonomous state.

It can expose:

- Current stage.
- Current iteration.
- Goal.
- Confidence.
- Current model.
- RAM usage.
- CPU usage.
- Temperature.
- Latency.
- Next action.
- Loop state.
- Loop history.

Developer controls include:

```text
[ Pause ] [ Resume ] [ Replan ] [ Stop ]
```

The purpose is transparency: **autonomous activity should be observable rather than hidden**.

---

## 🏗️ Architecture

```text
┌───────────────────────────────────────────────────────┐
│                    ANDROID APP                        │
│                                                       │
│  Jetpack Compose • Navigation • ViewModels            │
│                         │                             │
│                         ▼                             │
│                 Repository Layer                      │
│                         │                             │
│                         ▼                             │
│                   JCL Controller                      │
└─────────────────────────┬─────────────────────────────┘
                          │ JNI
                          ▼
┌───────────────────────────────────────────────────────┐
│                    NATIVE NDK                         │
│                                                       │
│  JalebiLoopEngine                                     │
│       ├── Perception                                  │
│       ├── Interpretation                               │
│       ├── Reasoning                                    │
│       ├── Planning                                     │
│       ├── Action                                       │
│       ├── Observation                                  │
│       ├── Evaluation                                   │
│       ├── Memory                                       │
│       ├── Model Routing                                │
│       ├── Resource Governor                            │
│       └── Security Policy                              │
│                                                       │
│  LLM • Vision • Audio • Hardware • JNI               │
└───────────────────────────────────────────────────────┘
```

---

## 🛠️ Technology Stack

### Android

- Kotlin.
- Jetpack Compose.
- Material 3.
- MVVM.
- Clean Architecture principles.
- Repository pattern.
- Hilt dependency injection.
- Room/SQLite architecture.
- CameraX.
- Android audio APIs.
- Runtime permissions.
- WorkManager / foreground execution where appropriate.

### Native

- C++17/20.
- Android NDK.
- JNI.
- CMake.
- ARM NEON.
- Optional Vulkan/OpenGL ES acceleration.
- Native resource monitoring.

### AI engines / model ecosystem

- [llama.cpp](https://github.com/ggerganov/llama.cpp) — local LLM inference.
- [whisper.cpp](https://github.com/ggerganov/whisper.cpp) — local speech recognition.
- OpenCV / lightweight vision backends where available.
- YOLO/MobileNet-class detectors.
- Piper/other local TTS engines where integrated.

> Actual model availability depends on the current implementation, model files, device hardware, licenses, and runtime configuration.

---

## 📦 Model Profiles

| Capability | 6 GB Profile | 16 GB Profile |
|---|---|---|
| Main LLM | Small quantized model | Medium quantized model |
| Advanced LLM | Conservative / disabled | Optional larger quantized model |
| STT | Whisper Tiny/Base class | Whisper Base/Small class |
| Vision | Lightweight detector | Medium detector |
| OCR | Lightweight | Higher accuracy |
| TTS | Small local voice | Higher quality local voice |
| Context | Conservative | Larger |
| Model cache | Limited | Expanded |
| Camera inference | Resource-gated | Higher budget when safe |

---

## 📂 Project Structure

```text
Live-Human-AI/
├── app/
│   ├── src/main/
│   │   ├── java/com/livehumanai/
│   │   │   ├── data/                 # Repositories and local data
│   │   │   ├── domain/               # Domain models and use cases
│   │   │   ├── jalebi/               # JCL runtime + policies
│   │   │   ├── native/               # JNI-facing Android code
│   │   │   └── ui/                   # Compose UI and navigation
│   │   ├── main/cpp/
│   │   │   ├── ai/                   # Native AI engines
│   │   │   ├── audio/                # Native audio processing
│   │   │   ├── vision/               # Native vision
│   │   │   ├── jalebi/               # Native JCL engine
│   │   │   ├── hardware/             # Hardware/resource monitoring
│   │   │   ├── security/             # Native safety policy
│   │   │   └── jni/                  # JNI bridge
│   │   ├── assets/                   # Model metadata/assets
│   │   └── res/                      # Android resources
│   ├── CMakeLists.txt
│   └── build.gradle.kts
├── .github/
│   └── workflows/                   # CI/CD and automated checks
├── README.md
├── LICENSE
└── .gitignore
```

---

## 💻 Requirements

Recommended development environment:

- Android Studio — latest stable release.
- JDK 17.
- Android SDK with the project's configured compile/target SDK.
- Android NDK compatible with the repository's Gradle/CMake configuration.
- CMake compatible with the repository's native build configuration.
- Git.
- A physical Android device is strongly recommended for camera/audio/thermal testing.

For AI model testing, available RAM and storage matter substantially more than emulator specifications.

---

## 🚀 Build From Source

```bash
git clone https://github.com/abdulraheemnohri/Live-Human-AI.git
cd Live-Human-AI
```

Open the project in Android Studio, allow Gradle to sync, install the required Android SDK/NDK/CMake components, and build the debug variant.

Command-line build:

```bash
./gradlew assembleDebug
```

On Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

The generated debug APK is produced under the module's Gradle build outputs.

---

## 🧪 Testing & Verification

The project is intended to use multiple verification layers:

```text
Kotlin Unit Tests
      ↓
Native C++ Tests
      ↓
JCL Policy Tests
      ↓
Android Build
      ↓
Lint / Static Checks
      ↓
Device Testing
      ↓
Camera / Audio / Thermal Testing
```

Important real-device scenarios include:

- 6 GB RAM device.
- 16 GB RAM device.
- Low battery.
- High thermal state.
- Permission denied.
- Camera unavailable.
- Microphone unavailable.
- Model unavailable.
- Network unavailable.
- Low storage.
- Interrupted conversation.
- JCL cancellation.
- JCL pause/resume.
- Confidence escalation.
- Tool failure and safe recovery.

---

## 🔐 Permission Model

Sensitive capabilities must be requested only when needed.

| Permission / Capability | Purpose |
|---|---|
| Camera | Live vision and document/object analysis |
| Microphone | Voice input and conversation |
| Notifications | User-visible foreground/background state where required |
| Location | Only features explicitly requiring location |
| Bluetooth | Only explicit device-control features |
| Storage / media access | Only model/media workflows that require it |

The application should degrade gracefully when a permission is denied.

---

## 📊 Performance Observability

The runtime can monitor or expose:

- CPU usage.
- RAM usage.
- Available RAM.
- Battery level.
- Temperature.
- Model load time.
- Inference latency.
- Tokens/second.
- Current JCL iteration.
- Current JCL stage.
- Confidence.
- Tool-call count.
- Loop duration.

These signals feed resource-aware decisions rather than existing only as cosmetic metrics.

---

## 🧭 Roadmap

### Foundation

- [x] Android application foundation.
- [x] Compose UI architecture.
- [x] Kotlin/JNI boundary.
- [x] Native C++ build foundation.
- [x] JCL state model.
- [x] Bounded loop concept.
- [x] Developer-mode JCL controls.

### Cognitive Runtime

- [x] Goal-driven loop architecture.
- [x] Confidence model integration.
- [x] Replan pathway.
- [x] Resource-aware execution concept.
- [x] Safety-policy boundary.
- [ ] Full production model routing across all modalities.
- [ ] Full production memory policy lifecycle.
- [ ] End-to-end tool verification for every device action.

### Live Multimodal AI

- [x] CameraX pipeline foundation.
- [x] Vision/JCL integration foundation.
- [ ] Production object detection model packaging.
- [ ] Production OCR model packaging.
- [ ] Streaming STT.
- [ ] Low-latency local TTS.
- [ ] Full interruptible voice conversation.
- [ ] Cross-modal context fusion.

### Optimization

- [ ] Device-specific model benchmarking.
- [ ] More aggressive scene-change gating.
- [ ] GPU/NPU backend optimization.
- [ ] Quantized model recommendations.
- [ ] Thermal benchmarking across devices.
- [ ] Battery-aware scheduling improvements.

### Developer Experience

- [x] JCL developer dashboard foundation.
- [ ] Full loop timeline inspector.
- [ ] Native performance trace export.
- [ ] Model benchmark dashboard.
- [ ] Automated regression reports.

---

## 🧠 Design Principles

### 1. Local first

Use on-device processing whenever practical and safe.

### 2. Evidence before confidence

A fluent answer is not automatically a correct answer.

### 3. Small model first

Use the smallest capable model and escalate only when necessary.

### 4. Bounded autonomy

Autonomous loops always have limits and cancellation paths.

### 5. User control

The human remains in control of permissions, sensitive actions, and autonomy.

### 6. Resource-aware intelligence

A smart AI that overheats a phone is not smart enough.

### 7. Transparent activity

The user should be able to understand when the system is listening, thinking, checking, waiting, or acting.

### 8. Fail safely

When uncertain, unavailable, unauthorized, or resource-constrained, stop, reduce workload, verify, or ask.

---

## 🤝 Contributing

Contributions are welcome.

```bash
git checkout -b feature/your-feature
git add .
git commit -m "feat: your change"
git push origin feature/your-feature
```

Then open a Pull Request.

For significant architecture changes, please explain:

- What problem is being solved.
- Why the proposed architecture is appropriate.
- RAM/CPU/GPU impact.
- Battery/thermal impact.
- Privacy implications.
- Permission requirements.
- Test coverage.
- Failure/recovery behavior.

---

## 🐛 Diagnostics & Troubleshooting

### Build fails in native C++

Check that Android Studio has the required NDK and CMake versions installed and that Gradle is using the repository configuration.

### Camera does not start

Check:

1. Camera permission.
2. Device camera availability.
3. CameraX lifecycle binding.
4. Analyzer configuration.
5. Device thermal/resource state.

### AI is slow

Check:

- Current model size.
- Quantization level.
- Available RAM.
- Thermal state.
- CPU/GPU backend.
- Camera analysis frequency.
- Context length.

### JCL stops

Inspect the JCL state. A stop can be intentional because of:

- Completion.
- Cancellation.
- Maximum iterations.
- Maximum time.
- Resource limit.
- Safety block.
- Missing permission.
- Missing model.
- Failed verification.

---

## 📜 License

This project is licensed under the **Apache License 2.0**. See [LICENSE](LICENSE).

Third-party models and dependencies may have their own licenses. Always verify the license of a model before distributing it with the application.

---

## 🙏 Acknowledgments

The project builds on the work of the open-source ecosystem, including:

- [llama.cpp](https://github.com/ggerganov/llama.cpp)
- [whisper.cpp](https://github.com/ggerganov/whisper.cpp)
- [Android](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/compose)
- [Android NDK](https://developer.android.com/ndk)
- [OpenCV](https://opencv.org/)
- The wider open-source AI and Android communities.

---

## ⭐ Support the Project

If Live Human AI is useful to you, you can help the project grow by:

- ⭐ Starring the repository.
- 🐛 Reporting reproducible bugs.
- 💡 Proposing useful features.
- 🔧 Contributing code.
- 🧪 Testing on different Android devices.
- 📣 Sharing the project with developers interested in local AI.
- ❤️ Supporting development through donations when available.

Every contribution helps move the project closer to a capable, privacy-respecting AI companion that can run directly on everyday phones.

---

## 📬 Contact

Project: [Live Human AI](https://github.com/abdulraheemnohri/Live-Human-AI)

Maintainer: [abdulraheemnohri](https://github.com/abdulraheemnohri)

For bugs and feature requests, please use the repository's **Issues** and **Pull Requests**.

---

<div align="center">

### 🧠 Live Human AI

**Perceive. Understand. Reason. Act. Verify. Remember. Replan.**

**© 2026 Live Human AI**

</div>

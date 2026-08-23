# Live Human AI

**See. Hear. Understand. Remember. Speak.**

A **privacy-first**, **offline-capable**, **real-time multimodal** personal AI assistant for Android. Built with **Kotlin + Jetpack Compose** for the UI and **C/C++ NDK** for high-performance AI inference.

---

## 🚀 **Features**

### **Core Capabilities**
- **Real-time Conversation**: Speak naturally, interrupt the AI, and ask follow-up questions.
- **Vision AI**: Use the camera to identify objects, read text, translate, and summarize visual information.
- **Voice Assistant**: Wake word detection, push-to-talk, and streaming speech-to-text (STT) and text-to-speech (TTS).
- **Memory System**: Store user-approved information for contextual and long-term recall.
- **Offline Operation**: Core AI functions work without internet access.
- **Dynamic Model Selection**: Automatically selects the best model based on device RAM, CPU, GPU, battery, and thermal state.

### **Hardware Optimization**
- **6 GB RAM Profile**: Optimized for lightweight models (e.g., Qwen3 0.6B/1.7B, Whisper Tiny/Base).
- **16 GB RAM Profile**: Supports larger models (e.g., Qwen3 4B, Whisper Small, optional 7B/8B LLM).
- **Thermal & Battery Management**: Adjusts AI workload to prevent overheating and conserve battery.
- **GPU/NPU Acceleration**: Uses Vulkan, OpenGL ES, and ARM NEON for faster inference.

### **Privacy & Security**
- **No Cloud Dependency**: All AI processing happens locally.
- **Permission-Controlled Tools**: Explicit user consent for camera, microphone, and other sensitive features.
- **Encrypted Storage**: Sensitive data is stored securely using Android Keystore.
- **No Hidden Recording**: Transparent AI state with user-controlled permissions.

---

## 📱 **Screenshots**
*(Coming soon)*

---

## 🛠 **Tech Stack**

### **Android**
- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: Clean Architecture + MVVM + Repository Pattern
- **Database**: Room/SQLite
- **Camera**: CameraX
- **Audio**: Android Audio APIs, AAudio
- **Permissions**: Runtime permission handling
- **Background Tasks**: WorkManager, Foreground Service

### **Native (C++)**
- **Language**: C++17/20
- **NDK**: Android NDK for native development
- **JNI**: Bridge between Kotlin and C++
- **AI Engines**:
  - **LLM**: [llama.cpp](https://github.com/ggerganov/llama.cpp) (Qwen3, etc.)
  - **STT**: [whisper.cpp](https://github.com/ggerganov/whisper.cpp) (Whisper models)
  - **Vision**: OpenCV, custom lightweight models
  - **TTS**: Local TTS engines (e.g., Piper, Coqui TTS)
- **Acceleration**: ARM NEON, Vulkan, OpenGL ES

### **AI Models**
| **Capability**       | **6 GB Profile**          | **16 GB Profile**               |
|----------------------|---------------------------|---------------------------------|
| **Main LLM**         | Qwen3 0.6B / 1.7B Q4      | Qwen3 4B Q4/Q5                  |
| **Advanced LLM**     | —                         | 7B/8B Q4 (Optional)             |
| **STT**             | Whisper Tiny/Base         | Whisper Base/Small              |
| **Wake Word**       | Tiny Local Model          | Tiny Local Model                |
| **Object Detection**| Nano/Mobile Detector      | Medium Detector                 |
| **OCR**             | Lightweight               | Higher Accuracy                 |
| **TTS**             | Small Local TTS           | Medium/High-Quality TTS         |

---

## 📂 **Project Structure**
```
LiveHumanAI/
├── app/
│   ├── src/main/
│   │   ├── java/com/livehumanai/
│   │   │   ├── ui/               # UI layers (Home, Chat, Vision, etc.)
│   │   │   ├── data/             # Repositories, databases, preferences
│   │   │   ├── domain/           # Use cases, models, business logic
│   │   │   └── native/           # JNI interfaces
│   │   │
│   │   ├── assets/               # Model metadata, static assets
│   │   └── res/                  # Resources (layouts, strings, etc.)
│   │
│   ├── CMakeLists.txt            # NDK build configuration
│   ├── build.gradle.kts          # Android build script
│   └── settings.gradle.kts       # Project settings
│
├── native/                       # C++ source code
│   ├── core/                     # Core engine (AI, hardware, etc.)
│   ├── ai/                       # AI inference (LLM, STT, TTS, Vision)
│   ├── audio/                    # Audio processing
│   ├── vision/                   # Vision processing
│   ├── memory/                   # Memory management
│   ├── hardware/                 # Hardware profiling
│   ├── scheduler/                # Task scheduling
│   ├── security/                 # Security utilities
│   └── jni/                      # JNI bridge
│
├── README.md
├── LICENSE
└── .gitignore
```

---

## 📋 **Setup & Installation**

### **Prerequisites**
- Android Studio (latest stable version)
- Android NDK (r25c or later)
- CMake (3.10.2 or later)
- Kotlin (1.8.0 or later)

### **Cloning the Repository**
```bash
git clone https://github.com/abdulraheemnohri/Live-Human-AI.git
cd Live-Human-AI
```

### **Building the Project**
1. Open the project in **Android Studio**.
2. Ensure the **NDK** and **CMake** are installed via SDK Manager.
3. Sync Gradle and build the project.

### **Running the App**
- Connect an Android device (API 24+) or use an emulator.
- Click **Run** in Android Studio.

---

## 🔧 **Configuration**

### **Device Profiles**
The app automatically detects your device's RAM and selects the optimal profile:
- **6 GB RAM**: Lite/Balanced mode (smaller models, limited context).
- **16 GB RAM**: Pro mode (larger models, longer context, multi-model caching).

### **Manual Override**
You can manually select a profile in **Settings > Performance**:
- **Battery Saver**: Minimal AI workload.
- **Balanced**: Default experience.
- **Performance**: Maximum AI capabilities.

---

## 🤖 **AI Models**

### **Model Download**
1. Open the **Model Manager** in the app.
2. Browse available models (LLM, STT, TTS, Vision).
3. Tap **Download** to install a model.
4. The app will verify the checksum and install the model locally.

### **Supported Models**
- **LLM**: Qwen3 (0.6B, 1.7B, 4B, 7B/8B)
- **STT**: Whisper (Tiny, Base, Small)
- **TTS**: Piper, Coqui TTS
- **Vision**: YOLO, MobileNet, custom lightweight models

### **Model Storage**
Models are stored in:
```
/Android/data/com.livehumanai.livehumanai/files/models/
```

---

## 🛡 **Privacy & Security**

### **Permissions**
The app requests permissions **only when needed**:
- **Camera**: For vision tasks (object detection, OCR, etc.).
- **Microphone**: For voice input (STT, wake word).
- **Storage**: For downloading and storing models.
- **Location/Bluetooth**: Optional, for specific tools (e.g., Bluetooth device control).

### **Data Storage**
- **Conversations**: Stored locally in Room/SQLite.
- **Memories**: User-approved facts stored locally.
- **Models**: Downloaded to local storage (checksum-verified).
- **No Cloud Sync**: All data stays on your device.

### **Encryption**
- Sensitive data (e.g., memories, settings) is encrypted using **Android Keystore**.
- Model files are verified using **SHA-256 checksums** before execution.

---

## 📊 **Performance Monitoring**

The app provides real-time metrics for:
- **CPU/GPU Usage**
- **RAM Usage**
- **Thermal State**
- **Battery Level**
- **Model Load Time**
- **Inference Latency**
- **Tokens/Second**

Access these metrics in **Settings > Performance Monitor**.

---

## 🐛 **Diagnostics**

Run a full diagnostic test in **Settings > Diagnostics** to check:
- Camera functionality
- Microphone functionality
- Speaker functionality
- STT/LLM/TTS/Vision engines
- Storage and RAM
- GPU/NPU support

---

## 📜 **License**

This project is licensed under the **Apache License 2.0** – see the [LICENSE](LICENSE) file for details.

---

## 🤝 **Contributing**

Contributions are welcome! Please follow these steps:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m "feat: add your feature"`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a **Pull Request**.

---

## 📧 **Contact**

For questions or feedback, open an **Issue** or contact the maintainer:
- **GitHub**: [abdulraheemnohri](https://github.com/abdulraheemnohri)
- **Email**: (If applicable)

---

## 🙏 **Acknowledgments**

- [llama.cpp](https://github.com/ggerganov/llama.cpp) for LLM inference.
- [whisper.cpp](https://github.com/ggerganov/whisper.cpp) for STT.
- [OpenCV](https://opencv.org/) for vision processing.
- [Jetpack Compose](https://developer.android.com/jetpack/compose) for modern UI.
- The open-source AI community for their incredible work.

---

**© 2026 Live Human AI**

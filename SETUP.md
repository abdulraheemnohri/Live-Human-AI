# Setup Instructions for Live Human AI

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK 34 (Android 14)
- Android NDK r26 or newer
- Kotlin 1.9+
- CMake 3.22+
- Git

## Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/your-org/livehumanai.git
cd livehumanai
```

### 2. Initialize Git Submodules (Required for LLM and STT)

The project uses `llama.cpp` for local LLM inference and `whisper.cpp` for speech-to-text.

```bash
git submodule update --init --recursive
```

This will clone:
- `llama.cpp` into `app/src/main/cpp/external/llama.cpp`
- `whisper.cpp` into `app/src/main/cpp/external/whisper.cpp`

**Note:** If you skip this step, the app will build but LLM and STT features will be disabled.

### 3. Open in Android Studio

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the `livehumanai` directory
4. Wait for Gradle sync to complete

### 4. Configure NDK

If prompted, install the required NDK version via SDK Manager:
- Go to **Tools > SDK Manager > SDK Tools**
- Check "NDK (Side by side)" version 26.x
- Click Apply

### 5. Build and Run

1. Connect an Android device (API 26+) or start an emulator
2. Select the `debug` build variant
3. Click **Run** (▶️)

## Manual Submodule Setup (Alternative)

If automatic submodule initialization fails:

```bash
cd app/src/main/cpp/external

# Clone llama.cpp
git clone https://github.com/ggerganov/llama.cpp.git
cd llama.cpp
git checkout <specific-commit-for-stability>
cd ..

# Clone whisper.cpp
git clone https://github.com/ggerganov/whisper.cpp.git
cd whisper.cpp
git checkout <specific-commit-for-stability>
cd ../..
```

## Downloading Models

The app does not bundle AI models. You must download them through the app:

1. Launch the app
2. Go to **More > Models**
3. Select recommended models for your device
4. Tap **Install** to download from Hugging Face

### Recommended Models by Device Profile

#### 6 GB RAM Devices (Lite Profile)
- **LLM:** Qwen3 1.7B Q4_K_M (~1.2 GB)
- **STT:** Whisper Base (~140 MB)
- **Vision:** MobileNet SSD + Tesseract OCR

#### 16 GB RAM Devices (Pro Profile)
- **LLM:** Qwen3 4B Q4_K_M (~2.8 GB) or Qwen3 7B Q4_K_M (~4.5 GB)
- **STT:** Whisper Small (~480 MB)
- **Vision:** YOLOv8n + Advanced OCR

## Build Variants

| Variant | Description | Use Case |
|---------|-------------|----------|
| `debug` | Debug symbols, logging enabled | Development |
| `release` | Optimized, no logging | Production |
| `benchmark` | Profiling enabled | Performance testing |

## Troubleshooting

### CMake Build Fails

**Error:** `llama.cpp not found`

**Solution:**
```bash
git submodule update --init --recursive
```

Or manually clone as shown above.

### Out of Memory During Build

Add to `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx2048m -XX:MaxMetaspaceSize=512m
```

### NDK Not Found

Ensure NDK is installed via SDK Manager and set `ndk.dir` in `local.properties`:
```properties
ndk.dir=/path/to/ndk
```

### Model Download Fails

- Check internet connection
- Ensure sufficient storage (at least 5 GB free)
- Try pausing and resuming the download
- Verify Hugging Face is accessible in your region

## Testing

### Run Unit Tests
```bash
./gradlew testDebugUnitTest
```

### Run Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### Run Native Tests (C++)
```bash
cd app/src/main/cpp
mkdir build && cd build
cmake .. -DCMAKE_BUILD_TYPE=Debug
make -j4
./tests/livehumanai-tests
```

## Performance Optimization

### For 6 GB Devices
- Enable **Battery Saver** mode in Settings > Performance
- Use **Lite** model profile
- Disable semantic memory indexing
- Reduce camera FPS to 5

### For 16 GB Devices
- Enable **Performance** mode
- Use **Pro** model profile
- Keep multiple models cached
- Enable high-quality TTS

## Privacy & Security

- All AI inference runs **locally** on your device
- No data is sent to cloud servers by default
- Microphone/Camera usage is indicated by system indicators
- Models are verified via SHA-256 checksums before installation
- Sensitive data is encrypted using Android Keystore

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Support

- **Documentation:** See `/docs` folder
- **Issues:** GitHub Issues tab
- **Discussions:** GitHub Discussions tab
- **Donations:** [Ko-fi](https://ko-fi.com/livehumanai) | [GitHub Sponsors](https://github.com/sponsors/livehumanai)

---

**Built with ❤️ for offline-first, privacy-preserving AI**

cmake_minimum_required(VERSION 3.22.1)

project("LiveHumanAI" LANGUAGES CXX C)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

# Android NDK settings
set(ANDROID_ARM_NEON ON)

# Find required packages
find_package(log REQUIRED)
find_package(android REQUIRED)

# Core Engine Sources
set(CORE_SOURCES
    src/main/cpp/core/Engine.cpp
    src/main/cpp/core/Engine.h
    src/main/cpp/core/ThreadPool.cpp
    src/main/cpp/core/ThreadPool.h
    src/main/cpp/core/EventBus.cpp
    src/main/cpp/core/EventBus.h
)

# Hardware Profiler
set(HARDWARE_SOURCES
    src/main/cpp/hardware/HardwareProfiler.cpp
    src/main/cpp/hardware/HardwareProfiler.h
    src/main/cpp/hardware/DeviceProfile.cpp
    src/main/cpp/hardware/DeviceProfile.h
    src/main/cpp/hardware/ThermalManager.cpp
    src/main/cpp/hardware/ThermalManager.h
    src/main/cpp/hardware/BatteryManager.cpp
    src/main/cpp/hardware/BatteryManager.h
)

# Model Management
set(MODEL_SOURCES
    src/main/cpp/models/ModelManager.cpp
    src/main/cpp/models/ModelManager.h
    src/main/cpp/models/ModelRegistry.cpp
    src/main/cpp/models/ModelRegistry.h
    src/main/cpp/models/ModelRouter.cpp
    src/main/cpp/models/ModelRouter.h
)

# Hugging Face Downloader
set(HUGGINGFACE_SOURCES
    src/main/cpp/huggingface/HuggingFaceDownloader.cpp
    src/main/cpp/huggingface/HuggingFaceDownloader.h
    src/main/cpp/huggingface/ModelCatalog.cpp
    src/main/cpp/huggingface/ModelCatalog.h
)

# AI Engine Base
set(AI_SOURCES
    src/main/cpp/ai/AIEngine.cpp
    src/main/cpp/ai/AIEngine.h
    src/main/cpp/ai/ConfidenceEngine.cpp
    src/main/cpp/ai/ConfidenceEngine.h
)

# Jalebi Loop Engine
set(JALEBI_SOURCES
    src/main/cpp/jalebi/JalebiLoopEngine.cpp
    src/main/cpp/jalebi/JalebiLoopEngine.h
    src/main/cpp/jalebi/JalebiIteration.cpp
    src/main/cpp/jalebi/JalebiIteration.h
)

# JNI Bridge
set(JNI_SOURCES
    src/main/cpp/jni/LiveHumanAIJNI.cpp
    src/main/cpp/jni/LiveHumanAIJNI.h
)

# Utilities
set(UTILS_SOURCES
    src/main/cpp/utils/Logger.cpp
    src/main/cpp/utils/Logger.h
    src/main/cpp/utils/StringUtils.cpp
    src/main/cpp/utils/StringUtils.h
    src/main/cpp/utils/FileUtils.cpp
    src/main/cpp/utils/FileUtils.h
    src/main/cpp/utils/ChecksumUtils.cpp
    src/main/cpp/utils/ChecksumUtils.h
)

# Security
set(SECURITY_SOURCES
    src/main/cpp/security/SecurityManager.cpp
    src/main/cpp/security/SecurityManager.h
)

# Diagnostics
set(DIAGNOSTICS_SOURCES
    src/main/cpp/diagnostics/Diagnostics.cpp
    src/main/cpp/diagnostics/Diagnostics.h
)

# Combine all sources
set(LIVEHUMANAI_SOURCES
    ${CORE_SOURCES}
    ${HARDWARE_SOURCES}
    ${MODEL_SOURCES}
    ${HUGGINGFACE_SOURCES}
    ${AI_SOURCES}
    ${JALEBI_SOURCES}
    ${JNI_SOURCES}
    ${UTILS_SOURCES}
    ${SECURITY_SOURCES}
    ${DIAGNOSTICS_SOURCES}
)

# Create shared library
add_library(livehumanai SHARED ${LIVEHUMANAI_SOURCES})

# Include directories
target_include_directories(livehumanai PRIVATE
    src/main/cpp
    src/main/cpp/core
    src/main/cpp/hardware
    src/main/cpp/models
    src/main/cpp/huggingface
    src/main/cpp/ai
    src/main/cpp/jalebi
    src/main/cpp/jni
    src/main/cpp/utils
    src/main/cpp/security
    src/main/cpp/diagnostics
)

# Link libraries
target_link_libraries(livehumanai
    android
    log
    m
)

# Compiler flags
target_compile_options(livehumanai PRIVATE
    -Wall
    -Wextra
    -fexceptions
    -frtti
)

# NEON optimization for ARM
if(${ANDROID_ABI} MATCHES "arm64-v8a")
    target_compile_options(livehumanai PRIVATE -mfpu=neon)
endif()

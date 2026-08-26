# Live Human AI - Native CMake Build Configuration
cmake_minimum_required(VERSION 3.22.1)

# Include llama.cpp and whisper.cpp as subdirectories
# Note: These should be added as git submodules in the actual project setup
# git submodule add https://github.com/ggerganov/llama.cpp native/external/llama.cpp
# git submodule add https://github.com/ggerganov/whisper.cpp native/external/whisper.cpp

if(EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/external/llama.cpp")
    set(LLAMA_BUILD_SERVER OFF CACHE BOOL "" FORCE)
    set(LLAMA_BUILD_TESTS OFF CACHE BOOL "" FORCE)
    set(LLAMA_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
    set(LLAMA_NATIVE OFF CACHE BOOL "" FORCE)
    set(LLAMA_VULKAN ON CACHE BOOL "" FORCE) # Enable Vulkan for GPU acceleration
    add_subdirectory(external/llama.cpp)
else()
    message(WARNING "llama.cpp not found. LLM features will be disabled. Please run: git submodule update --init --recursive")
endif()

if(EXISTS "${CMAKE_CURRENT_SOURCE_DIR}/external/whisper.cpp")
    set(WHISPER_BUILD_SERVER OFF CACHE BOOL "" FORCE)
    set(WHISPER_BUILD_TESTS OFF CACHE BOOL "" FORCE)
    set(WHISPER_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
    set(WHISPER_NATIVE OFF CACHE BOOL "" FORCE)
    add_subdirectory(external/whisper.cpp)
else()
    message(WARNING "whisper.cpp not found. STT features will be disabled. Please run: git submodule update --init --recursive")
endif()

project("LiveHumanAI-Native")

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Find Android NDK libraries
find_library(android-lib android)
find_library(log-lib log)

# Collect all source files
file(GLOB_RECURSE CORE_SOURCES "core/*.cpp")
file(GLOB_RECURSE AI_SOURCES "ai/*.cpp")
file(GLOB_RECURSE LLM_SOURCES "llm/*.cpp")
file(GLOB_RECURSE STT_SOURCES "stt/*.cpp")
file(GLOB_RECURSE TTS_SOURCES "tts/*.cpp")
file(GLOB_RECURSE VISION_SOURCES "vision/*.cpp")
file(GLOB_RECURSE MEMORY_SOURCES "memory/*.cpp")
file(GLOB_RECURSE JALEBI_SOURCES "jalebi/*.cpp")
file(GLOB_RECURSE HARDWARE_SOURCES "hardware/*.cpp")
file(GLOB_RECURSE MODELS_SOURCES "models/*.cpp")
file(GLOB_RECURSE HUGGINGFACE_SOURCES "huggingface/*.cpp")
file(GLOB_RECURSE SECURITY_SOURCES "security/*.cpp")
file(GLOB_RECURSE DIAGNOSTICS_SOURCES "diagnostics/*.cpp")
file(GLOB_RECURSE JNI_SOURCES "jni/*.cpp")

# Create the native library
add_library(
        livehumanai-native
        SHARED
        ${CORE_SOURCES}
        ${AI_SOURCES}
        ${LLM_SOURCES}
        ${STT_SOURCES}
        ${TTS_SOURCES}
        ${VISION_SOURCES}
        ${MEMORY_SOURCES}
        ${JALEBI_SOURCES}
        ${HARDWARE_SOURCES}
        ${MODELS_SOURCES}
        ${HUGGINGFACE_SOURCES}
        ${SECURITY_SOURCES}
        ${DIAGNOSTICS_SOURCES}
        ${JNI_SOURCES}
)

# Include directories
target_include_directories(livehumanai-native PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
        ${CMAKE_CURRENT_SOURCE_DIR}/external/llama.cpp/include
        ${CMAKE_CURRENT_SOURCE_DIR}/external/whisper.cpp/include
        ${CMAKE_CURRENT_SOURCE_DIR}/external/whisper.cpp/ggml/include
)

# Link libraries
target_link_libraries(
        livehumanai-native
        ${android-lib}
        ${log-lib}
)

# Link llama.cpp if available
if(TARGET llama)
    target_link_libraries(livehumanai-native llama)
    target_compile_definitions(livehumanai-native PRIVATE HAVE_LLAMA)
endif()

# Link whisper.cpp if available
if(TARGET whisper)
    target_link_libraries(livehumanai-native whisper)
    target_compile_definitions(livehumanai-native PRIVATE HAVE_WHISPER)
endif()

# Optimization flags for ARM64
if(${ANDROID_ABI} STREQUAL "arm64-v8a")
    target_compile_options(livehumanai-native PRIVATE -O3 -ffast-math -march=armv8-a+fp+simd)
elseif(${ANDROID_ABI} STREQUAL "armeabi-v7a")
    target_compile_options(livehumanai-native PRIVATE -O3 -ffast-math -mfloat-abi=softfp -mfpu=neon)
endif()

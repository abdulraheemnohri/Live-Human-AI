# Live Human AI - CMake Configuration
# This file configures the NDK build for the native C++ components.

cmake_minimum_required(VERSION 3.10.2)

# Project name
project("LiveHumanAI_Native")

# Enable C++17
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Set C++ compiler flags
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -O2")

# Android NDK toolchain
include($ENV{NDK}/build/cmake/android.toolchain.cmake)

# Android-specific settings
set(CMAKE_POSITION_INDEPENDENT_CODE ON)
set(CMAKE_BUILD_WITH_INSTALL_RPATH ON)

# Include directories
include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/core
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/ai
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/audio
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/vision
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/memory
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/hardware
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/scheduler
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/security
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/jni
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/utils
)

# Link directories
link_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp
)

# Android log library
find_library(log-lib log)

# OpenCV (Optional, uncomment if using OpenCV)
# find_package(OpenCV REQUIRED)

# Native libraries to build
add_library(
    native-core
    SHARED
    
    # Core
    src/main/cpp/core/NativeCore.cpp
    src/main/cpp/core/NativeEngine.cpp
    
    # AI
    src/main/cpp/ai/AIEngine.cpp
    src/main/cpp/ai/JalebiLoopEngine.cpp
    src/main/cpp/ai/ModelRouter.cpp
    src/main/cpp/ai/llm/LLMManager.cpp
    src/main/cpp/ai/stt/STTManager.cpp
    src/main/cpp/ai/tts/TTSManager.cpp
    src/main/cpp/ai/vision/VisionManager.cpp
    
    # Audio
    src/main/cpp/audio/AudioProcessor.cpp
    src/main/cpp/audio/WakeWordDetector.cpp
    src/main/cpp/audio/VAD.cpp
    
    # Memory
    src/main/cpp/memory/MemoryManager.cpp
    
    # Hardware
    src/main/cpp/hardware/HardwareProfiler.cpp
    src/main/cpp/hardware/ThermalMonitor.cpp
    src/main/cpp/hardware/BatteryMonitor.cpp
    
    # Scheduler
    src/main/cpp/scheduler/TaskScheduler.cpp
    
    # Security
    src/main/cpp/security/SecurityUtils.cpp
    
    # JNI
    src/main/cpp/jni/JNIBridge.cpp
    
    # Utils
    src/main/cpp/utils/Logger.cpp
    src/main/cpp/utils/Utils.cpp
)

# Include all header files
target_include_directories(
    native-core
    PRIVATE
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/core
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/ai
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/audio
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/vision
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/memory
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/hardware
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/scheduler
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/security
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/jni
    ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/utils
)

# Link libraries
target_link_libraries(
    native-core
    
    # Android log
    ${log-lib}
    
    # C++ standard library
    c++_shared
    
    # OpenCV (Optional)
    # ${OpenCV_LIBS}
    
    # Threading
    pthread
)

# Platform-specific linker flags
if(ANDROID)
    target_link_options(
        native-core
        PRIVATE
        -Wl,--build-id=sha1
        -Wl,--warn-shared-textrel
        -Wl,--fatal-warnings
    )
endif()

# Install the library
install(
    TARGETS native-core
    LIBRARY DESTINATION ${CMAKE_INSTALL_PREFIX}/lib
    INCLUDES DESTINATION ${CMAKE_INSTALL_PREFIX}/include
)

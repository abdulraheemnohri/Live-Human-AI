# Live Human AI - CMake Configuration
cmake_minimum_required(VERSION 3.10.2)
project("LiveHumanAI_Native")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra -O2")
set(CMAKE_POSITION_INDEPENDENT_CODE ON)
set(CMAKE_BUILD_WITH_INSTALL_RPATH ON)

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

find_library(log-lib log)

add_library(
    native-core
    SHARED
    src/main/cpp/core/NativeCore.cpp
    src/main/cpp/core/NativeEngine.cpp
    src/main/cpp/ai/AIEngine.cpp
    src/main/cpp/ai/JalebiLoopEngine.cpp
    src/main/cpp/ai/JalebiRuntimeCoordinator.cpp
    src/main/cpp/ai/ModelRouter.cpp
    src/main/cpp/ai/llm/LLMManager.cpp
    src/main/cpp/ai/stt/STTManager.cpp
    src/main/cpp/ai/tts/TTSManager.cpp
    src/main/cpp/ai/vision/VisionManager.cpp
    src/main/cpp/hardware/HardwareProfiler.cpp
    src/main/cpp/hardware/ThermalMonitor.cpp
    src/main/cpp/hardware/BatteryMonitor.cpp
    src/main/cpp/scheduler/TaskScheduler.cpp
    src/main/cpp/jni/JNIBridge.cpp
    src/main/cpp/utils/Logger.cpp
)

target_include_directories(
    native-core PRIVATE
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

target_link_libraries(native-core ${log-lib} c++_shared)

if(ANDROID)
    target_link_options(native-core PRIVATE
        -Wl,--build-id=sha1
        -Wl,--warn-shared-textrel
        -Wl,--fatal-warnings
    )
endif()

install(TARGETS native-core
    LIBRARY DESTINATION ${CMAKE_INSTALL_PREFIX}/lib
    INCLUDES DESTINATION ${CMAKE_INSTALL_PREFIX}/include
)

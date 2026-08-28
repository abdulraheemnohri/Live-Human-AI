# Live Human AI - Native NDK configuration
cmake_minimum_required(VERSION 3.22.1)
project("LiveHumanAI_Native")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

# Reproducible local LLM runtime. The pinned revision keeps the public C API
# used by LlamaCppBackend.cpp stable across Android builds.
include(FetchContent)
set(LLAMA_BUILD_IS_DEV OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_COMMON OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_TESTS OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_TOOLS OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_SERVER OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_APP OFF CACHE BOOL "" FORCE)
set(LLAMA_BUILD_HTML OFF CACHE BOOL "" FORCE)
set(LLAMA_OPENSSL OFF CACHE BOOL "" FORCE)
set(GGML_NATIVE OFF CACHE BOOL "" FORCE)
set(GGML_OPENMP OFF CACHE BOOL "" FORCE)
set(GGML_VULKAN OFF CACHE BOOL "" FORCE)
FetchContent_Declare(
    llama_cpp
    GIT_REPOSITORY https://github.com/ggml-org/llama.cpp.git
    GIT_TAG b19cbe925be361d229f0fe03435affe4a2717f37
    GIT_SHALLOW TRUE
)
FetchContent_MakeAvailable(llama_cpp)
add_compile_options(-Wall -Wextra -Wpedantic -Wshadow -Wconversion)

set(LH_CPP ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp)
file(GLOB_RECURSE LH_NATIVE_SOURCES CONFIGURE_DEPENDS "${LH_CPP}/*.cpp")
find_library(log-lib log REQUIRED)
add_library(native-core SHARED ${LH_NATIVE_SOURCES})

target_include_directories(native-core PRIVATE
    ${LH_CPP} ${LH_CPP}/core ${LH_CPP}/ai ${LH_CPP}/audio ${LH_CPP}/vision
    ${LH_CPP}/memory ${LH_CPP}/hardware ${LH_CPP}/scheduler ${LH_CPP}/security
    ${LH_CPP}/jni ${LH_CPP}/utils)

target_compile_definitions(native-core PRIVATE LIVE_HUMAN_AI_NDK=1 JALEBI_COGNITIVE_LOOP=1)

# Optional ONNX Runtime Android distribution.
set(ONNXRUNTIME_ROOT "${ONNXRUNTIME_ROOT}" CACHE PATH "ONNX Runtime Android root")
if(ONNXRUNTIME_ROOT)
    find_path(ONNXRUNTIME_INCLUDE_DIR NAMES onnxruntime_cxx_api.h PATHS "${ONNXRUNTIME_ROOT}/include" NO_DEFAULT_PATH)
    find_library(ONNXRUNTIME_LIBRARY NAMES onnxruntime PATHS "${ONNXRUNTIME_ROOT}/lib/${ANDROID_ABI}" "${ONNXRUNTIME_ROOT}/lib" NO_DEFAULT_PATH)
    if(ONNXRUNTIME_INCLUDE_DIR AND ONNXRUNTIME_LIBRARY)
        target_include_directories(native-core PRIVATE ${ONNXRUNTIME_INCLUDE_DIR})
        target_link_libraries(native-core PRIVATE ${ONNXRUNTIME_LIBRARY})
        target_compile_definitions(native-core PRIVATE LIVE_HUMAN_AI_HAS_ONNXRUNTIME=1)
    else()
        message(WARNING "ONNXRUNTIME_ROOT supplied but compatible Android ONNX Runtime was not found; vision inference is unavailable.")
    endif()
endif()

# Optional Whisper.cpp distribution. Set WHISPER_ROOT to a directory containing
# include/whisper.h and an ABI-compatible libwhisper shared/static library.
set(WHISPER_ROOT "${WHISPER_ROOT}" CACHE PATH "Whisper.cpp Android root")
if(WHISPER_ROOT)
    find_path(WHISPER_INCLUDE_DIR NAMES whisper.h PATHS "${WHISPER_ROOT}/include" "${WHISPER_ROOT}" NO_DEFAULT_PATH)
    find_library(WHISPER_LIBRARY NAMES whisper PATHS "${WHISPER_ROOT}/lib/${ANDROID_ABI}" "${WHISPER_ROOT}/lib" "${WHISPER_ROOT}" NO_DEFAULT_PATH)
    if(WHISPER_INCLUDE_DIR AND WHISPER_LIBRARY)
        target_include_directories(native-core PRIVATE ${WHISPER_INCLUDE_DIR})
        target_link_libraries(native-core PRIVATE ${WHISPER_LIBRARY})
        target_compile_definitions(native-core PRIVATE LIVE_HUMAN_AI_HAS_WHISPER=1)
    else()
        message(WARNING "WHISPER_ROOT supplied but compatible Whisper.cpp include/library was not found; native STT is unavailable.")
    endif()
endif()

target_link_libraries(native-core PRIVATE ${log-lib} c++_shared)

if(ANDROID)
    target_compile_definitions(native-core PRIVATE __ANDROID__=1)
    target_link_options(native-core PRIVATE -Wl,--build-id=sha1 -Wl,--warn-shared-textrel)
endif()

set_target_properties(native-core PROPERTIES CXX_VISIBILITY_PRESET hidden VISIBILITY_INLINES_HIDDEN YES)

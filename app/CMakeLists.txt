# Live Human AI - Native NDK configuration
cmake_minimum_required(VERSION 3.22.1)
project("LiveHumanAI_Native")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

add_compile_options(-Wall -Wextra -Wpedantic -Wshadow -Wconversion)

set(LH_CPP ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp)

file(GLOB_RECURSE LH_NATIVE_SOURCES CONFIGURE_DEPENDS
    "${LH_CPP}/*.cpp"
)

find_library(log-lib log REQUIRED)

add_library(native-core SHARED ${LH_NATIVE_SOURCES})

target_include_directories(native-core PRIVATE
    ${LH_CPP}
    ${LH_CPP}/core
    ${LH_CPP}/ai
    ${LH_CPP}/audio
    ${LH_CPP}/vision
    ${LH_CPP}/memory
    ${LH_CPP}/hardware
    ${LH_CPP}/scheduler
    ${LH_CPP}/security
    ${LH_CPP}/jni
    ${LH_CPP}/utils
)

target_compile_definitions(native-core PRIVATE
    LIVE_HUMAN_AI_NDK=1
    JALEBI_COGNITIVE_LOOP=1
)

target_link_libraries(native-core PRIVATE ${log-lib} c++_shared)

if(ANDROID)
    target_compile_definitions(native-core PRIVATE __ANDROID__=1)
    target_link_options(native-core PRIVATE
        -Wl,--build-id=sha1
        -Wl,--warn-shared-textrel
    )
endif()

set_target_properties(native-core PROPERTIES
    CXX_VISIBILITY_PRESET hidden
    VISIBILITY_INLINES_HIDDEN YES
)

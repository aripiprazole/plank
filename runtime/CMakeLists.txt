cmake_minimum_required(VERSION 3.16)
project(plank_runtime)

set(CMAKE_CXX_STANDARD 20)

include_directories(include)

file(GLOB HEADER_FILES include/plank/**.h)
file(GLOB SRC_FILES src/**.cpp src/**.h)

add_library(plank_runtime SHARED ${HEADER_FILES} ${SRC_FILES})

set_target_properties(plank_runtime PROPERTIES RULE_LAUNCH_LINK
        "${CMAKE_SOURCE_DIR}/scripts/copy_objects ${TARGET_OBJECTS_DIR} <OBJECTS> --")

install(TARGETS plank_runtime)

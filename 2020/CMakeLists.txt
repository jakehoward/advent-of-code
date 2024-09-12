cmake_minimum_required(VERSION 3.28)
project(aoc)

set(CMAKE_CXX_STANDARD 17)

file(GLOB SOURCES "src/days/*.cpp")
include_directories(src src/days)
add_executable(aoc main.cpp ${SOURCES} src/utils.cpp
        src/utils.h)

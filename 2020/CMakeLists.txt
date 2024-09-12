cmake_minimum_required(VERSION 3.28)
project(aoc)

set(CMAKE_CXX_STANDARD 17)

include_directories(src src/days)
add_executable(aoc main.cpp src/utils.cpp src/days/1.cpp src/days/2.cpp src/utils.h)

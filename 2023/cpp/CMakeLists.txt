cmake_minimum_required(VERSION 3.28)
project(aoc)

set(CMAKE_CXX_STANDARD 20)

include_directories(src src/days)
file(GLOB SOURCES "src/*.cpp" "src/**/*.cpp")
#message("Sources: ${SOURCES}")

find_package(Boost 1.86)
include_directories(${Boost_INCLUDE_DIRS})

add_executable(aoc main.cpp ${SOURCES})
target_link_libraries(aoc ${Boost_LIBRARIES})

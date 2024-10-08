# Apple MacOS
# brew install llvm
# echo 'export PATH="/opt/homebrew/opt/llvm/bin:$PATH"' >> ~/.zshrc
# source ~/.zshrc

cmake_minimum_required(VERSION 3.28)
project(aoc)

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

set(CMAKE_C_COMPILER "/opt/homebrew/opt/llvm/bin/clang")
set(CMAKE_CXX_COMPILER "/opt/homebrew/opt/llvm/bin/clang++")

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -stdlib=libc++")

include_directories(src src/days)
include_directories("/opt/homebrew/opt/llvm/include")

file(GLOB SOURCES "src/*.cpp" "src/**/*.cpp")
#message("Sources: ${SOURCES}")

find_package(Boost 1.86)
include_directories(${Boost_INCLUDE_DIRS})

add_executable(aoc main.cpp ${SOURCES})
target_link_libraries(aoc ${Boost_LIBRARIES})
target_link_directories(aoc PRIVATE "/opt/homebrew/opt/llvm/lib")
cmake_minimum_required(VERSION 3.28)
project(aoc)

set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

if(APPLE)
    if(EXISTS "/opt/homebrew/opt/llvm/bin/clang++")
        set(CMAKE_CXX_COMPILER "/opt/homebrew/opt/llvm/bin/clang++")
        set(CMAKE_C_COMPILER "/opt/homebrew/opt/llvm/bin/clang")
        set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -L/opt/homebrew/opt/llvm/lib/c++ -Wl,-rpath,/opt/homebrew/opt/llvm/lib/c++")
    elseif(EXISTS "/usr/local/opt/llvm/bin/clang++")
        set(CMAKE_C_COMPILER "/usr/local/opt/llvm/bin/clang")
        set(CMAKE_CXX_COMPILER "/usr/local/opt/llvm/bin/clang++")

    endif()
endif()


set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -stdlib=libc++")
set(CMAKE_CXX_FLAGS_RELEASE "-O3 -ffast-math -flto -march=native")

add_library(utils INTERFACE)
target_include_directories(utils INTERFACE include)

#find_package(Catch2 3 REQUIRED)

file(GLOB DAY_SOURCE_FILES "days/*.cpp")
foreach (SOURCE_FILE ${DAY_SOURCE_FILES})
    if(SOURCE_FILE MATCHES ".*\\.test\\.cpp$")
        continue()
    endif()

    get_filename_component(FILE_NAME ${SOURCE_FILE} NAME)
    add_executable(${FILE_NAME} ${SOURCE_FILE})
    target_link_libraries(${FILE_NAME} PRIVATE utils)

#    set(TEST_FILE "days/${FILE_NAME}.test.cpp")
#    if (EXISTS ${TEST_FILE})
#        add_executable("test_${FILE_NAME}" ${TEST_FILE} ${SOURCE_FILE})
#        target_link_libraries("test_${FILE_NAME}" PRIVATE Catch2::Catch2WithMain utils)
#    endif ()
endforeach ()

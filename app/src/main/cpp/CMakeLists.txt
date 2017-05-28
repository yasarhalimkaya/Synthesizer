cmake_minimum_required(VERSION 3.4.1)

add_library(# Name of the library
            openslplayerjni

            # Type of the library
            SHARED

            # Source files
            openslplayerjni.cpp
            OpenSLPlayerNative.cpp)

# Path to native header files.
include_directories(src/main/cpp/)

# Find required NDK libraries
find_library(logPath
             log)
find_library(OpenSLESPath
             OpenSLES)

# Link NDK libraries
target_link_libraries(# Library to link against
                      openslplayerjni

                      # Paths from find_library
                      ${logPath}
                      ${OpenSLESPath})
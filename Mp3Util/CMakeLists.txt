cmake_minimum_required(VERSION 3.4.1)
#set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/jniLibs/${ANDROID_ABI})
#设置变量SRC_DIR为lamemp3的所在路径
set(SRC_DIR src/main/cpp/lame)

#指定头文件所在，可以多次调用，指定多个路径
include_directories(src/main/cpp/lame)

#设定一个目录
aux_source_directory(src/main/cpp/lame SRC_LIST)

add_library( # Sets the name of the library.
        lamemp3
        SHARED
        src/main/cpp/Mp3Encoder.c
        ${SRC_LIST}) #src/main/cpp/native-lib.cpp
        ##)
#
#find_library(
#        log-lib
#        log)

target_link_libraries(
        lamemp3
#        ${log-lib}
)
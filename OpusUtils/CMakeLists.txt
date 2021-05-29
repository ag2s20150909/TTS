cmake_minimum_required(VERSION 3.10.2)

#set(libs_DIR src/main/jniLibs/${ANDROID_ABI}/libopus.so)
set(libs_include_DIR src/main/cpp/opus/include)
#支持-std=gnu++11
set(CMAKE_VERBOSE_MAKEFILE on)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")


#find_library( # Sets the name of the path variable.
#        log-lib
#        log )
add_library( # Sets the name of the library.
        opusTool
        SHARED
        ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/opus-use.cpp)

include_directories(${libs_include_DIR})
#link_directories(${libs_include_DIR})

#关联头文件目录到库名
target_include_directories(
        opusTool # 库名
        PRIVATE # 库属性
        ${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/opus/include # 头文件目录
)


target_link_libraries( # Specifies the target library.
        opusTool)

add_library(opus_my SHARED IMPORTED)
set_target_properties(
        opus_my
        PROPERTIES IMPORTED_LOCATION
        ${CMAKE_CURRENT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopus.so
)




target_link_libraries( # Specifies the target library.
        opusTool
        ${log-lib}
        opus_my)




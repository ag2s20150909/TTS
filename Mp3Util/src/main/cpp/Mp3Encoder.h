#include <jni.h>

#ifndef _Included_Mp3Encoder
#define _Included_Mp3Encoder
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com.zlw.main.recorderlib.recorder.mp3.Mp3Encoder
 * Method:    init
 */
JNIEXPORT void JNICALL Java_me_ag2s_mp3util_Mp3Encoder_init
(JNIEnv *, jobject thiz, jint, jint, jint, jint, jint);

JNIEXPORT jint JNICALL Java_me_ag2s_mp3util_Mp3Encoder_encode
        (JNIEnv *, jobject thiz, jshortArray, jshortArray, jint, jbyteArray);

JNIEXPORT jint JNICALL Java_me_ag2s_mp3util_Mp3Encoder_flush
        (JNIEnv *, jobject thiz, jbyteArray);

JNIEXPORT void JNICALL Java_me_ag2s_mp3util_Mp3Encoder_close
(JNIEnv *, jobject thiz);

JNIEXPORT jstring JNICALL Java_me_ag2s_mp3util_Mp3Encoder_getVersion
        (JNIEnv *, jobject thiz);


#ifdef __cplusplus
}
#endif
#endif
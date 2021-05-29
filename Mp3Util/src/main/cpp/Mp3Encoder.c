#include "lame/lame.h"
#include "Mp3Encoder.h"
#include <string.h>


static lame_global_flags *glf = NULL;
//TODO这里包名要与java中对接文件的路径一致(这里是路径是com.zlw.main.recorderlib.recorder.mp3，java文件: Mp3Encoder.java),下同
JNIEXPORT void JNICALL Java_me_ag2s_mp3util_Mp3Encoder_init(
        JNIEnv *env, jobject cls, jint inSamplerate, jint outChannel,
        jint outSamplerate, jint outBitrate, jint quality) {
    if (glf != NULL) {
        lame_close(glf);
        glf = NULL;
    }
    glf = lame_init();
    lame_set_in_samplerate(glf, inSamplerate);
    lame_set_num_channels(glf, outChannel);
    lame_set_out_samplerate(glf, outSamplerate);
    lame_set_brate(glf, outBitrate);
    lame_set_quality(glf, quality);
    lame_init_params(glf);
}

JNIEXPORT jint JNICALL Java_me_ag2s_mp3util_Mp3Encoder_encode(
        JNIEnv *env, jobject cls, jshortArray buffer_l, jshortArray buffer_r,
        jint samples, jbyteArray mp3buf) {
    jshort *j_buffer_l = (*env)->GetShortArrayElements(env, buffer_l, NULL);

    jshort *j_buffer_r = (*env)->GetShortArrayElements(env, buffer_r, NULL);

    const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
    jbyte *j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);

    int result = lame_encode_buffer(glf, j_buffer_l, j_buffer_r,
                                    samples, mp3buf, mp3buf_size);

    (*env)->ReleaseShortArrayElements(env, buffer_l, j_buffer_l, 0);
    (*env)->ReleaseShortArrayElements(env, buffer_r, j_buffer_r, 0);
    (*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);

    return result;
}

JNIEXPORT jint JNICALL Java_me_ag2s_mp3util_Mp3Encoder_flush(
        JNIEnv *env, jobject cls, jbyteArray mp3buf) {
    const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
    jbyte *j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);

    int result = lame_encode_flush(glf, mp3buf, mp3buf_size);

    (*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);

    return result;
}

JNIEXPORT void JNICALL Java_me_ag2s_mp3util_Mp3Encoder_close(
        JNIEnv *env, jobject cls) {
    lame_close(glf);
    glf = NULL;
}

 JNIEXPORT jstring JNICALL
Java_me_ag2s_mp3util_Mp3Encoder_getVersion(JNIEnv *env, jobject thiz) {
     return (*env)->NewStringUTF(env,get_lame_version());//->String(get_lame_version());
}
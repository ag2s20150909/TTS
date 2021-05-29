#include <jni.h>
#include <opus.h>

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL Java_me_ag2s_opusutils_OpusUtils_createEncoder
        (JNIEnv *env, jobject thiz, jint sampleRateInHz, jint channelConfig, jint complexity) {
    int error;
    OpusEncoder *pOpusEnc = opus_encoder_create(sampleRateInHz, channelConfig,
                                                OPUS_APPLICATION_RESTRICTED_LOWDELAY,
                                                &error);
    if (pOpusEnc) {
        opus_encoder_ctl(pOpusEnc, OPUS_SET_VBR(0));//0:CBR, 1:VBR
        opus_encoder_ctl(pOpusEnc, OPUS_SET_VBR_CONSTRAINT(true));
        opus_encoder_ctl(pOpusEnc, OPUS_SET_BITRATE(32000));//波特率单位是比特每秒（B / S）
        opus_encoder_ctl(pOpusEnc, OPUS_SET_COMPLEXITY(complexity));//复杂度 8    0~10
        opus_encoder_ctl(pOpusEnc, OPUS_SET_SIGNAL(OPUS_SIGNAL_VOICE));//信号 OPUS_AUTO（默认），OPUS_SIGNAL_VOICE或OPUS_SIGNAL_MUSIC
        opus_encoder_ctl(pOpusEnc, OPUS_SET_LSB_DEPTH(16));
        opus_encoder_ctl(pOpusEnc, OPUS_SET_DTX(0));
        opus_encoder_ctl(pOpusEnc, OPUS_SET_INBAND_FEC(0));
        opus_encoder_ctl(pOpusEnc, OPUS_SET_PACKET_LOSS_PERC(0));
    }
    return (jlong) pOpusEnc;
}
JNIEXPORT jlong JNICALL Java_me_ag2s_opusutils_OpusUtils_createDecoder
        (JNIEnv *env, jobject thiz, jint sampleRateInHz, jint channelConfig) {
    int error;
    OpusDecoder *pOpusDec = opus_decoder_create(sampleRateInHz, channelConfig, &error);
    return (jlong) pOpusDec;
}
JNIEXPORT jint JNICALL Java_me_ag2s_opusutils_OpusUtils_encode
        (JNIEnv *env, jobject thiz, jlong pOpusEnc, jshortArray samples, jint offset,
         jbyteArray bytes) {
    OpusEncoder *pEnc = (OpusEncoder *) pOpusEnc;
    if (!pEnc || !samples || !bytes)
        return 0;
    jshort *pSamples = env->GetShortArrayElements(samples, 0);
    jsize nSampleSize = env->GetArrayLength(samples);
    jbyte *pBytes = env->GetByteArrayElements(bytes, 0);
    jsize nByteSize = env->GetArrayLength(bytes);
    if (nSampleSize - offset < 320 || nByteSize <= 0)
        return 0;
    /*
frame_size = opus_decode(dec, packet, len, decoded, max_size, 0);
在这里
packet是包含压缩数据的字节数组
len为字节包的实际长度
decoded是在opus_int16解码音频数据（或opus_decode_float（））
MAX_SIZE是decoded_frame数组大小
     * */
    int nRet = opus_encode(pEnc, pSamples + offset, nSampleSize, (unsigned char *) pBytes,
                           nByteSize);
    env->ReleaseShortArrayElements(samples, pSamples, 0);
    env->ReleaseByteArrayElements(bytes, pBytes, 0);
    return nRet;
}
JNIEXPORT jint JNICALL Java_me_ag2s_opusutils_OpusUtils_decode
        (JNIEnv *env, jobject thiz, jlong pOpusDec, jbyteArray bytes,
         jshortArray samples) {
    OpusDecoder *pDec = (OpusDecoder *) pOpusDec;
    if (!pDec || !samples || !bytes)
        return 0;
    jshort *pSamples = env->GetShortArrayElements(samples, 0);
    jbyte *pBytes = env->GetByteArrayElements(bytes, 0);
    jsize nByteSize = env->GetArrayLength(bytes);
    jsize nSamplesSize = env->GetArrayLength(samples);
    if (nByteSize <= 0 || nSamplesSize <= 0) {
        return -1;
    }
    /*
frame_size = opus_decode(dec, packet, len, decoded, max_size, 0);
在这里
packet是包含压缩数据的字节数组
len为字节包的实际长度
decoded是在opus_int16解码音频数据（或opus_decode_float（））
MAX_SIZE是decoded_frame数组大小
     * */
/*    opus_int16 *out;
    out = new opus_int16[48000 / 80 * 2];
    int nRet = opus_decode(pDec, (unsigned char *) pBytes, nByteSize, out, 48000 / 80 * 2, 0);*/
    int nRet = opus_decode(pDec, (unsigned char *) pBytes, nByteSize, pSamples, nSamplesSize, 0);
    env->ReleaseShortArrayElements(samples, pSamples, 0);
    env->ReleaseByteArrayElements(bytes, pBytes, 0);
    return nRet;
}
JNIEXPORT void JNICALL Java_me_ag2s_opusutils_OpusUtils_destroyEncoder
(JNIEnv *env, jobject thiz, jlong pOpusEnc) {
OpusEncoder *pEnc = (OpusEncoder *) pOpusEnc;
if (!pEnc)
return;
opus_encoder_destroy(pEnc);
}
JNIEXPORT void JNICALL Java_me_ag2s_opusutils_OpusUtils_destroyDecoder
(JNIEnv *env, jobject thiz, jlong pOpusDec) {
OpusDecoder *pDec = (OpusDecoder *) pOpusDec;
if (!pDec)
return;
opus_decoder_destroy(pDec);
}
#ifdef __cplusplus
}
#endif
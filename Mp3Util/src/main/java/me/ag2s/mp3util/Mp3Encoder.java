package me.ag2s.mp3util;

public class Mp3Encoder {
    static {
        System.loadLibrary("lamemp3");
    }
    public native void init(int inSamplerate,int outChannel,int outSamplerate,int outBitrate,int quality);
    public native  int encode(short[] buffer_l,short[] buffer_,int samples,byte[] mp3buf);
    public native int flush(byte[] mp3buf);
    public native String getVersion();
    public native void close();

}

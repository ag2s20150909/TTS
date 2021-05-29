package me.ag2s.opusutils;

public class OpusUtils {
    static {
        System.loadLibrary("opusTool");
    }

    private static OpusUtils instance;
    public static OpusUtils getInstance(){
        if (instance==null){
            instance=new OpusUtils();
        }
        return instance;
    }


    public native long createEncoder(int sampleRateInHz ,int channelConfig, int complexity);
    public native long createDecoder(int sampleRateInHz,int channelConfig);
    public native int encode(long handle,short[] lin,int offset,byte[] encoded);
    public native int decode(long handle, byte[] encoded,short[] lin);
    public native void destroyEncoder(long handle);
    public native void destroyDecoder(long handle);
}

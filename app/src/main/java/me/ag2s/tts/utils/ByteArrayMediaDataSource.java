package me.ag2s.tts.utils;

import android.media.MediaDataSource;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.M)
public class ByteArrayMediaDataSource extends MediaDataSource {
    private byte[] data;

    public ByteArrayMediaDataSource(@NonNull byte[] data) {
        this.data = data;
    }

    @Override
    public int readAt(long position, byte[] buffer, int offset, int size) {
        if (position >= data.length) {
            return -1;
        }
        int endPosition = (int) (position + size);
        int size2 = size;
        if (endPosition > data.length) {
            size2 -= endPosition - data.length;
        }
        System.arraycopy(data, (int) position, buffer, offset, size2);
        return size2;
    }

    @Override
    public long getSize() {
        return data.length;
    }

    @Override
    public void close() {
        data = null;
    }
}

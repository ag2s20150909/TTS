package me.ag2s.tts.utils;

import android.os.SystemClock;

public final class GcManger {
    private static volatile GcManger instance;
    public long last = 0;

    private GcManger() {
    }

    public static GcManger getInstance() {
        if (instance == null) {
            synchronized (GcManger.class) {
                if (instance == null) {
                    instance = new GcManger();
                }
            }
        }
        return instance;
    }

    /**
     * 避免频繁GC
     */
    public void doGC() {
        synchronized (this) {
            if (SystemClock.elapsedRealtime() - last > 10000) {
                Runtime.getRuntime().gc();
                last = SystemClock.elapsedRealtime();
            }
        }

    }
}

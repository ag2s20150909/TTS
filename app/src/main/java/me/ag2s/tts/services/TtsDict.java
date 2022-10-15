package me.ag2s.tts.services;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class TtsDict implements Comparable<TtsDict> {
    private final String world;
    private final String ph;
    private final boolean isRegex;

    public TtsDict(String wd, String ph) {
        this.world = wd;
        this.ph = ph;
        this.isRegex = false;
    }

    public TtsDict(String wd, String ph, boolean isRegex) {
        this.world = wd;
        this.ph = ph;
        this.isRegex = isRegex;
    }

    public boolean isRegex() {
        return isRegex;
    }

    public String getWorld() {
        return this.world;
    }

    public String getXML(String name) {
        if (isRegex) {
            return this.ph;
        } else {
            if (name.startsWith("zh-CN") || name.startsWith("zh-HK") || name.startsWith("zh-TW")
                    || name.startsWith("en-US") || name.startsWith("ja-JP") || name.startsWith("de-DE")
                    || name.startsWith("fr-FR") || name.startsWith("es-ES")) {
                return "<phoneme alphabet='sapi' ph='" + this.ph + "' >" + this.world + "</phoneme>";

            } else {
                return "<phoneme alphabet='ipa' ph='" + this.ph + "' >" + this.world + "</phoneme>";
            }

        }

    }

    @SuppressWarnings("unused")
    public JSONObject toJsonObject() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("wd", world);
        jo.put("ph", ph);
        jo.put("isRegex", isRegex);
        return jo;
    }

    @Override
    public int compareTo(TtsDict o) {

        //普通规则排在前面，正则规则排在后面
        if (this.isRegex() != o.isRegex) {
            if (this.isRegex) {
                return 1;
            } else {
                return -1;
            }
        } else if (this.world.length() != o.world.length()) {
            //长的词排在前面
            return o.world.length() - this.world.length();
        } else {
            //长度相同自然排序
            return this.world.compareTo(o.world);
        }

    }

    @NonNull
    @Override
    public String toString() {
        return "TtsDict{" +
                "world='" + world + '\'' +
                ", ph='" + ph + '\'' +
                ", isRegex=" + isRegex +
                '}';
    }
}

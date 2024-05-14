package me.ag2s.tts.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;
import java.util.Objects;

public class TtsActor {
    /**
     * 标准名称
     */
    @NonNull
    private final String name;

    /**
     * 简写名称
     */
    @NonNull
    private final String shortName;
    /**
     * 性别,true 为女性，false为男性
     */
    private final boolean gender;

    /**
     * 地区
     */
    @NonNull
    private final String locale;

    /**
     * 注释
     */
    @Nullable
    private final String note;

    private Locale tempLocate;


    public TtsActor(@NonNull String name, @NonNull String shortName, boolean gender, @NonNull String locale, @NonNull String note) {
        this.name = name;
        this.shortName = shortName;
        this.gender = gender;
        this.locale = locale;
        this.note = note;
    }


    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getShortName() {
        return shortName;
    }

    public boolean getGender() {
        return gender;
    }

    @NonNull
    public String getLocaleStr() {
        return locale;
    }

    public Locale getLocale() {

        if (tempLocate != null) {
            return tempLocate;
        }


        String tag = "-";
        if (locale.contains("-")) {
            tag = "-";
        } else if (locale.contains("_")) {
            tag = "_";
        }
        String[] temp = locale.split(tag);
        tempLocate = new Locale(temp[0], temp[1], gender ? "Female" : "Male");
        return tempLocate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TtsActor ttsActor = (TtsActor) o;
        return gender == ttsActor.gender && Objects.equals(name, ttsActor.name) && Objects.equals(shortName, ttsActor.shortName) && Objects.equals(locale, ttsActor.locale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, shortName, gender, locale);
    }

    public Object getNote() {
        return this.note;
    }
}

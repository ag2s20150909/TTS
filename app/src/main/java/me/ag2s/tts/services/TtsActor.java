package me.ag2s.tts.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Locale;

public class TtsActor {

    /**
     * 标准名称
     */
    @NonNull
    private String name;

    /**
     * 简写名称
     */
    @NonNull
    private String shortName;
    /**
     * 性别,true 为女性，false为男性
     */
    private boolean gender;
    /**
     * 地区
     */
    @NonNull
    private String locale;
    /**
     * 注释
     */
    @Nullable
    private String note;
    private Locale tempLocate;

    public TtsActor(@NonNull String name, @NonNull String shortName, @NonNull String locate, boolean gender, @Nullable String note) {
        this.name = name;
        this.shortName = shortName;
        this.locale = locate;
        this.gender = gender;
        this.note = note;
    }

    public TtsActor(String shortName, boolean gender, @Nullable String note) {
        this.gender = gender;
        this.note = note;
        this.shortName = shortName;
        String tag = "-";
        if (shortName.contains("-")) {
            tag = "-";
        } else if (shortName.contains("_")) {
            tag = "_";
        }
        //String[] temp = locale.split(tag);

        this.name = shortName.substring(shortName.lastIndexOf(tag) + 1).replace("Neural", "");
        this.locale = shortName.substring(0, shortName.lastIndexOf(tag));

    }

    @SuppressWarnings("unused")
    public TtsActor(String name, String shortName, String locate, boolean gender) {
        this(name, shortName, locate, gender, "");
    }

    @NonNull
    public String getName() {
        return name;
    }

    @SuppressWarnings("unused")
    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getShortName() {
        return shortName;
    }

    @SuppressWarnings("unused")
    public void setShortName(@NonNull String shortName) {
        this.shortName = shortName;
    }

    public boolean getGender() {
        return gender;
    }

    @SuppressWarnings("unused")
    public void setGender(boolean gender) {
        this.gender = gender;
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
        return new Locale(temp[0], temp[1], gender ? "Female" : "Male");
    }

    @SuppressWarnings("unused")
    public void setLocale(@NonNull String locale) {
        this.locale = locale;
    }


    @Nullable
    public String getNote() {
        return note;
    }

    @SuppressWarnings("unused")
    public void setNote(@Nullable String note) {
        this.note = note;
    }
}



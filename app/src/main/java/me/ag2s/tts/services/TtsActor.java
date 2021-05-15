package me.ag2s.tts.services;

import java.util.Locale;

public class TtsActor {

    /**
     * 标准名称
     */
    private String name;

    /**
     * 简写名称
     */
    private String shortName;
    /**
     * 性别,true 为女性，false为男性
     */
    private boolean gender;
    /**
     * 地区
     */
    private String locale;
    /**
     * 注释
     */
    private String note;

    public TtsActor(String name, String shortName, String locate, boolean gender, String note) {
        this.name = name;
        this.shortName = shortName;
        this.locale = locate;
        this.gender = gender;
        this.note = note;
    }

    public TtsActor(String shortName, boolean gender, String note) {
        this.gender=gender;
        this.note=note;
        this.shortName=shortName;
        String tag = "-";
        if (shortName.contains("-")) {
            tag = "-";
        } else if (shortName.contains("_")) {
            tag = "_";
        }
        //String[] temp = locale.split(tag);

        this.name = shortName.substring(shortName.lastIndexOf(tag) + 1).replace("Neural","");
        this.locale = shortName.substring(0, shortName.lastIndexOf(tag));

    }

    public TtsActor(String name, String shortName, String locate, boolean gender) {
        this(name, shortName, locate, gender, "");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public boolean getGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public Locale getLocale() {
        String tag = "-";
        if (locale.contains("-")) {
            tag = "-";
        } else if (locale.contains("_")) {
            tag = "_";
        }
        String[] temp = locale.split(tag);
        return new Locale(temp[0], temp[1], gender ? "Female" : "Male");
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

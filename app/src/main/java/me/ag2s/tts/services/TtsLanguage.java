package me.ag2s.tts.services;

import java.util.Locale;

public class TtsLanguage {


    private String name;

    private String shortName;

    private String gender;

    private Locale locale;

    private String suggestedCodec;

    private String friendlyName;
    private String status;

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = Locale.forLanguageTag(locale);
    }

    public String getSuggestedCodec() {
        return suggestedCodec;
    }

    public void setSuggestedCodec(String suggestedCodec) {
        this.suggestedCodec = suggestedCodec;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

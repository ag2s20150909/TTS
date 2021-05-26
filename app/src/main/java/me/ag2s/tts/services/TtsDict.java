package me.ag2s.tts.services;

import org.json.JSONException;
import org.json.JSONObject;

public class TtsDict {
    public String world;
    public String ph;
    public TtsDict(String wd,String ph){
      this.world=wd;
      this.ph=ph;
    }
    public JSONObject toJsonObject() throws JSONException {
       JSONObject jo= new JSONObject();
       jo.put("wd",world);
       jo.put("ph",ph);
       return  jo;
    }
}

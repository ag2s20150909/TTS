package me.ag2s.tts.services;

import org.json.JSONException;
import org.json.JSONObject;

public class TtsDict implements Comparable<TtsDict> {
    private final String world;
    private final String ph;
    public TtsDict(String wd,String ph){
      this.world=wd;
      this.ph=ph;
    }

    public String getWorld(){
        return this.world;
    }
    public String getXML(){
        return "<phoneme alphabet='sapi' ph='"+this.ph+"' >"+this.world+"</phoneme>";
    }
    @SuppressWarnings("unused")
    public JSONObject toJsonObject() throws JSONException {
       JSONObject jo= new JSONObject();
       jo.put("wd",world);
       jo.put("ph",ph);
       return  jo;
    }

    @Override
    public int compareTo(TtsDict o) {
        if(this.world.length()!=o.world.length()){
            return o.world.length()-this.world.length();
        }else {
            return this.world.compareTo(o.world);
        }

    }
}

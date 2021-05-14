package me.ag2s.tts.services;

import java.util.ArrayList;

public class TtsOutputFormat {
   public String name;
   public String value;
   public int HZ;
   public int BitRate;
   public TtsOutputFormat(String name,String value,int hz,int bitRate){
       this.name=name;
       this.value=value;
       this.HZ=hz;
       this.BitRate=bitRate;
   }

}

package me.ag2s.tts.services;

import java.util.ArrayList;
import java.util.List;

public class TtsDictManger {
    //单例
    private static TtsDictManger instance;

    public static TtsDictManger getInstance() {
        if (instance == null) {
            instance = new TtsDictManger();
        }
        return instance;
    }
    private final List<TtsDict> dict;
    private TtsDictManger(){
        dict =new ArrayList<>(20);
        dict.add(new TtsDict("佛然","bo 2 ran 2"));
    }

    public void add(TtsDict dic){
        dict.add(dic);

    }

    public List<TtsDict> getDict() {
        return dict;
    }
}

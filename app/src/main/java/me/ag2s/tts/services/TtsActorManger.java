package me.ag2s.tts.services;

import java.util.ArrayList;
import java.util.List;

public class TtsActorManger {
    //单例
    private static TtsActorManger instance;

    public static TtsActorManger getInstance() {
        if (instance == null) {
            instance = new TtsActorManger();
        }
        return instance;
    }

    private List<TtsActor> actors;

    private TtsActorManger() {
        actors = new ArrayList<>();
        actors.add(new TtsActor("晓晓", "zh-cn-XiaoxiaoNeural", "zh-cn", true, "常规，使用 SSML 提供多种语音风格"));
        actors.add(new TtsActor("晓悠", "zh-cn-XiaoyouNeural", "zh-cn", true, "儿童语音，针对讲故事进行了优化"));
        actors.add(new TtsActor("云扬", "zh-cn-YunyangNeural", "zh-cn", false, "针对新闻阅读进行了优化，使用 SSML 提供多种语音风格"));
        actors.add(new TtsActor("云野", "zh-cn-YunyeNeural", "zh-cn", false, "针对讲故事进行了优化"));
        actors.add(new TtsActor("云希", "zh-cn-YunxiNeural", "zh-cn", false, "使用 SSML 提供多种语音风格"));
        actors.add(new TtsActor("晓涵", "zh-cn-XiaohanNeural", "zh-cn", true, "使用 SSML 提供多种语音风格"));
        actors.add(new TtsActor("晓墨", "zh-cn-XiaomoNeural", "zh-cn", true, "使用 SSML 提供多种语音风格"));
        actors.add(new TtsActor("晓睿", "zh-cn-XiaoruiNeural", "zh-cn", true, "使用 SSML 提供多种语音风格"));
        actors.add(new TtsActor("晓萱", "zh-cn-XiaoxuanNeural", "zh-cn", true, "使用 SSML 提供多种语音风格"));

        actors.add(new TtsActor("HiuGaai", "zh-HK-HiuGaaiNeural", "zh-HK", true, "粤语女声"));
        actors.add(new TtsActor("HiuMaan", "zh-HK-HiuMaanNeural", "zh-HK", true, "粤语女声"));
        actors.add(new TtsActor("WanLung", "zh-HK-WanLungNeural", "zh-HK", false, "粤语男声"));
        actors.add(new TtsActor("HsiaoChen", "zh-TW-HsiaoChenNeural", "zh-TW", true, "湾湾女声"));
        actors.add(new TtsActor("HsiaoYu", "zh-TW-HsiaoYuNeural", "zh-TW", true, "湾湾女声"));
        actors.add(new TtsActor("YunJhe", "zh-TW-YunJheNeural", "zh-TW", false, "湾湾男声"));

    }


    public List<TtsActor> getActors() {
        return this.actors;
    }


}

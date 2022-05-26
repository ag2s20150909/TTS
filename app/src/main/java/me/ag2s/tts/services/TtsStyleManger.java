package me.ag2s.tts.services;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class TtsStyleManger {
    //单例
    private static volatile TtsStyleManger instance;

    public static TtsStyleManger getInstance() {
        if (instance == null) {
            synchronized (TtsStyleManger.class) {
                if (instance == null) {
                    instance = new TtsStyleManger();
                }
            }

        }
        return instance;
    }

    private final List<TtsStyle> styles;

    //https://docs.microsoft.com/zh-cn/azure/cognitive-services/speech-service/speech-synthesis-markup?tabs=csharp#adjust-speaking-styles

    private TtsStyleManger() {
        styles = new ArrayList<>(22);
        styles.add(new TtsStyle("默认", "Default", "默认值"));
        styles.add(new TtsStyle("新闻", "newscast", "以正式专业的语气叙述新闻"));
        styles.add(new TtsStyle("客服", "customerservice", "以友好热情的语气为客户提供支持"));
        styles.add(new TtsStyle("助理", "assistant", "以热情而轻松的语气对数字助理讲话"));
        styles.add(new TtsStyle("聊天", "chat", "以轻松、随意的语气闲聊"));
        styles.add(new TtsStyle("冷静", "calm", "以沉着冷静的态度说话。 语气、音调、韵律与其他语音类型相比要统一得多。"));//Calm
        styles.add(new TtsStyle("开心", "cheerful", "以较高的音调和音量表达欢快、热情的语气"));//Cheerful Happy
        styles.add(new TtsStyle("悲伤", "sad", "以较高的音调、较低的强度和较低的音量表达悲伤的语气。 这种情绪的常见特征是说话时呜咽或哭泣。"));//Sad
        styles.add(new TtsStyle("恼怒", "angry", "以较低的音调、较高的强度和较高的音量来表达恼怒的语气。 说话者处于愤怒、生气和被冒犯的状态。")); //Angry
        styles.add(new TtsStyle("恐惧", "fearful", "以较高的音调、较高的音量和较快的语速来表达恐惧、紧张的语气。 说话者处于紧张和不安的状态。"));//Fearful,Nervous
        styles.add(new TtsStyle("抱怨", "disgruntled", "表达轻蔑和抱怨的语气。 这种情绪的语音表现出不悦和蔑视。"));// Disgruntled Complaining
        styles.add(new TtsStyle("严肃", "serious", "表达严肃和命令的语气。 说话者的声音通常比较僵硬，节奏也不那么轻松。"));//Serious Strict
        styles.add(new TtsStyle("撒娇", "affectionate", "以较高的音调和音量表达温暖而亲切的语气。 说话者处于吸引听众注意力的状态。 说话者的“个性”往往是讨人喜欢的。"));//Affectionate Cutesy
        styles.add(new TtsStyle("温柔", "gentle", "以较低的音调和音量表达温和、礼貌和愉快的语气"));//Gentle
        styles.add(new TtsStyle("抒情", "lyrical", "以优美又带感伤的方式表达情感"));
        styles.add(new TtsStyle("尴尬", "embarrassed", "在说话者感到不舒适时表达不确定、犹豫的语气"));//{\"Name\":\"Embarrassed\",\"LegacyNames\":[\"Shy\"]}
        styles.add(new TtsStyle("同理心", "empathetic", "表达出一种关怀和理解的感觉。"));
        styles.add(new TtsStyle("沮丧", "depressed", "调低音调和音量来表达忧郁、沮丧的语气"));//Depressed

        styles.add(new TtsStyle("新闻(休闲）", "newscast-casual", "以通用和随意的语气叙述新闻。"));
        styles.add(new TtsStyle("新闻(正式）", "newscast-formal", "以正式、自信、权威的语气叙述新闻。"));
        styles.add(new TtsStyle("旁白-专业", "narration-professional", "对内容阅读表达专业、客观的语气。（云扬，Aria）"));
        styles.add(new TtsStyle("轻松阅读", "narration-relaxed", "适合阅读轻松的文章(云希，云健)"));

        styles.add(new TtsStyle("体育解说", "Sports_commentary", "体育解说(云健)"));
        styles.add(new TtsStyle("体育解说(兴奋)", "Sports_commentary_excited", "兴奋语气的体育解说(云健)"));
        styles.add(new TtsStyle("乐观的广告", "Advertisement_upbeat", "(云皓)Advertisement_upbeat"));


        Log.e("Style", styles.size() + "");


    }

    public @NonNull
    List<TtsStyle> getStyles() {
        return this.styles;
    }

    @SuppressWarnings("unused")
    public @NonNull
    TtsStyle get(int index) {
        if (index >= 0 && index < styles.size()) {
            return styles.get(index);
        }
        return new TtsStyle("默认", "", "默认值");
    }

}

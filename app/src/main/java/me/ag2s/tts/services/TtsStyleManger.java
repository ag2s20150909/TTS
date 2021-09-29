package me.ag2s.tts.services;

import java.util.ArrayList;
import java.util.List;

public class TtsStyleManger {
    //单例
    private static TtsStyleManger instance;

    public static TtsStyleManger getInstance() {
        if (instance == null) {
            instance = new TtsStyleManger();
        }
        return instance;
    }
    private final List<TtsStyle> styles;
    private TtsStyleManger(){
        styles=new ArrayList<>();
        styles.add(new TtsStyle("默认","Default","默认值"));
        styles.add(new TtsStyle("新闻","newscast","以正式专业的语气叙述新闻"));
        styles.add(new TtsStyle("客服","customerservice","以友好热情的语气为客户提供支持"));
        styles.add(new TtsStyle("助手","assistant","以热情而轻松的语气对数字助理讲话"));
        styles.add(new TtsStyle("闲聊","chat","以轻松、随意的语气闲聊"));
        styles.add(new TtsStyle("冷静","calm","以沉着冷静的态度说话。 语气、音调、韵律与其他语音类型相比要统一得多。"));
        styles.add(new TtsStyle("欢快","cheerful","以较高的音调和音量表达欢快、热情的语气"));
        styles.add(new TtsStyle("悲伤","sad","以较高的音调、较低的强度和较低的音量表达悲伤的语气。 这种情绪的常见特征是说话时呜咽或哭泣。"));
        styles.add(new TtsStyle("恼怒","angry","以较低的音调、较高的强度和较高的音量来表达恼怒的语气。 说话者处于愤怒、生气和被冒犯的状态。"));
        styles.add(new TtsStyle("恐惧","fearful","以较高的音调、较高的音量和较快的语速来表达恐惧、紧张的语气。 说话者处于紧张和不安的状态。"));
        styles.add(new TtsStyle("蔑视","disgruntled","表达轻蔑和抱怨的语气。 这种情绪的语音表现出不悦和蔑视。"));
        styles.add(new TtsStyle("严肃","serious","表达严肃和命令的语气。 说话者的声音通常比较僵硬，节奏也不那么轻松。"));
        styles.add(new TtsStyle("亲切","affectionate","以较高的音调和音量表达温暖而亲切的语气。 说话者处于吸引听众注意力的状态。 说话者的“个性”往往是讨人喜欢的。"));
        styles.add(new TtsStyle("温和","gentle","以较低的音调和音量表达温和、礼貌和愉快的语气"));
        styles.add(new TtsStyle("优美","lyrical","以优美又带感伤的方式表达情感"));
        styles.add(new TtsStyle("测试","narration","以较高的音调和音量表达欢快、热情的语气"));

    }

    public List<TtsStyle> getStyles(){
        return this.styles;
    }

    public TtsStyle get(int index){
        if(index<styles.size()){
            return styles.get(index);
        }
        return new TtsStyle("默认","","默认值");
    }

}

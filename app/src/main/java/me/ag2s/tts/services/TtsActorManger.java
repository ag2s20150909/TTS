package me.ag2s.tts.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TtsActorManger {
    //单例

    private static TtsActorManger instance;

    public static TtsActorManger getInstance() {
        if (instance == null) {
            instance = new TtsActorManger();
        }
        return instance;
    }

    private final List<TtsActor> actors;

    private TtsActorManger() {
        actors = new ArrayList<>();
        //中文
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

        //阿拉伯语（埃及）
        actors.add(new TtsActor("ar-EG-SalmaNeural", true, "常规"));
        actors.add(new TtsActor("ar-EG-ShakirNeural", false, "常规"));
        //阿拉伯语（沙特阿拉伯）
        actors.add(new TtsActor("ar-SA-ZariyahNeural", true, "常规"));
        actors.add(new TtsActor("ar-SA-HamedNeural", false, "常规"));
        //保加利亚语
        actors.add(new TtsActor("bg-BG-KalinaNeural", true, "常规"));
        actors.add(new TtsActor("bg-BG-BorislavNeural", false, "常规"));
        //加泰罗尼亚语(西班牙)
        actors.add(new TtsActor("ca-ES-AlbaNeural", true, "常规"));
        actors.add(new TtsActor("ca-ES-JoanaNeural", true, "常规"));
        actors.add(new TtsActor("ca-ES-EnricNeural", false, "常规"));
        //克罗地亚语（克罗地亚)
        actors.add(new TtsActor("hr-HR-GabrijelaNeural", true, "常规"));
        actors.add(new TtsActor("hr-HR-SreckoNeural", false, "常规"));
        //捷克语（捷克)
        actors.add(new TtsActor("cs-CZ-VlastaNeural", true, "常规"));
        actors.add(new TtsActor("cs-CZ-AntoninNeural", false, "常规"));
        //丹麦语（丹麦)
        actors.add(new TtsActor("da-DK-ChristelNeural", true, "常规"));
        actors.add(new TtsActor("da-DK-JeppeNeural", false, "常规"));
        //荷兰语（比利时）
        actors.add(new TtsActor("nl-BE-DenaNeural", true, "常规"));
        actors.add(new TtsActor("nl-BE-ArnaudNeural", false, "常规"));
        //荷兰语（荷兰）
        actors.add(new TtsActor("nl-NL-ColetteNeural", true, "常规"));
        actors.add(new TtsActor("nl-NL-FennaNeural", true, "常规"));
        actors.add(new TtsActor("nl-NL-MaartenNeural", false, "常规"));
        //英语（澳大利亚）
        actors.add(new TtsActor("en-AU-NatashaNeural", true, "常规"));
        actors.add(new TtsActor("en-AU-WilliamNeural", false, "常规"));
        //英语（加拿大）
        actors.add(new TtsActor("en-CA-ClaraNeural", true, "常规"));
        actors.add(new TtsActor("en-CA-LiamNeural", false, "常规"));
        //英语（印度）
        actors.add(new TtsActor("en-IN-NeerjaNeural", true, "常规"));
        actors.add(new TtsActor("en-IN-PrabhatNeural", false, "常规"));
        //英语（爱尔兰）
        actors.add(new TtsActor("en-IE-EmilyNeural", true, "常规"));
        actors.add(new TtsActor("en-IE-EmilyNeural", false, "常规"));
        //英语（菲律宾）
        actors.add(new TtsActor("en-PH-RosaNeural", true, "常规"));
        actors.add(new TtsActor("en-PH-JamesNeural", false, "常规"));
        //英语（英国）	en-GB	女	en-GB-LibbyNeural	常规
        //英语（英国）	en-GB	女	en-GB-MiaNeural	常规
        //英语（英国）	en-GB	男	en-GB-RyanNeural	常规
        actors.add(new TtsActor("en-GB-LibbyNeural", true, "常规"));
        actors.add(new TtsActor("en-GB-MiaNeural", true, "常规"));
        actors.add(new TtsActor("en-GB-RyanNeural", false, "常规"));

        //英语（美国）	en-US	女	en-US-AriaNeural	常规，使用 SSML 提供多种语音风格
        //英语（美国）	en-US	女	en-US-JennyNeural	常规
        //英语（美国）	en-US	男	en-US-GuyNeural	常规
        actors.add(new TtsActor("en-US-AriaNeural", true, "常规，使用 SSML 提供多种语音风格"));
        actors.add(new TtsActor("en-US-JennyNeural", true, "常规"));
        actors.add(new TtsActor("en-US-GuyNeural", false, "常规"));
        //爱沙尼亚语(爱沙尼亚)	et-EE	女	et-EE-AnuNeural	常规
        //爱沙尼亚语(爱沙尼亚)	et-EE	男	et-EE-KertNeural	常规
        actors.add(new TtsActor("et-EE-AnuNeural", true, "常规"));
        actors.add(new TtsActor("et-EE-AnuNeural", false, "常规"));
        //芬兰语（芬兰）	fi-FI	女	fi-FI-NooraNeural	常规
        //芬兰语（芬兰）	fi-FI	女	fi-FI-SelmaNeural	常规
        //芬兰语（芬兰）	fi-FI	男	fi-FI-HarriNeural	常规
        actors.add(new TtsActor("fi-FI-NooraNeural", true, "常规"));
        actors.add(new TtsActor("fi-FI-SelmaNeural", true, "常规"));
        actors.add(new TtsActor("fi-FI-HarriNeural", false, "常规"));
        //法语（比利时）	fr-BE	Female	fr-BE-CharlineNeural 新建	常规
        //法语（比利时）	fr-BE	男	fr-BE-GerardNeural 新建	常规
        actors.add(new TtsActor("fr-BE-CharlineNeural", true, "常规"));
        actors.add(new TtsActor("fr-BE-GerardNeural", false, "常规"));
        //法语（加拿大）	fr-CA	Female	fr-CA-SylvieNeural	常规
        //法语（加拿大）	fr-CA	男	fr-CA-AntoineNeural	常规
        //法语（加拿大）	fr-CA	男	fr-CA-JeanNeural	常规
        actors.add(new TtsActor("fr-CA-SylvieNeural", true, "常规"));
        actors.add(new TtsActor("fr-CA-AntoineNeural", false, "常规"));
        actors.add(new TtsActor("fr-CA-JeanNeural", false, "常规"));
        //法语（法国）	fr-FR	Female	fr-FR-DeniseNeural	常规
        //法语（法国）	fr-FR	男	fr-FR-HenriNeural	常规
        actors.add(new TtsActor("fr-FR-DeniseNeural", true, "常规"));
        actors.add(new TtsActor("fr-FR-HenriNeural", false, "常规"));
        //法语（瑞士）	fr-CH	女	fr-CH-ArianeNeural	常规
        //法语（瑞士）	fr-CH	男	fr-CH-FabriceNeural	常规
        actors.add(new TtsActor("fr-CH-ArianeNeural", true, "常规"));
        actors.add(new TtsActor("fr-CH-FabriceNeural", false, "常规"));
        //德语（奥地利）	de-AT	女	de-AT-IngridNeural	常规
        //德语（奥地利）	de-AT	男	de-AT-JonasNeural	常规
        actors.add(new TtsActor("de-AT-IngridNeural", true, "常规"));
        actors.add(new TtsActor("de-AT-JonasNeural", false, "常规"));
        //德语（德国）	de-DE	Female	de-DE-KatjaNeural	常规
        //德语（德国）	de-DE	男	de-DE-ConradNeural	常规
        actors.add(new TtsActor("de-DE-KatjaNeural", true, "常规"));
        actors.add(new TtsActor("de-DE-ConradNeural", false, "常规"));
        //德语（瑞士）	de-CH	女	de-CH-LeniNeural	常规
        //德语（瑞士）	de-CH	男	de-CH-JanNeural	常规
        actors.add(new TtsActor("de-CH-LeniNeural", true, "常规"));
        actors.add(new TtsActor("de-DE-ConradNeural", false, "常规"));
        //希腊语(希腊)	el-GR	女	el-GR-AthinaNeural	常规
        //希腊语(希腊)	el-GR	男	el-GR-NestorasNeural	常规
        actors.add(new TtsActor("el-GR-AthinaNeural", true, "常规"));
        actors.add(new TtsActor("el-GR-NestorasNeural", false, "常规"));
        //希伯来语（以色列）	he-IL	女	he-IL-HilaNeural	常规
        //希伯来语（以色列）	he-IL	男	he-IL-AvriNeural	常规
        actors.add(new TtsActor("he-IL-HilaNeural", true, "常规"));
        actors.add(new TtsActor("he-IL-AvriNeural", false, "常规"));
        //印地语（印度）	hi-IN	Female	hi-IN-SwaraNeural	常规
        //印地语（印度）	hi-IN	男	hi-IN-MadhurNeural	常规
        actors.add(new TtsActor("hi-IN-SwaraNeural", true, "常规"));
        actors.add(new TtsActor("hi-IN-MadhurNeural", false, "常规"));
        //匈牙利语(匈牙利)	hu-HU	女	hu-HU-NoemiNeural	常规
        //匈牙利语(匈牙利)	hu-HU	男	hu-HU-TamasNeural	常规
        actors.add(new TtsActor("hu-HU-NoemiNeural", true, "常规"));
        actors.add(new TtsActor("hu-HU-TamasNeural", false, "常规"));
        //印度尼西亚语(印度尼西亚)	id-ID	Female	id-ID-GadisNeural	常规
        //印度尼西亚语(印度尼西亚)	id-ID	男	id-ID-ArdiNeural	常规
        actors.add(new TtsActor("id-ID-GadisNeural", true, "常规"));
        actors.add(new TtsActor("id-ID-ArdiNeural", false, "常规"));
        //爱尔兰语（爱尔兰）	ga-IE	女	ga-IE-OrlaNeural	常规
        //爱尔兰语（爱尔兰）	ga-IE	男	ga-IE-ColmNeural	常规
        actors.add(new TtsActor("ga-IE-OrlaNeural", true, "常规"));
        actors.add(new TtsActor("ga-IE-ColmNeural", false, "常规"));
        //意大利语（意大利）	it-IT	Female	it-IT-ElsaNeural	常规
        //意大利语（意大利）	it-IT	Female	it-IT-IsabellaNeural	常规
        //意大利语（意大利）	it-IT	男	it-IT-DiegoNeural	常规
        actors.add(new TtsActor("it-IT-ElsaNeural", true, "常规"));
        actors.add(new TtsActor("it-IT-IsabellaNeural", true, "常规"));
        actors.add(new TtsActor("it-IT-DiegoNeural", false, "常规"));
        //日语（日本）	ja-JP	女	ja-JP-NanamiNeural	常规
        //日语（日本）	ja-JP	男	ja-JP-KeitaNeural	常规
        actors.add(new TtsActor("ja-JP-NanamiNeural", true, "常规"));
        actors.add(new TtsActor("ja-JP-KeitaNeural", false, "常规"));
        //韩语(韩国)	ko-KR	Female	ko-KR-SunHiNeural	常规
        //韩语(韩国)	ko-KR	男	ko-KR-InJoonNeural	常规
        actors.add(new TtsActor("ko-KR-SunHiNeural", true, "常规"));
        actors.add(new TtsActor("ko-KR-InJoonNeural", false, "常规"));
        //拉脱维亚语(拉脱维亚)	lv-LV	女	lv-LV-EveritaNeural	常规
        //拉脱维亚语(拉脱维亚)	lv-LV	男	lv-LV-NilsNeural	常规
        actors.add(new TtsActor("lv-LV-EveritaNeural", true, "常规"));
        actors.add(new TtsActor("lv-LV-NilsNeural", false, "常规"));
        //立陶宛语(立陶宛)	lt-LT	女	lt-LT-OnaNeural	常规
        //立陶宛语(立陶宛)	lt-LT	男	lt-LT-LeonasNeural	常规
        actors.add(new TtsActor("lt-LT-OnaNeural", true, "常规"));
        actors.add(new TtsActor("lt-LT-LeonasNeural", false, "常规"));
        //马来语（马来西亚）	ms-MY	女	ms-MY-YasminNeural	常规
        //马来语（马来西亚）	ms-MY	男	ms-MY-OsmanNeural	常规
        actors.add(new TtsActor("ms-MY-YasminNeural", true, "常规"));
        actors.add(new TtsActor("ms-MY-OsmanNeural", false, "常规"));
        //马耳他语（马耳他）	mt-MT	女	mt-MT-GraceNeural	常规
        //马耳他语（马耳他）	mt-MT	男	mt-MT-JosephNeural	常规
        actors.add(new TtsActor("mt-MT-GraceNeural", true, "常规"));
        actors.add(new TtsActor("mt-MT-JosephNeural", false, "常规"));
        //挪威语（博克马尔语，挪威）	nb-NO	女	nb-NO-IselinNeural	常规
        //挪威语（博克马尔语，挪威）	nb-NO	女	nb-NO-PernilleNeural	常规
        //挪威语（博克马尔语，挪威）	nb-NO	男	nb-NO-FinnNeural	常规
        actors.add(new TtsActor("nb-NO-IselinNeural", true, "常规"));
        actors.add(new TtsActor("nb-NO-PernilleNeural", false, "常规"));
        actors.add(new TtsActor("nb-NO-FinnNeural", false, "常规"));
        //波兰语（波兰）	pl-PL	女	pl-PL-AgnieszkaNeural	常规
        //波兰语（波兰）	pl-PL	女	pl-PL-ZofiaNeural	常规
        //波兰语（波兰）	pl-PL	男	pl-PL-MarekNeural	常规
        actors.add(new TtsActor("nb-NO-IselinNeural", true, "常规"));
        actors.add(new TtsActor("nb-NO-PernilleNeural", false, "常规"));
        actors.add(new TtsActor("nb-NO-FinnNeural", false, "常规"));
        //葡萄牙语（巴西）	pt-BR	女	pt-BR-FranciscaNeural	常规，使用 SSML 提供多种语音风格
        //葡萄牙语(巴西)	pt-BR	男	pt-BR-AntonioNeural	常规
        actors.add(new TtsActor("pt-BR-FranciscaNeural", false, "常规"));
        actors.add(new TtsActor("pt-BR-AntonioNeural", false, "常规"));
        //葡萄牙语(葡萄牙)	pt-PT	女	pt-PT-FernandaNeural	常规
        //葡萄牙语(葡萄牙)	pt-PT	女	pt-PT-RaquelNeural	常规
        //葡萄牙语(葡萄牙)	pt-PT	男	pt-PT-DuarteNeural	常规
        actors.add(new TtsActor("pt-PT-FernandaNeural", true, "常规"));
        actors.add(new TtsActor("pt-PT-RaquelNeural", false, "常规"));
        actors.add(new TtsActor("pt-PT-DuarteNeural", false, "常规"));
        //罗马尼亚语(罗马尼亚)	ro-RO	女	ro-RO-AlinaNeural	常规
        //罗马尼亚语(罗马尼亚)	ro-RO	男	ro-RO-EmilNeural	常规
        actors.add(new TtsActor("ro-RO-AlinaNeural", false, "常规"));
        actors.add(new TtsActor("ro-RO-EmilNeural", false, "常规"));
        //俄语（俄罗斯）	ru-RU	Female	ru-RU-DariyaNeural	常规
        //俄语（俄罗斯）	ru-RU	Female	ru-RU-SvetlanaNeural	常规
        //俄语（俄罗斯）	ru-RU	男	ru-RU-DmitryNeural	常规
        actors.add(new TtsActor("ru-RU-DariyaNeural", true, "常规"));
        actors.add(new TtsActor("ru-RU-SvetlanaNeural", false, "常规"));
        actors.add(new TtsActor("ru-RU-DmitryNeural", false, "常规"));
        //斯洛伐克语(斯洛伐克)	sk-SK	女	sk-SK-ViktoriaNeural	常规
        //斯洛伐克语(斯洛伐克)	sk-SK	男	sk-SK-LukasNeural	常规
        actors.add(new TtsActor("sk-SK-ViktoriaNeural", false, "常规"));
        actors.add(new TtsActor("sk-SK-LukasNeural", false, "常规"));
        //斯洛文尼亚语(斯洛文尼亚)	sl-SI	女	sl-SI-PetraNeural	常规
        //斯洛文尼亚语(斯洛文尼亚)	sl-SI	男	sl-SI-RokNeural	常规
        actors.add(new TtsActor("sl-SI-PetraNeural", false, "常规"));
        actors.add(new TtsActor("sl-SI-RokNeural", false, "常规"));
        //西班牙语（墨西哥）	es-MX	Female	es-MX-DaliaNeural	常规
        //西班牙语（墨西哥）	es-MX	男	es-MX-JorgeNeural	常规
        actors.add(new TtsActor("es-MX-DaliaNeural", false, "常规"));
        actors.add(new TtsActor("es-MX-JorgeNeural", false, "常规"));
        //西班牙语(西班牙)	es-ES	女	es-ES-ElviraNeural	常规
        //西班牙语(西班牙)	es-ES	男	es-ES-AlvaroNeural	常规
        actors.add(new TtsActor("es-MX-DaliaNeural", false, "常规"));
        actors.add(new TtsActor("es-MX-JorgeNeural", false, "常规"));
        //瑞典语（瑞典）	sv-SE	女	sv-SE-HilleviNeural	常规
        //瑞典语（瑞典）	sv-SE	女	sv-SE-SofieNeural	常规
        //瑞典语（瑞典）	sv-SE	男	sv-SE-MattiasNeural	常规
        actors.add(new TtsActor("sv-SE-HilleviNeural", true, "常规"));
        actors.add(new TtsActor("sv-SE-SofieNeural", false, "常规"));
        actors.add(new TtsActor("sv-SE-MattiasNeural", false, "常规"));
        //泰米尔语（印度）	ta-IN	女	ta-IN-PallaviNeural	常规
        //泰米尔语（印度）	ta-IN	男	ta-IN-ValluvarNeural	常规
        actors.add(new TtsActor("ta-IN-PallaviNeural", false, "常规"));
        actors.add(new TtsActor("ta-IN-ValluvarNeural", false, "常规"));
        //泰卢固语（印度）	te-IN	女	te-IN-ShrutiNeural	常规
        //泰卢固语（印度）	te-IN	男	te-IN-MohanNeural	常规
        actors.add(new TtsActor("te-IN-ShrutiNeural", false, "常规"));
        actors.add(new TtsActor("te-IN-MohanNeural", false, "常规"));
        //泰语（泰国）	th-TH	女	th-TH-AcharaNeural	常规
        //泰语（泰国）	th-TH	女	th-TH-PremwadeeNeural	常规
        //泰语（泰国）	th-TH	男	th-TH-NiwatNeural	常规
        actors.add(new TtsActor("th-TH-AcharaNeural", true, "常规"));
        actors.add(new TtsActor("th-TH-PremwadeeNeural", false, "常规"));
        actors.add(new TtsActor("th-TH-NiwatNeural", false, "常规"));
        //土耳其语（土耳其）	tr-TR	女	tr-TR-EmelNeural	常规
        //土耳其语（土耳其）	tr-TR	男	tr-TR-AhmetNeural	常规
        actors.add(new TtsActor("tr-TR-EmelNeural", false, "常规"));
        actors.add(new TtsActor("tr-TR-AhmetNeural", false, "常规"));
        //乌克兰语(乌克兰)	uk-UA	Female	uk-UA-PolinaNeural 新建	常规
        //乌克兰语(乌克兰)	uk-UA	男	uk-UA-OstapNeural 新建	常规
        actors.add(new TtsActor("uk-UA-PolinaNeural", false, "常规"));
        actors.add(new TtsActor("uk-UA-OstapNeural", false, "常规"));
        //乌尔都语（巴基斯坦）	ur-PK	Female	ur-PK-UzmaNeural 新建	常规
        //乌尔都语（巴基斯坦）	ur-PK	男	ur-PK-AsadNeural 新建	常规
        actors.add(new TtsActor("ur-PK-UzmaNeural", false, "常规"));
        actors.add(new TtsActor("ur-PK-AsadNeural", false, "常规"));
        //越南语(越南)	vi-VN	女	vi-VN-HoaiMyNeural	常规
        //越南语(越南)	vi-VN	男	vi-VN-NamMinhNeural	常规
        actors.add(new TtsActor("vi-VN-HoaiMyNeural", false, "常规"));
        actors.add(new TtsActor("vi-VN-NamMinhNeural", false, "常规"));
        //威尔士语（英国）	cy-GB	Female	cy-GB-NiaNeural 新建	常规
        //威尔士语（英国）	cy-GB	男	cy-GB-AledNeural 新建	常规
        actors.add(new TtsActor("cy-GB-NiaNeural", false, "常规"));
        actors.add(new TtsActor("cy-GB-AledNeural", false, "常规"));


    }

    public List<TtsActor> sortByLocale(List<TtsActor> list, Locale locale) {
        Collections.sort(list, (o1, o2) -> {
            Locale loc1 = o1.getLocale();
            Locale loc2 = o2.getLocale();
            boolean b11 = loc1.getISO3Language().equals(locale.getISO3Language());
            boolean b12 = loc1.getISO3Country().equals(locale.getISO3Country());
            boolean b13 = loc1.getDisplayVariant(Locale.US).equals(locale.getDisplayVariant(Locale.US));
            boolean b21 = loc2.getISO3Language().equals(locale.getISO3Language());
            boolean b22 = loc2.getISO3Country().equals(locale.getISO3Country());
            boolean b23 = loc2.getDisplayVariant(Locale.US).equals(locale.getDisplayVariant(Locale.US));
            //语言不同
            if ((!b11)&&(!b21)) {
                return 0;
            }
            //两个都相同
            if (b11 && b12 && b13 == b21 && b22 && b23) {
                return 0;
            }
            if (b11 && b12 && b13) {
                return -1;
            }
            if (b21 && b22 && b23) {
                return 1;
            }

            if ((b11 && b12 == b21 && b22)) {
                if (b13 == b23) {
                    return 0;
                }
                if (b13) {
                    return -1;
                } else {
                    return 1;
                }
            }
            if (b11 && b12) {
                return -1;
            }
            if (b21 && b22) {
                return 1;
            }
            return 0;
        });
        return list;
    }


    @SuppressWarnings("unused")
    public List<TtsActor> getActors() {
        //return sortByLocale(this.actors, Locale.getDefault());
        return this.actors;
    }

    @SuppressWarnings("unused")
    public List<TtsActor> getActorsByLocale(Locale locale) {
        List<TtsActor> newActors = new ArrayList<>();
        for (TtsActor actor : actors) {
            //语言相同或者地区相同
            if (actor.getLocale().getISO3Language().equals(locale.getISO3Language()) || actor.getLocale().getISO3Country().equals(locale.getISO3Country())) {
                newActors.add(actor);
            }
        }
        sortByLocale(newActors, locale);
        return newActors;
    }

//    public List<Locale> getAllSupportIOS() {
//


}

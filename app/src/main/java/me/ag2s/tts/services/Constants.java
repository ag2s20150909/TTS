package me.ag2s.tts.services;

public final class Constants {
    /**
     * 是否使用自定义语音
     */
    public static final String USE_CUSTOM_VOICE = "use_custom_voice";
    /**
     * 自定义语音的名称
     */
    public static final String CUSTOM_VOICE = "custom_voice";
    /**
     * 自定义语音的index
     */
    public static final String CUSTOM_VOICE_INDEX = "custom_voice_index";

    /**
     * 语音的风格的强度
     */
    public static final String VOICE_STYLE_DEGREE = "voice_style_degree";
    /**
     * 语音的风格index
     */
    public static final String VOICE_STYLE_INDEX = "voice_style_index";

    /**
     * 是否使用词典
     */
    public static final String USE_DICT = "use_dict";
    /**
     * 是否对文本进行分段
     */
    public static final String SPLIT_SENTENCE = "use_split_sentence";
    /**
     * 是否检查更新
     */
    public static final String USE_AUTO_UPDATE = "use_auto_update";
    /**
     * 格式index
     */
    public static final String AUDIO_FORMAT_INDEX = "audio_format_index";
    /**
     * 音量
     */
    public static final String VOICE_VOLUME = "voice_volume";

    /**
     * 使用预览语音
     */
    public static final String USE_PREVIEW = "use_preview";


    public final static String[] supportedLanguages = {"zho-CHN", "zho-HKG", "zho-TWN", "jpn-JPN", "kor-KOR", "ara-EGY", "ara-SAU", "bul-BGR", "cat-ESP", "ces-CZE", "cym-GBR", "dan-DNK", "deu-AUT", "deu-CHE", "deu-DEU", "ell-GRC", "eng-AUS", "eng-CAN", "eng-GBR", "eng-HKG", "eng-IRL", "eng-IND", "eng-NZL", "eng-PHL", "eng-SGP", "eng-USA", "eng-ZAF", "spa-ARG", "spa-COL", "spa-ESP", "spa-MEX", "spa-USA", "est-EST", "fin-FIN", "fra-BEL", "fra-CAN", "fra-CHE", "fra-FRA", "gle-IRL", "guj-IND", "heb-ISR", "hin-IND", "hrv-HRV", "hun-HUN", "ind-IDN", "ita-ITA", "lit-LTU", "lav-LVA", "mar-IND", "msa-MYS", "mlt-MLT", "nob-NOR", "nld-BEL", "nld-NLD", "pol-POL", "por-BRA", "por-PRT", "ron-ROU", "rus-RUS", "slk-SVK", "slv-SVN", "swe-SWE", "swa-KEN", "tam-IND", "tel-IND", "tha-THA", "tur-TUR", "ukr-UKR", "urd-PAK", "vie-VNM"};
    public static final String[] supportVoiceNames = {"zh-CN-XiaochenNeural", "zh-CN-XiaohanNeural", "zh-CN-XiaomoNeural", "zh-CN-XiaoqiuNeural", "zh-CN-XiaoruiNeural", "zh-CN-XiaoshuangNeural", "zh-CN-XiaoxiaoNeural", "zh-CN-XiaoxuanNeural", "zh-CN-XiaoyanNeural", "zh-CN-XiaoyouNeural", "zh-CN-YunxiNeural", "zh-CN-YunyangNeural", "zh-CN-YunyeNeural", "zh-HK-HiuGaaiNeural", "zh-HK-HiuMaanNeural", "zh-HK-WanLungNeural", "zh-TW-HsiaoChenNeural", "zh-TW-HsiaoYuNeural", "zh-TW-YunJheNeural", "ja-JP-KeitaNeural", "ja-JP-NanamiNeural", "ko-KR-InJoonNeural", "ko-KR-SunHiNeural", "ar-EG-SalmaNeural", "ar-EG-ShakirNeural", "ar-SA-HamedNeural", "ar-SA-ZariyahNeural", "bg-BG-BorislavNeural", "bg-BG-KalinaNeural", "ca-ES-AlbaNeural", "ca-ES-EnricNeural", "ca-ES-JoanaNeural", "cs-CZ-AntoninNeural", "cs-CZ-VlastaNeural", "cy-GB-AledNeural", "cy-GB-NiaNeural", "da-DK-ChristelNeural", "da-DK-JeppeNeural", "de-AT-IngridNeural", "de-AT-JonasNeural", "de-CH-JanNeural", "de-CH-LeniNeural", "de-DE-ConradNeural", "de-DE-KatjaNeural", "el-GR-AthinaNeural", "el-GR-NestorasNeural", "en-AU-NatashaNeural", "en-AU-WilliamNeural", "en-CA-ClaraNeural", "en-CA-LiamNeural", "en-GB-LibbyNeural", "en-GB-MiaNeural", "en-GB-RyanNeural", "en-GB-SoniaNeural", "en-HK-SamNeural", "en-HK-YanNeural", "en-IE-ConnorNeural", "en-IE-EmilyNeural", "en-IN-NeerjaNeural", "en-IN-PrabhatNeural", "en-NZ-MitchellNeural", "en-NZ-MollyNeural", "en-PH-JamesNeural", "en-PH-RosaNeural", "en-SG-LunaNeural", "en-SG-WayneNeural", "en-US-AmberNeural", "en-US-AnaNeural", "en-US-AriaNeural", "en-US-AshleyNeural", "en-US-BrandonNeural", "en-US-ChristopherNeural", "en-US-CoraNeural", "en-US-ElizabethNeural", "en-US-EricNeural", "en-US-GuyNeural", "en-US-JacobNeural", "en-US-JennyMultilingualNeural", "en-US-JennyNeural", "en-US-MichelleNeural", "en-US-MonicaNeural", "en-US-SaraNeural", "en-ZA-LeahNeural", "en-ZA-LukeNeural", "es-AR-ElenaNeural", "es-AR-TomasNeural", "es-CO-GonzaloNeural", "es-CO-SalomeNeural", "es-ES-AlvaroNeural", "es-ES-ElviraNeural", "es-MX-DaliaNeural", "es-MX-JorgeNeural", "es-US-AlonsoNeural", "es-US-PalomaNeural", "et-EE-AnuNeural", "et-EE-KertNeural", "fi-FI-HarriNeural", "fi-FI-NooraNeural", "fi-FI-SelmaNeural", "fr-BE-CharlineNeural", "fr-BE-GerardNeural", "fr-CA-AntoineNeural", "fr-CA-JeanNeural", "fr-CA-SylvieNeural", "fr-CH-ArianeNeural", "fr-CH-FabriceNeural", "fr-FR-DeniseNeural", "fr-FR-HenriNeural", "ga-IE-ColmNeural", "ga-IE-OrlaNeural", "gu-IN-DhwaniNeural", "gu-IN-NiranjanNeural", "he-IL-AvriNeural", "he-IL-HilaNeural", "hi-IN-MadhurNeural", "hi-IN-SwaraNeural", "hr-HR-GabrijelaNeural", "hr-HR-SreckoNeural", "hu-HU-NoemiNeural", "hu-HU-TamasNeural", "id-ID-ArdiNeural", "id-ID-GadisNeural", "it-IT-DiegoNeural", "it-IT-ElsaNeural", "it-IT-IsabellaNeural", "lt-LT-LeonasNeural", "lt-LT-OnaNeural", "lv-LV-EveritaNeural", "lv-LV-NilsNeural", "mr-IN-AarohiNeural", "mr-IN-ManoharNeural", "ms-MY-OsmanNeural", "ms-MY-YasminNeural", "mt-MT-GraceNeural", "mt-MT-JosephNeural", "nb-NO-FinnNeural", "nb-NO-IselinNeural", "nb-NO-PernilleNeural", "nl-BE-ArnaudNeural", "nl-BE-DenaNeural", "nl-NL-ColetteNeural", "nl-NL-FennaNeural", "nl-NL-MaartenNeural", "pl-PL-AgnieszkaNeural", "pl-PL-MarekNeural", "pl-PL-ZofiaNeural", "pt-BR-AntonioNeural", "pt-BR-FranciscaNeural", "pt-PT-DuarteNeural", "pt-PT-FernandaNeural", "pt-PT-RaquelNeural", "ro-RO-AlinaNeural", "ro-RO-EmilNeural", "ru-RU-DariyaNeural", "ru-RU-DmitryNeural", "ru-RU-SvetlanaNeural", "sk-SK-LukasNeural", "sk-SK-ViktoriaNeural", "sl-SI-PetraNeural", "sl-SI-RokNeural", "sv-SE-HilleviNeural", "sv-SE-MattiasNeural", "sv-SE-SofieNeural", "sw-KE-RafikiNeural", "sw-KE-ZuriNeural", "ta-IN-PallaviNeural", "ta-IN-ValluvarNeural", "te-IN-MohanNeural", "te-IN-ShrutiNeural", "th-TH-AcharaNeural", "th-TH-NiwatNeural", "th-TH-PremwadeeNeural", "tr-TR-AhmetNeural", "tr-TR-EmelNeural", "uk-UA-OstapNeural", "uk-UA-PolinaNeural", "ur-PK-AsadNeural", "ur-PK-UzmaNeural", "vi-VN-HoaiMyNeural", "vi-VN-NamMinhNeural"};

    //public static final String[] SUPPORT_LOC={"zh-CN","zh-HK","zh-TW","ja-JP","ko-KR","ar-EG","ar-SA","bg-BG","ca-ES","cs-CZ","cy-GB","da-DK","de-AT","de-CH","de-DE","el-GR","en-AU","en-CA","en-GB","en-HK","en-IE","en-IN","en-NZ","en-PH","en-SG","en-US","en-ZA","es-AR","es-CO","es-ES","es-MX","es-US","et-EE","fi-FI","fr-BE","fr-CA","fr-CH","fr-FR","ga-IE","gu-IN","he-IL","hi-IN","hr-HR","hu-HU","id-ID","it-IT","lt-LT","lv-LV","mr-IN","ms-MY","mt-MT","nb-NO","nl-BE","nl-NL","pl-PL","pt-BR","pt-PT","ro-RO","ru-RU","sk-SK","sl-SI","sv-SE","sw-KE","ta-IN","te-IN","th-TH","tr-TR","uk-UA","ur-PK","vi-VN"};


    public static final String EDGE_ORIGIN = "chrome-extension://jdiccldimpdaibmpdkjnbmckianbfold";
    public static final String EDGE_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.5060.114 Safari/537.36 Edg/103.0.1264.62";
    public static final String EDGE_URL = "https://speech.platform.bing.com/consumer/speech/synthesize/readaloud/edge/v1?TrustedClientToken=6A5AA1D4EAFF4E9FB37E23D68491D6F4";

}

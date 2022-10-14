//package me.ag2s.tts.services;
//
//import android.util.Log;
//
//import java.util.Random;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//import me.ag2s.tts.APP;
//import me.ag2s.tts.utils.HttpTool;
//
//public class TokenHolder {
//    private static final String KEY = "tts_ms_token";
//    private static final String TAG = "@@";
//    private static final int timeout = 300000;
//
//    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//
//    private static volatile boolean started = false;
//
//    public static volatile String token = getToken();
//
//
//    private static String getToken() {
//        String e = APP.getString(KEY, null);
//        if (e != null) {
//            try {
//                long timeout = Long.parseLong(e.substring(0, e.indexOf(TAG)));
//                long now = System.currentTimeMillis();
//                if (timeout - now > 1000) {
//                    return e.substring(e.indexOf(TAG) + TAG.length());
//                }
//            } catch (Exception err) {
//                return null;
//            }
//
//
//        }
//        return null;
//    }
//
//    private static String encodeToString(String content) {
//        return (System.currentTimeMillis() + TokenHolder.timeout) + TAG + content;
//    }
//
//
//    public static void startToken() {
//        if (started) {
//            return;
//        }
//        started = true;
//        //token 有效时间5分钟，每4分50秒更新一次Token
//        scheduler.scheduleWithFixedDelay(((
//                        () -> {
//
//                            try {
//
//                                //HttpTool.httpGet("https://cn.bing.com/");
//                                String url = getUrl();// "https://azure.microsoft.com/zh-cn/services/cognitive-services/text-to-speech/";
//                                Log.e("Token", url);
//                                String s = HttpTool.httpGet(url);
//                                if (s.startsWith(HttpTool.HTTP_ERROR)) {
//                                    return;
//                                }
//
//                                s = s.substring(s.indexOf("token:") + 8);
//                                s = s.substring(0, s.indexOf("\""));
//                                if (s.length() > 10) {
//                                    token = s;
//                                }
//                                APP.putString(KEY, encodeToString(token));
//                                Log.e("Token", s);
//
//
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        })),
//                0, 290,
//                TimeUnit.SECONDS);
//    }
//
//
//    public static void stop() {
//        scheduler.shutdownNow();
//        started = false;
//    }
//
//
//    private static final Random r = new Random(System.currentTimeMillis());
//
//    /**
//     * 随机生成url
//     *
//     * @return url
//     */
//    private static String getUrl() {
//        return "https://azure.microsoft.com/" + areas[r.nextInt(areas.length)] + "/services/cognitive-services/text-to-speech/";
//
//    }
//
//    private static final String[] areas = {"en-us", "cs-cz", "da-dk", "de-de", "en-au", "en-ca", "en-in", "en-gb", "es-es", "es-mx", "fr-fr", "fr-ca", "id-id", "it-it", "hu-hu", "nb-no", "nl-nl", "pl-pl", "pt-br", "pt-pt", "sv-se", "tr-tr", "ru-ru", "ja-jp", "ko-kr", "zh-cn", "zh-tw"};
//
//
//}

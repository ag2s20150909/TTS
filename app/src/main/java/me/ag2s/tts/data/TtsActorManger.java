package me.ag2s.tts.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;

import me.ag2s.tts.APP;
import me.ag2s.tts.utils.CommonTool;
import me.ag2s.tts.utils.HttpTool;


public class TtsActorManger {
    private static final String TAG = "TtsActorManger";

    private final File cacheFile;
    //单例

    private static final String previewNote = "预览版语音,如果卡住了，杀掉应用重进！！！";

    private static volatile TtsActorManger instance;

    public static TtsActorManger getInstance() {
        if (instance == null) {
            synchronized (TtsActorManger.class) {
                if (instance == null) {
                    instance = new TtsActorManger();
                }
            }

        }
        return instance;
    }


    private final List<TtsActor> actors;

    private TtsActorManger() {
        //actors = new ArrayList<>();


        cacheFile = new File(APP.getContext().getExternalCacheDir(), "tts_actors.json");
        if (cacheFile.exists()) {

            actors = loadFromCache();
        } else {
            actors = loadFromNet();
        }


        //Collections.sort(actors, TtsActorComparator.INSTANCE);
        //actors.add(new TtsActor("Microsoft Server Speech Text to Speech Voice (ar-EG, Hoda)", "ar-EG-Hoda", true, "ar-EG"));
    }

    private synchronized List<TtsActor> loadFromString(String s) {
        ArrayList<TtsActor> copy = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(s);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jo = jsonArray.getJSONObject(i);
                String name = jo.getString("Name");
                String shortName = jo.getString("ShortName");
                String Gender = jo.getString("Gender");
                String locale = jo.getString("Locale");
                String note = jo.getJSONObject("VoiceTag").toString();

                if ( locale.toLowerCase().contains("latn") || locale.toLowerCase().contains("cans") ) {
                    // 因纽特语，各2个，共4个
                    continue;
                }

                copy.add(new TtsActor(
                        /*name*/ name,
                        /*shortName*/ shortName,
                        /*gender*/ Gender.equalsIgnoreCase("Female"),
                        /*locale*/ locale,
                        /*note*/note));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Collections.sort(copy, TtsActorComparator.INSTANCE);
        return copy;
    }


    private synchronized List<TtsActor> loadFromCache() {
        String s = CommonTool.readText(cacheFile);
        return loadFromString(s);
    }

    private synchronized List<TtsActor> loadFromNet() {
        try {

            Future<String> submit = HttpTool.executorService.submit(() -> HttpTool.httpGet("https://speech.platform.bing.com/consumer/speech/synthesize/readaloud/voices/list?trustedclienttoken=6A5AA1D4EAFF4E9FB37E23D68491D6F4"));
            String s = submit.get();
            CommonTool.writeText(cacheFile, s);
            return loadFromString(s);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<TtsActor> sortByLocale(List<TtsActor> list, Locale locale) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Collections.sort(list, (o1, o2) -> {
//                Locale loc1 = o1.getLocale();
//                Locale loc2 = o2.getLocale();
//                boolean b11 = loc1.getISO3Language().equals(locale.getISO3Language());
//                boolean b12 = loc1.getISO3Country().equals(locale.getISO3Country());
//                boolean b13 = loc1.getDisplayVariant(Locale.US).equals(locale.getDisplayVariant(Locale.US));
//                boolean b21 = loc2.getISO3Language().equals(locale.getISO3Language());
//                boolean b22 = loc2.getISO3Country().equals(locale.getISO3Country());
//                boolean b23 = loc2.getDisplayVariant(Locale.US).equals(locale.getDisplayVariant(Locale.US));
//                //语言都不同
//                if ((!b11) && (!b21)) {
//                    return 0;
//                }
//                //两个都相同
//                if (b11 && b12 && b13 == b21 && b22 && b23) {
//                    return 0;
//                }
//                if (b11 && b12 && b13) {
//                    return -1;
//                }
//                if (b21 && b22 && b23) {
//                    return 1;
//                }
//
//                if ((b11 && b12 == b21 && b22)) {
//                    if (b13 == b23) {
//                        return 0;
//                    }
//                    if (b13) {
//                        return -1;
//                    } else {
//                        return 1;
//                    }
//                }
//                if (b11 && b12) {
//                    return -1;
//                }
//                if (b21 && b22) {
//                    return 1;
//                }
//                return 0;
//            });
//        }
        return list;
    }

    @Nullable
    public TtsActor getByName(@NonNull String name) {
        for (TtsActor actor : actors) {
            if (actor.getShortName().equalsIgnoreCase(name) || actor.getName().equalsIgnoreCase(name)) {
                return actor;
            }
        }

        return null;
    }

    /**
     * 获取所有Actor
     *
     * @return List<TtsActor>
     */
    @SuppressWarnings("unused")
    public synchronized List<TtsActor> getActors() {
        Collections.sort(actors, TtsActorComparator.INSTANCE);
        //return sortByLocale(this.actors, Locale.getDefault());
        return this.actors;
    }

    /**
     * 获取当前Locale支持的Actor
     *
     * @param locale locale
     * @return List<TtsActor>
     */
    @SuppressWarnings("unused")
    public List<TtsActor> getActorsByLocale(Locale locale) {
        List<TtsActor> newActors = new ArrayList<>();
        for (TtsActor actor : actors) {
            //语言相同或者地区相同
            try {
                if (actor.getLocale().getISO3Language().equals(locale.getISO3Language()) || actor.getLocale().getISO3Country().equals(locale.getISO3Country())) {
                    newActors.add(actor);
                }
            } catch (Exception e) {
                System.out.println("==================== 匹配失败 " + e.getMessage());
            }

        }
        //sortByLocale(newActors, locale);
        Collections.sort(newActors, new TtsActorComparator(locale));
        return newActors;
    }

//    public List<Locale> getAllSupportIOS() {
//


}

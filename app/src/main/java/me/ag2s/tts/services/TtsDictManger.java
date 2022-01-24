package me.ag2s.tts.services;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import me.ag2s.tts.APP;

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

    private TtsDictManger() {

        dict = new ArrayList<>(20);
        readDicFile();
    }

    /**
     * 更新语音校正词典
     */
    public void updateDict() {
        readDicFile();
        APP.showToast("更新语音校正词典成功");

    }

    private void readDicFile() {
        this.dict.clear();

        File file = new File(APP.getContext().getExternalFilesDir(""), "dict.txt");
        if (!file.exists()) {
            try {
                boolean is = Objects.requireNonNull(file.getParentFile()).mkdirs();
                if (!is) {
                    Log.e("DICT", "创建文件夹：" + file.getParentFile().getAbsolutePath() + "出错");
                }
                FileWriter fw = new FileWriter(file);
                fw.write("佛然=bo 2 ran 2\n朱重八=zhu 1 chong 2 ba 1");
                fw.flush();
                fw.close();
            } catch (Exception e) {
                Log.e("DICT", "read", e);
                e.printStackTrace();
            }

        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            for (String line; (line = br.readLine()) != null; ) {
                line = line.trim();
                int length = line.length();
                int index = line.indexOf("=");
                if (length > 3 && index > 0 && index < length && !line.startsWith("#")) {
                    String key = line.substring(0, index).trim();
                    String value = line.substring(index + 1, length).trim();
                    if (key.length() > 0 && value.length() > 0) {
                        add(key, value);
                        Log.e("DICT", "key:" + key + "value:" + value);
                    }

                }
            }

        } catch (Exception e) {
            Log.e("DICT", "read", e);
            e.printStackTrace();
        }
        Collections.sort(this.dict);
    }

    public void add(TtsDict dic) {
        dict.add(dic);
    }

    public void add(String wd, String ph) {
        dict.add(new TtsDict(wd, ph));

    }

    public List<TtsDict> getDict() {
        return dict;
    }
}

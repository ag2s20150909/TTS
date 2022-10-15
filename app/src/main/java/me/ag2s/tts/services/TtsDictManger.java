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
    private final File file;
    private static final String ReStartLabel = "r:\"^";//正则表达式开始标记
    private static final String ReEndLabel = "$\"=";//正则表达式结束标记
    private static final String NoteLabel = "#";//注释标记

    //单例
    private static volatile TtsDictManger instance;

    public static TtsDictManger getInstance() {
        if (instance == null) {
            synchronized (TtsStyleManger.class) {
                if (instance == null) {
                    instance = new TtsDictManger();
                }
            }

        }
        return instance;
    }

    private final List<TtsDict> dict;

    private TtsDictManger() {
        file = new File(APP.getContext().getExternalFilesDir(""), "dict.txt");
        dict = new ArrayList<>(20);
        readDicFile();
    }

    /**
     * 更新语音校正词典
     */
    public void updateDict() {
        readDicFile();
        APP.showToast("更新语音校正词典成功.路径:\n" + file.getAbsolutePath());

    }

    private void readDicFile() {
        this.dict.clear();


        if (!file.exists()) {
            try {
                boolean is = Objects.requireNonNull(file.getParentFile()).mkdirs();
                if (!is) {
                    Log.e("DICT", "创建文件夹：" + file.getParentFile().getAbsolutePath() + "出错");
                }
                FileWriter fw = new FileWriter(file);
                fw.write("#用#开头的是注释\n\n" +
                        "#这是普通替换规则:key=ph\n" +
                        "朱重八=zhu 1 chong 2 ba 1\n\n" +
                        "#这是正则替换规则:r:\"^regex$\"=replacement\n" +
                        "#r:\"^重(?=[一二三四五六七八九十])|(?<=[一二三四五六七八九十])重$\"=<phoneme alphabet='sapi' ph='chong 2'>重</phoneme>");
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
                //判断是否为注释
                boolean isNote = line.startsWith(NoteLabel);
                if (isNote) {
                    continue;
                }
                int regexIndex = line.indexOf(ReEndLabel);
                int regexStartIndex = line.indexOf(ReStartLabel);
                int length = line.length();
                int index = line.indexOf("=");
                Log.e("SS", regexStartIndex + "SS");
                if (regexStartIndex != -1 && regexStartIndex < regexIndex && regexIndex < length) {
                    String regex = line.substring(regexStartIndex + ReStartLabel.length(), regexIndex);
                    String value = line.substring(regexIndex + ReEndLabel.length());
                    addRegex(regex, value);
                    Log.e("DICT", "regex:" + regex + "value:" + value);
                } else if (length > 3 && index > 0 && index < length) {
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

    public void addRegex(String wd, String ph) {
        dict.add(new TtsDict(wd, ph, true));
    }

    public void add(String wd, String ph) {
        dict.add(new TtsDict(wd, ph));
    }

    public List<TtsDict> getDict() {
        return dict;
    }
}

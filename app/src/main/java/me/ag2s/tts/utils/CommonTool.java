package me.ag2s.tts.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class CommonTool {


    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();

        try (PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        }
    }


    // 移除字符串首尾空字符的高效方法(利用ASCII值判断,包括全角空格)
    public static String FixTrim(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        int start = 0;
        int len = s.length();
        int end = len - 1;
        while (start < end && (s.charAt(start) <= 0x20 || s.charAt(start) == '　')) {
            ++start;
        }
        while (start < end && (s.charAt(end) <= 0x20 || s.charAt(end) == '　')) {
            --end;
        }
        if (end < len) {
            ++end;
        }
        return (start > 0 || end < len) ? s.substring(start, end) : s;

    }


    public static String localeToEmoji(Locale locale) {
        String countryCode = locale.getCountry();
        int firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6;
        int secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6;
        return new String(Character.toChars(firstLetter)) + new String(Character.toChars(secondLetter));
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 对传递过来的字符串进行md5加密
     *
     * @param str 待加密的字符串
     * @return 字符串Md5加密后的结果
     */
    public static String getMD5String(String str) {
        StringBuilder sb = new StringBuilder();//字符串容器
        try {
            //获取md5加密器.public static MessageDigest getInstance(String algorithm)返回实现指定摘要算法的 MessageDigest 对象。
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);//把要加密的字符串转换成字节数组
            byte[] digest = md.digest(bytes);//使用指定的 【byte 数组】对摘要进行最后更新，然后完成摘要计算。即完成md5的加密

            for (byte b : digest) {
                //把每个字节转换成16进制数
                int d = b & 0xff;//只保留后两位数
                String herString = Integer.toHexString(d);//把int类型数据转为16进制字符串表示
                //如果只有一位，则在前面补0.让其也是两位
                if (herString.length() == 1) {//字节高4位为0
                    herString = "0" + herString;//拼接字符串，拼成两位表示
                }
                sb.append(herString);
            }
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return sb.toString();
    }
}

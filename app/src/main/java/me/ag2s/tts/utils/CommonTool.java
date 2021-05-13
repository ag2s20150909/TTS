package me.ag2s.tts.utils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;

public class CommonTool {
    private static final HashMap<Character, String> htmlEncodeChars = new HashMap<>();

    static {

        // Special characters for HTML
        htmlEncodeChars.put('\u0026', "&amp;");
        htmlEncodeChars.put('\u003C', "&lt;");
        htmlEncodeChars.put('\u003E', "&gt;");
        htmlEncodeChars.put('\u0022', "&quot;");

        htmlEncodeChars.put('\u0152', "&OElig;");
        htmlEncodeChars.put('\u0153', "&oelig;");
        htmlEncodeChars.put('\u0160', "&Scaron;");
        htmlEncodeChars.put('\u0161', "&scaron;");
        htmlEncodeChars.put('\u0178', "&Yuml;");
        htmlEncodeChars.put('\u02C6', "&circ;");
        htmlEncodeChars.put('\u02DC', "&tilde;");
        htmlEncodeChars.put('\u2002', "&ensp;");
        htmlEncodeChars.put('\u2003', "&emsp;");
        htmlEncodeChars.put('\u2009', "&thinsp;");
        htmlEncodeChars.put('\u200C', "&zwnj;");
        htmlEncodeChars.put('\u200D', "&zwj;");
        htmlEncodeChars.put('\u200E', "&lrm;");
        htmlEncodeChars.put('\u200F', "&rlm;");
        htmlEncodeChars.put('\u2013', "&ndash;");
        htmlEncodeChars.put('\u2014', "&mdash;");
        htmlEncodeChars.put('\u2018', "&lsquo;");
        htmlEncodeChars.put('\u2019', "&rsquo;");
        htmlEncodeChars.put('\u201A', "&sbquo;");
        htmlEncodeChars.put('\u201C', "&ldquo;");
        htmlEncodeChars.put('\u201D', "&rdquo;");
        htmlEncodeChars.put('\u201E', "&bdquo;");
        htmlEncodeChars.put('\u2020', "&dagger;");
        htmlEncodeChars.put('\u2021', "&Dagger;");
        htmlEncodeChars.put('\u2030', "&permil;");
        htmlEncodeChars.put('\u2039', "&lsaquo;");
        htmlEncodeChars.put('\u203A', "&rsaquo;");
        htmlEncodeChars.put('\u20AC', "&euro;");

        // Character entity references for ISO 8859-1 characters
        htmlEncodeChars.put('\u00A0', "&nbsp;");
        htmlEncodeChars.put('\u00A1', "&iexcl;");
        htmlEncodeChars.put('\u00A2', "&cent;");
        htmlEncodeChars.put('\u00A3', "&pound;");
        htmlEncodeChars.put('\u00A4', "&curren;");
        htmlEncodeChars.put('\u00A5', "&yen;");
        htmlEncodeChars.put('\u00A6', "&brvbar;");
        htmlEncodeChars.put('\u00A7', "&sect;");
        htmlEncodeChars.put('\u00A8', "&uml;");
        htmlEncodeChars.put('\u00A9', "&copy;");
        htmlEncodeChars.put('\u00AA', "&ordf;");
        htmlEncodeChars.put('\u00AB', "&laquo;");
        htmlEncodeChars.put('\u00AC', "&not;");
        htmlEncodeChars.put('\u00AD', "&shy;");
        htmlEncodeChars.put('\u00AE', "&reg;");
        htmlEncodeChars.put('\u00AF', "&macr;");
        htmlEncodeChars.put('\u00B0', "&deg;");
        htmlEncodeChars.put('\u00B1', "&plusmn;");
        htmlEncodeChars.put('\u00B2', "&sup2;");
        htmlEncodeChars.put('\u00B3', "&sup3;");
        htmlEncodeChars.put('\u00B4', "&acute;");
        htmlEncodeChars.put('\u00B5', "&micro;");
        htmlEncodeChars.put('\u00B6', "&para;");
        htmlEncodeChars.put('\u00B7', "&middot;");
        htmlEncodeChars.put('\u00B8', "&cedil;");
        htmlEncodeChars.put('\u00B9', "&sup1;");
        htmlEncodeChars.put('\u00BA', "&ordm;");
        htmlEncodeChars.put('\u00BB', "&raquo;");
        htmlEncodeChars.put('\u00BC', "&frac14;");
        htmlEncodeChars.put('\u00BD', "&frac12;");
        htmlEncodeChars.put('\u00BE', "&frac34;");
        htmlEncodeChars.put('\u00BF', "&iquest;");
        htmlEncodeChars.put('\u00C0', "&Agrave;");
        htmlEncodeChars.put('\u00C1', "&Aacute;");
        htmlEncodeChars.put('\u00C2', "&Acirc;");
        htmlEncodeChars.put('\u00C3', "&Atilde;");
        htmlEncodeChars.put('\u00C4', "&Auml;");
        htmlEncodeChars.put('\u00C5', "&Aring;");
        htmlEncodeChars.put('\u00C6', "&AElig;");
        htmlEncodeChars.put('\u00C7', "&Ccedil;");
        htmlEncodeChars.put('\u00C8', "&Egrave;");
        htmlEncodeChars.put('\u00C9', "&Eacute;");
        htmlEncodeChars.put('\u00CA', "&Ecirc;");
        htmlEncodeChars.put('\u00CB', "&Euml;");
        htmlEncodeChars.put('\u00CC', "&Igrave;");
        htmlEncodeChars.put('\u00CD', "&Iacute;");
        htmlEncodeChars.put('\u00CE', "&Icirc;");
        htmlEncodeChars.put('\u00CF', "&Iuml;");
        htmlEncodeChars.put('\u00D0', "&ETH;");
        htmlEncodeChars.put('\u00D1', "&Ntilde;");
        htmlEncodeChars.put('\u00D2', "&Ograve;");
        htmlEncodeChars.put('\u00D3', "&Oacute;");
        htmlEncodeChars.put('\u00D4', "&Ocirc;");
        htmlEncodeChars.put('\u00D5', "&Otilde;");
        htmlEncodeChars.put('\u00D6', "&Ouml;");
        htmlEncodeChars.put('\u00D7', "&times;");
        htmlEncodeChars.put('\u00D8', "&Oslash;");
        htmlEncodeChars.put('\u00D9', "&Ugrave;");
        htmlEncodeChars.put('\u00DA', "&Uacute;");
        htmlEncodeChars.put('\u00DB', "&Ucirc;");
        htmlEncodeChars.put('\u00DC', "&Uuml;");
        htmlEncodeChars.put('\u00DD', "&Yacute;");
        htmlEncodeChars.put('\u00DE', "&THORN;");
        htmlEncodeChars.put('\u00DF', "&szlig;");
        htmlEncodeChars.put('\u00E0', "&agrave;");
        htmlEncodeChars.put('\u00E1', "&aacute;");
        htmlEncodeChars.put('\u00E2', "&acirc;");
        htmlEncodeChars.put('\u00E3', "&atilde;");
        htmlEncodeChars.put('\u00E4', "&auml;");
        htmlEncodeChars.put('\u00E5', "&aring;");
        htmlEncodeChars.put('\u00E6', "&aelig;");
        htmlEncodeChars.put('\u00E7', "&ccedil;");
        htmlEncodeChars.put('\u00E8', "&egrave;");
        htmlEncodeChars.put('\u00E9', "&eacute;");
        htmlEncodeChars.put('\u00EA', "&ecirc;");
        htmlEncodeChars.put('\u00EB', "&euml;");
        htmlEncodeChars.put('\u00EC', "&igrave;");
        htmlEncodeChars.put('\u00ED', "&iacute;");
        htmlEncodeChars.put('\u00EE', "&icirc;");
        htmlEncodeChars.put('\u00EF', "&iuml;");
        htmlEncodeChars.put('\u00F0', "&eth;");
        htmlEncodeChars.put('\u00F1', "&ntilde;");
        htmlEncodeChars.put('\u00F2', "&ograve;");
        htmlEncodeChars.put('\u00F3', "&oacute;");
        htmlEncodeChars.put('\u00F4', "&ocirc;");
        htmlEncodeChars.put('\u00F5', "&otilde;");
        htmlEncodeChars.put('\u00F6', "&ouml;");
        htmlEncodeChars.put('\u00F7', "&divide;");
        htmlEncodeChars.put('\u00F8', "&oslash;");
        htmlEncodeChars.put('\u00F9', "&ugrave;");
        htmlEncodeChars.put('\u00FA', "&uacute;");
        htmlEncodeChars.put('\u00FB', "&ucirc;");
        htmlEncodeChars.put('\u00FC', "&uuml;");
        htmlEncodeChars.put('\u00FD', "&yacute;");
        htmlEncodeChars.put('\u00FE', "&thorn;");
        htmlEncodeChars.put('\u00FF', "&yuml;");

        // Mathematical, Greek and Symbolic characters for HTML
        htmlEncodeChars.put('\u0192', "&fnof;");
        htmlEncodeChars.put('\u0391', "&Alpha;");
        htmlEncodeChars.put('\u0392', "&Beta;");
        htmlEncodeChars.put('\u0393', "&Gamma;");
        htmlEncodeChars.put('\u0394', "&Delta;");
        htmlEncodeChars.put('\u0395', "&Epsilon;");
        htmlEncodeChars.put('\u0396', "&Zeta;");
        htmlEncodeChars.put('\u0397', "&Eta;");
        htmlEncodeChars.put('\u0398', "&Theta;");
        htmlEncodeChars.put('\u0399', "&Iota;");
        htmlEncodeChars.put('\u039A', "&Kappa;");
        htmlEncodeChars.put('\u039B', "&Lambda;");
        htmlEncodeChars.put('\u039C', "&Mu;");
        htmlEncodeChars.put('\u039D', "&Nu;");
        htmlEncodeChars.put('\u039E', "&Xi;");
        htmlEncodeChars.put('\u039F', "&Omicron;");
        htmlEncodeChars.put('\u03A0', "&Pi;");
        htmlEncodeChars.put('\u03A1', "&Rho;");
        htmlEncodeChars.put('\u03A3', "&Sigma;");
        htmlEncodeChars.put('\u03A4', "&Tau;");
        htmlEncodeChars.put('\u03A5', "&Upsilon;");
        htmlEncodeChars.put('\u03A6', "&Phi;");
        htmlEncodeChars.put('\u03A7', "&Chi;");
        htmlEncodeChars.put('\u03A8', "&Psi;");
        htmlEncodeChars.put('\u03A9', "&Omega;");
        htmlEncodeChars.put('\u03B1', "&alpha;");
        htmlEncodeChars.put('\u03B2', "&beta;");
        htmlEncodeChars.put('\u03B3', "&gamma;");
        htmlEncodeChars.put('\u03B4', "&delta;");
        htmlEncodeChars.put('\u03B5', "&epsilon;");
        htmlEncodeChars.put('\u03B6', "&zeta;");
        htmlEncodeChars.put('\u03B7', "&eta;");
        htmlEncodeChars.put('\u03B8', "&theta;");
        htmlEncodeChars.put('\u03B9', "&iota;");
        htmlEncodeChars.put('\u03BA', "&kappa;");
        htmlEncodeChars.put('\u03BB', "&lambda;");
        htmlEncodeChars.put('\u03BC', "&mu;");
        htmlEncodeChars.put('\u03BD', "&nu;");
        htmlEncodeChars.put('\u03BE', "&xi;");
        htmlEncodeChars.put('\u03BF', "&omicron;");
        htmlEncodeChars.put('\u03C0', "&pi;");
        htmlEncodeChars.put('\u03C1', "&rho;");
        htmlEncodeChars.put('\u03C2', "&sigmaf;");
        htmlEncodeChars.put('\u03C3', "&sigma;");
        htmlEncodeChars.put('\u03C4', "&tau;");
        htmlEncodeChars.put('\u03C5', "&upsilon;");
        htmlEncodeChars.put('\u03C6', "&phi;");
        htmlEncodeChars.put('\u03C7', "&chi;");
        htmlEncodeChars.put('\u03C8', "&psi;");
        htmlEncodeChars.put('\u03C9', "&omega;");
        htmlEncodeChars.put('\u03D1', "&thetasym;");
        htmlEncodeChars.put('\u03D2', "&upsih;");
        htmlEncodeChars.put('\u03D6', "&piv;");
        htmlEncodeChars.put('\u2022', "&bull;");
        htmlEncodeChars.put('\u2026', "&hellip;");
        htmlEncodeChars.put('\u2032', "&prime;");
        htmlEncodeChars.put('\u2033', "&Prime;");
        htmlEncodeChars.put('\u203E', "&oline;");
        htmlEncodeChars.put('\u2044', "&frasl;");
        htmlEncodeChars.put('\u2118', "&weierp;");
        htmlEncodeChars.put('\u2111', "&image;");
        htmlEncodeChars.put('\u211C', "&real;");
        htmlEncodeChars.put('\u2122', "&trade;");
        htmlEncodeChars.put('\u2135', "&alefsym;");
        htmlEncodeChars.put('\u2190', "&larr;");
        htmlEncodeChars.put('\u2191', "&uarr;");
        htmlEncodeChars.put('\u2192', "&rarr;");
        htmlEncodeChars.put('\u2193', "&darr;");
        htmlEncodeChars.put('\u2194', "&harr;");
        htmlEncodeChars.put('\u21B5', "&crarr;");
        htmlEncodeChars.put('\u21D0', "&lArr;");
        htmlEncodeChars.put('\u21D1', "&uArr;");
        htmlEncodeChars.put('\u21D2', "&rArr;");
        htmlEncodeChars.put('\u21D3', "&dArr;");
        htmlEncodeChars.put('\u21D4', "&hArr;");
        htmlEncodeChars.put('\u2200', "&forall;");
        htmlEncodeChars.put('\u2202', "&part;");
        htmlEncodeChars.put('\u2203', "&exist;");
        htmlEncodeChars.put('\u2205', "&empty;");
        htmlEncodeChars.put('\u2207', "&nabla;");
        htmlEncodeChars.put('\u2208', "&isin;");
        htmlEncodeChars.put('\u2209', "&notin;");
        htmlEncodeChars.put('\u220B', "&ni;");
        htmlEncodeChars.put('\u220F', "&prod;");
        htmlEncodeChars.put('\u2211', "&sum;");
        htmlEncodeChars.put('\u2212', "&minus;");
        htmlEncodeChars.put('\u2217', "&lowast;");
        htmlEncodeChars.put('\u221A', "&radic;");
        htmlEncodeChars.put('\u221D', "&prop;");
        htmlEncodeChars.put('\u221E', "&infin;");
        htmlEncodeChars.put('\u2220', "&ang;");
        htmlEncodeChars.put('\u2227', "&and;");
        htmlEncodeChars.put('\u2228', "&or;");
        htmlEncodeChars.put('\u2229', "&cap;");
        htmlEncodeChars.put('\u222A', "&cup;");
        htmlEncodeChars.put('\u222B', "&int;");
        htmlEncodeChars.put('\u2234', "&there4;");
        htmlEncodeChars.put('\u223C', "&sim;");
        htmlEncodeChars.put('\u2245', "&cong;");
        htmlEncodeChars.put('\u2248', "&asymp;");
        htmlEncodeChars.put('\u2260', "&ne;");
        htmlEncodeChars.put('\u2261', "&equiv;");
        htmlEncodeChars.put('\u2264', "&le;");
        htmlEncodeChars.put('\u2265', "&ge;");
        htmlEncodeChars.put('\u2282', "&sub;");
        htmlEncodeChars.put('\u2283', "&sup;");
        htmlEncodeChars.put('\u2284', "&nsub;");
        htmlEncodeChars.put('\u2286', "&sube;");
        htmlEncodeChars.put('\u2287', "&supe;");
        htmlEncodeChars.put('\u2295', "&oplus;");
        htmlEncodeChars.put('\u2297', "&otimes;");
        htmlEncodeChars.put('\u22A5', "&perp;");
        htmlEncodeChars.put('\u22C5', "&sdot;");
        htmlEncodeChars.put('\u2308', "&lceil;");
        htmlEncodeChars.put('\u2309', "&rceil;");
        htmlEncodeChars.put('\u230A', "&lfloor;");
        htmlEncodeChars.put('\u230B', "&rfloor;");
        htmlEncodeChars.put('\u2329', "&lang;");
        htmlEncodeChars.put('\u232A', "&rang;");
        htmlEncodeChars.put('\u25CA', "&loz;");
        htmlEncodeChars.put('\u2660', "&spades;");
        htmlEncodeChars.put('\u2663', "&clubs;");
        htmlEncodeChars.put('\u2665', "&hearts;");
        htmlEncodeChars.put('\u2666', "&diams;");
    }

    public static String encodeHtml(String source) {
        return encode(source, htmlEncodeChars);
    }


    private static String encode(String source, HashMap<Character, String> encodingTable) {
        if (null == source) {
            return null;
        }

        if (null == encodingTable) {
            return source;
        }

        StringBuffer encoded_string = null;
        char[] string_to_encode_array = source.toCharArray();
        int last_match = -1;
        int difference = 0;

        for (int i = 0; i < string_to_encode_array.length; i++) {
            char char_to_encode = string_to_encode_array[i];

            if (encodingTable.containsKey(char_to_encode)) {
                if (null == encoded_string) {
                    encoded_string = new StringBuffer(source.length());
                }
                difference = i - (last_match + 1);
                if (difference > 0) {
                    encoded_string.append(string_to_encode_array, last_match + 1, difference);
                }
                encoded_string.append(encodingTable.get(char_to_encode));
                last_match = i;
            }
        }

        if (null == encoded_string) {
            return source;
        } else {
            difference = string_to_encode_array.length - (last_match + 1);
            if (difference > 0) {
                encoded_string.append(string_to_encode_array, last_match + 1, difference);
            }
            return encoded_string.toString();
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

package me.ag2s.tts.utils;



import java.io.EOFException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okio.Buffer;
import okio.ByteString;
import okio.Utf8;

/**
 * Trivial Dns Encoder/Decoder, basically ripped from Netty full implementation.
 */
class DnsRecordCodec {
    private static final byte SERVFAIL = 2;
    private static final byte NXDOMAIN = 3;
    public static final int TYPE_A = 0x0001;
    public static final int TYPE_AAAA = 0x001c;
    private static final int TYPE_PTR = 0x000c;
    private static final Charset ASCII = StandardCharsets.US_ASCII;

    private DnsRecordCodec() {
    }

    public static ByteString encodeQuery(String host, int type) {
        Buffer buf = new Buffer();

        buf.writeShort(0); //query id
        buf.writeShort(256); // flags with recursion
        buf.writeShort(1); // question count
        buf.writeShort(0); // answerCount
        buf.writeShort(0); // authorityResourceCount
        buf.writeShort(0); // additional
        Buffer nameBuf = new Buffer();
        final String[] labels = host.split("\\.");
        for (String label : labels) {
            long utf8ByteCount = Utf8.size(label);
            if (utf8ByteCount != label.length()) {
                throw new IllegalArgumentException("non-ascii hostname: " + host);
            }
            nameBuf.writeByte((byte) utf8ByteCount);
            nameBuf.writeUtf8(label);
        }
        nameBuf.writeByte(0); // end

        nameBuf.copyTo(buf, 0, nameBuf.size());
        buf.writeShort(type);
        buf.writeShort(1); // CLASS_IN

        return buf.readByteString();
    }

    public static String decodeQuery(ByteString byteString) throws EOFException {
        StringBuilder query= new StringBuilder();
        Buffer buf = new Buffer();
        buf.write(byteString);
        buf.readShort();//query id
        int flags = buf.readShort() & 0xffff;
        buf.readShort();// question count
        buf.readShort();//answerCount
        buf.readShort(); // authorityResourceCount
        buf.readShort(); // additional
        int len;
        String[] ss;
        while ((len=(int)buf.readByte())!=0){
            query.append(buf.readUtf8(len)).append(".");
        }
        query.deleteCharAt(query.length()-1);
        buf.readShort(); // type
        buf.readShort(); // CLASS_IN





        return query.toString();
    }

    public static List<InetAddress> decodeAnswers(String hostname, ByteString byteString)
            throws Exception {
        List<InetAddress> result = new ArrayList<>();

        Buffer buf = new Buffer();
        buf.write(byteString);
        buf.readShort(); // query id

        final int flags = buf.readShort() & 0xffff;
        if (flags >> 15 == 0) {
            throw new IllegalArgumentException("not a response");
        }

        byte responseCode = (byte) (flags & 0xf);

        if (responseCode == NXDOMAIN) {
            throw new UnknownHostException(hostname + ": NXDOMAIN");
        } else if (responseCode == SERVFAIL) {
            throw new UnknownHostException(hostname + ": SERVFAIL");
        }

        final int questionCount = buf.readShort() & 0xffff;
        final int answerCount = buf.readShort() & 0xffff;
        buf.readShort(); // authority record count
        buf.readShort(); // additional record count

        for (int i = 0; i < questionCount; i++) {
            skipName(buf); // name
            buf.readShort(); // type
            buf.readShort(); // class
        }

        for (int i = 0; i < answerCount; i++) {
            skipName(buf); // name

            int type = buf.readShort() & 0xffff;
            buf.readShort(); // class
            final long ttl = buf.readInt() & 0xffffffffL; // ttl
            final int length = buf.readShort() & 0xffff;

            if (type == TYPE_A || type == TYPE_AAAA) {
                byte[] bytes = new byte[length];
                buf.read(bytes);
                result.add(InetAddress.getByAddress(bytes));
            } else {
                buf.skip(length);
            }
        }

        return result;
    }

    private static void skipName(Buffer in) throws EOFException {
        // 0 - 63 bytes
        int length = in.readByte();

        if (length < 0) {
            // compressed name pointer, first two bits are 1
            // drop second byte of compression offset
            in.skip(1);
        } else {
            while (length > 0) {
                // skip each part of the domain name
                in.skip(length);
                length = in.readByte();
            }
        }
    }
}
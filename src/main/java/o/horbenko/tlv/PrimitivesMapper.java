package o.horbenko.tlv;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PrimitivesMapper {


    public static <T>
    byte[] toBytes(T t, Class<T> inType) {

        if (inType.isAssignableFrom(String.class))
            return toBytes((String) t);

        if (inType.isAssignableFrom(Short.class))
            return toBytes((short) t);

        if (inType.isAssignableFrom(Integer.class))
            return toBytes((Integer) t);

        if (inType.isAssignableFrom(Long.TYPE))
            return toBytes((long) t);

        throw new UnsupportedOperationException();
    }

    public static <T>
    T mapToObject(byte[] from, Class<T> mapInto) {

        if (mapInto.isAssignableFrom(String.class))
            return (T) toString(from);

        if (mapInto.isAssignableFrom(Short.TYPE))
            return (T) toShort(from);

        if (mapInto.isAssignableFrom(Integer.TYPE))
            return (T) toInt(from);

        if (mapInto.isAssignableFrom(Long.TYPE))
            return (T) toLong(from);

        throw new UnsupportedOperationException();
    }


    private static byte[] toBytes(short val) {
        return ByteBuffer.allocateDirect(Short.BYTES).putShort(val).array();
    }

    private static byte[] toBytes(int val) {
        return ByteBuffer.allocateDirect(Integer.BYTES).putInt(val).array();
    }

    private static byte[] toBytes(long val) {
        return ByteBuffer.allocateDirect(Long.BYTES).putLong(val).array();
    }

    private static byte[] toBytes(String val) {
        return val.getBytes(StandardCharsets.UTF_8);
    }

    private static Short toShort(byte[] bytes) {
        return ByteBuffer.allocateDirect(Short.BYTES).put(bytes).rewind().getShort();
    }

    private static Integer toInt(byte[] bytes) {
        return ByteBuffer.allocateDirect(Integer.BYTES).put(bytes).rewind().getInt();
    }

    private static Long toLong(byte[] bytes) {
        return ByteBuffer.allocateDirect(Long.BYTES).put(bytes).rewind().getLong();
    }

    private static String toString(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

}

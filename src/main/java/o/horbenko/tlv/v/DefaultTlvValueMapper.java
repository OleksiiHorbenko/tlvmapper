package o.horbenko.tlv.v;

import o.horbenko.tlv.ByteArrayValue;
import o.horbenko.tlv.TlvValueMapper;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class DefaultTlvValueMapper implements TlvValueMapper {

    private static final DefaultTlvValueMapper mapperInstance = new DefaultTlvValueMapper();

    public static DefaultTlvValueMapper getInstance() {
        return mapperInstance;
    }

    @Override
    public
    byte[] encodeTlvValue(Object t, Class<?> inType) {

        if (inType.isAssignableFrom(String.class))
            return toBytes((String) t);

        if (t instanceof Short)
            return toBytes((short) t);

        if (inType.isAssignableFrom(Integer.class))
            return toBytes((Integer) t);

        if (inType.isAssignableFrom(Long.TYPE))
            return toBytes((long) t);

        if (inType == byte[].class) {
            return (byte[]) t;
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T toObject(byte[] V, int valueStartOffset, int valueEndOffset, Class<T> outType) {

        if (outType.isAssignableFrom(String.class))
            return (T) toString(V, valueStartOffset, valueEndOffset);

        if (outType.isAssignableFrom(Short.class)) {
            if (valueEndOffset - valueStartOffset != 2)
                throw new IllegalArgumentException();
            return (T) toShort(V, valueStartOffset);
        }

        if (outType.isAssignableFrom(Integer.class)) {
            if (valueEndOffset - valueStartOffset != 4)
                throw new IllegalArgumentException();
            return (T) toInt(V, valueStartOffset);
        }

        if(outType.isAssignableFrom(Long.class)) {
            if (valueEndOffset - valueStartOffset != 8)
                throw new IllegalArgumentException();
            return (T) toLong(V, valueStartOffset);
        }

        if (outType == byte[].class) {
            return (T) Arrays.copyOfRange(V, valueStartOffset, valueEndOffset);
        }

        if (outType.isAssignableFrom(ByteArrayValue.class)) {
            return (T) ByteArrayValue.builder()
                    .valueHolder(V)
                    .byteArrayStartOffset(valueStartOffset)
                    .byteArrayEndOffset(valueEndOffset)
                    .build();
        }


        throw new IllegalArgumentException();
    }



    @Override
    public boolean isInnerTlvAttributesContainer(Class<?> clazz) {
        if (clazz.isAssignableFrom(String.class)
                || clazz.isAssignableFrom(Short.class)
                || clazz.isAssignableFrom(Integer.class)
                || clazz.isAssignableFrom(Long.class)
                || clazz == byte[].class
                || clazz == ByteArrayValue.class
        )
            return false;
        else
            return true;

    }


    public static byte[] toBytes(short val) {
        return ByteBuffer.allocate(Short.BYTES).putShort(val).array();
    }

    public static byte[] toBytes(int val) {
        return ByteBuffer.allocate(Integer.BYTES).putInt(val).array();
    }

    public static byte[] toBytes(long val) {
        return ByteBuffer.allocateDirect(Long.BYTES).putLong(val).array();
    }

    public static byte[] toBytes(String val) {
        return val.getBytes(StandardCharsets.UTF_8);
    }

    public static Short toShort(byte[] bytes) {
        return toShort(bytes, 0);
    }

    public static Short toShort(byte[] bytes, int startOffset) {
        return ByteBuffer.allocateDirect(Short.BYTES).put(bytes, startOffset, Short.BYTES).rewind().getShort();
    }

    public static Integer toInt(byte[] bytes, int offset) {
        return ByteBuffer
                .allocate(Integer.BYTES)
                .put(bytes, offset, Integer.BYTES)
                .rewind()
                .getInt();
    }

    public static Long toLong(byte[] bytes, int offset) {
        return ByteBuffer
                .allocateDirect(Long.BYTES)
                .put(bytes, offset, Long.BYTES)
                .rewind()
                .getLong();
    }

    public static String toString(byte[] bytes, int startOffset, int endOffset) {
        return new String(bytes, startOffset, endOffset - startOffset, StandardCharsets.UTF_8);
    }

}

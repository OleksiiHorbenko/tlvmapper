package o.horbenko.tlv.v;

import o.horbenko.tlv.TlvValueMapper;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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

//        if (inType.isAssignableFrom(Integer.class))
        if (t instanceof Integer)
            return toBytes((Integer) t);

        if (inType.isAssignableFrom(Long.TYPE))
            return toBytes((long) t);

        if (inType == byte[].class) {
            return (byte[]) t;
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public <T>
    T toObject(byte[] from, Class<T> mapInto) {

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

    @Override
    public boolean isFieldsContainer(Class<?> clazz) {
        if (clazz.isAssignableFrom(String.class)
                || clazz.isAssignableFrom(Short.TYPE)
                || clazz.isAssignableFrom(Integer.TYPE)
                || clazz.isAssignableFrom(Long.TYPE)
                || clazz == byte[].class
        )
            return false;
        else
            return true;

    }


    private static byte[] toBytes(short val) {
        return ByteBuffer.allocate(Short.BYTES).putShort(val).array();
    }

    private static byte[] toBytes(int val) {
        return ByteBuffer.allocate(Integer.BYTES).putInt(val).array();
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

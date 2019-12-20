package o.horbenko.tlv;

public interface TlvValueMapper {

    <T> byte[] toBytes(Object t, Class<?> inType);

    <T> T toObject(byte[] from, Class<T> outType);

}

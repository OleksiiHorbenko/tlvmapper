package o.horbenko.tlv;

public interface TlvValueMapper {

    byte[] encodeTlvValue(Object t, Class<?> inType);

    <T> T toObject(byte[] from, Class<T> outType);

    boolean isFieldsContainer(Class<?> clazz);

}

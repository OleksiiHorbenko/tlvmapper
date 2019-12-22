package o.horbenko.tlv;

public interface TlvValueMapper {

    byte[] encodeTlvValue(Object t, Class<?> inType);

    <T> T toObject(byte[] tlv,
                    int valueStartOffset, int valueEndOffset,
                    Class<T> outType);

    boolean isInnerTlvAttributesContainer(Class<?> clazz);

}

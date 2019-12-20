package o.horbenko.tlv;

public class TlvTagMapper {

    public static byte[] encodeTlvTag(short tag) {
        return DefaultTlvValueMapper.getInstance().encodeTlvValue(tag, Short.class);
    }


}

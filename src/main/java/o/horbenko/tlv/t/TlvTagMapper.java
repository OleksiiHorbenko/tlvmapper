package o.horbenko.tlv.t;

import o.horbenko.tlv.v.DefaultTlvValueMapper;

public class TlvTagMapper {

    public static byte[] encodeTlvTag(short tag) {
        return DefaultTlvValueMapper.getInstance().encodeTlvValue(tag, Short.class);
    }


}

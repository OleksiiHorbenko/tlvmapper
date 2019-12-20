package o.horbenko.tlv;

public class TlvTagMapper {

    public static byte[] encode(short tag) {
        return DefaultTlvValueMapper.getInstance().toBytes(tag, Short.class);
    }


}

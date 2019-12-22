package o.horbenko.tlv.t;

import o.horbenko.tlv.v.DefaultTlvValueMapper;

import java.nio.ByteBuffer;

public class TlvTagMapper {

    public static byte[] encodeTlvTag(short tag) {
        return DefaultTlvValueMapper.toBytes(tag);
    }

    public static short parseTlvTag(byte[] tlvs, int startOffset) {
        return DefaultTlvValueMapper.toShort(tlvs, startOffset);
    }


}

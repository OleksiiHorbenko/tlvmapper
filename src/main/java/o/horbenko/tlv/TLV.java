package o.horbenko.tlv;

import lombok.*;

@Data
@Builder
public class TLV {
    private short tag;

    private int length;
    private int encodedLengthBytesCount;

    private int valueStartOffset;
    private int valueEndOffset;

    private byte[] value;

    public int getTlvSize() {
        return 2 + encodedLengthBytesCount + length;
    }
}

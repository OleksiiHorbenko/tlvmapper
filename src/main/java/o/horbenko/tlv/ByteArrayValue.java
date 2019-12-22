package o.horbenko.tlv;

import lombok.Builder;
import lombok.Data;

import java.util.Arrays;

/**
 * Replaces copying of byte[] objects to avoid not required heapMemory leaks
 */
@Data
@Builder
public class ByteArrayValue {
    private byte[] valueHolder;
    private int byteArrayStartOffset;
    private int byteArrayEndOffset;

    public byte[] asByteArray() {
        return Arrays.copyOfRange(valueHolder, byteArrayStartOffset, byteArrayEndOffset);
    }

}

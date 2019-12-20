package o.horbenko.tlv.l;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * As said in https://docs.microsoft.com/en-us/windows/win32/seccertenroll/about-encoded-length-and-value-bytes,
 * <p>
 * Description:
 * The Length field in a TLV triplet identifies the number of bytes encoded in the Value field.
 * The Value field contains the content being sent between computers.
 * If the Value field contains fewer than 128 bytes, the Length field requires only one byte.
 * Bit 7 of the Length field is zero (0) and the remaining bits identify the number of bytes of content being sent.
 * If the Value field contains more than 127 bytes, bit 7 of the Length field is one (1)
 * and the remaining bits identify the number of bytes needed to contain the length.
 */
@Slf4j
public class TlvLengthMapper {

    public static byte[] encodeTlvLength(int valueLength) {

        if (valueLength < 0) {
            throw new IllegalArgumentException("L must be greater than zero!");
        }

        if (valueLength <= Byte.MAX_VALUE) {
            // return single-byte L

            return new byte[]{
                    (byte) valueLength
            };

        } else {
            // prepare multy-bytes encoded L

            int encodedLengthBytesCount = getLengthBytesCount(valueLength);
            ByteBuffer tmp = ByteBuffer.allocate(1 + encodedLengthBytesCount);

            // set FIRST bit to 1
            byte encodedLengthValueBytesCount = (byte) (encodedLengthBytesCount | 0x80);

            // put size of value length
            tmp.put(encodedLengthValueBytesCount);

            // put valueLength
            putLengthValueBytes(valueLength, encodedLengthBytesCount, tmp);

            return tmp.array();
        }
    }


    public static int decodeTlvLength(byte[] toParse, int tagOffset) {

        int lengthFirstByteOffset = tagOffset + 1;
        byte first = toParse[lengthFirstByteOffset];

        // is Value Length 0 <= Value Length <= 127 ?
        if (isFirstBitIsZero(first)) {
            return first;
        } else {

            byte multyByteLengthSize = setFirstBitToZero(first);

            return parseMultiByteLength(
                    multyByteLengthSize,
                    toParse,
                    lengthFirstByteOffset + 1
            );
        }
    }

    public static int getFullEncodedTlvLength(int length) {
        if (length <= Byte.MAX_VALUE)
            return 1;
        else
            return 1 + getLengthBytesCount(length);
    }

    private static boolean isFirstBitIsZero(byte toTest) {
        return (toTest & (1L << 7)) == 0;
    }

    // unset first bit in byte input(11111111) -> method -> result(01111111)
    private static byte setFirstBitToZero(byte toSetIn) {
        return (byte) (toSetIn & ~(1 << 7));
    }

    private static void putLengthValueBytes(int valueLength,
                                            int encodedLengthBytesCount,
                                            ByteBuffer bb) {
        if (encodedLengthBytesCount == Short.BYTES) {
            bb.putShort((short) valueLength);
        } else {
            bb.putInt(valueLength);
        }
    }


    /**
     * Retrieve the number of bytes, this length will be encoded into.
     *
     * @return the number of bytes, this length will be encoded into.
     */
    private static int getLengthBytesCount(int lengthInt) {
        if (lengthInt <= Byte.MAX_VALUE)
            return 1;
        else if (lengthInt <= Short.MAX_VALUE)
            return Short.BYTES;
        else
            return Integer.BYTES;
    }

    private static int parseMultiByteLength(int numBytes,
                                            byte[] data,
                                            int lengthValueOffset) {
        switch (numBytes) {
            case Short.BYTES:
                return parseShort(data, lengthValueOffset) & 0xFFFF;
            case Integer.BYTES:
                return parseInt(data, lengthValueOffset);
            default:
                throw new IllegalArgumentException("Wrong length encoding! Maximum length value is Integer.BYTES count");
        }
    }

    private static short parseShort(byte[] data, int dataOffset) {

        return ByteBuffer
                .allocate(Short.BYTES)
                .put(data, dataOffset, Short.BYTES)
                .rewind()
                .getShort();
    }

    private static int parseInt(byte[] data, int dataOffset) {
        return ByteBuffer
                .allocateDirect(Integer.BYTES)
                .put(data, dataOffset, Integer.BYTES)
                .rewind()
                .getInt();

    }

}

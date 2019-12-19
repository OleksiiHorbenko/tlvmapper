package o.horbenko.tlv;

import org.junit.Assert;
import org.junit.Test;

public class TlvLengthMapperTest {

    @Test
    public void toTlvLength() {

//        ARRANGE
        int inputByte = (byte) 77;
        int inputShort = (short) 888;
        int inputInt = 999999;

//        ACT
        byte[] tlvByte = TlvLengthMapper.encodeTlvLength(inputByte);
        byte[] tlvShort = TlvLengthMapper.encodeTlvLength(inputShort);
        byte[] tlvInt = TlvLengthMapper.encodeTlvLength(inputInt);


        int resultByte = TlvLengthMapper.decodeTlvLength(tlvByte, -1);
        int resultShort = TlvLengthMapper.decodeTlvLength(tlvShort, -1);
        int resultInt = TlvLengthMapper.decodeTlvLength(tlvInt, -1);

//        ASSERT
        Assert.assertEquals(tlvByte.length, Byte.BYTES);
        Assert.assertEquals(tlvShort.length, Short.BYTES + 1);
        Assert.assertEquals(tlvInt.length, Integer.BYTES + 1);

        Assert.assertEquals(inputByte, resultByte);
        Assert.assertEquals(inputShort, resultShort);
        Assert.assertEquals(inputInt, resultInt);
    }

    @Test
    public void encodeShortAsLength_ok() {

        int length = (short) 777;

        byte[] encodedL = TlvLengthMapper.encodeTlvLength(length);
        int reconstructedL = TlvLengthMapper.decodeTlvLength(encodedL, -1);

        Assert.assertEquals(length, reconstructedL);
    }

    @Test
    public void encodeIntegerAsLength_ok() {

        // ARRANGE
        int length =  999999;

        // ACT
        byte[] encodedL = TlvLengthMapper.encodeTlvLength(length);
        int reconstructedL = TlvLengthMapper.decodeTlvLength(encodedL, -1);

        // ASSERT
        Assert.assertEquals(length, reconstructedL);
    }

}

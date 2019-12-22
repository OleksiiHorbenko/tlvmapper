package o.horbenko.tlv;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import o.horbenko.tlv.v.DefaultTlvValueMapper;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

public class TlvParserTest {

    @Test
    public void testTlvParser_ok() {

        // ARRANGE
        byte[] input = HexBin.decode(
                "0378" + "04" + "000004D2" +            // intF
                        "0309" + "11" + "48656C6C6F20544C562070617273657221" + //stringF
                        "0001" + "02" + "0003" +                // shortF
                        "0002" + "08" + "0000000000000004" +    // longF
                        "03E7" + "02" + "0708"                  // ByteArrayValue
        );

        Level1 expected = Level1.builder()
                .intF(1234)
                .stirngF("Hello TLV parser!")
                .shortF((short) 3)
                .longF((long) 4)
                .byteArrayValue(ByteArrayValue.builder()
                        .valueHolder(input)
                        .byteArrayStartOffset(46)
                        .byteArrayEndOffset(48)
                        .build())
//                .level2(Level2.builder()
//                        .level2int(9)
//                        .build())
                .build();


        // ACT
        Level1 actual = TlvMapper.parseTlv(input, Level1.class);
        byte[] actualByteArrayValue = actual.getByteArrayValue().asByteArray();

        // ASSERT
        Assert.assertArrayEquals(new byte[]{0x07, 0x08}, actualByteArrayValue);
        Assert.assertEquals(expected, actual);
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Level1 {

        @TlvAttribute(tag = 777) // 0309
        private String stirngF;

        @TlvAttribute(tag = 888) // 0378
        private Integer intF;

        @TlvAttribute(tag = 999) // 03E7
        private ByteArrayValue byteArrayValue;

        @TlvAttribute(tag = 1)
        private Short shortF;

        @TlvAttribute(tag = 2)
        private Long longF;

        @TlvAttribute(tag = 3)
        private Level2 level2;

    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class Level2 {

        @TlvAttribute(tag = 55)
        private Integer level2int;

    }

}

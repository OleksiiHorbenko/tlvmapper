package o.horbenko.tlv;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TlvMapperTest {

    static class Level1 {

        @TlvAttribute(tag = 1)
        public Integer integer = 1;
        @TlvAttribute(tag = 2)
        public String string = "2";
        @TlvAttribute(tag = 3)
        public Short shortPrimitive = 3;
        @TlvAttribute(tag = 4)
        public List<Level2> stringList = new ArrayList();
        @TlvAttribute(tag = 5)
        public byte[] byteArray= new byte[]{0x71, 0x72, 0x73};

        public Level1() {
            stringList.add(new Level2());
            stringList.add(new Level2());
        }
    }

    static class Level2 {

        @TlvAttribute(tag = 11)
        public Short integer = 11;

    }



    @Test
    public void flatTest_success() {

        // ARRANGLE
        Level1 root = new Level1();
        byte[] expected = HexBin.decode(
                "00010400000001" +
                        "00020132" +
                        "0003020003" +
                        "00040A" + "000B02000B" + "000B02000B" +
                        "000503717273"
        );

        // ACT
        byte[] actual = TlvMapper.mapToTlv(root);

        // ASSERT
        Assert.assertArrayEquals(expected, actual);

    }

}

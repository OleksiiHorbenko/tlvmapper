package o.horbenko.tlv;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TlvMapperTest {

    static class FlatObject {

        @TlvAttribute(tag = 1)
        private Integer integer = 1;
        @TlvAttribute(tag = 2)
        private String string = "2";
        @TlvAttribute(tag = 3)
        private Short shortPrimitive = 3;

        @TlvAttribute(tag = 4)
        private List<String> stringList = new ArrayList();

        @TlvAttribute(tag = 5)
        private byte[] byteArray= new byte[]{0x71, 0x72, 0x73};

        public FlatObject integer(int i) {
            integer = i;
            return this;
        }

        public FlatObject string(String i) {
            string = i;
            return this;
        }

        public FlatObject() {
            this.stringList.add("FIRST");
            this.stringList.add("SECOND");
        }

        public Integer getInteger() {
            return integer;
        }

        public void setInteger(Integer integer) {
            this.integer = integer;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public short getShortPrimitive() {
            return shortPrimitive;
        }

        public void setShortPrimitive(short shortPrimitive) {
            this.shortPrimitive = shortPrimitive;
        }

        public FlatObject shortPrimitive(short i) {
            this.shortPrimitive = i;
            return this;
        }

        public List<String> getStringList() {
            return stringList;
        }

        public void setStringList(List<String> stringList) {
            this.stringList = stringList;
        }

        public byte[] getByteArray() {
            return byteArray;
        }

        public void setByteArray(byte[] byteArray) {
            this.byteArray = byteArray;
        }
    }

    @Test
    public void flatTest_success() {
        System.out.println("hello test");
        FlatObject flatObject = new FlatObject()
                .integer(777)
                .shortPrimitive((short) 111)
                .string("string");

        byte[] tlvs = TlvMapper.mapToTlv(flatObject);

        System.out.println(HexBin.encode(tlvs));

    }

}

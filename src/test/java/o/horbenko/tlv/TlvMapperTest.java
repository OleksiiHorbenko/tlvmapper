package o.horbenko.tlv;

import org.junit.Test;

import java.util.List;

public class TlvMapperTest {

    static class FlatObject {

        @TlvAttribute(tag = 1)
        private Integer integer;
        @TlvAttribute(tag = 2)
        private String string;
        @TlvAttribute(tag = 3)
        private short shortPrimitive;

        @TlvAttribute(tag = 4)
        private List<String> stringList;

        public FlatObject integer(int i) {
            integer = i;
            return this;
        }
        public FlatObject string(String i) {
            string = i;
            return this;
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
    }

    @Test
    public void flatTest_success() {
        System.out.println("hello test");
        FlatObject flatObject = new FlatObject()
                .integer(777)
                .shortPrimitive((short) 111)
                .string("string");

        byte[] tlvs = TlvMapper.mapToTlv(flatObject);

    }

}

package o.horbenko.tlv;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import o.horbenko.tlv.l.TlvLengthMapper;
import o.horbenko.tlv.t.TlvTagMapper;
import o.horbenko.tlv.v.DefaultTlvValueMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class TlvMapper {

    public static byte[] mapToTlv(Object toMap) {
        return mapToTlv(toMap, DefaultTlvValueMapper.getInstance());
    }

    // todo
    public static <T>
    byte[] mapToTlv(T toMap, TlvValueMapper tlvValueMapper) {
        try {

            if (!tlvValueMapper.isFieldsContainer(toMap.getClass())) {
                return tlvValueMapper.encodeTlvValue(toMap, toMap.getClass());
            }

            Class<?> objectClass = toMap.getClass();
            Field[] classFields = objectClass.getDeclaredFields();

            ByteArrayOutputStream resultBytes = new ByteArrayOutputStream();

            for (Field field : classFields) {
                if (field.isAnnotationPresent(TlvAttribute.class)) {
                    TlvAttribute anno = field.getAnnotation(TlvAttribute.class);

                    Class<?> fieldType = field.getType();
                    Object fieldValue = field.get(toMap);

                    System.out.println("\ttype=" + fieldType + "\tvalue=" + fieldValue + "\tanno=" + anno);

                    byte[] V;
                    if (isJavaList(fieldType)) {
                        V = mapListToTlv((List) fieldValue, tlvValueMapper);
                    } else {
                        V = tlvValueMapper.encodeTlvValue(fieldValue, fieldType);
                    }

                    byte[] L = TlvLengthMapper.encodeTlvLength(V.length);
                    byte[] T = TlvTagMapper.encodeTlvTag(anno.tag());

                    resultBytes.write(T);
                    resultBytes.write(L);
                    resultBytes.write(V);
                }
            }

            return resultBytes.toByteArray();

        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public static <T>
    T parseTlv(byte[] tlv,
               Class<T> outClass) {
        return parseTlv(tlv, outClass, DefaultTlvValueMapper.getInstance());

    }

    public static <T>
    T parseTlv(byte[] tlv,
               Class<T> outClass,
               TlvValueMapper tlvValueMapper) {
        try {

            if (!tlvValueMapper.isFieldsContainer(outClass)) {
                TLV singleTlv = parseTlv(tlv, 0);
                return tlvValueMapper.toObject(
                        tlv, singleTlv.getValueStartOffset(), singleTlv.getValueEndOffset(),
                        outClass);
            }

            Field[] classFields = outClass.getDeclaredFields();
            T resultInstance = outClass.getConstructor().newInstance();

            Map<Short, TLV> rootLevelTlvMap = parseTlvLevel(tlv, 0);

//            rootLevelTlvMap.forEach((aShort, tlv1) -> System.out.println(" ++++ tag=" + aShort + " value=" + tlv1));

            for (Field field : classFields) {
                if (field.isAnnotationPresent(TlvAttribute.class)) {
                    TlvAttribute anno = field.getAnnotation(TlvAttribute.class);
                    short tagToSearchBy = anno.tag();

                    if (rootLevelTlvMap.containsKey(tagToSearchBy)) {

                        TLV fieldTlv = rootLevelTlvMap.get(tagToSearchBy);

                        Class<?> fieldType = field.getType();

                        Object val = tlvValueMapper.toObject(
                                fieldTlv.getValue(),
                                fieldTlv.getValueStartOffset(), fieldTlv.getValueEndOffset(),
                                fieldType);

                        field.setAccessible(true);
                        field.set(resultInstance, val);
                        field.setAccessible(false);
                    }
                }
            }

            return resultInstance;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static Map<Short, TLV> parseTlvLevel(byte[] tlvBytes, int startOffset) {
        Map<Short, TLV> result = new HashMap<>();

        int tlvStartOffset = startOffset;

        while (tlvStartOffset < tlvBytes.length) {
            TLV tlv = parseTlv(tlvBytes, tlvStartOffset);
            result.put(tlv.getTag(), tlv);
            tlvStartOffset += tlv.getTlvSize();
        }

        return result;
    }

    private static <T extends List>
    byte[] mapListToTlv(T fieldListValue,
                        TlvValueMapper mapper) throws IOException {

        ByteArrayOutputStream resultBuf = new ByteArrayOutputStream();

        for (Object obj : fieldListValue) {
            byte[] objectTlv = mapToTlv(obj, mapper);
            resultBuf.write(objectTlv);
        }

        return resultBuf.toByteArray();
    }


    private static boolean isJavaList(Class<?> fieldType) {
        return List.class.isAssignableFrom(fieldType);
    }


    private static TLV parseTlv(byte[] tlv, int firstTagByteOffset) {

        short T = TlvTagMapper.parseTlvTag(tlv, firstTagByteOffset);

        int L = TlvLengthMapper.decodeTlvLength(tlv, firstTagByteOffset + 2);
        int LbytesCount = TlvLengthMapper.getFullEncodedTlvLength(L);

        int valueStartOffset = firstTagByteOffset + 2 + LbytesCount;
        int valueEndOffset = valueStartOffset + L;

        return TLV.builder()
                .tag(T)
                .length(L)
                .encodedLengthBytesCount(LbytesCount)
                .value(tlv)
                .valueStartOffset(valueStartOffset)
                .valueEndOffset(valueEndOffset)
                .build();
    }


}

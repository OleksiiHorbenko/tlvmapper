package o.horbenko.tlv;

import o.horbenko.tlv.l.TlvLengthMapper;
import o.horbenko.tlv.t.TlvTagMapper;
import o.horbenko.tlv.v.DefaultTlvValueMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TlvMapper {

    public static byte[] mapToTlv(Object toMap) {
        return mapToTlv(toMap, DefaultTlvValueMapper.getInstance());
    }

    public static <T>
    byte[] mapToTlv(T toMap, TlvValueMapper tlvValueMapper) {
        try {

            if (!tlvValueMapper.isInnerTlvAttributesContainer(toMap.getClass())) {
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

                    byte[] V = mapFieldToTlv(tlvValueMapper, fieldType, fieldValue);
                    byte[] L = TlvLengthMapper.encodeTlvLength(V.length);
                    byte[] T = TlvTagMapper.encodeTlvTag(anno.tag());

                    resultBytes.write(T);
                    resultBytes.write(L);
                    resultBytes.write(V);
                }
            }

            return resultBytes.toByteArray();

        } catch (IllegalAccessException | IOException e) {
            throw new RuntimeException(e);
        }
    }



    public static <T>
    T parseTlv(byte[] tlv,
               Class<T> outClass) {

        return parseTlv(
                tlv, 0, tlv.length,
                outClass,
                DefaultTlvValueMapper.getInstance());
    }

    public static <T>
    T parseTlv(byte[] tlv, int tlvStartOffset, int tlvEndOffset,
               Class<T> outClass,
               TlvValueMapper tlvValueMapper) {
        try {

            if (!tlvValueMapper.isInnerTlvAttributesContainer(outClass)) {
                TLV singleTlv = parseTlv(tlv, tlvStartOffset);
                return tlvValueMapper.toObject(
                        tlv, singleTlv.getValueStartOffset(), singleTlv.getValueEndOffset(),
                        outClass);
            }

            Field[] classFields = outClass.getDeclaredFields();
            T resultInstance = outClass.getConstructor().newInstance();

            Map<Short, TLV> rootLevelTlvMap = parseTlvLevel(tlv, tlvStartOffset, tlvEndOffset);

            // iterate over all result type fields
            for (Field field : classFields) {
                if (field.isAnnotationPresent(TlvAttribute.class)) {
                    TlvAttribute anno = field.getAnnotation(TlvAttribute.class);
                    short tagToSearchBy = anno.tag();

                    // if TLV by tag from object field was found in input
                    if (rootLevelTlvMap.containsKey(tagToSearchBy)) {

                        // get TLV field
                        TLV fieldTlv = rootLevelTlvMap.get(tagToSearchBy);

                        // get class to map into
                        Class<?> fieldType = field.getType();

                        // parse and cast object (RECURSION UNDER THE HOOD!)
                        Object val = parseFieldObject(tlvValueMapper, fieldTlv, fieldType);

                        // set value from TLV into result object
                        boolean prevAccessibleFlagState = field.isAccessible();
                        field.setAccessible(true);
                        field.set(resultInstance, val);
                        field.setAccessible(prevAccessibleFlagState);
                    }
                }
            }

            return resultInstance;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] mapFieldToTlv(TlvValueMapper tlvValueMapper,
                                        Class<?> fieldType,
                                        Object fieldValue) throws IOException {
        byte[] V;

        if (isJavaList(fieldType)) {
            V = mapListToTlv((List) fieldValue, tlvValueMapper);
        } else {
            V = tlvValueMapper.encodeTlvValue(fieldValue, fieldType);
        }
        return V;
    }


    private static Object parseFieldObject(TlvValueMapper tlvValueMapper,
                                           TLV fieldTlv,
                                           Class<?> fieldType) {
        Object val;

        if (tlvValueMapper.isInnerTlvAttributesContainer(fieldType)) {

            // inner Object that (maybe) contains inner attributes
            val = parseTlv(
                    fieldTlv.getValue(),
                    fieldTlv.getValueStartOffset(),
                    fieldTlv.getValueEndOffset(),
                    fieldType,
                    tlvValueMapper);
        } else {

            // map TLV value to field Class
            val = tlvValueMapper.toObject(
                    fieldTlv.getValue(),
                    fieldTlv.getValueStartOffset(), fieldTlv.getValueEndOffset(),
                    fieldType);
        }

        return val;
    }

    private static Map<Short, TLV> parseTlvLevel(byte[] tlvBytes, int startOffset, int endOffset) {
        Map<Short, TLV> result = new HashMap<>();

        int tlvStartOffset = startOffset;

        while (tlvStartOffset < endOffset) {
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

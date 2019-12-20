package o.horbenko.tlv;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

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
                    field.setAccessible(true);

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

    private static <T extends List>
    byte[] mapListToTlv(T fieldListValue, TlvValueMapper mapper) throws IOException {

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

}

package o.horbenko.tlv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public class TlvMapper {


    // todo
    public static <T>
    byte[] mapToTlv(T toMap, TlvValueMapper mapper) {
        try {

            ByteArrayOutputStream resultBytes = new ByteArrayOutputStream();

            Class<?> objectClass = toMap.getClass();
            Field[] classFields = objectClass.getDeclaredFields();

            for (Field field : classFields) {

                if (field.isAnnotationPresent(TlvAttribute.class)) {
                    TlvAttribute anno = field.getAnnotation(TlvAttribute.class);
                    field.setAccessible(true);

                    Class<?> fieldType = field.getType();
                    Object fieldValue = field.get(toMap);

                    System.out.println("\ttype=" + fieldType + "\tvalue=" + fieldValue + "\tanno=" + anno);

                    byte[] V = mapper.toBytes(fieldValue, fieldType);
                    byte[] L = TlvLengthMapper.encodeTlvLength(V.length);
                    byte[] T = TlvTagMapper.encode(anno.tag());

                    resultBytes.write(T);
                    resultBytes.write(L);
                    resultBytes.write(V);

                    if (isCollectionChild(fieldType)) {
                        ParameterizedType collectionType = (ParameterizedType) field.getGenericType();
                        Class<?> genericType = (Class<?>) collectionType.getActualTypeArguments()[0];
                        System.out.println("\t\t --- generic type=" + genericType);
                    }
                }
            }

            return resultBytes.toByteArray();

        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    private static boolean isCollectionChild(Class<?> fieldType) {
        return Collection.class.isAssignableFrom(fieldType);
    }

}

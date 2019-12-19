package o.horbenko.tlv;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public class TlvMapper {

    // todo
    public static <T>
    byte[] mapToTlv(T toMap) {
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

                    if (isCollectionChild(fieldType)) {
                        ParameterizedType collectionType = (ParameterizedType) field.getGenericType();
                        Class<?> genericType = (Class<?>) collectionType.getActualTypeArguments()[0];
                        System.out.println("\t\t --- generic type=" + genericType);
                    }
                }
            }

            return resultBytes.toByteArray();

        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException();
        }
    }


    private static boolean isCollectionChild(Class<?> fieldType) {
        return Collection.class.isAssignableFrom(fieldType);
    }

}

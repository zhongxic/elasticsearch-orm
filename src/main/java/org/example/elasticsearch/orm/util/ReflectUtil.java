package org.example.elasticsearch.orm.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ReflectUtil {


    private static final Map<Class<?>, List<Field>> KNOWN_CLASS_FIELDS = new ConcurrentHashMap<>();

    public static boolean isJdkProvideClass(Class<?> cls) {
        return cls != null && cls.getClassLoader() == null;
    }

    public static List<Field> getClassDeclaredFields(Class<?> aClass) {
        return KNOWN_CLASS_FIELDS.computeIfAbsent(aClass, clazz -> {
            Class<?> type = clazz;
            List<Field> declaredFields = new ArrayList<>();
            while (!Object.class.equals(type) && !isJdkProvideClass(type)) {
                declaredFields.addAll(Arrays.asList(type.getDeclaredFields()));
                type = type.getSuperclass();
            }
            return declaredFields;
        });
    }

    public static boolean isStaticFinal(Field field) {
        return Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers());
    }

}

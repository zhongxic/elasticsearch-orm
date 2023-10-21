package org.example.elasticsearch.orm.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.elasticsearch.orm.annotation.Param;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParameterNameUtil {

    public static Map<String, Object> getNamedParameters(Method method, Object[] args) {
        Map<String, Object> params = new HashMap<>();

        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            return params;
        }
        if (parameters.length == 1) {
            getSingleNamedParameter(parameters[0], getIndexParam(args, 0), params);
            return params;
        }
        for (int i = 0; i < parameters.length; i++) {
            doGetNamedParameter(parameters[i], getIndexParam(args, i), params);
        }
        return params;
    }

    private static void getSingleNamedParameter(Parameter parameter, Object arg, Map<String, Object> params) {

        if (Map.class.isAssignableFrom(parameter.getType()) && !parameter.isAnnotationPresent(Param.class)) {
            doGetNamedParameterFromMap(arg, params);
            return;
        }

        if (Object.class.isAssignableFrom(parameter.getType())
                && !ReflectUtil.isJdkProvideClass(parameter.getType())
                && !parameter.isAnnotationPresent(Param.class)) {

            doGetNamedParameterFromClassDeclaredFields(parameter.getType(), arg, params);
            return;
        }

        doGetNamedParameter(parameter, arg, params);
    }

    @SuppressWarnings("unchecked")
    private static void doGetNamedParameterFromMap(Object arg, Map<String, Object> params) {
        if (arg == null) {
            return;
        }
        Map<Object, Object> args = (Map<Object, Object>) arg;
        for (Entry<Object, Object> entry : args.entrySet()) {
            params.put(String.valueOf(entry.getKey()), entry.getValue());
        }
    }

    private static void doGetNamedParameterFromClassDeclaredFields(Class<?> parameterType, Object arg, Map<String, Object> params) {
        List<Field> fields = ReflectUtil.getClassDeclaredFields(parameterType);
        for (Field field : fields) {
            try {
                if (ReflectUtil.isStaticFinal(field)) {
                    continue;
                }
                ReflectionUtils.makeAccessible(field);
                params.put(field.getName(), arg == null ? null : field.get(arg));
            } catch (Exception e) {
                throw new UnsupportedOperationException(
                        String.format("error get named parameters of type '%s', can not get value of field '%s': ",
                                parameterType.getName(), field.getName()), e);
            }
        }
    }

    private static void doGetNamedParameter(Parameter parameter, Object arg, Map<String, Object> params) {
        String parameterName = parameter.getName();
        Param annotation = parameter.getAnnotation(Param.class);
        if (annotation != null) {
            parameterName = annotation.value();
        }
        params.put(parameterName, arg);
    }

    private static Object getIndexParam(Object[] args, int index) {
        if (index > args.length - 1) {
            return null;
        }
        return args[index];
    }

}

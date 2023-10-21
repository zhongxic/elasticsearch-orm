package org.example.elasticsearch.orm.util;

import lombok.extern.slf4j.Slf4j;
import org.example.elasticsearch.orm.mapper.UserMapper;
import org.example.elasticsearch.orm.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
class ParameterNameUtilTests {

    @Test
    void shouldUseParameterNameAsNamedParameter() throws NoSuchMethodException {
        Method method = UserMapper.class.getMethod("getUser", String.class, String.class);
        Map<String, Object> namedParameter = ParameterNameUtil.getNamedParameters(method, new Object[]{});
        log.info("named parameter: {}", namedParameter);
        Assertions.assertTrue(namedParameter.containsKey("username"));
        Assertions.assertTrue(namedParameter.containsKey("password"));
    }

    @Test
    void shouldUseMapAsNamedParameter() throws NoSuchMethodException {
        Method method = UserMapper.class.getMethod("getUsersByMap", Map.class);
        Map<String, Object> param = new HashMap<>();
        param.put("username", "username");
        param.put("password", "password");
        Map<String, Object> namedParameter = ParameterNameUtil.getNamedParameters(method, new Object[]{param});
        log.info("named parameter: {}", namedParameter);
        Assertions.assertTrue(namedParameter.containsKey("username"));
        Assertions.assertTrue(namedParameter.containsKey("password"));
    }

    @Test
    void shouldUsePropertiesAsNamedParameter() throws NoSuchMethodException {
        Method method = UserMapper.class.getMethod("getUsers", User.class);
        Map<String, Object> namedParameter = ParameterNameUtil.getNamedParameters(method, new Object[]{});
        log.info("named parameter: {}", namedParameter);
        Assertions.assertTrue(namedParameter.containsKey("username"));
        Assertions.assertTrue(namedParameter.containsKey("password"));
    }

    @Test
    void shouldAnnotationValueAsNamedParameter() throws NoSuchMethodException {
        Method method = UserMapper.class.getMethod("getUserWithAnnotatedParam", User.class);
        Map<String, Object> namedParameter = ParameterNameUtil.getNamedParameters(method, new Object[]{});
        log.info("named parameter: {}", namedParameter);
        Assertions.assertTrue(namedParameter.containsKey("u"));
    }

}

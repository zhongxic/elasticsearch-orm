package org.example.elasticsearch.orm;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.example.elasticsearch.orm.core.EsPage;
import org.example.elasticsearch.orm.core.EsPageStarter;
import org.example.elasticsearch.orm.mapper.UserMapper;
import org.example.elasticsearch.orm.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
class ElasticsearchOrmApplicationTests {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    UserMapper userMapper;

    @Test
    void getUserByMap() {
        Map<String, Object> param = new HashMap<>();
        param.put("password", "password");
        List<User> users = userMapper.getUsersByMap(param);
        Assertions.assertNotNull(users);
        log.info("users: {}", users);
    }

    @Test
    void getUsers() {
        User user = new User();
        user.setPassword("password");

        EsPageStarter.start(2, 10);
        EsPage<User> users = userMapper.getUsers(user);
        Assertions.assertNotNull(users);
        log.info("users: {}", users);
    }

    @Test
    void countUsers() {
        User user = new User();
        user.setPassword("password");

        Integer count = userMapper.countUser(user);
        Assertions.assertNotNull(count);
        log.info("count: {}", count);
    }

    @Test
    void aggregationByGender() {
        SearchResponse searchResponse = userMapper.aggregationByGender();
        Assertions.assertNotNull(searchResponse);
        log.info("searchResponse: {}", searchResponse);
    }

}

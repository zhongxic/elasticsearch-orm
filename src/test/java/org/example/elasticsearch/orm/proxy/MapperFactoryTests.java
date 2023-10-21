package org.example.elasticsearch.orm.proxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.example.elasticsearch.orm.core.Configuration;
import org.example.elasticsearch.orm.core.EsPage;
import org.example.elasticsearch.orm.core.EsPageStarter;
import org.example.elasticsearch.orm.executor.ElasticsearchRestHighLevelClientExecutor;
import org.example.elasticsearch.orm.handler.DefaultResponseHandler;
import org.example.elasticsearch.orm.mapper.UserMapper;
import org.example.elasticsearch.orm.model.User;
import org.example.elasticsearch.orm.xml.XmlStatementBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
class MapperFactoryTests {

    UserMapper mapper;

    @BeforeEach
    public void setup() throws IOException, ParserConfigurationException, SAXException {
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
        ElasticsearchRestHighLevelClientExecutor executor = new ElasticsearchRestHighLevelClientExecutor();
        executor.setRestHighLevelClient(restHighLevelClient);

        Configuration configuration = new Configuration();
        configuration.setElasticsearchExecutor(executor);
        configuration.setSearchResponseHandler(new DefaultResponseHandler(configuration));
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        configuration.setObjectMapper(objectMapper);
        new XmlStatementBuilder(new ClassPathResource("/mapper/UserMapper.xml"), configuration).parse();

        MapperFactory mapperFactory = new MapperFactory(configuration);
        mapper = mapperFactory.getMapper(UserMapper.class);
    }

    @Test
    void getUser() {
        Map<String, Object> user = mapper.getUser("user,n,ame,,,", "password");
        log.info("user: {}", user);
        Assertions.assertNotNull(user);
    }

    @Test
    void getUserByMap() {
        Map<String, Object> param = new HashMap<>();
        param.put("password", "password");
        List<User> users = mapper.getUsersByMap(param);
        log.info("users: {}", users);
    }

    @Test
    void getUsers() {
        User user = new User();
        user.setPassword("password");

        EsPageStarter.start(1, 10);
        EsPage<User> users = mapper.getUsers(user);
        Assertions.assertNotNull(users);
        log.info("users: {} , {}", users, users.getPages());
    }

    @Test
    void countUsers() {
        User user = new User();
        user.setPassword("password");

        Integer count = mapper.countUser(user);
        Assertions.assertNotNull(count);
        log.info("count: {}", count);
    }

    @Test
    void aggregationByGender() {
        SearchResponse searchResponse = mapper.aggregationByGender();
        Assertions.assertNotNull(searchResponse);
        log.info("searchResponse: {}", searchResponse);
    }

}

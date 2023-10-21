package org.example.elasticsearch.orm.mapper;

import org.elasticsearch.action.search.SearchResponse;
import org.example.elasticsearch.orm.annotation.EsMapper;
import org.example.elasticsearch.orm.annotation.Param;
import org.example.elasticsearch.orm.core.EsPage;
import org.example.elasticsearch.orm.model.User;

import java.util.List;
import java.util.Map;

@EsMapper
public interface UserMapper {

    Map<String, Object> getUser(String username, String password);

    List<User> getUsersByMap(Map<String, Object> param);

    EsPage<User> getUsers(User user);

    Integer countUser(User user);

    // no statement for this method
    List<User> getUserWithAnnotatedParam(@Param("u") User user);

    SearchResponse aggregationByGender();

}

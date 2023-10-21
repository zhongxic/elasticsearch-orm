package org.example.elasticsearch.orm.xml;

import lombok.extern.slf4j.Slf4j;
import org.example.elasticsearch.orm.core.Configuration;
import org.example.elasticsearch.orm.core.Statement;
import org.example.elasticsearch.orm.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Slf4j
class XmlStatementBuilderTests {

    @Test
    void loadXmlMapperFile() throws IOException, ParserConfigurationException, SAXException {
        Configuration configuration = new Configuration();
        XmlStatementBuilder xmlStatementBuilder = new XmlStatementBuilder(new ClassPathResource("/mapper/UserMapper.xml"), configuration);
        xmlStatementBuilder.parse();

        Statement statement = configuration.getStatement("org.example.elasticsearch.orm.mapper.UserMapper.getUser");
        Assertions.assertNotNull(statement);

        User user = new User();
        user.setUsername("user,n,ame,,,");
        user.setPassword("password");
        String dsl = statement.getBoundScript(user);
        log.info("dsl: \n{}", dsl);
    }

}

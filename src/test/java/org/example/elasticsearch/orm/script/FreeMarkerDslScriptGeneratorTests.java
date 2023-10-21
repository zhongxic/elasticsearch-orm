package org.example.elasticsearch.orm.script;

import lombok.extern.slf4j.Slf4j;
import org.example.elasticsearch.orm.model.User;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Slf4j
class FreeMarkerDslScriptGeneratorTests {

    @Test
    void generate() throws IOException {
        final String script = "{\n" +
                "    \"query\": {\n" +
                "        \"bool\": {\n" +
                "            \"must\": [\n" +
                "                <@trim>\n" +
                "                    <#if username?? && username != ''>{\"term\":{\"username\":{\"value\":\"${username}\"}}},</#if>\n" +
                "                    <#if password?? && password != ''>{\"term\":{\"password\":{\"value\":\"${password}\"}}},</#if>\n" +
                "                </@trim>\n" +
                "            ]\n" +
                "        }\n" +
                "    }\n" +
                "}\n";

        User user = new User();
        user.setUsername("user,n,ame,,,");
        user.setPassword("password");

        FreeMarkerDslScriptGenerator generator = new FreeMarkerDslScriptGenerator("test-script", script);
        String dsl = generator.generate(user);
        log.info("dsl: \n{}", dsl);
    }

}

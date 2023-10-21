package org.example.elasticsearch.orm.script;

import freemarker.template.Template;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.elasticsearch.orm.freemarker.FreeMarkerConfigurationUtil;
import org.example.elasticsearch.orm.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;

@Slf4j
@Getter
public class FreeMarkerDslScriptGenerator implements DslScriptGenerator {

    private final String id;

    private final String script;

    private final Template template;

    public FreeMarkerDslScriptGenerator(String id, String script) throws IOException {
        this.id = id;
        this.script = script;
        this.template = new Template(id, script, FreeMarkerConfigurationUtil.getConfiguration());
    }

    @Override
    public String generate(Object param) {
        try {
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, param);
        } catch (Exception e) {
            throw new UnsupportedOperationException(
                    String.format("error render template, id: '%s', template: '%s', param: '%s'", id, script, param));
        }
    }

}

package org.example.elasticsearch.orm.freemarker;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.StringWriter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FreeMarkerTemplateUtils {

    public static String processTemplateIntoString(Template template, Object model) throws IOException, TemplateException {
        StringWriter result = new StringWriter(1024);
        template.process(model, result);
        return result.toString();
    }

}
package org.example.elasticsearch.orm.freemarker;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class TrimDirective implements TemplateDirectiveModel {

    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        if (!params.isEmpty()) {
            throw new TemplateModelException("This directive doesn't allow parameters.");
        }
        if (loopVars.length != 0) {
            throw new TemplateModelException("This directive doesn't allow loop variables.");
        }

        if (body != null) {
            Writer writer = new StringWriter();
            body.render(writer);

            String content = writer.toString()
                    .replace("\n", "")
                    .replace("\t", "")
                    .trim();
            if (StringUtils.isNotBlank(content)) {
                if (content.startsWith(",")) {
                    content = content.substring(1);
                }
                if (content.endsWith(",")) {
                    content = content.substring(0, content.length() - 1);
                }
            }
            writer.close();
            env.getOut().write(content);
        } else {
            throw new RuntimeException("missing body");
        }
    }

}

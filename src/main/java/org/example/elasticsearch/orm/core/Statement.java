package org.example.elasticsearch.orm.core;

import lombok.Builder;
import lombok.Getter;
import org.example.elasticsearch.orm.script.DslScriptGenerator;

@Getter
@Builder
public class Statement {

    private final String id;

    private final String index;

    private final Class<?> resultType;

    private final DslScriptGenerator dslScriptGenerator;

    private final StatementType statementType;

    public String getBoundScript(Object param) {
        return this.dslScriptGenerator.generate(param);
    }

    public enum StatementType {

        QUERY,

        COUNT,

        AGGREGATION

    }

}

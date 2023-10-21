package org.example.elasticsearch.orm.proxy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.core.CountResponse;
import org.example.elasticsearch.orm.core.Configuration;
import org.example.elasticsearch.orm.core.EsPageStarter;
import org.example.elasticsearch.orm.core.Statement;
import org.example.elasticsearch.orm.util.ParameterNameUtil;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
public class MapperMethod {

    private final Method method;

    private final Configuration configuration;

    private final Statement statement;

    private boolean arrayReturn;

    private boolean listReturn;

    public MapperMethod(Method method, Configuration configuration) {
        this.method = method;
        this.configuration = configuration;
        String statementId = String.format("%s.%s", method.getDeclaringClass().getName(), method.getName());
        this.statement = configuration.getStatement(statementId);
        if (this.statement == null) {
            throw new UnsupportedOperationException(String.format("no statement found with statement id: '%s'", statementId));
        }
        this.analyzeMethod(method);
    }

    private void analyzeMethod(Method method) {
        if (method.getReturnType().isArray()) {
            this.arrayReturn = true;
        }
        if (List.class.isAssignableFrom(method.getReturnType())) {
            this.listReturn = true;
        }
    }

    public boolean isMultiReturn() {
        return this.isArrayReturn() || this.isListReturn();
    }

    public Object execute(Object[] args) {
        Map<String, Object> params = ParameterNameUtil.getNamedParameters(method, args);
        switch (statement.getStatementType()) {
            case QUERY:
                SearchResponse searchResponse = configuration.getElasticsearchExecutor()
                        .query(statement.getIndex(), statement.getBoundScript(params));
                return handlerSearchResponse(searchResponse);
            case COUNT:
                CountResponse countResponse = configuration.getElasticsearchExecutor()
                        .count(statement.getIndex(), statement.getBoundScript(params));
                return handlerCountResponse(countResponse);
            case AGGREGATION:
                return configuration.getElasticsearchExecutor()
                        .query(statement.getIndex(), statement.getBoundScript(params));
            default:
                throw new UnsupportedOperationException(String.format("unknown statement type '%s'", statement.getStatementType()));
        }
    }

    private Object handlerSearchResponse(SearchResponse searchResponse) {
        try {
            return configuration.getSearchResponseHandler().handle(searchResponse, this, statement.getResultType());
        } catch (Exception e) {
            throw new UnsupportedOperationException("error handle response: ", e);
        } finally {
            EsPageStarter.remove();
        }
    }

    private Object handlerCountResponse(CountResponse countResponse) {
        if (CountResponse.class.equals(method.getReturnType())) {
            return countResponse;
        } else {
            long count = countResponse.getCount();
            try {
                return configuration.convertValue(count, statement.getResultType());
            } catch (Exception e) {
                throw new UnsupportedOperationException(String.format("error convert long to type '%s'", statement.getResultType()), e);
            }
        }
    }

}

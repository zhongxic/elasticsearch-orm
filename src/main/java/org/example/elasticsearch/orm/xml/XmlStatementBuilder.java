package org.example.elasticsearch.orm.xml;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.elasticsearch.orm.core.Configuration;
import org.example.elasticsearch.orm.core.Statement;
import org.example.elasticsearch.orm.script.FreeMarkerDslScriptGenerator;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;

@Slf4j
public class XmlStatementBuilder {

    private static final String DISALLOW_DOC_TYPE = "http://apache.org/xml/features/disallow-doctype-decl";

    private final Resource resource;

    private final Configuration configuration;

    private final Document document;

    public XmlStatementBuilder(Resource resource, Configuration configuration) throws IOException, ParserConfigurationException, SAXException {
        this.resource = resource;
        this.configuration = configuration;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(DISALLOW_DOC_TYPE, true);
        this.document = factory.newDocumentBuilder().parse(this.resource.getInputStream());
    }

    public void parse() {
        XPathEvaluator xPathEvaluator = new XPathEvaluator(this.document);
        XNode node = xPathEvaluator.evalNode("/mapper");
        if (node != null) {
            String namespace = node.getAttribute("namespace");
            if (StringUtils.isBlank(namespace)) {
                throw new IllegalArgumentException(String.format("missing 'namespace' attribute in the mapper file '%s'", this.resource.getFilename()));
            }
            parseQuery(namespace, xPathEvaluator.evalNodes("/mapper/query"));
            parseCount(namespace, xPathEvaluator.evalNodes("/mapper/count"));
            parseAggregation(namespace, xPathEvaluator.evalNodes("/mapper/aggregation"));
        }
    }

    private void parseQuery(String namespace, List<XNode> xNodes) {
        for (XNode xNode : xNodes) {
            doParseQuery(namespace, xNode);
        }
    }

    private void doParseQuery(String namespace, XNode xNode) {
        String id = xNode.getAttribute("id");
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException(String.format("query in namespace '%s' missing required 'id' attribute", namespace));
        }

        String index = xNode.getAttribute("index");
        if (StringUtils.isBlank(index)) {
            throw new IllegalArgumentException(String.format("query '%s.%s' missing required 'index' attribute", namespace, id));
        }

        String resultType = xNode.getAttribute("resultType");
        if (StringUtils.isBlank(resultType)) {
            throw new IllegalArgumentException(String.format("query '%s.%s' missing required 'resultType' attribute", namespace, id));
        }

        String script = xNode.getBody();
        String statementId = String.format("%s.%s", namespace, id);
        try {
            Statement statement = Statement.builder()
                    .id(statementId)
                    .index(index)
                    .resultType(Class.forName(resultType))
                    .dslScriptGenerator(new FreeMarkerDslScriptGenerator(statementId, script))
                    .statementType(Statement.StatementType.QUERY)
                    .build();
            configuration.addStatement(statementId, statement);
        } catch (Exception e) {
            throw new UnsupportedOperationException(
                    String.format("error create query statement: '%s', resultType: '%s', script: '%s'",
                            statementId, resultType, script), e);
        }
    }

    private void parseCount(String namespace, List<XNode> xNodes) {
        for (XNode xNode : xNodes) {
            doParseCount(namespace, xNode);
        }
    }

    private void doParseCount(String namespace, XNode xNode) {
        String id = xNode.getAttribute("id");
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException(String.format("count in namespace '%s' missing required 'id' attribute", namespace));
        }

        String index = xNode.getAttribute("index");
        if (StringUtils.isBlank(index)) {
            throw new IllegalArgumentException(String.format("count '%s.%s' missing required 'index' attribute", namespace, id));
        }

        String resultType = xNode.getAttribute("resultType");
        if (StringUtils.isBlank(resultType)) {
            throw new IllegalArgumentException(String.format("count '%s.%s' missing required 'resultType' attribute", namespace, id));
        }

        String script = xNode.getBody();
        String statementId = String.format("%s.%s", namespace, id);
        try {
            Statement statement = Statement.builder()
                    .id(statementId)
                    .index(index)
                    .resultType(Class.forName(resultType))
                    .dslScriptGenerator(new FreeMarkerDslScriptGenerator(statementId, script))
                    .statementType(Statement.StatementType.COUNT)
                    .build();
            configuration.addStatement(statementId, statement);
        } catch (Exception e) {
            throw new UnsupportedOperationException(
                    String.format("error create count statement: '%s', resultType: '%s', script: '%s'", statementId, resultType, script), e);
        }
    }

    private void parseAggregation(String namespace, List<XNode> xNodes) {
        for (XNode xNode : xNodes) {
            doParseAggregation(namespace, xNode);
        }
    }

    private void doParseAggregation(String namespace, XNode xNode) {
        String id = xNode.getAttribute("id");
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException(String.format("aggregation in namespace '%s' missing required 'id' attribute", namespace));
        }

        String index = xNode.getAttribute("index");
        if (StringUtils.isBlank(index)) {
            throw new IllegalArgumentException(String.format("aggregation '%s.%s' missing required 'index' attribute", namespace, id));
        }

        String script = xNode.getBody();
        String statementId = String.format("%s.%s", namespace, id);
        try {
            Statement statement = Statement.builder()
                    .id(statementId)
                    .index(index)
                    .dslScriptGenerator(new FreeMarkerDslScriptGenerator(statementId, script))
                    .statementType(Statement.StatementType.AGGREGATION)
                    .build();
            configuration.addStatement(statementId, statement);
        } catch (Exception e) {
            throw new UnsupportedOperationException(
                    String.format("error create aggregation statement: '%s', script: '%s'", statementId, script), e);
        }
    }

}

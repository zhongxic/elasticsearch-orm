package org.example.elasticsearch.orm.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.util.ArrayList;
import java.util.List;

public class XPathEvaluator {

    private final Document document;

    private final XPath xPath;

    public XPathEvaluator(Document document) {
        this.document = document;
        this.xPath = XPathFactory.newInstance().newXPath();
    }

    public XNode evalNode(String expression) {
        Node node = (Node) evaluate(expression, this.document, XPathConstants.NODE);
        if (node == null) {
            return null;
        }
        return new XNode(node);
    }

    public List<XNode> evalNodes(String expression) {
        List<XNode> nodes = new ArrayList<>();
        NodeList nodeList = (NodeList) evaluate(expression, this.document, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
            nodes.add(new XNode(nodeList.item(i)));
        }
        return nodes;
    }

    private Object evaluate(String expression, Object root, QName returnType) {
        try {
            return this.xPath.evaluate(expression, root, returnType);
        } catch (Exception e) {
            throw new UnsupportedOperationException(String.format("xpath evaluate expression '%s' error: ", expression), e);
        }
    }

}

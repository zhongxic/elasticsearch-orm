package org.example.elasticsearch.orm.xml;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.CharacterData;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Properties;

@Getter
public class XNode {

    private final Node node;

    private final String body;

    private final Properties attributes;

    public XNode(Node node) {
        this.node = node;
        this.attributes = parseAttributes(node);
        this.body = parseBody(node);
    }

    private Properties parseAttributes(Node node) {
        Properties properties = new Properties();
        NamedNodeMap nodeAttributes = node.getAttributes();
        if (nodeAttributes != null) {
            for (int i = 0; i < nodeAttributes.getLength(); i++) {
                properties.put(nodeAttributes.item(i).getNodeName(), nodeAttributes.item(i).getNodeValue());
            }
        }
        return properties;
    }

    private String parseBody(Node node) {
        String data = getBodyData(node);
        if (data == null) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                data = getBodyData(child);
                if (data != null) {
                    break;
                }
            }
        }
        return data;
    }

    private String getBodyData(Node child) {
        if (child.getNodeType() == Node.CDATA_SECTION_NODE || child.getNodeType() == Node.TEXT_NODE) {
            String data = ((CharacterData) child).getData().replace("\n", "");
            if (StringUtils.isBlank(data)) {
                return null;
            }
            return data;
        }
        return null;
    }

    public String getAttribute(String id) {
        return this.attributes.getProperty(id);
    }

}

package org.gskbyte.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtils
{
public static Element firstChildWithName(Element element, String name)
{
    NodeList list = element.getElementsByTagName(name);
    if(list.getLength()>0) {
        Node n = list.item(0);
        if(n.getNodeType() == Node.ELEMENT_NODE)
            return (Element)n;
    }
    
    return null;
}

public static String valueOfFirstChildWithName(Element element, String name)
{
    Element e = firstChildWithName(element, name);
    if(e != null) {
        return e.getFirstChild().getNodeValue();
    }
    
    return null;
}

public static String attributeOfFirstChildWithName(Element element, String name, String attribute)
{
    Element e = firstChildWithName(element, name);
    if(element != null) {
        return e.getAttribute(attribute);
    }
    
    return null;
}

}

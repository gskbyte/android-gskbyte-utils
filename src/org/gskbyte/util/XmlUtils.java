package org.gskbyte.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtils
{
    public static Element firstChildWithName(Element e, String name)
    {
        NodeList list = e.getElementsByTagName(name);
        for(int i=0; i<list.getLength(); ++i) {
            Node n = list.item(i);
            if(n.getNodeType() == Node.ELEMENT_NODE) {
                return (Element)n;
            }
        }
        
        return null;
    }
    
    public static String valueOfFirstChildWithName(Element e, String name)
    {
        Element se = firstChildWithName(e, name);
        if(se != null) {
            return se.getFirstChild().getNodeValue();
        }
        
        return "";
    }
}

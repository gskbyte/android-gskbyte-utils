/*******************************************************************************
 * Copyright (c) 2013 Jose Alcalá Correa.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * 
 * Contributors:
 *     Jose Alcalá Correa - initial API and implementation
 ******************************************************************************/
package org.gskbyte.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @deprecated Do not use this, use jDOM from now on.
 * 
 * This class contains some static auxiliary methods to be used with the DOM
 * Java implementation.
 * 
 * The provided Java DOM implementation is a little bit shitty. Well, let's say
 * it is just unconfortable to use and lacks a lot of methods which I have
 * uglily implemented here.
 * 
 * A better solution would be to implement my own DOM parser or finding a good
 * one. This is why this class will remain undocumented.
 * 
 * 
 * */
public class XmlUtils
{

public static List<Element> childrenWithName(Element e, String name)
{
    final List<Element> ret = new ArrayList<Element>();
    
    NodeList list = e.getChildNodes();
    final int length = list.getLength();
    for(int i=0; i<length; ++i) {
        Node n = list.item(i);
        if(n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(name))
            ret.add( (Element)n );
    }
    
    return ret;
}

public static Element firstChildWithName(Element element, String name)
{
    NodeList list = element.getChildNodes();
    final int length = list.getLength();
    for(int i=0; i<length; ++i) {
        Node n = list.item(i);
        if(n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals(name))
            return (Element)n;
    }
    
    return null;
}
public static Element firstDescendantWithName(Element element, String name)
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
        Node firstChild = e.getFirstChild();
        if(firstChild != null)
            return firstChild.getNodeValue();
    }
    
    return null;
}

public static String valueOfFirstDescendantWithName(Element element, String name)
{
    Element e = firstDescendantWithName(element, name);
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
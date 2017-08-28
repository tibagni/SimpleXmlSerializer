package com.tiagobagni.simplexmlserializer.sampleobjects;

import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlClass;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObject;

@XmlClass
public class NestedXmlObject2 {
    @XmlObject public NestedXmlObject nested;
    @XmlObject public SimpleXmlObject simple;
    @XmlObject public SimpleListXmlObject list;
    @XmlObject public SimpleXmlMultipleObjects multiple;
}

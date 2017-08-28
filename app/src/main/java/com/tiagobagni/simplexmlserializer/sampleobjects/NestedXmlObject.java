package com.tiagobagni.simplexmlserializer.sampleobjects;

import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlClass;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlField;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObject;

@XmlClass
public class NestedXmlObject {
    @XmlField public int primitiveVal1;
    @XmlField public boolean primitiveVal2;
    @XmlField public String stringPrimitiveVal;
    @XmlObject public InnerXmlObject innerObject;

    @XmlClass
    public static class InnerXmlObject {
        @XmlField public String stringVal;
    }
}

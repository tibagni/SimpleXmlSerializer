package com.tiagobagni.simplexmlserializer.sampleobjects;

import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlClass;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlField;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObjectList;

import java.util.List;

@XmlClass
public class SimpleListXmlObject {
    @XmlField public int primitiveValue;
    @XmlObjectList public List<Item> items;

    @XmlClass
    public static class Item {
        @XmlField public int value;
    }
}

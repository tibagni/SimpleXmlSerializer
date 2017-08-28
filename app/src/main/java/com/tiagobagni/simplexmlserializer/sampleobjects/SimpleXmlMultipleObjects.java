package com.tiagobagni.simplexmlserializer.sampleobjects;

import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlClass;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlField;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObjects;

import java.util.List;

@XmlClass
public class SimpleXmlMultipleObjects {
    @XmlField public int primitiveValue;
    @XmlObjects("item") public List<Item> items;

    @XmlClass
    public static class Item {
        @XmlField public int value;
    }
}

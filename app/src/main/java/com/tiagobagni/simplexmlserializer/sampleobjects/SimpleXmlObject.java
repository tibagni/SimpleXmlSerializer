package com.tiagobagni.simplexmlserializer.sampleobjects;

import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlClass;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlField;

@XmlClass("simple")
public class SimpleXmlObject {
    @XmlField("int") public int intVal;
    @XmlField("long") public long longVal;
    @XmlField("double") public double doubleVal;
    @XmlField("float") public float floatVal;
    @XmlField("boolean") public boolean booleanVal;
    @XmlField("string") public String stringVal;
}

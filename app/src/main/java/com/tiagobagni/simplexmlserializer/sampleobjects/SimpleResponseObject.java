package com.tiagobagni.simplexmlserializer.sampleobjects;

import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlClass;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlField;

@XmlClass
public class SimpleResponseObject {
    @XmlField public String response;

    @Override
    public String toString() {
        return "SimpleResponseObject{" +
                "response='" + response + '\'' +
                '}';
    }
}


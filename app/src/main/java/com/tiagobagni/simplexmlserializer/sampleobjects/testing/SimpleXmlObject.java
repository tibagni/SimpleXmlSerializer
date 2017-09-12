package com.tiagobagni.simplexmlserializer.sampleobjects.testing;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleXmlObject that = (SimpleXmlObject) o;

        if (intVal != that.intVal) return false;
        if (longVal != that.longVal) return false;
        if (Double.compare(that.doubleVal, doubleVal) != 0) return false;
        if (Float.compare(that.floatVal, floatVal) != 0) return false;
        if (booleanVal != that.booleanVal) return false;
        return stringVal != null ? stringVal.equals(that.stringVal) : that.stringVal == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = intVal;
        result = 31 * result + (int) (longVal ^ (longVal >>> 32));
        temp = Double.doubleToLongBits(doubleVal);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (floatVal != +0.0f ? Float.floatToIntBits(floatVal) : 0);
        result = 31 * result + (booleanVal ? 1 : 0);
        result = 31 * result + (stringVal != null ? stringVal.hashCode() : 0);
        return result;
    }
}

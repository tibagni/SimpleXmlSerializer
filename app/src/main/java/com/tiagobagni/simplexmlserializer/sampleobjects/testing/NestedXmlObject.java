package com.tiagobagni.simplexmlserializer.sampleobjects.testing;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            InnerXmlObject that = (InnerXmlObject) o;

            return stringVal != null ? stringVal.equals(that.stringVal) : that.stringVal == null;
        }

        @Override
        public int hashCode() {
            return stringVal != null ? stringVal.hashCode() : 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NestedXmlObject that = (NestedXmlObject) o;

        if (primitiveVal1 != that.primitiveVal1) return false;
        if (primitiveVal2 != that.primitiveVal2) return false;
        if (stringPrimitiveVal != null ? !stringPrimitiveVal.equals(that.stringPrimitiveVal) : that.stringPrimitiveVal != null)
            return false;
        return innerObject != null ? innerObject.equals(that.innerObject) : that.innerObject == null;
    }

    @Override
    public int hashCode() {
        int result = primitiveVal1;
        result = 31 * result + (primitiveVal2 ? 1 : 0);
        result = 31 * result + (stringPrimitiveVal != null ? stringPrimitiveVal.hashCode() : 0);
        result = 31 * result + (innerObject != null ? innerObject.hashCode() : 0);
        return result;
    }
}

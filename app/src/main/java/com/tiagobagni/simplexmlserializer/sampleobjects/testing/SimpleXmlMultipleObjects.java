package com.tiagobagni.simplexmlserializer.sampleobjects.testing;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Item item = (Item) o;

            return value == item.value;
        }

        @Override
        public int hashCode() {
            return value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleXmlMultipleObjects that = (SimpleXmlMultipleObjects) o;

        if (primitiveValue != that.primitiveValue) return false;
        return items != null ? items.equals(that.items) : that.items == null;
    }

    @Override
    public int hashCode() {
        int result = primitiveValue;
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }
}

package com.tiagobagni.simplexmlserializer;

import com.tiagobagni.simplexmlserializer.sampleobjects.NestedXmlObject;
import com.tiagobagni.simplexmlserializer.sampleobjects.SimpleListXmlObject;
import com.tiagobagni.simplexmlserializer.sampleobjects.SimpleXmlMultipleObjects;
import com.tiagobagni.simplexmlserializer.sampleobjects.SimpleXmlObject;
import com.tiagobagni.simplexmlserializerlib.xml.SimpleXmlParams;
import com.tiagobagni.simplexmlserializerlib.xml.XmlSerializer;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * The {@link XmlSerializer} does not depend on any class from Android Framework
 * So, we can test it using the JVM on the host machine. No need to test on a device/emulator
 */
public class SerializerTests {

    XmlSerializer serializer;

    @Before
    public void setUp() {
        SimpleXmlParams.get().setDebugMode(false);
        SimpleXmlParams.get().indentOutput(false);
        serializer = new XmlSerializer();
    }

    @Test
    public void test_simpleXmlObjectAllValues() throws IllegalAccessException {
        SimpleXmlObject obj = new SimpleXmlObject();
        obj.stringVal = "My String";
        obj.booleanVal = true;
        obj.floatVal = 7.2f;
        obj.doubleVal = Double.MAX_VALUE;
        obj.longVal = Long.MAX_VALUE;
        obj.intVal = 85;

        String expected = "<?xml version=\"1.0\"?><simple>" +
                "<string>My String</string>" +
                "<boolean>true</boolean>" +
                "<float>7.2</float>" +
                "<double>" + Double.MAX_VALUE + "</double>" +
                "<long>" + Long.MAX_VALUE + "</long>" +
                "<int>85</int>" +
                "</simple>";

        String actual = serializer.serialize(obj);
        assertEquals(expected, actual);
    }

    @Test
    public void test_simpleXmlObjectDefaultValues() throws IllegalAccessException {
        SimpleXmlObject obj = new SimpleXmlObject();

        String expected = "<?xml version=\"1.0\"?><simple>" +
                "<string></string>" +
                "<boolean>false</boolean>" +
                "<float>0.0</float>" +
                "<double>0.0</double>" +
                "<long>0</long>" +
                "<int>0</int>" +
                "</simple>";

        String actual = serializer.serialize(obj);
        assertEquals(expected, actual);
    }
    @Test
    public void test_simpleXmlObjectNegativeValues() throws IllegalAccessException {
        SimpleXmlObject obj = new SimpleXmlObject();
        obj.stringVal = "";
        obj.booleanVal = false;
        obj.floatVal = -7.2f;
        obj.doubleVal = Double.MIN_VALUE;
        obj.longVal = Long.MIN_VALUE;
        obj.intVal = -85;

        String expected = "<?xml version=\"1.0\"?><simple>" +
                "<string></string>" +
                "<boolean>false</boolean>" +
                "<float>-7.2</float>" +
                "<double>" + Double.MIN_VALUE + "</double>" +
                "<long>" + Long.MIN_VALUE + "</long>" +
                "<int>-85</int>" +
                "</simple>";

        String actual = serializer.serialize(obj);
        assertEquals(expected, actual);
    }

    @Test
    public void test_nestedXmlObjectAllValues() throws IllegalAccessException {
        NestedXmlObject.InnerXmlObject inner = new NestedXmlObject.InnerXmlObject();
        inner.stringVal = "Inner String Val";

        NestedXmlObject obj = new NestedXmlObject();
        obj.stringPrimitiveVal = "My String Value";
        obj.primitiveVal2 = true;
        obj.primitiveVal1 = 10;
        obj.innerObject = inner;

        String expected = "<?xml version=\"1.0\"?><NestedXmlObject>" +
                "<stringPrimitiveVal>My String Value</stringPrimitiveVal>" +
                "<primitiveVal2>true</primitiveVal2>" +
                "<primitiveVal1>10</primitiveVal1>" +
                "<innerObject><stringVal>Inner String Val</stringVal></innerObject>" +
                "</NestedXmlObject>";

        String actual = serializer.serialize(obj);
        assertEquals(expected, actual);
    }

    @Test
    public void test_nestedXmlObjectDefaultValues() throws IllegalAccessException {
        NestedXmlObject obj = new NestedXmlObject();
        obj.innerObject = new NestedXmlObject.InnerXmlObject();

        String expected = "<?xml version=\"1.0\"?><NestedXmlObject>" +
                "<stringPrimitiveVal></stringPrimitiveVal>" +
                "<primitiveVal2>false</primitiveVal2>" +
                "<primitiveVal1>0</primitiveVal1>" +
                "<innerObject><stringVal></stringVal></innerObject>" +
                "</NestedXmlObject>";

        String actual = serializer.serialize(obj);
        assertEquals(expected, actual);
    }

    @Test
    public void test_nestedXmlObjectDefaultValuesNullInner() throws IllegalAccessException {
        NestedXmlObject obj = new NestedXmlObject();

        String expected = "<?xml version=\"1.0\"?><NestedXmlObject>" +
                "<stringPrimitiveVal></stringPrimitiveVal>" +
                "<primitiveVal2>false</primitiveVal2>" +
                "<primitiveVal1>0</primitiveVal1>" +
                "</NestedXmlObject>";

        String actual = serializer.serialize(obj);
        assertEquals(expected, actual);
    }

    @Test
    public void test_simpleListXmlObjectAllValues() throws IllegalAccessException {
        SimpleListXmlObject obj = new SimpleListXmlObject();
        obj.primitiveValue = 100;
        obj.items = new ArrayList<>();

        SimpleListXmlObject.Item item = new SimpleListXmlObject.Item();
        item.value = 10;

        obj.items.add(item);
        obj.items.add(item);
        obj.items.add(item);

        item = new SimpleListXmlObject.Item();
        item.value = 15;

        obj.items.add(item);
        obj.items.add(item);
        obj.items.add(item);

        String expected = "<?xml version=\"1.0\"?><SimpleListXmlObject>" +
                "<primitiveValue>100</primitiveValue>" +
                "<items>" +
                "<Item><value>10</value></Item>" +
                "<Item><value>10</value></Item>" +
                "<Item><value>10</value></Item>" +
                "<Item><value>15</value></Item>" +
                "<Item><value>15</value></Item>" +
                "<Item><value>15</value></Item>" +
                "</items>" +
                "</SimpleListXmlObject>";

        String actual = serializer.serialize(obj);
        assertEquals(expected, actual);
    }

    @Test
    public void test_simpleListXmlObjectDefaultValues() throws IllegalAccessException {
        SimpleListXmlObject obj = new SimpleListXmlObject();
        obj.items = new ArrayList<>();
        obj.items.add(new SimpleListXmlObject.Item());

        String expected = "<?xml version=\"1.0\"?><SimpleListXmlObject>" +
                "<primitiveValue>0</primitiveValue>" +
                "<items>" +
                "<Item><value>0</value></Item>" +
                "</items>" +
                "</SimpleListXmlObject>";

        String actual = serializer.serialize(obj);
        assertEquals(expected, actual);
    }

    @Test
    public void test_simpleListXmlObjectDefaultValuesEmptyList() throws IllegalAccessException {
        SimpleListXmlObject obj = new SimpleListXmlObject();
        obj.items = new ArrayList<>();

        String expected = "<?xml version=\"1.0\"?><SimpleListXmlObject>" +
                "<primitiveValue>0</primitiveValue>" +
                "<items>" +
                "</items>" +
                "</SimpleListXmlObject>";

        String actual = serializer.serialize(obj);
        assertEquals(expected, actual);
    }

    @Test
    public void test_simpleListXmlObjectDefaultValuesNullList() throws IllegalAccessException {
        SimpleListXmlObject obj = new SimpleListXmlObject();

        String expected = "<?xml version=\"1.0\"?><SimpleListXmlObject>" +
                "<primitiveValue>0</primitiveValue>" +
                "</SimpleListXmlObject>";

        String actual = serializer.serialize(obj);
        assertEquals(expected, actual);
    }

    @Test
    public void test_simpleXmlMultipleObjectsAllValues() throws IllegalAccessException {
        SimpleXmlMultipleObjects obj = new SimpleXmlMultipleObjects();
        obj.primitiveValue = 1000;
        obj.items = new ArrayList<>();

        SimpleXmlMultipleObjects.Item item = new SimpleXmlMultipleObjects.Item();
        item.value = 100;

        obj.items.add(item);
        obj.items.add(item);
        obj.items.add(item);

        item = new SimpleXmlMultipleObjects.Item();
        item.value = 150;

        obj.items.add(item);
        obj.items.add(item);
        obj.items.add(item);

        String expected = "<?xml version=\"1.0\"?><SimpleXmlMultipleObjects>" +
                "<primitiveValue>1000</primitiveValue>" +
                "<item><value>100</value></item>" +
                "<item><value>100</value></item>" +
                "<item><value>100</value></item>" +
                "<item><value>150</value></item>" +
                "<item><value>150</value></item>" +
                "<item><value>150</value></item>" +
                "</SimpleXmlMultipleObjects>";

        String actual = serializer.serialize(obj);
        assertEquals(expected, actual);
    }

    @Test
    public void test_simpleXmlMultipleObjectsDefaultValues() throws IllegalAccessException {
        SimpleXmlMultipleObjects obj = new SimpleXmlMultipleObjects();
        obj.items = new ArrayList<>();
        obj.items.add(new SimpleXmlMultipleObjects.Item());

        String expected = "<?xml version=\"1.0\"?><SimpleXmlMultipleObjects>" +
                "<primitiveValue>0</primitiveValue>" +
                "<item><value>0</value></item>" +
                "</SimpleXmlMultipleObjects>";

        String actual = serializer.serialize(obj);
        assertEquals(expected, actual);
    }

    @Test
    public void test_simpleXmlMultipleObjectsDefaultValuesEmptyObjects() throws IllegalAccessException {
        SimpleXmlMultipleObjects obj = new SimpleXmlMultipleObjects();
        obj.items = new ArrayList<>();

        String expected = "<?xml version=\"1.0\"?><SimpleXmlMultipleObjects>" +
                "<primitiveValue>0</primitiveValue>" +
                "</SimpleXmlMultipleObjects>";

        String actual = serializer.serialize(obj);
        assertEquals(expected, actual);
    }

    @Test
    public void test_simpleXmlMultipleObjectsDefaultValuesNullObjects() throws IllegalAccessException {
        SimpleXmlMultipleObjects obj = new SimpleXmlMultipleObjects();

        String expected = "<?xml version=\"1.0\"?><SimpleXmlMultipleObjects>" +
                "<primitiveValue>0</primitiveValue>" +
                "</SimpleXmlMultipleObjects>";

        String actual = serializer.serialize(obj);
        assertEquals(expected, actual);
    }
}
package com.tiagobagni.simplexmlserializer;

import android.support.test.runner.AndroidJUnit4;

import com.tiagobagni.simplexmlserializer.sampleobjects.testing.NestedXmlObject;
import com.tiagobagni.simplexmlserializer.sampleobjects.testing.SimpleListXmlObject;
import com.tiagobagni.simplexmlserializer.sampleobjects.testing.SimpleXmlMultipleObjects;
import com.tiagobagni.simplexmlserializer.sampleobjects.testing.SimpleXmlObject;
import com.tiagobagni.simplexmlserializerlib.xml.SimpleXmlParams;
import com.tiagobagni.simplexmlserializerlib.xml.XmlDeserializationException;
import com.tiagobagni.simplexmlserializerlib.xml.XmlDeserializer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * The {@link XmlDeserializer} relies on {@link org.xmlpull.v1.XmlPullParser} which is available
 * on Android Framework. So, in order to properly test it, it should be run on Android
 * on a device/emulator
 */
@RunWith(AndroidJUnit4.class)
public class DeserializerTests {

    @Before
    public void setUp() {
        SimpleXmlParams.get().setDebugMode(false);
    }

    @Test
    public void test_simpleXmlObjectAllValues() throws XmlDeserializationException {
        XmlDeserializer deserializer = new XmlDeserializer(SimpleXmlObject.class);
        SimpleXmlObject actual = (SimpleXmlObject) deserializer.deserialize(
                "<?xml version=\"1.0\"?><simple>" +
                        "<string>My String</string>" +
                        "<boolean>true</boolean>" +
                        "<float>" + Float.MAX_VALUE + "</float>" +
                        "<double>" + Double.MAX_VALUE + "</double>" +
                        "<long>" + Long.MAX_VALUE + "</long>" +
                        "<int>" + Integer.MAX_VALUE + "</int>" +
                        "</simple>");

        SimpleXmlObject expected = new SimpleXmlObject();
        expected.stringVal = "My String";
        expected.booleanVal = true;
        expected.floatVal = Float.MAX_VALUE;
        expected.doubleVal = Double.MAX_VALUE;
        expected.longVal = Long.MAX_VALUE;
        expected.intVal = Integer.MAX_VALUE;

        assertEquals(expected, actual);
    }

    @Test
    public void test_simpleXmlObjectSomeValues() throws XmlDeserializationException {
        XmlDeserializer deserializer = new XmlDeserializer(SimpleXmlObject.class);
        SimpleXmlObject actual = (SimpleXmlObject) deserializer.deserialize(
                "<?xml version=\"1.0\"?><simple>" +
                        "<boolean>true</boolean>" +
                        "<float>" + Float.MAX_VALUE + "</float>" +
                        "<long>" + Long.MAX_VALUE + "</long>" +
                        "</simple>");

        SimpleXmlObject expected = new SimpleXmlObject();
        expected.booleanVal = true;
        expected.floatVal = Float.MAX_VALUE;
        expected.longVal = Long.MAX_VALUE;

        assertEquals(expected, actual);
    }

    @Test
    public void test_nestedXmlObjectAllValues() throws XmlDeserializationException {
        XmlDeserializer deserializer = new XmlDeserializer(NestedXmlObject.class);
        NestedXmlObject actual = (NestedXmlObject) deserializer.deserialize(
                "<?xml version=\"1.0\"?><NestedXmlObject>" +
                        "<stringPrimitiveVal>My String Value</stringPrimitiveVal>" +
                        "<primitiveVal2>true</primitiveVal2>" +
                        "<primitiveVal1>10</primitiveVal1>" +
                        "<innerObject><stringVal>Inner String Val</stringVal></innerObject>" +
                        "</NestedXmlObject>");

        NestedXmlObject.InnerXmlObject inner = new NestedXmlObject.InnerXmlObject();
        inner.stringVal = "Inner String Val";

        NestedXmlObject expected = new NestedXmlObject();
        expected.stringPrimitiveVal = "My String Value";
        expected.primitiveVal2 = true;
        expected.primitiveVal1 = 10;
        expected.innerObject = inner;

        assertEquals(expected, actual);
    }

    @Test
    public void test_simpleListXmlObjectAllValues() throws IllegalAccessException, XmlDeserializationException {
        XmlDeserializer deserializer = new XmlDeserializer(SimpleListXmlObject.class);

        SimpleListXmlObject actual = (SimpleListXmlObject) deserializer.deserialize(
                "<?xml version=\"1.0\"?><SimpleListXmlObject>" +
                "<primitiveValue>100</primitiveValue>" +
                "<items>" +
                "<Item><value>10</value></Item>" +
                "<Item><value>10</value></Item>" +
                "<Item><value>10</value></Item>" +
                "<Item><value>15</value></Item>" +
                "<Item><value>15</value></Item>" +
                "<Item><value>15</value></Item>" +
                "</items>" +
                "</SimpleListXmlObject>");

        SimpleListXmlObject expected = new SimpleListXmlObject();
        expected.primitiveValue = 100;
        expected.items = new ArrayList<>();

        SimpleListXmlObject.Item item = new SimpleListXmlObject.Item();
        item.value = 10;

        expected.items.add(item);
        expected.items.add(item);
        expected.items.add(item);

        item = new SimpleListXmlObject.Item();
        item.value = 15;

        expected.items.add(item);
        expected.items.add(item);
        expected.items.add(item);

        assertEquals(expected, actual);
    }

    @Test
    public void test_simpleListXmlObjectSomeValues() throws IllegalAccessException, XmlDeserializationException {
        XmlDeserializer deserializer = new XmlDeserializer(SimpleListXmlObject.class);

        SimpleListXmlObject actual = (SimpleListXmlObject) deserializer.deserialize(
                "<?xml version=\"1.0\"?><SimpleListXmlObject>" +
                        "<items>" +
                        "<Item><value>10</value></Item>" +
                        "<Item><value>10</value></Item>" +
                        "<Item><value>10</value></Item>" +
                        "<Item><value>15</value></Item>" +
                        "<Item><value>15</value></Item>" +
                        "<Item><value>15</value></Item>" +
                        "</items>" +
                        "</SimpleListXmlObject>");

        SimpleListXmlObject expected = new SimpleListXmlObject();
        expected.items = new ArrayList<>();

        SimpleListXmlObject.Item item = new SimpleListXmlObject.Item();
        item.value = 10;

        expected.items.add(item);
        expected.items.add(item);
        expected.items.add(item);

        item = new SimpleListXmlObject.Item();
        item.value = 15;

        expected.items.add(item);
        expected.items.add(item);
        expected.items.add(item);

        assertEquals(expected, actual);
    }

    @Test
    public void test_simpleXmlMultipleObjectsAllValues() throws IllegalAccessException, XmlDeserializationException {
        XmlDeserializer deserializer = new XmlDeserializer(SimpleXmlMultipleObjects.class);

        SimpleXmlMultipleObjects actual = (SimpleXmlMultipleObjects) deserializer.deserialize(
                "<?xml version=\"1.0\"?><SimpleXmlMultipleObjects>" +
                "<primitiveValue>1000</primitiveValue>" +
                "<item><value>100</value></item>" +
                "<item><value>100</value></item>" +
                "<item><value>100</value></item>" +
                "<item><value>150</value></item>" +
                "<item><value>150</value></item>" +
                "<item><value>150</value></item>" +
                "</SimpleXmlMultipleObjects>");

        SimpleXmlMultipleObjects expected = new SimpleXmlMultipleObjects();
        expected.primitiveValue = 1000;
        expected.items = new ArrayList<>();

        SimpleXmlMultipleObjects.Item item = new SimpleXmlMultipleObjects.Item();
        item.value = 100;

        expected.items.add(item);
        expected.items.add(item);
        expected.items.add(item);

        item = new SimpleXmlMultipleObjects.Item();
        item.value = 150;

        expected.items.add(item);
        expected.items.add(item);
        expected.items.add(item);

        assertEquals(expected, actual);
    }

    @Test
    public void test_simpleXmlMultipleObjectsSomeValues() throws IllegalAccessException, XmlDeserializationException {
        XmlDeserializer deserializer = new XmlDeserializer(SimpleXmlMultipleObjects.class);

        SimpleXmlMultipleObjects actual = (SimpleXmlMultipleObjects) deserializer.deserialize(
                "<?xml version=\"1.0\"?><SimpleXmlMultipleObjects>" +
                        "<item><value>100</value></item>" +
                        "<item><value>100</value></item>" +
                        "<item><value>100</value></item>" +
                        "<item><value>150</value></item>" +
                        "<item><value>150</value></item>" +
                        "<item><value>150</value></item>" +
                        "</SimpleXmlMultipleObjects>");

        SimpleXmlMultipleObjects expected = new SimpleXmlMultipleObjects();
        expected.items = new ArrayList<>();

        SimpleXmlMultipleObjects.Item item = new SimpleXmlMultipleObjects.Item();
        item.value = 100;

        expected.items.add(item);
        expected.items.add(item);
        expected.items.add(item);

        item = new SimpleXmlMultipleObjects.Item();
        item.value = 150;

        expected.items.add(item);
        expected.items.add(item);
        expected.items.add(item);

        assertEquals(expected, actual);
    }

    @Test(expected = XmlDeserializationException.class)
    public void test_simpleXmlObjectBadValues() throws XmlDeserializationException {
        XmlDeserializer deserializer = new XmlDeserializer(SimpleXmlObject.class);
        deserializer.deserialize(
                "<?xml version=\"1.0\"?><simple>" +
                        "<string>My String</string>" +
                        "<boolean>true</boolean>" +
                        "<float>d</float>" +
                        "</simple>");
    }

    @Test(expected = XmlDeserializationException.class)
    public void test_simpleXmlObjectBadValues2() throws XmlDeserializationException {
        XmlDeserializer deserializer = new XmlDeserializer(SimpleXmlObject.class);
        deserializer.deserialize(
                "<?xml version=\"1.0\"?><simple>" +
                        "<string>My String</string>" +
                        "<boolean>true</boolean>" +
                        "<double>w</double>" +
                        "</simple>");
    }

    @Test(expected = XmlDeserializationException.class)
    public void test_simpleXmlObjectBadValues3() throws XmlDeserializationException {
        XmlDeserializer deserializer = new XmlDeserializer(SimpleXmlObject.class);
        deserializer.deserialize(
                "<?xml version=\"1.0\"?><simple>" +
                        "<string>My String</string>" +
                        "<boolean>true</boolean>" +
                        "<long>3.1</long>" +
                        "</simple>");
    }

    @Test(expected = XmlDeserializationException.class)
    public void test_simpleXmlObjectBadValues4() throws XmlDeserializationException {
        XmlDeserializer deserializer = new XmlDeserializer(SimpleXmlObject.class);
        deserializer.deserialize(
                "<?xml version=\"1.0\"?><simple>" +
                        "<string>My String</string>" +
                        "<boolean>true</boolean>" +
                        "<int>3.3</int>" +
                        "</simple>");
    }

    @Test
    public void test_simpleXmlObjectBadBoolean() throws XmlDeserializationException {
        XmlDeserializer deserializer = new XmlDeserializer(SimpleXmlObject.class);
        SimpleXmlObject actual = (SimpleXmlObject) deserializer.deserialize(
                "<?xml version=\"1.0\"?><simple>" +
                        "<boolean>truuuuue</boolean>" +
                        "</simple>");

        SimpleXmlObject expected = new SimpleXmlObject();
        expected.booleanVal = false;

        assertEquals(expected, actual);
    }

    @Test
    public void test_simpleXmlObjectBadBooleanAllCaps() throws XmlDeserializationException {
        XmlDeserializer deserializer = new XmlDeserializer(SimpleXmlObject.class);
        SimpleXmlObject actual = (SimpleXmlObject) deserializer.deserialize(
                "<?xml version=\"1.0\"?><simple>" +
                        "<boolean>TRUE</boolean>" +
                        "</simple>");

        SimpleXmlObject expected = new SimpleXmlObject();
        expected.booleanVal = true;

        assertEquals(expected, actual);
    }

    @Test
    public void test_simpleXmlObjectBadBooleanFirstLetterCaps() throws XmlDeserializationException {
        XmlDeserializer deserializer = new XmlDeserializer(SimpleXmlObject.class);
        SimpleXmlObject actual = (SimpleXmlObject) deserializer.deserialize(
                "<?xml version=\"1.0\"?><simple>" +
                        "<boolean>True</boolean>" +
                        "</simple>");

        SimpleXmlObject expected = new SimpleXmlObject();
        expected.booleanVal = true;

        assertEquals(expected, actual);
    }
}

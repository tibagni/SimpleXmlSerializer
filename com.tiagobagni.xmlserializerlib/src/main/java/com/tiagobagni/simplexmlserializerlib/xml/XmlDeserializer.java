package com.tiagobagni.simplexmlserializerlib.xml;

import android.text.TextUtils;
import android.util.Log;

import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlClass;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlField;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObject;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObjectList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Stack;

/**
 * Utility class responsible for de-serializing xml into an object annotated with
 * {@link XmlClass} into xml.
 *
 * @author  Tiago Bagni
 */
public class XmlDeserializer {
    private Object xmlObject;
    private Class xmlClass;

    private HashMap<String, FieldWrapper> xmlPropertiesMap = new HashMap<>();

    private XmlPullParser xmlPullParser;
    private Stack<String> parsingTags;

    private String lastTagToParse;

    public XmlDeserializer(Class xmlClass) {
        try {
            ensureValid(xmlClass);
            this.xmlClass = xmlClass;
            this.xmlObject = xmlClass.newInstance();
            readClassMembers();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException("It was not possible to instantiate "
                    + xmlClass, e);
        }
    }

    private void ensureValid(Class xmlClass) {
        if (xmlClass == null) {
            throw new NullPointerException("Cannot create an XmlDeserializer for a null XmlClass");
        }
        Annotation annotation = xmlClass.getAnnotation(XmlClass.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Provided Xml Class missing XmlClass annotation");
        }
    }

    private void readClassMembers() {
        Field[] fields = xmlClass.getDeclaredFields();
        for (Field field : fields) {
            Annotation fieldAnnotation = getFieldAnnotation(field);
            if (fieldAnnotation != null) {
                xmlPropertiesMap.put(getTagFor(field), new FieldWrapper(field, fieldAnnotation));
            }
        }
    }

    private Annotation getFieldAnnotation(Field field) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof XmlField ||
                    annotation instanceof XmlObject ||
                    annotation instanceof XmlObjectList) {
                return annotation;
            }
        }

        return null;
    }

    private String getTagFor(Field field) {
        Annotation annotation = field.getAnnotation(XmlField.class);
        if (annotation != null) {
            return ((XmlField) annotation).value();
        }

        annotation = field.getAnnotation(XmlObject.class);
        if (annotation != null) {
            return ((XmlObject) annotation).value();
        }

        annotation = field.getAnnotation(XmlObjectList.class);
        if (annotation != null) {
            return ((XmlObjectList) annotation).value();
        }

        throw new IllegalStateException("Called getTagFor on a field without annotation " + field);
    }

    public Object deserialize(String xml) throws XmlDeserializationException {
        initialize(new StringReader(xml));
        parse();

        return xmlObject;
    }

    private void initialize(Reader inputReader) throws XmlDeserializationException {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(inputReader);
            parsingTags = new Stack<>();
        } catch (XmlPullParserException e) {
            throw new XmlDeserializationException("It was not possible to initialize " +
                    "the Xml Parser", e);
        }
    }

    private void parse() throws XmlDeserializationException {
        try {
            int eventType = xmlPullParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        Log.d("debug", "start tag: " + xmlPullParser.getName());
                        parsingTags.push(xmlPullParser.getName());
                        break;
                    case XmlPullParser.TEXT:
                        Log.d("debug", "TEXT: " + xmlPullParser.getText());
                        handleText();
                        break;
                    case XmlPullParser.END_TAG:
                        Log.d("debug", "end tag: " + xmlPullParser.getName());
                        String closedTag = parsingTags.pop();
                        if (shouldAbortParsing(closedTag)) {
                            // End early. This happens if we are parsing a nested object.
                            return;
                        }
                        break;
                }
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException | IOException e) {
            throw new XmlDeserializationException("Possibly malformed Xml", e);
        }
    }

    private void handleText() throws XmlDeserializationException {
        String currentTag = parsingTags.peek();
        String value = xmlPullParser.getText();
        FieldWrapper fieldWrapper = xmlPropertiesMap.get(currentTag);
        if (fieldWrapper == null) {
            // Nothing to do here. We can fall here if there is an unexpected tag. That's fine,
            // probably because it is not relevant to the problem, so it was not declared in the
            // xml object.
            return;
        }

        Annotation fieldAnnotation = fieldWrapper.annotation;
        if (fieldAnnotation instanceof XmlField) {
            parsePrimitiveValue(fieldWrapper.field, value);
        } else if (fieldAnnotation instanceof XmlObject) {
            parseXmlObject(fieldWrapper.field);
        } else if (fieldAnnotation instanceof XmlObjectList) {

        } else {
            // Something went terribly wrong here...
        }
    }

    private void parsePrimitiveValue(Field field, String value) {
        if (TextUtils.isEmpty(value)) {
            // Nothing to do here. If the xml value is empty, don't try to parse it
            return;
        }
        Class cls = field.getType();
        Object parsedValue;

        if (cls == int.class || cls == Integer.class) {
            parsedValue = Integer.parseInt(value);
        } else if (cls == long.class || cls == Long.class) {
            parsedValue = Long.parseLong(value);
        } else if (cls == double.class || cls == Double.class) {
            parsedValue = Double.parseDouble(value);
        } else if (cls == float.class || cls == Float.class) {
            parsedValue = Float.parseFloat(value);
        } else if (cls == boolean.class || cls == Boolean.class) {
            parsedValue =  Boolean.parseBoolean(value);
        } else if (cls == String.class) {
            parsedValue = value;
        } else {
            throw new IllegalStateException();
        }

        Log.d("debug", "parsePrimitiveValue: " + cls + " - " + parsedValue);
        setField(field, parsedValue);
    }

    private void parseXmlObject(Field field) throws XmlDeserializationException {
        Log.d("debug", "XmlObjectFound");
        XmlDeserializer nestedDeserializer = new XmlDeserializer(field.getType());
        nestedDeserializer.lastTagToParse = parsingTags.peek(); // Only parse this object
        nestedDeserializer.xmlPullParser = xmlPullParser;
        nestedDeserializer.parsingTags = parsingTags;

        nestedDeserializer.parse();
        setField(field, nestedDeserializer.xmlObject);
    }

    private void setField(Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(xmlObject, value);
        } catch (IllegalAccessException e) {
            // This should not happen, but if we fail to set the value to the field for some reason,
            // just ignore. We will not fail everything because one field could not be set
        }
    }

    private boolean shouldAbortParsing(String tag) {
        return lastTagToParse != null && lastTagToParse.equals(tag);
    }

    private class FieldWrapper  {
        public final Field field;
        public final Annotation annotation;

        private FieldWrapper(Field field, Annotation annotation) {
            this.field = field;
            this.annotation = annotation;
        }
    }
}

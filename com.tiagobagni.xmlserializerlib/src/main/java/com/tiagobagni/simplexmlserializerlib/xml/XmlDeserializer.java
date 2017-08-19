package com.tiagobagni.simplexmlserializerlib.xml;

import android.text.TextUtils;

import com.tiagobagni.simplexmlserializerlib.XmlSerializerLogger;
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private boolean isParsingList;
    private String listTag;
    private List listOfXmlObjects;
    private Class listItemType;
    private Field listField;

    private final boolean DBG;
    private final XmlSerializerLogger LOGGER;

    public XmlDeserializer(Class xmlClass) {
        DBG = SimpleXmlParams.get().isDebugMode();
        LOGGER = SimpleXmlParams.get().getLogger();

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
                        String openTag = xmlPullParser.getName();
                        if (DBG) LOGGER.debug("Open tag: " + openTag);
                        parsingTags.push(openTag);
                        break;
                    case XmlPullParser.TEXT:
                        handleText();
                        break;
                    case XmlPullParser.END_TAG:
                        String closedTag = parsingTags.pop();
                        String endTag = xmlPullParser.getName();
                        if (!TextUtils.equals(closedTag, endTag) && DBG) {
                            if (DBG) LOGGER.error("End tag is different from the" +
                                    " top of the stack!" +
                                    " EndTag = " + endTag +
                                    " TopStack = " + closedTag);
                        }

                        if (DBG) LOGGER.debug("Close tag: " + closedTag);
                        if (isParsingList && closedTag.equals(listTag)) {
                            finishParseXmlObjectList();
                        }
                        if (shouldAbortParsing(closedTag)) {
                            // End early. This happens if we are parsing a nested object.
                            if (DBG) LOGGER.debug("End Early. " +
                                    "Finished parsing nested object");
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

    private void finishParseXmlObjectList() {
        // We finished parsing the list. Save it and cleanup!
        setField(listField, listOfXmlObjects);
        if (DBG) LOGGER.debug("Finished parsing list: " + listTag);

        isParsingList = false;
        listTag = null;
        listItemType = null;
        listOfXmlObjects = null;
        listField = null;
    }

    private void handleText() throws XmlDeserializationException {
        String currentTag = parsingTags.peek();
        String value = xmlPullParser.getText();
        FieldWrapper fieldWrapper = xmlPropertiesMap.get(currentTag);
        if (fieldWrapper == null) {
            if (isParsingList) {
                parseXmlObjectListItem();
            }

            // Basically there are two cases in which fieldWrapper can be null:
            //   1 - The xml tag doesn't have a corresponding java field (That's ok.
            //       The xml can be more complex than the actual Java object)
            //   2 - We are parsing a list. In this case, we need to parse the objects
            //       and add to the list
            // Case (2) was handled above, and case (1) we can just ignore the tag.
            // So, just return here
            return;
        }

        Annotation fieldAnnotation = fieldWrapper == null ? null : fieldWrapper.annotation;
        if (fieldAnnotation instanceof XmlField) {
            parsePrimitiveValue(fieldWrapper.field, value);
        } else if (fieldAnnotation instanceof XmlObject) {
            parseXmlObject(fieldWrapper.field);
        } else if (fieldAnnotation instanceof XmlObjectList) {
            if (!isParsingList) {
                // If we are already parsing the list, don't start it again
                startParseXmlObjectList(fieldWrapper.field);
            }
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
            throw new IllegalStateException("Trying to parse unsupported type: " + cls);
        }

        setField(field, parsedValue);
        if (DBG) LOGGER.debug("Primitive value found: " + cls + " = " + parsedValue);
    }

    private void parseXmlObject(Field field) throws XmlDeserializationException {
        Class type = field.getType();
        Object object = deserializeObject(type);
        setField(field, object);

        if (DBG) LOGGER.debug("Object found: " + type + " = " + object);
    }

    private Object deserializeObject(Class type) throws XmlDeserializationException {
        XmlDeserializer nestedDeserializer = new XmlDeserializer(type);
        nestedDeserializer.lastTagToParse = parsingTags.peek(); // Only parse this object
        nestedDeserializer.xmlPullParser = xmlPullParser;
        nestedDeserializer.parsingTags = parsingTags;

        nestedDeserializer.parse();
        return nestedDeserializer.xmlObject;
    }

    private void startParseXmlObjectList(Field field) {
        if (field.getType() != List.class) {
            throw new IllegalStateException("A List was expected for " + field.getName());
        }

        if (isParsingList) {
            throw new IllegalStateException("Trying to start parsing a list while parsing a list");
        }

        isParsingList = true;
        listOfXmlObjects = new ArrayList();
        listTag = parsingTags.peek();
        listField = field;

        // Find out the type of the list items
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Type[] arr = pType.getActualTypeArguments();
            if (arr != null && arr.length == 1) {
                listItemType = (Class) arr[0];
            }
        }

        if (listItemType == null) {
            throw new IllegalStateException("Not possible to read generic type of "
                    + field.getName());
        }

        if (DBG) LOGGER.debug("Started parsing list: " + listTag);
    }

    private void parseXmlObjectListItem() throws XmlDeserializationException {
        Object object = deserializeObject(listItemType);
        listOfXmlObjects.add(object);
        if (DBG) LOGGER.debug("Object added to list: " + listItemType + " = " + object);
    }

    private void setField(Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(xmlObject, value);
        } catch (IllegalAccessException e) {
            // This should not happen, but if we fail to set the value to the field for some reason,
            // just ignore. We will not fail everything because one field could not be set
            if (DBG) LOGGER.error("An error occurred while trying to set " +
                    "value [" + value + "] to field " + field.getName(), e);
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

package com.tiagobagni.simplexmlserializerlib.xml;

import com.tiagobagni.simplexmlserializerlib.XmlSerializerLogger;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlClass;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlField;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObject;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObjectList;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObjects;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Utility class responsible for serializing an object annotated with
 * {@link XmlClass} into xml.
 *
 * @author  Tiago Bagni
 */
public class XmlSerializer {
    private static final String NEW_LINE = "\n";
    private static final String INDENTATION = "  ";
    private static final String XML_HEADER = "<?xml version=\"1.0\"?>";

    private StringBuilder xmlBuilder;
    private String spacing;
    private String nestedSpacing;
    private int indentLevels;

    private Object xmlObject;
    private String xmlRootTag;
    private XmlSerializer nestedSerializer;

    private final boolean DBG;
    private final XmlSerializerLogger LOGGER;

    public XmlSerializer() {
        DBG = SimpleXmlParams.get().isDebugMode();
        LOGGER = SimpleXmlParams.get().getLogger();
    }

    /**
     * Serializes a Java object into its XML representation.
     *
     * @param xmlObject Object annotated with {@link XmlObject}
     * @return The XML String representing the input object
     * @throws IllegalAccessException
     */
    public String serialize(Object xmlObject) throws IllegalAccessException {
        initializeFor(xmlObject);
        writeXmlHeader();
        writeXmlBody(0);

        return xmlBuilder.toString();
    }

    private void initializeFor(Object xmlObject) {
        ensureValid(xmlObject);
        this.xmlObject = xmlObject;
        this.xmlRootTag = ReflectionUtils.getClassTag(xmlObject);
        this.xmlBuilder = new StringBuilder();
        this.nestedSerializer = null;
    }

    private void ensureValid(Object xmlObject) {
        if (xmlObject == null) {
            throw new NullPointerException("Trying to serialize a null object");
        }

        Annotation annotation = xmlObject.getClass().getAnnotation(XmlClass.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Invalid XmlClass. Missing Xml annotation");
        }
    }

    private void writeXmlHeader() {
        xmlBuilder.append(XML_HEADER);
        xmlBuilder.append(NEW_LINE);

        if (DBG) LOGGER.debug("writing xml header");
    }

    private void writeXmlBody(StringBuilder xmlBuilder, int indentLevels)
            throws IllegalAccessException {
        this.xmlBuilder = xmlBuilder;
        writeXmlBody(indentLevels);
    }

    private void writeXmlBody(int indentLevels) throws IllegalAccessException {
        setIndentation(indentLevels);
        writeOpenTag();
        writeFields();
        writeCloseTag();
    }

    private void setIndentation(int indentLevels) {
        this.indentLevels = indentLevels;
        spacing = getSpaces(indentLevels);
        nestedSpacing = getSpaces(indentLevels + 1);
    }

    private void writeOpenTag() {
        if (DBG) LOGGER.debug("writing open tag: " + xmlRootTag);
        xmlBuilder.append(spacing);
        xmlBuilder.append(open(xmlRootTag));
        xmlBuilder.append(NEW_LINE);
    }

    private void writeFields() throws IllegalAccessException {
        Field[] fields = getOrderedFields();
        for (Field field : fields) {
            Annotation annotation = ReflectionUtils.getFieldAnnotation(field);
            field.setAccessible(true);
            Object value = field.get(xmlObject);

            String tag = ReflectionUtils.getFieldTag(field, annotation);
            if (annotation instanceof XmlField) {
                writeXmlField(tag, value);
            } else if (annotation instanceof XmlObject) {
                writeXmlObject(tag, value);
            } else if (annotation instanceof XmlObjectList) {
                writeXmlList(tag, (List) value);
            } else if (annotation instanceof XmlObjects) {
                writeMultipleItems(tag, (List) value);
            } else {
                // Do not append a new line if no xml was written on this iteration
                continue;
            }
            xmlBuilder.append(NEW_LINE);
        }
    }

    private Field[] getOrderedFields() {
        Field[] fields = xmlObject.getClass().getDeclaredFields();
        Arrays.sort(fields, (first, second) -> {
            Annotation annotation1 = ReflectionUtils.getFieldAnnotation(first);
            Annotation annotation2 = ReflectionUtils.getFieldAnnotation(second);

            // XmlField has higher priority
            if (annotation1 instanceof XmlField) return -1;
            if (annotation2 instanceof XmlField) return 1;
            return 0;
        });

        return fields;
    }

    private void writeXmlField(String tag, Object value) {
        if (DBG) LOGGER.debug("writing primitive: " + tag + " = " + value);
        xmlBuilder.append(nestedSpacing);
        xmlBuilder.append(open(tag));
        xmlBuilder.append(value);
        xmlBuilder.append(close(tag));
    }

    private void writeXmlObject(String tag, Object value) throws IllegalAccessException {
        if (DBG) LOGGER.debug("writing object: " + tag + " = " + value);
        if (nestedSerializer == null) {
            nestedSerializer = new XmlSerializer();
        }

        nestedSerializer.initializeFor(value);
        // Use the defined Tag instead of the Class name for nested objects
        nestedSerializer.xmlRootTag = tag;
        nestedSerializer.writeXmlBody(xmlBuilder, indentLevels + 1);
    }

    private void writeXmlList(String tag, List items) throws IllegalAccessException {
        if (DBG) LOGGER.debug("writing list: " + tag);
        xmlBuilder.append(nestedSpacing);
        xmlBuilder.append(open(tag));
        xmlBuilder.append(NEW_LINE);

        Class itemType = (items != null && items.size() > 0) ? items.get(0).getClass() : null;
        String itemTag = itemType != null ? itemType.getSimpleName() : "";

        indentLevels++; // Indent one more level for list items
        writeMultipleItems(itemTag, items);
        indentLevels--; // After the list was written, restore indentation

        xmlBuilder.append(nestedSpacing);
        xmlBuilder.append(close(tag));
    }

    private void writeMultipleItems(String tag, List items) throws IllegalAccessException {
        for (Object item : items) {
            // For objects in a List, use the class name as tag
            writeXmlObject(tag, item);
            xmlBuilder.append(NEW_LINE);
        }
    }

    private void writeCloseTag() {
        if (DBG) LOGGER.debug("writing close tag: " + xmlRootTag);
        xmlBuilder.append(spacing);
        xmlBuilder.append(close(xmlRootTag));
    }

    private String open(String tag) {
        return "<" + tag + ">";
    }

    private String close(String tag) {
        return "</" + tag + ">";
    }

    private String getSpaces(int indentLevels) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentLevels; i++) {
            sb.append(INDENTATION);
        }
        return sb.toString();
    }
}

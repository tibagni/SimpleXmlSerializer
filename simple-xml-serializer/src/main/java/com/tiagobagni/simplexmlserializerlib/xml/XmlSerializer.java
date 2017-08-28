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
import java.util.List;

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
    private final boolean SHOULD_INDENT;
    private final XmlSerializerLogger LOGGER;

    public XmlSerializer() {
        SimpleXmlParams params = SimpleXmlParams.get();
        DBG = params.isDebugMode();
        SHOULD_INDENT = params.shouldIndentOutput();
        LOGGER = params.getLogger();
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
        content(XML_HEADER);
        newLine();

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
        indent(spacing);
        open(xmlRootTag);
        newLine();
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
            newLine();
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
        indent(nestedSpacing);
        open(tag);
        content(value == null ? "" : value);
        close(tag);
    }

    private void writeXmlObject(String tag, Object value) throws IllegalAccessException {
        if (DBG) LOGGER.debug("writing object: " + tag + " = " + value);

        if (value == null) {
            if (DBG) LOGGER.debug("Skip object because it is null");
            return;
        }

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

        if (items == null) {
            if (DBG) LOGGER.debug("Skip list because it is null");
            return;
        }

        indent(nestedSpacing);
        open(tag);
        newLine();

        Class itemType = (items != null && items.size() > 0) ? items.get(0).getClass() : null;
        String itemTag = itemType != null ? itemType.getSimpleName() : "";

        indentLevels++; // Indent one more level for list items
        writeMultipleItems(itemTag, items);
        indentLevels--; // After the list was written, restore indentation

        indent(nestedSpacing);
        close(tag);
    }

    private void writeMultipleItems(String tag, List items) throws IllegalAccessException {
        if (items == null) {
            if (DBG) LOGGER.debug("Skip multiple objects because it is null");
            return;
        }

        for (Object item : items) {
            // For objects in a List, use the class name as tag
            writeXmlObject(tag, item);
            newLine();
        }
    }

    private void writeCloseTag() {
        if (DBG) LOGGER.debug("writing close tag: " + xmlRootTag);
        indent(spacing);
        close(xmlRootTag);
    }

    private void open(String tag) {
        xmlBuilder.append("<" + tag + ">");
    }

    private void close(String tag) {
        xmlBuilder.append("</" + tag + ">");
    }

    private void content(Object value) {
        xmlBuilder.append(value);
    }

    private String getSpaces(int indentLevels) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indentLevels; i++) {
            sb.append(INDENTATION);
        }
        return sb.toString();
    }

    private void newLine() {
        if (SHOULD_INDENT) {
            xmlBuilder.append(NEW_LINE);
        }
    }

    private void indent(String spacing) {
        if (SHOULD_INDENT) {
            xmlBuilder.append(spacing);
        }
    }
}

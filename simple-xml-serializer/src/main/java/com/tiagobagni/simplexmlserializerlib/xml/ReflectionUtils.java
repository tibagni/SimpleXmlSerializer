package com.tiagobagni.simplexmlserializerlib.xml;

import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlField;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObject;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObjectList;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObjects;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/*
 * Utility class with methods to help handling reflection
 *
 * @author Tiago Bagni
 */
class ReflectionUtils {
    static Annotation getFieldAnnotation(Field field) {
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof XmlField ||
                    annotation instanceof XmlObject ||
                    annotation instanceof XmlObjectList ||
                    annotation instanceof XmlObjects) {
                return annotation;
            }
        }

        return null;
    }

    static String getFieldTag(Field field) {
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

        annotation = field.getAnnotation(XmlObjects.class);
        if (annotation != null) {
            return ((XmlObjects) annotation).value();
        }

        throw new IllegalStateException("Called getTagFor on a field without annotation " + field);
    }
}

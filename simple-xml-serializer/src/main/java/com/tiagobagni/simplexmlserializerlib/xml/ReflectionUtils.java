package com.tiagobagni.simplexmlserializerlib.xml;

import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlClass;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlField;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObject;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObjectList;
import com.tiagobagni.simplexmlserializerlib.xml.annotation.XmlObjects;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/*
 * Utility class with methods to help handling reflection
 *
 * @author Tiago Bagni
 */
class ReflectionUtils {
    static String getFieldTag(Field field) {
        Annotation annotation = getFieldAnnotation(field);
        if (annotation == null) {
            throw new IllegalStateException("Called getTagFor on a field without " +
                    "annotation " + field);
        }

        return getFieldTag(field, annotation);
    }

    static String getFieldTag(Field field, Annotation annotation) {
        try {
            Method value = annotation.getClass().getDeclaredMethod("value");
            value.setAccessible(true);
            String annotationValue = (String) value.invoke(annotation);
            return isEmpty(annotationValue) ? field.getName() : annotationValue;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("There was a problem while trying to " +
                    "read tag from " + field, e);
        }
    }

    static Annotation getFieldAnnotation(Field field) {
        Annotation annotation = field.getAnnotation(XmlField.class);
        if (annotation != null) return annotation;

        annotation = field.getAnnotation(XmlObject.class);
        if (annotation != null) return annotation;

        annotation = field.getAnnotation(XmlObjectList.class);
        if (annotation != null) return annotation;

        annotation = field.getAnnotation(XmlObjects.class);
        if (annotation != null) return annotation;

        return null;
    }

    static String getClassTag(Object object) {
        XmlClass classAnnotation = object.getClass().getAnnotation(XmlClass.class);
        String annotationValue = classAnnotation.value();
        String className = object.getClass().getSimpleName();

        return isEmpty(annotationValue) ? className : annotationValue;
    }

    private static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }
}

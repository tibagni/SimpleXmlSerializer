package com.tiagobagni.simplexmlserializerlib.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a {@link java.util.List} of objects should be serialized into xml. Elements of the
 * list must be from a type annotated with {@link XmlObject}
 *
 * @author  Tiago Bagni
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface XmlObjectList {
    String value();
}

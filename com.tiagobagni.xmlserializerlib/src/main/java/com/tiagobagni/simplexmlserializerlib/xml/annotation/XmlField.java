package com.tiagobagni.simplexmlserializerlib.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a certain primitive field from a class should be serialized into xml. It should
 * only be used to annotate primitive types such as int, long, double, float,
 * boolean or String.
 *
 * @author  Tiago Bagni
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface XmlField {
    String value();
}

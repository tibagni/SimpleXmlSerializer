package com.tiagobagni.simplexmlserializerlib.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a certain object field should be serialized into xml. Check {@link XmlField}
 * if you want to annotate primitive types and String
 *
 * @author  Tiago Bagni
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface XmlObject {
    String value() default "";
}

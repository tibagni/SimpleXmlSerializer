package com.tiagobagni.simplexmlserializerlib.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a certain class can be serialized into xml.
 * This Annotation should be used for Classes only.
 *
 * @author  Tiago Bagni
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface XmlClass {
}

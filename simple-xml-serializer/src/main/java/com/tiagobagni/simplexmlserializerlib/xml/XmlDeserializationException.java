package com.tiagobagni.simplexmlserializerlib.xml;

/**
 * Represents an error while trying to deserialize XML
 *
 * @author  Tiago Bagni
 */
public class XmlDeserializationException extends Exception {
    public XmlDeserializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlDeserializationException(String message) {
        super(message);
    }
}

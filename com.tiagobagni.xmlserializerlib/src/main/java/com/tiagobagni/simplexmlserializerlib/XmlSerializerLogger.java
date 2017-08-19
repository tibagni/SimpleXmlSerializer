package com.tiagobagni.simplexmlserializerlib;

/**
 * Logger interface which {@link com.tiagobagni.simplexmlserializerlib.xml.XmlSerializer} and
 * {@link com.tiagobagni.simplexmlserializerlib.xml.XmlDeserializer} will use to print debug
 * information if configured to do so.
 *
 * @author  Tiago Bagni
 */
public interface XmlSerializerLogger {
    void debug(String logMessage);
    void error(String errorMsg);
    void error(String errorMsg, Throwable error);
}

package com.tiagobagni.simplexmlserializerlib.xml;

import com.tiagobagni.simplexmlserializerlib.XmlSerializerLogger;

/**
 * Here you can customize some parameters of the lib like enabling logs and providing
 * a new {@link XmlSerializerLogger} for the lib to use.
 *
 * @author Tiago Bagni
 */
public class SimpleXmlParams {
    private XmlSerializerLogger logger;
    private boolean isDebugMode;

    private static SimpleXmlParams instance;

    private SimpleXmlParams() {
        // initialize default values
        logger = new AndroidLogger();
        isDebugMode = false;
    }

    public static SimpleXmlParams get() {
        if (instance == null) {
            instance = new SimpleXmlParams();
        }
        return instance;
    }

    XmlSerializerLogger getLogger() {
        return logger;
    }

    /**
     * Set a customized logger. If none is provided, will use {@link android.util.Log} by default
     * @param logger that implements {@link XmlSerializerLogger}
     */
    public void setLogger(XmlSerializerLogger logger) {
        this.logger = logger;
    }

    boolean isDebugMode() {
        return isDebugMode;
    }

    /**
     * Enable the debug logging. False by default
     * @param debugMode
     */
    public void setDebugMode(boolean debugMode) {
        isDebugMode = debugMode;
    }
}

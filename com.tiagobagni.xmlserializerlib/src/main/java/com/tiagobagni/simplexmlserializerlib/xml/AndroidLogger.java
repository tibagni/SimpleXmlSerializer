package com.tiagobagni.simplexmlserializerlib.xml;

import android.util.Log;

import com.tiagobagni.simplexmlserializerlib.XmlSerializerLogger;

/**
 * Created by tiagobagni on 19/08/17.
 */
class AndroidLogger implements XmlSerializerLogger {
    private static final String TAG = "SimpleXml";

    @Override
    public void debug(String logMessage) {
        Log.d(TAG, logMessage);
    }

    @Override
    public void error(String errorMsg) {
        Log.e(TAG, getErrorMsg(errorMsg));
    }

    @Override
    public void error(String errorMsg, Throwable error) {
        Log.e(TAG, getErrorMsg(errorMsg), error);
    }

    private String getErrorMsg(String msg) {
        return "[ERROR] " + msg;
    }
}

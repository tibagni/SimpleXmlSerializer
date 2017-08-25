package com.tiagobagni.simplexmlserializerlib.xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/*
 * Utility class with methods to help handling Strings
 *
 * @author Tiago Bagni
 */
class StringUtils {
    static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    static String trim(String input) {
        try {
            BufferedReader reader = new BufferedReader(new StringReader(input));
            StringBuilder result = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line.trim());
            }

            return result.toString();
        } catch (IOException e) {
            throw new IllegalArgumentException("There was a problem reading input: " + input, e);
        }
    }

    static boolean equals(String str1, String str2) {
        return (str1 == str2) || (str1 != null && str1.equals(str2));
    }
}

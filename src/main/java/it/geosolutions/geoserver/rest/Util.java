/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *
 *  Copyright (C) 2007,2015 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package it.geosolutions.geoserver.rest;

import it.geosolutions.geoserver.rest.decoder.RESTStyle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jdom.Element;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class Util {

public static final String QUIET_ON_NOT_FOUND_PARAM = "quietOnNotFound="; 
    
    public static final boolean DEFAULT_QUIET_ON_NOT_FOUND = true; 

    /**
     * Search for a stylename in global and in all workspaces.
     */
    public static List<RESTStyle> searchStyles(GeoServerRESTReader reader, String stylename) {

        List<RESTStyle> styles = new ArrayList<RESTStyle>();

        RESTStyle style = reader.getStyle(stylename);
        if(style != null)
            styles.add(style);

        for (String workspace : reader.getWorkspaceNames()) {
            style = reader.getStyle(workspace, stylename);
            if(style != null)
                styles.add(style);
        }

        return styles;
    }
    
    public static List<String> getElementsChildrenStringContent(Element elementRoot){
        return getElementsChildrenContent(elementRoot, "string");
    }

    public static List<String> getElementsChildrenContent(Element elementRoot, String childrensName){
        assert(elementRoot != null) : "The passed elementRoot must be not null";
        assert(childrensName != null) : "The passed childrensName must be not null";
        List<String> stringContentList = new ArrayList<String>();
        for (Element listItem : (List<Element>) Util.safeList(elementRoot.getChildren(childrensName))) {
            stringContentList.add(listItem.getText());
        }
        return stringContentList;
    }
    
    public static Integer getIntValueFromText(String text){
        Integer result = null;
        if(text != null){
            result = Integer.parseInt(text);
        }
        return result;
    }

    public static Float getFloatValueFromText(String text){
        Float result = null;
        if(text != null){
            result = Float.parseFloat(text);
        }
        return result;
    }

    public static Double getDoubleValueFromText(String text){
        Double result = null;
        if(text != null){
            result = Double.parseDouble(text);
        }
        return result;
    }

    public static Long getLongValueFromText(String text){
        Long result = null;
        if(text != null){
            result = Long.parseLong(text);
        }
        return result;
    }

    public static Boolean getBooleanValueFromText(String text){
        Boolean result = null;
        if(text != null){
            result = Boolean.parseBoolean(text);
        }
        return result;
    }
    
    /**
     * Append the quietOnNotFound parameter to the input URL
     * @param quietOnNotFound parameter
     * @param url input url
     * @return a composed url with the parameter appended
     */
    public static String appendQuietOnNotFound(boolean quietOnNotFound, String url) {
        boolean contains = url.contains("?");
        String composed = url + (contains ? "&":"?") + QUIET_ON_NOT_FOUND_PARAM + quietOnNotFound;
        return composed;
    }

    public static <T> List<T> safeList(List<T> list) {
        return list == null ? Collections.EMPTY_LIST : list;
    }

    public static <T> Collection<T> safeCollection(Collection<T> collection) {
        return collection == null ? Collections.EMPTY_SET : collection;
    }

    public static <TK, TV> Map<TK, TV> safeMap(Map<TK, TV> map) {
        return map == null ? Collections.EMPTY_MAP : map;
    }
    
    /**
     * 
     * @param stringValue
     * @return true iff the stringValue is NULL or it is empty
     */
    public static boolean isEmpty(String stringValue){
        return stringValue == null || stringValue.isEmpty();
    }

    public static char getParameterSeparator(String url) {
        char parameterSeparator = '?';
        if (url.contains("?")) {
            parameterSeparator = '&';
        }
        return parameterSeparator;
    }

    public static char getParameterSeparator(StringBuilder url) {
        char parameterSeparator = '?';
        if (url.indexOf("?") != -1) {
            parameterSeparator = '&';
        }
        return parameterSeparator;
    }

    public static boolean appendParameter(StringBuilder url, String parameterName,
            String parameterValue) {
        boolean result = false;
        if (parameterName != null && !parameterName.isEmpty()
                && parameterValue != null && !parameterValue.isEmpty()) {
            char parameterSeparator = getParameterSeparator(url);
            url.append(parameterSeparator).append(parameterName.trim())
                    .append('=').append(parameterValue.trim());
        }
        return result;
    }
}

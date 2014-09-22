/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *
 *  Copyright (C) 2007,2013 GeoSolutions S.A.S.
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
import java.util.List;

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
}

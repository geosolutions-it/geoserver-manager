/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2011 GeoSolutions S.A.S.
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

package it.geosolutions.geoserver.rest.encoder;

import org.jdom.Element;

import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * 
 * The layer encoder is enabled by default
 * 
 */
public class GSLayerEncoder extends PropertyXMLEncoder {

    public GSLayerEncoder() {
        super("layer");
        addEnabled();
    }
    
    /**
     * enabled the layer
     */
    protected void addEnabled(){
        add("enabled","true");
    }
    
    /**
     * @param enable true if layer should be set to enabled 
     */
    public void setEnabled(boolean enable){
    	if (enable)
    		set("enabled","true");
    	else
    		set("enabled","false");
    }
        
    private final static String DESCRIPTION = "description";
    /**
     * Add the 'description' node with a text value from 'description'
     * 
     */
    protected void addDescription(final String description) {
        add(DESCRIPTION, description);
    }
    /**
     * Set or modify the 'description' node with a text value from 'description'
     */
    public void setDescription(final String description) {
        set(DESCRIPTION, description);
    }
    
//    queryable
    private final static String QUERYABLE = "queryable";
    /**
     * Add the 'queryable' node with a text value from 'queryable' (true as default)
     */
    protected void addQueryable(final String queryable) {
        add(QUERYABLE, queryable!=null?queryable.toString():"true");
    }
    /**
     * Set or modify the 'queryable' node with a text value from 'queryable' (true as default)
     */
    public void setQueryable(final Boolean queryable) {
        set(QUERYABLE, queryable!=null?queryable.toString():"true");
    }

    /**
     * @see {@link GSLayerEncoder#setWmsPath(String)}
     * 
     * @param path the wms path to set
     */
    protected void addWmsPath(final String path) {
        add("path", path);
    }
    
    /**
     * Default WMS Path The GeoServer WMS path is, by default, /geoserver/wms.
     * However you can change it to /geoserver/anythingyouwant
     * 
     * @param path the wms path to set
     * @throws IllegalArgumentException if path is null or empty
     */
    public void setWmsPath(final String path) throws IllegalArgumentException {
        if (path==null || path.isEmpty())
            throw new IllegalArgumentException("Unable to set an empty or null parameter"); 
        set("path",path);
    }

    /**
     * @see {@link GSLayerEncoder#setDefaultStyle(String)}
     * @param defaultStyle 
     */
    protected void addDefaultStyle(String defaultStyle) {
        add("defaultStyle", defaultStyle);
    }

    /**
     * @param defaultStyle The style that will be applied if no style is specified.
     * @throws IllegalArgumentException if defaultStyle is null or empty
     */
    public void setDefaultStyle(final String defaultStyle) throws IllegalArgumentException {
        if (defaultStyle==null || defaultStyle.isEmpty())
            throw new IllegalArgumentException("Unable to set an empty or null parameter");
        set("defaultStyle", defaultStyle);
    }
}

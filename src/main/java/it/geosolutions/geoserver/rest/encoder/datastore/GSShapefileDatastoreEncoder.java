/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2012 GeoSolutions S.A.S.
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
package it.geosolutions.geoserver.rest.encoder.datastore;

import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Encoder for a {@value #TYPE} datastore.
 * 
 * @author Oscar Fonts
 */
public class GSShapefileDatastoreEncoder extends GSAbstractDatastoreEncoder {
	
	static final String TYPE = "Shapefile";
	
	final static boolean DEFAULT_ENABLED = true;
	final static String   DEFAULT_CHARSET = "ISO-8859-1";
	final static boolean DEFAULT_CREATE_SPATIAL_INDEX = true;
	final static boolean DEFAULT_MEMORY_MAPPED_BUFFER = false;
	final static boolean DEFAULT_CACHE_AND_REUSE_MEMORY_MAPS = true;

    /**
     * Create a {@value #TYPE} datastore with default connection parameters,
     * given a store name and a url (the store location).
     * 
     * The following default connection parameters are set:
     * <ul>
     *   <li>enabled: {@value #DEFAULT_ENABLED}
     *   <li>charset: {@value #DEFAULT_CHARSET}
     *   <li>create spatial index: {@value #DEFAULT_CREATE_SPATIAL_INDEX}
     *   <li>memory mapped buffer: {@value #DEFAULT_MEMORY_MAPPED_BUFFER}
     *   <li>cache and reuse memory maps: {@value #DEFAULT_CACHE_AND_REUSE_MEMORY_MAPS}
     * </ul>
     * 
     * @param name New datastore name
     * @param url The shapefile location in the server, relative to $GEOSERVER_DATA_DIR.
     */
    public GSShapefileDatastoreEncoder(String name, URL url) {
        // Set fixed values
    	super(name);
    	setType(TYPE);
        
        // Set mandatory parameter
		ensureValidURL(url);
        setUrl(url);
        
        // Set default values
        setEnabled(DEFAULT_ENABLED);
        setCharset(Charset.forName(DEFAULT_CHARSET));
    	setCreateSpatialIndex(DEFAULT_CREATE_SPATIAL_INDEX);
    	setMemoryMappedBuffer(DEFAULT_MEMORY_MAPPED_BUFFER);
    	setCacheAndReuseMemoryMaps(DEFAULT_CACHE_AND_REUSE_MEMORY_MAPS);
    }
    
    /**
     * Create a {@value #TYPE} datastore encoder from an existing store read from server.
     * 
     * @param store The existing store.
     * @throws IllegalArgumentException if store type or mandatory parameters are not valid
     */
    public GSShapefileDatastoreEncoder(RESTDataStore store) {
    	super(store);

    	// Check mandatory parameter validity
    	try {
			ensureValidURL(new URL(store.getConnectionParameters().get("url")));
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Shapefile store URL is malformed", e);
		}
    }
    
    public void setUrl(URL url) {
    	ensureValidURL(url);
    	connectionParameters.set("url", url.toString());
    }
    
    public URL getUrl() {
    	try {
			return new URL(ElementUtils.contains(connectionParameters.getRoot(), "description").getTextTrim());
		} catch (MalformedURLException e) {
			return null;
		}
    }
    
    public void setCharset(Charset charset) {
    	connectionParameters.set("charset", charset.name());
    }
    
    public Charset getCharset() {
    	return Charset.forName(ElementUtils.contains(connectionParameters.getRoot(), "charset").getTextTrim());
    }

    public void setCreateSpatialIndex(boolean createSpatialIndex) {
    	connectionParameters.set("create spatial index", Boolean.toString(createSpatialIndex));
    }
    
    public boolean getCreateSpatialIndex() {
    	return Boolean.parseBoolean(ElementUtils.contains(connectionParameters.getRoot(), "create spatial index").getTextTrim());
    }
    
    public void setMemoryMappedBuffer(boolean memoryMappedBuffer) {
    	connectionParameters.set("memory mapped buffer", Boolean.toString(memoryMappedBuffer));
    }
    
    public boolean getMemoryMappedBuffer() {
    	return Boolean.parseBoolean(ElementUtils.contains(connectionParameters.getRoot(), "memory mapped buffer").getTextTrim());
    }
    
    public void setCacheAndReuseMemoryMaps(boolean cacheAndReuseMemoryMaps) {
    	connectionParameters.set("cache and reuse memory maps", Boolean.toString(cacheAndReuseMemoryMaps));
    }
    
    public boolean getCacheAndReuseMemoryMaps() {
    	return Boolean.parseBoolean(ElementUtils.contains(connectionParameters.getRoot(), "cache and reuse memory maps").getTextTrim());
    }    
        
    /**
     * Check url validity.
     * 
     * @param url the url
     * @throws IllegalArgumentException if url is null or empty
     */
    private static void ensureValidURL(URL url) {
		if (url == null || url.toString().isEmpty()) {
			throw new IllegalArgumentException(
				"Shapefile store URL cannot be null or empty");
		}
    }
    
    /**
     * @return {@value #TYPE}
     */
    protected String getValidType() {
    	return TYPE;
    }
}

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

import java.util.Map;

import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import it.geosolutions.geoserver.rest.encoder.utils.NestedElementEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;

/**
 * Generic Datastore encoder.
 * 
 * Provides getters and setters for parameters common to all Datastores,
 * an internal placeholder for specific connection parameters, and
 * a constructor to read parameters from a {@link RESTDataStore}.
 * 
 * @author Oscar Fonts
 */
public abstract class GSAbstractDatastoreEncoder extends PropertyXMLEncoder {

	final static String ROOT = "dataStore";
	
	NestedElementEncoder connectionParameters = new NestedElementEncoder("connectionParameters");
	
	GSAbstractDatastoreEncoder(String storeName) {
		super(ROOT);
        // Add mandatory parameter
    	ensureValidName(storeName);
		setName(storeName);
		
        // Add connection parameters
    	addContent(connectionParameters.getRoot());
	}
	
    /**
     * Create a {@value #TYPE} datastore encoder from a store read from server.
     * 
     * @param store The existing store.
     * @throws IllegalArgumentException if store type or mandatory parameters are not valid
     */
    GSAbstractDatastoreEncoder(RESTDataStore store) {
    	this(store.getName());
    	
    	// Match datastore type
    	ensureValidType(store.getStoreType());
    	setType(store.getStoreType());
    	
    	// Copy store parameters
		setDescription(store.getDescription());
		setEnabled(store.isEnabled());
		
		// Copy connection parameters - bulk
		Map<String, String> params = store.getConnectionParameters();
		for(String key : params.keySet()) {
			connectionParameters.set(key, params.get(key));
		}
    }
    
    void setType(String type) {
        set("type", type);
    }
    
    public String getType() {
    	return ElementUtils.contains(getRoot(), "type").getTextTrim();
    }
   
    public void setName(String name) {
    	ensureValidName(name);
    	set("name", name);
    }
    
    public String getName() {
    	return ElementUtils.contains(getRoot(), "name").getTextTrim();
    }

    public void setDescription(String description) {
    	set("description", description);
    }
    
    public String getDescription() {
    	return ElementUtils.contains(getRoot(), "description").getTextTrim();
    }
    
    public void setEnabled(boolean enabled) {
    	set("enabled", Boolean.toString(enabled));
    }
    
    public boolean getEnabled() {
    	return Boolean.parseBoolean(ElementUtils.contains(getRoot(), "enabled").getTextTrim());
    }
    
    /**
     * Check name validity.
     * 
     * @param name the name
     * @throws IllegalArgumentException if name is null or empty
     */
    void ensureValidName(String name) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException(
				"Store name cannot be null or empty");			
		}
    }
    
    /**
     * Check type validity.
     * 
     * @param type the type.
     * @throws IllegalArgumentException if type is not {@value #TYPE}
     */
    void ensureValidType(String type) {
		if (!type.equals(getValidType())) {
			throw new IllegalArgumentException(
				"The store type '"+ type +"' is not valid");
		}
    }
    
    /**
     * The type of the implementing datastore.
     */
    abstract String getValidType();
}

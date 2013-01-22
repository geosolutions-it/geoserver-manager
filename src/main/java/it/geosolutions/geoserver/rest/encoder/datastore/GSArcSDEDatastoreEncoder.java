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

/**
 * Encoder for an {@value #TYPE} datastore.
 *  
 * @author Gianni Barrotta
 * @author Oscar Fonts
 */
public class GSArcSDEDatastoreEncoder extends GSAbstractDatastoreEncoder {

	static final String TYPE = "ArcSDE";
	
	static final String DEFAULT_DB_TYPE = "arcsde";
	static final int DEFAULT_PORT = 5151;
	static final int DEFAULT_MIN_CONNECTIONS = 2;
	static final int DEFAULT_MAX_CONNECTIONS = 6;
	static final int DEFAULT_CONNECTION_TIMEOUT = 500;
	static final boolean DEFAULT_ALLOW_NON_SPATIAL_TABLES = false;
 
    /**
     * Create an {@value #TYPE} datastore with default connection parameters,
     * given a store name, a server name, and a user name.
     * 
     * The following default connection parameters are set:
     * <ul>
     *   <li>dbtype: {@value #DEFAULT_DB_TYPE}
     *   <li>port: {@value #DEFAULT_PORT}
     *   <li>pool.minConnections: {@value #DEFAULT_MIN_CONNECTIONS}
     *   <li>pool.maxConnections: {@value #DEFAULT_MAX_CONNECTIONS}
     *   <li>pool.timeOut: {@value #DEFAULT_CONNECTION_TIMEOUT}
     *   <li>datastore.allowNonSpatialTables: {@value #DEFAULT_ALLOW_NON_SPATIAL_TABLES}
     * </ul>
     * 
     * @param name New datastore name
     * @param server New server name
     * @param user New user name
     */
    public GSArcSDEDatastoreEncoder(String name, String server, String user) {
        super(name);
        
        // Set mandatory parameters
        setServer(server);
        setUser(user);
        
        // Set default values
        setDbType(DEFAULT_DB_TYPE);
        setPort(DEFAULT_PORT);
        setMinConnections(DEFAULT_MIN_CONNECTIONS);
        setMaxConnections(DEFAULT_MAX_CONNECTIONS);
        setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT);
        setAllowNonSpatialTables(DEFAULT_ALLOW_NON_SPATIAL_TABLES);
    }
    
    /**
     * Create an {@value #TYPE} datastore encoder from an existing store read from server.
     * 
     * @param store The existing store.
     * @throws IllegalArgumentException if store type or mandatory parameters are not valid
     */
    public GSArcSDEDatastoreEncoder(RESTDataStore store) {
    	super(store);

    	// Check mandatory parameter validity
		ensureValidServer(store.getConnectionParameters().get("server"));
		ensureValidUser(store.getConnectionParameters().get("user"));
    }

    public void setDbType(String dbtype) {
        connectionParameters.set("dbtype", dbtype);
    }
    
    public String getDbType() {
    	return ElementUtils.contains(connectionParameters.getRoot(), "dbtype").getTextTrim();
    }
    
    public void setServer(String server) {
    	ensureValidServer(server);
        connectionParameters.set("server", server);
    }
    
    public String getServer() {
    	return ElementUtils.contains(connectionParameters.getRoot(), "server").getTextTrim();
    }

    public void setPort(int port) {
        connectionParameters.set("port", Integer.toString(port));
    }
    
    public int getPort() {
    	return Integer.parseInt(ElementUtils.contains(connectionParameters.getRoot(), "port").getTextTrim());
    }

    public void setInstance(String instance) {
        connectionParameters.set("instance", instance);
    }
    
    public String getInstance() {
    	return ElementUtils.contains(connectionParameters.getRoot(), "instance").getTextTrim();
    }

    public void setUser(String user) {
    	ensureValidUser(user);
        connectionParameters.set("user", user);
    }

    public String getUser() {
    	return ElementUtils.contains(connectionParameters.getRoot(), "user").getTextTrim();
    }
    
    public void setPassword(String password) {
        connectionParameters.set("password", password);
    }

    public String getPassword() {
    	return ElementUtils.contains(connectionParameters.getRoot(), "password").getTextTrim();
    }
    
    public void setNamespace(String namespace) {
        connectionParameters.set("namespace", namespace);
    }
    
    public String getNamespace() {
    	return ElementUtils.contains(connectionParameters.getRoot(), "namespace").getTextTrim();
    }
    
    public void setMinConnections(int minConnections) {
    	connectionParameters.set("pool.minConnections", Integer.toString(minConnections));
    }
    
    public int getMinConnections() {
    	return Integer.parseInt(ElementUtils.contains(connectionParameters.getRoot(), "pool.minConnections").getTextTrim());
    }
    
    public void setMaxConnections(int maxConnections) {
    	connectionParameters.set("pool.maxConnections", Integer.toString(maxConnections));
    }
    
    public int getMaxConnections() {
    	return Integer.parseInt(ElementUtils.contains(connectionParameters.getRoot(), "pool.maxConnections").getTextTrim());
    }
    
    public void setConnectionTimeout(int seconds) {
    	connectionParameters.set("pool.timeOut", Integer.toString(seconds));
    }
    
    public int getConnectionTimeout() {
    	return Integer.parseInt(ElementUtils.contains(connectionParameters.getRoot(), "pool.timeOut").getTextTrim());
    }
    
    public void setAllowNonSpatialTables(boolean allowNonSpatialTables) {
    	connectionParameters.set("datastore.allowNonSpatialTables", Boolean.toString(allowNonSpatialTables));
    }
    
    public boolean getAllowNonSpatialTables() {
    	return Boolean.parseBoolean(ElementUtils.contains(connectionParameters.getRoot(), "datastore.allowNonSpatialTables").getTextTrim());
    }

    /**
     * Check server validity.
     * 
     * @param server the server name
     * @throws IllegalArgumentException if server name is null or empty
     */
    private static void ensureValidServer(String server) {
		if (server == null || server.length() == 0) {
			throw new IllegalArgumentException(
				"ArcSDE store server name cannot be null or empty");
		}
    }
    
    /**
     * Check user validity.
     * 
     * @param user the user name
     * @throws IllegalArgumentException if user name is null or empty
     */
    private static void ensureValidUser(String user) {
		if (user == null || user.length() == 0) {
			throw new IllegalArgumentException(
				"ArcSDE store user name cannot be null or empty");
		}
    }
    
    /**
     * @return {@value #TYPE}
     */
    protected String getValidType() {
    	return TYPE;
    }
}
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

import it.geosolutions.geoserver.rest.encoder.utils.NestedElementEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;

/**
 * Geoserver datastore XML encoder.
 *  
 * @author Eric Grosso
 * @author ETj
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GSPostGISDatastoreEncoder extends PropertyXMLEncoder {

    private NestedElementEncoder connectionParameters = new NestedElementEncoder("connectionParameters");

    public GSPostGISDatastoreEncoder() {
        super("dataStore");
        addContent(connectionParameters.getRoot());
        
        addType("PostGIS"); // may be overwritten with e.g. "PostGIS (JNDI)"
        addDatabaseType("postgis");
    }
    
    /**
     * Set some initial defaults.
     * <br/><br/>
     * The default parameters are as follows: <ul>
     * <li>maximum connections: 10, </li>
     * <li>minimum connections: 1,</li>
     * <li>fetch size: 1000, </li>
     * <li>connection timeout: 20 seconds, </li>
     * <li>loose BBox: true, </li>
     * <li>prepared statements: false,</li>
     * <li>maximum open prepared statements: 50.    </li>
     * </ul>
     */
    public void defaultInit() {
        setMinConnections(1);
        setMaxConnections(10);
        setFetchSize(1000);
        setConnectionTimeout(20);
        setLooseBBox(true);
        setPreparedStatements(false);
        setMaxOpenPreparedStatements(50);
    }

    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addName(String name) {
        add("name", name);
    }
    
    public void setName(String name) {
        set("name", name);
    }

    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addDescription(String description) {
        add("description", description);
    }
    
    public void setDescription(String description) {
        set("description", description);
    }

    /**
     * @deprecated will be set to protected in the next release
     */
	public void addType(String type) {
        add("type", type);
    }
    
    public void setType(String type) {
        set("type", type);
    }

    /**
		* @deprecated will be set to protected in the next release
		*/
	public void addEnabled(boolean enabled) {
        add("enabled", Boolean.toString(enabled));
    }
    
    public void setEnabled(boolean enabled) {
        set("enabled", Boolean.toString(enabled));
    }
    
    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addNamespace(String namespace) {
        connectionParameters.add("namespace", namespace);
    }
    
    public void setNamespace(String namespace) {
        connectionParameters.set("namespace", namespace);
    }
    
    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addHost(String host) {
        connectionParameters.add("host", host);
    }
    
    public void setHost(String host) {
        connectionParameters.set("host", host);
    }

    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addPort(int port) {
        connectionParameters.add("port", Integer.toString(port));
    }
    
    public void setPort(int port) {
        connectionParameters.set("port", Integer.toString(port));
    }

    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addDatabase(String database) {
        connectionParameters.add("database", database);
    }
    
    public void setDatabase(String database) {
        connectionParameters.set("database", database);
    }

    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addSchema(String schema) {
        connectionParameters.add("schema", schema);
    }
    
    public void setSchema(String schema) {
        connectionParameters.set("schema", schema);
    }

    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addUser(String user) {
        connectionParameters.add("user", user);
    }
    
    public void setUser(String user) {
        connectionParameters.set("user", user);
    }

    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addPassword(String password) {
        connectionParameters.add("passwd", password);
    }
    
    public void setPassword(String password) {
        connectionParameters.set("passwd", password);
    }

    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addDatabaseType(String dbtype) {
        connectionParameters.add("dbtype", dbtype);
    }

    public void setDatabaseType(String dbtype) {
        connectionParameters.set("dbtype", dbtype);
    }

    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addJndiReferenceName(String jndiReferenceName) {
        connectionParameters.add("jndiReferenceName", jndiReferenceName);
    }
    
    public void setJndiReferenceName(String jndiReferenceName) {
        connectionParameters.set("jndiReferenceName", jndiReferenceName);
    }

    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addExposePrimaryKeys(boolean exposePrimaryKeys) {
    	connectionParameters.add("Expose primary keys", Boolean.toString(exposePrimaryKeys));
    }
    
    public void setExposePrimaryKeys(boolean exposePrimaryKeys) {
    	connectionParameters.set("Expose primary keys", Boolean.toString(exposePrimaryKeys));
    }
    
    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addMaxConnections(int maxConnections) {
    	connectionParameters.add("max connections", Integer.toString(maxConnections));
    }
    
    public void setMaxConnections(int maxConnections) {
    	connectionParameters.set("max connections", Integer.toString(maxConnections));
    }
    
    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addMinConnections(int minConnections) {
    	connectionParameters.add("min connections", Integer.toString(minConnections));
    }
    
    public void setMinConnections(int minConnections) {
    	connectionParameters.set("min connections", Integer.toString(minConnections));
    }
    
    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addFetchSize(int fetchSize) {
    	connectionParameters.add("fetch size", Integer.toString(fetchSize));
    }
    
    public void setFetchSize(int fetchSize) {
    	connectionParameters.set("fetch size", Integer.toString(fetchSize));
    }
    
    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addConnectionTimeout(int seconds) {
    	connectionParameters.add("Connection timeout", Integer.toString(seconds));
    }
    
    public void setConnectionTimeout(int seconds) {
    	connectionParameters.set("Connection timeout", Integer.toString(seconds));
    }
    
    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addValidateConnections(boolean validateConnections) {
    	connectionParameters.add("validate connections", Boolean.toString(validateConnections));
    }
    
    public void setValidateConnections(boolean validateConnections) {
    	connectionParameters.set("validate connections", Boolean.toString(validateConnections));
    }
    
    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addPrimaryKeyMetadataTable(String primaryKeyMetadataTable) {
    	connectionParameters.add("Primary key metadata table", primaryKeyMetadataTable);
    }
    
    public void setPrimaryKeyMetadataTable(String primaryKeyMetadataTable) {
    	connectionParameters.set("Primary key metadata table", primaryKeyMetadataTable);
    }
    
    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addLooseBBox(boolean looseBBox) {
    	connectionParameters.add("Loose bbox", Boolean.toString(looseBBox));
    }
    
    public void setLooseBBox(boolean looseBBox) {
    	connectionParameters.set("Loose bbox", Boolean.toString(looseBBox));
    }
    
    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addPreparedStatements(boolean preparedStatements) {
    	connectionParameters.add("preparedStatements", Boolean.toString(preparedStatements));
    }
    
    public void setPreparedStatements(boolean preparedStatements) {
    	connectionParameters.set("preparedStatements", Boolean.toString(preparedStatements));
    }
    
    /**
	 * @deprecated will be set to protected in the next release
	 */
	public void addMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
    	connectionParameters.add("Max open prepared statements", Integer.toString(maxOpenPreparedStatements));
    }
    
    public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
    	connectionParameters.set("Max open prepared statements", Integer.toString(maxOpenPreparedStatements));
    }

}
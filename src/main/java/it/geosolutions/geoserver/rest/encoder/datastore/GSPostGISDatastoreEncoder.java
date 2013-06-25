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

/**
 * Encoder for a {@value #TYPE} datastore.
 *  
 * @author Eric Grosso
 * @author ETj
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * @author Oscar Fonts
 */
public class GSPostGISDatastoreEncoder extends GSAbstractDatastoreEncoder {

	static final String TYPE = "PostGIS";

	static final int DEFAULT_MIN_CONNECTIONS = 1;
	static final int DEFAULT_MAX_CONNECTIONS = 10;
	static final int DEFAULT_FETCH_SIZE = 1000;
	static final int DEFAULT_CONNECTION_TIMEOUT = 20;
	static final boolean DEFAULT_LOOSE_BBOX = true;
	static final boolean DEFAULT_PREPARED_STATEMENTS = false;
	static final int DEFAULT_MAX_OPEN_PREPARED_STATEMENTS = 50;
	static final boolean DEFAULT_ESTIMATED_EXTENDS = false;
	
	/**
	 * 
	 * @param name DataStore name
	 */
    public GSPostGISDatastoreEncoder(String name) {
        super(name);

        // Set mandatory parameter
        setType(TYPE);
        setDatabaseType("postgis");
        
        // Set default values
        setMinConnections(DEFAULT_MIN_CONNECTIONS);
        setMaxConnections(DEFAULT_MAX_CONNECTIONS);
        setFetchSize(DEFAULT_FETCH_SIZE);
        setConnectionTimeout(DEFAULT_CONNECTION_TIMEOUT);
        setLooseBBox(DEFAULT_LOOSE_BBOX);
        setPreparedStatements(DEFAULT_PREPARED_STATEMENTS);
        setMaxOpenPreparedStatements(DEFAULT_MAX_OPEN_PREPARED_STATEMENTS);
        setEstimatedExtends(DEFAULT_ESTIMATED_EXTENDS);
        
    }
    
    public void setNamespace(String namespace) {
        connectionParameters.set("namespace", namespace);
    }
    
    public void setHost(String host) {
        connectionParameters.set("host", host);
    }
    
    public void setPort(int port) {
        connectionParameters.set("port", Integer.toString(port));
    }
    
    public void setDatabase(String database) {
        connectionParameters.set("database", database);
    }

    public void setSchema(String schema) {
        connectionParameters.set("schema", schema);
    }
    
    public void setUser(String user) {
        connectionParameters.set("user", user);
    }
    
    public void setPassword(String password) {
        connectionParameters.set("passwd", password);
    }

    public void setDatabaseType(String dbtype) {
        connectionParameters.set("dbtype", dbtype);
    }
    
    public void setJndiReferenceName(String jndiReferenceName) {
        connectionParameters.set("jndiReferenceName", jndiReferenceName);
    }
    
    public void setExposePrimaryKeys(boolean exposePrimaryKeys) {
    	connectionParameters.set("Expose primary keys", Boolean.toString(exposePrimaryKeys));
    }
    
    public void setMaxConnections(int maxConnections) {
    	connectionParameters.set("max connections", Integer.toString(maxConnections));
    }
    
    public void setMinConnections(int minConnections) {
    	connectionParameters.set("min connections", Integer.toString(minConnections));
    }
    
    public void setFetchSize(int fetchSize) {
    	connectionParameters.set("fetch size", Integer.toString(fetchSize));
    }
    
    public void setConnectionTimeout(int seconds) {
    	connectionParameters.set("Connection timeout", Integer.toString(seconds));
    }
    
    public void setValidateConnections(boolean validateConnections) {
    	connectionParameters.set("validate connections", Boolean.toString(validateConnections));
    }
    
    public void setPrimaryKeyMetadataTable(String primaryKeyMetadataTable) {
    	connectionParameters.set("Primary key metadata table", primaryKeyMetadataTable);
    }
    
    public void setLooseBBox(boolean looseBBox) {
    	connectionParameters.set("Loose bbox", Boolean.toString(looseBBox));
    }
    
    public void setPreparedStatements(boolean preparedStatements) {
    	connectionParameters.set("preparedStatements", Boolean.toString(preparedStatements));
    }
    
    public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
    	connectionParameters.set("Max open prepared statements", Integer.toString(maxOpenPreparedStatements));
    }
    
    public void setEstimatedExtends(boolean estimatedExtends){
    	connectionParameters.set("Estimated extends", Boolean.toString(estimatedExtends));
    }
    
    /**
     * @return {@value #TYPE}
     */
    protected String getValidType() {
    	return TYPE;
    }
}

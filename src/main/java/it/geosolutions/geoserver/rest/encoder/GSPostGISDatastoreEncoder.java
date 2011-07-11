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

import it.geosolutions.geoserver.rest.encoder.utils.EntryKeyListEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;

/**
 * Geoserver datastore XML encoder.
 *  
 * @author Eric Grosso
 * @author ETj
 */
public class GSPostGISDatastoreEncoder extends PropertyXMLEncoder {

    private EntryKeyListEncoder<String> connectionParameters = new EntryKeyListEncoder<String>("connectionParameters");


    public GSPostGISDatastoreEncoder() {
        super("dataStore");
        setType("PostGIS"); // may be overwritten with e.g. "PostGIS (JNDI)"
        setDatabaseType("postgis");
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

    public void setName(String name) {
        add("name", name);
    }

    public void setDescription(String description) {
        add("description", description);
    }

    public void setType(String type) {
        add("type", type);
    }

    public void setEnabled(boolean enabled) {
        add("enabled", Boolean.toString(enabled));
    }
    
    public void setNamespace(String namespace) {
        connectionParameters.add("namespace", namespace);
    }
    
    public void setHost(String host) {
        connectionParameters.add("host", host);
    }

    public void setPort(int port) {
        connectionParameters.add("port", Integer.toString(port));
    }

    public void setDatabase(String database) {
        connectionParameters.add("database", database);
    }

    public void setSchema(String schema) {
        connectionParameters.add("schema", schema);
    }

    public void setUser(String user) {
        connectionParameters.add("user", user);
    }

    public void setPassword(String password) {
        connectionParameters.add("passwd", password);
    }

    public void setDatabaseType(String dbtype) {
        connectionParameters.add("dbtype", dbtype);
    }

    public void setJndiReferenceName(String jndiReferenceName) {
        connectionParameters.add("jndiReferenceName", jndiReferenceName);
    }

    public void setExposePrimaryKeys(boolean exposePrimaryKeys) {
    	connectionParameters.add("Expose primary keys", Boolean.toString(exposePrimaryKeys));
    }
    
    public void setMaxConnections(int maxConnections) {
    	connectionParameters.add("max connections", Integer.toString(maxConnections));
    }
    
    public void setMinConnections(int minConnections) {
    	connectionParameters.add("min connections", Integer.toString(minConnections));
    }
    
    public void setFetchSize(int fetchSize) {
    	connectionParameters.add("fetch size", Integer.toString(fetchSize));
    }
    
    public void setConnectionTimeout(int seconds) {
    	connectionParameters.add("Connection timeout", Integer.toString(seconds));
    }
    
    public void setValidateConnections(boolean validateConnections) {
    	connectionParameters.add("validate connections", Boolean.toString(validateConnections));
    }
    
    public void setPrimaryKeyMetadataTable(String primaryKeyMetadataTable) {
    	connectionParameters.add("Primary key metadata table", primaryKeyMetadataTable);
    }
    
    public void setLooseBBox(boolean looseBBox) {
    	connectionParameters.add("Loose bbox", Boolean.toString(looseBBox));
    }
    
    public void setPreparedStatements(boolean preparedStatements) {
    	connectionParameters.add("preparedStatements", Boolean.toString(preparedStatements));
    }
    
    public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
    	connectionParameters.add("Max open prepared statements", Integer.toString(maxOpenPreparedStatements));
    }

}
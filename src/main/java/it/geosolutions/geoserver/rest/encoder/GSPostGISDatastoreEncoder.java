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
        addType("PostGIS"); // may be overwritten with e.g. "PostGIS (JNDI)"
        addDatabaseType("postgis");
        addContent(connectionParameters.getRoot());
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
        addMinConnections(1);
        addMaxConnections(10);
        addFetchSize(1000);
        addConnectionTimeout(20);
        addLooseBBox(true);
        addPreparedStatements(false);
        addMaxOpenPreparedStatements(50);
    }

    public void addName(String name) {
        add("name", name);
    }

    public void addDescription(String description) {
        add("description", description);
    }

    public void addType(String type) {
        add("type", type);
    }

    public void addEnabled(boolean enabled) {
        add("enabled", Boolean.toString(enabled));
    }
    
    public void addNamespace(String namespace) {
        connectionParameters.add("namespace", namespace);
    }
    
    public void addHost(String host) {
        connectionParameters.add("host", host);
    }

    public void addPort(int port) {
        connectionParameters.add("port", Integer.toString(port));
    }

    public void addDatabase(String database) {
        connectionParameters.add("database", database);
    }

    public void addSchema(String schema) {
        connectionParameters.add("schema", schema);
    }

    public void addUser(String user) {
        connectionParameters.add("user", user);
    }

    public void addPassword(String password) {
        connectionParameters.add("passwd", password);
    }

    public void addDatabaseType(String dbtype) {
        connectionParameters.add("dbtype", dbtype);
    }

    public void addJndiReferenceName(String jndiReferenceName) {
        connectionParameters.add("jndiReferenceName", jndiReferenceName);
    }

    public void addExposePrimaryKeys(boolean exposePrimaryKeys) {
    	connectionParameters.add("Expose primary keys", Boolean.toString(exposePrimaryKeys));
    }
    
    public void addMaxConnections(int maxConnections) {
    	connectionParameters.add("max connections", Integer.toString(maxConnections));
    }
    
    public void addMinConnections(int minConnections) {
    	connectionParameters.add("min connections", Integer.toString(minConnections));
    }
    
    public void addFetchSize(int fetchSize) {
    	connectionParameters.add("fetch size", Integer.toString(fetchSize));
    }
    
    public void addConnectionTimeout(int seconds) {
    	connectionParameters.add("Connection timeout", Integer.toString(seconds));
    }
    
    public void addValidateConnections(boolean validateConnections) {
    	connectionParameters.add("validate connections", Boolean.toString(validateConnections));
    }
    
    public void addPrimaryKeyMetadataTable(String primaryKeyMetadataTable) {
    	connectionParameters.add("Primary key metadata table", primaryKeyMetadataTable);
    }
    
    public void addLooseBBox(boolean looseBBox) {
    	connectionParameters.add("Loose bbox", Boolean.toString(looseBBox));
    }
    
    public void addPreparedStatements(boolean preparedStatements) {
    	connectionParameters.add("preparedStatements", Boolean.toString(preparedStatements));
    }
    
    public void addMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
    	connectionParameters.add("Max open prepared statements", Integer.toString(maxOpenPreparedStatements));
    }

}
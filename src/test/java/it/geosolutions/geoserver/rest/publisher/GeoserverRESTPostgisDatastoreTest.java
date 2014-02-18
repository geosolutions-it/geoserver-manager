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

package it.geosolutions.geoserver.rest.publisher;


import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import it.geosolutions.geoserver.rest.datastore.StoreIntegrationTest;
import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSPostGISDatastoreEncoder;

import java.net.MalformedURLException;

/**
 * Testcase for creating postgis-based resources on geoserver.
 * <P>
 * Since these tests require a running postgis instance, this is more like integration tests.<br/>
 * You may skip them by defining<tt> <pre>
 *        -DpgIgnore=true </pre></tt>
 * When <tt>pgIgnore</tt> is defined that way, failing tests will not break
 * the build: they will be logged as errors instead.
 *
 * <P>
 * The target postgis instance can be customized by defining the following env vars: <ul>
 * <LI><TT>pgHost</TT> (default <TT>localhost</TT>)</LI>
 * <LI><TT>pgPort</TT> (default: <TT>5432</TT>)</LI>
 * <LI><TT>pgDatabase</TT> (default: <TT>test</TT>)</LI>
 * <LI><TT>pgSchema</TT> (default: <TT>public</TT>)</LI>
 * <LI><TT>pgUser</TT> (default: <TT>utest</TT>)</LI>
 * <LI><TT>pgPassword</TT> (default: <TT>ptest</TT>)</LI>
 * </ul>
 *
 * @author etj
 * @author Eric Grosso
 * @author carlo cancellieri - GeoSolutions
 *
 * @see GeoserverRESTTest
 */
public class GeoserverRESTPostgisDatastoreTest extends StoreIntegrationTest {

    public GeoserverRESTPostgisDatastoreTest()
            throws IllegalArgumentException, MalformedURLException {
        super(System.getProperty("pgIgnore", "true").equalsIgnoreCase("true"));
    }

        
    @Override
    public GSAbstractStoreEncoder getStoreEncoderTest(){
        boolean exposePrimaryKeys = true;
        boolean validateConnections = false;
        String primaryKeyMetadataTable = "test";

        String datastoreName = "resttestpostgis";
        String description = "description";
        String dsNamespace = "http://www.geo-solutions.it";
        
        GSPostGISDatastoreEncoder datastoreEncoder = new GSPostGISDatastoreEncoder(datastoreName);
        datastoreEncoder.setDescription(description);
        datastoreEncoder.setNamespace(dsNamespace);
        
        String pgHost      = System.getProperty("pgHost", "localhost");
        int pgPort      = Integer.parseInt(System.getProperty("pgPort", "5432"));
        String pgDatabase  = System.getProperty("pgDatabase", "test");
        String pgSchema    = System.getProperty("pgSchema", "public");
        String pgUser      = System.getProperty("pgUser", "utest");
        String pgPassword  = System.getProperty("pgPassword", "ptest");
        
        datastoreEncoder.setHost(pgHost);
        datastoreEncoder.setPort(pgPort);
        datastoreEncoder.setDatabase(pgDatabase);
        datastoreEncoder.setSchema(pgSchema);
        datastoreEncoder.setUser(pgUser);
        datastoreEncoder.setPassword(pgPassword);
        datastoreEncoder.setExposePrimaryKeys(exposePrimaryKeys);
        datastoreEncoder.setValidateConnections(validateConnections);
        datastoreEncoder.setPrimaryKeyMetadataTable(primaryKeyMetadataTable);
        return datastoreEncoder;
    }
    
}

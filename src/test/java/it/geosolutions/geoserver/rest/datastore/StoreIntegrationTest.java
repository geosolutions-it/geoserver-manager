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
package it.geosolutions.geoserver.rest.datastore;

import java.net.MalformedURLException;

import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSAbstractDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSOracleNGDatastoreEncoder;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStoreManager;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <P>
 * Since these tests require a running Store instance, this is more like integration tests.<br/>
 *
 * @author carlo cancellieri - GeoSolutions
 *
 * @see GeoserverRESTTest
 */
public abstract class StoreIntegrationTest extends GeoserverRESTTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(StoreIntegrationTest.class);

    private final GeoServerRESTStoreManager storeManager;
   
    /**
     * ignore integration tests
     */
    private final boolean ignore;
    
    public boolean isIgnore() {
        return ignore;
    }

    /**
     * 
     * @param ignore false if this test shoudl be disabled
     * @throws IllegalArgumentException
     * @throws MalformedURLException
     */
    public StoreIntegrationTest(boolean ignore) throws IllegalArgumentException, MalformedURLException {
        super();
        this.storeManager = new GeoServerRESTStoreManager(URL, RESTUSER, RESTPW);
        this.ignore=ignore;
    }
    
    public abstract GSAbstractStoreEncoder getStoreEncoderTest();

    @Test
    public void testCreateDeleteDatastore() throws IllegalArgumentException, MalformedURLException {
        if (!enabled()) {
            return;
        }
        deleteAll();

        assertTrue(publisher.createWorkspace(DEFAULT_WS));
        
        // creation test
        GSAbstractStoreEncoder storeEncoder=getStoreEncoderTest();

        String storeName = storeEncoder.getName();
//        String description = storeEncoder.getDescription();
        
        boolean created = storeManager.create(DEFAULT_WS, storeEncoder);

        if( ! ignore )
            assertTrue("Datastore not created", created);
        else if( ! created)
            LOGGER.error("*** store " + storeName + " has not been created.");


        RESTDataStore datastore = reader.getDatastore(DEFAULT_WS, storeName);
        assertNotNull(datastore);
        LOGGER.info("The type of the created datastore is: " + datastore.getStoreType());

        // removing test
        boolean removed = storeManager.remove(DEFAULT_WS, storeEncoder, true);
        if( ! ignore )
            assertTrue("Datastore not removed", removed);
        else if( ! removed )
            LOGGER.error("*** Datastore " + storeName + " has not been removed.");
        
        assertTrue(publisher.removeWorkspace(DEFAULT_WS, false));
    }
}

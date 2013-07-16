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

import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSOracleNGDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStoreManager;

import java.net.MalformedURLException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <P>
 * Since these tests require a running Store instance, this is more like integration tests.<br/>
 * 
 * For testing that a datastore is properly configured, a layer publication has
 * to be attempted. For this, the 'states' data (states shapefile - available in
 * testdata/states.zip) has to be imported in the corresponding store.
 * Note: For Oracle NG this name has to be uppercase (STATES).
 * 
 * @author carlo cancellieri - GeoSolutions
 * @author emmanuel blondel
 *
 * @see GeoserverRESTTest
 */
public abstract class StoreIntegrationTest extends GeoserverRESTTest {

    protected final GeoServerRESTStoreManager storeManager;
   
    /**
     * ignore integration tests
     */
    protected final boolean ignore;

    private final static Logger LOGGER = LoggerFactory.getLogger(StoreIntegrationTest.class);
    
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
    
            //check if the datastore is properly configured in GS for publishing layers
            String layername = "states";
            if(storeEncoder instanceof GSOracleNGDatastoreEncoder)
            	layername = layername.toUpperCase();
            
            GSFeatureTypeEncoder fte = new GSFeatureTypeEncoder();
            fte.setName(layername);
            fte.setTitle(layername);
            fte.setNativeCRS("EPSG:4326");
            fte.setDescription("desc");
            fte.setEnabled(true);
            GSLayerEncoder layerEncoder = new GSLayerEncoder();
            layerEncoder.setEnabled(true);
            layerEncoder.setQueryable(true);
            layerEncoder.setDefaultStyle("polygon");
            
            boolean published = publisher.publishDBLayer(DEFAULT_WS, storeName, fte, layerEncoder);
            if(!ignore){
            	assertTrue("Test layer not published", published);
            }else if(!published){
    			LOGGER.error("*** Test layer "
    					+ layername
    					+ " has not been published. Problem in datastore configuration");
    		}
            
            // removing test
            boolean removed = storeManager.remove(DEFAULT_WS, storeEncoder, true);
            if( ! ignore )
                assertTrue("Datastore not removed", removed);
            else if( ! removed )
                LOGGER.error("*** Datastore " + storeName + " has not been removed.");
            
            assertTrue(publisher.removeWorkspace(DEFAULT_WS, false));
        }
}

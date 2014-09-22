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

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher.StoreType;
import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import it.geosolutions.geoserver.rest.decoder.RESTCoverageStore;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Testcase for publishing layers on geoserver.
 * We need a running GeoServer to properly run the tests. 
 * If such geoserver instance cannot be contacted, tests will be skipped.
 *
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GeoserverRESTGeoTiffTest extends GeoserverRESTTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoserverRESTGeoTiffTest.class);

    String storeName = "testRESTStoreGeotiff";
    String layerName = "resttestdem";
    
    @Test
    public void testExternalGeotiff() throws FileNotFoundException, IOException {
        if (!enabled()) return;
        deleteAll();

        File geotiff = new ClassPathResource("testdata/resttestdem.tif").getFile();

        assertTrue(reader.getWorkspaces().isEmpty());
        assertTrue(publisher.createWorkspace(DEFAULT_WS));


        // known state?
        assertFalse("Cleanup failed", existsLayer(layerName));
        
        // Test exists
        assertFalse(reader.existsLayer(DEFAULT_WS, layerName));

        // test insert
        boolean pc = publisher.publishExternalGeoTIFF(DEFAULT_WS, storeName, geotiff, layerName,"EPSG:4326",ProjectionPolicy.FORCE_DECLARED,"raster");
        assertTrue("publish() failed", pc);
        assertTrue(existsLayer(layerName));
        assertFalse(reader.existsLayer(DEFAULT_WS, layerName));
        LOGGER.info("Published "+pc);
        RESTCoverageStore reloadedCS = reader.getCoverageStore(DEFAULT_WS, storeName);

        assertEquals(storeName, reloadedCS.getName());
        assertEquals(DEFAULT_WS, reloadedCS.getWorkspaceName());

        //test delete
        assertTrue("Unpublish() failed", publisher.unpublishCoverage(DEFAULT_WS, storeName, layerName));
        assertTrue("Unpublish() failed", publisher.removeCoverageStore(DEFAULT_WS, storeName));
        assertFalse("Bad unpublish()",   publisher.unpublishCoverage(DEFAULT_WS, storeName, layerName));
        assertFalse(existsLayer(layerName));
    }
   
    @Test
    public void testGeotiff() throws FileNotFoundException, IOException {
        if (!enabled()) return;
        deleteAll();

        File geotiff = new ClassPathResource("testdata/resttestdem.tif").getFile();
        
        assertTrue(reader.getWorkspaces().isEmpty());
        assertTrue(publisher.createWorkspace(DEFAULT_WS));

        // known state?
        assertFalse("Cleanup failed", existsLayer(layerName));

        // test insert
        boolean pub = publisher.publishGeoTIFF(DEFAULT_WS, storeName, geotiff);
        
        assertNotNull("publish() failed", pub);
        // Test exists
        assertTrue(reader.existsCoveragestore(DEFAULT_WS, storeName));
        assertTrue(reader.existsCoverage(DEFAULT_WS, storeName, storeName));

        pub = publisher.publishGeoTIFF(DEFAULT_WS, storeName+"another", "layername", geotiff);
        
        assertTrue("publish() failed", pub);
        
        double[] bbox = {-103.85, 44.38, -103.62, 44.50};
        pub = publisher.publishGeoTIFF(DEFAULT_WS, storeName+"another_complex", storeName+"another_complex", geotiff, "EPSG:4326", ProjectionPolicy.REPROJECT_TO_DECLARED, "raster", bbox);
        
        assertTrue("publish() failed", pub);

        //delete
        assertTrue("Unpublish() failed", publisher.removeCoverageStore(DEFAULT_WS, storeName,true));
        // Test not exists
        assertFalse(reader.existsCoveragestore(DEFAULT_WS, storeName));
    }
    

    @Test
    public void testReloadCoverageStore() throws FileNotFoundException, IOException {
        if (!enabled()) return;
        deleteAll();

        File geotiff = new ClassPathResource("testdata/resttestdem.tif").getFile();
        
        assertTrue(publisher.createWorkspace(DEFAULT_WS));
        
        // test insert
        boolean pub = publisher.publishGeoTIFF(DEFAULT_WS, storeName, geotiff);
        
        assertNotNull("publish() failed", pub);

        // test reload
        assertTrue(publisher.reloadStore(DEFAULT_WS, storeName, StoreType.COVERAGESTORES));   
    }
}

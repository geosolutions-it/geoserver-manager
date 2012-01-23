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
import it.geosolutions.geoserver.rest.decoder.RESTCoverageStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

/**
 * Testcase for publishing layers on geoserver.
 * We need a running GeoServer to properly run the tests. 
 * If such geoserver instance cannot be contacted, tests will be skipped.
 *
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GeoserverRESTGeoTiffTest extends GeoserverRESTTest {

    private final static Logger LOGGER = Logger.getLogger(GeoserverRESTGeoTiffTest.class);

    public GeoserverRESTGeoTiffTest(String testName) {
        super(testName);
    }

    public void testExternalGeotiff() throws FileNotFoundException, IOException {
        if (!enabled()) return;
        deleteAll();

        String storeName = "testRESTStoreGeotiff";
        String layerName = "resttestdem";

        assertTrue(reader.getWorkspaces().isEmpty());
        assertTrue(publisher.createWorkspace(DEFAULT_WS));

        File geotiff = new ClassPathResource("testdata/resttestdem.tif").getFile();

        // known state?
        assertFalse("Cleanup failed", existsLayer(layerName));

        // test insert
        RESTCoverageStore pc = publisher.publishExternalGeoTIFF(DEFAULT_WS, storeName, geotiff, null, null);
        assertNotNull("publish() failed", pc);
        assertTrue(existsLayer(layerName));
        LOGGER.info(pc);
        RESTCoverageStore reloadedCS = reader.getCoverageStore(DEFAULT_WS, storeName);

        assertEquals(pc.getName(), reloadedCS.getName());
        assertEquals(pc.getWorkspaceName(), reloadedCS.getWorkspaceName());

        //test delete
        assertTrue("Unpublish() failed", publisher.unpublishCoverage(DEFAULT_WS, storeName, layerName));
        assertTrue("Unpublish() failed", publisher.removeCoverageStore(DEFAULT_WS, storeName));
        assertFalse("Bad unpublish()",   publisher.unpublishCoverage(DEFAULT_WS, storeName, layerName));
        assertFalse(existsLayer(layerName));
    }
    
    public void testGeotiff() throws FileNotFoundException, IOException {
        if (!enabled()) return;
        deleteAll();

        String storeName = "testRESTStoreGeotiff";
        String layerName = "resttestdem";

        assertTrue(reader.getWorkspaces().isEmpty());
        assertTrue(publisher.createWorkspace(DEFAULT_WS));

        File geotiff = new ClassPathResource("testdata/resttestdem.tif").getFile();

        // known state?
        assertFalse("Cleanup failed", existsLayer(layerName));

        // test insert
        boolean pub = publisher.publishGeoTIFF(DEFAULT_WS, storeName, geotiff);
        
        assertNotNull("publish() failed", pub);

        //delete
        assertTrue("Unpublish() failed", publisher.removeCoverageStore(DEFAULT_WS, storeName,true));
    }
}

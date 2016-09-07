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
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.core.io.ClassPathResource;

/**
 * Testcase for publishing layers on geoserver.
 * We need a running GeoServer to properly run the tests. 
 * If such geoserver instance cannot be contacted, tests will be skipped.
 *
 * @author etj
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GeoserverRESTWorkspaceTest extends GeoserverRESTTest {

    @Test
    public void testWorkspaces() {
        if (!enabled()) return;
        deleteAll();

        assertEquals(0, reader.getWorkspaces().size());

        assertTrue(publisher.createWorkspace("WS1"));
        assertTrue(publisher.createWorkspace("WS2"));
        assertEquals(2, reader.getWorkspaces().size());

        assertFalse(publisher.createWorkspace("WS2"));
        assertEquals(2, reader.getWorkspaces().size());
    }
    
    /**
     * remove workspace and all of its contents
     * @throws IOException
     */
    @Test
    public void testWorkspaceRemoval() throws IOException {
        if (!enabled()) return;
        	deleteAll();

        String storeName = "testRESTStoreGeotiff";
        String layerName = "resttestdem";

        assertTrue(reader.getWorkspaces().isEmpty());
        assertTrue(publisher.createWorkspace(DEFAULT_WS));
        // test exists
        assertTrue(reader.existsWorkspace(DEFAULT_WS));

        File geotiff = new ClassPathResource("testdata/resttestdem.tif").getFile();

        // known state?
        assertFalse("Cleanup failed", existsLayer(layerName));

        // test insert
        boolean pc = publisher.publishExternalGeoTIFF(DEFAULT_WS, storeName, geotiff, layerName, "EPSG:4326",ProjectionPolicy.REPROJECT_TO_DECLARED,"raster");
        
        // remove workspace and all of its contents
        assertTrue(publisher.removeWorkspace(DEFAULT_WS,true));
        // Test not exists
        assertFalse(reader.existsWorkspace(DEFAULT_WS));
    }
}

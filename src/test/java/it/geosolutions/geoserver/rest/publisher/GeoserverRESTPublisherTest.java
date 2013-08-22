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
import it.geosolutions.geoserver.rest.decoder.RESTLayer;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Testcase for publishing layers on geoserver.
 * We need a running GeoServer to properly run the tests. 
 * If such geoserver instance cannot be contacted, tests will be skipped.
 *
 * @author etj
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GeoserverRESTPublisherTest extends GeoserverRESTTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoserverRESTPublisherTest.class);

    protected void cleanupTestFT(String layerName, String ns, String storeName) {
        // dry run delete to work in a known state
        RESTLayer testLayer = reader.getLayer(layerName);
        if (testLayer != null) {
            LOGGER.info("Clearing stale test layer " + layerName);
            boolean ok = publisher.unpublishFeatureType(ns, storeName, layerName);
            if (!ok) {
                fail("Could not unpublish layer " + layerName);
            }
        }
        if (publisher.removeDatastore(ns, storeName)) {
            LOGGER.info("Cleared stale datastore " + storeName);
        }

        assertFalse("Cleanup failed", existsLayer(layerName));
    }

    @Test
    public void testDeleteUnexistingCoverage() throws FileNotFoundException, IOException {
        if (!enabled()) {
            return;
        }
//        Assume.assumeTrue(enabled);

        String wsName = "this_ws_does_not_exist";
        String storeName = "this_store_does_not_exist";
        String layerName = "this_layer_does_not_exist";

        boolean ok = publisher.unpublishCoverage(wsName, storeName, layerName);
        assertFalse("unpublished not existing layer", ok);
    }

    @Test
    public void testDeleteUnexistingFeatureType() throws FileNotFoundException, IOException {
        if (!enabled()) {
            return;
        }
//        Assume.assumeTrue(enabled);

        String wsName = "this_ws_does_not_exist";
        String storeName = "this_store_does_not_exist";
        String layerName = "this_layer_does_not_exist";

        boolean ok = publisher.unpublishFeatureType(wsName, storeName, layerName);
        assertFalse("unpublished not existing layer", ok);
    }

    @Test
    public void testDeleteUnexistingDatastore() throws FileNotFoundException, IOException {
        if (!enabled()) {
            return;
        }
//        Assume.assumeTrue(enabled);

        String wsName = "this_ws_does_not_exist";
        String storeName = "this_store_does_not_exist";

        boolean ok = publisher.removeDatastore(wsName, storeName,true);
        assertFalse("removed not existing datastore", ok);
    }

    //	public void testDeleteUnexistingFT() throws FileNotFoundException, IOException {
//		String wsName = "this_ws_does_not_exist";
//		String storeName = "this_store_does_not_exist";
//		String layerName = "this_layer_does_not_exist";
//
//		boolean ok = publisher.unpublishFT(wsName, storeName, layerName);
//		assertFalse("unpublished not existing layer", ok);
//	}
    
}

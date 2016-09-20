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

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.GeoserverRESTTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Testcase for publishing layers on geoserver.
 * We need a running GeoServer to properly run the tests. 
 * If such geoserver instance cannot be contacted, tests will be skipped.
 *
  * @author Carl Schroedl - cschroedl@usgs.gov
 */
public class GeoserverRESTRecalculateTest extends GeoserverRESTTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoserverRESTRecalculateTest.class);
    
    @Before
    @Override
    public void before(){
        super.before();
        
        if (!enabled()) 
            return;
        
        deleteAll();
        assertTrue(publisher.createWorkspace(DEFAULT_WS));
    }
    
    @After
    public void cleanUp(){
        if (!enabled()) 
            return;
        
        deleteAll();
    }
    
    @Test
    public void testRecalculateFeatureTypeBBox() throws FileNotFoundException, IOException {
        if (!enabled()) 
            return;

        String storeName = "resttestshp";
        String layerName = "cities";

        File zipFile = new ClassPathResource("testdata/resttestshp.zip").getFile();
        assertTrue(publisher.publishShp(DEFAULT_WS, storeName, layerName, zipFile));
        
        for(GeoServerRESTPublisher.BBoxRecalculationMode recalcMode : GeoServerRESTPublisher.BBoxRecalculationMode.values()){
            boolean recalculated = publisher.recalculateFeatureTypeBBox(DEFAULT_WS,storeName, layerName, recalcMode, true);
            assertTrue("recalculateBBox failed with recalculation mode '" +recalcMode.toString() + "'.", recalculated);
        }
    }
        
    @Test
    public void testRecalculateCoverageBBox() throws FileNotFoundException, IOException {
        if (!enabled()) 
            return;
        
        String storeName = "testRESTStoreArcGrid";
        String layerName = "resttestdem";
        
        File arcgrid = new ClassPathResource("testdata/resttestdem.asc").getFile();
        
        assertTrue(publisher.publishArcGrid(DEFAULT_WS, storeName, layerName, arcgrid));
        
        for(GeoServerRESTPublisher.BBoxRecalculationMode recalcMode : GeoServerRESTPublisher.BBoxRecalculationMode.values()){
            boolean recalculated = publisher.recalculateCoverageBBox(DEFAULT_WS,storeName, layerName, recalcMode, true);
            assertTrue("recalculateBBox failed with recalculation mode '" +recalcMode.toString() + "'.", recalculated);
        }
    }
}

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

package it.geosolutions.geoserver.rest;


import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Testcase for publishing layers on geoserver.
 *
 * @author etj
 */
public class ConfigTest extends GeoserverRESTTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigTest.class);

    private static final String DEFAULT_WS = "geosolutions";

    @Test
    public void insertStyles() throws FileNotFoundException, IOException {
    	if(!enabled()){
    		LOGGER.info("Skipping test "+"insertStyles"+"for class:"+this.getClass().getSimpleName());
    		return;
    	}
    	deleteAllStyles();
    	
        File sldDir = new ClassPathResource("testdata").getFile();
        for(File sldFile : sldDir.listFiles((FilenameFilter)new SuffixFileFilter(".sld"))) {
            LOGGER.info("Existing styles: " + reader.getStyles().getNames());
            String basename = FilenameUtils.getBaseName(sldFile.toString());
            LOGGER.info("Publishing style " + sldFile + " as " + basename);
            assertTrue("Cound not publish " + sldFile, publisher.publishStyle(sldFile, basename));
        }
    }

    @Test
    public void insertExternalGeotiff() throws FileNotFoundException, IOException {
    	if(!enabled()){
    		LOGGER.info("Skipping test "+"insertExternalGeotiff"+"for class:"+this.getClass().getSimpleName());
    		return;
    	}
    	deleteAll();
    	
        String storeName = "testRESTStoreGeotiff";
        String layerName = "resttestdem";

        publisher.createWorkspace(DEFAULT_WS);
        publisher.publishStyle(new File(new ClassPathResource("testdata").getFile(),"raster.sld"));
        
        File geotiff = new ClassPathResource("testdata/resttestdem.tif").getFile();
        boolean pc = publisher.publishExternalGeoTIFF(DEFAULT_WS, storeName, geotiff, layerName,"EPSG:4326",ProjectionPolicy.FORCE_DECLARED,"raster");
        
        assertTrue(pc);
    }
    
    @Test
    public void insertExternalShape() throws FileNotFoundException, IOException {
    	if(!enabled()){
    		LOGGER.info("Skipping test "+"insertExternalShape"+"for class:"+this.getClass().getSimpleName());
    		return;
    	}
    	deleteAll();
    	
    	publisher.createWorkspace(DEFAULT_WS);
    	publisher.publishStyle(new File(new ClassPathResource("testdata").getFile(),"default_point.sld"));
    	
        File zipFile = new ClassPathResource("testdata/resttestshp.zip").getFile();

        boolean published = publisher.publishShp(DEFAULT_WS, "anyname", "cities", zipFile, "EPSG:41001", "default_point");
        assertTrue("publish() failed", published);

        //test delete
        boolean ok = publisher.unpublishFeatureType(DEFAULT_WS, "anyname", "cities");
        assertTrue("Unpublish() failed", ok);
        // remove also datastore
        boolean dsRemoved = publisher.removeDatastore(DEFAULT_WS, "anyname");
        assertTrue("removeDatastore() failed", dsRemoved);
    }

}
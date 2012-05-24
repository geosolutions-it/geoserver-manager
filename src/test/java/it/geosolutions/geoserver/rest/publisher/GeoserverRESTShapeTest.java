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

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher.UploadMethod;
import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Testcase for publishing layers on geoserver.
 * We need a running GeoServer to properly run the tests. 
 * If such geoserver instance cannot be contacted, tests will be skipped.
 *
 * @author etj
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GeoserverRESTShapeTest extends GeoserverRESTTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoserverRESTShapeTest.class);

    public GeoserverRESTShapeTest(String testName) {
        super(testName);
    }
    

    public void testPublishDeleteShapeZip() throws FileNotFoundException, IOException {
        if (!enabled()) {
            return;
        }
//        Assume.assumeTrue(enabled);
        deleteAllWorkspaces();
        assertTrue(publisher.createWorkspace(DEFAULT_WS));

        String storeName = "resttestshp";
        String layerName = "cities";

        File zipFile = new ClassPathResource("testdata/resttestshp.zip").getFile();
        
        
        // test insert
        boolean published = publisher.publishShp(DEFAULT_WS, storeName, layerName, zipFile);
        assertTrue("publish() failed", published);
        assertTrue(existsLayer(layerName));

        RESTLayer layer = reader.getLayer(layerName);

        LOGGER.info("Layer style is " + layer.getDefaultStyle());

        //test delete
        boolean ok = publisher.unpublishFeatureType(DEFAULT_WS, storeName, layerName);
        assertTrue("Unpublish() failed", ok);
        assertFalse(existsLayer(layerName));

        // remove also datastore
        boolean dsRemoved = publisher.removeDatastore(DEFAULT_WS, storeName,false);
        assertTrue("removeDatastore() failed", dsRemoved);

    }


    public void testPublishDeleteExternalComplexShapeZip() throws FileNotFoundException, IOException {
        if (!enabled()) {
            return;
        }
//        Assume.assumeTrue(enabled);
        deleteAllWorkspaces();
        assertTrue(publisher.createWorkspace(DEFAULT_WS));

        String storeName = "resttestshp_complex";
        String datasetName = "cities";

        File zipFile = new ClassPathResource("testdata/shapefile/cities.shp").getFile();
        
        // test insert
        boolean published = publisher.publishShp(DEFAULT_WS, storeName, new NameValuePair[]{new NameValuePair("charset", "UTF-8")},datasetName, UploadMethod.external, zipFile.toURI(), "EPSG:4326",ProjectionPolicy.REPROJECT_TO_DECLARED,"polygon");
        assertTrue("publish() failed", published);
        assertTrue(existsLayer(datasetName));
        

        RESTLayer layer = reader.getLayer(datasetName);

        LOGGER.info("Layer style is " + layer.getDefaultStyle());

        //test delete
        boolean ok = publisher.unpublishFeatureType(DEFAULT_WS, storeName, datasetName);
        assertTrue("Unpublish() failed", ok);
        assertFalse(existsLayer(datasetName));

        // remove also datastore
        boolean dsRemoved = publisher.removeDatastore(DEFAULT_WS, storeName,false);
        assertTrue("removeDatastore() failed", dsRemoved);
    }
    
    public void testPublishDeleteComplexShapeZip() throws FileNotFoundException, IOException {
        if (!enabled()) {
            return;
        }
//        Assume.assumeTrue(enabled);
        deleteAllWorkspaces();
        assertTrue(publisher.createWorkspace(DEFAULT_WS));

        String storeName = "resttestshp_complex";
        String datasetName = "cities";

        File zipFile = new ClassPathResource("testdata/resttestshp.zip").getFile();
        
        // test insert
        boolean published = publisher.publishShp(DEFAULT_WS, storeName, new NameValuePair[]{new NameValuePair("charset", "UTF-8")},datasetName, UploadMethod.file, zipFile.toURI(), "EPSG:4326",ProjectionPolicy.REPROJECT_TO_DECLARED,"polygon");
        assertTrue("publish() failed", published);
        assertTrue(existsLayer(datasetName));
        

        RESTLayer layer = reader.getLayer(datasetName);

        LOGGER.info("Layer style is " + layer.getDefaultStyle());

        //test delete
        boolean ok = publisher.unpublishFeatureType(DEFAULT_WS, storeName, datasetName);
        assertTrue("Unpublish() failed", ok);
        assertFalse(existsLayer(datasetName));

        // remove also datastore
        boolean dsRemoved = publisher.removeDatastore(DEFAULT_WS, storeName,false);
        assertTrue("removeDatastore() failed", dsRemoved);
    }

    public void testPublishDeleteStyledShapeZip() throws FileNotFoundException, IOException {
        if (!enabled()) {
            return;
        }
//        Assume.assumeTrue(enabled);

        String ns = "geosolutions";
        String storeName = "resttestshp";
        String layerName = "cities";
        final String styleName = "restteststyle";

        File zipFile = new ClassPathResource("testdata/resttestshp.zip").getFile();
        publisher.removeDatastore(DEFAULT_WS, storeName,true);
        publisher.removeStyle(styleName);
        
        File sldFile = new ClassPathResource("testdata/restteststyle.sld").getFile();

        // insert style
        boolean sldpublished = publisher.publishStyle(sldFile); // Will take the name from sld contents
        assertTrue("style publish() failed", sldpublished);
        assertTrue(reader.existsStyle(styleName));

        // test insert
        boolean published = publisher.publishShp(ns, storeName, layerName, zipFile, "EPSG:4326", styleName);
        assertTrue("publish() failed", published);
        assertTrue(existsLayer(layerName));

        RESTLayer layer = reader.getLayer(layerName);
//        RESTLayer layerDecoder = new RESTLayer(layer);
        LOGGER.info("Layer style is " + layer.getDefaultStyle());
        assertEquals("Style not assigned properly", styleName, layer.getDefaultStyle());

        // remove also datastore
        boolean dsRemoved = publisher.removeDatastore(ns, storeName,true);
        assertTrue("removeDatastore() failed", dsRemoved);

        //test delete style
        boolean oksld = publisher.removeStyle(styleName);
        assertTrue("Unpublish() failed", oksld);
        assertFalse(reader.existsStyle(styleName));
    }

    public void testPublishDeleteShapeZipWithParams() throws FileNotFoundException, IOException {
        if (!enabled()) {
            return;
        }
//        Assume.assumeTrue(enabled);
        deleteAllWorkspaces();
        assertTrue(publisher.createWorkspace(DEFAULT_WS));

        String storeName = "resttestshp";
        String layerName = "cities";

        File zipFile = new ClassPathResource("testdata/resttestshp.zip").getFile();

        // known state?
        publisher.removeDatastore(DEFAULT_WS, storeName,true);

        // test insert
        boolean published = publisher.publishShp(DEFAULT_WS, storeName, layerName, zipFile,"EPSG:4326",new NameValuePair("charset","UTF-8"));
        assertTrue("publish() failed", published);
        assertTrue(existsLayer(layerName));

        RESTLayer layer = reader.getLayer(layerName);

        LOGGER.info("Layer style is " + layer.getDefaultStyle());

        //test delete
        boolean ok = publisher.unpublishFeatureType(DEFAULT_WS, storeName, layerName);
        assertTrue("Unpublish() failed", ok);
        assertFalse(existsLayer(layerName));

        // remove also datastore
        boolean dsRemoved = publisher.removeDatastore(DEFAULT_WS, storeName);
        assertTrue("removeDatastore() failed", dsRemoved);

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

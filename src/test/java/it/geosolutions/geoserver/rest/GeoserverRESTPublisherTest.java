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

import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTCoverageStore;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.core.io.ClassPathResource;

/**
 * Testcase for publishing layers on geoserver.
 * We need a running GeoServer to properly run the tests. 
 * If such geoserver instance cannot be contacted, tests will be skipped.
 *
 * @author etj
 */
public class GeoserverRESTPublisherTest extends GeoserverRESTTest {

    private final static Logger LOGGER = Logger.getLogger(GeoserverRESTPublisherTest.class);
    private static final String DEFAULT_WS = "it.geosolutions";

    public GeoserverRESTPublisherTest(String testName) {
        super(testName);
    }

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

    public void testStyles() throws IOException {
        if (!enabled()) return;
        deleteAll();

        assertEquals(0, reader.getStyles().size());

        final String styleName = "restteststyle";
        File sldFile = new ClassPathResource("testdata/restteststyle.sld").getFile();

        // insert style
        assertTrue(publisher.publishStyle(sldFile));
        assertTrue(reader.existsStyle(styleName));

        assertFalse(publisher.publishStyle(sldFile));
        assertTrue(reader.existsStyle(styleName));

        String sld = reader.getSLD(styleName);
        assertNotNull(sld);

        Element styleEl = JDOMBuilder.buildElement(sld);
        assertNotNull(styleEl);

        Namespace SLDNS = Namespace.getNamespace("sld", "http://www.opengis.net/sld");

        try{

            assertEquals(styleName, styleEl.getChild("NamedLayer", SLDNS).getChild("Name",SLDNS).getText());
            assertEquals("STYLE FOR TESTING PURPOSES", styleEl.getChild("NamedLayer", SLDNS).getChild("UserStyle", SLDNS).getChild("Title", SLDNS).getText());
        } catch(NullPointerException npe) {
            fail("Error in SLD");
        }

//        assertEquals(1475, sld.length());

        assertEquals(1, reader.getStyles().size());
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

    protected void cleanupTestStyle(final String styleName) {
        // dry run delete to work in a known state
        if (reader.existsStyle(styleName)) {
            LOGGER.info("Clearing stale test style " + styleName);
            boolean ok = publisher.removeStyle(styleName);
            if (!ok) {
                fail("Could not unpublish style " + styleName);
            }
        }
        assertFalse("Cleanup failed", reader.existsStyle(styleName));
    }

    public void testPublishDeleteShapeZip() throws FileNotFoundException, IOException {
        if (!enabled()) {
            return;
        }
//        Assume.assumeTrue(enabled);

        String ns = "it.geosolutions";
        String storeName = "resttestshp";
        String layerName = "cities";

        File zipFile = new ClassPathResource("testdata/resttestshp.zip").getFile();

        // known state?
        cleanupTestFT(layerName, ns, storeName);

        // test insert
        boolean published = publisher.publishShp(ns, storeName, layerName, zipFile);
        assertTrue("publish() failed", published);
        assertTrue(existsLayer(layerName));

        RESTLayer layer = reader.getLayer(layerName);

        LOGGER.info("Layer style is " + layer.getDefaultStyle());

        //test delete
        boolean ok = publisher.unpublishFeatureType(ns, storeName, layerName);
        assertTrue("Unpublish() failed", ok);
        assertFalse(existsLayer(layerName));

        // remove also datastore
        boolean dsRemoved = publisher.removeDatastore(ns, storeName);
        assertTrue("removeDatastore() failed", dsRemoved);

    }

    public void testPublishDeleteStyledShapeZip() throws FileNotFoundException, IOException {
        if (!enabled()) {
            return;
        }
//        Assume.assumeTrue(enabled);

        String ns = "it.geosolutions";
        String storeName = "resttestshp";
        String layerName = "cities";

        File zipFile = new ClassPathResource("testdata/resttestshp.zip").getFile();
        cleanupTestFT(layerName, ns, storeName);

        final String styleName = "restteststyle";
        File sldFile = new ClassPathResource("testdata/restteststyle.sld").getFile();
        cleanupTestStyle(styleName);

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

        //test delete
        boolean ok = publisher.unpublishFeatureType(ns, storeName, layerName);
        assertTrue("Unpublish() failed", ok);
        assertFalse(existsLayer(layerName));

        // remove also datastore
        boolean dsRemoved = publisher.removeDatastore(ns, storeName);
        assertTrue("removeDatastore() failed", dsRemoved);

        //test delete style
        boolean oksld = publisher.removeStyle(styleName);
        assertTrue("Unpublish() failed", oksld);
        assertFalse(reader.existsStyle(styleName));
    }

    public void testPublishDeleteStyleFile() throws FileNotFoundException, IOException {
        if (!enabled()) {
            return;
        }
//        Assume.assumeTrue(enabled);
        final String styleName = "restteststyle";

        File sldFile = new ClassPathResource("testdata/restteststyle.sld").getFile();

        // known state?
        cleanupTestStyle(styleName);

        // test insert
        boolean published = publisher.publishStyle(sldFile); // Will take the name from sld contents
        assertTrue("publish() failed", published);
        assertTrue(reader.existsStyle(styleName));

        //test delete
        boolean ok = publisher.removeStyle(styleName);
        assertTrue("Unpublish() failed", ok);
        assertFalse(reader.existsStyle(styleName));
    }

    public void testPublishDeleteStyleString() throws FileNotFoundException, IOException {
        if (!enabled()) {
            return;
        }
//        Assume.assumeTrue(enabled);
        final String styleName = "restteststyle";

        File sldFile = new ClassPathResource("testdata/restteststyle.sld").getFile();

        // known state?
        cleanupTestStyle(styleName);

        // test insert
        String sldContent = IOUtils.toString(new FileInputStream(sldFile));

        boolean published = publisher.publishStyle(sldContent);  // Will take the name from sld contents
        assertTrue("publish() failed", published);
        assertTrue(reader.existsStyle(styleName));

        //test delete
        boolean ok = publisher.removeStyle(styleName);
        assertTrue("Unpublish() failed", ok);
        assertFalse(reader.existsStyle(styleName));
    }

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

    public void testDeleteUnexistingDatastore() throws FileNotFoundException, IOException {
        if (!enabled()) {
            return;
        }
//        Assume.assumeTrue(enabled);

        String wsName = "this_ws_does_not_exist";
        String storeName = "this_store_does_not_exist";

        boolean ok = publisher.removeDatastore(wsName, storeName);
        assertFalse("removed not existing datastore", ok);
    }


    public void testUpdateDefaultStyle() throws FileNotFoundException, IOException {
        if (!enabled()) {
            return;
        }

        String ns = "it.geosolutions";
        String storeName = "resttestshp";
        String layerName = "cities";


        final String styleName = "restteststyle";
        {
            File sldFile = new ClassPathResource("testdata/restteststyle.sld").getFile();
            cleanupTestStyle(styleName);
            boolean sldpublished = publisher.publishStyle(sldFile); // Will take the name from sld contents
            assertTrue("style publish() failed", sldpublished);
            assertTrue(reader.existsStyle(styleName));
        }

        final String styleName2 = "restteststyle2";
        {
            File sldFile = new ClassPathResource("testdata/restteststyle2.sld").getFile();
            cleanupTestStyle(styleName2);
            boolean sldpublished = publisher.publishStyle(sldFile); // Will take the name from sld contents
            assertTrue("style publish() failed", sldpublished);
            assertTrue(reader.existsStyle(styleName2));
        }


        File zipFile = new ClassPathResource("testdata/resttestshp.zip").getFile();

        // known state?
        cleanupTestFT(layerName, ns, storeName);

        // test insert
        boolean published = publisher.publishShp(ns, storeName, layerName, zipFile, "EPSG:4326", styleName);
        assertTrue("publish() failed", published);
        assertTrue(existsLayer(layerName));

        {
            RESTLayer layer = reader.getLayer(layerName);
            LOGGER.info("Layer style is " + layer.getDefaultStyle());
            assertEquals(styleName, layer.getDefaultStyle());
        }

        GSLayerEncoder le = new GSLayerEncoder();
        le.addDefaultStyle(styleName2);
        publisher.configureLayer(le, layerName);

        {
            RESTLayer layer = reader.getLayer(layerName);
            LOGGER.info("Layer style is " + layer.getDefaultStyle());
            assertEquals(styleName2, layer.getDefaultStyle());
        }


        // remove layer and datastore
        boolean ok = publisher.unpublishFeatureType(ns, storeName, layerName);
        assertFalse(existsLayer(layerName));
        boolean dsRemoved = publisher.removeDatastore(ns, storeName);
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
    private boolean existsLayer(String layername) {
        return reader.getLayer(layername) != null;
    }
}

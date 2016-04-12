/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2015 GeoSolutions S.A.S.
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

import it.geosolutions.geoserver.rest.decoder.gwc.GWCRESTWMSLayer;
import it.geosolutions.geoserver.rest.decoder.gwc.diskquota.GWCExpirationPolicy;
import it.geosolutions.geoserver.rest.decoder.gwc.diskquota.GWCQuotaConfiguration;
import it.geosolutions.geoserver.rest.decoder.gwc.masstruncate.MassTruncateRequests;
import it.geosolutions.geoserver.rest.decoder.gwc.seed.GWCTruncateSeedType;
import it.geosolutions.geoserver.rest.decoder.gwc.seed.GlobalSeedStatus;
import it.geosolutions.geoserver.rest.decoder.gwc.statistics.GWCInMemoryCacheStatisticsXML;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.JsonMappingException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Initializes REST params.
 * <P>
 * <B>These tests are destructive, so you have to explicitly enable them</B> by
 * setting the env var <TT>resttest</TT> to <TT>true</TT>.
 * <P>
 * The target geoserver instance can be customized by defining the following env
 * vars:
 * <ul>
 * <LI><TT>resturl</TT> (default
 * <TT>http://localhost:8080/geowebcache</TT>)</LI>
 * <LI><TT>restuser</TT> (default: <TT>geowebcache</TT>)</LI>
 * <LI><TT>restpw</TT> (default: <TT>secured</TT>)</LI>
 * </ul>
 * Maven command execution: mvn test -Dtest=GeoWebCacheRESTTest
 * -Dgwcmgr_resttest=true
 *
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class GeoWebCacheRESTTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoWebCacheRESTTest.class);

    @Rule
    public TestName _testName = new TestName();

    public static final String RESTURL;

    public static final String RESTUSER;

    public static final String RESTPW;

    // geowebcache target version
//    public static final String GWC_VERSION;
    public static URL URL;

    public static GeoWebCacheRESTManager geoWebCache;

    private static boolean enabled = false;

    private static Boolean existgwc = null;

    static {
        RESTURL = getenv("gwcmgr_resturl", "http://localhost:8989/geowebcache");
        RESTUSER = getenv("gwcmgr_restuser", "geowebcache");
        RESTPW = getenv("gwcmgr_restpw", "secured");
//        GWC_VERSION = getenv("gwcmgr_version", "1.8.0");

        // These tests will destroy data, so let's make sure we do want to run them
        enabled = getenv("gwcmgr_resttest", "false").equalsIgnoreCase("true");
        if (!enabled) {
            LOGGER.warn("Tests are disabled. Please read the documentation to enable them.");
        }

        try {
            URL = new URL(RESTURL);
            geoWebCache = new GeoWebCacheRESTManager(RESTURL, RESTUSER, RESTPW);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private static String getenv(String envName, String envDefault) {
        String env = System.getenv(envName);
        String prop = System.getProperty(envName, env);
        LOGGER.debug("varname " + envName + " --> env:" + env + " prop:" + prop);
        return prop != null ? prop : envDefault;
    }

    @BeforeClass
    public static void setUp() throws Exception {

        if (enabled) {
            if (existgwc == null) {
                existgwc = geoWebCache.existGeoWebCache();
                if (!existgwc) {
                    LOGGER.error("TESTS WILL FAIL BECAUSE NO GEOWEBCACHE WAS FOUND AT " + RESTURL
                            + " (" + RESTUSER + ":" + RESTPW + ")");
                } else {
                    LOGGER.info("Using geowebcache instance " + RESTUSER + ":" + RESTPW + " @ "
                            + RESTURL);
                }
            } else if (existgwc == false) {
                System.out.println("Failing tests : geowebcache not found");
                fail("GeoWebCache not found");
            }

        } else {
            System.out.println("Skipping tests ");
            LOGGER.warn("Tests are disabled. Please read the documentation to enable them.");
        }
    }

    @Before
    public void before() {
        String testName = _testName.getMethodName();
        LOGGER.warn("");
        LOGGER.warn("============================================================");
        LOGGER.warn("=== RUNNING TEST " + testName);
        LOGGER.warn("");
    }

    protected boolean enabled() {
        return enabled;
    }

    @Test
    public void testGWCExistence() {
        LOGGER.debug("Testing GWC Existence");
        Assert.assertTrue("The GeoWebCache is unreachable", geoWebCache.existGeoWebCache());
    }

    // <editor-fold desc="/masstruncate" defaultstate="collapsed">
    //==========================================================================
    //=== MASSTRUNCATE 
    //==========================================================================
    @Test
    public void testMassTruncate() throws InterruptedException, IOException {
        File layerFile = new ClassPathResource("testdata/geowebcache/layer2.xml").getFile();
        final String layerName = "layer2";
        cleanupTestLayer(layerName);
        Assert.assertTrue(geoWebCache.addLayer(layerFile, layerName));
        File seedRequestFile = new ClassPathResource("testdata/geowebcache/seedRequest.xml").getFile();
        Assert.assertTrue("Failed to add seedRequest",
                geoWebCache.addSeedRequest(seedRequestFile, layerName));
        LOGGER.info("Waiting 2s before seed truncation");
        Thread.sleep(2500L);
        //Truncation
        Assert.assertTrue(geoWebCache.truncateSeedRequestTask(layerName, GWCTruncateSeedType.all));

        Assert.assertTrue(geoWebCache.truncateLayer(layerName));
        LOGGER.info("Waiting 2s before removing layer");
        Thread.sleep(2500L);
        Assert.assertTrue(geoWebCache.removeLayer(layerName));
    }

    @Test
    public void testGetMassTruncateReqAvailable() throws InterruptedException, IOException {
        MassTruncateRequests massTruncateRequests = geoWebCache.getMassTruncateReqAvailable();
        Assert.assertNotNull("The mass truncate request is not available", massTruncateRequests);
        Assert.assertNotNull(massTruncateRequests.getRequestTypes());
        Assert.assertEquals(1, massTruncateRequests.getRequestTypes().size());
        Assert.assertEquals("truncateLayer", massTruncateRequests.getRequestTypes().get(0));
    }
    // </editor-fold>

    // <editor-fold desc="/diskquota" defaultstate="collapsed">
    //==========================================================================
    //=== DISKQUOTA
    //==========================================================================
    @Test
    public void testChangeDiskQuotaConfig() throws JsonMappingException {
        GWCQuotaConfiguration quotaConfiguration = geoWebCache.getCurrentDiskQuotaConfig();
        int maxConcurrentCleanUps = quotaConfiguration.getMaxConcurrentCleanUps();
        LOGGER.info("Quota maxConcurrentCleanUps: " + maxConcurrentCleanUps);
        quotaConfiguration.setMaxConcurrentCleanUps(maxConcurrentCleanUps + 1);
        geoWebCache.changeDiskQuotaConfig(quotaConfiguration);
        //After update
        quotaConfiguration = geoWebCache.getCurrentDiskQuotaConfig();
        //After retrieve new config
        Assert.assertEquals(maxConcurrentCleanUps + 1,
                (int) quotaConfiguration.getMaxConcurrentCleanUps());
        quotaConfiguration.setMaxConcurrentCleanUps(maxConcurrentCleanUps);
        //Re-update
        geoWebCache.changeDiskQuotaConfig(quotaConfiguration);
        //After retrieve new config
        quotaConfiguration = geoWebCache.getCurrentDiskQuotaConfig();
        Assert.assertEquals(maxConcurrentCleanUps,
                (int) quotaConfiguration.getMaxConcurrentCleanUps());
    }

    @Test
    public void testCurrentDiskQuotaConfiguration() throws JsonMappingException {
        GWCQuotaConfiguration quotaConfiguration = geoWebCache.getCurrentDiskQuotaConfig();
        LOGGER.info("Quota configuration: " + quotaConfiguration);
        Assert.assertNotNull(quotaConfiguration);
        Assert.assertEquals(GWCExpirationPolicy.LFU, quotaConfiguration.getGlobalExpirationPolicyName());
        Assert.assertEquals(2, (int) quotaConfiguration.getMaxConcurrentCleanUps());
        Assert.assertEquals(10, (int) quotaConfiguration.getCacheCleanUpFrequency());
        Assert.assertNotNull(quotaConfiguration.getGlobalQuota());
        Assert.assertNotNull(quotaConfiguration.getGlobalQuota().getBytes());
//        LOGGER.info("Quota configuration: " + quotaConfiguration.getGlobalQuota().getBytes());
        Assert.assertEquals(TimeUnit.SECONDS, quotaConfiguration.getCacheCleanUpUnits());
        Assert.assertFalse(quotaConfiguration.isEnabled());
    }
    // </editor-fold>

    // <editor-fold desc="/seed" defaultstate="collapsed">
    //==========================================================================
    //=== SEED
    //==========================================================================
    @Test
    public void testGlobalSeedingStatus() throws JsonMappingException {
        GlobalSeedStatus globalSeedingStatus = geoWebCache.getGlobalSeedingStatus();
        if (globalSeedingStatus != null && globalSeedingStatus.getElements() != null
                && !globalSeedingStatus.getElements().isEmpty()) {
            LOGGER.info("Result global seeding status: " + globalSeedingStatus.getElements().get(0).getTaskStatus());
            Assert.assertNotNull(globalSeedingStatus.getElements().get(0).getExpectedRemainingTime());
            Assert.assertNotNull(globalSeedingStatus.getElements().get(0).getTaskID());
            Assert.assertNotNull(globalSeedingStatus.getElements().get(0).getTaskStatus());
            Assert.assertNotNull(globalSeedingStatus.getElements().get(0).getTilesProcessed());
            Assert.assertNotNull(globalSeedingStatus.getElements().get(0).getTotalNumOfTilesToProcess());
        }
    }

    @Test
    public void testTruncateSeedingRequest() {
        Assert.assertTrue(geoWebCache.truncateSeedRequestTask("layer2", GWCTruncateSeedType.all));
    }

    @Test
    public void testLayerSeedingStatus() throws JsonMappingException, IOException, InterruptedException {
        File layerFile = new ClassPathResource("testdata/geowebcache/layer2.xml").getFile();
        final String layerName = "layer2";
        cleanupTestLayer(layerName);
        Assert.assertTrue(geoWebCache.addLayer(layerFile, layerName));
        File seedRequestFile = new ClassPathResource("testdata/geowebcache/seedRequest.xml").getFile();
        Assert.assertTrue("Failed to add seedRequest",
                geoWebCache.addSeedRequest(seedRequestFile, layerName));
        //
        GlobalSeedStatus globalSeedingStatus = geoWebCache.getLayerSeedingStatus(layerName);
        if (globalSeedingStatus != null && globalSeedingStatus.getElements() != null
                && !globalSeedingStatus.getElements().isEmpty()) {
            LOGGER.info("Result single layer seeding status: " + globalSeedingStatus.getElements().get(0).getTaskStatus());
            Assert.assertNotNull(globalSeedingStatus.getElements().get(0).getExpectedRemainingTime());
            Assert.assertNotNull(globalSeedingStatus.getElements().get(0).getTaskID());
            Assert.assertNotNull(globalSeedingStatus.getElements().get(0).getTaskStatus());
            Assert.assertNotNull(globalSeedingStatus.getElements().get(0).getTilesProcessed());
            Assert.assertNotNull(globalSeedingStatus.getElements().get(0).getTotalNumOfTilesToProcess());
        }
        //
        LOGGER.info("Waiting 2s before seed truncation");
        Thread.sleep(50000L);
        Assert.assertTrue(geoWebCache.truncateSeedRequestTask(layerName, GWCTruncateSeedType.all));
        LOGGER.info("Waiting 2s before removing layer");
        Thread.sleep(50000L);
        Assert.assertTrue(geoWebCache.removeLayer(layerName));
    }

    @Test
    public void testADDRemoveSeedRequest() throws IOException, InterruptedException {
        File layerFile = new ClassPathResource("testdata/geowebcache/layer2.xml").getFile();
        final String layerName = "layer2";
        cleanupTestLayer(layerName);
        Assert.assertTrue(geoWebCache.addLayer(layerFile, layerName));
        File seedRequestFile = new ClassPathResource("testdata/geowebcache/seedRequest.xml").getFile();
        Assert.assertTrue("Failed to add seedRequest",
                geoWebCache.addSeedRequest(seedRequestFile, layerName));
        LOGGER.info("Waiting 2s before seed truncation");
        Thread.sleep(2500L);
        Assert.assertTrue(geoWebCache.truncateSeedRequestTask(layerName, GWCTruncateSeedType.all));
        LOGGER.info("Waiting 2s before removing layer");
        Thread.sleep(2500L);
        Assert.assertTrue(geoWebCache.removeLayer(layerName));
    }
    // </editor-fold>

    // <editor-fold desc="/statistics" defaultstate="collapsed">
    //==========================================================================
    //=== STATISTICS 
    //==========================================================================
//    @Test
    public void testGetCacheStatistics() throws JsonMappingException {
        /**
         * NB: the in memory cache statistics if the blobstore used is an
         * instance of MemoryBlobStore.
         */
        GWCInMemoryCacheStatisticsXML statistics = geoWebCache.getInMemoryCacheStatisticsXML();
        Assert.assertNotNull("Cannot get the in memory cache statistics. "
                + "Are you sure that you are using the MemoryBlobStore?", statistics);
        LOGGER.info("In Memory Cache statistics: " + statistics);
        Assert.assertTrue(statistics.getTotalSize() > 0);
        Assert.assertTrue(statistics.getActualSize() > 0);
        Assert.assertTrue(statistics.getCurrentMemoryOccupation() > 0);
    }
    // </editor-fold>

    // <editor-fold desc="/layers" defaultstate="collapsed">
    //==========================================================================
    //=== LAYERS
    //==========================================================================
    /**
     * Dummy test to check the gel layer functionality
     */
//    @Test
    public void testGWCGetLayers() {
        Assert.assertEquals(3, geoWebCache.getLayers().size());
        Assert.assertEquals("img states", geoWebCache.getLayers().get(0).getName());
    }

    @Test
    public void testAddRemoveResource() throws IOException {
        File layerFile = new ClassPathResource("testdata/geowebcache/layer.xml").getFile();
        final String layerName = "layer1";
        // known state?
        cleanupTestLayer(layerName);
        int layersSize = geoWebCache.getLayers().size();

        // test insert
        String layerContent = IOUtils.toString(new FileInputStream(layerFile));
        Assert.assertTrue("Failed to add layer", geoWebCache.addLayer(layerContent, layerName));
        Assert.assertEquals(layersSize + 1, geoWebCache.getLayers().size());
        //Removing test layer
        Assert.assertTrue("The layer was not removed", geoWebCache.removeLayer(layerName));
        Assert.assertEquals(layersSize, geoWebCache.getLayers().size());
    }

    @Test
    public void testAddRemoveFile() throws IOException {
        File layerFile = new ClassPathResource("testdata/geowebcache/layer.xml").getFile();
        // known state?
        final String layerName = "layer1";
        cleanupTestLayer(layerName);
        int layersSize = geoWebCache.getLayers().size();
        Assert.assertTrue("Failed to add layer", geoWebCache.addLayer(layerFile, layerName));
        Assert.assertEquals(layersSize + 1, geoWebCache.getLayers().size());
        //Removing test layer
        Assert.assertTrue("The layer was not removed", geoWebCache.removeLayer(layerName));
        Assert.assertEquals(layersSize, geoWebCache.getLayers().size());
    }

    @Test
    public void testAddModifyRemoveFile() throws IOException {
        File layerFile = new ClassPathResource("testdata/geowebcache/layer.xml").getFile();
        File modLayerFile = new ClassPathResource("testdata/geowebcache/layerMod.xml").getFile();
        // known state?
        final String layerName = "layer1";
        cleanupTestLayer(layerName);
        int layersSize = geoWebCache.getLayers().size();
        Assert.assertTrue("Failed to add layer", geoWebCache.addLayer(layerFile, layerName));
        Assert.assertFalse("It is not possible to add two times the same layer",
                geoWebCache.addLayer(layerFile, layerName));
        Assert.assertTrue("Failed to modify layer", geoWebCache.updateLayer(modLayerFile, layerName));
        Assert.assertEquals(layersSize + 1, geoWebCache.getLayers().size());
        //Removing test layer
        Assert.assertTrue("The layer was not removed", geoWebCache.removeLayer(layerName));
        Assert.assertEquals(layersSize, geoWebCache.getLayers().size());
    }

    protected void cleanupTestLayer(final String layerName) {
        // dry run delete to work in a known state
        if (geoWebCache.existsLayer(layerName)) {
            LOGGER.info("Clearing stale test layer " + layerName);
            boolean ok = geoWebCache.removeLayer(layerName);
            if (!ok) {
                fail("Could not unpublish layer " + layerName);
            }
        }
        assertFalse("Cleanup failed", geoWebCache.existsLayer(layerName));
    }

//    @Test
//    public void testGWCRemoveLayer() {
//        Assert.assertEquals(3, geoWebCache.getLayers().size());
//        LOGGER.info("Removing layer having name: " + geoWebCache.getLayers().get(2).getName());
//        Assert.assertTrue("The layer was not removed", geoWebCache.removeLayer(geoWebCache.getLayers().get(2).getName()));
//        Assert.assertEquals(2, geoWebCache.getLayers().size());
////        Assert.assertEquals("img states", );
//    }

    @Test
    public void testGWCRemoveUnexistingLayer() {
        Assert.assertEquals(3, geoWebCache.getLayers().size());
        LOGGER.info("Removing layer having name: unexistingLayer");
        Assert.assertFalse("The layer must not exist", geoWebCache.removeLayer("unexistingLayer"));
        Assert.assertEquals(3, geoWebCache.getLayers().size());
//        Assert.assertEquals("img states", );
    }

    @Test
    public void testGWCGetLayerIMGStates() {
        LOGGER.debug("Testing GWC GetLayer");
        GWCRESTWMSLayer layer = geoWebCache.getLayer("img states");
        Assert.assertNotNull("Please, ensure that the 'img states' default layer is available", layer);
        Assert.assertEquals("img states", layer.getName());
        Assert.assertEquals("Nicer title for Image States", layer.getTitle());
        Assert.assertEquals(4, layer.getMimeFormats().size());
        Assert.assertEquals(1, layer.getGridSubsets().size());
        Assert.assertNotNull(layer.getGridSubsets().get(0));
        Assert.assertEquals("EPSG:4326", layer.getGridSubsets().get(0).getGridSetName());
        Assert.assertNotNull(layer.getGridSubsets().get(0).getExtent());
        Assert.assertNotNull(layer.getGridSubsets().get(0).getExtent().getCoords());
        Assert.assertEquals(4, layer.getGridSubsets().get(0).getExtent().getCoords().size());
        Assert.assertNotNull(layer.getExpireCacheList());
        Assert.assertEquals(1, layer.getExpireCacheList().size());
        Assert.assertEquals(0, (int) layer.getExpireCacheList().get(0).getMinZoom());
        Assert.assertEquals(60, (int) layer.getExpireCacheList().get(0).getExpiration());
        Assert.assertNotNull(layer.getExpireClientsList());
        Assert.assertEquals(1, layer.getExpireClientsList().size());
        Assert.assertEquals(0, (int) layer.getExpireClientsList().get(0).getMinZoom());
        Assert.assertEquals(500, (int) layer.getExpireClientsList().get(0).getExpiration());
        Assert.assertNotNull(layer.getWmsUrl());
        Assert.assertEquals(1, layer.getWmsUrl().size());
//        LOGGER.info("WMS TEXT: " + layer.getWmsUrl().get(0));
        Assert.assertEquals("http://demo.opengeo.org/geoserver/wms?", layer.getWmsUrl().get(0));
        Assert.assertEquals("nurc:Img_Sample,topp:states", layer.getWMSLayers());
        Assert.assertEquals(false, (boolean) layer.getTransparent());
        Assert.assertEquals("0x0066FF", layer.getBGColor());
        LOGGER.info("Testing GWC GetLayer result: " + geoWebCache.getLayer("img states"));
    }

    @Test
    public void testGWCGetLayerToppStates() {
        LOGGER.debug("Testing GWC GetLayer");
        GWCRESTWMSLayer layer = geoWebCache.getLayer("topp:states");
        Assert.assertNotNull("Please, ensure that the 'topp:states' default layer is available", layer);
        Assert.assertEquals("topp:states", layer.getName());

        Assert.assertNotNull(layer.getMimeFormats());
        Assert.assertEquals(4, layer.getMimeFormats().size());
        Assert.assertEquals("image/gif", layer.getMimeFormats().get(0));
        Assert.assertEquals("image/jpeg", layer.getMimeFormats().get(1));
        Assert.assertEquals("image/png", layer.getMimeFormats().get(2));
        Assert.assertEquals("image/png8", layer.getMimeFormats().get(3));

        Assert.assertNotNull(layer.getParameterFilters());
        Assert.assertNotNull(layer.getParameterFilters().getStringParameterFilter());
        Assert.assertEquals("STYLES", layer.getParameterFilters().getStringParameterFilter().getKey());
        Assert.assertEquals("population", layer.getParameterFilters().getStringParameterFilter().getDefaultValue());
        Assert.assertEquals(3, layer.getParameterFilters().getStringParameterFilter().getValues().size());
        Assert.assertEquals("pophatch", layer.getParameterFilters().getStringParameterFilter().getValues().get(2));

        Assert.assertNotNull(layer.getWmsUrl());
        Assert.assertEquals(1, layer.getWmsUrl().size());
//        LOGGER.info("WMS TEXT: " + layer.getWmsUrl().get(0));
        Assert.assertEquals("http://demo.opengeo.org/geoserver/topp/wms?", layer.getWmsUrl().get(0));

        LOGGER.info("Testing GWC GetLayer result: " + geoWebCache.getLayer("img states"));
    }
    // </editor-fold>

}

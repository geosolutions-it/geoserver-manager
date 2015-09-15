package it.geosolutions.geoserver.rest.wmsstore;

import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import it.geosolutions.geoserver.rest.decoder.RESTWmsStore;
import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSWMSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.wmsstore.GSWmsStoreEncoder;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStoreManager;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * <P>
 * Since these tests require a running Store instance, this is more like integration tests.<br/>
 *
 * For testing that a wmsstore is properly configured, a layer publication has to be attempted
 *
 * @see GeoserverRESTTest
 */
public abstract class WmsStoreIntegrationTest extends GeoserverRESTTest {

        protected final GeoServerRESTStoreManager storeManager;

        /**
         * ignore integration tests
         */
        protected final boolean ignore;

        private final static Logger LOGGER = LoggerFactory.getLogger(WmsStoreIntegrationTest.class);

        public boolean isIgnore() {
            return ignore;
        }

        /**
         *
         * @param ignore true if this test should be disabled
         * @throws IllegalArgumentException
         * @throws MalformedURLException
         */
        public WmsStoreIntegrationTest(boolean ignore) throws IllegalArgumentException,
                MalformedURLException {
            super();
            this.storeManager = new GeoServerRESTStoreManager(URL, RESTUSER, RESTPW);
            this.ignore = ignore;
        }

        public abstract GSAbstractStoreEncoder getStoreEncoderTest();

        @Test
        public void testCreateWmsStore() throws IllegalArgumentException, MalformedURLException {
            if (!enabled() || ignore) {
                return;
            }
//        deleteAll();

            assertTrue(publisher.createWorkspace(DEFAULT_WS));

            // creation test
            GSAbstractStoreEncoder storeEncoder = getStoreEncoderTest();

            String storeName = storeEncoder.getName();
            // String description = storeEncoder.getDescription();

            boolean created = storeManager.create(DEFAULT_WS, storeEncoder);

            assertTrue("*** store " + storeName + " has not been created.", created);

            RESTWmsStore wmsStore = reader.getWmsStore(DEFAULT_WS, storeName);
            assertNotNull(wmsStore);

            // check if the wmsstore is properly configured in GS for publishing layers
            String layername = "wmsStoreLayer";

            if (storeEncoder instanceof GSWmsStoreEncoder)
                layername = layername.toUpperCase();

            GSWMSLayerEncoder fte = new GSWMSLayerEncoder();
            fte.setName(layername);
            fte.setNativeName(layername);
            fte.setTitle(layername);
            fte.setNativeCRS("EPSG:4326");
            fte.setEnabled(true);

            GSLayerEncoder layerEncoder = new GSLayerEncoder();
            layerEncoder.setEnabled(true);
            layerEncoder.setQueryable(true);
            layerEncoder.setDefaultStyle("polygon");

            boolean published = publisher.publishWMSLayer(DEFAULT_WS, storeName, fte, layerEncoder);

            assertTrue("*** Test layer " + layername
                    + " has not been published. Problem in wms store configuration", published);

            // removing test
            boolean removed = storeManager.remove(DEFAULT_WS, storeEncoder, true);
            assertTrue("*** Wmsstore " + storeName + " has not been removed.", removed);
            assertTrue(publisher.removeWorkspace(DEFAULT_WS, false));
        }

    @Test
    public void testUnpublishWmsStore() throws IllegalArgumentException, MalformedURLException {
        if (!enabled() || ignore) {
            return;
        }
//        deleteAll();

        assertTrue(publisher.createWorkspace(DEFAULT_WS));

        // creation test
        GSAbstractStoreEncoder storeEncoder = getStoreEncoderTest();

        String storeName = storeEncoder.getName();
        // String description = storeEncoder.getDescription();

        boolean created = storeManager.create(DEFAULT_WS, storeEncoder);

        assertTrue("*** store " + storeName + " has not been created.", created);

        RESTWmsStore wmsStore = reader.getWmsStore(DEFAULT_WS, storeName);
        assertNotNull(wmsStore);

        // check if the wmsstore is properly configured in GS for publishing layers
        String layername = "wmsStoreLayer";

        if (storeEncoder instanceof GSWmsStoreEncoder)
            layername = layername.toUpperCase();

        GSWMSLayerEncoder fte = new GSWMSLayerEncoder();
        fte.setName(layername);
        fte.setNativeName(layername);
        fte.setTitle(layername);
        fte.setNativeCRS("EPSG:4326");
        fte.setEnabled(true);

        GSLayerEncoder layerEncoder = new GSLayerEncoder();
        layerEncoder.setEnabled(true);
        layerEncoder.setQueryable(true);
        layerEncoder.setDefaultStyle("polygon");

        boolean published = publisher.publishWMSLayer(DEFAULT_WS, storeName, fte, layerEncoder);

        assertTrue("*** Test layer " + layername
                + " has not been published. Problem in wms store configuration", published);

        boolean unpublished = publisher.unpublishWmsLayer(DEFAULT_WS, storeName, layername);

        assertTrue("*** Test layer " + layername
                + " has not been unpublished. Problem in wms store configuration", unpublished);


        // removing test
        boolean removed = storeManager.remove(DEFAULT_WS, storeEncoder, true);
        assertTrue("*** Wmsstore " + storeName + " has not been removed.", removed);
        assertTrue(publisher.removeWorkspace(DEFAULT_WS, false));

        }
    }


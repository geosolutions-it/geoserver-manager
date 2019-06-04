package it.geosolutions.geoserver.rest.publisher;

import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSWMSLayerEncoder;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStoreManager;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Testcase for publishing wms layers on geoserver.
 * We need a running GeoServer to properly run the tests.
 * If such geoserver instance cannot be contacted, tests will be skipped.
 *
 * @author draktina
 */
public class GeoserverRESTPublisherWmsTest extends GeoserverRESTTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(GeoserverRESTPublisherWmsTest.class);


    @After
    public void cleanUp(){
    }

    @Test
    public void testPublishUnexistingWmsLayer(){
        if (!enabled()) {
            return;
        }

        String wsName = "this_ws_does_not_exist";
        String storeName = "this_store_does_not_exist";
        String layerName ="this_layer_does_not_exist";

        GSWMSLayerEncoder gswmsLayerEncoder = new GSWMSLayerEncoder();
        gswmsLayerEncoder.setName(layerName);
        GSLayerEncoder layerEncoder = new GSLayerEncoder();

        boolean ok = publisher.publishWMSLayer(wsName, storeName, gswmsLayerEncoder, layerEncoder);
        assertFalse("added not existing layer", ok);
    }


    @Test
    public void testUnpublishUnexistingWmsLayer() throws IOException {
        if (!enabled()) {
            return;
        }

        String wsName = "this_ws_does_not_exist";
        String storeName = "this_store_does_not_exist";
        String layerName ="this_layer_does_not_exist";

        boolean ok = publisher.unpublishWmsLayer(wsName, storeName, layerName);
        assertFalse("removed not existing layer", ok);
    }
}

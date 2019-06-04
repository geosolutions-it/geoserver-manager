package it.geosolutions.geoserver.rest.wmsstore;

import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSOracleNGDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.wmsstore.GSWmsStoreEncoder;
import java.net.URL;

import java.net.MalformedURLException;

/**
 * Created by drakiko on 15/09/2015.
 */
public class WmsStoreEncoderTest extends WmsStoreIntegrationTest{

    public WmsStoreEncoderTest() throws IllegalArgumentException, MalformedURLException {
        super(System.getProperty("wmsIgnore", "true").equalsIgnoreCase("true"));

    }

    @Override
    public GSAbstractStoreEncoder getStoreEncoderTest()  {

        URL url = null;
        try {
            url = new URL(System.getProperty("wmsStoreUrl", "test"));
        } catch (MalformedURLException e) {
            //
        }

        GSWmsStoreEncoder wmsStoreEncoder = new GSWmsStoreEncoder(System.getProperty("wmsDataStoreName", "testWmsIntegration"), url);

        wmsStoreEncoder.setEnabled(true);

        return wmsStoreEncoder;
    }
}

package it.geosolutions.geoserver.rest.encoder;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by drakiko on 14/09/2015.
 */
public class GSWMSLayerEncoderTest {

    GSWMSLayerEncoder gswmsLayerEncoder;

    @Before
    public void setUp() {
        gswmsLayerEncoder = new GSWMSLayerEncoder();

        gswmsLayerEncoder.setName("WMS_NAME");

        gswmsLayerEncoder.setNativeName("WMS_NATIVE_NAME");
        gswmsLayerEncoder.setTitle("WMS_TITLE");
        gswmsLayerEncoder.setNativeCRS("EPSG:4326");
        gswmsLayerEncoder.setEnabled(true);

    }

    /**
     * Test method for {@link GSWMSLayerEncoder#GSWMSLayerEncoder()}.
     */
    @Test
    public void testProperties() {
        Assert.assertEquals(
                true,
                Boolean.parseBoolean(gswmsLayerEncoder.getRoot().getChild("enabled")
                        .getValue()));
        Assert.assertEquals(
                "WMS_NAME",
                gswmsLayerEncoder.getRoot().getChild("name").getValue());
        Assert.assertEquals(
                "WMS_NATIVE_NAME",
                gswmsLayerEncoder.getRoot().getChild("nativeName").getValue());
        Assert.assertEquals(
                "WMS_TITLE",
                gswmsLayerEncoder.getRoot().getChild("title").getValue());
        Assert.assertEquals(
                "EPSG:4326",
                gswmsLayerEncoder.getRoot().getChild("nativeCRS").getValue());
    }
}

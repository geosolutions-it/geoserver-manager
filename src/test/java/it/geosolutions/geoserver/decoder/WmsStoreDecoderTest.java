package it.geosolutions.geoserver.decoder;

import it.geosolutions.geoserver.rest.decoder.RESTWmsStore;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * WmsStoreDecoderTest
 * 
 * @author cip (cippinofg at gmail.com)
 * 
 */
public class WmsStoreDecoderTest {

	RESTWmsStore wmsstore;

    @Before
    public void setup() throws IOException {
        File wmsstoreFile = new ClassPathResource("testdata/wmsstoreExample.xml").getFile();
        String wmsstoreContent = FileUtils.readFileToString(wmsstoreFile);
        wmsstore = RESTWmsStore.build(wmsstoreContent);
    }

    @Test
    public void testName() {
        Assert.assertEquals(wmsstore.getName(), "wmsstore001");
    }

    @Test
    public void testType() {
        Assert.assertEquals(wmsstore.getType(), "WMS");
    }
    
    @Test
    public void testEnabled() {
        Assert.assertEquals(wmsstore.getEnabled(), true);
    }
    
    @Test
    public void testWorkspace() {
        Assert.assertEquals(wmsstore.getWorkspaceName(), "ws001");
    }
    
    @Test
    public void testUseConnectionPooling() {
        Assert.assertEquals(wmsstore.getUseConnectionPooling(),true);
    }
    
    @Test
    public void testCapabilitiesURL() {
        Assert.assertEquals(wmsstore.getCapabilitiesURL(), "http://myhost/geoserver/wms");
    }
        
    @Test
    public void testUser() {
        Assert.assertEquals(wmsstore.getUser(), "admin");
    }
        
    @Test
    public void testPassword() {
        Assert.assertEquals(wmsstore.getPassword(), "geoserver");
    }
    
    @Test
    public void testMaxConnections() {
        Assert.assertEquals(wmsstore.getMaxConnections(), "6");
    }
    
    @Test
    public void testReadTimeout() {
        Assert.assertEquals(wmsstore.getReadTimeout(), "60");
    }    
    
    @Test
    public void testConnectTimeout() {
        Assert.assertEquals(wmsstore.getConnectTimeout(), "30");
    }    
}

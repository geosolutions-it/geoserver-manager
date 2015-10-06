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

import it.geosolutions.geoserver.rest.decoder.gwc.GWCRESTWMSLayer;
import java.net.MalformedURLException;
import java.net.URL;
import junit.framework.Assert;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes REST params.
 * <P>
 * <B>These tests are destructive, so you have to explicitly enable them</B> by setting the env var <TT>resttest</TT> to <TT>true</TT>.
 * <P>
 * The target geoserver instance can be customized by defining the following env vars:
 * <ul>
 * <LI><TT>resturl</TT> (default <TT>http://localhost:8080/geowebcache</TT>)</LI>
 * <LI><TT>restuser</TT> (default: <TT>geowebcache</TT>)</LI>
 * <LI><TT>restpw</TT> (default: <TT>secured</TT>)</LI>
 * </ul>
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
        if (!enabled)
            LOGGER.warn("Tests are disabled. Please read the documentation to enable them.");

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
        LOGGER.debug("varname " + envName + " --> env:" + env + " prop:"+prop);
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
            } else if (existgwc == false){
                System.out.println("Failing tests : geowebcache not found");
                fail("GeoWebCache not found");
            }
            
        } else {
            System.out.println("Skipping tests ");
            LOGGER.warn("Tests are disabled. Please read the documentation to enable them.");
        }
    }

    @Before
    public void before(){
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
    public void testGWCExistence(){
        LOGGER.debug("Testing GWC Existence");
        Assert.assertTrue("The GeoWebCache is unreachable", geoWebCache.existGeoWebCache());
    }

    @Test
    public void testGWCGetLayer(){
        LOGGER.debug("Testing GWC GetLayer");
        GWCRESTWMSLayer layer = geoWebCache.getLayer("img states");
        Assert.assertNotNull("Please, ensure that the 'img states' default layer is available", layer);
        Assert.assertEquals("img states", layer.getName());
        Assert.assertEquals("Nicer title for Image States", layer.getTitle());
        Assert.assertEquals(4, layer.getMimeFormats().size());
        LOGGER.info("Testing GWC GetLayer result: " + geoWebCache.getLayer("img states"));
    }


}
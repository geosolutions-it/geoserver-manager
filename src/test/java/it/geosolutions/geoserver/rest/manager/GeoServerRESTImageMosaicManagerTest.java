/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2011, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package it.geosolutions.geoserver.rest.manager;

import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.datastore.StoreIntegrationTest;
import it.geosolutions.geoserver.rest.decoder.RESTCoverageStore;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageGranulesList;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageGranulesList.RESTStructuredCoverageGranule;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageIndexSchema;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageIndexSchema.RESTStructuredCoverageIndexAttribute;
import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In order to test that class, make sure to configure a geoserver with a "mosaic" store.
 * 
 * 1) take the mosaic.zip archive contained on src/test/resources/testdata.granules
 * 2) extract it on disk
 * 3) configure an ImageMosaic store on geoserver (name the store as "mosaic"), use the "it.geosolutions" workspace
 * 4) configure a layer on that store (name the coverage as "mosaic" again).
 * 5) on dimensions configuration tab, make sure to enable custom depth and date dimensions.
 * 6) publish it.
 * 
 * 
 * @author Simone Giannecchini, simone.giannecchini@geo-solutions.it
 * @author Daniele Romagnoli, GeoSolutions SAS
 *
 */
public class GeoServerRESTImageMosaicManagerTest extends StoreIntegrationTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoServerRESTImageMosaicManagerTest.class);
    
    /**
     * @param ignore
     * @throws IllegalArgumentException
     * @throws MalformedURLException
     */
    public GeoServerRESTImageMosaicManagerTest()
            throws IllegalArgumentException, MalformedURLException {
        super(true);
    }

    @Override
    public GSAbstractStoreEncoder getStoreEncoderTest() {
        return null;
    }
    
    @Test
    public void createAndDelete() throws IllegalArgumentException, MalformedURLException, UnsupportedEncodingException{
        if (!enabled()) {
            return;
        }
        GeoServerRESTStructuredGridCoverageReaderManager manager = 
            new GeoServerRESTStructuredGridCoverageReaderManager(new URL(RESTURL), RESTUSER, RESTPW);
        GeoServerRESTReader reader = new GeoServerRESTReader(new URL(RESTURL), RESTUSER, RESTPW);
        
        // check index format
        RESTStructuredCoverageIndexSchema indexFormat = manager.getGranuleIndexSchema("it.geosolutions", "mosaic","mosaic");
        if (indexFormat == null) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("sample coverage hasn't been found. Make sure to configure the layer before running this test");
                return;
            }
        }
        assertNotNull(indexFormat);
        assertFalse(indexFormat.isEmpty());
        assertEquals(5, indexFormat.size());
        Iterator<RESTStructuredCoverageIndexAttribute> iterator = indexFormat.iterator();
        while (iterator.hasNext()) {
            final RESTStructuredCoverageIndexAttribute element = iterator.next();
            final String elementName = element.getName();
            if (elementName.equals("location")) {
                assertEquals("0", element.getMinOccurs());
                assertEquals("1", element.getMaxOccurs());
                assertEquals("true", element.getNillable());
                assertEquals("java.lang.String", element.getBinding());
            } else if (elementName.equals("time")) {
                assertEquals("0", element.getMinOccurs());
                assertEquals("1", element.getMaxOccurs());
                assertEquals("true", element.getNillable());
                assertEquals("java.sql.Timestamp", element.getBinding());
            } else if (elementName.equals("date")) {
                assertEquals("0", element.getMinOccurs());
                assertEquals("1", element.getMaxOccurs());
                assertEquals("true", element.getNillable());
                assertEquals("java.lang.String", element.getBinding());
            } else if (elementName.equals("depth")) {
                assertEquals("0", element.getMinOccurs());
                assertEquals("1", element.getMaxOccurs());
                assertEquals("true", element.getNillable());
                assertEquals("java.lang.Integer", element.getBinding());
            }
        }

        RESTStructuredCoverageGranulesList granulesList = null;
        RESTStructuredCoverageGranule granule = null;
        // get some granules by id
//        manager.getGranuleById("it.geosolutions", "mosaic","mosaic","2");
//        assertNotNull(granulesList);
//        assertSame(1, granulesList.size());
//        assertFalse(granulesList.isEmpty());
//        RESTStructuredCoverageGranule granule = granulesList.get(0);
//        assertNotNull(granule);
//        assertEquals(granule.getAttributeByIndex(4), "1250.0");
//        assertEquals(granule.getAttributeByName("elevation"), "1250.0");
        
        
        // get with paging
        granulesList = manager.getGranules("it.geosolutions", "mosaic", "mosaic" , null, 0, 1);
        assertNotNull(granulesList);
        assertEquals(1, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule);       
        
        granulesList = manager.getGranules("it.geosolutions", "mosaic", "mosaic", null, null, 2);
        assertNotNull(granulesList);
        assertEquals(2, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule);
        
        granulesList = manager.getGranules("it.geosolutions", "mosaic", "mosaic", null, null, null);
        assertNotNull(granulesList);
        assertEquals(4, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule);   
        
        granulesList = manager.getGranules("it.geosolutions", "mosaic", "mosaic", "depth = 100", null, null);
        assertNotNull(granulesList);
        assertEquals(2, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule); 
        
        granulesList = manager.getGranules("it.geosolutions", "mosaic", "mosaic", "depth = 100 AND date='20081101T0000000'", null, null);
        assertNotNull(granulesList);
        assertEquals(1, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule); 
        
        // remove by filter
        final String fileLocation = "NCOM_wattemp_100_20081101T0000000_12.tiff";
        boolean result = manager.removeGranulesByCQL("it.geosolutions", "mosaic", "mosaic", "location = '" + fileLocation + "'");
        Assert.assertTrue(result);
        
        granulesList = manager.getGranules("it.geosolutions", "mosaic", "mosaic", null, null, null);
        assertNotNull(granulesList);
        assertEquals(3, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule);
        
        // Readding that granule
        RESTCoverageStore store = reader.getCoverageStore("it.geosolutions", "mosaic");
        final String urlString = store.getURL();
        final URL url = new URL(urlString); 
        final File file = urlToFile(url);
        final String filePath = file.getAbsolutePath();
        
        // use reflection to get the store URL since coveragestore only returns name and workspace
        result = manager.createOrHarvestExternal("it.geosolutions", "mosaic", "imagemosaic", filePath + File.separatorChar + fileLocation );
        Assert.assertTrue(result);
        
        granulesList = manager.getGranules("it.geosolutions", "mosaic", "mosaic", null, null, null);
        assertNotNull(granulesList);
        assertEquals(4, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule);
        
    }
    
    
    /**
     * This method has been copied from org.geotools.data.DataUtilities
     * 
     * Takes a URL and converts it to a File. The attempts to deal with Windows UNC format specific
     * problems, specifically files located on network shares and different drives.
     * 
     * If the URL.getAuthority() returns null or is empty, then only the url's path property is used
     * to construct the file. Otherwise, the authority is prefixed before the path.
     * 
     * It is assumed that url.getProtocol returns "file".
     * 
     * Authority is the drive or network share the file is located on. Such as "C:", "E:",
     * "\\fooServer"
     * 
     * @param url
     *            a URL object that uses protocol "file"
     * @return a File that corresponds to the URL's location
     */
    private static File urlToFile(URL url) {
        if (!"file".equals(url.getProtocol())) {
            return null; // not a File URL
        }
        String string = url.toExternalForm();
        if (string.contains("+")) {
            // this represents an invalid URL created using either
            // file.toURL(); or
            // file.toURI().toURL() on a specific version of Java 5 on Mac
            string = string.replace("+", "%2B");
        }
        try {
            string = URLDecoder.decode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Could not decode the URL to UTF-8 format", e);
        }

        String path3;

        String simplePrefix = "file:/";
        String standardPrefix = "file://";
        String os = System.getProperty("os.name");

        if (os.toUpperCase().contains("WINDOWS") && string.startsWith(standardPrefix)) {
            // win32: host/share reference
            path3 = string.substring(standardPrefix.length() - 2);
        } else if (string.startsWith(standardPrefix)) {
            path3 = string.substring(standardPrefix.length());
        } else if (string.startsWith(simplePrefix)) {
            path3 = string.substring(simplePrefix.length() - 1);
        } else {
            String auth = url.getAuthority();
            String path2 = url.getPath().replace("%20", " ");
            if (auth != null && !auth.equals("")) {
                path3 = "//" + auth + path2;
            } else {
                path3 = path2;
            }
        }

        return new File(path3);
    }

}

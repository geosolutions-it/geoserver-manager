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

import it.geosolutions.geoserver.rest.datastore.StoreIntegrationTest;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageGranulesList;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageGranulesList.RESTStructuredCoverageGranule;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageIndexSchema;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageIndexSchema.RESTStructuredCoverageIndexAttribute;
import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author Simone Giannecchini, simone.giannecchini@geo-solutions.it
 *
 */
public class GeoServerRESTStructuredCoverageGridReaderManagerTest extends StoreIntegrationTest {

    /**
     * @param ignore
     * @throws IllegalArgumentException
     * @throws MalformedURLException
     */
    public GeoServerRESTStructuredCoverageGridReaderManagerTest()
            throws IllegalArgumentException, MalformedURLException {
        super(true);
    }

    @Override
    public GSAbstractStoreEncoder getStoreEncoderTest() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Test
    public void createAndDelete() throws IllegalArgumentException, MalformedURLException, UnsupportedEncodingException{
        GeoServerRESTStructuredCoverageGridReaderManager manager = 
            new GeoServerRESTStructuredCoverageGridReaderManager(new URL("http://localhost:8080/geoserver"), "admin", "geoserver");
//        boolean result=manager.createOrHarvestExternal("it.geosolutions", "polyphemus", "imagemosaic", "D:\\DLR\\Geoserver-MD\\polyphemus\\polyphemus_20130301.nc");
//        Assert.assertTrue(result);
        
        // check index format
        RESTStructuredCoverageIndexSchema indexFormat = manager.getGranuleIndexSchema("it.geosolutions", "polyphemus","V");
        assertNotNull(indexFormat);
        assertFalse(indexFormat.isEmpty());
        assertEquals(7, indexFormat.size());
        Iterator<RESTStructuredCoverageIndexAttribute> iterator = indexFormat.iterator();
        while(iterator.hasNext()){
            final RESTStructuredCoverageIndexAttribute element = iterator.next();
            if(element.getName().equals("location")){
                assertEquals("0", element.getMinOccurs());
                assertEquals("1", element.getMaxOccurs());
                assertEquals("true", element.getNillable());
                assertEquals("java.lang.String", element.getBinding());
                break;
            }
        }
        
        // get some granules by id
        RESTStructuredCoverageGranulesList granulesList = manager.getGranuleById("it.geosolutions", "polyphemus","V","348");
        assertNotNull(granulesList);
        assertSame(1, granulesList.size());
        assertFalse(granulesList.isEmpty());
        RESTStructuredCoverageGranule granule = granulesList.get(0);
        assertNotNull(granule);
        assertEquals(granule.getAttributeByIndex(4), "1250.0");
        assertEquals(granule.getAttributeByName("elevation"), "1250.0");
        
        
        // get with paging
        granulesList = manager.getGranules("it.geosolutions", "polyphemus","V",null,"0","10");
        assertNotNull(granulesList);
        assertEquals(10, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule);       
        
        granulesList = manager.getGranules("it.geosolutions", "polyphemus","V",null,null,"10");
        assertNotNull(granulesList);
        assertEquals(10, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule);
        
        granulesList = manager.getGranules("it.geosolutions", "polyphemus","V",null,null,null);
        assertNotNull(granulesList);
        assertEquals(1007, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule);   
        
        granulesList = manager.getGranules("it.geosolutions", "polyphemus","V","elevation = 10",null,null);
        assertNotNull(granulesList);
        assertEquals(72, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule); 
        
        granulesList = manager.getGranules("it.geosolutions", "polyphemus","V","elevation = 10","0","10");
        assertNotNull(granulesList);
        assertEquals(10, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule); 
        
        // remove by id
        boolean result = manager.removeGranuleById("it.geosolutions", "polyphemus","V", "349");
        assertTrue(result);
        
        // remove by filter
        result = manager.removeGranulesByCQL("it.geosolutions", "polyphemus","V", "location = 'polyphemus_20130301.nc'");
        Assert.assertTrue(result);
    }
}

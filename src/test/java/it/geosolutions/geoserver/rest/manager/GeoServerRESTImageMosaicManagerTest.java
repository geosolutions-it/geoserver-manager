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

import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageGranulesList;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageGranulesList.RESTStructuredCoverageGranule;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageIndexSchema;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageIndexSchema.RESTStructuredCoverageIndexAttribute;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.geosolutions.geoserver.rest.encoder.coverage.GSImageMosaicEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder.Presentation;

import java.net.URL;
import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Self contained test for working with Structured readers
 * 
 * 
 * @author Simone Giannecchini, simone.giannecchini@geo-solutions.it
 * @author Daniele Romagnoli, GeoSolutions SAS
 *
 */
public class GeoServerRESTImageMosaicManagerTest extends GeoserverRESTTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoServerRESTImageMosaicManagerTest.class);
    
    
    @Test
    public void createAndDelete() throws Exception{
        if (!enabled()) {
            return;
        }
        GeoServerRESTStructuredGridCoverageReaderManager manager = 
            new GeoServerRESTStructuredGridCoverageReaderManager(new URL(RESTURL), RESTUSER, RESTPW);
        
        // create mosaic
        boolean create=manager.create("it.geosolutions", "mosaic",new ClassPathResource("testdata/granules/mosaic.zip").getFile().getAbsolutePath());
        assertTrue(create);
        
        // enable dimension
        fixDimensions("it.geosolutions", "mosaic", "mosaic");
        
        // check index format
        RESTStructuredCoverageIndexSchema indexFormat = manager.getGranuleIndexSchema("it.geosolutions", "mosaic","mosaic");
        assertTrue(create);
        
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
                assertEquals("java.util.Date", element.getBinding());
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
        // use reflection to get the store URL since coveragestore only returns name and workspace
        result = manager.harvestExternal("it.geosolutions", "mosaic", "imagemosaic", new ClassPathResource("testdata/granules/NCOM_wattemp_100_20081101T0000000_12.tiff").getFile().getAbsolutePath() );
        Assert.assertTrue(result);
        
        granulesList = manager.getGranules("it.geosolutions", "mosaic", "mosaic", null, null, null);
        assertNotNull(granulesList);
        assertEquals(4, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule);
        
    }
    
    
    /**
     * This method enables the various dimensions for the coverage autocreated for this test.
     * 
     * @param wsName the workspace
     * @param coverageStoreName the coverage store name
     * @param csname the coverage name
     */
    private void fixDimensions(String wsName, String coverageStoreName, String csname) {

        final GSImageMosaicEncoder coverageEncoder = new GSImageMosaicEncoder();
        /*
         * unused in mosaic creation
         * this is only useful if you want to modify an existing coverage:
         * publisher.configureCoverage(ce, wsname, csname);
         * or create a new one from an existing store:
         * publisher.createCoverage(ce, wsname, csname);
         */
        coverageEncoder.setName("mosaic");
        
        coverageEncoder.setAllowMultithreading(true);
        coverageEncoder.setBackgroundValues("");
        coverageEncoder.setFilter("");
        coverageEncoder.setInputTransparentColor("");
        coverageEncoder.setLatLonBoundingBox(-180, -90, 180, 90, "EPSG:4326");
        coverageEncoder.setMaxAllowedTiles(11);
        coverageEncoder.setNativeBoundingBox(-180, -90, 180, 90, "EPSG:4326");
        coverageEncoder.setProjectionPolicy(ProjectionPolicy.NONE);
        coverageEncoder.setSRS("EPSG:4326");
        
        // activate time
        final GSDimensionInfoEncoder time=new GSDimensionInfoEncoder(true);
        time.setUnit("Seconds");
        time.setUnitSymbol("s");
        time.setPresentation(Presentation.LIST);
        coverageEncoder.setMetadataDimension("time", time);
        
        // activate date
        final GSDimensionInfoEncoder date=new GSDimensionInfoEncoder(true);
        date.setPresentation(Presentation.LIST);
        coverageEncoder.setMetadataDimension("custom_dimension_DATE", date);
        
        // activate depth
        final GSDimensionInfoEncoder depth=new GSDimensionInfoEncoder(true);
        depth.setPresentation(Presentation.LIST);
        depth.setUnit("Meters");
        depth.setUnitSymbol("m");
        coverageEncoder.setMetadataDimension("custom_dimension_DEPTH", depth);
        
        
        boolean config=publisher.configureCoverage(coverageEncoder, wsName, csname);
        assertTrue(config);
       
    }

}

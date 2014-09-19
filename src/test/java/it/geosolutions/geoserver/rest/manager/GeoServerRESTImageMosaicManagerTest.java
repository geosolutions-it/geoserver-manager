package it.geosolutions.geoserver.rest.manager;

import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import it.geosolutions.geoserver.rest.decoder.RESTCoverage;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageGranulesList;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageGranulesList.RESTStructuredCoverageGranule;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageIndexSchema;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageIndexSchema.RESTStructuredCoverageIndexAttribute;
import it.geosolutions.geoserver.rest.encoder.coverage.GSImageMosaicEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder.Presentation;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
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
        deleteAll();
        
        publisher.createWorkspace(DEFAULT_WS);
        
        // crea the manager
        GeoServerRESTStructuredGridCoverageReaderManager manager = 
            new GeoServerRESTStructuredGridCoverageReaderManager(new URL(RESTURL), RESTUSER, RESTPW);
        
        // create mosaic
        final String coverageStoreName = "mosaic";
        final String coverageName = "mosaic";
        final String format = "imagemosaic";
        
        // upload the mosaic
        boolean create=manager.create(DEFAULT_WS, coverageStoreName,new ClassPathResource("testdata/granules/mosaic.zip").getFile().getAbsolutePath());
        assertTrue(create);
        
        // enable dimension
        fixDimensions(DEFAULT_WS, coverageStoreName, coverageName);
        
        // check index format
        RESTStructuredCoverageIndexSchema indexFormat = manager.getGranuleIndexSchema(DEFAULT_WS, coverageName,coverageName);
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
        
        
        // get with paging
        granulesList = manager.getGranules(DEFAULT_WS, coverageStoreName, coverageName , null, 0, 1);
        assertNotNull(granulesList);
        assertEquals(1, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule);       
        
        granulesList = manager.getGranules(DEFAULT_WS, coverageStoreName, coverageName, null, null, 2);
        assertNotNull(granulesList);
        assertEquals(2, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule);
        
        // get with no paging
        granulesList = manager.getGranules(DEFAULT_WS, coverageStoreName, coverageName);
        assertNotNull(granulesList);
        assertEquals(4, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule);   
        
        // examples of filtering with CQL
        granulesList = manager.getGranules(DEFAULT_WS, coverageStoreName, coverageName, "depth = 100", null, null);
        assertNotNull(granulesList);
        assertEquals(2, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule); 
        
        granulesList = manager.getGranules(DEFAULT_WS, coverageStoreName, coverageName, "depth = 100 AND date='20081101T0000000'", null, null);
        assertNotNull(granulesList);
        assertEquals(1, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule); 
        
        granulesList = manager.getGranules(DEFAULT_WS, coverageStoreName, coverageName, "location LIKE 'NCOM_wattemp%'", 0, 1);
        assertNotNull(granulesList);
        assertEquals(1, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule); 
        
        // remove by filter
        final String fileLocation = "NCOM_wattemp_100_20081101T0000000_12.tiff";
        boolean result = manager.removeGranulesByCQL(DEFAULT_WS, coverageStoreName, coverageName, "location = '" + fileLocation + "'");
        Assert.assertTrue(result);
        
        granulesList = manager.getGranules(DEFAULT_WS, coverageStoreName, coverageName);
        assertNotNull(granulesList);
        assertFalse(granulesList.isEmpty());
        assertEquals(3, granulesList.size());
        granule = granulesList.get(0);
        assertNotNull(granule);
        
        // Readding that granule with harvest
        result = manager.harvestExternal(DEFAULT_WS, coverageStoreName, format, new ClassPathResource("testdata/granules/NCOM_wattemp_100_20081101T0000000_12.tiff").getFile().getAbsolutePath() );
        Assert.assertTrue(result);
        
        granulesList = manager.getGranules(DEFAULT_WS, coverageStoreName, coverageName, null, null, null);
        assertNotNull(granulesList);
        assertEquals(4, granulesList.size());
        assertFalse(granulesList.isEmpty());
        granule = granulesList.get(0);
        assertNotNull(granule);
        
        
        // delete
        delete(DEFAULT_WS, coverageStoreName);
    }


    /**
     * This method enables the various dimensions for the coverage autocreated for this test.
     * 
     * <p> Notice that 
     * @param wsName the workspace
     * @param coverageStoreName the coverage store name
     * @param csname the coverage name
     */
    
    private void fixDimensions(String wsName, String coverageStoreName, String csname) {
        final GSImageMosaicEncoder coverageEncoder = copyParameters(wsName, coverageStoreName,
                csname);
  
               
        // activate time dimension
        final GSDimensionInfoEncoder time=new GSDimensionInfoEncoder(true);
        time.setUnit("Seconds");
        time.setUnitSymbol("s");
        time.setPresentation(Presentation.CONTINUOUS_INTERVAL);
        coverageEncoder.setMetadataDimension("time", time);
        
        // activate run which is a custom dimension
        final GSDimensionInfoEncoder date=new GSDimensionInfoEncoder(true);
        date.setPresentation(Presentation.LIST);
        coverageEncoder.setMetadataDimension("date", date,true);
        
        final GSDimensionInfoEncoder depth=new GSDimensionInfoEncoder(true);
        depth.setPresentation(Presentation.LIST);
        depth.setUnit("Meters");
        depth.setUnitSymbol("m");
        coverageEncoder.setMetadataDimension("depth", depth,true);
        
        // persiste the changes
        boolean config=publisher.configureCoverage(coverageEncoder, wsName, csname);
        assertTrue(config);
       
    }


    /**
     * @param wsName
     * @param coverageStoreName
     * @param csname
     * @return
     * @throws NumberFormatException
     */
    private GSImageMosaicEncoder copyParameters(String wsName, String coverageStoreName,
            String csname) throws NumberFormatException {
        // get current config for the coverage to extract the params we want to set again
        final RESTCoverage coverage = reader.getCoverage(wsName, coverageStoreName, csname);
        if (coverage==null)
            return null;
        final Map<String, String> params = coverage.getParametersList();     
        
        // prepare and fill the encoder 
        final GSImageMosaicEncoder coverageEncoder = new GSImageMosaicEncoder();
        coverageEncoder.setName("mosaic");
        
        // set the current params, change here if you want to change the values
        for(Map.Entry<String, String> entry:params.entrySet()){
            if(entry.getKey().equals(GSImageMosaicEncoder.allowMultithreading)){
                coverageEncoder.setAllowMultithreading(Boolean.parseBoolean(entry.getValue()));
                continue;
            }
            
            if(entry.getKey().equals(GSImageMosaicEncoder.backgroundValues)){
                coverageEncoder.setBackgroundValues(entry.getValue());
                continue;
            }
            
            if(entry.getKey().equals(GSImageMosaicEncoder.filter)){
                coverageEncoder.setFilter(entry.getValue());
                continue;
            }
            
            if(entry.getKey().equals(GSImageMosaicEncoder.inputTransparentColor)){
                coverageEncoder.setInputTransparentColor(entry.getValue());
                continue;
            }
            
            if(entry.getKey().equals(GSImageMosaicEncoder.maxAllowedTiles)){
                coverageEncoder.setMaxAllowedTiles(Integer.parseInt(entry.getValue()));
                continue;
            }
            
            if(entry.getKey().equals(GSImageMosaicEncoder.MERGEBEHAVIOR)){
                coverageEncoder.setMergeBehavior(entry.getValue());
                continue;
            }
            
            if(entry.getKey().equals(GSImageMosaicEncoder.outputTransparentColor)){
                coverageEncoder.setOutputTransparentColor(entry.getValue());
                continue;
            }
            
            if(entry.getKey().equals(GSImageMosaicEncoder.SORTING)){
                coverageEncoder.setSORTING(entry.getValue());
                continue;
            }
            
            if(entry.getKey().equals(GSImageMosaicEncoder.SUGGESTED_TILE_SIZE)){
                coverageEncoder.setSUGGESTED_TILE_SIZE(entry.getValue());
                continue;
            }
            
            if(entry.getKey().equals(GSImageMosaicEncoder.USE_JAI_IMAGEREAD)){
                coverageEncoder.setUSE_JAI_IMAGEREAD(Boolean.parseBoolean(entry.getValue()));
                continue;
            }
            
        }
        return coverageEncoder;
    }
    
    /**
     * Deletes the provided coverage recursively with purging.
     * @param workspaceName
     * @param coverageStoreName
     * @throws Exception
     */
    private void delete(String workspaceName, String coverageStoreName) throws Exception {
        if (!enabled()) {
            return;
        }
        
        // delete mosaic
        boolean result = publisher.removeCoverageStore(workspaceName, coverageStoreName, true);
        assertTrue(result);
    }
 
}

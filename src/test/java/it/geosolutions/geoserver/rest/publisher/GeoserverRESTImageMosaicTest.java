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

package it.geosolutions.geoserver.rest.publisher;


import it.geosolutions.geoserver.rest.GeoServerRESTPublisher.ParameterConfigure;
import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import it.geosolutions.geoserver.rest.decoder.RESTCoverageStore;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageGranulesList;
import it.geosolutions.geoserver.rest.decoder.about.GSVersionDecoder;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.geosolutions.geoserver.rest.encoder.coverage.GSImageMosaicEncoder;
import it.geosolutions.geoserver.rest.encoder.dimensions.GSCoverageDimensionEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder.Presentation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import org.apache.commons.httpclient.NameValuePair;
import org.junit.Test;
import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Testcase for creating postgis-based resources on geoserver.
 * <P>
 * Since these tests require a running postgis instance, this is more like integration tests.<br/>
 * You may skip them by defining<tt> <pre>
 *        -DpgIgnore=true </pre></tt>
 * When <tt>pgIgnore</tt> is defined that way, failing tests will not break
 * the build: they will be logged as errors instead.
 *
 * <P>
 * The target postgis instance can be customized by defining the following env vars: <ul>
 * <LI><TT>pgHost</TT> (default <TT>localhost</TT>)</LI>
 * <LI><TT>pgPort</TT> (default: <TT>5432</TT>)</LI>
 * <LI><TT>pgDatabase</TT> (default: <TT>test</TT>)</LI>
 * <LI><TT>pgSchema</TT> (default: <TT>public</TT>)</LI>
 * <LI><TT>pgUser</TT> (default: <TT>utest</TT>)</LI>
 * <LI><TT>pgPassword</TT> (default: <TT>ptest</TT>)</LI>
 * </ul>
 *
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 *
 * @see GeoserverRESTTest
 */
public class GeoserverRESTImageMosaicTest extends GeoserverRESTTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoserverRESTImageMosaicTest.class);

    @Test
    public void testCreateDeleteImageMosaicDatastore() throws MalformedURLException, UnsupportedEncodingException {
        if (!enabled()) {
            return;
        }
        deleteAll();
        
        final String wsName = "geosolutions";
        final String coverageStoreName = "resttestImageMosaic";

        final GSImageMosaicEncoder coverageEncoder = new GSImageMosaicEncoder();
        /*
         * unused in mosaic creation
         * this is only useful if you want to modify an existing coverage:
         * publisher.configureCoverage(ce, wsname, csname);
         * or create a new one from an existing store:
         * publisher.createCoverage(ce, wsname, csname);
         */
//        coverageEncoder.setName("time_geotiff");
        
        coverageEncoder.setAllowMultithreading(true);
        coverageEncoder.setBackgroundValues("");
        coverageEncoder.setFilter("");
        coverageEncoder.setInputTransparentColor("");
        coverageEncoder.setLatLonBoundingBox(-180, -90, 180, 90, "EPSG:4326");
        coverageEncoder.setMaxAllowedTiles(6000);
        coverageEncoder.setNativeBoundingBox(-180, -90, 180, 90, "EPSG:4326");
        coverageEncoder.setOutputTransparentColor("");
        coverageEncoder.setProjectionPolicy(ProjectionPolicy.REPROJECT_TO_DECLARED);
        coverageEncoder.setSRS("EPSG:4326");
        coverageEncoder.setSUGGESTED_TILE_SIZE("256,256");
        coverageEncoder.setUSE_JAI_IMAGEREAD(true);

        GSVersionDecoder v=reader.getGeoserverVersion();
        if (v.compareTo(GSVersionDecoder.VERSION.v24)>=0){
            GSCoverageDimensionEncoder gsCoverageDimensionEncoder = new GSCoverageDimensionEncoder(
                    "GRAY_INDEX", "GridSampleDimension[-Infinity,Infinity]", "-inf", "inf",
                    "dobson units³", "REAL_32BITS");
            coverageEncoder.addCoverageDimensionInfo(gsCoverageDimensionEncoder);            
        }

            // activate time
            final GSDimensionInfoEncoder time=new GSDimensionInfoEncoder(true);
            time.setPresentation(Presentation.LIST);
            // set time metadata
            coverageEncoder.setMetadata("time", time);
            // not active elevation
            coverageEncoder.setMetadata("elevation", new GSDimensionInfoEncoder());

        
        
        assertTrue(publisher.createWorkspace(wsName));
        
        LOGGER.info(coverageEncoder.toString());
        
        final String styleName = "raster";
        File sldFile;
		try {
			sldFile = new ClassPathResource("testdata/raster.sld").getFile();
	        // insert style
	        assertTrue(publisher.publishStyle(sldFile));
		} catch (IOException e1) {
			assertFalse(e1.getLocalizedMessage(),Boolean.FALSE);
			e1.printStackTrace();
		}

        
        GSLayerEncoder layerEncoder=new GSLayerEncoder();
        
        layerEncoder.setDefaultStyle(styleName);
        LOGGER.info(layerEncoder.toString());
        // creation test
        RESTCoverageStore coverageStore =null;
        try {
        	final File mosaicFile = new ClassPathResource("testdata/time_geotiff/").getFile();
        	
        	if (!publisher.publishExternalMosaic(wsName,coverageStoreName,mosaicFile,coverageEncoder,layerEncoder)){
        		fail();
        	}
    		coverageStore = reader.getCoverageStore(wsName,coverageStoreName);
    		
    		if (coverageStore==null){
                LOGGER.error("*** coveragestore " + coverageStoreName + " has not been created.");
                fail("*** coveragestore " + coverageStoreName + " has not been created.");
    		}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
        // Get a Granule
        String coverageName = "time_geotiff";
        RESTStructuredCoverageGranulesList granules = reader.getGranules(wsName, coverageStoreName, coverageName, null, null, null);
        String granuleId = granules.get(0).getFid();
        // Test Granule Exists
        assertTrue(reader.existsGranule(wsName, coverageStoreName, coverageName, granuleId));
        // test a Granule does not exists
        assertFalse(reader.existsGranule(wsName, coverageStoreName, coverageName, granuleId.substring(0, granuleId.indexOf(".")) + "." + granules.size() + 1));
		
        // removing recursively coveragestore
        boolean removed = publisher.removeCoverageStore(coverageStore.getWorkspaceName(), coverageStore.getName(), true);
        if( ! removed ){
            LOGGER.error("*** CoverageStore " + coverageStoreName + " has not been removed.");
            fail("*** CoverageStore " + coverageStoreName + " has not been removed.");
        }
        
        assertTrue(publisher.removeStyle(styleName));
        assertTrue(publisher.removeWorkspace(wsName));
    }

    @Test
	public void testPublishImageMosaic() throws IOException {

		if (!enabled()) {
			return;
		}
		deleteAll();

		String storeName = "testImageMosaic";

		assertTrue(reader.getWorkspaces().isEmpty());

		assertTrue(publisher.createWorkspace(DEFAULT_WS));

		File imageMosaicFile = new ClassPathResource(
				"testdata/mosaic_geotiff.zip").getFile();

		// test publish
		boolean wp = publisher.publishImageMosaic(DEFAULT_WS, storeName,
				imageMosaicFile, ParameterConfigure.NONE, (NameValuePair) null);

		assertTrue("Publish imagemosaic with no layer configured, failed.", wp);

		assertTrue("Unpublish() failed",
				publisher.removeCoverageStore(DEFAULT_WS, storeName, true));

		// create default style
		File sldFile = new ClassPathResource("testdata/restteststyle.sld")
				.getFile();
		assertTrue(publisher.publishStyle(sldFile, "raster"));

		wp = publisher.publishImageMosaic(DEFAULT_WS, storeName, imageMosaicFile,
				ParameterConfigure.FIRST, new NameValuePair("coverageName",
						"imageMosaic_test"));

		assertTrue("Publish imagemosaic configuring layer name, failed.", wp);

		assertTrue("Unpublish() failed",
				publisher.removeCoverageStore(DEFAULT_WS, storeName, true));

		wp = publisher.publishImageMosaic(DEFAULT_WS, storeName,
				imageMosaicFile, ParameterConfigure.ALL, (NameValuePair) null);

		assertTrue(
				"Publish imagemosaic configuring all available layers, failed.",
				wp);

		assertTrue("Unpublish() failed",
				publisher.removeCoverageStore(DEFAULT_WS, storeName, true));
	}

}

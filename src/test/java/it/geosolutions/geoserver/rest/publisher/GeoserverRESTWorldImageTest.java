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

import java.io.File;
import java.io.IOException;

import org.apache.commons.httpclient.NameValuePair;
import org.junit.Test;
import static org.junit.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Testcase for publishing layers on geoserver.
 * We need a running GeoServer to properly run the tests. 
 * If such geoserver instance cannot be contacted, tests will be skipped.
 *
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GeoserverRESTWorldImageTest extends GeoserverRESTTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoserverRESTWorldImageTest.class);

    @Test
    public void testPublishWorldImage() throws IOException {

		if (!enabled()) {
			return;
		}
		deleteAll();
		String storeName = "testWorldimage";

		assertTrue(reader.getWorkspaces().isEmpty());

		assertTrue(publisher.createWorkspace(DEFAULT_WS));

		File worldImageFile = new ClassPathResource(
				"testdata/sw.zip").getFile();

		// test publish

		boolean wp = publisher.publishWorldImage(DEFAULT_WS, storeName,
				worldImageFile, ParameterConfigure.NONE, null);

		assertTrue("Publish worldfile with no layer configured, failed.", wp);
		
		assertTrue("Unpublish() failed", publisher.removeCoverageStore(DEFAULT_WS, storeName, true));
		
        // create default style
        File sldFile = new ClassPathResource("testdata/restteststyle.sld").getFile();
        assertTrue(publisher.publishStyle(sldFile,"raster"));
		
		wp = publisher.publishWorldImage(DEFAULT_WS, storeName,
				worldImageFile, ParameterConfigure.FIRST, new NameValuePair("coverageName", "worldImage_test"));
		
		assertTrue("Publish worldfile configuring layer name, failed.", wp);
		
		assertTrue("Unpublish() failed", publisher.removeCoverageStore(DEFAULT_WS, storeName, true));
		
		wp = publisher.publishWorldImage(DEFAULT_WS, storeName,
				worldImageFile, ParameterConfigure.ALL,null);
		
		assertTrue("Publish worldfile configuring all available layers, failed.", wp);
		
		assertTrue("Unpublish() failed", publisher.removeCoverageStore(DEFAULT_WS, storeName, true));
	}
}

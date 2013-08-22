/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2012 GeoSolutions S.A.S.
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

import it.geosolutions.geoserver.rest.GeoserverRESTTest;

import java.net.URI;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.core.io.ClassPathResource;

/**
 * @author Oscar Fonts
 */
public class GeoserverRESTPublishShpCollectionTest extends GeoserverRESTTest {

    final String workspace = DEFAULT_WS;
    final String storeName = "testshpcollection";
    
    @Test
    public void testLocalZip() throws Exception {
        if (!enabled()) {
            return;
        }
    	
        URI location = new ClassPathResource("testdata/multipleshp.zip").getFile().toURI();
        test(location);
    }
    
    @Test
    public void testExternalDir() throws Exception {
        if (!enabled()) {
            return;
        }
    	
        URI location = new ClassPathResource("testdata/multipleshapefiles").getFile().toURI();
        test(location);
    }
    
    void test(URI location) throws Exception {
    	
    	// Delete all resources except styles
    	deleteAllWorkspacesRecursively(); 
        
        // Create workspace
        assertTrue(publisher.createWorkspace(workspace));
        
        // Publish shp collection
        assertTrue(publisher.publishShpCollection(workspace, storeName, location));
        
        String storeType = reader.getDatastore(workspace, storeName).getStoreType();
        assertEquals(storeType, "Shapefile");
        
        // Test published layer names
        List<String> layers = reader.getLayers().getNames();
        assertTrue(layers.contains("cities"));
        assertTrue(layers.contains("boundaries"));
    }
}

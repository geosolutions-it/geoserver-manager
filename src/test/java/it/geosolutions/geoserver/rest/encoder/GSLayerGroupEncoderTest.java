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
package it.geosolutions.geoserver.rest.encoder;

import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import it.geosolutions.geoserver.rest.decoder.RESTLayerGroup;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import it.geosolutions.geoserver.rest.decoder.RESTPublished;
import it.geosolutions.geoserver.rest.decoder.RESTPublishedList;
import it.geosolutions.geoserver.rest.decoder.utils.NameLinkElem;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.core.io.ClassPathResource;

/**
 * @todo applied patch for https://github.com/geosolutions-it/geoserver-manager/issues/61
 * still need to split test encoder from integration
 */
public class GSLayerGroupEncoderTest extends GeoserverRESTTest {

    @Before
    public void setup() throws Exception {
        if (enabled()){
            init();
        }
    }
    
    private void init() throws IOException{
        String ws = "topp";
        String storeName = "testshpcollection";    
        
        // Delete all resources except styles
        deleteAllWorkspacesRecursively(); 
        
        // Create workspace
        assertTrue(publisher.createWorkspace(ws));
        
        // Publish shp collection
        URI location = new ClassPathResource("testdata/multipleshp.zip").getFile().toURI();
        assertTrue(publisher.publishShpCollection(ws, storeName, location));
        
        String storeType = reader.getDatastore(ws, storeName).getStoreType();
        assertEquals(storeType, "Shapefile");
        
        // Test published layer names
        List<String> layers = reader.getLayers().getNames();
        assertTrue(layers.contains("cities"));
        assertTrue(layers.contains("boundaries"));        
        
        // Publish style
        publisher.publishStyle(new ClassPathResource("testdata/default_line.sld").getFile(), "default_line");

    }
    
    @Test
    public void testCreateLayerGroup() throws Exception {
        
        if (!enabled()) return;
        
        String groupName = "my-tasmania";
                
        GSLayerGroupEncoder groupWriter = new GSLayerGroupEncoder();
        groupWriter.setBounds("EPSG:26713", 589425.93423656, 609518.6719560538, 4913959.224611808, 4928082.949945881);
        groupWriter.addLayer("topp:boundaries");
        groupWriter.addLayer("topp:cities");
        assertTrue(publisher.createLayerGroup(groupName, groupWriter));
        // Test exists
        assertTrue(reader.existsLayerGroup(null, groupName));
        try {
            RESTLayerGroup groupReader = reader.getLayerGroup(groupName);
            assertNull(groupReader.getWorkspace());            
            assertEquals(groupName, groupReader.getName());
            
            RESTPublishedList publishedList = groupReader.getPublishedList();
            if (publishedList != null) {
                // GeoServer >= 2.3
                assertEquals(2, publishedList.size());
                for (RESTPublished published : publishedList) {
                    assertEquals("layer", published.getType());
                    assertTrue("boundaries".equals(published.getName()) || "cities".equals(published.getName()));
                }                            
            } else {
                RESTLayerList layerList = groupReader.getLayerList();
                assertEquals(2, layerList.size());
                for (NameLinkElem layer : layerList) {
                    assertTrue("boundaries".equals(layer.getName()) || "cities".equals(layer.getName()));
                }                                            
            }
        } finally {
            assertTrue(publisher.removeLayerGroup(groupName));
            // Test not exists
            assertFalse(reader.existsLayerGroup(null, groupName));
        }
    }    
    
    @Test
    public void testCreateLayerGroupInWorkspace() throws Exception {
        
        if (!enabled()) return;
        
        String groupName = "my-tasmania-in-ws";
        
        GSLayerGroupEncoder groupWriter = new GSLayerGroupEncoder();
        groupWriter.setBounds("EPSG:26713", 589425.93423656, 609518.6719560538, 4913959.224611808, 4928082.949945881);
        groupWriter.addLayer("topp:boundaries");
        groupWriter.addLayer("topp:cities");
        assertTrue(publisher.createLayerGroup("topp", groupName, groupWriter));
        // Test exists
        assertTrue(reader.existsLayerGroup("topp", groupName));
        try {
            RESTLayerGroup groupReader = reader.getLayerGroup("topp", groupName);
            assertEquals("topp", groupReader.getWorkspace());            
            assertEquals(groupName, groupReader.getName());
            
            RESTPublishedList publishedList = groupReader.getPublishedList();
            if (publishedList != null) {
                // GeoServer >= 2.3
                assertEquals(2, publishedList.size());
                for (RESTPublished published : publishedList) {
                    assertEquals("layer", published.getType());
                    assertTrue("boundaries".equals(published.getName()) || "cities".equals(published.getName()));
                }                            
            } else {
                RESTLayerList layerList = groupReader.getLayerList();
                assertEquals(2, layerList.size());
                for (NameLinkElem layer : layerList) {
                    assertTrue("boundaries".equals(layer.getName()) || "cities".equals(layer.getName()));
                }    
            }
        } finally {
            assertTrue(publisher.removeLayerGroup("topp", groupName));
            // Test not exists
            assertFalse(reader.existsLayerGroup("topp", groupName));
        }
    }

    private void createTestLayerGroup(String workspace, String groupName) {
        
        if (!enabled()) return;
        
        GSLayerGroupEncoder groupWriter = new GSLayerGroupEncoder();
        groupWriter.setBounds("EPSG:26713", 589425.93423656, 609518.6719560538, 4913959.224611808, 4928082.949945881);
        groupWriter.addLayer("topp:boundaries");
        groupWriter.addLayer("topp:cities");
        assertTrue(publisher.createLayerGroup(workspace, groupName, groupWriter));        
    }
    
    @Test
    public void testConfigureLayerGroup() throws Exception {
        
        if (!enabled()) return;
        
        String groupName = "my-tasmania";
                
        createTestLayerGroup(null, groupName);
        try {
            GSLayerGroupEncoder groupWriter = new GSLayerGroupEncoder();
            groupWriter.addLayer("topp:boundaries");

            assertTrue(publisher.configureLayerGroup(groupName, groupWriter));  
            
            RESTLayerGroup groupReader = reader.getLayerGroup(groupName);
            assertNull(groupReader.getWorkspace());            
            assertEquals(groupName, groupReader.getName());
            
            RESTPublishedList publishedList = groupReader.getPublishedList();
            if (publishedList != null) {
                // GeoServer >= 2.3
                assertEquals(1, publishedList.size());
                for (RESTPublished published : publishedList) {
                    assertEquals("layer", published.getType());
                    assertTrue("boundaries".equals(published.getName()));
                }                            
            } else {
                RESTLayerList layerList = groupReader.getLayerList();
                assertEquals(1, layerList.size());
                for (NameLinkElem layer : layerList) {
                    assertTrue("boundaries".equals(layer.getName()));
                }                                            
            }
        } finally {
            assertTrue(publisher.removeLayerGroup(groupName));
        }
    }    
    
    @Test
    public void testConfigureLayerGroupInWorkspace() throws Exception {
        
        if (!enabled()) return;
        
        String groupName = "my-tasmania-in-ws";
        
        createTestLayerGroup("topp", groupName);
        try {
            GSLayerGroupEncoder groupWriter = new GSLayerGroupEncoder();
            groupWriter.addLayer("topp:boundaries");

            assertTrue(publisher.configureLayerGroup("topp", groupName, groupWriter));  
            
            RESTLayerGroup groupReader = reader.getLayerGroup("topp", groupName);
            assertEquals("topp", groupReader.getWorkspace());            
            assertEquals(groupName, groupReader.getName());
            
            RESTPublishedList publishedList = groupReader.getPublishedList();
            if (publishedList != null) {
                // GeoServer >= 2.3
                assertEquals(1, publishedList.size());
                for (RESTPublished published : publishedList) {
                    assertEquals("layer", published.getType());
                    assertTrue("boundaries".equals(published.getName()));
                }                            
            } else {
                RESTLayerList layerList = groupReader.getLayerList();
                assertEquals(1, layerList.size());
                for (NameLinkElem layer : layerList) {
                    assertTrue("boundaries".equals(layer.getName()));
                }                                            
            }
        } finally {
            assertTrue(publisher.removeLayerGroup("topp", groupName));
        }
    }    
    
    @Test
    public void testConfigureLayerGroup23() throws Exception {
        
        if (!enabled()) return;
        
        String groupName = "my-tasmania-23";
        
        createTestLayerGroup(null, groupName);
        try {
            GSLayerGroupEncoder23 groupWriter = new GSLayerGroupEncoder23();
            groupWriter.addLayer("topp:boundaries");
            groupWriter.setMode(GSLayerGroupEncoder23.MODE_NAMED);
            groupWriter.setTitle("my title");
            groupWriter.setAbstract("my abstract");
            
            assertTrue(publisher.configureLayerGroup(groupName, groupWriter));  
            
            RESTLayerGroup groupReader = reader.getLayerGroup(groupName);
            assertNull(groupReader.getWorkspace());            
            assertEquals(groupName, groupReader.getName());
            assertEquals("my title", groupReader.getTitle());            
            assertEquals("my abstract", groupReader.getAbstract());
            assertEquals(GSLayerGroupEncoder23.MODE_NAMED, groupReader.getMode());
            
            RESTPublishedList publishedList = groupReader.getPublishedList();
            assertEquals(1, publishedList.size());
            for (RESTPublished published : publishedList) {
                assertEquals("layer", published.getType());
                assertTrue("boundaries".equals(published.getName()));
            }                            
        } finally {
            assertTrue(publisher.removeLayerGroup(groupName));
        }
    }
    
    /**
     * This test only works with GeoServer >= 2.3
     */
    @Test
    public void testCreateNestedLayerGroup23() throws Exception {
        
        if (!enabled()) return;
        
        String groupName = "my-tasmania-eo";
        
        createTestLayerGroup(null, "tasmania");
        try {
            GSLayerGroupEncoder23 groupWriter = new GSLayerGroupEncoder23();
            groupWriter.setTitle("my title");
            groupWriter.setAbstract("my abstract");
            groupWriter.setMode(GSLayerGroupEncoder23.MODE_EO);
            groupWriter.setRootLayer("topp:boundaries", "default_line");
            groupWriter.setBounds("EPSG:26713", 589425.93423656, 609518.6719560538, 4913959.224611808, 4928082.949945881);
            groupWriter.addLayer("topp:cities");
            groupWriter.addLayerGroup("tasmania");
            
            assertTrue(publisher.createLayerGroup(groupName, groupWriter));
            try {
                RESTLayerGroup groupReader = reader.getLayerGroup(groupName);
                assertNull(groupReader.getWorkspace());            
                assertEquals(groupName, groupReader.getName());
                assertEquals("my title", groupReader.getTitle());            
                assertEquals("my abstract", groupReader.getAbstract());
                assertEquals(GSLayerGroupEncoder23.MODE_EO, groupReader.getMode());
                assertEquals("boundaries", groupReader.getRootLayer());
                
                RESTPublishedList publishedList = groupReader.getPublishedList();
                assertEquals(2, publishedList.size());
                for (RESTPublished published : publishedList) {
                    if ("layer".equals(published.getType())) {
                        assertEquals("cities", published.getName());
                    } else {
                        assertEquals("layerGroup", published.getType());
                        assertEquals("tasmania", published.getName());                    
                    }
                }            
            } finally {
                assertTrue(publisher.removeLayerGroup(groupName));
            }
        } finally {
            assertTrue(publisher.removeLayerGroup("tasmania"));
        }
    }        
}
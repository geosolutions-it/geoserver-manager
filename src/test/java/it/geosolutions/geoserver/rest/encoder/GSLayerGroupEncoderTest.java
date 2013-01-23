package it.geosolutions.geoserver.rest.encoder;

import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import it.geosolutions.geoserver.rest.decoder.RESTLayerGroup;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import it.geosolutions.geoserver.rest.decoder.RESTPublished;
import it.geosolutions.geoserver.rest.decoder.RESTPublishedList;
import it.geosolutions.geoserver.rest.decoder.utils.NameLinkElem;

import org.junit.Test;

public class GSLayerGroupEncoderTest extends GeoserverRESTTest {
    
    @Test
    public void testCreateLayerGroup() throws Exception {
        String groupName = "my-tasmania";
                
        GSLayerGroupEncoder groupWriter = new GSLayerGroupEncoder();
        groupWriter.setBounds("EPSG:26713", 589425.93423656, 609518.6719560538, 4913959.224611808, 4928082.949945881);
        groupWriter.addLayer("topp:tasmania_roads");
        groupWriter.addLayer("topp:tasmania_cities");
        assertTrue(publisher.createLayerGroup(groupName, groupWriter));
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
                    assertTrue("tasmania_roads".equals(published.getName()) || "tasmania_cities".equals(published.getName()));
                }                            
            } else {
                RESTLayerList layerList = groupReader.getLayerList();
                assertEquals(2, layerList.size());
                for (NameLinkElem layer : layerList) {
                    assertTrue("tasmania_roads".equals(layer.getName()) || "tasmania_cities".equals(layer.getName()));
                }                                            
            }
        } finally {
            assertTrue(publisher.removeLayerGroup(groupName));
        }
    }    
    
    @Test
    public void testCreateLayerGroupInWorkspace() throws Exception {
        String groupName = "my-tasmania-in-ws";
        
        GSLayerGroupEncoder groupWriter = new GSLayerGroupEncoder();
        groupWriter.setBounds("EPSG:26713", 589425.93423656, 609518.6719560538, 4913959.224611808, 4928082.949945881);
        groupWriter.addLayer("topp:tasmania_roads");
        groupWriter.addLayer("topp:tasmania_cities");
        assertTrue(publisher.createLayerGroup("topp", groupName, groupWriter));
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
                    assertTrue("tasmania_roads".equals(published.getName()) || "tasmania_cities".equals(published.getName()));
                }                            
            } else {
                RESTLayerList layerList = groupReader.getLayerList();
                assertEquals(2, layerList.size());
                for (NameLinkElem layer : layerList) {
                    assertTrue("tasmania_roads".equals(layer.getName()) || "tasmania_cities".equals(layer.getName()));
                }    
            }
        } finally {
            assertTrue(publisher.removeLayerGroup("topp", groupName));
        }
    }

    private void createTestLayerGroup(String workspace, String groupName) {
        GSLayerGroupEncoder groupWriter = new GSLayerGroupEncoder();
        groupWriter.setBounds("EPSG:26713", 589425.93423656, 609518.6719560538, 4913959.224611808, 4928082.949945881);
        groupWriter.addLayer("topp:tasmania_roads");
        groupWriter.addLayer("topp:tasmania_cities");
        assertTrue(publisher.createLayerGroup(workspace, groupName, groupWriter));        
    }
    
    @Test
    public void testConfigureLayerGroup() throws Exception {
        String groupName = "my-tasmania";
                
        createTestLayerGroup(null, groupName);
        try {
            GSLayerGroupEncoder groupWriter = new GSLayerGroupEncoder();
            groupWriter.addLayer("topp:tasmania_roads");

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
                    assertTrue("tasmania_roads".equals(published.getName()));
                }                            
            } else {
                RESTLayerList layerList = groupReader.getLayerList();
                assertEquals(1, layerList.size());
                for (NameLinkElem layer : layerList) {
                    assertTrue("tasmania_roads".equals(layer.getName()));
                }                                            
            }
        } finally {
            assertTrue(publisher.removeLayerGroup(groupName));
        }
    }    
    
    @Test
    public void testConfigureLayerGroupInWorkspace() throws Exception {
        String groupName = "my-tasmania-in-ws";
        
        createTestLayerGroup("topp", groupName);
        try {
            GSLayerGroupEncoder groupWriter = new GSLayerGroupEncoder();
            groupWriter.addLayer("topp:tasmania_roads");

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
                    assertTrue("tasmania_roads".equals(published.getName()));
                }                            
            } else {
                RESTLayerList layerList = groupReader.getLayerList();
                assertEquals(1, layerList.size());
                for (NameLinkElem layer : layerList) {
                    assertTrue("tasmania_roads".equals(layer.getName()));
                }                                            
            }
        } finally {
            assertTrue(publisher.removeLayerGroup("topp", groupName));
        }
    }    
    
    @Test
    public void testConfigureLayerGroup23() throws Exception {
        String groupName = "my-tasmania-23";
        
        createTestLayerGroup(null, groupName);
        try {
            GSLayerGroupEncoder23 groupWriter = new GSLayerGroupEncoder23();
            groupWriter.addLayer("topp:tasmania_roads");
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
                assertTrue("tasmania_roads".equals(published.getName()));
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
        String groupName = "my-tasmania-eo";
                
        GSLayerGroupEncoder23 groupWriter = new GSLayerGroupEncoder23();
        groupWriter.setTitle("my title");
        groupWriter.setAbstract("my abstract");
        groupWriter.setMode(GSLayerGroupEncoder23.MODE_EO);
        groupWriter.setRootLayer("topp:tasmania_roads", "simple_roads");
        groupWriter.setBounds("EPSG:26713", 589425.93423656, 609518.6719560538, 4913959.224611808, 4928082.949945881);
        groupWriter.addLayer("topp:tasmania_cities");
        groupWriter.addLayerGroup("tasmania");
        
        assertTrue(publisher.createLayerGroup(groupName, groupWriter));
        try {
            RESTLayerGroup groupReader = reader.getLayerGroup(groupName);
            assertNull(groupReader.getWorkspace());            
            assertEquals(groupName, groupReader.getName());
            assertEquals("my title", groupReader.getTitle());            
            assertEquals("my abstract", groupReader.getAbstract());
            assertEquals(GSLayerGroupEncoder23.MODE_EO, groupReader.getMode());
            assertEquals("tasmania_roads", groupReader.getRootLayer());
            
            RESTPublishedList publishedList = groupReader.getPublishedList();
            assertEquals(2, publishedList.size());
            for (RESTPublished published : publishedList) {
                if ("layer".equals(published.getType())) {
                    assertEquals("tasmania_cities", published.getName());
                } else {
                    assertEquals("layerGroup", published.getType());
                    assertEquals("tasmania", published.getName());                    
                }
            }            
        } finally {
            assertTrue(publisher.removeLayerGroup(groupName));
        }
    }        
}
package it.geosolutions.geoserver.rest.decoder;

import it.geosolutions.geoserver.rest.decoder.utils.NameLinkElem;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class RESTLayerGroupTest {
    
    @Test
    public void parseLayerGroup() throws IOException{
        InputStream is = RESTFeatureTypeListTest.class.getResourceAsStream("/testdata/layergroups.xml");
        String response = IOUtils.toString(is);
        RESTLayerGroup lg = RESTLayerGroup.build(response);
            
        String[] expected_layers=new String[]{
            "layer001",
            "layer002",
            "layer003",
            "geo:lg001",
            "geo:lg002"
        }; 
        
        String[] expected_styles=new String[]{
            "grass",
            null,
            "raster",
            null,
            null            
        }; 
        
        
        RESTPublishedList children = lg.getPublishedList();
        RESTStyleList styles = lg.getStyleList();
        
        for (int i=0;i<children.size();i++){
            RESTPublished child = children.get(i);
            NameLinkElem style = styles.get(i);
            Assert.assertEquals(child.getName(), expected_layers[i]);
            Assert.assertEquals(style.getName(), expected_styles[i]);
        }        
    }
}

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

import static org.junit.Assert.assertEquals;
import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import net.sf.json.JSONObject;

import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Testcase for publishing layers on geoserver. 
 * We need a running GeoServer to properly run the tests. 
 * If such geoserver instance cannot be contacted, tests will be skipped.
 * 
 * @author Alessio Fabiani, GeoSolutions S.A.S.
 * 
 */
public class GeoserverRESTImporterTest extends GeoserverRESTTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoserverRESTImporterTest.class);
    
    @After
    public void cleanUp() {
    }
    
    @Test
    public void testShapeFileImport() throws Exception {
        if (!enabled())
            return;
        
        // Creates a new Importer Context and gets back the ID
        int i = publisher.postNewImport();

        // Attaches to the new Importer Context a Task pointing to a shapefile's zip archive
        String data = new ClassPathResource("testdata/test_noepsg.zip").getFile().getAbsolutePath();
        int t = publisher.postNewTaskAsMultiPartForm(i, data);

        // Check that the Task was actually created and that the CRS has not recognized in this case
        JSONObject task = publisher.getTask(i, t);
        //assertEquals("NO_CRS", task.getString("state"));
        assertEquals("READY", task.getString("state"));

        // Prepare the JSON String instructing the Task about the SRS to use
        String json = "{\"layer\":{\"srs\":\"EPSG:26713\"}}";

        // Performing the Task update
        publisher.putTaskLayer(i, t, json);

        // Double check that the Task is in the READY state
        task = publisher.getTask(i, t);
        assertEquals("READY", task.getString("state"));
        assertEquals("nurc_10m_populated_places", task.getJSONObject("layer").getJSONObject("style").getString("name"));

        // Prepare the JSON String instructing the Task avout the SLD to use for the new Layer
        json = "{\"layer\":{\"style\":{\"name\": \"point\"}}}"; 

        // Performing the Task update
        publisher.putTaskLayer(i, t,json);

        // Double check that the Task is in the READY state and that the Style has been correctly updated
        task = publisher.getTask(i, t);
        assertEquals("READY", task.getString("state"));
        assertEquals("point", task.getJSONObject("layer").getJSONObject("style").getString("name"));

        // Finally starts the Import ...
        publisher.postImport(i);
    }

}

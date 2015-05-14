/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2015, Open Source Geospatial Foundation (OSGeo)
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
package it.geosolutions.geoserver.rest.publisher;

import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import net.sf.json.JSONObject;

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
 * @author Alessio Fabiani
 * 
 */
public class GeoserverRESTImporterTest extends GeoserverRESTTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoserverRESTImporterTest.class);
    
    @Test
    public void testShapeFileImport() throws Exception {
        if (!enabled())
            return;
        
        // Creates a new Importer Context and gets back the ID
        int i = publisher.postNewImport();

        // Attaches to the new Importer Context a Task pointing to a shapefile's zip archive
        int t = publisher.postNewTaskAsMultiPartForm(i, new ClassPathResource("testdata/test_noepsg.zip").getPath());

        // Check that the Task was actually created and that the CRS has not recognized in this case
        JSONObject task = publisher.getTask(i, t);
        assertEquals("NO_CRS", task.getString("state"));

        // Prepare the JSON String instructing the Task about the SRS to use
        String json = 
        "{" +
          "\"task\": {" +
            "\"layer\": {" +
                    "\"srs\": \"EPSG:4326\"" + 
             "}" +
           "}" + 
        "}";

        // Performing the Task update
        publisher.putTask(i, t, json);

        // Double check that the Task is in the READY state
        task = publisher.getTask(i, t);
        assertEquals("READY", task.getString("state"));
        assertEquals("gs_archsites", task.getJSONObject("layer").getJSONObject("style").getString("name"));

        // Prepare the JSON String instructing the Task avout the SLD to use for the new Layer
        json = 
        "{" +
          "\"task\": {" +
            "\"layer\": {" +
              "\"style\": {" +
                    "\"name\": \"point\"" + 
                 "}" +
               "}" +
           "}" + 
        "}";

        // Performing the Task update
        publisher.putTask(i, t,json);

        // Double check that the Task is in the READY state and that the Style has been correctly updated
        task = publisher.getTask(i, t);
        assertEquals("READY", task.getString("state"));
        assertEquals("point", task.getJSONObject("layer").getJSONObject("style").getString("name"));

        // Finally starts the Import ...
        publisher.postImport(i);
    }

}

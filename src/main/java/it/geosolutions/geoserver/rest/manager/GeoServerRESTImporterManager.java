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
package it.geosolutions.geoserver.rest.manager;

import it.geosolutions.geoserver.rest.HTTPUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.sf.json.JSON;
import net.sf.json.JSONObject;

import org.geoserver.importer.VFSWorker;
import org.restlet.data.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Alessio Fabiani
 *
 */
public class GeoServerRESTImporterManager extends GeoServerRESTAbstractManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoServerRESTImporterManager.class);
    
    /**
     * Default constructor.
     *
     * @param restURL GeoServer REST API endpoint
     * @param username GeoServer REST API authorized username
     * @param password GeoServer REST API password for the former username
     */
    public GeoServerRESTImporterManager(URL restURL, String username, String password)
            throws IllegalArgumentException {
        super(restURL, username, password);
    }

    /**
     * Retrieves the Import JSON Object given its identifier
     * 
     * @param imp int: Import context number ID
     */
    public JSONObject getImport(int imp) throws Exception {
        JSON json = HTTPUtils.getAsJSON(String.format(buildUrl()+"/%d", imp), gsuser , gspass);
        return ((JSONObject)json).getJSONObject("import");
    }

    /**
     * Retrieves the Import Task JSON Object given its identifier and task number
     * 
     * @param imp int: Import context number ID
     * @param task int: Task number
     */
    public JSONObject getTask(int imp, int task) throws Exception {
        JSON json = HTTPUtils.getAsJSON(String.format(buildUrl()+"/%d/tasks/%d?expand=all", imp, task), gsuser , gspass);
        return ((JSONObject)json).getJSONObject("task");
    }

    /**
     * Example usage:
     * <pre>
     *  // Creates a new Importer Context and gets back the ID
     *  int i = postNewImport();
     *  
     *  // Attaches to the new Importer Context a Task pointing to a shapefile's zip archive
     *  int t = postNewTaskAsMultiPartForm(i, "/path_to/shape/archsites_no_crs.zip");
     *
     *  // Check that the Task was actually created and that the CRS has not recognized in this case
     *  JSONObject task = getTask(i, t);
     *  assertEquals("NO_CRS", task.getString("state"));
     *  
     *  // Prepare the JSON String instructing the Task about the SRS to use
     *  String json = 
     *  "{" +
     *    "\"task\": {" +
     *      "\"layer\": {" +
     *              "\"srs\": \"EPSG:4326\"" + 
     *       "}" +
     *     "}" + 
     *  "}";
     *  
     *  // Performing the Task update
     *  putTask(i, t, json);
     *
     *  // Double check that the Task is in the READY state
     *  task = getTask(i, t);
     *  assertEquals("READY", task.getString("state"));
     *  assertEquals("gs_archsites", task.getJSONObject("layer").getJSONObject("style").getString("name"));
     *  
     *  // Prepare the JSON String instructing the Task avout the SLD to use for the new Layer
     *  json = 
     *  "{" +
     *    "\"task\": {" +
     *      "\"layer\": {" +
     *        "\"style\": {" +
     *              "\"name\": \"point\"" + 
     *           "}" +
     *         "}" +
     *     "}" + 
     *  "}";
     *  
     *  // Performing the Task update
     *  putTask(i, t,json);
     *
     *  // Double check that the Task is in the READY state and that the Style has been correctly updated
     *  task = getTask(i, t);
     *  assertEquals("READY", task.getString("state"));
     *  assertEquals("point", task.getJSONObject("layer").getJSONObject("style").getString("name"));
     *  
     *  // Finally starts the Import ...
     *  postImport(i);
     * </pre>
     * 
     * @param imp int: Import context number ID
     * @param task int: Task number
     * @param json String: JSON containing the Task properties to be updated
     * @throws Exception
     */
    public void putTask(int imp, int task, final String json) throws Exception {
        HTTPUtils.putJson(String.format(buildUrl()+"/%d/tasks/%d", imp, task), json, gsuser, gspass);
    }

    /**
     * Just update the Layers properties associated to a Task (t) in the Importer Context (i).
     * 
     * e.g.:
     * <pre>
     * putTaskLayer(i, t, "{\"title\":\"Archsites\", \"abstract\":\"Archeological Sites\"}");
     * </pre>
     * 
     * @param imp int: Import context number ID
     * @param task int: Task number
     * @param json String: JSON containing the Layer properties to be updated
     * @throws Exception
     */
    public void putTaskLayer(int imp, int task, final String json) throws Exception {
        HTTPUtils.putJson(String.format(buildUrl()+"/%d/tasks/%d/layer", imp, task), json, gsuser, gspass);
    }

    /**
     * Creates an empty Importer Context.
     * 
     * @return The new Importer Context ID
     * @throws Exception
     */
    public int postNewImport() throws Exception {
        return postNewImport(null);
    }
    
    /**
     * e.g.:
     * <pre>
     * String body = 
     *         "{" + 
     *              "\"import\": { " + 
     *                  "\"data\": {" +
     *                     "\"type\": \"mosaic\", " + 
     *                     "\"time\": {" +
     *                        " \"mode\": \"auto\"" + 
     *                     "}" + 
     *                   "}" +
     *              "}" + 
     *         "}";
     * </pre>
     * 
     * @param body JSON String representing the Importer Context definition
     * @return The new Importer Context ID
     * @throws Exception
     */
    public int postNewImport(String body) throws Exception {
        String resp = body == null ? HTTPUtils.postJson(buildUrl(), "", gsuser, gspass)
            : HTTPUtils.postJson(buildUrl(), body, gsuser, gspass);
        
        JSONObject json = (JSONObject) HTTPUtils.json(resp);
        JSONObject imprt = json.getJSONObject("import");
        return imprt.getInt("id");
    }

    /**
     * Actually starts the READY State Import.
     * 
     * @param imp int: Import context number ID
     * @throws Exception
     */
    public void postImport(int imp) throws Exception {
        HTTPUtils.postJson(buildUrl()+"/" + imp, "", gsuser, gspass);
    }

    /**
     * 
     * @param imp int: Import context number ID
     * @param data
     * @return
     * @throws Exception
     */
    public int postNewTaskAsMultiPartForm(int imp, String data) throws Exception {
        String resp = HTTPUtils.postMultipartForm(buildUrl()+"/" + imp + "/tasks", unpack(data), gsuser, gspass);
        
        JSONObject json = (JSONObject) HTTPUtils.json(resp);

        JSONObject task = json.getJSONObject("task");
        return task.getInt("id");
    }

    /**
     * Allows to attach a new zip file to an existing Importer Context.
     * 
     * @param imp int: Import context number ID
     * @param path
     * @return
     * @throws Exception
     */
    public int putNewTask(int imp, String path) throws Exception {
        File zip = new File(path);

        String resp = HTTPUtils.put(buildUrl()+"/" + imp + "/tasks/" + zip.getName(), zip, MediaType.APPLICATION_ZIP.toString(), gsuser, gspass);

        JSONObject json = (JSONObject) HTTPUtils.json(resp);

        JSONObject task = json.getJSONObject("task");
        return task.getInt("id");
    }
    
    //=========================================================================
    // Util methods
    //=========================================================================
    
    /**
     * Creates the base REST URL for the imports
     */
    protected String buildUrl() {
        StringBuilder sUrl = new StringBuilder(gsBaseUrl.toString()).append("/rest/imports");

        return sUrl.toString();
    }

    /**
     * Creates a temporary file
     * 
     * @return Path to the temporary file
     * @throws Exception
     */
    public static File tmpDir() throws Exception {
        File dir = File.createTempFile("importer", "data", new File("target"));
        dir.delete();
        dir.mkdirs();
        return dir;
    }
    
    /**
     * Expands a zip archive into the temporary folder.
     * 
     * @param path The absolute path to the source zip file
     * @return Path to the temporary folder containing the expanded files
     * @throws Exception
     */
    public static File unpack(String path) throws Exception {
        return unpack(path, tmpDir());
    }
    
    /**
     * Expands a zip archive into the target folder.
     * 
     * @param path The absolute path to the source zip file
     * @param dir Full path of the target folder where to expand the archive
     * @return Path to the temporary folder containing the expanded files
     * @throws Exception
     */
    public static File unpack(String path, File dir) throws Exception {
        
        File file = new File(path);
        
        new VFSWorker().extractTo(file, dir);
        if (!file.delete()) {
            // fail early as tests will expect it's deleted
            throw new IOException("deletion failed during extraction");
        }
        
        return dir;
    }
}

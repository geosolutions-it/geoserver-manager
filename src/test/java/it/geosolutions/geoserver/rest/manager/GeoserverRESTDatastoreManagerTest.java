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
package it.geosolutions.geoserver.rest.manager;

import it.geosolutions.geoserver.rest.GeoserverRESTTest;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.encoder.datastore.GSAbstractDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSDirectoryOfShapefilesDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSShapefileDatastoreEncoder;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import org.junit.Test;

/**
 * Test datastore handling (create, read and update):
 * 
 * <ul><li>Tests all the constructors and setters from
 * {@link GSDirectoryOfShapefilesDatastoreEncoder} and parent classes
 * ({@link GSShapefileDatastoreEncoder}, {@link GSAbstractDatastoreEncoder}).
 * 
 * <li>Tests constructors and getters from {@link RESTDataStore} (reader).
 * 
 * <li>Tests {@link GeoServerRESTDatastoreManager} create and update methods.</ul>
 * 
 * <p>The sequence is:
 * <ol>
 * <li>Create a DirectoryOfShapefilesDatastoreEncoder, with default parameters.
 * <li>Publish via GeoServerRESTDatastoreManager.create.
 * <li>Read the datastore from server.
 * <li>Test all parameter values.
 * <li>Create a new Encoder from it.
 * <li>Change all datastore parameter to non-default ones.
 * <li>Update via GeoServerRESTDatastoreManager.update.
 * <li>Read again.
 * <li>Test all new values.
 * </ol>
 * 
 * @author Oscar Fonts
 */
public class GeoserverRESTDatastoreManagerTest extends GeoserverRESTTest {
	
	private static final String WS_NAME = DEFAULT_WS;
	private static final String DS_NAME = "testCreateDatastore";
	private static final String DS_DESCRIPTION = "A description";
	private static URL LOCATION_1;
	private static URL LOCATION_2;
	
	public GeoserverRESTDatastoreManagerTest() throws Exception {
		LOCATION_1 = new URL("file:data/1");
		LOCATION_2 = new URL("file:data/2");
	}

	@Test
	public void test() throws Exception {
        if (!enabled()) {
            return;
        }
		
    	// Delete all resources except styles
    	deleteAllWorkspacesRecursively();
		
        // Create workspace
        assertTrue(publisher.createWorkspace(WS_NAME));
        
		// Create a directory of spatial files with default parameters
		GSDirectoryOfShapefilesDatastoreEncoder create = new GSDirectoryOfShapefilesDatastoreEncoder(DS_NAME, LOCATION_1);
		assertTrue(manager.getDatastoreManager().create(WS_NAME, create));
		
		// Read the store from server; check all parameter values
		RESTDataStore read = reader.getDatastore(WS_NAME, DS_NAME);
		assertEquals(read.getName(), DS_NAME);
		assertEquals(read.getWorkspaceName(), WS_NAME);
		assertEquals(read.isEnabled(), true);
		
		Map<String,String> connParams = read.getConnectionParameters();
		assertEquals(connParams.get("url"), LOCATION_1.toString());
		assertEquals(connParams.get("charset"), "ISO-8859-1");
		assertEquals(connParams.get("create spatial index"), "true");
		assertEquals(connParams.get("memory mapped buffer"), "false");
		assertEquals(connParams.get("cache and reuse memory maps"), "true");

		// Change all parameter to non-default values
		GSDirectoryOfShapefilesDatastoreEncoder update = new GSDirectoryOfShapefilesDatastoreEncoder(read);
		update.setDescription(DS_DESCRIPTION);
		update.setEnabled(false);
		update.setUrl(LOCATION_2);
		update.setCharset(Charset.forName("UTF-8"));
		update.setCreateSpatialIndex(false);
		update.setMemoryMappedBuffer(true);		
		update.setCacheAndReuseMemoryMaps(false);
		
		//update the store
		assertTrue(manager.getDatastoreManager().update(WS_NAME, update));
		
		// Read again, check that all parameters have changed
		read = reader.getDatastore(WS_NAME, DS_NAME);
		assertEquals(read.getWorkspaceName(), WS_NAME);
		assertEquals(read.isEnabled(), false);
		connParams = read.getConnectionParameters();
		assertEquals(connParams.get("url"), LOCATION_2.toString());
		assertEquals(connParams.get("charset"), "UTF-8");
		assertEquals(connParams.get("create spatial index"), "false");
		assertEquals(connParams.get("memory mapped buffer"), "true");
		assertEquals(connParams.get("cache and reuse memory maps"), "false");
	}
}

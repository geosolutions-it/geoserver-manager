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

package it.geosolutions.geoserver.rest;

import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.decoder.RESTDataStoreList;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import it.geosolutions.geoserver.rest.decoder.RESTNamespaceList;
import it.geosolutions.geoserver.rest.decoder.RESTWorkspaceList;
import it.geosolutions.geoserver.rest.decoder.about.GSVersionDecoder;
import it.geosolutions.geoserver.rest.decoder.utils.NameLinkElem;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import static org.junit.Assert.*;


/**
 *
 * @author etj
 */
public class GeoserverRESTReaderTest extends GeoserverRESTTest {

	private final static Logger LOGGER = LoggerFactory.getLogger(GeoserverRESTReaderTest.class);
	/**
	 * Test of getLayers method, of class GeoServerRESTReader.
	 */
	@Test
	public void testGetLayers() {
        if(!enabled()) return;

		RESTLayerList result = reader.getLayers();
		assertNotNull(result);
//        assertEquals(/*CHANGEME*/19, result.getChildren("layer").size()); // value in default gs installation

//        System.out.println("Layers:" + result.getChildren("layer").size());
        LOGGER.debug("Layers: " + result.size());
//		LOGGER.debug("Layers:" );
        for (NameLinkElem shlayer : result) {
            assertNotNull(shlayer.getName());
            LOGGER.debug(shlayer.getName() + " ");
        }
//		for (Element layer : (List<Element>)result.getChildren("layer")) {
//			System.out.print(layer.getChildText("name") + " ");
//		}
        LOGGER.debug("");
	}
	
	/**
	 * Test of getLayers method with a given workspace, of class GeoServerRESTReader.
	 * Requires Geoserver > 2.13
	 * @throws IOException 
	 */
	@Test
	public void testGetLayersWithWorkspace() throws IOException {
        if(!enabled()) return;
        // Skip the test if Geoserver < 2.13
        if(GSVersionDecoder.VERSION.v213.compareTo(GSVersionDecoder.VERSION.getVersion(GS_VERSION)) > 0) {
            return;
        }
        deleteAllWorkspacesRecursively();
        
        assertTrue(publisher.createWorkspace(DEFAULT_WS));        
        File zipFile = new ClassPathResource("testdata/resttestshp.zip").getFile(); 
        // test insert
        assertTrue(publisher.publishShp(DEFAULT_WS, "resttestshp", "cities", zipFile));		
        assertTrue(reader.getLayers(DEFAULT_WS).size() == 1);
        LOGGER.debug("");
	}

	/**
	 * Test of getDatastores method, of class GeoServerRESTReader.
	 */
	@Test
	public void testGetDatastores() {
        if(!enabled()) return;

		RESTWorkspaceList wslist = reader.getWorkspaces();
		assertNotNull(wslist);
//        assertEquals(7, wslist.size()); // value in default gs installation

        LOGGER.debug("Workspaces: " + wslist.size());
        int dsnum = 0;
		for (RESTWorkspaceList.RESTShortWorkspace ws : wslist) {
            LOGGER.debug("Getting DSlist for WS " + ws.getName() + "..." );
			RESTDataStoreList result = reader.getDatastores(ws.getName());
			assertNotNull(result);
            dsnum += result.size();
            for (NameLinkElem ds : result) {
                assertNotNull(ds.getName());
                LOGGER.debug(ds.getName() + " " );
                RESTDataStore datastore = reader.getDatastore(ws.getName(), ds.getName());
                assertNotNull(datastore);
                assertEquals(ds.getName(), datastore.getName());
                assertEquals(ws.getName(), datastore.getWorkspaceName());
            }
            LOGGER.debug("");
		}
        LOGGER.debug("");
        LOGGER.debug("Datastores:" + dsnum); // value in default gs installation
//        assertEquals(4, dsnum); // value in default gs installation

	}

	@Test
	public void testGetWSDSNames() {
        if(!enabled())
            return;

		RESTWorkspaceList wslist = reader.getWorkspaces();
        assertNotNull(wslist);
//        assertEquals(7, wslist.size()); // value in default gs installation

		List<String> wsnames = reader.getWorkspaceNames();
        assertNotNull(wsnames);
//        assertEquals(7, wsnames.size()); // value in default gs installation

//        System.out.println("Workspaces: " + wslist.size());
        int wscnt = 0;
		for (RESTWorkspaceList.RESTShortWorkspace ws : wslist) {
            String wsname = wsnames.get(wscnt++);

			List<String> dsnames = reader.getDatastores(wsname).getNames();
            RESTDataStoreList dslist = reader.getDatastores(ws.getName());
			assertNotNull(dsnames);
			assertNotNull(dslist);
            assertEquals(dsnames.size(), dslist.size());
		}
	}

	/**
	 * Test of getDatastore method, of class GeoServerRESTReader.
	 */
	@Test
	public void testGetDatastore() {
        //tested in testGetDatastores()
	}

	/**
	 * Test of getLayer method, of class GeoServerRESTReader.
	 */
	@Test
	public void testGetLayer() {
	}

	/**
	 * Test of getNamespaceNames method, of class GeoServerRESTReader.
	 */
	@Test
	public void testGetNamespaces() {
        if(!enabled()) return;

        RESTNamespaceList result = reader.getNamespaces();
        List<String> names = reader.getNamespaceNames();
		assertNotNull(result);
//        assertEquals(7, result.size()); // value in default gs installation
		assertNotNull(names);
        assertEquals(names.size(), result.size()); // value in default gs installation

        LOGGER.debug("Namespaces:" + result.size());
        LOGGER.debug("Namespaces:" );
        int namesIdx = 0;
        for (RESTNamespaceList.RESTShortNamespace ns : result) {
            assertEquals("namespace mismatch", names.get(namesIdx++), ns.getName());
            LOGGER.debug(ns.getName() + " " );
        }
        LOGGER.debug("");
	}
	
	/**
	 * Test of getWorkspaceNames method, of class GeoServerRESTReader.
	 */
	@Test
	public void testGetNamespaceNames() {
        if(!enabled()) return;

		List<String> names = reader.getNamespaceNames();
		assertNotNull(names);
//        assertEquals(7, names.size()); // value in default gs installation

		LOGGER.debug("Namespaces:" + names.size());
		LOGGER.debug("Namespaces:");
		for (String name : names) {
			LOGGER.debug(name + " ");
		}
		LOGGER.debug("");
	}

	/**
	 * Test of getWorkspaceNames method, of class GeoServerRESTReader.
	 */
	@Test
	public void testGetWorkspaces() {
        if(!enabled()) return;

		RESTWorkspaceList wslist = reader.getWorkspaces();
		assertNotNull(wslist);
//        assertEquals(7, wslist.size()); // value in default gs installation

		LOGGER.debug("Workspaces:" + wslist.size());
		LOGGER.debug("Workspaces:");
		for (RESTWorkspaceList.RESTShortWorkspace ws : wslist) {
			LOGGER.debug(ws.getName() + " ");
		}
		LOGGER.debug("");

        assertEquals(wslist.size(), reader.getWorkspaceNames().size());
	}
	/**
	 * Test of getWorkspaceNames method, of class GeoServerRESTReader.
	 */
	@Test
	public void testGetWorkspaceNames() {
        if(!enabled()) return;

		List<String> names = reader.getWorkspaceNames();
		assertNotNull(names);
//        assertEquals(7, names.size()); // value in default gs installation

		LOGGER.debug("Workspaces:" + names.size());
		LOGGER.debug("Workspaces:");
		for (String name : names) {
			LOGGER.debug(name + " ");
		}
		LOGGER.debug("");
	}

}

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

import it.geosolutions.geoserver.rest.decoder.RESTCoverage;
import it.geosolutions.geoserver.rest.decoder.RESTCoverageStore;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.decoder.RESTFeatureType;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTLayerGroup;
import it.geosolutions.geoserver.rest.decoder.utils.NameLinkElem;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Initializes REST params.
 * <P>
 * <B>These tests are destructive, so you have to explicitly enable them</B>
 * by setting the env var <TT>resttest</TT> to <TT>true</TT>.
 * <P>
 * The target geoserver instance can be customized by defining the following env vars: <ul>
 * <LI><TT>resturl</TT> (default <TT>http://localhost:8080/geoserver</TT>)</LI>
 * <LI><TT>restuser</TT> (default: <TT>admin</TT>)</LI>
 * <LI><TT>restpw</TT> (default: <TT>geoserver</TT>)</LI>
 * </ul>
 *
 * @author etj
 */
public abstract class GeoserverRESTTest extends TestCase {
    private final static Logger LOGGER = LoggerFactory.getLogger(GeoserverRESTTest.class);

    protected static final String DEFAULT_WS = "geosolutions";

	public static final String RESTURL;
	public static final String RESTUSER;
	public static final String RESTPW;

	public static final URL URL;
	public static final GeoServerRESTReader reader;
	public static final GeoServerRESTPublisher publisher;

    private static boolean enabled = false;
    private static Boolean existgs = null;
    
	static {
        RESTURL  = getenv("gsmgr_resturl",  "http://localhost:8080/geoserver");
        RESTUSER = getenv("gsmgr_restuser", "admin");
        RESTPW   = getenv("gsmgr_restpw",   "geoserver");
        
        // These tests will destroy data, so let's make sure we do want to run them
        enabled  = getenv("gsmgr_resttest", "false").equalsIgnoreCase("true");
        if( ! enabled )
            LOGGER.warn("Tests are disabled. Please read the documentation to enable them.");

		URL lurl = null;
		try {
			lurl = new URL(RESTURL);
		} catch (MalformedURLException ex) {
		}

		URL = lurl;
		reader = new GeoServerRESTReader(lurl, RESTUSER, RESTPW);
        publisher = new GeoServerRESTPublisher(RESTURL, RESTUSER, RESTPW);
	}

    private static String getenv(String envName, String envDefault) {
        String env = System.getenv(envName);
        String ret = System.getProperty(envName, env);
        LOGGER.debug("env var " + envName + " is " + ret);
        return ret != null? ret : envDefault;
    }

    public GeoserverRESTTest(String testName) {
        super(testName);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        if(enabled) {
            if(existgs == null) {
                existgs = reader.existGeoserver();
                if ( ! existgs ) {
                    LOGGER.error("TESTS WILL FAIL BECAUSE NO GEOSERVER WAS FOUND AT " + RESTURL + " ("+ RESTUSER+":"+RESTPW+")");
                } else {
                    LOGGER.info("Using geoserver instance " + RESTUSER+":"+RESTPW+ " @ " + RESTURL);
                }
            }

            if ( ! existgs ) {
                System.out.println("Failing test " + this.getClass().getSimpleName() + "::" + this.getName() + " : geoserver not found");
                fail("GeoServer not found");
            }
            System.out.println("\n-------------------> RUNNING TEST " + this.getName());
        } else {
            System.out.println("Skipping test " + this.getClass().getSimpleName() + "::" + this.getName());
        }
    }

    protected boolean enabled() {
        return enabled;
    }

    protected void deleteAll() {
        LOGGER.info("Starting DELETEALL procedure");
        deleteAllLayerGroups();
        assertTrue("Some layergroups were not removed", reader.getLayerGroups().isEmpty());

        deleteAllLayers();
        assertTrue("Some layers were not removed", reader.getLayers().isEmpty());

        deleteAllCoverageStores();
        deleteAllDataStores();

        deleteAllWorkspaces();
//        assertTrue("Some workspaces were not removed", reader.getWorkspaces().isEmpty());

        deleteAllStyles();
        assertTrue("Some styles were not removed", reader.getStyles().isEmpty());

        LOGGER.info("ENDING DELETEALL procedure");
    }

    private void deleteAllLayerGroups() {
        List<String> groups = reader.getLayerGroups().getNames();
        LOGGER.info("Found " + groups.size() + " layerGroups");
        for (String groupName : groups) {
            RESTLayerGroup group = reader.getLayerGroup(groupName);
            StringBuilder sb = new StringBuilder("Group: ").append(groupName).append(":");
            for (NameLinkElem layer : group.getLayerList()) {
                sb.append(" ").append(layer);
            }

            boolean removed = publisher.removeLayerGroup(groupName);
            LOGGER.info(sb.toString()+ ": removed: " + removed);
            assertTrue("LayerGroup not removed: " + groupName, removed);
        }

    }

    private void deleteAllLayers() {
        List<String> layers = reader.getLayers().getNames();
        for (String layerName : layers) {
            RESTLayer layer = reader.getLayer(layerName);
            if(layer.getType() == RESTLayer.TYPE.VECTOR)
                deleteFeatureType(layer);
            else if(layer.getType() == RESTLayer.TYPE.RASTER)
                deleteCoverage(layer);
            else
                LOGGER.error("Unknown layer type " + layer.getType());
        }

    }

    private void deleteAllCoverageStores() {
        List<String> workspaces = reader.getWorkspaceNames();
        for (String workspace : workspaces) {
            List<String> stores = reader.getCoverageStores(workspace).getNames();

            for (String storename : stores) {
//                RESTCoverageStore store = reader.getCoverageStore(workspace, storename);

                LOGGER.warn("Deleting CoverageStore " + workspace + " : " + storename);
                boolean removed = publisher.removeCoverageStore(workspace, storename, false);
                assertTrue("CoverageStore not removed " + workspace + " : " + storename, removed);
            }
        }
    }

    private void deleteAllDataStores() {
        List<String> workspaces = reader.getWorkspaceNames();
        for (String workspace : workspaces) {
            List<String> stores = reader.getDatastores(workspace).getNames();

            for (String storename : stores) {
                RESTDataStore store = reader.getDatastore(workspace, storename);

//                if(store.getType() == RESTDataStore.DBType.POSTGIS) {
//                    LOGGER.info("Skipping PG datastore " + store.getWorkspaceName()+":"+store.getName());
//                    continue;
//                }

                LOGGER.warn("Deleting DataStore " + workspace + " : " + storename);
                boolean removed = publisher.removeDatastore(workspace, storename, false);
                assertTrue("DataStore not removed " + workspace + " : " + storename, removed);
            }
        }
    }
    
    protected void deleteAllWorkspacesRecursively() {
        List<String> workspaces = reader.getWorkspaceNames();
        for (String workspace : workspaces) {
                LOGGER.warn("Deleting Workspace " + workspace );
                boolean removed = publisher.removeWorkspace(workspace,true);
                assertTrue("Workspace not removed " + workspace, removed );

        }
    }

    protected void deleteAllWorkspaces() {
        List<String> workspaces = reader.getWorkspaceNames();
        for (String workspace : workspaces) {
                LOGGER.warn("Deleting Workspace " + workspace );
                boolean removed = publisher.removeWorkspace(workspace,false);
                assertTrue("Workspace not removed " + workspace, removed );

        }
    }

    private void deleteAllStyles() {
        List<String> styles = reader.getStyles().getNames();
        for (String style : styles) {
                LOGGER.warn("Deleting Style " + style );
                boolean removed = publisher.removeStyle(style);
                assertTrue("Style not removed " + style, removed );

        }
    }

    private void deleteFeatureType(RESTLayer layer) {
        RESTFeatureType featureType = reader.getFeatureType(layer);
        RESTDataStore datastore = reader.getDatastore(featureType);

        LOGGER.warn("Deleting FeatureType"
                + datastore.getWorkspaceName() + " : "
                + datastore.getName() + " / "
                + featureType.getName()
                );

        boolean removed = publisher.unpublishFeatureType(datastore.getWorkspaceName(), datastore.getName(), layer.getName());
        assertTrue("FeatureType not removed:"
                + datastore.getWorkspaceName() + " : "
                + datastore.getName() + " / "
                + featureType.getName(),
                removed);

    }

    private void deleteCoverage(RESTLayer layer) {
        RESTCoverage coverage = reader.getCoverage(layer);
        RESTCoverageStore coverageStore = reader.getCoverageStore(coverage);

        LOGGER.warn("Deleting Coverage "
                + coverageStore.getWorkspaceName() + " : "
                + coverageStore.getName() + " / "
                + coverage.getName());

        boolean removed = publisher.unpublishCoverage(coverageStore.getWorkspaceName(),
                                                        coverageStore.getName(),
                                                        coverage.getName());
        assertTrue("Coverage not deleted "
                + coverageStore.getWorkspaceName() + " : "
                + coverageStore.getName() + " / "
                + coverage.getName(),
                removed);
    }
    
    protected boolean existsLayer(String layername) {
        return reader.getLayer(layername) != null;
    }

}
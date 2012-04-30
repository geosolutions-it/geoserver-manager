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
import it.geosolutions.geoserver.rest.decoder.RESTCoverageList;
import it.geosolutions.geoserver.rest.decoder.RESTCoverageStore;
import it.geosolutions.geoserver.rest.decoder.RESTCoverageStoreList;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.decoder.RESTDataStoreList;
import it.geosolutions.geoserver.rest.decoder.RESTFeatureType;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTLayerGroup;
import it.geosolutions.geoserver.rest.decoder.RESTLayerGroupList;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import it.geosolutions.geoserver.rest.decoder.RESTNamespace;
import it.geosolutions.geoserver.rest.decoder.RESTNamespaceList;
import it.geosolutions.geoserver.rest.decoder.RESTResource;
import it.geosolutions.geoserver.rest.decoder.RESTStyleList;
import it.geosolutions.geoserver.rest.decoder.RESTWorkspaceList;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




/**
 * Connect to a GeoServer instance to read its data.
 * <BR>Info are returned as <TT>Strings</TT> or, for complex data, as XML elements
 * wrapped in proper parsers (e.g.: {@link RESTLayer}, {@link RESTCoverageStore}, ...).
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeoServerRESTReader {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoServerRESTReader.class);
    private final String baseurl;
    private String username;
    private String password;

    /**
     * Creates a <TT>GeoServerRESTReader</TT> for a given GeoServer instance and
     * no auth credentials.
     * <P><B><I>Note that GeoServer 2.0 REST interface requires username/password credentials by
     * default, if not otherwise configured. </I></B>.
     *
     * @param restUrl the base GeoServer URL(e.g.: <TT>http://localhost:8080/geoserver</TT>)
     */
    public GeoServerRESTReader(URL restUrl) {
        String extForm = restUrl.toExternalForm();
        this.baseurl = extForm.endsWith("/") ?
                        extForm.substring(0, extForm.length()-1) :
                        extForm;
    }

    /**
     * Creates a <TT>GeoServerRESTReader</TT> for a given GeoServer instance and
     * no auth credentials.
     * <P><B><I>Note that GeoServer 2.0 REST interface requires username/password credentials by
     * default, if not otherwise configured. </I></B>.
     *
     * @param restUrl the base GeoServer URL (e.g.: <TT>http://localhost:8080/geoserver</TT>)
     */
    public GeoServerRESTReader(String restUrl)
            throws MalformedURLException {
        new URL(restUrl); // check URL correctness
        this.baseurl = restUrl.endsWith("/") ?
                        restUrl.substring(0, restUrl.length()-1) :
                        restUrl;
    }

    /**
     * Creates a <TT>GeoServerRESTReader</TT> for a given GeoServer instance
     * with the given auth credentials.
     *
     * @param restUrl the base GeoServer URL (e.g.: <TT>http://localhost:8080/geoserver</TT>)
     * @param username username auth credential
     * @param password password auth credential
     */
    public GeoServerRESTReader(String restUrl, String username, String password) throws MalformedURLException {
        this(restUrl);
        this.username = username;
        this.password = password;
    }

    /**
     * Creates a <TT>GeoServerRESTReader</TT> for a given GeoServer instance
     * with the given auth credentials.
     *
     * @param restUrl the base GeoServer URL (e.g.: <TT>http://localhost:8080/geoserver</TT>)
     * @param username username auth credential
     * @param password password auth credential
     */
    public GeoServerRESTReader(URL restUrl, String username, String password) {
        this(restUrl);
        this.username = username;
        this.password = password;
    }

    private String load(String url) {
        LOGGER.info("Loading from REST path " + url);
        try {
            String response = HTTPUtils.get(baseurl + url, username, password);
            return response;
        } catch (MalformedURLException ex) {
            LOGGER.warn("Bad URL", ex);
        } 

        return null;
    }

    private String loadFullURL(String url) {
        LOGGER.info("Loading from REST path " + url);
        try {
            String response = HTTPUtils.get(url, username, password);
            return response;
        } catch (MalformedURLException ex) {
            LOGGER.warn("Bad URL", ex);
        }
        return null;
    }

    /**
     * Check if a GeoServer instance is running at the given URL.
     * <BR>
     * Return <TT>true</TT> if the configured GeoServer is up and replies to REST requests.
     * <BR>
     * Send a HTTP GET request to the configured URL.<BR>
     * Return <TT>true</TT> if a HTTP 200 code (OK) is read from the HTTP response;
     * any other response code, or connection error, will return a
     * <TT>false</TT> boolean.
     *
     * @return true if a GeoServer instance was found at the configured URL.
     */
    public boolean existGeoserver() {
        return HTTPUtils.httpPing(baseurl + "/rest/", username, password);
    }

    //==========================================================================
    //=== STYLES
    //==========================================================================

    /**
     * Check if a Style exists in the configured GeoServer instance.
     * @param styleName the name of the style to check for.
     * @return <TT>true</TT> on HTTP 200, <TT>false</TT> on HTTP 404
     * @throws RuntimeException if any other HTTP code than 200 or 404 was retrieved.
     */
    public boolean existsStyle(String styleName) throws RuntimeException {
        String url = baseurl + "/rest/styles/" + styleName + ".xml";
        return HTTPUtils.exists(url, username, password);
    }

    /**
     * Get summary info about all Styles.
     *
     * @return summary info about Styles as a {@link RESTStyleList}
     */
    public RESTStyleList getStyles() {
        String url = "/rest/styles.xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving Styles list from " + url);
        }
        return RESTStyleList.build(load(url));
    }

    /**
     * Get the SLD body of a Style.
     */
    public String getSLD(String styleName) {
        String url = "/rest/styles/"+styleName+".sld";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving SLD body from " + url);
        }
        return load(url);
    }

    //==========================================================================
    //=== DATASTORES
    //==========================================================================

    /**
     * Get summary info about all DataStores in a WorkSpace.
     * 
     * @param workspace The name of the workspace
     * 
     * @return summary info about Datastores as a {@link RESTDataStoreList}
     */
    public RESTDataStoreList getDatastores(String workspace) {
        String url = "/rest/workspaces/" + workspace + "/datastores.xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving DS list from " + url);
        }
        return RESTDataStoreList.build(load(url));
    }

    /**
     * Get detailed info about a given Datastore in a given Workspace.
     *
     * @param workspace The name of the workspace
     * @param dsName The name of the Datastore
     * @return DataStore details as a {@link RESTDataStore}
     */
    public RESTDataStore getDatastore(String workspace, String dsName) {
        String url = "/rest/workspaces/" + workspace + "/datastores/" + dsName + ".xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving DS from " + url);
        }
        String response = load(url);
//        System.out.println("DATASTORE " + workspace+":"+dsName+"\n"+response);
        return RESTDataStore.build(response);
    }

    /**
     * Get detailed info about a FeatureType's Datastore.
     *
     * @param featureType the RESTFeatureType 
     * @return DataStore details as a {@link RESTDataStore}
     */
    public RESTDataStore getDatastore(RESTFeatureType featureType) {

        String url = featureType.getStoreUrl();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving DS from fullurl " + url);
        }
        String response = loadFullURL(url);
        return RESTDataStore.build(response);
    }

    //==========================================================================
    //=== FEATURETYPES
    //==========================================================================

    /**
     * Get detailed info about a FeatureType given the Layer where it's published with.
     *
     * @param layer A layer publishing the FeatureType
     * @return FeatureType details as a {@link RESTCoverage}
     */

    public RESTFeatureType getFeatureType(RESTLayer layer) {
        if(layer.getType() != RESTLayer.TYPE.VECTOR)
            throw new RuntimeException("Bad layer type for layer " + layer.getName());

        String response = loadFullURL(layer.getResourceUrl());
        return RESTFeatureType.build(response);
    }

    //==========================================================================
    //=== COVERAGESTORES
    //==========================================================================

    /**
     * Get summary info about all CoverageStores in a WorkSpace.
     *
     * @param workspace The name of the workspace
     *
     * @return summary info about CoverageStores as a {@link RESTDataStoreList}
     */
    public RESTCoverageStoreList getCoverageStores(String workspace) {
        String url = "/rest/workspaces/" + workspace + "/coveragestores.xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving CS list from " + url);
        }
        return RESTCoverageStoreList.build(load(url));
    }

    /**
     * Get detailed info about a given CoverageStore in a given Workspace.
     *
     * @param workspace The name of the workspace
     * @param csName The name of the CoverageStore
     * @return CoverageStore details as a {@link RESTCoverageStore}
     */
    public RESTCoverageStore getCoverageStore(String workspace, String csName) {
        String url = "/rest/workspaces/" + workspace + "/coveragestores/" + csName + ".xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving CS from " + url);
        }
        return RESTCoverageStore.build(load(url));
    }
    
    /**
     * Get detailed info about a Coverage's Datastore.
     *
     * @param coverage the RESTFeatureType
     * @return CoverageStore details as a {@link RESTCoverageStore}
     */
    public RESTCoverageStore getCoverageStore(RESTCoverage coverage) {

        String url = coverage.getStoreUrl();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving CS from fullurl " + url);
        }
        String response = loadFullURL(url);
        return RESTCoverageStore.build(response);
    }

    //==========================================================================
    //=== COVERAGES
    //==========================================================================

    /**
     * Get list of coverages (usually only one).
     *
     * @param workspace The name of the workspace
     * @param csName The name of the CoverageStore
     * @return Coverages list as a {@link RESTCoverageList}
     */
    public RESTCoverageList getCoverages(String workspace, String csName) {
            // restURL + "/rest/workspaces/" + workspace + "/coveragestores/" + coverageStore + "/coverages.xml";
        String url = "/rest/workspaces/" + workspace + "/coveragestores/" + csName + "/coverages.xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving Covs from " + url);
        }
        return RESTCoverageList.build(load(url));
    }
    
    /**
     * Get detailed info about a given Coverage.
     *
     * @param workspace The name of the workspace
     * @param store The name of the CoverageStore
     * @param name The name of the Coverage
     * @return Coverage details as a {@link RESTCoverage}
     */
    public RESTCoverage getCoverage(String workspace, String store, String name) {
        String url = "/rest/workspaces/" + workspace + "/coveragestores/" + store + "/coverages/"+name+".xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving Coverage from " + url);
        }
        return RESTCoverage.build(load(url));
    }

    /**
     * Get detailed info about a Coverage given the Layer where it's published with.
     *
     * @param layer A layer publishing the CoverageStore
     * @return Coverage details as a {@link RESTCoverage}
     */
    public RESTCoverage getCoverage(RESTLayer layer) {
        if(layer.getType() != RESTLayer.TYPE.RASTER)
            throw new RuntimeException("Bad layer type for layer " + layer.getName());

        String response = loadFullURL(layer.getResourceUrl());
        return RESTCoverage.build(response);
    }

    //==========================================================================
    //==========================================================================
    
    /**
     * Get detailed info about a Resource given the Layer where it's published with.
     * The Resource can then be converted to RESTCoverage or RESTFeatureType
     *
     * @return Resource details as a {@link RESTResource}
     */
    public RESTResource getResource(RESTLayer layer) {
        String response = loadFullURL(layer.getResourceUrl());
        return RESTResource.build(response);
    }

    //==========================================================================
    //=== LAYERGROUPS
    //==========================================================================

    /**
     * Get summary info about all LayerGroups.
     *
     * @return summary info about LayerGroups as a {@link RESTLayerGroupList}
     */
    public RESTLayerGroupList getLayerGroups() {
        String url = "/rest/layergroups.xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving layergroups from " + url);
        }
        return RESTLayerGroupList.build(load(url));
    }

    /**
     * Get detailed info about a given LayerGroup.
     *
     * @param name The name of the LayerGroup
     * @return LayerGroup details as a {@link RESTLayerGroup}
     */
    public RESTLayerGroup getLayerGroup(String name) {
        String url = "/rest/layergroups/" + name + ".xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving layergroup from " + url);
        }
        return RESTLayerGroup.build(load(url));
    }

    //==========================================================================
    //=== LAYERS
    //==========================================================================

    /**
     * Get summary info about all Layers.
     *
     * @return summary info about Layers as a {@link RESTLayerList}
     */
    public RESTLayerList getLayers() {
        String url = "/rest/layers.xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving layers from " + url);
        }
        return RESTLayerList.build(load(url));
    }

    /**
     * Get detailed info about a given Layer.
     *
     * @param name The name of the Layer
     * @return Layer details as a {@link RESTLayer}
     */
    public RESTLayer getLayer(String name) {
        String url = "/rest/layers/" + name + ".xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving layer from " + url);
        }
        return RESTLayer.build(load(url));
    }

    //==========================================================================
    //=== NAMESPACES
    //==========================================================================

    /**
     * Get a namespace.
     * 
     * @param prefix namespace prefix.
     * 
     * @return a RESTNamespace, or null if couldn't be created.
     */
    public RESTNamespace getNamespace(String prefix) {
		if (prefix == null || prefix.isEmpty()) {
			throw new IllegalArgumentException(
				"Namespace prefix cannot be null or empty");			
		}
    	String url = "/rest/namespaces/"+prefix+".xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Getting namespace from " + url);
        }
        return RESTNamespace.build(load(url));
    }
    
    /**
     * Get summary info about all Namespaces.
     *
     * @return summary info about Namespaces as a {@link RESTNamespaceList}
     */
    public RESTNamespaceList getNamespaces() {
        String url = "/rest/namespaces.xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving namespaces from " + url);
        }
        return RESTNamespaceList.build(load(url));
    }

    /**
     * Get the names of all the Namespaces.
     * <BR>
     * This is a shortcut call: These info could be retrieved using {@link #getNamespaces getNamespaces}
     * @return the list of the names of all Namespaces.
     */
    public List<String> getNamespaceNames() {
        RESTNamespaceList list = getNamespaces();
        List<String> names = new ArrayList<String>(list.size());
        for (RESTNamespaceList.RESTShortNamespace item : list) {
            names.add(item.getName());
        }
        return names;
    }

    //==========================================================================
    //=== WORKSPACES
    //==========================================================================

    /**
     * Get summary info about all Workspaces.
     *
     * @return summary info about Workspaces as a {@link RESTWorkspaceList}
     */
    public RESTWorkspaceList getWorkspaces() {
        String url = "/rest/workspaces.xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving workspaces from " + url);
        }
        
        return RESTWorkspaceList.build(load(url));
    }

    /**
     * Get the names of all the Workspaces.
     * <BR>
     * This is a shortcut call: These info could be retrieved using {@link #getWorkspaces getWorkspaces}
     * @return the list of the names of all Workspaces.
     */
    public List<String> getWorkspaceNames() {
        RESTWorkspaceList list = getWorkspaces();
        List<String> names = new ArrayList<String>(list.size());
        for (RESTWorkspaceList.RESTShortWorkspace item : list) {
            names.add(item.getName());
        }
        return names;
    }

}

/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2013 GeoSolutions S.A.S.
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
import it.geosolutions.geoserver.rest.decoder.RESTFeatureTypeList;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.decoder.RESTLayer21;
import it.geosolutions.geoserver.rest.decoder.RESTLayerGroup;
import it.geosolutions.geoserver.rest.decoder.RESTLayerGroupList;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import it.geosolutions.geoserver.rest.decoder.RESTNamespace;
import it.geosolutions.geoserver.rest.decoder.RESTNamespaceList;
import it.geosolutions.geoserver.rest.decoder.RESTResource;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageGranulesList;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageIndexSchema;
import it.geosolutions.geoserver.rest.decoder.RESTStyle;
import it.geosolutions.geoserver.rest.decoder.RESTStyleList;
import it.geosolutions.geoserver.rest.decoder.RESTWms;
import it.geosolutions.geoserver.rest.decoder.RESTWmsList;
import it.geosolutions.geoserver.rest.decoder.RESTWmsStore;
import it.geosolutions.geoserver.rest.decoder.RESTWmsStoreList;
import it.geosolutions.geoserver.rest.decoder.RESTWorkspaceList;
import it.geosolutions.geoserver.rest.decoder.about.GSVersionDecoder;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStructuredGridCoverageReaderManager;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStyleManager;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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

    private GeoServerRESTStyleManager styleManager;

    /**
     * Creates a <TT>GeoServerRESTReader</TT> for a given GeoServer instance and
     * no auth credentials.
     * <P><B><I>Note that GeoServer 2.0 REST interface requires username/password credentials by
     * default, if not otherwise configured. </I></B>.
     *
     * @param gsUrl the base GeoServer URL(e.g.: <TT>http://localhost:8080/geoserver</TT>)
     */
    public GeoServerRESTReader(URL gsUrl) {
        baseurl = init(gsUrl, null, null);
    }

    /**
     * Creates a <TT>GeoServerRESTReader</TT> for a given GeoServer instance and
     * no auth credentials.
     * <P><B><I>Note that GeoServer 2.0 REST interface requires username/password credentials by
     * default, if not otherwise configured. </I></B>.
     *
     * @param gsUrl the base GeoServer URL (e.g.: <TT>http://localhost:8080/geoserver</TT>)
     */
    public GeoServerRESTReader(String gsUrl) throws MalformedURLException {
        baseurl = init(gsUrl, null, null);
    }

    /**
     * Creates a <TT>GeoServerRESTReader</TT> for a given GeoServer instance
     * with the given auth credentials.
     *
     * @param gsUrl the base GeoServer URL (e.g.: <TT>http://localhost:8080/geoserver</TT>)
     * @param username username auth credential
     * @param password password auth credential
     */
    public GeoServerRESTReader(String gsUrl, String username, String password) throws MalformedURLException {
        baseurl = init(gsUrl, username, password);
    }

    /**
     * Creates a <TT>GeoServerRESTReader</TT> for a given GeoServer instance
     * with the given auth credentials.
     *
     * @param gsUrl the base GeoServer URL (e.g.: <TT>http://localhost:8080/geoserver</TT>)
     * @param username username auth credential
     * @param password password auth credential
     */
    public GeoServerRESTReader(URL gsUrl, String username, String password) {
        baseurl = init(gsUrl, username, password);
    }

    private String init(String gsUrl, String username, String password) throws MalformedURLException {
        return init(new URL(gsUrl), username, password);
    }

    private String init(URL gsUrl, String username, String password) {
        String restUrl = gsUrl.toExternalForm();
        String cleanUrl = restUrl.endsWith("/") ?
                            restUrl.substring(0, restUrl.length()-1) :
                            restUrl;
        this.username = username;
        this.password = password;

        styleManager = new GeoServerRESTStyleManager(gsUrl, username, password);

        return cleanUrl;
    }

    private String load(String url) {
        LOGGER.info("Loading from REST path " + url);
        String response = HTTPUtils.get(baseurl + url, username, password);
        return response;
    }

    private String loadFullURL(String url) {
        LOGGER.info("Loading from REST path " + url);
        String response = HTTPUtils.get(url, username, password);
        return response;
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
    
    /**
     * Return the version of the target GeoServer
     */
    public GSVersionDecoder getGeoserverVersion() {
        final String url = "/rest/about/version.xml";
        String xml = load(url);
        if (xml == null) {
            GSVersionDecoder v = new GSVersionDecoder();
            v.getGeoServer().setVersion(GSVersionDecoder.VERSION.UNRECOGNIZED.toString());
            return v;
        } else {
            return GSVersionDecoder.build(load(url));
        }
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
        return styleManager.existsStyle(styleName);
    }
    
    /**
     * Check if a Style exists in the configured GeoServer instance.
     * @param styleName the name of the style to check for.
     * @param quietOnNotFound if true, mute exception if false is returned
     * @return <TT>true</TT> on HTTP 200, <TT>false</TT> on HTTP 404
     * @throws RuntimeException if any other HTTP code than 200 or 404 was retrieved.
     */
    public boolean existsStyle(String styleName, boolean quietOnNotFound) throws RuntimeException {
        return styleManager.existsStyle(styleName, quietOnNotFound);
    }

    /**
     * @see GeoServerRESTStyleManager#existsStyle(java.lang.String, java.lang.String) 
     * @since GeoServer 2.2
     */
    public boolean existsStyle(String workspace, String styleName) throws RuntimeException {
        return styleManager.existsStyle(workspace, styleName);
    }

    /**
     * @see GeoServerRESTStyleManager#getStyle(java.lang.String)
     * @since GeoServer 2.2
     */
    public RESTStyle getStyle(String name) {
        return styleManager.getStyle(name);
    }

    /**
     * @see GeoServerRESTStyleManager#getStyle(java.lang.String, java.lang.String)
     * @since GeoServer 2.2
     */
    public RESTStyle getStyle(String workspace, String name) {
        return styleManager.getStyle(workspace, name);
    }

    /**
     * Get summary info about all Styles.
     *
     * @return summary info about Styles as a {@link RESTStyleList}
     */
    public RESTStyleList getStyles() {
        return styleManager.getStyles();
    }

    /**
     * @see GeoServerRESTStyleManager#getStyles(java.lang.String)
     * @since GeoServer 2.2
     */
    public RESTStyleList getStyles(String workspace) {
        return styleManager.getStyles(workspace);
    }

    /**
     * Get the SLD body of a Style.
     */
    public String getSLD(String styleName) {
        return styleManager.getSLD(styleName);
    }

    /**
     * @see GeoServerRESTStyleManager#getSLD(java.lang.String, java.lang.String) 
     * @since GeoServer 2.2
     */
    public String getSLD(String workspace, String styleName) {
        return styleManager.getSLD(workspace, styleName);
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
    
    /**
     * Checks if the selected DataStore is present
     * 
     * @param workspace workspace of the datastore
     * @param dsName name of the datastore
     * @return boolean indicating if the datastore exists
     */
    public boolean existsDatastore(String workspace, String dsName){
        return existsDatastore(workspace, dsName, Util.DEFAULT_QUIET_ON_NOT_FOUND);
    }
    
    /**
     * Checks if the selected DataStore is present. Parameter quietOnNotFound can be used for controlling the logging when 404 is returned.
     * 
     * @param workspace workspace of the datastore
     * @param dsName name of the datastore
     * @param quietOnNotFound if true, no exception is logged
     * @return boolean indicating if the datastore exists
     */
    public boolean existsDatastore(String workspace, String dsName, boolean quietOnNotFound){
        String url = baseurl + "/rest/workspaces/" + workspace + "/datastores/" + dsName + ".xml";
        String composed = Util.appendQuietOnNotFound(quietOnNotFound, url);
        return HTTPUtils.exists(composed, username, password);
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
        if(layer.getType() != RESTLayer.Type.VECTOR)
            throw new RuntimeException("Bad layer type for layer " + layer.getName());

        String response = loadFullURL(layer.getResourceUrl());
        return RESTFeatureType.build(response);
    }

    /**
     * Checks if the selected FeatureType is present.
     * 
     * @param workspace workspace of the datastore
     * @param dsName name of the datastore
     * @param ftName name of the featuretype
     * @return boolean indicating if the featuretype exists
     */
    public boolean existsFeatureType(String workspace, String dsName, String ftName){
        return existsFeatureType(workspace, dsName, ftName, Util.DEFAULT_QUIET_ON_NOT_FOUND);
    }

    /**
     * Checks if the selected FeatureType is present. Parameter quietOnNotFound can be used for controlling the logging when 404 is returned.
     * 
     * @param workspace workspace of the datastore
     * @param dsName name of the datastore
     * @param ftName name of the featuretype
     * @param quietOnNotFound if true, no exception is logged
     * @return boolean indicating if the featuretype exists
     */
    public boolean existsFeatureType(String workspace, String dsName, String ftName, boolean quietOnNotFound){
        String url = baseurl + "/rest/workspaces/" + workspace + "/datastores/" + dsName + "/featuretypes/" + ftName +".xml";
        String composed = Util.appendQuietOnNotFound(quietOnNotFound, url);
        return HTTPUtils.exists(composed, username, password);
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
    
    /**
     * Checks if the selected Coverage store is present. Parameter quietOnNotFound can be used for controlling the logging when 404 is returned.
     * 
     * @param workspace workspace of the coveragestore
     * @param dsName name of the coveragestore
     * @param quietOnNotFound if true, no exception is logged
     * @return boolean indicating if the coveragestore exists
     */
    public boolean existsCoveragestore(String workspace, String csName, boolean quietOnNotFound){
        String url = baseurl + "/rest/workspaces/" + workspace + "/coveragestores/" + csName + ".xml";
        String composed = Util.appendQuietOnNotFound(quietOnNotFound, url);
        return HTTPUtils.exists(composed, username, password);
    }

    /**
     * Checks if the selected Coverage store is present.
     * 
     * @param workspace workspace of the coveragestore
     * @param dsName name of the coveragestore
     * @return boolean indicating if the coveragestore exists
     */
    public boolean existsCoveragestore(String workspace, String csName){
        return existsCoveragestore(workspace, csName, Util.DEFAULT_QUIET_ON_NOT_FOUND);
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
     * Checks if the selected Coverage is present. Parameter quietOnNotFound can be used for controlling the logging when 404 is returned.
     * 
     * @param workspace workspace of the coveragestore
     * @param dsName name of the coveragestore
     * @param name name of the coverage
     * @param quietOnNotFound if true, no exception is logged
     * @return boolean indicating if the coverage exists
     */
    public boolean existsCoverage(String workspace, String store, String name, boolean quietOnNotFound){
        String url = baseurl + "/rest/workspaces/" + workspace + "/coveragestores/" + store + "/coverages/"+name+".xml";
        String composed = Util.appendQuietOnNotFound(quietOnNotFound, url);
        return HTTPUtils.exists(composed, username, password);
    }

    /**
     * Checks if the selected Coverage is present.
     * 
     * @param workspace workspace of the coveragestore
     * @param store name of the coveragestore
     * @param name name of the coverage
     * @return boolean indicating if the coverage exists
     */
    public boolean existsCoverage(String workspace, String store, String name){
        return existsCoverage(workspace, store, name, Util.DEFAULT_QUIET_ON_NOT_FOUND);
    }

    /**
     * Get detailed info about a Coverage given the Layer where it's published with.
     *
     * @param layer A layer publishing the CoverageStore
     * @return Coverage details as a {@link RESTCoverage}
     */
    public RESTCoverage getCoverage(RESTLayer layer) {
        if(layer.getType() != RESTLayer.Type.RASTER)
            throw new RuntimeException("Bad layer type for layer " + layer.getName());

        String response = loadFullURL(layer.getResourceUrl());
        return RESTCoverage.build(response);
    }

    //==========================================================================
    //=== WMSSTORES
    //==========================================================================
    
    /**
     * Get summary info about all WmsStore in a WorkSpace.
     *
     * @param workspace The name of the workspace
     *
     * @return summary info about CoverageStores as a {@link RESTWmsStoreList}
     */
    public RESTWmsStoreList getWmsStores(String workspace) {
        String url = "/rest/workspaces/" + workspace + "/wmsstores.xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving CS list from " + url);
        }
        return RESTWmsStoreList.build(load(url));
    }
    
    /**
     * Get detailed info about a given WmsStore in a given Workspace.
     *
     * @param workspace The name of the workspace
     * @param wsName The name of the WmsStore
     * @return WmsStore details as a {@link RESTWmsStore}
     */
    public RESTWmsStore getWmsStore(String workspace, String wsName) {
        String url = "/rest/workspaces/" + workspace + "/wmsstores/" + wsName + ".xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving CS from " + url);
        }
        return RESTWmsStore.build(load(url));
    }
    
    /**
     * Get detailed info about a Wms's Datastore.
     *
     * @param wms the RESTWms
     * @return wmsStore details as a {@link RESTWmsStore}
     */
    public RESTWmsStore getWmsStore(RESTWms wms) {
        String url = wms.getStoreUrl();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving CS from fullurl " + url);
        }
        String response = loadFullURL(url);
        return RESTWmsStore.build(response);
    }

    /**
     * Checks if the selected Wms store is present. Parameter quietOnNotFound can be used for controlling the logging when 404 is returned.
     * 
     * @param workspace workspace of the wmsstore
     * @param wsName name of the wmsstore
     * @param quietOnNotFound if true, no exception is logged
     * @return boolean indicating if the wmsstore exists
     */
    public boolean existsWmsstore(String workspace, String wsName, boolean quietOnNotFound){
        String url = baseurl + "/rest/workspaces/" + workspace + "/wmsstores/" + wsName + ".xml";
        String composed = Util.appendQuietOnNotFound(quietOnNotFound, url);
        return HTTPUtils.exists(composed, username, password);
    }

    /**
     * Checks if the selected wms store is present.
     * 
     * @param workspace workspace of the wmsstore
     * @param wsName name of the wmsstore
     * @return boolean indicating if the wmsstore exists
     */
    public boolean existsWmsstore(String workspace, String wsName){
        return existsWmsstore(workspace, wsName, Util.DEFAULT_QUIET_ON_NOT_FOUND);
    }
    
    //==========================================================================
    //=== WMSS
    //==========================================================================
 
    /**
     * Get list of wmss (usually only one).
     *
     * @param workspace The name of the workspace
     * @param wsName The name of the WmsStore
     * @return wms list as a {@link RESTWmsList}
     */
    public RESTWmsList getWms(String workspace, String wsName) {
        String url = "/rest/workspaces/" + workspace + "/wmsstores/" + wsName + "/wmslayers.xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving Wmss from " + url);
        }
        return RESTWmsList.build(load(url));
    }
    
    /**
     * Get detailed info about a given Wms.
     *
     * @param workspace The name of the workspace
     * @param store The name of the WmsStore
     * @param name The name of the Wms
     * @return wms details as a {@link RESTwms}
     */
    public RESTWms getWms(String workspace, String store, String name) {
        String url = "/rest/workspaces/" + workspace + "/wmsstores/" + store + "/wmslayers/"+name+".xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving Wmss from " + url);
        }
        return RESTWms.build(load(url));
    }
    
    /**
     * Checks if the selected Wms is present. Parameter quietOnNotFound can be used for controlling the logging when 404 is returned.
     * 
     * @param workspace workspace of the wmsstore
     * @param wsName name of the wmsstore
     * @param name name of the wms
     * @param quietOnNotFound if true, no exception is logged
     * @return boolean indicating if the coverage exists
     */
    public boolean existsWms(String workspace, String store, String name, boolean quietOnNotFound){
        String url = baseurl + "/rest/workspaces/" + workspace + "/wmsstores/" + store + "/wmslayers/"+name+".xml";
        String composed = Util.appendQuietOnNotFound(quietOnNotFound, url);
        return HTTPUtils.exists(composed, username, password);
    }
    
    /**
     * Checks if the selected wms is present.
     * 
     * @param workspace workspace of the wmsstore
     * @param store name of the wmsstore
     * @param name name of the wms
     * @return boolean indicating if the coverage exists
     */
    public boolean existsWms(String workspace, String store, String name){
        return existsWms(workspace, store, name, Util.DEFAULT_QUIET_ON_NOT_FOUND);
    }
    
    /**
     * Get detailed info about a Wms given the Layer where it's published with.
     *
     * @param layer A layer publishing the wmsStore
     * @return Wms details as a {@link RESTWms}
     */
    public RESTWms getWms(RESTLayer layer) {
        String response = loadFullURL(layer.getResourceUrl());
        return RESTWms.build(response);
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
     * Get summary info about all LayerGroups in the given workspace.
     *
     * @param workspace name of the workspace
     * @return summary info about LayerGroups as a {@link RESTLayerGroupList}
     */
    public RESTLayerGroupList getLayerGroups(String workspace) {
        String url;
        if (workspace == null) {
            url = "/rest/layergroups.xml";
        } else {
            url = "/rest/workspaces/" + workspace + "/layergroups.xml";
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving layergroups from " + url);
        }
        return RESTLayerGroupList.build(load(url));
    }

    /**
     * Get detailed info about a given LayerGroup.
     *
     * @param workspace name of the workspace
     * @param name the name of the LayerGroup
     * @return LayerGroup details as a {@link RESTLayerGroup}
     */
    public RESTLayerGroup getLayerGroup(String workspace, String name) {
        String url;
        if (workspace == null) {
            url = "/rest/layergroups/" + name + ".xml";
        } else {
            url = "/rest/workspaces/" + workspace + "/layergroups/" + name + ".xml";
        }        
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving layergroup from " + url);
        }
        return RESTLayerGroup.build(load(url));
    }
    
    /**
     * Get summary info about all LayerGroups.
     *
     * @return summary info about LayerGroups as a {@link RESTLayerGroupList}
     */
    public RESTLayerGroupList getLayerGroups() {
        return getLayerGroups(null);
    }

    /**
     * Get detailed info about a given LayerGroup.
     *
     * @param name The name of the LayerGroup
     * @return LayerGroup details as a {@link RESTLayerGroup}
     */
    public RESTLayerGroup getLayerGroup(String name) {
        return getLayerGroup(null, name);
    }

    /**
     * Checks if the selected LayerGroup is present. Parameter quietOnNotFound can be used for controlling the logging when 404 is returned.
     * 
     * @param workspace workspace of the LayerGroup
     * @param name name of the LayerGroup
     * @param quietOnNotFound if true, no exception is logged
     * @return boolean indicating if the LayerGroup exists
     */
    public boolean existsLayerGroup(String workspace, String name, boolean quietOnNotFound){
        String url;
        if (workspace == null) {
            url = baseurl + "/rest/layergroups/" + name + ".xml";
        } else {
            url = baseurl + "/rest/workspaces/" + workspace + "/layergroups/" + name + ".xml";
        }  
        String composed = Util.appendQuietOnNotFound(quietOnNotFound, url);
        return HTTPUtils.exists(composed, username, password);
    }

    /**
     * Checks if the selected LayerGroup is present.
     * 
     * @param workspace workspace of the LayerGroup
     * @param name name of the LayerGroup
     * @return boolean indicating if the LayerGroup exists
     */
    public boolean existsLayerGroup(String workspace, String name){
        return existsLayerGroup(workspace, name, Util.DEFAULT_QUIET_ON_NOT_FOUND);
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
     * Get summary info about all FeatureTypes of a workspace.
     *
     * @return summary info about Layers as a {@link RESTLayerList}
     */
    public RESTFeatureTypeList getFeatureTypes(String workspace) {
        String url = "/rest/workspaces/" + workspace + "/featuretypes.xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving featuretypes from " + url);
        }
        return RESTFeatureTypeList.build(load(url));
    }

    /**
     * Get detailed info about a given Layer.
     * 
     * @deprecated use {@link #getLayer(String, String)}
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

    /**
     * Get detailed info about a given Layer.
     * 
     * @param workspace the workspace name
     * @param name the layer name
     * @return a RESTLayer with layer information or null
     */
    public RESTLayer getLayer(String workspace, String name) {
        if (workspace == null || workspace.isEmpty())
            throw new IllegalArgumentException("Workspace may not be null");
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Layername may not be null");
        String url = HTTPUtils.append("/rest/layers/",workspace,":",name,".xml").toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving layer from " + url);
        }
        
		RESTLayer layer = null;
		if (this.getGeoserverVersion().getVersion()
				.equals(GSVersionDecoder.VERSION.UNRECOGNIZED)) {
			layer = RESTLayer21.build(load(url));
		} else {
			layer = RESTLayer.build(load(url));
		}
		return layer;
    }
    
    /**
     * Checks if the selected Layer is present. Parameter quietOnNotFound can be used for controlling the logging when 404 is returned.
     * 
     * @param workspace workspace of the Layer
     * @param name name of the Layer
     * @param quietOnNotFound if true, no exception is logged
     * @return boolean indicating if the Layer exists
     */
    public boolean existsLayer(String workspace, String name, boolean quietOnNotFound){
        String url;
        if (workspace == null) {
            url = baseurl + "/rest/layers/" + name + ".xml";
        } else {
            url = baseurl + "/rest/layers/" + workspace + ":" + name + ".xml";
        }  
        String composed = Util.appendQuietOnNotFound(quietOnNotFound, url);
        return HTTPUtils.exists(composed, username, password);
    }

    /**
     * Checks if the selected Layer is present.
     * 
     * @param workspace workspace of the Layer
     * @param name name of the Layer
     * @return boolean indicating if the Layer exists
     */
    public boolean existsLayer(String workspace, String name){
        return existsLayerGroup(workspace, name, Util.DEFAULT_QUIET_ON_NOT_FOUND);
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

    /**
     * Checks if the selected Namespace is present. Parameter quietOnNotFound can be used for controlling the logging when 404 is returned.
     * 
     * @param prefix namespace prefix.
     * @param quietOnNotFound if true, no exception is logged
     * @return boolean indicating if the Namespace exists
     */
    public boolean existsNamespace(String prefix, boolean quietOnNotFound) {
        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("Namespace prefix cannot be null or empty");
        }
        String url = baseurl + "/rest/namespaces/" + prefix + ".xml";
        String composed = Util.appendQuietOnNotFound(quietOnNotFound, url);
        return HTTPUtils.exists(composed, username, password);
    }

    /**
     * Checks if the selected Namespace is present.
     * 
     * @param prefix namespace prefix.
     * @return boolean indicating if the Namespace exists
     */
    public boolean existsNamespace(String prefix){
        return existsNamespace(prefix, Util.DEFAULT_QUIET_ON_NOT_FOUND);
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
     * @return the list of the names of all Workspaces or an empty list.
     */
    public List<String> getWorkspaceNames() {
        RESTWorkspaceList list = getWorkspaces();
        if(list==null){
        	return Collections.emptyList();
        }
        List<String> names = new ArrayList<String>(list.size());
        for (RESTWorkspaceList.RESTShortWorkspace item : list) {
            names.add(item.getName());
        }
        return names;
    }
    
    /**
     * Checks if the selected Workspace is present. Parameter quietOnNotFound can be used for controlling the logging when 404 is returned.
     * 
     * @param prefix Workspace prefix.
     * @param quietOnNotFound if true, no exception is logged
     * @return boolean indicating if the Workspace exists
     */
    public boolean existsWorkspace(String prefix, boolean quietOnNotFound) {
        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("Workspace prefix cannot be null or empty");
        }
        String url = baseurl + "/rest/workspaces/" + prefix + ".xml";
        String composed = Util.appendQuietOnNotFound(quietOnNotFound, url);
        return HTTPUtils.exists(composed, username, password);
    }

    /**
     * Checks if the selected Workspace is present.
     * 
     * @param prefix Workspace prefix.
     * @return boolean indicating if the Workspace exists
     */
    public boolean existsWorkspace(String prefix){
        return existsWorkspace(prefix, Util.DEFAULT_QUIET_ON_NOT_FOUND);
    }

    //==========================================================================
    //=== Structured Coverages
    //==========================================================================    
    
    /**
     * Get information about a granule for a structured coverage.
     * 
     * @param workspace the GeoServer workspace
     * @param coverageStore the GeoServer coverageStore
     * @param format the format of the file to upload
     * @param the absolute path to the file to upload
     * @param id the ID of the granule to get information for
     * 
     * @return <code>null</code> in case the call does not succeed, or an instance of {@link RESTStructuredCoverageGranulesList}.
     * 
     * @throws MalformedURLException
     * @throws UnsupportedEncodingException
     */
    public RESTStructuredCoverageGranulesList getGranuleById(final String workspace,
            String coverageStore, String coverage, String id) throws MalformedURLException,
            UnsupportedEncodingException {
        try {
            GeoServerRESTStructuredGridCoverageReaderManager manager = 
                new GeoServerRESTStructuredGridCoverageReaderManager(new URL(baseurl), username, password);
            return manager.getGranuleById(workspace, coverageStore, coverage, id);
        } catch (IllegalArgumentException e) {
            if(LOGGER.isInfoEnabled()){
                LOGGER.info(e.getLocalizedMessage(),e);
            }
        } catch (MalformedURLException e) {
            if(LOGGER.isInfoEnabled()){
                LOGGER.info(e.getLocalizedMessage(),e);
            }
        }
        return null;
    }
    
    /**
     * Checks if the selected Granule is present. Parameter quietOnNotFound can be used for controlling the logging when 404 is returned.
     * 
     * @param workspace workspace of the coveragestore
     * @param coverageStore name of the coveragestore
     * @param coverage name of the coverage
     * @param id id of the granule
     * @param quietOnNotFound if true, no exception is logged
     * @return boolean indicating if the Granule exists
     */
    public boolean existsGranule(String workspace, String coverageStore, String coverage,
            String id, boolean quietOnNotFound) {
        String url = baseurl + "/rest/workspaces/" + workspace + "/coveragestores/" + coverageStore
                + "/coverages/" + coverage + "/index/granules/" + id + ".xml";
        String composed = Util.appendQuietOnNotFound(quietOnNotFound, url);
        return HTTPUtils.exists(composed, username, password);
    }

    /**
     * Checks if the selected Granule is present.
     * 
     * @param workspace workspace of the coveragestore
     * @param coverageStore name of the coveragestore
     * @param coverage name of the coverage
     * @param id id of the granule
     * @return boolean indicating if the Granule exists
     */
    public boolean existsGranule(String workspace, String coverageStore, String coverage, String id) {
        return existsGranule(workspace, coverageStore, coverage, id,
                Util.DEFAULT_QUIET_ON_NOT_FOUND);
    }

    /**
     * Get information about the schema of the index for a structured coverage.
     * 
     * @param workspace the GeoServer workspace
     * @param coverageStore the GeoServer coverageStore
     * @param format the format of the file to upload
     * 
     * @return <code>null</code> in case the call does not succeed, or an instance of {@link RESTStructuredCoverageGranulesList}.
     * 
     * @throws MalformedURLException
     * @throws UnsupportedEncodingException
     */
     public RESTStructuredCoverageIndexSchema getGranuleIndexSchema(final String workspace, String coverageStore, String coverage) throws MalformedURLException {
         try {
             GeoServerRESTStructuredGridCoverageReaderManager manager = 
                 new GeoServerRESTStructuredGridCoverageReaderManager(new URL(baseurl), username, password);
             return manager.getGranuleIndexSchema(workspace, coverageStore, coverage);
         } catch (IllegalArgumentException e) {
             if(LOGGER.isInfoEnabled()){
                 LOGGER.info(e.getLocalizedMessage(),e);
             }
         } catch (MalformedURLException e) {
             if(LOGGER.isInfoEnabled()){
                 LOGGER.info(e.getLocalizedMessage(),e);
             }
         }
         return null;
     }

    /**
      * Get information about the granules for a coverage with optional filter and paging.
      * 
      * @param workspace the GeoServer workspace
      * @param coverageStore the GeoServer coverageStore
      * @param coverage the name of the target coverage
      * @param filter the format of the file to upload, can be <code>null</code> to include all the granules
      * @param offset the start page, can be <code>null</code> or an integer
      * @param limit the dimension of the page, can be <code>null</code> or a positive integer
      * 
      * @return <code>null</code> in case the call does not succeed, or an instance of {@link RESTStructuredCoverageGranulesList}.
      * 
      * @throws MalformedURLException
      * @throws UnsupportedEncodingException
      */
    public RESTStructuredCoverageGranulesList getGranules(final String workspace, String coverageStore, String coverage, String filter, Integer offset, Integer limit)
             throws MalformedURLException, UnsupportedEncodingException {
         try {
             GeoServerRESTStructuredGridCoverageReaderManager manager = 
                 new GeoServerRESTStructuredGridCoverageReaderManager(new URL(baseurl), username, password);
             return manager.getGranules(workspace, coverageStore, coverage, filter, offset, limit);
         } catch (IllegalArgumentException e) {
             if(LOGGER.isInfoEnabled()){
                 LOGGER.info(e.getLocalizedMessage(),e);
             }
         } catch (MalformedURLException e) {
             if(LOGGER.isInfoEnabled()){
                 LOGGER.info(e.getLocalizedMessage(),e);
             }
         }
         return null;
     }

}

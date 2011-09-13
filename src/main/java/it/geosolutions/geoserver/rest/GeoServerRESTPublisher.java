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

import it.geosolutions.geoserver.rest.decoder.RESTCoverageList;
import it.geosolutions.geoserver.rest.decoder.RESTCoverageStore;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSPostGISDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.GSWorkspaceEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.geosolutions.geoserver.rest.encoder.coverage.GSCoverageEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

/**
 * Connect to a GeoServer instance to publish or modify data.
 * <P>
 * There are no modifiable instance fields, so all the calls are thread-safe.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeoServerRESTPublisher {

    private static final Logger LOGGER = Logger.getLogger(GeoServerRESTPublisher.class);
    private final String restURL;
    private final String gsuser;
    private final String gspass;

    /**
     * Creates a <TT>GeoServerRESTPublisher</TT> for a given GeoServer instance
     * with the given auth credentials.
     *
     * @param restURL the base GeoServer URL (e.g.: <TT>http://localhost:8080/geoserver</TT>)
     * @param username username auth credential
     * @param password password auth credential
     */
    public GeoServerRESTPublisher(String restURL, String username, String password) {
        this.restURL = restURL;
        this.gsuser = username;
        this.gspass = password;
    }

    //==========================================================================
    //=== WORKSPACES
    //==========================================================================

    /**
     * Create a new Workspace
     *
     * @param workspace The name of the new workspace.
     *
     *  <P>
     *  This is the equivalent call with cUrl:
     * <PRE>{@code curl -u admin:geoserver -XPOST \
     *      -H 'Content-type: text/xml' \
     *      -d "<workspace><name>$WORKSPACE</name></workspace>" \
     *      http://$GSIP:$GSPORT/$SERVLET/rest/workspaces
     * }</PRE>
     */
    public boolean createWorkspace(final String workspace) {
    	final String sUrl = restURL + "/rest/workspaces";
        final GSWorkspaceEncoder wsenc = new GSWorkspaceEncoder(workspace);
        final String wsxml = wsenc.toString();
        final String result = HTTPUtils.postXml(sUrl, wsxml, gsuser, gspass);
        return result != null;
    }

    //==========================================================================
    //=== STYLES
    //==========================================================================

    /**
     *  Store and publish an SLD.
     *  <P>
     *  This is the equivalent call with cUrl:
     * <PRE>
     * {@code curl -u admin:geoserver -XPOST \
     *      -H 'Content-type: application/vnd.ogc.sld+xml' \
     *      -d @$FULLSLD \
     *      http://$GSIP:$GSPORT/$SERVLET/rest/styles}</PRE>
     *
     * @param sldBody the SLD document as an XML String.
     *
     * @return <TT>true</TT> if the operation completed successfully.
     */
    public boolean publishStyle(String sldBody) {
        String sUrl = restURL + "/rest/styles";
        String result = HTTPUtils.post(sUrl, sldBody, "application/vnd.ogc.sld+xml", gsuser, gspass);
        return result != null;
    }

    /**
     * Store and publish an SLD.
     *
     * @param sldFile the File containing the SLD document.
     *
     * @return <TT>true</TT> if the operation completed successfully.
     */
    public boolean publishStyle(File sldFile) {
        return publishStyle(sldFile, null);
    }

    /**
     * Store and publish an SLD, assigning it a name.
     *
     * @param sldFile the File containing the SLD document.
     * @param name    the Style name.
     *
     * @return <TT>true</TT> if the operation completed successfully.
     */
    public boolean publishStyle(File sldFile, String name) {
        String sUrl = restURL + "/rest/styles";
        if (name != null) {
            sUrl += "?name=" + encode(name);
        }
        LOGGER.debug("POSTing new style " + name + " to " + sUrl);
        String result = HTTPUtils.post(sUrl, sldFile, "application/vnd.ogc.sld+xml", gsuser, gspass);
        return result != null;
    }

    /**
     * Remove a Style.
     * <P>
     * The Style will be unpublished and the related SLD file will be removed.
     *
     * @param styleName the name of the Style to remove.
     *
     * @return <TT>true</TT> if the operation completed successfully.
     */
    public boolean removeStyle(String styleName) {
        styleName = styleName.replaceAll(":", "_"); // ???
        styleName = encode(styleName); // spaces may
        String sUrl = restURL + "/rest/styles/" + styleName + "?purge=true";
        return HTTPUtils.delete(sUrl, gsuser, gspass);
    }

    //==========================================================================
    //=== DATASTORE
    //==========================================================================

    /**
     * Create a PostGIS datastore.
     *
     * @param workspace Name of the workspace to contain the database. This will also be the prefix of any layer names created from tables in the database.
     * @param datastoreEncoder the set of parameters to be set to the datastore (including connection params)
     *
     * @return <TT>true</TT> if the PostGIS datastore has been successfully created, <TT>false</TT> otherwise
     */
    public boolean createPostGISDatastore(String workspace, GSPostGISDatastoreEncoder datastoreEncoder) {          
        String sUrl = restURL + "/rest/workspaces/" + workspace + "/datastores/";
        String xml = datastoreEncoder.toString();
        String result = HTTPUtils.postXml(sUrl, xml, gsuser, gspass);
        return result != null;
    }

    //==========================================================================
    //=== SHAPEFILES
    //==========================================================================

    /**
     * Publish a zipped shapefile.
     * <BR>The CRS will be forced to EPSG:4326.
     *
     * @param workspace
     * @param storename
     * @param layername
     * @param zipFile
     * @return true if the operation completed successfully.
     * @throws FileNotFoundException
     */
    public boolean publishShp(String workspace, String storename, String layername, File zipFile) throws FileNotFoundException {
        return publishShp(workspace, storename, layername, zipFile, "EPSG:4326");
    }

    /**
     * Publish a zipped shapefile.
     *
     * @param workspace
     * @param storename
     * @param layerName
     * @param nativeCrs
     * @param defaultStyle may be null
     * @return true if the operation completed successfully.
     * @throws FileNotFoundException
     */
    public boolean publishShp(String workspace, String storename, String layerName, File zipFile, String nativeCrs, String defaultStyle) throws FileNotFoundException {
        boolean sent = publishShp(workspace, storename, layerName, zipFile, nativeCrs);
        if (sent) {

            try {
                GSLayerEncoder layerEncoder = new GSLayerEncoder();
                layerEncoder.addDefaultStyle(defaultStyle);
                configureLayer(layerEncoder, layerName);
            } catch (Exception e) {
                LOGGER.warn("Error in publishing shapefile " + e.getMessage(), e);
                sent = false;
            }
        }

        return sent;
    }

    /**
     * Publish a zipped shapefile.
     *
     * <P>These are the equivalent calls with cUrl:
     * <PRE>{@code
     *curl -u admin:geoserver -XPUT -H 'Content-type: application/zip' \
     *      --data-binary @$ZIPFILE \
     *      http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/datastores/$STORENAME/file.shp
     *
     *curl -u admin:geoserver -XPOST -H 'Content-type: text/xml'  \
     *      -d "<featureType><name>$BARE</name><nativeCRS>EPSG:4326</nativeCRS><enabled>true</enabled></featureType>"  \
     *      http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/datastores/$STORENAME/featuretypes/$LAYERNAME
     * }</PRE>
     *
     * @return true if the operation completed successfully.
     */
    public boolean publishShp(String workspace, String storename, String layername, File zipFile, String srs) throws FileNotFoundException {
        // build full URL
        StringBuilder sbUrl = new StringBuilder(restURL)
                .append("/rest/workspaces/").append(workspace)
                .append("/datastores/").append(storename)
                .append("/file.shp?");
//        if (workspace != null) {
//            sbUrl.append("namespace=").append(workspace);
//        }
//        sbUrl.append("&SRS=4326&SRSHandling=Force"); // hack

        String sentResult = HTTPUtils.put(sbUrl.toString(), zipFile, "application/zip", gsuser, gspass);
        boolean shpSent = sentResult != null;

        if (shpSent) {
            LOGGER.info("Zipfile successfully uploaded (layer:" + layername + " zip:" + zipFile + ")");

            StringBuilder postUrl = new StringBuilder(restURL)
                    .append("/rest/workspaces/").append(workspace)
                    .append("/datastores/").append(storename)
                    .append("/featuretypes/").append(layername);

            GSFeatureTypeEncoder fte = new GSFeatureTypeEncoder();
            fte.addName(layername);
            fte.addSRS(srs);

            String configuredResult = HTTPUtils.putXml(postUrl.toString(), fte.toString(), this.gsuser, this.gspass);
            boolean shpConfigured = configuredResult != null;

            if (!shpConfigured) {
                LOGGER.warn("Error in configuring " + workspace + ":" + storename + "/" + layername + " -- Zipfile was uploaded successfully: " + zipFile);
            } else {
                LOGGER.info("Shapefile successfully configured (layer:" + layername + ")");
            }

            return shpConfigured;

        } else {
            LOGGER.warn("Error in sending zipfile " + workspace + ":" + storename + "/" + layername + " " + zipFile);
            return false;
        }

    }

    /**
     * Publish a table in a PostGis store as a new layer.
     *
     * <P>This is the equivalent call with cUrl:
     * <PRE>{@code curl -u admin:geoserver -XPOST -H 'Content-type: text/xml' \
     *      -d "<featureType><name>easia_gaul_1_aggr</name><nativeCRS>EPSG:4326</nativeCRS><enabled>true</enabled></featureType>" \
     *      http://localhost:8080/geoserver/rest/workspaces/it.geosolutions/datastores/pg_kids/featuretypes
     * }</PRE>
     *
     * and a PUT to
     * <BR>restURL + "/rest/layers/" + layerName
     *
     */
    public boolean publishDBLayer(String workspace, String storename, String layername, String srs, String defaultStyle) {
        StringBuilder postUrl = new StringBuilder(restURL)
                .append("/rest/workspaces/").append(workspace)
                .append("/datastores/").append(storename)
                .append("/featuretypes");

        GSFeatureTypeEncoder fte = new GSFeatureTypeEncoder();
        fte.addName(layername);
        fte.addSRS(srs); // srs=null?"EPSG:4326":srs);
        String ftypeXml = fte.toString();

        String configuredResult = HTTPUtils.postXml(postUrl.toString(), ftypeXml, this.gsuser, this.gspass);
        boolean published = configuredResult != null;
        boolean configured = false;

        if (!published) {
            LOGGER.warn("Error in publishing (" + configuredResult + ") "
                    + workspace + ":" + storename + "/" + layername);
        } else {
            LOGGER.info("DB layer successfully added (layer:" + layername + ")");

            GSLayerEncoder layerEncoder = new GSLayerEncoder();
            layerEncoder.addDefaultStyle(defaultStyle);
            configured = configureLayer(layerEncoder, layername);

            if (!configured) {
                LOGGER.warn("Error in configuring (" + configuredResult + ") "
                        + workspace + ":" + storename + "/" + layername);
            } else {
                LOGGER.info("DB layer successfully configured (layer:" + layername + ")");
            }
        }

        return published && configured;
    }

    //==========================================================================
    //=== GEOTIFF
    //==========================================================================

    /**
     * Publish a GeoTiff.
     *
     * <P>This is the equivalent call with cUrl:
     * <PRE>{@code
     *curl -u admin:geoserver -XPUT -H 'Content-type: text' -d "file:$FULLPATH" \
     *      http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/coveragestores/$STORENAME/external.geotiff
     * }</PRE>
     *
     * @return true if the operation completed successfully.
     * @deprecated UNTESTED
     */
    public boolean publishGeoTIFF(String workspace, String storeName, File geotiff) throws FileNotFoundException {
        String sUrl = restURL + "/rest/workspaces/" + workspace + "/coveragestores/" + storeName + "/geotiff";
        String sendResult = HTTPUtils.put(sUrl, geotiff, "text", gsuser, gspass); // CHECKME: text?!?
        boolean sent = sendResult != null;
        return sent;
    }

    /**
     * Publish a GeoTiff already in a filesystem readable by GeoServer.
     *
     * @param workspace an existing workspace
     * @param storeName the coverageStore to be created
     * @param geotiff the geoTiff to be published
     *
     * @return a PublishedCoverage, or null on errors
     * @throws FileNotFoundException
     */
    public RESTCoverageStore publishExternalGeoTIFF(String workspace, String storeName, File geotiff, String srs, String defaultStyle) throws FileNotFoundException {
        // create store
        String sUrl = restURL + "/rest/workspaces/" + workspace + "/coveragestores/" + storeName + "/external.geotiff";
        String sendResult = HTTPUtils.put(sUrl, geotiff.toURI().toString(), "text/plain", gsuser, gspass);
        RESTCoverageStore store = RESTCoverageStore.build(sendResult);

        if (store != null) {
            try {
//              // retrieve coverage name
                GeoServerRESTReader reader = new GeoServerRESTReader(restURL, gsuser, gspass);
                RESTCoverageList covList = reader.getCoverages(workspace, storeName);
                if (covList.isEmpty()) {
                    LOGGER.error("No coverages found in new coveragestore " + storeName);
                    return null;
                }
                final String coverageName = covList.get(0).getName();

                // config coverage props (srs)
                GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
                coverageEncoder.addName(FilenameUtils.getBaseName(geotiff.getName()));
                coverageEncoder.addSRS(srs);
                coverageEncoder.setProjectionPolicy(ProjectionPolicy.REPROJECT_TO_DECLARED);
                configureCoverage(coverageEncoder, workspace, storeName, coverageName);

                // config layer props (style, ...)
                GSLayerEncoder layerEncoder = new GSLayerEncoder();
                layerEncoder.addDefaultStyle(defaultStyle);
                configureLayer(layerEncoder, coverageName);

            } catch (Exception e) {
                LOGGER.warn("Could not configure external GEOTiff:" + storeName, e);
                store = null; // TODO: should we remove the configured pc?
            }
        }

        return store;
    }

    //==========================================================================
    //=== MOSAIC
    //==========================================================================

    /**
     * Publish a Mosaic already in a filesystem readable by GeoServer.
     *
     * <P> Sample cUrl usage:<BR>
     * <>
     * <TT>curl -u admin:geoserver -XPUT -H 'Content-type: text' -d "file:$ABSPORTDIR"
     *          http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/coveragestores/$BAREDIR/external.imagemosaic </TT>
     *
     * @param workspace an existing workspace
     * @param storeName the name of the coverageStore to be created
     * @param mosaicDir the directory where the raster images are located
     * @return true if the operation completed successfully.
     * @throws FileNotFoundException
     */
    public RESTCoverageStore configureExternaMosaicDatastore(String workspace, String storeName, File mosaicDir) throws FileNotFoundException {
        if (!mosaicDir.isDirectory()) {
            throw new IllegalArgumentException("Not a directory '" + mosaicDir + "'");
        }
        String sUrl = restURL + "/rest/workspaces/" + workspace + "/coveragestores/" + storeName + "/external.imagemosaic";
        String sendResult = HTTPUtils.put(sUrl, mosaicDir.toURI().toString(), "text/plain", gsuser, gspass);
        return RESTCoverageStore.build(sendResult);
    }

    /**
     * Publish a Mosaic already in a filesystem readable by GeoServer.
     *
     * <P> Sample cUrl usage:<BR>
     * <TT>curl -u admin:geoserver -XPUT -H 'Content-type: text' -d "file:$ABSPORTDIR"
     *          http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/coveragestores/$BAREDIR/external.imagemosaic </TT>
     *
     * @param workspace an existing workspace
     * @param storeName the name of the coverageStore to be created
     * @param mosaicDir the directory where the raster images are located
     * @param srs the coverage declared SRS
     * @param defaultStyle may be null
     * 
     * @return true if the operation completed successfully.
     * 
     * @throws FileNotFoundException
     */
    public RESTCoverageStore publishExternalMosaic(String workspace, String storeName, File mosaicDir, String srs, String defaultStyle) throws FileNotFoundException {
        GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
        coverageEncoder.addSRS(srs);
        coverageEncoder.addName(FilenameUtils.getBaseName(mosaicDir.getName()));
        GSLayerEncoder layerEncoder = new GSLayerEncoder();
        layerEncoder.addDefaultStyle(defaultStyle);

        return publishExternalMosaic(workspace, storeName, mosaicDir, coverageEncoder, layerEncoder);
    }

    /**
     * Publish a Mosaic already in a filesystem readable by GeoServer.
     *
     * <P> Sample cUrl usage:<BR>
     * <TT>curl -u admin:geoserver -XPUT -H 'Content-type: text' -d "file:$ABSPORTDIR"
     *          http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/coveragestores/$BAREDIR/external.imagemosaic </TT>
     *
     * @param workspace an existing workspace
     * @param storeName the name of the coverageStore to be created
     * @param mosaicDir the directory where the raster images are located
     * @param coverageEncoder the set of parameters to be set to the coverage (bbox, srs, ...)
     * @param layerEncoder  the set of parameters to be set to the layer (defaultstyle, wmspath, ...)
     * 
     * @return true if the operation completed successfully.
     * 
     * @throws FileNotFoundException
     */
    public RESTCoverageStore publishExternalMosaic(String workspace, String storeName, File mosaicDir, GSCoverageEncoder coverageEncoder, GSLayerEncoder layerEncoder) throws FileNotFoundException {
        RESTCoverageStore store = configureExternaMosaicDatastore(workspace, storeName, mosaicDir);

        if (coverageEncoder == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("no coverageEncoder provided for " + workspace + ":" + storeName);
            }
            coverageEncoder = new GSCoverageEncoder();
        }

        if (layerEncoder == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("no layerEncoder provided for " + workspace + ":" + storeName);
            }
            layerEncoder = new GSLayerEncoder();
        }

        if (store != null) {
            try {
//              // retrieve coverage name
                GeoServerRESTReader reader = new GeoServerRESTReader(restURL, gsuser, gspass);
                RESTCoverageList covList = reader.getCoverages(store.getWorkspaceName(), storeName);
                if (covList.isEmpty()) {
                    LOGGER.error("No coverages found in new coveragestore " + storeName);
                    return null;
                }
                String coverageName = covList.get(0).getName();

                configureCoverage(coverageEncoder, store.getWorkspaceName(), storeName, coverageName);
                configureLayer(layerEncoder, storeName);

            } catch (Exception e) {
                LOGGER.warn("Could not configure external mosaic:" + storeName, e);
                store = null; // TODO: should we remove the configured store?
            }
        }

        return store;
    }

    //==========================================================================
    //=== COVERAGES
    //==========================================================================

    /**
     * Remove the Coverage configuration from GeoServer.
     * <BR>
     * First, the associated layer is removed, then the Coverage configuration itself.
     * <P>
     * <B>CHECKME</B> Maybe the coveragestore has to be removed as well.
     *
     * <P> REST URL:
     * <TT>http://localhost:8080/geoserver/rest/workspaces/it.geosolutions/coveragestores/gbRESTtestStore/coverages/resttestdem.xml</TT>
     *
     * @return true if the operation completed successfully.
     */
    public boolean unpublishCoverage(String workspace, String storename, String layername) {
        try {
            // delete related layer
            URL deleteLayerUrl = new URL(restURL + "/rest/layers/" + layername);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Going to delete " + "/rest/layers/" + layername);
            }
            boolean layerDeleted = HTTPUtils.delete(deleteLayerUrl.toExternalForm(), gsuser, gspass);
            if (!layerDeleted) {
                LOGGER.warn("Could not delete layer '" + layername + "'");
                return false;
            }
            // delete the coverage
            URL deleteCovUrl = new URL(restURL + "/rest/workspaces/" + workspace + "/coveragestores/" + storename + "/coverages/" + layername);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Going to delete " + "/rest/workspaces/" + workspace + "/coveragestores/" + storename + "/coverages/" + layername);
            }
            boolean covDeleted = HTTPUtils.delete(deleteCovUrl.toExternalForm(), gsuser, gspass);
            if (!covDeleted) {
                LOGGER.warn("Could not delete coverage " + workspace + ":" + storename + "/" + layername + ", but layer was deleted.");
            } else {
                LOGGER.info("Coverage successfully deleted " + workspace + ":" + storename + "/" + layername);
            }
            return covDeleted;

            // the covstore is still there: should we delete it?

        } catch (MalformedURLException ex) {
            LOGGER.error(ex);
            return false;
        }
    }

    //==========================================================================
    //=== FEATURETYPES
    //==========================================================================

    /**
     * Removes the featuretype and the associated layer.
     * <BR>You may also want to {@link #removeDatastore(String, String) remove the datastore}.
     *
     * @return true if the operation completed successfully.
     */
    public boolean unpublishFeatureType(String workspace, String storename, String layername) {
        try {
            // delete related layer
            URL deleteLayerUrl = new URL(restURL + "/rest/layers/" + layername);
            boolean layerDeleted = HTTPUtils.delete(deleteLayerUrl.toExternalForm(), gsuser, gspass);
            if (!layerDeleted) {
                LOGGER.warn("Could not delete layer '" + layername + "'");
                return false;
            }
            // delete the coverage
            URL deleteFtUrl = new URL(restURL + "/rest/workspaces/" + workspace + "/datastores/" + storename + "/featuretypes/" + layername);
            boolean ftDeleted = HTTPUtils.delete(deleteFtUrl.toExternalForm(), gsuser, gspass);
            if (!ftDeleted) {
                LOGGER.warn("Could not delete featuretype " + workspace + ":" + storename + "/" + layername + ", but layer was deleted.");
            } else {
                LOGGER.info("FeatureType successfully deleted " + workspace + ":" + storename + "/" + layername);
            }

            return ftDeleted;

            // the store is still there: should we delete it?

        } catch (MalformedURLException ex) {
            LOGGER.error(ex);
            return false;
        }
    }

    /**
     * Remove a given Datastore in a given Workspace.
     *
     * @param workspace The name of the workspace
     * @param storename The name of the Datastore to remove.
     * @return <TT>true</TT> if the datastore was successfully removed.
     */
    public boolean removeDatastore(String workspace, String storename) {
        try {
            URL deleteStore = new URL(restURL + "/rest/workspaces/" + workspace + "/datastores/" + storename);
            boolean deleted = HTTPUtils.delete(deleteStore.toExternalForm(), gsuser, gspass);
            if (!deleted) {
                LOGGER.warn("Could not delete datastore " + workspace + ":" + storename);
            } else {
                LOGGER.info("Datastore successfully deleted " + workspace + ":" + storename);
            }

            return deleted;
        } catch (MalformedURLException ex) {
            LOGGER.error(ex);
            return false;
        }
    }

    /**
     * Remove a given CoverageStore in a given Workspace.
     *
     * @param workspace The name of the workspace
     * @param storename The name of the CoverageStore to remove.
     * @return <TT>true</TT> if the CoverageStore was successfully removed.
     */
    public boolean removeCoverageStore(String workspace, String storename) {
        try {
            URL deleteStore = new URL(restURL + "/rest/workspaces/" + workspace + "/coveragestores/" + storename);
            boolean deleted = HTTPUtils.delete(deleteStore.toExternalForm(), gsuser, gspass);
            if (!deleted) {
                LOGGER.warn("Could not delete CoverageStore " + workspace + ":" + storename);
            } else {
                LOGGER.info("CoverageStore successfully deleted " + workspace + ":" + storename);
            }

            return deleted;
        } catch (MalformedURLException ex) {
            LOGGER.error(ex);
            return false;
        }
    }

    /**
     * Remove a given Workspace.
     *
     * @param workspace The name of the workspace
     * @return <TT>true</TT> if the WorkSpace was successfully removed.
     */
    public boolean removeWorkspace(String workspace) {
        workspace = sanitize(workspace);
        try {
            URL deleteUrl = new URL(restURL + "/rest/workspaces/" + workspace);
            boolean deleted = HTTPUtils.delete(deleteUrl.toExternalForm(), gsuser, gspass);
            if (!deleted) {
                LOGGER.warn("Could not delete Workspace " + workspace);
            } else {
                LOGGER.info("Workspace successfully deleted " + workspace);
            }

            return deleted;
        } catch (MalformedURLException ex) {
            LOGGER.error(ex);
            return false;
        }
    }

    public boolean removeLayerGroup(String name) {
        try {
            URL deleteUrl = new URL(restURL + "/rest/layergroups/" + name);
            boolean deleted = HTTPUtils.delete(deleteUrl.toExternalForm(), gsuser, gspass);
            if (!deleted) {
                LOGGER.warn("Could not delete layergroup " + name);
            } else {
                LOGGER.info("Layergroup successfully deleted: " + name);
            }

            return deleted;
        } catch (MalformedURLException ex) {
            LOGGER.error(ex);
            return false;
        }
    }

    //==========================================================================
    //===
    //==========================================================================

    /**
     * Allows to configure some layer attributes such as WmsPath and DefaultStyle
     *
     */
    protected boolean configureLayer(final GSLayerEncoder layer, final String layerName) {

        if (layer.isEmpty()) {
            return true;
        }

        final String url = restURL + "/rest/layers/" + layerName;

        String layerXml = layer.toString();
        String sendResult = HTTPUtils.putXml(url, layerXml, gsuser, gspass);
        if (sendResult != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Layer successfully configured: " + layerName);
            }
        } else {
            LOGGER.warn("Error configuring layer " + layerName + " (" + sendResult + ")");
        }

        return sendResult != null;
    }

    /**
     * Allows to configure some coverage's attributes
     *
     */
    protected boolean configureCoverage(final GSCoverageEncoder ce, String wsname, String csname, String cname) {

        final String url = restURL + "/rest/workspaces/" + wsname + "/coveragestores/" + csname + "/coverages/" + cname + ".xml";

        String xmlBody = ce.toString();
        String sendResult = HTTPUtils.putXml(url, xmlBody, gsuser, gspass);
        if (sendResult != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Coverage successfully configured " + wsname + ":" + csname + ":" + cname);
            }
        } else {
            LOGGER.warn("Error configuring coverage " + wsname + ":" + csname + ":" + cname + " (" + sendResult + ")");
        }

        return sendResult != null;
    }

    /**
     *
     */
    protected String sanitize(String s) {
        if (s.indexOf(".") != -1) {
            return s + ".DUMMY";
        }
        return s;
    }

    protected String encode(String s) {
//        try {
//			return URLEncoder.encode(s,"UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			LOGGER.warn("Error encoding :"+s+" with UTF-8: "+e.getLocalizedMessage());
			return URLEncoder.encode(s);
//		}
    }
}

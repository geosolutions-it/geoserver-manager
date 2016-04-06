/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2015,2016 GeoSolutions S.A.S.
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
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageGranulesList;
import it.geosolutions.geoserver.rest.decoder.RESTStyleList;
import it.geosolutions.geoserver.rest.decoder.utils.NameLinkElem;
import it.geosolutions.geoserver.rest.encoder.GSBackupEncoder;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSLayerGroupEncoder;
import it.geosolutions.geoserver.rest.encoder.GSNamespaceEncoder;
import it.geosolutions.geoserver.rest.encoder.GSPostGISDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.geosolutions.geoserver.rest.encoder.GSWorkspaceEncoder;
import it.geosolutions.geoserver.rest.encoder.coverage.GSCoverageEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStructuredGridCoverageReaderManager;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStructuredGridCoverageReaderManager.ConfigureCoveragesOption;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStyleManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.ZipFile;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connect to a GeoServer instance to publish or modify its contents via REST API.
 * <P>
 * There are no modifiable instance fields, so all the calls are thread-safe.
 * 
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * @author Lennart Karsten - lennart.k@thinking-aloud.eu
 */
public class GeoServerRESTPublisher {

    public static final String DEFAULT_CRS = "EPSG:4326";

    /** The logger for this class */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeoServerRESTPublisher.class);

    /**
     * GeoServer instance base URL. E.g.: <TT>http://localhost:8080/geoserver</TT>.
     */
    private final String restURL;

    /**
     * GeoServer instance privileged username, with read & write permission on REST API
     */
    private final String gsuser;

    /**
     * GeoServer instance password for privileged username with r&w permission on REST API
     */
    private final String gspass;


    private final GeoServerRESTStyleManager styleManager;
    /**
     * Creates a <TT>GeoServerRESTPublisher</TT> to connect against a GeoServer instance with the given URL and user credentials.
     * 
     * @param restURL the base GeoServer URL (e.g.: <TT>http://localhost:8080/geoserver</TT>)
     * @param username auth credential
     * @param password auth credential
     */
    public GeoServerRESTPublisher(String restURL, String username, String password) {
        this.restURL = HTTPUtils.decurtSlash(restURL);
        this.gsuser = username;
        this.gspass = password;

        URL url = null;
        try {
            url = new URL(restURL);
        } catch (MalformedURLException ex) {
            LOGGER.error("Bad URL: Calls to GeoServer are going to fail" , ex);
        }
        styleManager = new GeoServerRESTStyleManager(url, username, password);
    }

    // ==========================================================================
    // === BACKUP and RESTORE
    // ==========================================================================

    /**
     * Issues a GeoServer BACKUP.
     * <P>
     * Won't include data, cached tiles, or logs. Use {@link #backup(String, boolean, boolean, boolean)} to control these parameters.
     * 
     * @param backupDir the target Backup Dir String.
     * 
     * @return <TT>id</TT> of the backup.
     * @throws IllegalArgumentException if the backupDir is null or empty
     */
    public String backup(final String backupDir) throws IllegalArgumentException {
        /*
         * This is the equivalent call with cUrl:
         * 
         * {@code curl -u admin:geoserver -XPOST \ -H 'Content-type: text/xml' \ --data
         * "&lt;task&gt;&lt;path&gt;${BACKUP_DATADIR}&lt;/path&gt;&lt;/task&gt;" \ ${restURL}/rest/bkprst/backup}
         */
        return backup(backupDir, false, false, false);
    }

    /**
     * Issues a GeoServer BACKUP.
     * 
     * @param backupDir the target Backup Dir String.
     * @param includedata whether or not include the data dir Boolean.
     * @param includegwc whether or not include the geowebcache dir Boolean.
     * @param includelog whether or not include the log dir Boolean.
     * 
     * @return <TT>id</TT> of the backup.
     * @throws IllegalArgumentException if the backupDir is null or empty.
     */
    public String backup(final String backupDir, final boolean includedata,
            final boolean includegwc, final boolean includelog) throws IllegalArgumentException {
        /*
         * This is the equivalent call with cUrl:
         * 
         * {@code curl -u admin:geoserver -XPOST \ -H 'Content-type: text/xml' \ --data
         * "&lt;task&gt;&lt;path&gt;${BACKUP_DATADIR}&lt;/path&gt;&lt;includedata&gt;${includedata}&lt;/includedata&gt;&lt;includegwc&gt;${includegwc}&lt;/includegwc&gt;&lt;includelog&gt;${includelog}&lt;/includelog&gt;&lt;/task&gt;"
         * \ ${restURL}/rest/bkprst/backup}
         */
        if ((backupDir == null) || backupDir.isEmpty()) {
            throw new IllegalArgumentException("The backup_dir must not be null or empty");
        }

        StringBuilder bkpUrl = new StringBuilder(restURL);
        bkpUrl.append("/rest/bkprst/backup");

        final GSBackupEncoder bkpenc = new GSBackupEncoder(backupDir);
        bkpenc.setIncludeData(includedata);
        bkpenc.setIncludeGwc(includegwc);
        bkpenc.setIncludeLog(includelog);
        final String result = HTTPUtils.post(bkpUrl.toString(), bkpenc.toString(), "text/xml",
                gsuser, gspass);

        return result;
    }

    /**
     * Issues a GeoServer RESTORE.
     * 
     * @param backupDir the source backup dir.
     * 
     * @return <TT>id</TT> of the backup.
     * @throws IllegalArgumentException if the backupDir is null or empty
     */
    public String restore(final String backupDir) throws IllegalArgumentException {
        /*
         * This is the equivalent call with cUrl:
         * 
         * {@code curl -u admin:geoserver -XPOST \ -H 'Content-type: text/xml' \ --data
         * "&lt;task&gt;&lt;path&gt;${BACKUP_DATADIR}&lt;/path&gt;&lt;/task&gt;" \ ${restURL}/rest/bkprst/restore}
         */
        if ((backupDir == null) || backupDir.isEmpty()) {
            throw new IllegalArgumentException("The backup_dir must not be null or empty");
        }

        StringBuilder bkpUrl = new StringBuilder(restURL);
        bkpUrl.append("/rest/bkprst/restore");

        final GSBackupEncoder bkpenc = new GSBackupEncoder(backupDir);

        final String result = HTTPUtils.post(bkpUrl.toString(), bkpenc.toString(), "text/xml",
                gsuser, gspass);

        return result;
    }

    // ==========================================================================
    // === WORKSPACES
    // ==========================================================================

    /**
     * Create a new Workspace.
     * <P>
     * GeoServer will automatically create an associated Namespace with the URI being "http://{workspaceName}". To specify a custom Namespace URI, use
     * {@link #createWorkspace(String, URI)}.
     * 
     * @param workspace The name of the new workspace.
     * 
     * @return <TT>true</TT> if the workspace was created.
     */
    public boolean createWorkspace(final String workspace) {
        /*
         * This is the equivalent call with cUrl:
         * 
         * {@code curl -u admin:geoserver -XPOST \ -H 'Content-type: text/xml' \ -d "<workspace><name>$WORKSPACE</name></workspace>" \
         * http://$GSIP:$GSPORT/$SERVLET/rest/workspaces }
         */
        final String sUrl = restURL + "/rest/workspaces";
        final GSWorkspaceEncoder wsenc = new GSWorkspaceEncoder(workspace);
        final String wsxml = wsenc.toString();
        final String result = HTTPUtils.postXml(sUrl, wsxml, gsuser, gspass);
        return result != null;
    }

    /**
     * Create both a workspace and its associated namespace.
     * <P>
     * Note that this method is equivalent to {@link #createNamespace}.
     * 
     * @param name Name for the new workspace, which will be also its associated namespace prefix.
     * @param uri Namespace URI. Cannot be empty.
     * @return <TT>true</TT> if the Workspace and its associated namespace were successfully created.
     */
    public boolean createWorkspace(final String name, final URI uri) {
        // This is really an alias to createNamespace, as GeoServer
        // will automatically create the associated workspace as well.
        return createNamespace(name, uri);
    }

    // ==========================================================================
    // === NAMESPACES
    // ==========================================================================

    /**
     * Create a new Namespace. GeoServer will automatically create the corresponding Workspace.
     * 
     * Prefix and URI are mandatory and cannot be empty. If a Namespace with the given prefix already exists, it won't be created.
     * 
     * @param prefix The name of the new Namespace.
     * @param uri The URI of the new Namespace.
     * 
     * @return <TT>true</TT> if the Namespace was successfully created.
     * @see <a href="http://docs.geoserver.org/stable/en/user/restconfig/rest-config-api.html#namespaces"> GeoServer Documentation</a>
     */
    public boolean createNamespace(final String prefix, final URI uri) {
        final String sUrl = restURL + "/rest/namespaces";
        final GSNamespaceEncoder nsenc = new GSNamespaceEncoder(prefix, uri);
        final String nsxml = nsenc.toString();
        final String result = HTTPUtils.postXml(sUrl, nsxml, gsuser, gspass);
        return result != null;
    }

    /**
     * Update a Namespace URI.
     * 
     * Prefix and URI are mandatory and cannot be empty. A Namespace with the given prefix should exist.
     * 
     * @param prefix The prefix of an existing Namespace.
     * @param uri The new URI.
     * 
     * @return <TT>true</TT> if the Namespace was successfully updated.
     */
    public boolean updateNamespace(final String prefix, final URI uri) {
        final String sUrl = restURL + "/rest/namespaces/" + encode(prefix);
        final GSNamespaceEncoder nsenc = new GSNamespaceEncoder(prefix, uri);
        final String nsxml = nsenc.toString();
        final String result = HTTPUtils.put(sUrl, nsxml, "application/xml", gsuser, gspass);
        return result != null;
    }

    /**
     * Remove a given Namespace. It will remove the associated Workspace as well.
     * 
     * @param prefix The Namespace prefix
     * @param recurse The recurse parameter is used to recursively delete all resources contained in the workspace associated with this Namespace.
     *        This includes data stores, coverage stores, feature types, etc... Allowable values for this parameter are <i>true</i> or <i>false</i>.
     *        The default (safer) value is <i>false</i>.
     * 
     * @return <TT>true</TT> if the Namespace was successfully removed.
     */
    public boolean removeNamespace(final String prefix, boolean recurse) {
        // Hack: We are instead calling removeWorkspace, as DELETE on
        // a namespace will leave associated workspace in an inconsistent
        // state. See https://jira.codehaus.org/browse/GEOS-5075
        // TODO switch to namespace when GEOS-5075 is solved
        return removeWorkspace(prefix, recurse);
    }

    // ==========================================================================
    // === STYLES
    // ==========================================================================

    /**
     * Store and publish a Style.
     * 
     * @param sldBody the full SLD document as a String.
     * 
     * @return <TT>true</TT> if the operation completed successfully.
     */
    public boolean publishStyle(String sldBody) {
        return styleManager.publishStyle(sldBody);
    }

    /**
     * Store and publish a Style, assigning it a name.
     * 
     * @param sldBody the full SLD document as a String.
     * @param name the Style name.
     * 
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if the style body is null or empty.
     */
    public boolean publishStyle(final String sldBody, final String name)
            throws IllegalArgumentException {
        return styleManager.publishStyle(sldBody, name);
    }

    /**
     * Store and publish a Style.
     * 
     * @param sldFile the File containing the SLD document.
     * 
     * @return <TT>true</TT> if the operation completed successfully.
     */
    public boolean publishStyle(File sldFile) {
        return styleManager.publishStyle(sldFile);
    }

    /**
     * Store and publish a Style, assigning it a name.
     * 
     * @param sldFile the File containing the SLD document.
     * @param name the Style name.
     * 
     * @return <TT>true</TT> if the operation completed successfully.
     */
    public boolean publishStyle(File sldFile, String name) {
        return styleManager.publishStyle(sldFile, name);
    }

    /**
     * Store and publish a Style, assigning it a name and choosing the raw format.
     *
     * @param sldBody the full SLD document as a String.
     * @param name the Style name.
     * @param raw the raw format
     *
     * @return <TT>true</TT> if the operation completed successfully.
     */
    public boolean publishStyle(String sldBody, String name, boolean raw) {
        return styleManager.publishStyle(sldBody, name, raw);
    }
    
    /**
     * Store and publish a Style, assigning it a name and choosing the raw format.
     *
     * @param sldFile the File containing the SLD document.
     * @param name the Style name.
     * @param raw the raw format
     *
     * @return <TT>true</TT> if the operation completed successfully.
     */
    public boolean publishStyle(File sldFile, String name, boolean raw) {
        return styleManager.publishStyle(sldFile, name, raw);
    }

    /**
     * Update a Style.
     * 
     * @param sldFile the File containing the SLD document.
     * @param name the Style name.
     * @param raw the raw format
     * 
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if the style body or name are null or empty.
     */
    public boolean updateStyle(final File sldFile, final String name, boolean raw)
            throws IllegalArgumentException {
        return styleManager.updateStyle(sldFile, name, raw);
    }
    
    /**
     * Update a Style.
     * 
     * @param sldBody the new SLD document as a String.
     * @param name the Style name.
     * @param raw the raw format
     * 
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if the style body or name are null or empty.
     */
    public boolean updateStyle(final String sldBody, final String name, boolean raw)
            throws IllegalArgumentException {
        return styleManager.updateStyle(sldBody, name, raw);
    }

    /**
     * Update a Style.
     * 
     * @param sldBody the new SLD document as a String.
     * @param name the Style name to update.
     * 
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if the style body or name are null or empty.
     */
    public boolean updateStyle(final String sldBody, final String name)
            throws IllegalArgumentException {
        return styleManager.updateStyle(sldBody, name);
    }

    /**
     * Update a Style.
     * 
     * @param sldFile the File containing the SLD document.
     * @param name the Style name.
     * 
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if the sldFile file or name are null or name is empty.
     */
    public boolean updateStyle(final File sldFile, final String name)
            throws IllegalArgumentException {

        return styleManager.updateStyle(sldFile, name);
    }

    /**
     * Remove a Style.
     * <P>
     * The Style will be unpublished, and (optionally) the SLD file will be removed.
     * 
     * @param styleName the name of the Style to remove.
     * @param purge remove the related SLD file from disk.
     * 
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if styleName is null or empty.
     */
    public boolean removeStyle(String styleName, final boolean purge)
            throws IllegalArgumentException {

        return styleManager.removeStyle(styleName, purge);
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
        return styleManager.removeStyle(styleName);
    }

    /**
     * @since GeoServer 2.2
     * @see GeoServerRESTStyleManager#
     */
    public boolean publishStyleInWorkspace(String workspace, String sldBody) {
        return styleManager.publishStyleInWorkspace(workspace, sldBody);
    }

    /**
     * @since GeoServer 2.2
     * @see GeoServerRESTStyleManager#
     */
    public boolean publishStyleInWorkspace(String workspace, String sldBody, String name) throws IllegalArgumentException {
        return styleManager.publishStyleInWorkspace(workspace, sldBody, name);
    }

    /**
     * @since GeoServer 2.2
     * @see GeoServerRESTStyleManager#publishStyleInWorkspace(java.lang.String, java.io.File)
     */
    public boolean publishStyleInWorkspace(String workspace, File sldFile) {
        return styleManager.publishStyleInWorkspace(workspace, sldFile);
    }

    /**
     * @since GeoServer 2.2
     * @see GeoServerRESTStyleManager#publishStyleInWorkspace(java.lang.String, java.io.File, java.lang.String)
     */
    public boolean publishStyleInWorkspace(String workspace, File sldFile, String name) {
        return styleManager.publishStyleInWorkspace(workspace, sldFile, name);
    }

    /**
     * @since GeoServer 2.2
     * @see GeoServerRESTStyleManager#updateStyleInWorkspace(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean updateStyleInWorkspace(String workspace, String sldBody, String name) throws IllegalArgumentException {
        return styleManager.updateStyleInWorkspace(workspace, sldBody, name);
    }

    /**
     * @since GeoServer 2.2
     * @see GeoServerRESTStyleManager#updateStyleInWorkspace(java.lang.String, java.io.File, java.lang.String)
     */
    public boolean updateStyleInWorkspace(String workspace, File sldFile, String name) throws IllegalArgumentException {
        return styleManager.updateStyleInWorkspace(workspace, sldFile, name);
    }

    /**
     * @since GeoServer 2.2
     * @see GeoServerRESTStyleManager#removeStyleInWorkspace(java.lang.String, java.lang.String, boolean)
     */
    public boolean removeStyleInWorkspace(String workspace, String styleName, boolean purge) throws IllegalArgumentException {
        return styleManager.removeStyleInWorkspace(workspace, styleName, purge);
    }

    /**
     * @since GeoServer 2.2
     * @see GeoServerRESTStyleManager#removeStyleInWorkspace(java.lang.String, java.lang.String)
     */
    public boolean removeStyleInWorkspace(String workspace, String styleName) {
        return styleManager.removeStyleInWorkspace(workspace, styleName);
    }


    // ==========================================================================
    // === DATASTORE PUBLISHING
    // ==========================================================================

    /**
     * 
     * @author cancellieri
     * @deprecated use {@link StoreType}
     */
    public enum DataStoreType {
        /**
         * Raster based data sources.
         */
        COVERAGESTORES,
        /**
         * Vector based data sources. Can be a file in the case of a Shapefile, a database connection in the case of PostGIS, or a server in the case
         * of a remote Web Feature Service.
         */
        DATASTORES;

        /**
         * @deprecated use {@link StoreType#getTypeNameWithFormat(StoreType, Format)}
         * @param type
         * @return
         */
        public static String getTypeName(StoreType type) {
            return StoreType.getTypeNameWithFormat(type, Format.XML);
        }

        /**
         * @deprecated use {@link StoreType#toString()}
         */
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    /**
     * DataStoreType definitions.
     * <UL>
     * <LI>{@link #DATASTORES} vector based data sources.
     * <LI>{@link #COVERAGESTORES} raster based data sources.
     * </UL>
     * 
     * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
     */
    public enum StoreType {
        /**
         * Raster based data sources.
         */
        COVERAGESTORES,
        /**
         * Vector based data sources. Can be a file in the case of a Shapefile, a database connection in the case of PostGIS, or a server in the case
         * of a remote Web Feature Service.
         */
        DATASTORES;

        /**
         * Get the type name of a StoreType with the specified format.
         * 
         * @param type the StoreType.
         * @param format see {@link Format}
         * @return "featureTypes.{xml|html|...}" for DATASTORES, "coverages.{xml|html|...}" otherwise.
         */
        public static String getTypeNameWithFormat(StoreType type, Format format) {
            return getTypeName(type) + "." + format;
        }

        /**
         * Get the type name of a StoreType with the specified format.
         * 
         * @param type the StoreType.
         * @param format see {@link Format}
         * @return "featuretypes.{xml|html|...}" for DATASTORES, "coverages.{xml|html|...}" otherwise.
         */
        public String getTypeNameWithFormat(Format format) {
            return getTypeName(this).toLowerCase() + "." + format;
        }

        /**
         * Get the type name of a StoreType.
         * 
         * @param type the StoreType.
         * @return "featureTypes" for DATASTORES, "coverages" otherwise.
         */
        public static String getTypeName(StoreType type) {
            switch (type) {
            case COVERAGESTORES:
                return "coverages"; // Format
            case DATASTORES:
                return "featureTypes";
            default:
                return "coverages";
            }
        }

        /**
         * Get the type name of a StoreType.
         * 
         * @param type the StoreType.
         * @return "dataStore" for DATASTORES, "coverageStore" otherwise.
         */
        public static String getType(StoreType type) {
            switch (type) {
            case COVERAGESTORES:
                return "coverageStore"; // Format
            case DATASTORES:
                return "dataStore";
            default:
                return "coverageStore";
            }
        }

        /**
         * Get the type name of a StoreType.
         * 
         * @return "featuretypes" for DATASTORES, "coverages" otherwise.
         */
        public String getTypeName() {
            return getTypeName(this);
        }

        /**
         * Get the type of a StoreType.
         * 
         * @param type the StoreType.
         * @return "dataStore" for DATASTORES, "coverageStore" otherwise.
         */
        public String getType() {
            return getType(this);
        }

        /**
         * Returns a lowercase representation of the parameter value, suitable to construct the rest call.
         */
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    /**
     * Specifies the method used to publish a resource.
     * <ul>
     * <li>{@link #FILE} upload a file from a local source.
     * <li>{@link #URL} indirectly upload a file from a remote source.
     * <li>{@link #EXTERNAL} don't upload; use an existing file on the server.
     * </ul>
     * 
     * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
     */
    public enum UploadMethod {
        /**
         * directly upload a file from a local source. The body of the request is the file itself.
         */
        FILE, @Deprecated
        file,
        /**
         * indirectly upload a file from a remote source. The body of the request is the URL where the data is published. This url must be visible
         * from the server.
         */
        URL, @Deprecated
        url,
        /**
         * forgo upload, and use an existing file on the server. The body of the request is the absolute local path to the existing file.
         */
        EXTERNAL, @Deprecated
        external;

        /**
         * Returns a lowercase representation of the parameter value, suitable to construct the rest call.
         */
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    /**
     * Vector data format being uploaded. Following extensions are supported:
     * <ul>
     * <li>{@link #SHP} Shapefile
     * <li>{@link #PROPERTIES} Property file
     * <li>{@link #H2} H2 Database
     * <li>{@link #SPATIALITE} SpatiaLite Database
     * </ul>
     * 
     * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
     */
    public enum DataStoreExtension {
        /** Shapefile */
        SHP,
        /** Properties file */
        PROPERTIES,
        /** H2 Database */
        H2,
        /** SpatiaLite Database */
        SPATIALITE;

        /**
         * Returns a lowercase representation of the parameter value, suitable to construct the rest call.
         */
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    /**
     * Raster data format being uploaded. Following extensions are supported:
     * <ul>
     * <li>{@link #GEOTIFF} GeoTiff coverage
     * <li>{@link #IMAGEMOSAIC} ImageMosaic
     * <li>{@link #WORLDIMAGE} Geo referenced image (JPEG,PNG,TIF)
     * </ul>
     * 
     * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
     */
    public enum CoverageStoreExtension {
        /** GeoTiff coverage */
        GEOTIFF,
        /** ImageMosaic */
        IMAGEMOSAIC,
        /** Geo referenced image (JPEG,PNG,TIF) */
        WORLDIMAGE,
        /** Esri ArcGrid */
        ARCGRID;

        /**
         * Returns a lowercase representation of the parameter value, suitable to construct the rest call.
         */
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    /**
     * Control if feature types are to be automatically configured upon file upload. It can take one of the three values:
     * <ul>
     * <li>{@link #FIRST} Configure first feature type only (default).
     * <li>{@link #NONE} Don't configure any feature types.
     * <li>{@link #ALL} Configure all feature types.
     * </ul>
     */
    public static enum ParameterConfigure {
        /** Configure first feature type only (default). */
        FIRST,
        /** Don't configure any feature types. */
        NONE,
        /** Configure all feature types. */
        ALL;

        /**
         * Returns a lowercase representation of the parameter value, suitable to construct the rest call.
         */
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    /**
     * Upload and publish data (vector or raster), and automatically create the associated Store if needed.
     * 
     * This is a generic method; use {@link #createDataStore} and {@link #createCoverageStore} for vector and raster publishing respectively.
     * 
     * @param workspace workspace where data will be published.
     * @param dsType Store type. See {@link DataStoreType}.
     * @param storeName Store name. Will be created if doesn't exist.
     * @param method Publication method. See {@link UploadMethod}.
     * @param extension Dataset format. One of {@link DataStoreExtension} for vector data, or one of {@link CoverageStoreExtension} for raster data.
     * @param mimeType Dataset MIME type.
     * @param uri The data location. A local path for {@link UploadMethod#FILE}, a server path for {@link UploadMethod#EXTERNAL}, or a URL for
     *        {@link UploadMethod#URL}.
     * @param configure Indicate if associated feature types will be automatically configured. See {@link ParameterConfigure}.
     * @param params Other parameters to be included in request, such as "update", "charset" or "coverageName". See GeoServer REST documentation for
     *        details: <a href= "http://docs.geoserver.org/latest/en/user/restconfig/rest-config-api.html#id2" >dataStore</a> and <a href=
     *        "http://docs.geoserver.org/latest/en/user/restconfig/rest-config-api.html#id4" >coverageStore</a> operations.
     * @return {@code true} if the store was successfully created.
     * @throws FileNotFoundException if local file does not exist when using the {@link UploadMethod#FILE} method.
     * @throws IllegalArgumentException if any of the mandatory {@code workspace}, {@code dsType}, {@code storeName}, {@code method},
     *         {@code extension}, {@code mimeType} or {@code uri} parameters are {@code null}.
     */
    private boolean createStore(String workspace, StoreType dsType, String storeName,
            UploadMethod method, Enum extension, String mimeType, URI uri,
            ParameterConfigure configure, NameValuePair... params) throws FileNotFoundException,
            IllegalArgumentException {
        if (workspace == null || dsType == null || storeName == null || method == null
                || extension == null || mimeType == null || uri == null) {
            throw new IllegalArgumentException("Null argument");
        }
        StringBuilder sbUrl = new StringBuilder(restURL).append("/rest/workspaces/")
                .append(workspace).append("/").append(dsType).append("/").append(storeName)
                .append("/").append(method).append(".").append(extension);

        if (configure != null) {
            sbUrl.append("?configure=").append(configure);
            if (params != (NameValuePair[]) null) {
                final String paramString = appendParameters(params);
                if (!paramString.isEmpty()) {
                    sbUrl.append("&").append(paramString);
                }
            }
        }

        String sentResult = null;

        if (method.equals(UploadMethod.FILE)) {
            final File file = new File(uri);
            if (!file.exists())
                throw new FileNotFoundException("unable to locate file: " + file);
            sentResult = HTTPUtils.put(sbUrl.toString(), file, mimeType, gsuser, gspass);
        } else if (method.equals(UploadMethod.EXTERNAL)) {
            sentResult = HTTPUtils.put(sbUrl.toString(), uri.toString(), mimeType, gsuser, gspass);
        } else if (method.equals(UploadMethod.URL)) {
            // TODO check
            sentResult = HTTPUtils.put(sbUrl.toString(), uri.toString(), mimeType, gsuser, gspass);
        }

        if (sentResult != null) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Store successfully created using ( " + uri + " )");
            return true;
        } else {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Error in creating store using: " + uri);
            return false;
        }

    }

    /**
     * Upload and publish vector data, and automatically create the associated DataStore if needed.
     * 
     * Note that this is same as {@link #createStore} but specific for vector data (that is, {@code dsType} being {@link DataStoreType#DATASTORES}).
     * 
     * @param workspace workspace where data will be published.
     * @param storeName DataStore name. Will be created if doesn't exist.
     * @param method Publication method. See {@link UploadMethod}.
     * @param extension Dataset format. One of {@link DataStoreExtension}.
     * @param mimeType Dataset MIME type.
     * @param uri The data location. A local path for {@link UploadMethod#FILE}, a server path for {@link UploadMethod#EXTERNAL}, or a URL for
     *        {@link UploadMethod#URL}.
     * @param configure Indicate if associated feature types will be automatically configured. See {@link ParameterConfigure}.
     * @param params Other parameters to be included in request, such as "update" or "charset". See GeoServer REST documentation for details: <a href=
     *        "http://docs.geoserver.org/latest/en/user/restconfig/rest-config-api.html#id2" >dataStore operations</a>.
     * @return {@code true} if the store was successfully created.
     * @throws FileNotFoundException if local file does not exist when using the {@link UploadMethod#FILE} method.
     * @throws IllegalArgumentException if any of the mandatory {@code workspace}, {@code storeName}, {@code method}, {@code extension},
     *         {@code mimeType} or {@code uri} parameters are {@code null}.
     */
    private boolean createDataStore(String workspace, String storeName, UploadMethod method,
            DataStoreExtension extension, String mimeType, URI uri, ParameterConfigure configure,
            NameValuePair... params) throws FileNotFoundException, IllegalArgumentException {
        return createStore(workspace, StoreType.DATASTORES, storeName, method, extension, mimeType,
                uri, configure, params);
    }

    /**
     * Upload and publish raster data, and automatically create the associated CoverageStore if needed.
     * 
     * Note that this is same as {@link #createStore} but specific for raster data (that is, {@code dsType} being {@link DataStoreType#COVERAGESTORES}
     * ).
     * 
     * @param workspace workspace where data will be published.
     * @param storeName CoverageStore name. Will be created if doesn't exist.
     * @param method Publication method. See {@link UploadMethod}.
     * @param extension Dataset format. One of {@link CoverageStoreExtension}.
     * @param mimeType Dataset MIME type.
     * @param uri The data location. A local path for {@link UploadMethod#FILE}, a server path for {@link UploadMethod#EXTERNAL}, or a URL for
     *        {@link UploadMethod#URL}.
     * @param configure Indicate if associated feature types will be automatically configured. See {@link ParameterConfigure}.
     * @param params Other parameters to be included in request, such as "coverageName". See GeoServer REST documentation for details: <a href=
     *        "http://docs.geoserver.org/latest/en/user/restconfig/rest-config-api.html#id4" >coverageStore operations</a>.
     * @return {@code true} if the store was successfully created.
     * @throws FileNotFoundException if local file does not exist when using the {@link UploadMethod#FILE} method.
     * @throws IllegalArgumentException if any of the mandatory {@code workspace}, {@code storeName}, {@code method}, {@code extension},
     *         {@code mimeType} or {@code uri} parameters are {@code null}.
     */
    private boolean createCoverageStore(String workspace, String storeName, UploadMethod method,
            CoverageStoreExtension extension, String mimeType, URI uri,
            ParameterConfigure configure, NameValuePair... params) throws FileNotFoundException,
            IllegalArgumentException {
        return createStore(workspace, StoreType.COVERAGESTORES, storeName, method, extension,
                mimeType, uri, configure, params);
    }

    /**
     * Create a PostGIS datastore.
     * 
     * @deprecated Will be deleted in next version 1.5.x, use {@link GeoServerRESTDatastoreManager} instead.
     * 
     * @param workspace Name of the workspace to contain the database. This will also be the prefix of any layer names created from tables in the
     *        database.
     * @param datastoreEncoder the set of parameters to be set to the datastore (including connection params).
     * 
     * @return <TT>true</TT> if the PostGIS datastore has been successfully created, <TT>false</TT> otherwise
     */
    public boolean createPostGISDatastore(String workspace,
            GSPostGISDatastoreEncoder datastoreEncoder) {
        String sUrl = restURL + "/rest/workspaces/" + workspace + "/datastores/";
        String xml = datastoreEncoder.toString();
        String result = HTTPUtils.postXml(sUrl, xml, gsuser, gspass);
        return result != null;
    }

    /**
     * @deprecated Will be removed in the next release.
     */
    public boolean publishDBLayer(String workspace, String storename, String layername, String srs,
            String defaultStyle) {

        final GSFeatureTypeEncoder fte = new GSFeatureTypeEncoder();

        fte.setProjectionPolicy(ProjectionPolicy.REPROJECT_TO_DECLARED);
        fte.addKeyword("KEYWORD");
        fte.setTitle(layername);
        fte.setName(layername);
        fte.setSRS(srs); // srs=null?"EPSG:4326":srs);
        final GSLayerEncoder layerEncoder = new GSLayerEncoder();
        layerEncoder.setDefaultStyle(defaultStyle);
        return publishDBLayer(workspace, storename, fte, layerEncoder);
    }

    /**
     * Publish and configure a new layer from an existing DataStore (v. gr. a layer from a DB table).
     * 
     * @param workspace Workspace name where DataStore is.
     * @param storename DataStore name.
     * @param fte FeatureType configuration details using a {@link GSFeatureTypeEncoder}.
     * @return {@code true} if layer is successfully created.
     */
    public boolean publishDBLayer(final String workspace, final String storename,
            final GSFeatureTypeEncoder fte, final GSLayerEncoder layerEncoder) {
        /*
         * This is the equivalent call with cUrl:
         * 
         * {@code curl -u admin:geoserver -XPOST -H 'Content-type: text/xml' \ -d
         * "<featureType><name>easia_gaul_1_aggr</name><nativeCRS>EPSG:4326</nativeCRS><enabled>true</enabled></featureType>" \
         * http://localhost:8080/geoserver/rest/workspaces/it.geosolutions/ datastores/pg_kids/featuretypes }
         * 
         * and a PUT to <BR> restURL + "/rest/layers/" workspace + : + layerName
         */
        String ftypeXml = fte.toString();
        StringBuilder postUrl = new StringBuilder(restURL).append("/rest/workspaces/")
                .append(workspace).append("/datastores/").append(storename).append("/featuretypes");

        final String layername = fte.getName();
        if (layername == null || layername.isEmpty()) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("GSFeatureTypeEncoder has no valid name associated, try using GSFeatureTypeEncoder.setName(String)");
            return false;
        }

        String configuredResult = HTTPUtils.postXml(postUrl.toString(), ftypeXml, this.gsuser,
                this.gspass);
        boolean published = configuredResult != null;
        boolean configured = false;

        if (!published) {
            LOGGER.warn("Error in publishing (" + configuredResult + ") " + workspace + ":"
                    + storename + "/" + layername);
        } else {
            LOGGER.info("DB layer successfully added (layer:" + layername + ")");

            if (layerEncoder == null) {
                if (LOGGER.isErrorEnabled())
                    LOGGER.error("GSLayerEncoder is null: Unable to find the defaultStyle for this layer");
                return false;
            }

            configured = configureLayer(workspace, layername, layerEncoder);

            if (!configured) {
                LOGGER.warn("Error in configuring (" + configuredResult + ") " + workspace + ":"
                        + storename + "/" + layername);
            } else {
                LOGGER.info("DB layer successfully configured (layer:" + layername + ")");
            }
        }

        return published && configured;
    }

    // ==========================================================================
    // === SHAPEFILES
    // ==========================================================================

    /**
     * Upload an publish a local shapefile.
     * <P>
     * The SRS will be set to EPSG:4326.
     * 
     * @see {@link #publishShp(String, String, NameValuePair[], String, UploadMethod, URI, String, ProjectionPolicy, String)}
     * 
     * @param workspace The workspace name.
     * @param storename The store name.
     * @param layername The layer name.
     * @param zipFile The zipped file to publish.
     * 
     * @return {@code true} if the operation completed successfully.
     * @throws FileNotFoundException , IllegalArgumentException
     */
    public boolean publishShp(String workspace, String storename, String datasetname, File zipFile)
            throws FileNotFoundException, IllegalArgumentException {
        return publishShp(workspace, storename, new NameValuePair[0], datasetname,
                UploadMethod.FILE, zipFile.toURI(), DEFAULT_CRS, null);
    }

    /**
     * Publish a shapefile.
     * 
     * @param workspace the name of the workspace to use
     * @param storename the name of the store to create
     * @param storeParams parameters to append to the url (can be null).<br>
     *        Accepted parameters are:<br>
     *        <ul>
     *        <li><b>charset</b> used to set the charset</li>
     *        </ul>
     * @param layername the name of the layer to configure
     * @param method {@link UploadMethod}
     * @param fileUri the uri of the file containing the shapefile.It should be:
     *        <ul>
     *        <li>A zip file if 'method' is file</li>
     *        <li>A shp file if 'method' is external</li>
     *        <li>A zip file if 'method' is uri (UNTESTED)</li>
     *        </ul>
     * @param srs the SRS for this shapefile. It must be an ESPG code or GeoServer will choke.
     * @param nativeCRS the nativeCRS for this shapefile. It can be an EPSG code (for {@link ProjectionPolicy#NONE} or a WKT for
     *        {@link ProjectionPolicy#REPROJECT_TO_DECLARED}.
     * @param policy {@link ProjectionPolicy}
     * @param defaultStyle the default style to set (can be null).
     * @return true if success false otherwise
     * 
     * @throws FileNotFoundException if file to upload is not found
     * @throws IllegalArgumentException if any of the mandatory arguments are {@code null}.
     */
    public boolean publishShp(String workspace, String storeName, NameValuePair[] storeParams,
            String datasetName, UploadMethod method, URI shapefile, String srs, String nativeCRS,
            ProjectionPolicy policy, String defaultStyle) throws FileNotFoundException,
            IllegalArgumentException {
        if (workspace == null || storeName == null || shapefile == null || datasetName == null
                || policy == null) {
            throw new IllegalArgumentException("Unable to run: null parameter");
        }

        //
        // SRS Policy Management
        //
        boolean srsNull = !(srs != null && srs.length() != 0);
        boolean nativeSrsNull = !(nativeCRS != null && nativeCRS.length() != 0);
        // if we are asking to use the reproject policy we must have the native crs
        if (policy == ProjectionPolicy.REPROJECT_TO_DECLARED && (nativeSrsNull || srsNull)) {
            throw new IllegalArgumentException(
                    "Unable to run: you can't ask GeoServer to reproject while not specifying a native CRS");
        }

        // if we are asking to use the NONE policy we must have the native crs.
        if (policy == ProjectionPolicy.NONE && nativeSrsNull) {
            throw new IllegalArgumentException(
                    "Unable to run: you can't ask GeoServer to use a native srs which is null");
        }

        // if we are asking to use the reproject policy we must have the native crs
        if (policy == ProjectionPolicy.FORCE_DECLARED && srsNull) {
            throw new IllegalArgumentException(
                    "Unable to run: you can't force GeoServer to use an srs which is null");
        }

        //
        final String mimeType;
        switch (method) {
        case EXTERNAL:
        case external:
            mimeType = "text/plain";
            break;
        case URL: // TODO check which mime-type should be used
        case FILE:
        case file:
        case url:
            mimeType = "application/zip";
            break;
        default:
            mimeType = null;
        }
        if (!createDataStore(workspace,
                (storeName != null) ? storeName : FilenameUtils.getBaseName(shapefile.toString()),
                method, DataStoreExtension.SHP, mimeType, shapefile, ParameterConfigure.NONE,
                storeParams)) {
            LOGGER.error("Unable to create data store for shapefile: " + shapefile);
            return false;
        }

        // config coverage props (srs)
        final GSFeatureTypeEncoder featureTypeEncoder = new GSFeatureTypeEncoder();
        featureTypeEncoder.setName(datasetName);
        featureTypeEncoder.setTitle(datasetName);
        // set destination srs
        if (!srsNull) {
            featureTypeEncoder.setSRS(srs);
        } else {
            // this under the assumption that when the destination srs is null the nativeCRS has an EPSG one so we force them to be the same
            featureTypeEncoder.setSRS(nativeCRS);
        }
        // set native srs
        if (!nativeSrsNull) {
            featureTypeEncoder.setNativeCRS(nativeCRS);
        }
        featureTypeEncoder.setProjectionPolicy(policy);

        if (!createResource(workspace, StoreType.DATASTORES, storeName, featureTypeEncoder)) {
            LOGGER.error("Unable to create a coverage store for coverage: " + shapefile);
            return false;
        }

        // config layer props (style, ...)
        final GSLayerEncoder layerEncoder = configureDefaultStyle(defaultStyle);

        return configureLayer(workspace, datasetName, layerEncoder);
    }

    private GSLayerEncoder configureDefaultStyle(String defaultStyle) {
        final GSLayerEncoder layerEncoder = new GSLayerEncoder();
        if (defaultStyle != null && !defaultStyle.isEmpty()) {
            if(defaultStyle.indexOf(":") != -1) {
                String[] wsAndName = defaultStyle.split(":");
                layerEncoder.setDefaultStyle(wsAndName[0], wsAndName[1]);
            } else {
                layerEncoder.setDefaultStyle(defaultStyle);
            }
        }
        return layerEncoder;
    }

    /**
     * Publish a shapefile.
     * 
     * @param workspace the name of the workspace to use
     * @param storename the name of the store to create
     * @param storeParams parameters to append to the url (can be null).<br>
     *        Accepted parameters are:<br>
     *        <ul>
     *        <li><b>charset</b> used to set the charset</li>
     *        </ul>
     * @param layername the name of the layer to configure
     * @param method {@link UploadMethod}
     * @param fileUri the uri of the file containing the shapefile.It should be:
     *        <ul>
     *        <li>A zip file if 'method' is file</li>
     *        <li>A shp file if 'method' is external</li>
     *        <li>A zip file if 'method' is uri (UNTESTED)</li>
     *        </ul>
     * @param srs the SRS for this shapefile. It must be an ESPG code or GeoServer will choke. Notice that we can only use
     *        {@link ProjectionPolicy#FORCE_DECLARED}.
     * @param policy {@link ProjectionPolicy}
     * @param defaultStyle the default style to set (can be null).
     * @return true if success false otherwise
     * 
     * @throws FileNotFoundException if file to upload is not found
     * @throws IllegalArgumentException if any of the mandatory arguments are {@code null}.
     * @deprecated use {@link #publishShp(String, String, NameValuePair[], String, UploadMethod, URI, String, String)} instead as the behaviour of
     *             this method is misleading as it allows you to use wrong ProjectionPolicy values.
     */
    public boolean publishShp(String workspace, String storeName, NameValuePair[] storeParams,
            String datasetName, UploadMethod method, URI shapefile, String srs,
            ProjectionPolicy policy, String defaultStyle) throws FileNotFoundException,
            IllegalArgumentException {
        return publishShp(workspace, storeName, storeParams, datasetName, method, shapefile, srs,
                null, policy, defaultStyle);
    }

    /**
     * Publish a shapefile.
     * 
     * @param workspace the name of the workspace to use
     * @param storename the name of the store to create
     * @param storeParams parameters to append to the url (can be null).<br>
     *        Accepted parameters are:<br>
     *        <ul>
     *        <li><b>charset</b> used to set the charset</li>
     *        </ul>
     * @param layername the name of the layer to configure
     * @param method {@link UploadMethod}
     * @param fileUri the uri of the file containing the shapefile.It should be:
     *        <ul>
     *        <li>A zip file if 'method' is file</li>
     *        <li>A shp file if 'method' is external</li>
     *        <li>A zip file if 'method' is uri (UNTESTED)</li>
     *        </ul>
     * @param srs the SRS for this shapefile. It must be an ESPG code or GeoServer will choke.
     * @param defaultStyle the default style to set (can be null).
     * @return true if success false otherwise
     * 
     * @throws FileNotFoundException if file to upload is not found
     * @throws IllegalArgumentException if any of the mandatory arguments are {@code null}.
     */
    public boolean publishShp(String workspace, String storeName, NameValuePair[] storeParams,
            String datasetName, UploadMethod method, URI shapefile, String srs, String defaultStyle)
            throws FileNotFoundException, IllegalArgumentException {
        return publishShp(workspace, storeName, storeParams, datasetName, method, shapefile, srs,
                null, ProjectionPolicy.FORCE_DECLARED, defaultStyle);
    }

    /**
     * Publish a zipped shapefile.
     * 
     * @see {@link #publishShp(String, String, NameValuePair[], String, UploadMethod, URI, String, ProjectionPolicy, String)}
     * 
     * @param workspace the name of the workspace to use
     * @param storename the name of the store to create
     * @param layerName the name of the layer to configure
     * @param zipFile The zipped file to publish
     * @param srs the srs for this shapefile. It will be forced to use this one in GeoServer using {@link ProjectionPolicy#FORCE_DECLARED}.
     * @param defaultStyle the default style to set (can be null).
     * 
     * @return {@code true} if the operation completed successfully
     * @throws FileNotFoundException if file to upload is not found
     * @throws IllegalArgumentException if any of the mandatory arguments are {@code null}.
     */
    public boolean publishShp(String workspace, String storename, String layerName, File zipFile,
            String srs, String defaultStyle) throws FileNotFoundException, IllegalArgumentException {

        return publishShp(workspace, storename, (NameValuePair[]) null, layerName,
                UploadMethod.FILE, zipFile.toURI(), srs, defaultStyle);
    }

    /**
     * Publish a zipped shapefile forcing the srs to the one provided.
     * 
     * @see {@link #publishShp(String, String, NameValuePair[], String, UploadMethod, URI, String, ProjectionPolicy, String)}
     * 
     * @param workspace the name of the workspace to use
     * @param storename the name of the store to create
     * @param layername the name of the layer to configure
     * @param zipFile The zipped file to publish
     * @param srs the CRS for this shapefile. It must be an EPSG CODE !
     * 
     * @return {@code true} if the operation completed successfully.
     * @throws FileNotFoundException if file to upload is not found
     * @throws IllegalArgumentException if any of the mandatory arguments are {@code null}.
     */
    public boolean publishShp(String workspace, String storename, String layername, File zipFile,
            String srs) throws FileNotFoundException {
        return publishShp(workspace, storename, (NameValuePair[]) null, layername,
                UploadMethod.FILE, zipFile.toURI(), srs, null);
    }

    /**
     * Publish a zipped shapefile.
     * 
     * @see {@link #publishShp(String, String, NameValuePair[], String, UploadMethod, URI, String, ProjectionPolicy, String)}
     * 
     * @param workspace the name of the workspace to use
     * @param storename the name of the store to create
     * @param layername the name of the layer to configure
     * @param zipFile the zip file containing the shapefile
     * @param srs the shapefile srs. This must be an EPSG Codefor this code to work!
     * @param params parameters to append to the url (can be null).<br>
     *        Accepted parameters are:<br>
     *        <ul>
     *        <li><b>charset</b> used to set the charset</li>
     *        </ul>
     * @return {@code true} if the operation completed successfully.
     * @throws FileNotFoundException if file to upload is not found
     * @throws IllegalArgumentException if any of the mandatory arguments are {@code null}.
     */
    public boolean publishShp(String workspace, String storename, String layername, File zipFile,
            String srs, NameValuePair... params) throws FileNotFoundException,
            IllegalArgumentException {

        return publishShp(workspace, storename, params, layername, UploadMethod.FILE,
                zipFile.toURI(), srs, null);
    }

    /**
     * Publish a collection of shapefiles.
     * <P>
     * Will automatically create the store and publish each shapefile as a layer.
     * 
     * @param workspace the name of the workspace to use
     * @param storeName the name of the store to create
     * @param resource the shapefile collection. It can be:
     *        <ul>
     *        <li>A path to a directory containing shapefiles in the server. <li>A local zip file containing shapefiles that will be uploaded. <li>A
     *        URL pointing to a shapefile collection in the wild web (not tested).
     *        </ul>
     * @return {@code true} if publication successful.
     * @throws FileNotFoundException if the specified zip file does not exist.
     */
    public boolean publishShpCollection(String workspace, String storeName, URI resource)
            throws FileNotFoundException {

        // Deduce upload method & mime type from resource syntax.
        UploadMethod method = null;
        String mime = null;
        if (resource.getScheme().equals("file") || resource.isAbsolute() == false) {
            File f = new File(resource);
            if (f.exists() && f.isFile() && f.toString().endsWith(".zip")) {
                method = UploadMethod.FILE;
                mime = "application/zip";
            } else if (f.isDirectory()) {
                method = UploadMethod.EXTERNAL;
                mime = "text/plain";
            }
        } else {
            try {
                if (resource.toURL() != null) {
                    method = UploadMethod.URL;
                    mime = "text/plain";
                }
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(
                        "Resource is not recognized as a zip file, or a directory, or a valid URL",
                        e);
            }
        }

        // Create store, upload data, and publish layers
        return createStore(workspace, StoreType.DATASTORES, storeName, method,
                DataStoreExtension.SHP, mime, resource, ParameterConfigure.ALL,
                new NameValuePair[0]);
    }

    // ==========================================================================
    // === COVERAGES
    // ==========================================================================

    /**
     * Controls how existing data is handled when the file is PUT into a datastore that (a) already exists and (b) already contains a schema that
     * matches the content of the file. It can take one of the two values:
     * <ul>
     * <li>{@link #APPEND} Data being uploaded is appended to the existing data. This is the default.
     * <li>{@link #OVERWRITE} Data being uploaded replaces any existing data.
     * </ul>
     * 
     * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
     */
    public static enum ParameterUpdate {
        /** Data being uploaded is appended to the existing data. */
        APPEND,
        /** Data being uploaded replaces any existing data. */
        OVERWRITE;

        /**
         * Returns a lowercase representation of the parameter. Useful when constructing the REST request.
         */
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    /**
     * Represents the format used to GET, PUT or POST information via REST. For example consider the resource "foo". To request a representation of
     * foo as XML the request uri would end with "foo.xml". To request as JSON the request uri would end with "foo.json". When no format is specified
     * the server will use its own internal format, usually html.
     * <P>
     * In a POST or PUT operation the format specifies 1) the representatin of the content being sent to the server, and 2) the representation of the
     * response to be sent back. The former is specified with the Content-type header. To send a representation in XML, the content type "text/xml" or
     * "application/xml" would be used. The latter is specified with the Accepts header as specified in the above paragraph describing a GET
     * operation.
     * <P>
     * The following table defines the Content-type values for each format:
     * <ul>
     * <li>XML (application/xml)</li>
     * <li>JSON (application/json)</li>
     * <li>HTML (application/html)</li>
     * <li>SLD (application/vnd.ogc.sld+xml)</li>
     * </ul>
     */
    public enum Format {
        XML, JSON, HTML, SLD, SLD_1_1_0;

        /**
         * Gets the mime type from a format.
         * 
         * @param f the format key.
         * @return The content-type (mime), or {@code null} if not in the enum.
         */
        public static String getContentType(Format f) {
            switch (f) {
            case XML:
                return "application/xml";
            case HTML:
                return "application/html";
            case JSON:
                return "application/json";
            case SLD:
                return "application/vnd.ogc.sld+xml";
            case SLD_1_1_0:
                return "application/vnd.ogc.se+xml";
            default:
                return null;
            }
        }

        /**
         * Gets the mime type from a format.
         * 
         * @param f the format key.
         * @return The content-type (mime), or {@code null} if not in the enum.
         */
        public String getContentType() {
            return getContentType(this);
        }

        /**
         * Returns a lowercase representation of the parameter. Useful when constructing the REST request.
         */
        @Override
        public String toString() {
            return this.name().toLowerCase();
        }
    }

    /**
     * Upload and publish a raster file.
     * 
     * @param workspace Workspace to use
     * @param coveragestore Name of the coveragestore
     * @param file file to upload
     * @param configure Configure parameter. See {@link ParameterConfigure}. It can be null.
     * @param params parameters to append to the url (can be null).<br>
     *        Accepted parameters are:
     *        <ul>
     *        <li><b>coverageName=name</b> coverageName parameter to append. Only works if configure is not set to ParameterConfigure.NONE.</li>
     *        </ul>
     * @return true if the operation completed successfully.
     */
    private boolean publishCoverage(String workspace, String coveragestore,
            CoverageStoreExtension extension, String mimeType, File file,
            ParameterConfigure configure, NameValuePair... params) throws FileNotFoundException {
        /*
         * This is an example with cUrl:
         * 
         * {@code curl -u admin:geoserver -XPUT -H 'Content-type: application/zip' \ --data-binary @$ZIPFILE \ http://$GSIP:$GSPORT/$SERVLET
         * /rest/workspaces/$WORKSPACE/coveragestores /$COVERAGESTORE/file.worldimage
         */
        return createCoverageStore(workspace, coveragestore, UploadMethod.FILE, extension,
                mimeType, file.toURI(), configure, params);
    }

    /**
     * Publish a raster file local to the server.
     * 
     * @param workspace Workspace to use
     * @param coveragestore Name of the coveragestore
     * @param file absolute path to the file location in the server
     * @param configure Configure parameter. It may be null.
     * @param update Accepted parameters are:
     *        <ul>
     *        <li>See <b>{@link #ParameterUpdate}</b></li>
     *        </ul>
     * @see #{@link ParameterConfigure}
     * @return true if the operation completed successfully.
     * @throws IllegalArgumentException
     * @throws FileNotFoundException
     */
    private boolean publishExternalCoverage(String workspace, String coveragestore,
            CoverageStoreExtension extension, String mimeType, File file,
            ParameterConfigure configure, ParameterUpdate update) throws FileNotFoundException,
            IllegalArgumentException {
        /*
         * Curl example:
         * 
         * {@code curl -u admin:geoserver -XPUT -H 'Content-type: application/zip' \
         * 
         * --data-binary @$ZIPFILE \
         * 
         * http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/coveragestores /$COVERAGESTORE/file.worldimage
         */
        return createCoverageStore(
                workspace,
                coveragestore,
                UploadMethod.EXTERNAL,
                extension,
                mimeType,
                file.toURI(),
                configure,
                (update != null) ? new NameValuePair[] { new NameValuePair("update", update
                        .toString()) } : (NameValuePair[]) null);
    }

    // ==========================================================================
    // === ARCGRID
    // ==========================================================================

    /**
     * Upload and publish a ArcGrid image.
     *
     * @param workspace Workspace to use
     * @param storeName The store name to be used or created.
     * @param arcgrid The ArcGrid file.
     * @return true if success.
     * @throws FileNotFoundException if ArcGrid file does not exist.
     */
    public boolean publishArcGrid(String workspace, String storeName, File arcgrid)
            throws FileNotFoundException {
        return publishCoverage(workspace, storeName, CoverageStoreExtension.ARCGRID,
                "image/arcgrid", arcgrid, ParameterConfigure.FIRST, (NameValuePair[]) null);
    }

    /**
     * Upload and publish a ArcGrid image.
     *
     * @param workspace Workspace to use
     * @param storeName Name of the coveragestore (if null the file name will be used)
     * @param coverageName the name of the coverage (if null the file name will be used)
     * @param arcgrid file to upload
     * @return true if the operation completed successfully.
     * @throws FileNotFoundException if file does not exists
     * @throws IllegalArgumentException if workspace or arcgrid are null
     */
    public boolean publishArcGrid(final String workspace, final String storeName,
                                  final String coverageName, final File arcgrid) throws FileNotFoundException,
            IllegalArgumentException {
        if (workspace == null || arcgrid == null)
            throw new IllegalArgumentException("Unable to proceed, some arguments are null");

        return publishCoverage(
                workspace,
                (storeName != null) ? storeName : FilenameUtils.getBaseName(arcgrid
                        .getAbsolutePath()), CoverageStoreExtension.ARCGRID, "image/arcgrid",
                arcgrid, ParameterConfigure.FIRST,
                (coverageName != null) ? new NameValuePair[] { new NameValuePair("coverageName",
                        coverageName) } : (NameValuePair[]) null);
    }

    /**
     * Same as {@link #publishArcGrid(String, String, String, File, String, ProjectionPolicy, String, double[])} but without the last parameter
     * (bbox). Kept here for backwards compatibility.
     *
     * @deprecated use the former method with bbox set to null.
     */
    public boolean publishArcGrid(String workspace, String storeName, String resourceName,
                                  File arcgrid, String srs, ProjectionPolicy policy, String defaultStyle)
            throws FileNotFoundException, IllegalArgumentException {
        return publishArcGrid(workspace, storeName, resourceName, arcgrid, srs, policy,
                defaultStyle, null);
    }

    /**
     * Upload and publish a ArcGrid image.
     *
     * @param workspace Workspace to use
     * @param storeName Name of the coveragestore (if null the file name will be used)
     * @param coverageName the name of the coverage (if null the file name will be used)
     * @param arcgrid file to upload
     * @param srs the native CRS
     * @param policy projection policy. See {@link ProjectionPolicy}.
     * @param defaultStyle the default style to apply.
     * @param bbox An array of 4 doubles indicating envelope in EPSG:4326. Order is [Xmin, Ymin, Xmax, Ymax].
     * @return true if the operation completed successfully.
     * @throws FileNotFoundException if file does not exists
     * @throws IllegalArgumentException if workspace or arcgrid are null
     *
     */
    public boolean publishArcGrid(String workspace, String storeName, String coverageName,
                                  File arcgrid, String srs, ProjectionPolicy policy, String defaultStyle, double[] bbox)
            throws FileNotFoundException, IllegalArgumentException {
        if (workspace == null || storeName == null || arcgrid == null || coverageName == null
                || srs == null || policy == null || defaultStyle == null)
            throw new IllegalArgumentException("Unable to run: null parameter");

        if (!createCoverageStore(
                workspace,
                (storeName != null) ? storeName : FilenameUtils.getBaseName(arcgrid
                        .getAbsolutePath()), UploadMethod.FILE, CoverageStoreExtension.ARCGRID,
                "image/arcgrid", arcgrid.toURI(), ParameterConfigure.NONE, (NameValuePair[]) null)) {
            LOGGER.error("Unable to create coverage store for coverage: " + arcgrid);
            return false;
        }

        // config coverage props (srs)
        final GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
        coverageEncoder.setName(coverageName);
        coverageEncoder.setTitle(coverageName);
        coverageEncoder.setSRS(srs);
        coverageEncoder.setNativeFormat("ArcGrid");
        coverageEncoder.addSupportedFormats("ARCGRID");
        coverageEncoder.addKeyword("arcGrid");
        coverageEncoder.addKeyword("WCS");
        coverageEncoder.setNativeCRS(srs);
        coverageEncoder.setProjectionPolicy(policy);
        coverageEncoder.setRequestSRS(srs);
        coverageEncoder.setResponseSRS(srs);
        if (bbox != null && bbox.length == 4) {
            coverageEncoder.setLatLonBoundingBox(bbox[0], bbox[1], bbox[2], bbox[3], DEFAULT_CRS);
        }

        if (!createCoverage(workspace, storeName, coverageEncoder)) {
            LOGGER.error("Unable to create a coverage store for coverage: " + arcgrid);
            return false;
        }

        // config layer props (style, ...)
        final GSLayerEncoder layerEncoder = configureDefaultStyle(defaultStyle);

        return configureLayer(workspace, coverageName, layerEncoder);
    }

    /**
     * Publish a ArcGrid already in a filesystem readable by GeoServer.
     *
     * @param workspace an existing workspace
     * @param storeName the coverageStore to be created
     * @param arcgrid the arcGrid to be published
     * @param srs the native CRS
     * @param policy projection policy. See {@link ProjectionPolicy}.
     * @param defaultStyle the default style to apply.
     *
     * @return true if the operation completed successfully.
     * @throws FileNotFoundException if file does not exists
     * @throws IllegalArgumentException if any of the mandatory parameters are null.
     */
    public boolean publishExternalArcGrid(String workspace, String storeName, File arcgrid,
                                          String coverageName, String srs, ProjectionPolicy policy, String defaultStyle)
            throws FileNotFoundException, IllegalArgumentException {
        if (workspace == null || storeName == null || arcgrid == null || coverageName == null
                || srs == null || policy == null || defaultStyle == null)
            throw new IllegalArgumentException("Unable to run: null parameter");

        // config coverage props (srs)
        final GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
        coverageEncoder.setName(coverageName);
        coverageEncoder.setTitle(coverageName);
        coverageEncoder.setSRS(srs);
        coverageEncoder.setProjectionPolicy(policy);

        // config layer props (style, ...)
        final GSLayerEncoder layerEncoder = new GSLayerEncoder();
        layerEncoder.setDefaultStyle(defaultStyle);

        return publishExternalArcGrid(workspace, storeName, arcgrid, coverageEncoder, layerEncoder) != null ? true
                : false;
    }

    /**
     * Publish a ArcGrid already in a filesystem readable by GeoServer.
     *
     * @param workspace an existing workspace
     * @param storeName the coverageStore to be created
     * @param arcgrid the arcGrid to be published
     * @param coverageEncoder coverage details. See {@link GSCoverageEncoder}.
     * @param layerEncoder layer details, See {@link GSLayerEncoder}.
     *
     * @return true if the operation completed successfully.
     * @throws FileNotFoundException if file does not exists
     * @throws IllegalArgumentException if any of the mandatory parameters are null.
     */
    public RESTCoverageStore publishExternalArcGrid(final String workspace, final String storeName,
                                                    final File arcgrid, final GSCoverageEncoder coverageEncoder,
                                                    final GSLayerEncoder layerEncoder) throws IllegalArgumentException,
            FileNotFoundException {

        if (workspace == null || arcgrid == null || storeName == null || layerEncoder == null
                || coverageEncoder == null)
            throw new IllegalArgumentException("Unable to run: null parameter");

        final String coverageName = coverageEncoder.getName();
        if (coverageName.isEmpty()) {
            throw new IllegalArgumentException("Unable to run: empty coverage store name");
        }

        // create store
        final boolean store = publishExternalCoverage(workspace, storeName,
                CoverageStoreExtension.ARCGRID, "text/plain", arcgrid, ParameterConfigure.NONE,
                ParameterUpdate.OVERWRITE);
        if (!store) {
            return null;
        }

        // create Coverage Store
        if (!createCoverage(workspace, storeName, coverageEncoder)) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Unable to create a coverage for the store:" + coverageName);
            return null;
        }

        // create Layer
        if (configureLayer(workspace, coverageName, layerEncoder)) {
            GeoServerRESTReader reader;
            try {
                reader = new GeoServerRESTReader(this.restURL, this.gsuser, this.gspass);
                return reader.getCoverageStore(workspace, storeName);
            } catch (MalformedURLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

    // ==========================================================================
    // === GEOTIFF
    // ==========================================================================

    /**
     * Upload and publish a GeoTIFF image.
     * 
     * @param workspace Workspace to use
     * @param storeName The store name to be used or created.
     * @param geotiff The GeoTIFF file.
     * @return true if success.
     * @throws FileNotFoundException if GeoTIFF file does not exist.
     */
    public boolean publishGeoTIFF(String workspace, String storeName, File geotiff)
            throws FileNotFoundException {
        return publishCoverage(workspace, storeName, CoverageStoreExtension.GEOTIFF,
                "image/geotiff", geotiff, ParameterConfigure.FIRST, (NameValuePair[]) null);
    }

    /**
     * Upload and publish a GeoTIFF image.
     * 
     * @param workspace Workspace to use
     * @param storeName Name of the coveragestore (if null the file name will be used)
     * @param coverageName the name of the coverage (if null the file name will be used)
     * @param geotiff file to upload
     * @return true if the operation completed successfully.
     * @throws FileNotFoundException if file does not exists
     * @throws IllegalArgumentException if workspace or geotiff are null
     */
    public boolean publishGeoTIFF(final String workspace, final String storeName,
            final String coverageName, final File geotiff) throws FileNotFoundException,
            IllegalArgumentException {
        if (workspace == null || geotiff == null)
            throw new IllegalArgumentException("Unable to proceed, some arguments are null");

        return publishCoverage(
                workspace,
                (storeName != null) ? storeName : FilenameUtils.getBaseName(geotiff
                        .getAbsolutePath()), CoverageStoreExtension.GEOTIFF, "image/geotiff",
                geotiff, ParameterConfigure.FIRST,
                (coverageName != null) ? new NameValuePair[] { new NameValuePair("coverageName",
                        coverageName) } : (NameValuePair[]) null);
    }

    /**
     * Same as {@link #publishGeoTIFF(String, String, String, File, String, ProjectionPolicy, String, double[])} but without the last parameter
     * (bbox). Kept here for backwards compatibility.
     * 
     * @deprecated use the former method with bbox set to null.
     */
    public boolean publishGeoTIFF(String workspace, String storeName, String resourceName,
            File geotiff, String srs, ProjectionPolicy policy, String defaultStyle)
            throws FileNotFoundException, IllegalArgumentException {
        return publishGeoTIFF(workspace, storeName, resourceName, geotiff, srs, policy,
                defaultStyle, null);
    }

    /**
     * Upload and publish a GeoTIFF image.
     * 
     * @param workspace Workspace to use
     * @param storeName Name of the coveragestore (if null the file name will be used)
     * @param coverageName the name of the coverage (if null the file name will be used)
     * @param geotiff file to upload
     * @param srs the native CRS
     * @param policy projection policy. See {@link ProjectionPolicy}.
     * @param defaultStyle the default style to apply.
     * @param bbox An array of 4 doubles indicating envelope in EPSG:4326. Order is [Xmin, Ymin, Xmax, Ymax].
     * @return true if the operation completed successfully.
     * @throws FileNotFoundException if file does not exists
     * @throws IllegalArgumentException if workspace or geotiff are null
     * 
     */
    public boolean publishGeoTIFF(String workspace, String storeName, String coverageName,
            File geotiff, String srs, ProjectionPolicy policy, String defaultStyle, double[] bbox)
            throws FileNotFoundException, IllegalArgumentException {
        if (workspace == null || storeName == null || geotiff == null || coverageName == null
                || srs == null || policy == null || defaultStyle == null)
            throw new IllegalArgumentException("Unable to run: null parameter");

        if (!createCoverageStore(
                workspace,
                (storeName != null) ? storeName : FilenameUtils.getBaseName(geotiff
                        .getAbsolutePath()), UploadMethod.FILE, CoverageStoreExtension.GEOTIFF,
                "image/geotiff", geotiff.toURI(), ParameterConfigure.NONE, (NameValuePair[]) null)) {
            LOGGER.error("Unable to create coverage store for coverage: " + geotiff);
            return false;
        }

        // config coverage props (srs)
        final GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
        coverageEncoder.setName(coverageName);
        coverageEncoder.setTitle(coverageName);
        coverageEncoder.setSRS(srs);
        coverageEncoder.setNativeFormat("GeoTIFF");
        coverageEncoder.addSupportedFormats("GEOTIFF");
        coverageEncoder.addKeyword("geoTiff");
        coverageEncoder.addKeyword("WCS");
        coverageEncoder.setNativeCRS(srs);
        coverageEncoder.setProjectionPolicy(policy);
        coverageEncoder.setRequestSRS(srs);
        coverageEncoder.setResponseSRS(srs);
        if (bbox != null && bbox.length == 4) {
            coverageEncoder.setLatLonBoundingBox(bbox[0], bbox[1], bbox[2], bbox[3], DEFAULT_CRS);
        }

        if (!createCoverage(workspace, storeName, coverageEncoder)) {
            LOGGER.error("Unable to create a coverage store for coverage: " + geotiff);
            return false;
        }

        // config layer props (style, ...)
        final GSLayerEncoder layerEncoder = configureDefaultStyle(defaultStyle);

        return configureLayer(workspace, coverageName, layerEncoder);
    }

    /**
     * Publish a GeoTiff already in a filesystem readable by GeoServer.
     * 
     * @param workspace an existing workspace
     * @param storeName the coverageStore to be created
     * @param geotiff the geoTiff to be published
     * @param srs the native CRS
     * @param policy projection policy. See {@link ProjectionPolicy}.
     * @param defaultStyle the default style to apply.
     * 
     * @return true if the operation completed successfully.
     * @throws FileNotFoundException if file does not exists
     * @throws IllegalArgumentException if any of the mandatory parameters are null.
     */
    public boolean publishExternalGeoTIFF(String workspace, String storeName, File geotiff,
            String coverageName, String srs, ProjectionPolicy policy, String defaultStyle)
            throws FileNotFoundException, IllegalArgumentException {
        if (workspace == null || storeName == null || geotiff == null || coverageName == null
                || srs == null || policy == null || defaultStyle == null)
            throw new IllegalArgumentException("Unable to run: null parameter");

        // config coverage props (srs)
        final GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
        coverageEncoder.setName(coverageName);
        coverageEncoder.setTitle(coverageName);
        coverageEncoder.setSRS(srs);
        coverageEncoder.setProjectionPolicy(policy);

        // config layer props (style, ...)
        final GSLayerEncoder layerEncoder = new GSLayerEncoder();
        layerEncoder.setDefaultStyle(defaultStyle);

        return publishExternalGeoTIFF(workspace, storeName, geotiff, coverageEncoder, layerEncoder) != null ? true
                : false;
    }

    /**
     * Publish a GeoTiff already in a filesystem readable by GeoServer.
     * 
     * @param workspace an existing workspace
     * @param storeName the coverageStore to be created
     * @param geotiff the geoTiff to be published
     * @param coverageEncoder coverage details. See {@link GSCoverageEncoder}.
     * @param layerEncoder layer details, See {@link GSLayerEncoder}.
     * 
     * @return true if the operation completed successfully.
     * @throws FileNotFoundException if file does not exists
     * @throws IllegalArgumentException if any of the mandatory parameters are null.
     */
    public RESTCoverageStore publishExternalGeoTIFF(final String workspace, final String storeName,
            final File geotiff, final GSCoverageEncoder coverageEncoder,
            final GSLayerEncoder layerEncoder) throws IllegalArgumentException,
            FileNotFoundException {

        if (workspace == null || geotiff == null || storeName == null || layerEncoder == null
                || coverageEncoder == null)
            throw new IllegalArgumentException("Unable to run: null parameter");

        final String coverageName = coverageEncoder.getName();
        if (coverageName.isEmpty()) {
            throw new IllegalArgumentException("Unable to run: empty coverage store name");
        }

        // create store
        final boolean store = publishExternalCoverage(workspace, storeName,
                CoverageStoreExtension.GEOTIFF, "text/plain", geotiff, ParameterConfigure.NONE,
                ParameterUpdate.OVERWRITE);
        if (!store) {
            return null;
        }

        // create Coverage Store
        if (!createCoverage(workspace, storeName, coverageEncoder)) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Unable to create a coverage for the store:" + coverageName);
            return null;
        }

        // create Layer
        if (configureLayer(workspace, coverageName, layerEncoder)) {
            GeoServerRESTReader reader;
            try {
                reader = new GeoServerRESTReader(this.restURL, this.gsuser, this.gspass);
                return reader.getCoverageStore(workspace, storeName);
            } catch (MalformedURLException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

    // ==========================================================================
    // === WORLDIMAGE
    // ==========================================================================

    /**
     * Publish a zipped worldimage file. It is assumed that the the zip-file contain the *.prj to set the srs.
     * 
     * @param workspace Workspace to use
     * @param coveragestore Name of the coveragestore
     * @param zipFile zip file to upload
     * 
     * @return true if the operation completed successfully.
     */
    public boolean publishWorldImage(String workspace, String coveragestore, File zipFile)
            throws FileNotFoundException {
        return publishWorldImage(workspace, coveragestore, zipFile, ParameterConfigure.FIRST,
                (NameValuePair) null);
    }

    /**
     * Publish a zipped worldimage file. It is assumed that the the zip-file contain the *.prj to set the srs.
     * 
     * @param workspace Workspace to use
     * @param coveragestore Name of the coveragestore
     * @param zipFile zip file to upload
     * @param configure Configure parameter. See {@link ParameterConfigure}. It can be null.
     * @param params parameters to append to the url (can be null).<br>
     *        Accepted parameters are:
     *        <ul>
     *        <li><b>coverageName=name</b> coverageName parameter to append. Only works if configure is not set to {@link ParameterConfigure#NONE}.</li>
     *        </ul>
     * @return true if the operation completed successfully.
     */
    public boolean publishWorldImage(String workspace, String coveragestore, File zipFile,
            ParameterConfigure configure, NameValuePair... params) throws FileNotFoundException {
        return publishCoverage(workspace, coveragestore, CoverageStoreExtension.WORLDIMAGE,
                "application/zip", zipFile, configure, params);
    }

    // ==========================================================================
    // === MOSAIC
    // ==========================================================================

    /**
     * Publish imagemosaic as zip file.
     * 
     * @param workspace Workspace to use
     * @param storeName Name of the coveragestore
     * @param zipFile file to upload
     * 
     * @return true if the operation completed successfully.
     */
    public boolean publishImageMosaic(String workspace, String storeName, File zipFile)
            throws FileNotFoundException {
        return publishCoverage(workspace, storeName, CoverageStoreExtension.IMAGEMOSAIC,
                "application/zip", zipFile, ParameterConfigure.FIRST, (NameValuePair[]) null);
    }

    /**
     * Publish imagemosaic as zip file.
     * 
     * @param workspace Workspace to use
     * @param storeName Name of the coveragestore
     * @param zipFile file to upload
     * @param configure Configure parameter. See {@link ParameterConfigure}. It can be null.
     * @param params parameters to append to the url (can be null).<br>
     *        Accepted parameters are:
     *        <ul>
     *        <li><b>coverageName=name</b> coverageName parameter to append. Only works if configure is not set to ParameterConfigure.NONE.</li>
     *        </ul>
     * 
     * @return true if the operation completed successfully.
     */
    public boolean publishImageMosaic(String workspace, String storeName, File zipFile,
            ParameterConfigure configure, NameValuePair... params) throws FileNotFoundException {
        return publishCoverage(workspace, storeName, CoverageStoreExtension.IMAGEMOSAIC,
                "application/zip", zipFile, configure, params);
    }

    /**
     * Publish a Mosaic from a filesystem currently readable by GeoServer.
     * 
     * @param workspace an existing workspace
     * @param storeName the name of the coverageStore to be created
     * @param mosaicDir the directory where the raster images are located
     * @param configure a specify if a coverage should be configured
     * 
     * @return true if the operation completed successfully.
     * @throws FileNotFoundException
     */
    public RESTCoverageStore createExternaMosaicDatastore(String workspace, String storeName,
            File mosaicDir, ParameterConfigure configure, ParameterUpdate update)
            throws FileNotFoundException {

        /*
         * Carlo (23 Nov 2011): commented out since this directory should be readable by target GeoServer not the calling client!
         */
        if (!mosaicDir.isDirectory()) {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Directory '"
                        + mosaicDir
                        + "' not exists locally. Continue: please check existance on the remote server.");
        }

        String sUrl = restURL + "/rest/workspaces/" + workspace + "/coveragestores/" + storeName
                + "/external.imagemosaic?configure=" + configure.toString() + "&update="
                + update.toString();
        String sendResult = HTTPUtils.put(sUrl, mosaicDir.toURI().toString(), "text/plain", gsuser,
                gspass);
        return RESTCoverageStore.build(sendResult);
    }

    /**
     * Publish a Mosaic already in a filesystem readable by GeoServer.
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
    public boolean publishExternalMosaic(String workspace, String storeName, File mosaicDir,
            String srs, String defaultStyle) throws FileNotFoundException {

        final GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
        coverageEncoder.setSRS(srs);
        final String name = FilenameUtils.getBaseName(mosaicDir.getName());
        coverageEncoder.setName(name);

        final GSLayerEncoder layerEncoder = new GSLayerEncoder();
        layerEncoder.setDefaultStyle(defaultStyle);

        return publishExternalMosaic(workspace, storeName, mosaicDir, coverageEncoder, layerEncoder);
    }

    /**
     * @deprecated use {@link #publishExternalMosaic(String, String, File, GSCoverageEncoder, GSLayerEncoder)}
     */
    public boolean createExternalMosaic(String workspace, String storeName, File mosaicDir,
            GSCoverageEncoder coverageEncoder, GSLayerEncoder layerEncoder)
            throws FileNotFoundException {
        return publishExternalMosaic(workspace, storeName, mosaicDir, coverageEncoder, layerEncoder);
    }

    /**
     * Publish a Mosaic already in a filesystem readable by GeoServer.
     * 
     * @param workspace an existing workspace
     * @param storeName the name of the coverageStore to be created
     * @param mosaicDir the directory where the raster images are located
     * @param coverageEncoder the set of parameters to be set to the coverage (bbox, srs, ...)
     * @param layerEncoder the set of parameters to be set to the layer (defaultstyle, wmspath, ...)
     * 
     * @return true if the operation completed successfully.
     * @throws FileNotFoundException
     */
    public boolean publishExternalMosaic(String workspace, final String storeName, File mosaicDir,
            GSCoverageEncoder coverageEncoder, GSLayerEncoder layerEncoder)
            throws FileNotFoundException, IllegalArgumentException {

        if (coverageEncoder == null) {
            throw new IllegalArgumentException("no coverageEncoder provided for mosaic "
                    + mosaicDir);
        }

        if (layerEncoder == null) {
            throw new IllegalArgumentException("no layerEncoder provided for " + mosaicDir);
        }

        RESTCoverageStore store = createExternaMosaicDatastore(workspace, storeName, mosaicDir,
                ParameterConfigure.NONE, ParameterUpdate.OVERWRITE);

        if (store == null) {
            return false;
        }
        
        // override name to match the FIRST configured coverage
        String coverageName = coverageEncoder.getName();
        if (coverageName==null){
            coverageName=mosaicDir.getName();
            coverageEncoder.setName(coverageName);
        }
        if (!createCoverage(workspace, storeName, coverageEncoder)) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Unable to create a coverage for the store:" + coverageName);
            return false;
        }
        if (!configureLayer(workspace, coverageName, layerEncoder)) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Unable to configure the Layer for the coverage:" + coverageName);
            return false;
        }
        return true;
    }

    // ==========================================================================
    // === REMOVING THINGS
    // ==========================================================================

    /**
     * Remove the Coverage configuration from GeoServer.
     * <P>
     * First, the associated layer is removed, then the Coverage configuration itself.
     * <P>
     * <B>CHECKME</B> Maybe the coveragestore has to be removed as well.
     * 
     * @return true if the operation completed successfully.
     */
    public boolean unpublishCoverage(String workspace, String storename, String layerName) {
        try {
            final String fqLayerName;

            // this null check is here only for backward compatibility.
            // workspace
            // shall be mandatory.
            if (workspace == null) {

                fqLayerName = layerName;

                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Null workspace while configuring layer : " + layerName
                            + " -- This behavior is deprecated.");
                }
            } else {
                fqLayerName = workspace + ":" + layerName;
            }
            // delete related layer
            URL deleteLayerUrl = new URL(restURL + "/rest/layers/" + fqLayerName);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Going to delete " + "/rest/layers/" + fqLayerName);
            }
            boolean layerDeleted = HTTPUtils
                    .delete(deleteLayerUrl.toExternalForm(), gsuser, gspass);
            if (!layerDeleted) {
                LOGGER.warn("Could not delete layer '" + fqLayerName + "'");
                return false;
            }
            // delete the coverage
            URL deleteCovUrl = new URL(restURL + "/rest/workspaces/" + workspace
                    + "/coveragestores/" + storename + "/coverages/" + layerName);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Going to delete " + "/rest/workspaces/" + workspace
                        + "/coveragestores/" + storename + "/coverages/" + layerName);
            }
            boolean covDeleted = HTTPUtils.delete(deleteCovUrl.toExternalForm(), gsuser, gspass);
            if (!covDeleted) {
                LOGGER.warn("Could not delete coverage " + workspace + ":" + storename + "/"
                        + layerName + ", but layer was deleted.");
            } else {
                LOGGER.info("Coverage successfully deleted " + workspace + ":" + storename + "/"
                        + layerName);
            }
            return covDeleted;

            // the covstore is still there: should we delete it?

        } catch (MalformedURLException ex) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error(ex.getLocalizedMessage(), ex);
            return false;
        }
    }

    /**
     * Removes the featuretype and the associated layer.
     * <P>
     * You may also want to {@link #removeDatastore(String, String) remove the datastore}.
     * 
     * @return true if the operation completed successfully.
     */
    public boolean unpublishFeatureType(String workspace, String storename, String layerName) {
        try {

            final String fqLayerName;
            // this null check is here only for backward compatibility.
            // workspace
            // shall be mandatory.
            if (workspace == null) {

                fqLayerName = layerName;

                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Null workspace while configuring layer : " + layerName
                            + " -- This behavior is deprecated.");
                }
            } else {
                fqLayerName = workspace + ":" + layerName;
            }
            // delete related layer
            URL deleteLayerUrl = new URL(restURL + "/rest/layers/" + fqLayerName);
            boolean layerDeleted = HTTPUtils
                    .delete(deleteLayerUrl.toExternalForm(), gsuser, gspass);
            if (!layerDeleted) {
                LOGGER.warn("Could not delete layer '" + fqLayerName + "'");
                return false;
            }
            // delete the coverage
            URL deleteFtUrl = new URL(restURL + "/rest/workspaces/" + workspace + "/datastores/"
                    + storename + "/featuretypes/" + layerName);
            boolean ftDeleted = HTTPUtils.delete(deleteFtUrl.toExternalForm(), gsuser, gspass);
            if (!ftDeleted) {
                LOGGER.warn("Could not delete featuretype " + workspace + ":" + storename + "/"
                        + layerName + ", but layer was deleted.");
            } else {
                LOGGER.info("FeatureType successfully deleted " + workspace + ":" + storename + "/"
                        + layerName);
            }

            return ftDeleted;

            // the store is still there: should we delete it?

        } catch (MalformedURLException ex) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error(ex.getLocalizedMessage(), ex);
            return false;
        }
    }

    /**
     * Remove recursively a given Datastore in a given Workspace.
     * 
     * @param workspace The name of the workspace
     * @param storename The name of the Datastore to remove.
     * @return <TT>true</TT> if the datastore was successfully removed.
     * 
     * @deprecated will be removed in next release use {@link GeoServerRESTPublisher#removeDatastore(String, String, boolean)}
     */
    public boolean removeDatastore(String workspace, String storename) {
        try {
            return removeDatastore(workspace, storename, true);
        } catch (IllegalArgumentException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Arguments may not be null or empty!", e);
        }
        return false;
    }

    /**
     * Remove a given Datastore in a given Workspace.
     * 
     * @param workspace The name of the workspace
     * @param storename The name of the Datastore to remove.
     * @param recurse if remove should be performed recursively
     * @throws IllegalArgumentException if workspace or storename are null or empty
     * @return <TT>true</TT> if the datastore was successfully removed.
     */
    public boolean removeDatastore(String workspace, String storename, final boolean recurse)
            throws IllegalArgumentException {
        return removeStore(workspace, storename, StoreType.DATASTORES, recurse, Purge.NONE);
    }

    public boolean removeDatastore(String workspace, String storename, final boolean recurse, final Purge purge)
            throws IllegalArgumentException {
        return removeStore(workspace, storename, StoreType.DATASTORES, recurse, purge);
    }

    /**
     * Remove recursively a given CoverageStore in a given Workspace.
     * 
     * @param workspace The name of the workspace
     * @param storename The name of the CoverageStore to remove.
     * @return <TT>true</TT> if the CoverageStore was successfully removed.
     * @deprecated use {@link #removeCoverageStore(String, String, boolean)}
     */
    public boolean removeCoverageStore(String workspace, String storename) {
        return removeCoverageStore(workspace, storename, true);
    }

    /**
     * Remove a given CoverageStore in a given Workspace.
     * 
     * @param workspace The name of the workspace
     * @param storename The name of the CoverageStore to remove.
     * @param recurse if remove should be performed recursively
     * @return <TT>true</TT> if the CoverageStore was successfully removed.
     */
    public boolean removeCoverageStore(final String workspace, final String storename,
            final boolean recurse) throws IllegalArgumentException {
        return removeStore(workspace, storename, StoreType.COVERAGESTORES, recurse, Purge.NONE);
    }

    /**
     * Remove a given CoverageStore in a given Workspace.
     *
     * Note that purging may not work when deleting mosaics (https://jira.codehaus.org/browse/GEOT-4613).
     *
     * @param workspace The name of the workspace
     * @param storename The name of the CoverageStore to remove.
     * @param recurse if remove should be performed recursively
     * @param purge the purge method
     * @return <TT>true</TT> if the CoverageStore was successfully removed.
     */
    public boolean removeCoverageStore(final String workspace, final String storename,
            final boolean recurse, final Purge purge) throws IllegalArgumentException {
        return removeStore(workspace, storename, StoreType.COVERAGESTORES, recurse, purge);
    }

    public enum Purge {NONE, METADATA, ALL};

    /**
     * Remove a given Datastore in a given Workspace.
     * 
     * @param workspace The name of the workspace
     * @param storename The name of the Datastore to remove.
     * @param the {@link StoreType} type
     * @param recurse if remove should be performed recursively
     * @param purge the purge method
     * @throws IllegalArgumentException if workspace or storename are null or empty
     * @return <TT>true</TT> if the store was successfully removed.
     */
    private boolean removeStore(String workspace, String storename, StoreType type,
            final boolean recurse, final Purge purge) throws IllegalArgumentException {
        try {
            if (workspace == null || storename == null)
                throw new IllegalArgumentException("Arguments may not be null!");
            if (workspace.isEmpty() || storename.isEmpty())
                throw new IllegalArgumentException("Arguments may not be empty!");

            final StringBuilder url = new StringBuilder(restURL);
            url.append("/rest/workspaces/").append(workspace).append("/").append(type).append("/")
                    .append(storename);
            url.append("?recurse=").append(recurse);
            if(purge != null)
                url.append("&purge=").append(purge);

            final URL deleteStore = new URL(url.toString());

            boolean deleted = HTTPUtils.delete(deleteStore.toExternalForm(), gsuser, gspass);
            if (!deleted) {
                LOGGER.warn("Could not delete store " + workspace + ":" + storename);
            } else {
                LOGGER.info("Store successfully deleted " + workspace + ":" + storename);
            }

            return deleted;
        } catch (MalformedURLException ex) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error(ex.getLocalizedMessage(), ex);
            return false;
        }
    }

    /**
     * Remove the workspace given Workspace using default parameters
     * 
     * @see {@link GeoServerRESTPublisher#removeWorkspace(String, boolean)}
     * @param workspace the workspace to remove
     * @return true if success, false otherwise
     * @deprecated {@link #removeWorkspace(String, boolean)}
     */
    public boolean removeWorkspace(String workspace) {
        return removeWorkspace(workspace, false);
    }

    /**
     * Remove a given Workspace.
     * 
     * @param workspace The name of the workspace
     * @param recurse The recurse parameter is used to recursively delete all resources contained by the specified workspace. This includes data
     *        stores, coverage stores, feature types, etc... Allowable values for this parameter are <i>true</i> or <i>false</i>. The default value is
     *        <i>false</i>.
     * @return <TT>true</TT> if the WorkSpace was successfully removed.
     */
    public boolean removeWorkspace(String workspace, boolean recurse)
            throws IllegalArgumentException {
        workspace = sanitize(workspace);
        try {
            if (workspace == null)
                throw new IllegalArgumentException("Arguments may not be null!");
            if (workspace.isEmpty())
                throw new IllegalArgumentException("Arguments may not be empty!");

            StringBuffer url = new StringBuffer(restURL).append("/rest/workspaces/").append(
                    workspace);
            if (recurse)
                url.append("?recurse=true");

            deleteStylesForWorkspace(workspace); // !!! workaround

            final URL deleteUrl = new URL(url.toString());
            boolean deleted = HTTPUtils.delete(deleteUrl.toExternalForm(), gsuser, gspass);
            if (!deleted) {
                LOGGER.warn("Could not delete Workspace " + workspace);
            } else {
                LOGGER.info("Workspace successfully deleted " + workspace);
            }

            return deleted;
        } catch (MalformedURLException ex) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error(ex.getLocalizedMessage(), ex);
            return false;
        }
    }

    /**
     *  workaround: geoserver does not delete styles inside workspaces
     * https://jira.codehaus.org/browse/GEOS-5986
     */
    private void deleteStylesForWorkspace(String workspace) {
        RESTStyleList styles = styleManager.getStyles(workspace);
        if (styles==null)
            return;
        for (NameLinkElem nameLinkElem : styles) {
            removeStyleInWorkspace(workspace, nameLinkElem.getName(), true);
        }
    }

    /**
     * Remove a layer group.
     * 
     * @param workspace the layer group workspace.
     * @param name the layer group name.
     * @return true if succeeded.
     */
    public boolean removeLayerGroup(String workspace, String name) {
        String url = restURL + "/rest";
        if (workspace == null) {
            url += "/layergroups/" + name;
        } else {
            url += "/workspaces/" + workspace + "/layergroups/" + name;
        }

        try {
            URL deleteUrl = new URL(url);
            boolean deleted = HTTPUtils.delete(deleteUrl.toExternalForm(), gsuser, gspass);
            if (!deleted) {
                if (LOGGER.isWarnEnabled())
                    LOGGER.warn("Could not delete layergroup " + name);
            } else {
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Layergroup successfully deleted: " + name);
            }

            return deleted;
        } catch (MalformedURLException ex) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error(ex.getLocalizedMessage(), ex);
            return false;
        }
    }

    /**
     * Remove a layer group.
     * 
     * @param name the layer group name.
     * @return true if succeeded.
     */
    public boolean removeLayerGroup(String name) {
        return removeLayerGroup(null, name);
    }

    /**
     * remove a generic given layer from a given workspace
     * 
     * @param workspace
     * @param layerName
     * @return true if success
     */
    public boolean removeLayer(final String workspace, final String layerName) {

        final String fqLayerName;

        // this null check is here only for backward compatibility. workspace
        // shall be mandatory.
        if (workspace == null) {

            fqLayerName = layerName;

            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Null workspace while removing layer : " + layerName
                        + " -- This behavior is deprecated.");
            }
        } else {
            fqLayerName = workspace + ":" + layerName;
        }
        if (layerName == null) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Null layerName : " + layerName);
            }
            return false;
        }

        final String url = restURL + "/rest/layers/" + fqLayerName;

        boolean result = HTTPUtils.delete(url, gsuser, gspass);
        if (result) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Layer successfully removed: " + fqLayerName);
            }
        } else {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Error removing layer " + fqLayerName);
        }

        return result;
    }

    // ==========================================================================
    // === CATALOG REFRESHING
    // ==========================================================================

    /**
     * 
     * /workspaces/<ws>/datastores/<ds>.xml /workspaces/<ws>/coveragestores/<ds>.xml
     * 
     * @param workspace
     * @param storeName
     * @param mosaicDir
     * @return true if successfully reloaded
     * @throws FileNotFoundException
     * @throws IllegalArgumentException
     * @throws MalformedURLException
     */
    public boolean reloadStore(String workspace, final String storeName, StoreType storeType)
            throws IllegalArgumentException, MalformedURLException {
        final String url = HTTPUtils.append(this.restURL, "/rest/workspaces/", workspace, "/",
                storeType.toString(), "/", storeName, ".xml").toString();
        final String store = HTTPUtils.get(url, this.gsuser, this.gspass);

        if (store != null) {
            String storeTag = storeType.getTypeName();
            // switch (storeType) {
            // case COVERAGESTORES:
            // storeTag = storeType.toString().replaceAll("store", "");
            // break;
            // case DATASTORES:
            // storeTag = "featureTypes";
            // break;
            // default:
            // throw new IllegalArgumentException("Unrecognized type");
            // }

            String startTag = "<" + storeTag + ">";
            int start = store.indexOf(startTag);
            String endTag = "</" + storeTag + ">";
            int stop = store.indexOf(endTag) + endTag.length();
            return HTTPUtils.putXml(url, store.subSequence(0, start) + store.substring(stop),
                    this.gsuser, this.gspass) != null ? true : false;
        } else
            return false;
    }

    /**
     * Reload the target geoserver configuration
     * 
     * @return true if success
     * 
     * @see <a href="http://docs.geoserver.org/stable/en/user/restconfig/rest-config-api.html">GeoServer REST Config API</a>
     */
    public boolean reload() {
        String sUrl = restURL + "/rest/reload";
        String result = HTTPUtils.post(sUrl, "", "text/plain", gsuser, gspass);
        return result != null;
    }

    /**
     * Reset the target geoserver configuration
     * 
     * @return true if success
     * 
     * @see <a href="http://docs.geoserver.org/stable/en/user/restconfig/rest-config-api.html">GeoServer REST Config API</a>
     */
    public boolean reset() {
        String sUrl = restURL + "/rest/reset";
        String result = HTTPUtils.post(sUrl, "", "text/plain", gsuser, gspass);
        return result != null;
    }

    // ==========================================================================
    // === MISCELLANEOUS
    // ==========================================================================

    /**
     * Allows to configure some layer attributes such as DefaultStyle
     * 
     * @param workspace
     * @param resourceName the name of the resource to use (featureStore or coverageStore name)
     * @param layer the layer encoder used to configure the layer
     * @return true if success
     * @throws IllegalArgumentException if some arguments are null or empty
     * 
     * @TODO WmsPath
     */
    public boolean configureLayer(final String workspace, final String resourceName,
            final GSLayerEncoder layer) throws IllegalArgumentException {

        if (workspace == null || resourceName == null || layer == null) {
            throw new IllegalArgumentException("Null argument");
        }
        // TODO: check this usecase, layer should always be defined
        if (workspace.isEmpty() || resourceName.isEmpty() || layer.isEmpty()) {
            throw new IllegalArgumentException("Empty argument");
        }

        final String fqLayerName = workspace + ":" + resourceName;

        final String url = restURL + "/rest/layers/" + fqLayerName;

        String layerXml = layer.toString();
        String sendResult = HTTPUtils.putXml(url, layerXml, gsuser, gspass);
        if (sendResult != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Layer successfully configured: " + fqLayerName);
            }
        } else {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Error configuring layer " + fqLayerName + " (" + sendResult + ")");
        }

        return sendResult != null;
    }

    /**
     * Create a new LayerGroup using the specified encoder
     * 
     * @param name name of the layer group
     * @param group group encoder
     * @return true if operation was successful
     */
    public boolean createLayerGroup(String name, GSLayerGroupEncoder group) {
        return createLayerGroup(null, name, group);
    }

    /**
     * Create a new LayerGroup using the specified encoder
     * 
     * @param workspace name of the workspace
     * @param name name of the layer group
     * @param group group encoder
     * @return true if operation was successful
     */
    public boolean createLayerGroup(String workspace, String name, GSLayerGroupEncoder group) {
        String url = restURL + "/rest";
        if (workspace == null) {
            url += "/layergroups/";
        } else {
            group.setWorkspace(workspace);
            url += "/workspaces/" + workspace + "/layergroups/";
        }

        group.setName(name);

        String sendResult = HTTPUtils.postXml(url, group.toString(), gsuser, gspass);
        if (sendResult != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("LayerGroup successfully configured: " + name);
            }
        } else {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Error configuring LayerGroup " + name + " (" + sendResult + ")");
        }

        return sendResult != null;
    }

    /**
     * Update a LayerGroup using the specified encoder
     * 
     * @param name name of the layer group
     * @param group group encoder
     * @return true if operation was successful
     */
    public boolean configureLayerGroup(String name, GSLayerGroupEncoder group) {
        return configureLayerGroup(null, name, group);
    }

    /**
     * Update a LayerGroup using the specified encoder
     * 
     * @param workspace name of the workspace
     * @param name name of the layer group
     * @param group group encoder
     * @return true if operation was successful
     */
    public boolean configureLayerGroup(String workspace, String name, GSLayerGroupEncoder group) {
        String url = restURL + "/rest";
        if (workspace == null) {
            url += "/layergroups/" + name;
        } else {
            url += "/workspaces/" + workspace + "/layergroups/" + name;
        }

        String sendResult = HTTPUtils.putXml(url, group.toString(), gsuser, gspass);
        if (sendResult != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("LayerGroup successfully configured: " + name);
            }
        } else {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Error configuring LayerGroup " + name + " (" + sendResult + ")");
        }

        return sendResult != null;
    }
    
    /**
     * Configure an existing coverage in a given workspace and coverage store
     * 
     * @param ce contains the coverage name to configure and the configuration to apply
     * @param wsname the workspace to search for existent coverage
     * @param csname the coverage store to search for existent coverage
     * @return true if success
     */
    public boolean configureCoverage(final GSCoverageEncoder ce, final String wsname,
            final String csname) {
        return configureCoverage(ce, wsname, csname, ce.getName());
    }    

    /**
     * Configure an existing coverage in a given workspace and coverage store
     * 
     * @param ce contains the coverage name to configure and the configuration to apply
     * @param wsname the workspace to search for existent coverage
     * @param csname the coverage store to search for existent coverage
     * @param coverageName the name of the coverage, useful for changing name for the coverage itself
     * @return true if success
     */
    public boolean configureCoverage(final GSCoverageEncoder ce, final String wsname,
            final String csname, final String coverageName) {
        if (coverageName == null) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Unable to configure a coverage with no name try using GSCoverageEncoder.setName(String)");
            return false;
        }
        // retrieve coverage name
        GeoServerRESTReader reader;
        try {
            reader = new GeoServerRESTReader(restURL, gsuser, gspass);
        } catch (MalformedURLException e) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error(e.getLocalizedMessage(), e);
            return false;
        }
        
        // optimized search, left the old code for reference 
        RESTCoverage coverage = reader.getCoverage(wsname, csname, coverageName);
//        final RESTCoverageList covList = reader.getCoverages(wsname, csname);
//        if (covList==null||covList.isEmpty()) {
//            if (LOGGER.isErrorEnabled())
//                LOGGER.error("No coverages found in new coveragestore " + csname);
//            return false;
//        }
//        final Iterator<NameLinkElem> it = covList.iterator();
//        while (it.hasNext()) {
//            NameLinkElem nameElem = it.next();
//            if (nameElem.getName().equals(coverageName)) {
//                found = true;
//                break;
//            }
//        }
        // if no coverage to configure is found return false
        if (coverage==null) {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("No coverages found in new coveragestore " + csname + " called "
                        + coverageName);
            return false;
        }

        // configure the selected coverage
        final String url = restURL + "/rest/workspaces/" + wsname + "/coveragestores/" + csname
                + "/coverages/" + coverageName + ".xml";

        final String xmlBody = ce.toString();
        final String sendResult = HTTPUtils.putXml(url, xmlBody, gsuser, gspass);
        if (sendResult != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Coverage successfully configured " + wsname + ":" + csname + ":"
                        + coverageName);
            }
        } else {
            if (LOGGER.isWarnEnabled())
                LOGGER.warn("Error configuring coverage " + wsname + ":" + csname + ":" + coverageName
                        + " (" + sendResult + ")");
        }

        return sendResult != null;
    }

    /**
     * @deprecated use {@link #createCoverage(String, String, GSCoverageEncoder)}
     */
    public boolean createCoverage(final GSCoverageEncoder ce, final String wsname,
            final String csname) {
        return createCoverage(wsname, csname, ce);
    }

    /**
     * Create a new coverage in a given workspace and coverage store
     * 
     * @param wsname the workspace to search for existent coverage
     * @param storeName an existent store name to use as data source
     * @param ce contains the coverage name to create and the configuration to apply
     * @return true if success
     * @throws IllegalArgumentException if arguments are null or empty
     */
    public boolean createCoverage(final String wsname, final String storeName,
            final GSCoverageEncoder ce) throws IllegalArgumentException {
        return createResource(wsname, StoreType.COVERAGESTORES, storeName, ce);
    }

    /**
     * Create a new resource in a given workspace and store
     * 
     * @param wsname the workspace to search for existent coverage
     * @param storeName an existent store name to use as data source
     * @param re contains the coverage name to create and the configuration to apply
     * 
     * @TODO For FeatureType: The list parameter is used to control the category of feature types that are returned. It can take one of the three
     *       values configured, available, or all.
     * 
     *       configured - Only setup or configured feature types are returned. This is the default value. available - Only unconfigured feature types
     *       (not yet setup) but are available from the specified datastore will be returned. available_with_geom - Same as available but only
     *       includes feature types that have a geometry granule. all - The union of configured and available.
     * 
     * 
     * @return true if success
     * @throws IllegalArgumentException if arguments are null or empty
     */
    private boolean createResource(String workspace, StoreType dsType, String storeName,
            GSResourceEncoder re) throws IllegalArgumentException {
        if (workspace == null || dsType == null || storeName == null || re == null) {
            throw new IllegalArgumentException("Null argument");
        }
        StringBuilder sbUrl = new StringBuilder(restURL).append("/rest/workspaces/")
                .append(workspace).append("/").append(dsType).append("/").append(storeName)
                .append("/").append(dsType.getTypeNameWithFormat(Format.XML));

        final String resourceName = re.getName();
        if (resourceName == null) {
            throw new IllegalArgumentException(
                    "Unable to configure a coverage using unnamed coverage encoder");
        }

        final String xmlBody = re.toString();
        final String sendResult = HTTPUtils.postXml(sbUrl.toString(), xmlBody, gsuser, gspass);
        if (sendResult != null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(dsType + " successfully created " + workspace + ":" + storeName + ":"
                        + resourceName);
            }
        } else {
            if (LOGGER.isErrorEnabled())
                LOGGER.error("Error creating coverage " + workspace + ":" + storeName + ":"
                        + resourceName + " (" + sendResult + ")");
        }

        return sendResult != null;
    }

    /**
     * Appends ".DUMMY" to any string containing a dot (<i>sic</i>).
     */
    protected String sanitize(String s) {
        if (s.indexOf(".") != -1) {
            return s + ".DUMMY";
        }
        return s;
    }

    /**
     * Append params generating a string in the form:
     * <p>
     * NAME_0=VALUE_0&NAME_1=VALUE_1&....&NAME_n-1=VALUE_n-1
     * </p>
     * </br>
     * 
     * @param params an array of NameValuePair
     * @return the parameter string or empty an string
     */
    private String appendParameters(NameValuePair... params) {
        StringBuilder sbUrl = new StringBuilder();
        // append parameters
        if (params != null) {
            final int paramsSize = params.length;
            if (paramsSize > 0) {
                int i = 0;
                NameValuePair param = params[i];
                while (param != null && i++ < paramsSize) {
                    final String name = param.getName();
                    final String value = param.getValue();
                    // success
                    if (name != null && !name.isEmpty() && value != null && !value.isEmpty()) {
                        sbUrl.append(name).append("=").append(value);
                        // end cycle
                        param = null;
                    } else {
                        // next value
                        param = params[i];
                    }
                }
                for (; i < paramsSize; i++) {
                    param = params[i];
                    if (param != null) {
                        final String name = param.getName();
                        final String value = param.getValue();
                        sbUrl.append(name).append("=").append(value);
                        if (name != null && !name.isEmpty() && value != null && !value.isEmpty()) {
                            sbUrl.append("&").append(name).append("=").append(value);
                        }

                    }

                }
            }
        }
        return sbUrl.toString();
    }

    /**
     * URL-encodes a String.
     * 
     * @param s The original string.
     * @return The encoded string.
     */
    protected String encode(String s) {
        // try {
        // return URLEncoder.encode(s,"UTF-8");
        // } catch (UnsupportedEncodingException e) {
        // LOGGER.warn("Error encoding :"+s+" with UTF-8: "+e.getLocalizedMessage());
        return URLEncoder.encode(s);
        // }
    }

    // ==> StructuredCoverageGridReader

    /**
     * Create a store or harvest the coverage from the provided <b>external</b> path.
     * 
     * @param workspace the GeoServer workspace
     * @param coverageStore the GeoServer coverageStore
     * @param format the format of the file to upload
     * @param the absolut path to the file to upload
     * 
     * @return <code>true</code> if the call succeeds or <code>false</code> otherwise.
     */
    public boolean harvestExternal(String workspace, String coverageStore, String format,
            String path) {
        try {
            GeoServerRESTStructuredGridCoverageReaderManager manager = new GeoServerRESTStructuredGridCoverageReaderManager(
                    new URL(restURL), gsuser, gspass);
            return manager.harvestExternal(workspace, coverageStore, format, path);
        } catch (IllegalArgumentException e) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(e.getLocalizedMessage(), e);
            }
        } catch (MalformedURLException e) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(e.getLocalizedMessage(), e);
            }
        }
        return false;
    }

    /**
     * Create a new ImageMosaic with the provided configuration provided as a zip file.
     * 
     * <p>
     * This call configures all the coverages contained in the ImageMosaic.
     * 
     * @param workspace the GeoServer workspace
     * @param coverageStore the GeoServer coverageStore
     * @param the absolute path to the file to upload
     * 
     * @return <code>true</code> if the call succeeds or <code>false</code> otherwise.
     * @since geoserver-2.4.0, geoserver-mng-1.6.0
     */
    public boolean createImageMosaic(String workspace, String coverageStore, String path) {
        return createImageMosaic(workspace, coverageStore, path, ConfigureCoveragesOption.ALL);
    }
    
    /**
     * Create a new ImageMosaic with the provided configuration provided as a zip file.
     * 
     * <p>
     * With the options configure we can decide whether or not to configure or not the coverages contained in the ImageMosaic.
     * 
     * @param workspace the GeoServer workspace
     * @param coverageStore the GeoServer coverageStore
     * @param the absolute path to the file to upload
     * @param configureOpt tells GeoServer whether to configure all coverages in this mosaic (ALL) or none of them (NONE).
     * 
     * @return <code>true</code> if the call succeeds or <code>false</code> otherwise.
     * @since geoserver-2.4.0, geoserver-mng-1.6.0
     */
    public boolean createImageMosaic(String workspace, String coverageStore, String path, ConfigureCoveragesOption configureOpt) {
        // checks
        checkString(workspace);
        checkString(coverageStore);
        checkString(path);
        final File zipFile= new File(path);
        if(!zipFile.exists()||!zipFile.isFile()||!zipFile.canRead()){
            throw new IllegalArgumentException("The provided pathname does not point to a valide zip file: "+path);
        }
        // is it a zip?
        ZipFile zip=null;
        try{
            zip= new ZipFile(zipFile);
            zip.getName();
        }catch (Exception e) {
            LOGGER.trace(e.getLocalizedMessage(),e.getStackTrace());
            throw new IllegalArgumentException("The provided pathname does not point to a valide zip file: "+path);
        }finally{
            if(zip!=null){
                try {
                    zip.close();
                } catch (IOException e) {
                    // swallow
                    LOGGER.trace(e.getLocalizedMessage(),e.getStackTrace());
                }
            }
        }

        // create URL
        StringBuilder ss=HTTPUtils.append(restURL, "/rest/workspaces/", workspace, "/coveragestores/",
                coverageStore, "/", UploadMethod.EXTERNAL.toString(), ".imagemosaic");
        switch(configureOpt){
        case ALL:
            break;
        case NONE:
            ss.append("?configure=none");
            break;
        default: 
            throw new IllegalArgumentException("Unrecognized COnfigureOption: "+configureOpt);
        }
        String sUrl = ss.toString();

        // POST request
        String result = HTTPUtils.put(sUrl, zipFile, "application/zip", gsuser, gspass);
        return result != null;
    }

    /**
     * Remove a granule from a structured coverage by id.
     * 
     * @param workspace the GeoServer workspace
     * @param coverageStore the GeoServer coverageStore
     * @param coverage the name of the target coverage from which we are going to remove
     * @param filter the absolute path to the file to upload
     * 
     * @return <code>null</code> in case the call does not succeed, or an instance of {@link RESTStructuredCoverageGranulesList}.
     * 
     * @throws MalformedURLException
     * @throws UnsupportedEncodingException
     */
    public boolean removeGranuleById(final String workspace, String coverageStore, String coverage,
            String granuleId) {
        try {
            GeoServerRESTStructuredGridCoverageReaderManager manager = new GeoServerRESTStructuredGridCoverageReaderManager(
                    new URL(restURL), gsuser, gspass);
            return manager.removeGranuleById(workspace, coverageStore, coverage, granuleId);
        } catch (IllegalArgumentException e) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(e.getLocalizedMessage(), e);
            }
        } catch (MalformedURLException e) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(e.getLocalizedMessage(), e);
            }
        }
        return false;
    }

    /**
     * Remove granules from a structured coverage, by providing a CQL filter.
     * 
     * @param workspace the GeoServer workspace
     * @param coverageStore the GeoServer coverageStore
     * @param coverage the name of the target coverage from which we are going to remove
     * @param filter the absolute path to the file to upload
     * 
     * @return <code>null</code> in case the call does not succeed, or an instance of {@link RESTStructuredCoverageGranulesList}.
     * 
     * @throws MalformedURLException
     * @throws UnsupportedEncodingException
     */
    public boolean removeGranulesByCQL(final String workspace, String coverageStore,
            String coverage, String filter) throws UnsupportedEncodingException {
        try {
            GeoServerRESTStructuredGridCoverageReaderManager manager = new GeoServerRESTStructuredGridCoverageReaderManager(
                    new URL(restURL), gsuser, gspass);
            return manager.removeGranulesByCQL(workspace, coverageStore, coverage, filter);
        } catch (IllegalArgumentException e) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(e.getLocalizedMessage(), e);
            }
        } catch (MalformedURLException e) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(e.getLocalizedMessage(), e);
            }
        }
        return false;

    }

    /**
     * Check the provided string for not being null or empty.
     * 
     * <p>
     * It throws an exception in case the string is either null or empty.
     * 
     * @param string the {@link String} to be checked
     */
    private static void checkString(String string) {
        if (string == null) {
            throw new NullPointerException("Provided string is is null!");
        }
        if (string.length() <= 0) {
            throw new IllegalArgumentException("Provided string is is empty!");
        }
    
    }

}

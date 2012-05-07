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
import it.geosolutions.geoserver.rest.decoder.utils.NameLinkElem;
import it.geosolutions.geoserver.rest.encoder.GSBackupEncoder;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSNamespaceEncoder;
import it.geosolutions.geoserver.rest.encoder.GSPostGISDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.geosolutions.geoserver.rest.encoder.GSWorkspaceEncoder;
import it.geosolutions.geoserver.rest.encoder.coverage.GSCoverageEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Connect to a GeoServer instance to publish or modify data.
 * <P>
 * There are no modifiable instance fields, so all the calls are thread-safe.
 * 
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GeoServerRESTPublisher {

	private static final Logger LOGGER = LoggerFactory.getLogger(GeoServerRESTPublisher.class);
	private final String restURL;
	private final String gsuser;
	private final String gspass;

	/**
	 * Creates a <TT>GeoServerRESTPublisher</TT> for a given GeoServer instance
	 * with the given auth credentials.
	 * 
	 * @param restURL
	 *            the base GeoServer URL (e.g.:
	 *            <TT>http://localhost:8080/geoserver</TT>)
	 * @param username
	 *            username auth credential
	 * @param password
	 *            password auth credential
	 */
	public GeoServerRESTPublisher(String restURL, String username,
			String password) {
		this.restURL = HTTPUtils.decurtSlash(restURL);
		this.gsuser = username;
		this.gspass = password;
	}


    // ==========================================================================
    // === BACKUP and RESTORE
    // ==========================================================================

    /**
     * Issues a GeoServer BACKUP.
     * <P>
     * This is the equivalent call with cUrl:
     *
     * <PRE>
     * {@code curl -u admin:geoserver -XPOST \
     *      -H 'Content-type: text/xml' \
     *      --data "&lt;task&gt;&lt;path&gt;${BACKUP_DATADIR}&lt;/path&gt;&lt;/task&gt;" \
     *      http://$GSIP:$GSPORT/$SERVLET/rest/bkprst/backup}
     * </PRE>
     *
     * @param backupDir
     *            the target Backup Dir String.
     *
     * @return <TT>id</TT> of the backup.
     * @throws IllegalArgumentException
     *             if the backup_dir is null or empty
     */
    public String backup(final String backupDir) throws IllegalArgumentException
    {
        return backup(backupDir, false, false, false);
    }

    /**
     * Issues a GeoServer BACKUP.
     * <P>
     * This is the equivalent call with cUrl:
     *
     * <PRE>
     * {@code curl -u admin:geoserver -XPOST \
     *      -H 'Content-type: text/xml' \
     *      --data "&lt;task&gt;&lt;path&gt;${BACKUP_DATADIR}&lt;/path&gt;&lt;includedata&gt;${includedata}&lt;/includedata&gt;&lt;includegwc&gt;${includegwc}&lt;/includegwc&gt;&lt;includelog&gt;${includelog}&lt;/includelog&gt;&lt;/task&gt;" \
     *      http://$GSIP:$GSPORT/$SERVLET/rest/bkprst/backup}
     * </PRE>
     *
     * @param backupDir
     *            the target Backup Dir String.
     * @param includedata
     *            whether or not include the data dir Boolean.
     * @param includegwc
     *            whether or not include the geowebcache dir Boolean.
     * @param includelog
     *            whether or not include the log dir Boolean.
     *
     * @return <TT>id</TT> of the backup.
     * @throws IllegalArgumentException
     *             if the backup_dir is null or empty
     */
    public String backup(final String backupDir,
    		final boolean includedata,
    		final boolean includegwc,
    		final boolean includelog) throws IllegalArgumentException
    {
        if ((backupDir == null) || backupDir.isEmpty())
        {
            throw new IllegalArgumentException(
                "The backup_dir must not be null or empty");
        }

        StringBuilder bkpUrl = new StringBuilder(restURL);
        bkpUrl.append("/rest/bkprst/backup");

        final GSBackupEncoder bkpenc = new GSBackupEncoder(backupDir);
        bkpenc.setIncludeData(includedata);
        bkpenc.setIncludeGwc(includegwc);
        bkpenc.setIncludeLog(includelog);
        final String result = HTTPUtils.post(bkpUrl.toString(), bkpenc.toString(),
                "text/xml", gsuser, gspass);

        return result;
    }

    /**
     * Issues a GeoServer RESTORE.
     * <P>
     * This is the equivalent call with cUrl:
     *
     * <PRE>
     * {@code curl -u admin:geoserver -XPOST \
     *      -H 'Content-type: text/xml' \
     *      --data "&lt;task&gt;&lt;path&gt;${BACKUP_DATADIR}&lt;/path&gt;&lt;/task&gt;" \
     *      http://$GSIP:$GSPORT/$SERVLET/rest/bkprst/restore}
     * </PRE>
     *
     * @param backupDir
     *            the target Backup Dir String.
     *
     * @return <TT>id</TT> of the backup.
     * @throws IllegalArgumentException
     *             if the backup_dir is null or empty
     */
    public String restore(final String backupDir) throws IllegalArgumentException
    {
        if ((backupDir == null) || backupDir.isEmpty())
        {
            throw new IllegalArgumentException(
                "The backup_dir must not be null or empty");
        }

        StringBuilder bkpUrl = new StringBuilder(restURL);
        bkpUrl.append("/rest/bkprst/restore");

        final GSBackupEncoder bkpenc = new GSBackupEncoder(backupDir);
        
        final String result = HTTPUtils.post(bkpUrl.toString(), bkpenc.toString(),
                "text/xml", gsuser, gspass);

        return result;
    }

	// ==========================================================================
	// === WORKSPACES
	// ==========================================================================

	/**
	 * Create a new Workspace
	 * 
	 * @param workspace
	 *            The name of the new workspace.
	 * 
	 *            <P>
	 *            This is the equivalent call with cUrl:
	 * 
	 *            <PRE>
	 * {@code curl -u admin:geoserver -XPOST \
	 *      -H 'Content-type: text/xml' \
	 *      -d "<workspace><name>$WORKSPACE</name></workspace>" \
	 *      http://$GSIP:$GSPORT/$SERVLET/rest/workspaces
	 * }
	 * </PRE>
	 */
	public boolean createWorkspace(final String workspace) {
		final String sUrl = restURL + "/rest/workspaces";
		final GSWorkspaceEncoder wsenc = new GSWorkspaceEncoder(workspace);
		final String wsxml = wsenc.toString();
		final String result = HTTPUtils.postXml(sUrl, wsxml, gsuser, gspass);
		return result != null;
	}
	
	/**
	 * Create both a workspace and its associated namespace.
	 * 
	 * Note that this method is equivalent to {@link #createNamespace}.
	 * 
	 * @param name Name for the new workspace, which will be also its associated namespace prefix.
	 * @param uri Namespace URI. Cannot be empty.
	 * @return <TT>true</TT> if the Workspace and its associated namespace were successfully created.
	 */
	public boolean createWorkspace(final String name, final URI uri) {
		// This is really an alias to createNamespace, as GeoServer
		// will automatically create the workspace as well.
		return createNamespace(name, uri);
	}
	
	// ==========================================================================
	// === NAMESPACES
	// ==========================================================================

	/**
	 * Create a new namespace. GeoServer will automatically create the corresponding workspace.
	 * 
	 * Prefix and uri are mandatory and cannot be empty.
	 * If a namespace with the given prefix already exists, it won't be created. 
	 * 
	 * @param prefix The name of the new namespace.
	 * @param uri The URI of the new namespace.
	 * @return <TT>true</TT> if the namespace was successfully created.
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
	 * Update a namespace URI.
	 * 
	 * Prefix and uri are mandatory and cannot be empty.
	 * A namespace with the given prefix should exist.
	 * 
	 * @param prefix The prefix of an existing namespace.
	 * @param uri The new URI.
	 * @return <TT>true</TT> if the namespace was successfully updated.
	 */
	public boolean updateNamespace(final String prefix, final URI uri) {
		final String sUrl = restURL + "/rest/namespaces/"+ encode(prefix);
		final GSNamespaceEncoder nsenc = new GSNamespaceEncoder(prefix, uri);
		final String nsxml = nsenc.toString();
		final String result = HTTPUtils.put(sUrl, nsxml, "application/xml", gsuser, gspass);
		return result != null;
	}
	
	/**
	 * Remove a given Namespace. It will remove the associated workspace as well.
	 * 
	 * @param prefix
	 *            The namespace prefix
	 * @param recurse
	 *            The recurse parameter is used to recursively delete all
	 *            resources contained in the workspace associated with this
	 *            namespace. This includesdata stores, coverage stores,
	 *            feature types, etc... Allowable values for this parameter
	 *            are <i>true</i> or <i>false</i>. The default (safer) value
	 *            is <i>false</i>.
	 * @return <TT>true</TT> if the namespace was successfully removed.
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
	 * Store and publish an SLD.
	 * <P>
	 * This is the equivalent call with cUrl:
	 * 
	 * <PRE>
	 * {@code curl -u admin:geoserver -XPOST \
	 *      -H 'Content-type: application/vnd.ogc.sld+xml' \
	 *      -d @$FULLSLD \
	 *      http://$GSIP:$GSPORT/$SERVLET/rest/styles}
	 * </PRE>
	 * 
	 * @param sldBody
	 *            the SLD document as an XML String.
	 * 
	 * @return <TT>true</TT> if the operation completed successfully.
	 */
	public boolean publishStyle(String sldBody) {
		try {
			return publishStyle(sldBody, null);
		} catch (IllegalArgumentException e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(e.getLocalizedMessage(), e);
			}
		}
		return false;
	}

	/**
	 * Store and publish an SLD.
	 * <P>
	 * This is the equivalent call with cUrl:
	 * 
	 * <PRE>
	 * {@code curl -u admin:geoserver -XPOST \
	 *      -H 'Content-type: application/vnd.ogc.sld+xml' \
	 *      -d @$FULLSLD \
	 *      http://$GSIP:$GSPORT/$SERVLET/rest/styles?name=name}
	 * </PRE>
	 * 
	 * @param sldBody
	 *            the SLD document as an XML String.
	 * @param name
	 *            the Style name (can be null).
	 * 
	 * @return <TT>true</TT> if the operation completed successfully.
	 * @throws IllegalArgumentException
	 *             if the style body is null or empty
	 */
	public boolean publishStyle(final String sldBody, final String name)
			throws IllegalArgumentException {
		if (sldBody == null || sldBody.isEmpty()) {
			throw new IllegalArgumentException(
					"The style body may not be null or empty");
		}
		StringBuilder sUrl = new StringBuilder(restURL);
		sUrl.append("/rest/styles");
		if (name != null && !name.isEmpty()) {
			sUrl.append("?name=").append(name);
		}
		final String result = HTTPUtils.post(sUrl.toString(), sldBody,
				"application/vnd.ogc.sld+xml", gsuser, gspass);
		return result != null;
	}

	/**
	 * Store and publish an SLD.
	 * 
	 * @param sldFile
	 *            the File containing the SLD document.
	 * 
	 * @return <TT>true</TT> if the operation completed successfully.
	 */
	public boolean publishStyle(File sldFile) {
		return publishStyle(sldFile, null);
	}

	/**
	 * Store and publish an SLD, assigning it a name.
	 * 
	 * @param sldFile
	 *            the File containing the SLD document.
	 * @param name
	 *            the Style name.
	 * 
	 * @return <TT>true</TT> if the operation completed successfully.
	 */
	public boolean publishStyle(File sldFile, String name) {
		String sUrl = restURL + "/rest/styles";
		if (name != null && !name.isEmpty()) {
			sUrl += "?name=" + encode(name);
		}
		LOGGER.debug("POSTing new style " + name + " to " + sUrl);
		String result = HTTPUtils.post(sUrl, sldFile,
				"application/vnd.ogc.sld+xml", gsuser, gspass);
		return result != null;
	}

	/**
	 * Update SLD called as 'name'.
	 * <P>
	 * This is the equivalent call with cUrl:
	 * 
	 * <PRE>
	 * {@code curl -u admin:geoserver -XPUT \
	 *      -H 'Content-type: application/vnd.ogc.sld+xml' \
	 *      -d @$FULLSLD \
	 *      http://$GSIP:$GSPORT/$SERVLET/rest/styles/$NAME}
	 * </PRE>
	 * 
	 * @param sldBody
	 *            the SLD document as an XML String.
	 * @param name
	 *            the Style name to modify.
	 * 
	 * @return <TT>true</TT> if the operation completed successfully.
	 * 
	 * @throws IllegalArgumentException
	 *             if the style body or name are null or empty
	 * 
	 */
	public boolean updateStyle(final String sldBody, final String name)
			throws IllegalArgumentException {
		if (sldBody == null || sldBody.isEmpty()) {
			throw new IllegalArgumentException(
					"The style body may not be null or empty");
		} else if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException(
					"The style name may not be null or empty");
		}

		final StringBuilder sUrl = new StringBuilder(restURL);
		sUrl.append("/rest/styles/").append(encode(name));

		final String result = HTTPUtils.put(sUrl.toString(), sldBody,
				"application/vnd.ogc.sld+xml", gsuser, gspass);
		return result != null;
	}

	/**
	 * Update an SLD called 'name'.
	 * 
	 * @param sldFile
	 *            the File containing the SLD document.
	 * @param name
	 *            the Style name.
	 * 
	 * @return <TT>true</TT> if the operation completed successfully.
	 * 
	 * @throws IllegalArgumentException
	 *             if the sldFile file or name are null or name is empty
	 * 
	 */
	public boolean updateStyle(final File sldFile, final String name)
			throws IllegalArgumentException {

		if (sldFile == null) {
			throw new IllegalArgumentException(
					"Unable to updateStyle using a null parameter file");
		} else if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException(
					"The style name may not be null or empty");
		}

		final StringBuilder sUrl = new StringBuilder(restURL);
		sUrl.append("/rest/styles/").append(encode(name));

		final String result = HTTPUtils.put(sUrl.toString(), sldFile,
				"application/vnd.ogc.sld+xml", gsuser, gspass);
		return result != null;

	}

	/**
	 * Remove a Style.<br>
	 * 
	 * The Style will be unpublished and the related SLD file will be removed
	 * (if purge==true).<br>
	 * 
	 * @param styleName
	 *            the name of the Style to remove.
	 * @param purge
	 *            and the related SLD file will be removed.
	 * 
	 * @return <TT>true</TT> if the operation completed successfully.
	 * @throws IllegalArgumentException
	 *             if styleName is null or empty
	 */
	public boolean removeStyle(String styleName, final boolean purge)
			throws IllegalArgumentException {
		if (styleName == null || styleName.isEmpty())
			throw new IllegalArgumentException(
					"Check styleName parameter, it may never be null or empty");

		final StringBuffer sUrl = new StringBuffer(restURL);

		// check style name
		// TODO may we whant to throw an exception instead of
		// change style name?
		styleName = styleName.replaceAll(":", "_");
		styleName = encode(styleName);

		sUrl.append("/rest/styles/").append(styleName);
		if (purge) {
			sUrl.append("?purge=true");
		}

		return HTTPUtils.delete(sUrl.toString(), gsuser, gspass);
	}

	/**
	 * Remove a Style.
	 * <P>
	 * The Style will be unpublished and the related SLD file will be removed.
	 * 
	 * @param styleName
	 *            the name of the Style to remove.
	 * 
	 * @return <TT>true</TT> if the operation completed successfully.
	 */
	public boolean removeStyle(String styleName) {
		try {
			return removeStyle(styleName, true);
		} catch (IllegalArgumentException e) {
			if (LOGGER.isErrorEnabled()) {
				LOGGER.error(e.getLocalizedMessage(), e);
			}
		}
		return false;
	}

	private boolean createDataStore(String workspace, String storeName,
			UploadMethod method, DataStoreExtension extension, String mimeType,
			URI uri, ParameterConfigure configure, NameValuePair... params)
			throws FileNotFoundException, IllegalArgumentException {
		return createStore(workspace, DataStoreType.datastores, storeName,
				method, extension, mimeType, uri, configure, params);
	}

	private boolean createCoverageStore(String workspace, String storeName,
			UploadMethod method, CoverageStoreExtension extension,
			String mimeType, URI uri, ParameterConfigure configure,
			NameValuePair... params) throws FileNotFoundException,
			IllegalArgumentException {
		return createStore(workspace, DataStoreType.coveragestores, storeName,
				method, extension, mimeType, uri, configure, params);
	}

	/**
	 * 
	 * @param workspace
	 * @param dsType
	 * @param storeName
	 * @param method
	 * @param extension
	 * @param mimeType
	 * @param uri
	 * @param configure
	 * @param params
	 * @return
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 */
	private boolean createStore(String workspace, DataStoreType dsType,
			String storeName, UploadMethod method, Enum extension,
			String mimeType, URI uri, ParameterConfigure configure,
			NameValuePair... params) throws FileNotFoundException,
			IllegalArgumentException {
		if (workspace == null || dsType == null || storeName == null
				|| method == null | extension == null || mimeType == null
				|| uri == null) {
			throw new IllegalArgumentException("Null argument");
		}
		StringBuilder sbUrl = new StringBuilder(restURL)
				.append("/rest/workspaces/").append(workspace).append("/")
				.append(dsType).append("/").append(storeName).append("/")
				.append(method).append(".").append(extension);

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

		if (method.equals(UploadMethod.file)) {
			final File file = new File(uri);
			if (!file.exists())
				throw new FileNotFoundException("unable to locate file: "
						+ file);
			sentResult = HTTPUtils.put(sbUrl.toString(), file, mimeType,
					gsuser, gspass);
		} else if (method.equals(UploadMethod.external)) {
			sentResult = HTTPUtils.put(sbUrl.toString(), uri.toString(),
					mimeType, gsuser, gspass);
		} else if (method.equals(UploadMethod.url)) {
			// TODO check
			sentResult = HTTPUtils.put(sbUrl.toString(), uri.toString(),
					mimeType, gsuser, gspass);
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
	 * A data store is a source of spatial data that is vector based. It can be
	 * a file in the case of a Shapefile, a database in the case of PostGIS, or
	 * a server in the case of a remote Web Feature Service.<br>
	 * 
	 * A coverage store is a source of spatial data that is raster based.<br>
	 * 
	 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
	 * 
	 */
	public enum DataStoreType {
		coveragestores, datastores;

		public static String getTypeName(DataStoreType type) {
			switch (type) {
			case coveragestores:
				return "coverages.xml"; // Format
			case datastores:
				return "featuretypes.xml";
			default:
				return "coverages.xml";
			}
		}
	}

	/**
	 * The file, url, and external endpoints are used to specify the method that
	 * is used to upload the file.
	 * 
	 * The file method is used to directly upload a file from a local source.
	 * The body of the request is the file itself.
	 * 
	 * The url method is used to indirectly upload a file from an remote source.
	 * The body of the request is a url pointing to the file to upload. This url
	 * must be visible from the server.
	 * 
	 * The external method is used to forgo upload and use an existing file on
	 * the server. The body of the request is the absolute path to the existing
	 * file.
	 * 
	 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
	 * 
	 */
	public enum UploadMethod {
		file, url, external
	}

	// ==========================================================================
	// === DATASTORE
	// ==========================================================================

	/**
	 * Create a PostGIS datastore.
	 * 
	 * @param workspace
	 *            Name of the workspace to contain the database. This will also
	 *            be the prefix of any layer names created from tables in the
	 *            database.
	 * @param datastoreEncoder
	 *            the set of parameters to be set to the datastore (including
	 *            connection params)
	 * 
	 * @return <TT>true</TT> if the PostGIS datastore has been successfully
	 *         created, <TT>false</TT> otherwise
	 */
	public boolean createPostGISDatastore(String workspace,
			GSPostGISDatastoreEncoder datastoreEncoder) {
		String sUrl = restURL + "/rest/workspaces/" + workspace
				+ "/datastores/";
		String xml = datastoreEncoder.toString();
		String result = HTTPUtils.postXml(sUrl, xml, gsuser, gspass);
		return result != null;
	}

	// ==========================================================================
	// === SHAPEFILES
	// ==========================================================================

	/**
	 * Publish a zipped shapefile. <BR>
	 * The defaultCRS will be set to EPSG:4326.
	 * 
	 * @see {@link #publishShp(String, String, NameValuePair[], String, UploadMethod, URI, String, ProjectionPolicy, String)}
	 * 
	 * @param workspace
	 * @param storename
	 * @param layername
	 * @param zipFile
	 * @return true if the operation completed successfully.
	 * @throws FileNotFoundException, IllegalArgumentException
	 */
	public boolean publishShp(String workspace, String storename,
			String datasetname, File zipFile) throws FileNotFoundException, IllegalArgumentException {
		return publishShp(workspace, storename, new NameValuePair[0], datasetname,UploadMethod.file, zipFile.toURI(), "EPSG:4326", ProjectionPolicy.NONE,null);
	}

	/**
	 * 
	 * Publish a shapefile.<br>
	 * 
	 * @param workspace
	 *            the name of the workspace to use
	 * @param storename
	 *            the name of the store to create
	 * @param storeParams
	 *            parameters to append to the url (can be null).<br>
	 *            Accepted parameters are:<br>
	 *            <ul>
	 *            <li><b>charset</b> used to set the charset</li>
	 *            </ul>
	 * @param layername
	 *            the name of the layer to configure
	 * @param method
	 *            {@link UploadMethod}
	 * @param fileUri
	 *            the uri of the file containing the shapefile.It should be:
	 *            <ul>
	 *            <li>A zip file if 'method' is file</li>
	 *            <li>A shp file if 'method' is external</li>
	 *            <li>A zip file if 'method' is uri (UNTESTED)</li>
	 *            </ul>
	 * @param srs
	 *            the native CRS
	 * @param policy
	 *            {@link ProjectionPolicy}
	 * @param defaultStyle
	 *            the default style to set (can be null).
	 * @return true if success false otherwise
	 * @throws FileNotFoundException
	 *             if file to upload is not found
	 * @throws IllegalArgumentException
	 *             if some arguments are null.
	 */
	public boolean publishShp(String workspace, String storeName,
			NameValuePair[] storeParams, String datasetName, UploadMethod method, URI shapefile,
			String srs, ProjectionPolicy policy, String defaultStyle)
			throws FileNotFoundException, IllegalArgumentException {
		if (workspace == null || storeName == null || shapefile == null
				|| datasetName == null || srs == null || policy == null)
			throw new IllegalArgumentException("Unable to run: null parameter");
		//
		final String mimeType;
		switch (method){
			case external:
				mimeType="text/plain";
				break;
			case url: // TODO check which mime-type should be used
			case file:
				mimeType="application/zip";
				break;
			default:
				mimeType=null;
		}
		if (!createDataStore(workspace,
				(storeName != null) ? storeName : FilenameUtils.getBaseName(shapefile.toString()), 
						method, DataStoreExtension.shp, mimeType,
				shapefile, ParameterConfigure.NONE, storeParams)) {
			LOGGER.error("Unable to create data store for shapefile: "
					+ shapefile);
			return false;
		}

		// config coverage props (srs)
		final GSFeatureTypeEncoder featureTypeEncoder = new GSFeatureTypeEncoder();
		featureTypeEncoder.setName(datasetName);
		featureTypeEncoder.setSRS(srs);
		featureTypeEncoder.setProjectionPolicy(policy);

		if (!createResource(workspace, DataStoreType.datastores, storeName,
				featureTypeEncoder)) {
			LOGGER.error("Unable to create a coverage store for coverage: "
					+ shapefile);
			return false;
		}

		// config layer props (style, ...)
		final GSLayerEncoder layerEncoder = new GSLayerEncoder();
		if (defaultStyle != null && !defaultStyle.isEmpty())
			layerEncoder.setDefaultStyle(defaultStyle);

		return configureLayer(workspace, datasetName, layerEncoder);
	}

	/**
 	 * @see {@link #publishShp(String, String, NameValuePair[], String, UploadMethod, URI, String, ProjectionPolicy, String)}
 	 * 
	 * Publish a zipped shapefile.
	 * 
	 * @param workspace
	 * @param storename
	 * @param layerName
	 * @param nativeCrs
	 * @param defaultStyle
	 *            may be null
	 * @return true if the operation completed successfully.
	 * @throws FileNotFoundException
	 */
	public boolean publishShp(String workspace, String storename,
			String layerName, File zipFile, String nativeCrs,
			String defaultStyle) throws FileNotFoundException, IllegalArgumentException {
		
		return publishShp(workspace, storename, (NameValuePair[])null, layerName, UploadMethod.file, zipFile.toURI(), nativeCrs, ProjectionPolicy.NONE,defaultStyle);
	}

	/**
	 * Publish a zipped shapefile.
	 * 
	 * @see {@link #publishShp(String, String, NameValuePair[], String, UploadMethod, URI, String, ProjectionPolicy, String)}
	 * 
	 * <P>
	 * These are the equivalent calls with cUrl:
	 * 
	 * <PRE>
	 * {@code
	 * curl -u admin:geoserver -XPUT -H 'Content-type: application/zip' \
	 *       --data-binary @$ZIPFILE \
	 *       http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/datastores/$STORENAME/file.shp
	 * 
	 * curl -u admin:geoserver -XPOST -H 'Content-type: text/xml'  \
	 *       -d "<featureType><name>$BARE</name><nativeCRS>EPSG:4326</nativeCRS><enabled>true</enabled></featureType>"  \
	 *       http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/datastores/$STORENAME/featuretypes/$LAYERNAME
	 *  }
	 * </PRE>
	 * 
	 * @return true if the operation completed successfully.
	 */
	public boolean publishShp(String workspace, String storename,
			String layername, File zipFile, String srs)
			throws FileNotFoundException {
		return publishShp(workspace, storename, (NameValuePair[])null, layername, UploadMethod.file, zipFile.toURI(), srs, ProjectionPolicy.NONE,null);
	}

	/**
	 * 
	 * Publish a zipped shapefile.<br>
	 * 
	 * @see {@link #publishShp(String, String, NameValuePair[], String, UploadMethod, URI, String, ProjectionPolicy, String)}
	 * 
	 * http://docs.geoserver.org/stable/en/user
	 * /restconfig/rest-config-examples/rest-
	 * config-examples-curl.html#uploading-a-shapefile
	 * 
	 * @param workspace
	 *            the name of the workspace to use
	 * @param storename
	 *            the name of the store to create
	 * @param layername
	 *            the name of the layer to configure
	 * @param zipFile
	 *            the zip file containing the shapefile
	 * @param srs
	 *            the native CRS
	 * @param params
	 *            parameters to append to the url (can be null).<br>
	 *            Accepted parameters are:<br>
	 *            <ul>
	 *            <li><b>charset</b> used to set the charset</li>
	 *            </ul>
	 * @return true if success false otherwise
	 * @throws FileNotFoundException, IllegalArgumentException
	 */
	public boolean publishShp(String workspace, String storename,
			String layername, File zipFile, String srs, NameValuePair... params)
			throws FileNotFoundException, IllegalArgumentException {

		return publishShp(workspace, storename, params, layername, UploadMethod.file, zipFile.toURI(), srs, ProjectionPolicy.NONE,null);
	}

	/**
	 * @param workspace
	 * @param storename
	 * @param layername
	 * @param srs
	 * @param defaultStyle
	 * @return
	 * @deprecated will be removed in the next release
	 */
	public boolean publishDBLayer(String workspace, String storename,
			String layername, String srs, String defaultStyle) {

		final GSFeatureTypeEncoder fte = new GSFeatureTypeEncoder();

		fte.setProjectionPolicy(ProjectionPolicy.REPROJECT_TO_DECLARED);
		fte.addKeyword("KEYWORD");
		fte.setName(layername);
		fte.setSRS(srs); // srs=null?"EPSG:4326":srs);
		final GSLayerEncoder layerEncoder = new GSLayerEncoder();
		layerEncoder.setDefaultStyle(defaultStyle);
		return publishDBLayer(workspace, storename, fte, layerEncoder);
	}

	/**
	 * 
	 * Publish a table in a PostGis store as a new layer.
	 * 
	 * <P>
	 * This is the equivalent call with cUrl:
	 * 
	 * <PRE>
	 * {@code curl -u admin:geoserver -XPOST -H 'Content-type: text/xml' \
	 *      -d "<featureType><name>easia_gaul_1_aggr</name><nativeCRS>EPSG:4326</nativeCRS><enabled>true</enabled></featureType>" \
	 *      http://localhost:8080/geoserver/rest/workspaces/it.geosolutions/datastores/pg_kids/featuretypes
	 * }
	 * </PRE>
	 * 
	 * and a PUT to <BR>
	 * restURL + "/rest/layers/" workspace + : + layerName
	 * 
	 * @param workspace
	 * @param storename
	 * @param fte
	 *            GSFeatureTypeEncoder encoding creating FeatureType
	 * @return true if layer is successfully created
	 * 
	 * 
	 */
	public boolean publishDBLayer(final String workspace,
			final String storename, final GSFeatureTypeEncoder fte,
			final GSLayerEncoder layerEncoder) {
		String ftypeXml = fte.toString();
		StringBuilder postUrl = new StringBuilder(restURL)
				.append("/rest/workspaces/").append(workspace)
				.append("/datastores/").append(storename)
				.append("/featuretypes");

		final String layername = fte.getName();
		if (layername == null || layername.isEmpty()) {
			if (LOGGER.isErrorEnabled())
				LOGGER.error("GSFeatureTypeEncoder has no valid name associated, try using GSFeatureTypeEncoder.setName(String)");
			return false;
		}

		String configuredResult = HTTPUtils.postXml(postUrl.toString(),
				ftypeXml, this.gsuser, this.gspass);
		boolean published = configuredResult != null;
		boolean configured = false;

		if (!published) {
			LOGGER.warn("Error in publishing (" + configuredResult + ") "
					+ workspace + ":" + storename + "/" + layername);
		} else {
			LOGGER.info("DB layer successfully added (layer:" + layername + ")");

			if (layerEncoder == null) {
				if (LOGGER.isErrorEnabled())
					LOGGER.error("GSLayerEncoder is null: Unable to find the defauldStyle for this layer");
				return false;
			}

			configured = configureLayer(workspace, layername, layerEncoder);

			if (!configured) {
				LOGGER.warn("Error in configuring (" + configuredResult + ") "
						+ workspace + ":" + storename + "/" + layername);
			} else {
				LOGGER.info("DB layer successfully configured (layer:"
						+ layername + ")");
			}
		}

		return published && configured;
	}

	/**
	 * The configure parameter is used to control how the data store is
	 * configured upon file upload. It can take one of the three values
	 * <i>first</i>, <i>none</i>, or <i>all</i>.
	 * <ul>
	 * <li><b>first</b> - This is the default.</li>
	 * <li><b>none</b> - Do not configure any feature types.</li>
	 * <li><b>all</b> - Configure all feature types.</li>
	 * </ul>
	 */
	public static enum ParameterConfigure {
		FIRST, NONE, ALL;

		/**
		 * Overrides the default toString method printing a toLowerCase
		 * representation of this Parameter which is suitable to build parameter
		 * in the rest URL
		 */
		@Override
		public String toString() {
			return this.name().toLowerCase();
		}
	}

	/**
	 * The update parameter is used to control how existing data is handled when
	 * the file is PUT into a datastore that (a) already exists and (b) already
	 * contains a schema that matches the content of the file. It can take one
	 * of the two values <i>append</i>, or <i>overwrite</i>.<br>
	 * append - Data being uploaded is appended to the existing data. This is
	 * the default.<br>
	 * overwrite - Data being uploaded replaces any existing data.<br>
	 * 
	 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
	 * 
	 */
	public static enum ParameterUpdate {
		APPEND, OVERWRITE;

		@Override
		public String toString() {
			return this.name().toLowerCase();
		}
	}

	// ==========================================================================
	// === COVERAGES
	// ==========================================================================

	/**
	 * 
	 * Publish a file.
	 * <P>
	 * This is an example with cUrl:
	 * 
	 * <PRE>
	 * {@code
	 * curl -u admin:geoserver -XPUT -H 'Content-type: application/zip' \
	 * 
	 *       --data-binary @$ZIPFILE \
	 * 
	 *       http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/coveragestores/$COVERAGESTORE/file.worldimage
	 * </PRE>
	 * 
	 * @param workspace
	 *            Workspace to use
	 * @param coveragestore
	 *            Name of the coveragestore
	 * @param file
	 *            file to upload
	 * @param configure
	 *            Configure parameter. It may be null.
	 * @param params
	 *            parameters to append to the url (can be null).<br>
	 *            Accepted parameters are:
	 *            <ul>
	 *            <li> <b>coverageName=name</b> coverageName parameter to
	 *            append. Only works if configure is not set to
	 *            ParameterConfigure.NONE. </li>
	 *            </ul>
	 * @see #{@link ParameterConfigure}
	 * @return true if the operation completed successfully.
	 */
	private boolean publishCoverage(String workspace, String coveragestore,
			CoverageStoreExtension extension, String mimeType, File file,
			ParameterConfigure configure, NameValuePair... params)
			throws FileNotFoundException {
		return createCoverageStore(workspace, coveragestore, UploadMethod.file,
				extension, mimeType, file.toURI(), configure, params);
	}

	/**
	 * Represents the format used to get, put or post information via ReST. For
	 * example consider the resource “foo”. To request a representation of foo
	 * as XML the request uri would end with “foo.xml”. To request as JSON the
	 * request uri would end with “foo.json”. When no format is specified the
	 * server will use its own internal format, usually html.
	 * 
	 * In a POST or PUT operation the format specifies 1) the representatin of
	 * the content being sent to the server, and 2) the representation of the
	 * resposne to be sent back. The former is specified with the Content-type
	 * header. To send a representation in XML, the content type “text/xml” or
	 * “application/xml” would be used. The latter is specified with the Accepts
	 * header as specified in the above paragraph describing a GET operation.
	 * 
	 * The following table defines the Content-type values for each format: <br>
	 * Format Content-type</br>
	 * <ul>
	 * <li>XML application/xml</li>
	 * <li>JSON application/json</li>
	 * <li>HTML application/html</li>
	 * <li>SLD application/vnd.ogc.sld+xml</li>
	 * </ul>
	 */
	public enum Format {
		XML, JSON, HTML, SLD;

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
			default:
				return null;
			}

		}
	}

	/**
	 * 
	 * The extension parameter specifies the type of data being uploaded. The
	 * following extensions are supported:
	 * <ul>
	 * <li>Extension:<b>geotiff</b> Datastore:<b>GeoTiff coverage</b></li>
	 * <li>Extension:<b>imagemosaic</b> Datastore:<b>ImageMosaic</b></li>
	 * <li>Extension:<b>worldimage</b> Datastore:<b>Geo referenced image
	 * (JPEG,PNG,TIF)</b></li>
	 * </ul>
	 * 
	 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
	 * 
	 */
	public enum CoverageStoreExtension {
		geotiff, imagemosaic, worldimage
	}

	/**
	 * 
	 * The extension parameter specifies the type of data being uploaded. The
	 * following extensions are supported:
	 * <ul>
	 * <li>Extension:<b>shp</b> Datastore:<b>Shapefile</b></li>
	 * <li>Extension:<b>properties</b> Datastore:<b>Property file</b></li>
	 * <li>Extension:<b>h2</b> Datastore:<b>H2 Database</b></li>
	 * <li>Extension:<b>spatialite</b> Datastore:<b>SpatiaLite Database</b></li>
	 * </ul>
	 * 
	 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
	 * 
	 */
	public enum DataStoreExtension {
		shp, properties, h2, spatialite
	}

	/**
	 * 
	 * Publish a file sending it to the GeoServer.
	 * 
	 * <P>
	 * Here is an example call with cUrl:
	 * 
	 * <PRE>
	 * {@code
	 * curl -u admin:geoserver -XPUT -H 'Content-type: application/zip' \
	 * 
	 *       --data-binary @$ZIPFILE \
	 * 
	 *       http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/coveragestores/$COVERAGESTORE/file.worldimage
	 * </PRE>
	 * 
	 * @param workspace
	 *            Workspace to use
	 * @param coveragestore
	 *            Name of the coveragestore
	 * @param file
	 *            file to upload
	 * @param configure
	 *            Configure parameter. It may be null.
	 * @param update
	 *            Accepted parameters are:
	 *            <ul>
	 *            <li> See <b>{@link #ParameterUpdate}</b> </li>
	 *            </ul>
	 * @see #{@link ParameterConfigure}
	 * @return true if the operation completed successfully.
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 */
	private boolean publishExternalCoverage(String workspace,
			String coveragestore, CoverageStoreExtension extension,
			String mimeType, File file, ParameterConfigure configure,
			ParameterUpdate update) throws FileNotFoundException,
			IllegalArgumentException {
		return createCoverageStore(workspace, coveragestore,
				UploadMethod.external, extension, mimeType, file.toURI(),
				configure,
				(update != null) ? new NameValuePair[] { new NameValuePair(
						"update", update.toString()) } : (NameValuePair[]) null);
	}

	// ==========================================================================
	// === GEOTIFF
	// ==========================================================================

	/**
	 * Simple wrapper for {@link #publishGeoTIFF(String, String, String, File)}
	 */
	public boolean publishGeoTIFF(String workspace, String storeName,
			File geotiff) throws FileNotFoundException {
		return publishCoverage(workspace, storeName,
				CoverageStoreExtension.geotiff, "image/geotiff", geotiff,
				ParameterConfigure.FIRST, (NameValuePair[]) null);
	}

	/**
	 * Publish a GeoTiff. Simple wrapper for
	 * {@link #publishCoverage(String, String, CoverageStoreExtension, String, File, ParameterConfigure, NameValuePair...)}
	 * <P>
	 * This is the equivalent call with cUrl:
	 * 
	 * <PRE>
	 * {@code
	 * curl -u admin:geoserver -XPUT -H 'Content-type: text' -d "file:$FULLPATH" \
	 *       http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/coveragestores/$STORENAME/external.geotiff
	 *  }
	 * </PRE>
	 * 
	 * @param workspace
	 *            Workspace to use
	 * @param storeName
	 *            Name of the coveragestore (if null the file name will be used)
	 * @param coverageName
	 *            the name of the coverage (if null the file name will be used)
	 * @param geotiff
	 *            file to upload
	 * @return true if the operation completed successfully.
	 * @throws FileNotFoundException
	 *             if file does not exists
	 * @throws IllegalArgumentException
	 *             if workspace or geotiff are null
	 */
	public boolean publishGeoTIFF(final String workspace,
			final String storeName, final String coverageName,
			final File geotiff) throws FileNotFoundException,
			IllegalArgumentException {
		if (workspace == null || geotiff == null)
			throw new IllegalArgumentException(
					"Unable to proceed, some arguments are null");

		return publishCoverage(
				workspace,
				(storeName != null) ? storeName : FilenameUtils
						.getBaseName(geotiff.getAbsolutePath()),
				CoverageStoreExtension.geotiff,
				"image/geotiff",
				geotiff,
				ParameterConfigure.FIRST,
				(coverageName != null) ? new NameValuePair[] { new NameValuePair(
						"coverageName", coverageName) }
						: (NameValuePair[]) null);
	}

	/**
	 * @see {@link #publishExternalGeoTIFF(String, String, File, String, String, ProjectionPolicy, String)}
	 * @param workspace
	 * @param storeName
	 * @param resourceName
	 * @param geotiff
	 * @param srs
	 * @param policy
	 * @param defaultStyle
	 * @return
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 */
	public boolean publishGeoTIFF(String workspace, String storeName,
			String resourceName, File geotiff, String srs,
			ProjectionPolicy policy, String defaultStyle)
			throws FileNotFoundException, IllegalArgumentException {
		if (workspace == null || storeName == null || geotiff == null
				|| resourceName == null || srs == null || policy == null
				|| defaultStyle == null)
			throw new IllegalArgumentException("Unable to run: null parameter");

		if (!createCoverageStore(workspace, (storeName != null) ? storeName
				: FilenameUtils.getBaseName(geotiff.getAbsolutePath()),
				UploadMethod.file, CoverageStoreExtension.geotiff,
				"image/geotiff", geotiff.toURI(), ParameterConfigure.NONE,
				(NameValuePair[]) null)) {
			LOGGER.error("Unable to create coverage store for coverage: "
					+ geotiff);
			return false;
		}

		// config coverage props (srs)
		final GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
		coverageEncoder.setName(resourceName);
		coverageEncoder.setSRS(srs);
		coverageEncoder.setProjectionPolicy(policy);

		if (!createCoverage(workspace, storeName, coverageEncoder)) {
			LOGGER.error("Unable to create a coverage store for coverage: "
					+ geotiff);
			return false;
		}

		// config layer props (style, ...)
		final GSLayerEncoder layerEncoder = new GSLayerEncoder();
		layerEncoder.setDefaultStyle(defaultStyle);

		return configureLayer(workspace, resourceName, layerEncoder);
	}

	/**
	 * 
	 * Publish a GeoTiff already in a filesystem readable by GeoServer.
	 * 
	 * @param workspace
	 *            an existing workspace
	 * @param storeName
	 *            the coverageStore to be created
	 * @param geotiff
	 *            the geoTiff to be published
	 * @param srs
	 * @param policy
	 * @param defaultStyle
	 * @return
	 * @throws FileNotFoundException
	 */
	public boolean publishExternalGeoTIFF(String workspace, String storeName,
			File geotiff, String coverageName, String srs,
			ProjectionPolicy policy, String defaultStyle)
			throws FileNotFoundException, IllegalArgumentException {
		if (workspace == null || storeName == null || geotiff == null
				|| coverageName == null || srs == null || policy == null
				|| defaultStyle == null)
			throw new IllegalArgumentException("Unable to run: null parameter");

		// config coverage props (srs)
		final GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
		coverageEncoder.setName(coverageName);
		coverageEncoder.setSRS(srs);
		coverageEncoder.setProjectionPolicy(policy);

		// config layer props (style, ...)
		final GSLayerEncoder layerEncoder = new GSLayerEncoder();
		layerEncoder.setDefaultStyle(defaultStyle);

		return publishExternalGeoTIFF(workspace, storeName, geotiff,
				coverageEncoder, layerEncoder) != null ? true : false;
	}

	/**
	 * Publish a GeoTiff already in a filesystem readable by GeoServer.
	 * 
	 * @param workspace
	 *            an existing workspace
	 * @param storeName
	 *            the coverageStore to be created
	 * @param geotiff
	 *            the geoTiff to be published
	 * @param coverageEncoder
	 * @param layerEncoder
	 * @return true if successfully configured
	 * 
	 * 
	 * @throws FileNotFoundException
	 * @throws IllegalArgumentException
	 *             if null parameter
	 */
	public RESTCoverageStore publishExternalGeoTIFF(final String workspace,
			final String storeName, final File geotiff,
			final GSCoverageEncoder coverageEncoder,
			final GSLayerEncoder layerEncoder) throws IllegalArgumentException,
			FileNotFoundException {

		if (workspace == null || geotiff == null || storeName == null
				|| layerEncoder == null || coverageEncoder == null)
			throw new IllegalArgumentException("Unable to run: null parameter");

		final String coverageName = coverageEncoder.getName();
		if (coverageName.isEmpty()) {
			throw new IllegalArgumentException(
					"Unable to run: empty coverage store name");
		}

		// create store
		final boolean store = publishExternalCoverage(workspace, storeName,
				CoverageStoreExtension.geotiff, "text/plain", geotiff,
				ParameterConfigure.NONE, ParameterUpdate.OVERWRITE);
		if (!store) {
			return null;
		}

		// create Coverage Store
		if (!createCoverage(workspace, storeName, coverageEncoder)) {
			if (LOGGER.isErrorEnabled())
				LOGGER.error("Unable to create a coverage for the store:"
						+ coverageName);
			return null;
		}

		// create Layer
		if (configureLayer(workspace, coverageName, layerEncoder)) {
			GeoServerRESTReader reader;
			try {
				reader = new GeoServerRESTReader(this.restURL, this.gsuser,
						this.gspass);
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
	 * {@link #publishWorldImage(String, String, File, ParameterConfigure, NameValuePair...)}
	 */
	public boolean publishWorldImage(String workspace, String coveragestore,
			File zipFile) throws FileNotFoundException {
		return publishWorldImage(workspace, coveragestore, zipFile,
				ParameterConfigure.FIRST, (NameValuePair) null);
	}

	/**
	 * 
	 * Publish a zipped worldimage file. It is assumed that the the zip-file
	 * contain the *.prj to set the srs.
	 * <P>
	 * This is equivalent call with cUrl:
	 * 
	 * <PRE>
	 * {@code
	 * curl -u admin:geoserver -XPUT -H 'Content-type: application/zip' \
	 * 
	 *       --data-binary @$ZIPFILE \
	 * 
	 *       http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/coveragestores/$COVERAGESTORE/file.worldimage
	 * </PRE>
	 * 
	 * @param workspace
	 *            Workspace to use
	 * @param coveragestore
	 *            Name of the coveragestore
	 * @param zipFile
	 *            zip file to upload
	 * @param configure
	 *            Configure parameter. It may be null.
	 * @param params
	 *            parameters to append to the url (can be null).<br>
	 *            Accepted parameters are:
	 *            <ul>
	 *            <li> <b>coverageName=name</b> coverageName parameter to
	 *            append. Only works if configure is not set to
	 *            ParameterConfigure.NONE. </li>
	 *            </ul>
	 * @see #{@link ParameterConfigure}
	 * @return true if the operation completed successfully.
	 */
	public boolean publishWorldImage(String workspace, String coveragestore,
			File zipFile, ParameterConfigure configure, NameValuePair... params)
			throws FileNotFoundException {
		return publishCoverage(workspace, coveragestore,
				CoverageStoreExtension.worldimage, "application/zip", zipFile,
				configure, params);
	}

	// ==========================================================================
	// === MOSAIC
	// ==========================================================================

	/**
	 * Publish imagemosaic as zip file
	 * 
	 * @see {@link #publishWorldImage(String, String, File)}
	 */
	public boolean publishImageMosaic(String workspace, String storeName,
			File zipFile) throws FileNotFoundException {
		return publishCoverage(workspace, storeName,
				CoverageStoreExtension.imagemosaic, "application/zip", zipFile,
				ParameterConfigure.FIRST, (NameValuePair[]) null);
	}

	/**
	 * Publish imagemosaic as zip file
	 * 
	 * @see {@link #publishWorldImage(String, String, File, ParameterConfigure, NameValuePair...)}
	 */
	public boolean publishImageMosaic(String workspace, String storeName,
			File zipFile, ParameterConfigure configure, NameValuePair... params)
			throws FileNotFoundException {
		return publishCoverage(workspace, storeName,
				CoverageStoreExtension.imagemosaic, "application/zip", zipFile,
				configure, params);
	}

	/**
	 * Publish a Mosaic from a filesystem currently readable by GeoServer.
	 * 
	 * <P>
	 * Sample cUrl usage:<BR>
	 * <>
	 * <TT>curl -u admin:geoserver -XPUT -H 'Content-type: text' -d "file:$ABSPORTDIR"
	 *          http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/coveragestores/$BAREDIR/external.imagemosaic </TT>
	 * 
	 * @param workspace
	 *            an existing workspace
	 * @param storeName
	 *            the name of the coverageStore to be created
	 * @param mosaicDir
	 *            the directory where the raster images are located
	 * @param configure
	 *            a specify if a coverage should be configured
	 * @return true if the operation completed successfully.
	 * @throws FileNotFoundException
	 */
	public RESTCoverageStore createExternaMosaicDatastore(String workspace,
			String storeName, File mosaicDir, ParameterConfigure configure,
			ParameterUpdate update) throws FileNotFoundException {

		/*
		 * Carlo (23 Nov 2011): commented out since this directory should be
		 * readable by target GeoServer not the calling client!
		 */
		if (!mosaicDir.isDirectory()) {
			if (LOGGER.isWarnEnabled())
				LOGGER.warn("Directory '"
						+ mosaicDir
						+ "' not exists locally. Continue: please check existance on the remote server.");
		}

		String sUrl = restURL + "/rest/workspaces/" + workspace
				+ "/coveragestores/" + storeName
				+ "/external.imagemosaic?configure=" + configure.toString()
				+ "&update=" + update.toString();
		String sendResult = HTTPUtils.put(sUrl, mosaicDir.toURI().toString(),
				"text/plain", gsuser, gspass);
		return RESTCoverageStore.build(sendResult);
	}

	/**
	 * Publish a Mosaic already in a filesystem readable by GeoServer.
	 * 
	 * <P>
	 * Sample cUrl usage:<BR>
	 * <TT>curl -u admin:geoserver -XPUT -H 'Content-type: text' -d "file:$ABSPORTDIR"
	 *          http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/coveragestores/$BAREDIR/external.imagemosaic </TT>
	 * 
	 * @param workspace
	 *            an existing workspace
	 * @param storeName
	 *            the name of the coverageStore to be created
	 * @param mosaicDir
	 *            the directory where the raster images are located
	 * @param srs
	 *            the coverage declared SRS
	 * @param defaultStyle
	 *            may be null
	 * 
	 * @return true if the operation completed successfully.
	 * 
	 * @throws FileNotFoundException
	 */
	public boolean publishExternalMosaic(String workspace, String storeName,
			File mosaicDir, String srs, String defaultStyle)
			throws FileNotFoundException {

		final GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
		coverageEncoder.setSRS(srs);
		final String name = FilenameUtils.getBaseName(mosaicDir.getName());
		coverageEncoder.setName(name);

		final GSLayerEncoder layerEncoder = new GSLayerEncoder();
		layerEncoder.setDefaultStyle(defaultStyle);

		return publishExternalMosaic(workspace, storeName, mosaicDir,
				coverageEncoder, layerEncoder);
	}

	/**
	 * @deprecated use {@link #publishExternalMosaic(String workspace, final
	 *             String storeName, File mosaicDir, GSCoverageEncoder
	 *             coverageEncoder, GSLayerEncoder layerEncoder)}
	 * @param workspace
	 * @param storeName
	 * @param mosaicDir
	 * @param coverageEncoder
	 * @param layerEncoder
	 * @return
	 * @throws FileNotFoundException
	 */
	public boolean createExternalMosaic(String workspace, String storeName,
			File mosaicDir, GSCoverageEncoder coverageEncoder,
			GSLayerEncoder layerEncoder) throws FileNotFoundException {
		return publishExternalMosaic(workspace, storeName, mosaicDir,
				coverageEncoder, layerEncoder);
	}

	/**
	 * Publish a Mosaic already in a filesystem readable by GeoServer.
	 * 
	 * <P>
	 * Sample cUrl usage:<BR>
	 * <TT>curl -u admin:geoserver -XPUT -H 'Content-type: text' -d "file:$ABSPORTDIR"
	 *          http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/coveragestores/$BAREDIR/external.imagemosaic </TT>
	 * 
	 * @param workspace
	 *            an existing workspace
	 * @param storeName
	 *            the name of the coverageStore to be created
	 * @param mosaicDir
	 *            the directory where the raster images are located
	 * @param coverageEncoder
	 *            the set of parameters to be set to the coverage (bbox, srs,
	 *            ...)
	 * @param layerEncoder
	 *            the set of parameters to be set to the layer (defaultstyle,
	 *            wmspath, ...)
	 * @return true if the operation completed successfully.
	 * 
	 * @throws FileNotFoundException
	 */
	public boolean publishExternalMosaic(String workspace,
			final String storeName, File mosaicDir,
			GSCoverageEncoder coverageEncoder, GSLayerEncoder layerEncoder)
			throws FileNotFoundException, IllegalArgumentException {

		if (coverageEncoder == null) {
			throw new IllegalArgumentException(
					"no coverageEncoder provided for mosaic " + mosaicDir);
		}
		// override name to match the FIRST configured coverage
		String coverageName = coverageEncoder.getName();

		if (layerEncoder == null) {
			throw new IllegalArgumentException("no layerEncoder provided for "
					+ workspace + ":" + coverageName);
		}

		RESTCoverageStore store = createExternaMosaicDatastore(workspace,
				storeName, mosaicDir, ParameterConfigure.NONE,
				ParameterUpdate.OVERWRITE);

		if (store == null) {
			return false;
		}
		if (!createCoverage(workspace, storeName, coverageEncoder)) {
			if (LOGGER.isErrorEnabled())
				LOGGER.error("Unable to create a coverage for the store:"
						+ coverageName);
			return false;
		}
		if (!configureLayer(workspace, coverageName, layerEncoder)) {
			if (LOGGER.isErrorEnabled())
				LOGGER.error("Unable to configure the Layer for the coverage:"
						+ coverageName);
			return false;
		}
		return true;
	}

	// ==========================================================================
	// === COVERAGES
	// ==========================================================================

	/**
	 * Remove the Coverage configuration from GeoServer. <BR>
	 * First, the associated layer is removed, then the Coverage configuration
	 * itself.
	 * <P>
	 * <B>CHECKME</B> Maybe the coveragestore has to be removed as well.
	 * 
	 * <P>
	 * REST URL:
	 * <TT>http://localhost:8080/geoserver/rest/workspaces/it.geosolutions/coveragestores/gbRESTtestStore/coverages/resttestdem.xml</TT>
	 * 
	 * @return true if the operation completed successfully.
	 */
	public boolean unpublishCoverage(String workspace, String storename,
			String layerName) {
		try {
			final String fqLayerName;

			// this null check is here only for backward compatibility.
			// workspace
			// shall be mandatory.
			if (workspace == null) {

				fqLayerName = layerName;

				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("Null workspace while configuring layer : "
							+ layerName + " -- This behavior is deprecated.");
				}
			} else {
				fqLayerName = workspace + ":" + layerName;
			}
			// delete related layer
			URL deleteLayerUrl = new URL(restURL + "/rest/layers/"
					+ fqLayerName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Going to delete " + "/rest/layers/" + fqLayerName);
			}
			boolean layerDeleted = HTTPUtils.delete(
					deleteLayerUrl.toExternalForm(), gsuser, gspass);
			if (!layerDeleted) {
				LOGGER.warn("Could not delete layer '" + fqLayerName + "'");
				return false;
			}
			// delete the coverage
			URL deleteCovUrl = new URL(restURL + "/rest/workspaces/"
					+ workspace + "/coveragestores/" + storename
					+ "/coverages/" + layerName);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Going to delete " + "/rest/workspaces/"
						+ workspace + "/coveragestores/" + storename
						+ "/coverages/" + layerName);
			}
			boolean covDeleted = HTTPUtils.delete(
					deleteCovUrl.toExternalForm(), gsuser, gspass);
			if (!covDeleted) {
				LOGGER.warn("Could not delete coverage " + workspace + ":"
						+ storename + "/" + layerName
						+ ", but layer was deleted.");
			} else {
				LOGGER.info("Coverage successfully deleted " + workspace + ":"
						+ storename + "/" + layerName);
			}
			return covDeleted;

			// the covstore is still there: should we delete it?

		} catch (MalformedURLException ex) {
			if (LOGGER.isErrorEnabled())
				LOGGER.error(ex.getLocalizedMessage(),ex);
			return false;
		}
	}

	// ==========================================================================
	// === FEATURETYPES
	// ==========================================================================

	/**
	 * Removes the featuretype and the associated layer. <BR>
	 * You may also want to {@link #removeDatastore(String, String) remove the
	 * datastore}.
	 * 
	 * @return true if the operation completed successfully.
	 */
	public boolean unpublishFeatureType(String workspace, String storename,
			String layerName) {
		try {

			final String fqLayerName;
			// this null check is here only for backward compatibility.
			// workspace
			// shall be mandatory.
			if (workspace == null) {

				fqLayerName = layerName;

				if (LOGGER.isWarnEnabled()) {
					LOGGER.warn("Null workspace while configuring layer : "
							+ layerName + " -- This behavior is deprecated.");
				}
			} else {
				fqLayerName = workspace + ":" + layerName;
			}
			// delete related layer
			URL deleteLayerUrl = new URL(restURL + "/rest/layers/"
					+ fqLayerName);
			boolean layerDeleted = HTTPUtils.delete(
					deleteLayerUrl.toExternalForm(), gsuser, gspass);
			if (!layerDeleted) {
				LOGGER.warn("Could not delete layer '" + fqLayerName + "'");
				return false;
			}
			// delete the coverage
			URL deleteFtUrl = new URL(restURL + "/rest/workspaces/" + workspace
					+ "/datastores/" + storename + "/featuretypes/" + layerName);
			boolean ftDeleted = HTTPUtils.delete(deleteFtUrl.toExternalForm(),
					gsuser, gspass);
			if (!ftDeleted) {
				LOGGER.warn("Could not delete featuretype " + workspace + ":"
						+ storename + "/" + layerName
						+ ", but layer was deleted.");
			} else {
				LOGGER.info("FeatureType successfully deleted " + workspace
						+ ":" + storename + "/" + layerName);
			}

			return ftDeleted;

			// the store is still there: should we delete it?

		} catch (MalformedURLException ex) {
			if (LOGGER.isErrorEnabled())
				LOGGER.error(ex.getLocalizedMessage(),ex);
			return false;
		}
	}

	/**
	 * Remove recursively a given Datastore in a given Workspace.
	 * 
	 * @param workspace
	 *            The name of the workspace
	 * @param storename
	 *            The name of the Datastore to remove.
	 * @return <TT>true</TT> if the datastore was successfully removed.
	 * 
	 * @deprecated will be removed in next release use
	 *             {@link GeoServerRESTPublisher#removeDatastore(String, String, boolean)}
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
	 * @param workspace
	 *            The name of the workspace
	 * @param storename
	 *            The name of the Datastore to remove.
	 * @param recurse
	 *            if remove should be performed recursively
	 * @throws IllegalArgumentException
	 *             if workspace or storename are null or empty
	 * @return <TT>true</TT> if the datastore was successfully removed.
	 */
	public boolean removeDatastore(String workspace, String storename,
			final boolean recurse) throws IllegalArgumentException {
		try {
			if (workspace == null || storename == null)
				throw new IllegalArgumentException("Arguments may not be null!");
			if (workspace.isEmpty() || storename.isEmpty())
				throw new IllegalArgumentException(
						"Arguments may not be empty!");

			final StringBuilder url = new StringBuilder(restURL);
			url.append("/rest/workspaces/").append(workspace)
					.append("/datastores/").append(storename);
			if (recurse)
				url.append("?recurse=true");
			final URL deleteStore = new URL(url.toString());

			boolean deleted = HTTPUtils.delete(deleteStore.toExternalForm(),
					gsuser, gspass);
			if (!deleted) {
				LOGGER.warn("Could not delete datastore " + workspace + ":"
						+ storename);
			} else {
				LOGGER.info("Datastore successfully deleted " + workspace + ":"
						+ storename);
			}

			return deleted;
		} catch (MalformedURLException ex) {
			if (LOGGER.isErrorEnabled())
				LOGGER.error(ex.getLocalizedMessage(),ex);
			return false;
		}
	}

	/**
	 * Remove recursively a given CoverageStore in a given Workspace.
	 * 
	 * @param workspace
	 *            The name of the workspace
	 * @param storename
	 *            The name of the CoverageStore to remove.
	 * @return <TT>true</TT> if the CoverageStore was successfully removed.
	 * @deprecated use {@link #removeCoverageStore(String, String, boolean)}
	 */
	public boolean removeCoverageStore(String workspace, String storename) {
		try {
			return removeCoverageStore(workspace, storename, true);
		} catch (IllegalArgumentException e) {
			if (LOGGER.isErrorEnabled())
				LOGGER.error("Arguments may not be null or empty!", e);
		}
		return false;
	}

	/**
	 * Remove a given CoverageStore in a given Workspace.
	 * 
	 * @param workspace
	 *            The name of the workspace
	 * @param storename
	 *            The name of the CoverageStore to remove.
	 * @param recurse
	 *            if remove should be performed recursively
	 * @return <TT>true</TT> if the CoverageStore was successfully removed.
	 */
	public boolean removeCoverageStore(final String workspace,
			final String storename, final boolean recurse)
			throws IllegalArgumentException {
		try {
			if (workspace == null || storename == null)
				throw new IllegalArgumentException("Arguments may not be null!");
			if (workspace.isEmpty() || storename.isEmpty())
				throw new IllegalArgumentException(
						"Arguments may not be empty!");

			final StringBuilder url = new StringBuilder(restURL);
			url.append("/rest/workspaces/").append(workspace)
					.append("/coveragestores/").append(storename);
			if (recurse)
				url.append("?recurse=true");
			final URL deleteStore = new URL(url.toString());

			boolean deleted = HTTPUtils.delete(deleteStore.toExternalForm(),
					gsuser, gspass);
			if (!deleted) {
				LOGGER.warn("Could not delete CoverageStore " + workspace + ":"
						+ storename);
			} else {
				LOGGER.info("CoverageStore successfully deleted " + workspace
						+ ":" + storename);
			}
			return deleted;

		} catch (MalformedURLException ex) {
			if (LOGGER.isErrorEnabled())
				LOGGER.error(ex.getLocalizedMessage(),ex);
			return false;
		}
	}

	/**
	 * Remove the workspace given Workspace using default parameters
	 * 
	 * @see {@link GeoServerRESTPublisher#removeWorkspace(String, boolean)}
	 * @param workspace
	 *            the workspace to remove
	 * @return true if success, false otherwise
	 * @deprecated {@link #removeWorkspace(String, boolean)}
	 */
	public boolean removeWorkspace(String workspace) {
		try {
			return removeWorkspace(workspace, false);
		} catch (IllegalArgumentException e) {
			if (LOGGER.isErrorEnabled())
				LOGGER.error("Arguments may not be null or empty!", e);
		}
		return false;
	}

	/**
	 * Remove a given Workspace.
	 * 
	 * @param workspace
	 *            The name of the workspace
	 * @param recurse
	 *            The recurse parameter is used to recursively delete all
	 *            resources contained by the specified workspace. This includes
	 *            data stores, coverage stores, feature types, etc... Allowable
	 *            values for this parameter are <i>true</i> or <i>false</i>. The
	 *            default value is <i>false</i>.
	 * @return <TT>true</TT> if the WorkSpace was successfully removed.
	 */
	public boolean removeWorkspace(String workspace, boolean recurse)
			throws IllegalArgumentException {
		workspace = sanitize(workspace);
		try {
			if (workspace == null)
				throw new IllegalArgumentException("Arguments may not be null!");
			if (workspace.isEmpty())
				throw new IllegalArgumentException(
						"Arguments may not be empty!");

			StringBuffer url = new StringBuffer(restURL).append(
					"/rest/workspaces/").append(workspace);
			if (recurse)
				url.append("?recurse=true");
			final URL deleteUrl = new URL(url.toString());
			boolean deleted = HTTPUtils.delete(deleteUrl.toExternalForm(),
					gsuser, gspass);
			if (!deleted) {
				LOGGER.warn("Could not delete Workspace " + workspace);
			} else {
				LOGGER.info("Workspace successfully deleted " + workspace);
			}

			return deleted;
		} catch (MalformedURLException ex) {
			if (LOGGER.isErrorEnabled())
				LOGGER.error(ex.getLocalizedMessage(),ex);
			return false;
		}
	}

	/**
	 * reload the target geoserver configuration
	 * 
	 * @return true if success
	 * 
	 * @see http 
	 *      ://docs.geoserver.org/stable/en/user/restconfig/rest-config-api.html
	 */
	public boolean reload() {
		String sUrl = restURL + "/rest/reload";
		String result = HTTPUtils.post(sUrl, "", "text/plain", gsuser, gspass);
		return result != null;
	}

	/**
	 * reset the target geoserver configuration
	 * 
	 * @return true if success
	 * 
	 * @see http 
	 *      ://docs.geoserver.org/stable/en/user/restconfig/rest-config-api.html
	 */
	public boolean reset() {
		String sUrl = restURL + "/rest/reset";
		String result = HTTPUtils.post(sUrl, "", "text/plain", gsuser, gspass);
		return result != null;
	}

	public boolean removeLayerGroup(String name) {
		try {
			URL deleteUrl = new URL(restURL + "/rest/layergroups/" + name);
			boolean deleted = HTTPUtils.delete(deleteUrl.toExternalForm(),
					gsuser, gspass);
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
				LOGGER.error(ex.getLocalizedMessage(),ex);
			return false;
		}
	}

	// ==========================================================================
	// ===
	// ==========================================================================

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
				LOGGER.warn("Null workspace while removing layer : "
						+ layerName + " -- This behavior is deprecated.");
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

	/**
	 * Allows to configure some layer attributes such and DefaultStyle
	 * 
	 * @param workspace
	 * @param resourceName
	 *            the name of the resource to use (featureStore or coverageStore
	 *            name)
	 * @param layer
	 *            the layer encoder used to configure the layer
	 * @return true if success
	 * @throws IllegalArgumentException
	 *             if some arguments are null or empty
	 * 
	 * @TODO WmsPath
	 */
	public boolean configureLayer(final String workspace,
			final String resourceName, final GSLayerEncoder layer)
			throws IllegalArgumentException {

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
				LOGGER.warn("Error configuring layer " + fqLayerName + " ("
						+ sendResult + ")");
		}

		return sendResult != null;
	}

	/**
	 * Configure an existent coverage in a given workspace and coverage store
	 * 
	 * @param ce
	 *            contains the coverage name to configure and the configuration
	 *            to apply
	 * @param wsname
	 *            the workspace to search for existent coverage
	 * @param csname
	 *            the coverage store to search for existent coverage
	 * @return true if success
	 */
	public boolean configureCoverage(final GSCoverageEncoder ce,
			final String wsname, final String csname) {
		final String cname = ce.getName();
		if (cname == null) {
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
				LOGGER.error(e.getLocalizedMessage(),e);
			return false;
		}
		final RESTCoverageList covList = reader.getCoverages(wsname, csname);
		if (covList.isEmpty()) {
			if (LOGGER.isErrorEnabled())
				LOGGER.error("No coverages found in new coveragestore "
						+ csname);
			return false;
		}
		final Iterator<NameLinkElem> it = covList.iterator();
		boolean found = false;
		while (it.hasNext()) {
			NameLinkElem nameElem = it.next();
			if (nameElem.getName().equals(cname)) {
				found = true;
				break;
			}
		}
		// if no coverage to configure is found return false
		if (!found) {
			if (LOGGER.isErrorEnabled())
				LOGGER.error("No coverages found in new coveragestore "
						+ csname + " called " + cname);
			return false;
		}

		// configure the selected coverage
		final String url = restURL + "/rest/workspaces/" + wsname
				+ "/coveragestores/" + csname + "/coverages/" + cname + ".xml";

		final String xmlBody = ce.toString();
		final String sendResult = HTTPUtils
				.putXml(url, xmlBody, gsuser, gspass);
		if (sendResult != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Coverage successfully configured " + wsname + ":"
						+ csname + ":" + cname);
			}
		} else {
			if (LOGGER.isWarnEnabled())
				LOGGER.warn("Error configuring coverage " + wsname + ":"
						+ csname + ":" + cname + " (" + sendResult + ")");
		}

		return sendResult != null;
	}

	/**
	 * @deprecated use
	 *             {@link #createCoverage(String, String, GSCoverageEncoder)}
	 * @param ce
	 * @param wsname
	 * @param csname
	 * @return
	 */
	public boolean createCoverage(final GSCoverageEncoder ce,
			final String wsname, final String csname) {
		return createCoverage(wsname, csname, ce);
	}

	/**
	 * Create a new coverage in a given workspace and coverage store
	 * 
	 * @param wsname
	 *            the workspace to search for existent coverage
	 * @param storeName
	 *            an existent store name to use as data source
	 * @param ce
	 *            contains the coverage name to create and the configuration to
	 *            apply
	 * @return true if success
	 * @throws IllegalArgumentException
	 *             if arguments are null or empty
	 */
	public boolean createCoverage(final String wsname, final String storeName,
			final GSCoverageEncoder ce) throws IllegalArgumentException {
		return createResource(wsname, DataStoreType.coveragestores, storeName,
				ce);
	}

	/**
	 * Create a new resource in a given workspace and store
	 * 
	 * @param wsname
	 *            the workspace to search for existent coverage
	 * @param storeName
	 *            an existent store name to use as data source
	 * @param re
	 *            contains the coverage name to create and the configuration to
	 *            apply
	 * 
	 * @TODO For FeatureType: The list parameter is used to control the category
	 *       of feature types that are returned. It can take one of the three
	 *       values configured, available, or all.
	 * 
	 *       configured - Only setup or configured feature types are returned.
	 *       This is the default value. available - Only unconfigured feature
	 *       types (not yet setup) but are available from the specified
	 *       datastore will be returned. available_with_geom - Same as available
	 *       but only includes feature types that have a geometry attribute. all
	 *       - The union of configured and available.
	 * 
	 * 
	 * @return true if success
	 * @throws IllegalArgumentException
	 *             if arguments are null or empty
	 */
	private boolean createResource(String workspace, DataStoreType dsType,
			String storeName, GSResourceEncoder re)
			throws IllegalArgumentException {
		if (workspace == null || dsType == null || storeName == null
				|| re == null) {
			throw new IllegalArgumentException("Null argument");
		}
		StringBuilder sbUrl = new StringBuilder(restURL)
				.append("/rest/workspaces/").append(workspace).append("/")
				.append(dsType).append("/").append(storeName).append("/")
				.append(DataStoreType.getTypeName(dsType));

		final String resourceName = re.getName();
		if (resourceName == null) {
			throw new IllegalArgumentException(
					"Unable to configure a coverage using unnamed coverage encoder");
		}

		final String xmlBody = re.toString();
		final String sendResult = HTTPUtils.postXml(sbUrl.toString(), xmlBody,
				gsuser, gspass);
		if (sendResult != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(dsType + " successfully created " + workspace
						+ ":" + storeName + ":" + resourceName);
			}
		} else {
			if (LOGGER.isErrorEnabled())
				LOGGER.error("Error creating coverage " + workspace + ":"
						+ storeName + ":" + resourceName + " (" + sendResult
						+ ")");
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

	/**
	 * Append params generating a string in the form: <br>
	 * <p>
	 * NAME_0=VALUE_0&NAME_1=VALUE_1&....&NAME_n-1=VALUE_n-1
	 * </p>
	 * </br>
	 * 
	 * @param params
	 *            an array of NameValuePair
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
					if (name != null && !name.isEmpty() && value != null
							&& !value.isEmpty()) {
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
						if (name != null && !name.isEmpty() && value != null
								&& !value.isEmpty()) {
							sbUrl.append("&").append(name).append("=")
									.append(value);
						}

					}

				}
			}
		}
		return sbUrl.toString();
	}

	protected String encode(String s) {
		// try {
		// return URLEncoder.encode(s,"UTF-8");
		// } catch (UnsupportedEncodingException e) {
		// LOGGER.warn("Error encoding :"+s+" with UTF-8: "+e.getLocalizedMessage());
		return URLEncoder.encode(s);
		// }
	}
}

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
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.GSPostGISDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder.ProjectionPolicy;
import it.geosolutions.geoserver.rest.encoder.GSWorkspaceEncoder;
import it.geosolutions.geoserver.rest.encoder.coverage.GSCoverageEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * Connect to a GeoServer instance to publish or modify data.
 * <P>
 * There are no modifiable instance fields, so all the calls are thread-safe.
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class GeoServerRESTPublisher {

	private static final Logger LOGGER = Logger
			.getLogger(GeoServerRESTPublisher.class);
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
		String sUrl = restURL + "/rest/styles";
		String result = HTTPUtils.post(sUrl, sldBody,
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
		if (name != null) {
			sUrl += "?name=" + encode(name);
		}
		LOGGER.debug("POSTing new style " + name + " to " + sUrl);
		String result = HTTPUtils.post(sUrl, sldFile,
				"application/vnd.ogc.sld+xml", gsuser, gspass);
		return result != null;
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
		styleName = styleName.replaceAll(":", "_"); // ???
		styleName = encode(styleName); // spaces may
		String sUrl = restURL + "/rest/styles/" + styleName + "?purge=true";
		return HTTPUtils.delete(sUrl, gsuser, gspass);
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
	 * The CRS will be forced to EPSG:4326.
	 * 
	 * @param workspace
	 * @param storename
	 * @param layername
	 * @param zipFile
	 * @return true if the operation completed successfully.
	 * @throws FileNotFoundException
	 */
	public boolean publishShp(String workspace, String storename,
			String layername, File zipFile) throws FileNotFoundException {
		return publishShp(workspace, storename, layername, zipFile, "EPSG:4326");
	}

	/**
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
			String defaultStyle) throws FileNotFoundException {
		boolean sent = publishShp(workspace, storename, layerName, zipFile,
				nativeCrs);
		if (sent) {

			try {
				GSLayerEncoder layerEncoder = new GSLayerEncoder();
				layerEncoder.addDefaultStyle(defaultStyle);
				configureLayer(workspace, layerName, layerEncoder);
			} catch (Exception e) {
				LOGGER.warn("Error in publishing shapefile " + e.getMessage(),
						e);
				sent = false;
			}
		}

		return sent;
	}

	/**
	 * Publish a zipped shapefile.
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
	 * {@link #publishShp(String, String, String, File, String, NameValuePair...)}
	 * 
	 * @return true if the operation completed successfully.
	 */
	public boolean publishShp(String workspace, String storename,
			String layername, File zipFile, String srs)
			throws FileNotFoundException {
		return publishShp(workspace, storename, layername, zipFile, srs,
				new NameValuePair[0]);
	}

	/**
	 * 
	 * Publish a zipped shapefile.<br>
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
	 * @throws FileNotFoundException
	 */
	public boolean publishShp(String workspace, String storename,
			String layername, File zipFile, String srs, NameValuePair... params)
			throws FileNotFoundException {
		// build full URL
		StringBuilder sbUrl = new StringBuilder(restURL)
				.append("/rest/workspaces/").append(workspace)
				.append("/datastores/").append(storename).append("/file.shp?");

		// append parameters
		if (params != null) {
			final int paramsSize = params.length;
			if (paramsSize > 0) {
				sbUrl.append(params[0].getName()).append("=")
						.append(params[0].getValue());
				for (int i = 1; i < paramsSize; i++) {
					sbUrl.append("&").append(params[i].getName()).append("=")
							.append(params[i].getValue());
				}
			}
		}

		// if (workspace != null) {
		// sbUrl.append("namespace=").append(workspace);
		// }
		// sbUrl.append("&SRS=4326&SRSHandling=Force"); // hack

		String sentResult = HTTPUtils.put(sbUrl.toString(), zipFile,
				"application/zip", gsuser, gspass);
		boolean shpSent = sentResult != null;

		if (shpSent) {
			LOGGER.info("Zipfile successfully uploaded (layer:" + layername
					+ " zip:" + zipFile + ")");

			StringBuilder postUrl = new StringBuilder(restURL)
					.append("/rest/workspaces/").append(workspace)
					.append("/datastores/").append(storename)
					.append("/featuretypes/").append(layername);

			GSFeatureTypeEncoder fte = new GSFeatureTypeEncoder();
			fte.addName(layername);
			fte.addSRS(srs);
			fte.addProjectionPolicy(ProjectionPolicy.REPROJECT_TO_DECLARED);

			String configuredResult = HTTPUtils.putXml(postUrl.toString(),
					fte.toString(), this.gsuser, this.gspass);
			boolean shpConfigured = configuredResult != null;

			if (!shpConfigured) {
				LOGGER.warn("Error in configuring " + workspace + ":"
						+ storename + "/" + layername
						+ " -- Zipfile was uploaded successfully: " + zipFile);
			} else {
				LOGGER.info("Shapefile successfully configured (layer:"
						+ layername + ")");
			}

			return shpConfigured;

		} else {
			LOGGER.warn("Error in sending zipfile " + workspace + ":"
					+ storename + "/" + layername + " " + zipFile);
			return false;
		}

	}

	/**
	 * @deprecated use {@link publishDBLayer(final String workspace, final
	 *             String storename, final GSFeatureTypeEncoder fte, final
	 *             GSLayerEncoder layerEncoder)}
	 * @param workspace
	 * @param storename
	 * @param layername
	 * @param srs
	 * @param defaultStyle
	 * @return
	 */
	public boolean publishDBLayer(String workspace, String storename,
			String layername, String srs, String defaultStyle) {

		final GSFeatureTypeEncoder fte = new GSFeatureTypeEncoder();

		fte.setProjectionPolicy(ProjectionPolicy.REPROJECT_TO_DECLARED);
		fte.addKeyword("KEYWORD");
		fte.addName(layername);
		fte.addSRS(srs); // srs=null?"EPSG:4326":srs);
		final GSLayerEncoder layerEncoder = new GSLayerEncoder();
		layerEncoder.addDefaultStyle(defaultStyle);
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
			if (LOGGER.isEnabledFor(Level.ERROR))
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
				if (LOGGER.isEnabledFor(Level.ERROR))
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
	 * configured upon file upload. It can take one of the three values <i>first</i>,
	 * <i>none</i>, or <i>all</i>. <br>
	 * first - Only setup the first feature type available in the data store.
	 * This is the default. <br>
	 * none - Do not configure any feature types.<br>
	 * all - Configure all feature types.
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

	// ==========================================================================
	// === GEOTIFF
	// ==========================================================================

	/**
	 * Publish a GeoTiff.
	 * 
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
	 * @return true if the operation completed successfully.
	 * @deprecated UNTESTED
	 */
	public boolean publishGeoTIFF(String workspace, String storeName,
			File geotiff) throws FileNotFoundException {
		String sUrl = restURL + "/rest/workspaces/" + workspace
				+ "/coveragestores/" + storeName + "/geotiff";
		String sendResult = HTTPUtils
				.put(sUrl, geotiff, "text", gsuser, gspass); // CHECKME: text?!?
		boolean sent = sendResult != null;
		return sent;
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
	 * 
	 * @return a PublishedCoverage, or null on errors
	 * @throws FileNotFoundException
	 */
	public RESTCoverageStore publishExternalGeoTIFF(String workspace,
			String storeName, File geotiff, String srs, String defaultStyle)
			throws FileNotFoundException {
		// create store
		String sUrl = restURL + "/rest/workspaces/" + workspace
				+ "/coveragestores/" + storeName + "/external.geotiff";
		String sendResult = HTTPUtils.put(sUrl, geotiff.toURI().toString(),
				"text/plain", gsuser, gspass);
		RESTCoverageStore store = RESTCoverageStore.build(sendResult);

		if (store != null) {
			try {
				// retrieve coverage name
				GeoServerRESTReader reader = new GeoServerRESTReader(restURL,
						gsuser, gspass);
				RESTCoverageList covList = reader.getCoverages(workspace,
						storeName);
				if (covList.isEmpty()) {
					LOGGER.error("No coverages found in new coveragestore "
							+ storeName);
					return null;
				}
				final String coverageName = covList.get(0).getName();

				// config coverage props (srs)
				GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
				coverageEncoder.addName(FilenameUtils.getBaseName(geotiff
						.getName()));
				coverageEncoder.addSRS(srs);
				coverageEncoder
						.addProjectionPolicy(ProjectionPolicy.REPROJECT_TO_DECLARED);
				configureCoverage(coverageEncoder, workspace, storeName,
						coverageName);

				// config layer props (style, ...)
				GSLayerEncoder layerEncoder = new GSLayerEncoder();
				layerEncoder.addDefaultStyle(defaultStyle);
				configureLayer(workspace, coverageName, layerEncoder);

			} catch (Exception e) {
				LOGGER.warn(
						"Could not configure external GEOTiff:" + storeName, e);
				store = null; // TODO: should we remove the configured pc?
			}
		}

		return store;
	}

	// ==========================================================================
	// === MOSAIC
	// ==========================================================================

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
		 * readable by targhet GeoServer not the calling client!
		 */
		if (!mosaicDir.isDirectory()) {
			if (LOGGER.isEnabledFor(Level.WARN))
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
	 * @deprecated provided for backward compatibility use {@link
	 *             createExternaMosaicDatastore(String workspace, String
	 *             storeName, File mosaicDir, CoverageConfigure configure)}
	 * @param workspace
	 * @param storeName
	 * @param mosaicDir
	 * @return
	 * @throws FileNotFoundException
	 */
	public RESTCoverageStore configureExternaMosaicDatastore(String workspace,
			String storeName, File mosaicDir) throws FileNotFoundException {
		return createExternaMosaicDatastore(workspace, storeName, mosaicDir,
				ParameterConfigure.FIRST, ParameterUpdate.APPEND);
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
	public RESTCoverageStore publishExternalMosaic(String workspace,
			String storeName, File mosaicDir, String srs, String defaultStyle)
			throws FileNotFoundException {
		final GSCoverageEncoder coverageEncoder = new GSCoverageEncoder();
		coverageEncoder.addSRS(srs);
		coverageEncoder.addName(FilenameUtils.getBaseName(mosaicDir.getName()));
		final GSLayerEncoder layerEncoder = new GSLayerEncoder();
		layerEncoder.addDefaultStyle(defaultStyle);

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
	 * 
	 * @return the created RESTCoverageStore
	 * @deprecated this is keep only for backward compatibility use
	 *             createExternalMosaic and getCoverageStore separately
	 * 
	 * @throws FileNotFoundException
	 */
	public RESTCoverageStore publishExternalMosaic(String workspace,
			String storeName, File mosaicDir,
			GSCoverageEncoder coverageEncoder, GSLayerEncoder layerEncoder)
			throws FileNotFoundException {

		if (!createExternalMosaic(workspace, storeName, mosaicDir,
				coverageEncoder, layerEncoder)) {
			return null;
		}

		GeoServerRESTReader reader;
		try {
			reader = new GeoServerRESTReader(restURL, gsuser, gspass);
		} catch (MalformedURLException e1) {
			LOGGER.warn("Could not configure external Mosaic:" + storeName, e1);
			return null;
		}

		final RESTCoverageStore store = reader.getCoverageStore(workspace,
				storeName);

		if (store == null) {
			LOGGER.warn("Unable to get the store" + workspace + ":" + storeName
					+ " from the targhet geoserver.");
			return null;
		}

		return store;
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
	 * 
	 * @return true if the operation completed successfully.
	 * 
	 * @throws FileNotFoundException
	 */
	public boolean createExternalMosaic(String workspace, String storeName,
			File mosaicDir, GSCoverageEncoder coverageEncoder,
			GSLayerEncoder layerEncoder) throws FileNotFoundException {

		RESTCoverageStore store = createExternaMosaicDatastore(workspace,
				storeName, mosaicDir, ParameterConfigure.NONE,
				ParameterUpdate.OVERWRITE);

		// override name to match the FIRST configured coverage
		final String coverageStoreName = FilenameUtils.getBaseName(mosaicDir
				.getName());

		if (coverageEncoder == null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("no coverageEncoder provided for " + workspace
						+ ":" + storeName);
			}

			coverageEncoder = new GSCoverageEncoder();
			coverageEncoder.setName(coverageStoreName);
		}

		if (layerEncoder == null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("no layerEncoder provided for " + workspace + ":"
						+ storeName);
			}
			layerEncoder = new GSLayerEncoder();
		}

		if (store != null) {
			try {
				// override name
				coverageEncoder.setName(coverageStoreName);

				if (!createCoverage(coverageEncoder, workspace, storeName)) {
					if (LOGGER.isEnabledFor(Level.ERROR))
						LOGGER.error("Unable to create a coverage for the store:"
								+ storeName);
					return false;
				}
				if (!configureLayer(workspace, coverageStoreName, layerEncoder)) {
					if (LOGGER.isEnabledFor(Level.ERROR))
						LOGGER.error("Unable to configure the Layer for the coverage:"
								+ coverageStoreName);
					return false;
				}

			} catch (Exception e) {
				if (LOGGER.isEnabledFor(Level.WARN))
					LOGGER.warn("Could not configure external mosaic:"
							+ storeName, e);
				store = null; // TODO: should we remove the configured store?
				return false;
			}
			return true;
		}
		return false;
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

				if (LOGGER.isEnabledFor(Level.WARN)) {
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
			if (LOGGER.isEnabledFor(Level.ERROR))
				LOGGER.error(ex);
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

				if (LOGGER.isEnabledFor(Level.WARN)) {
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
			if (LOGGER.isEnabledFor(Level.ERROR))
				LOGGER.error(ex);
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
			if (LOGGER.isEnabledFor(Level.ERROR))
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
			if (workspace==null || storename==null)
				throw new IllegalArgumentException("Arguments may not be null!");
			if (workspace.isEmpty() || storename.isEmpty())
				throw new IllegalArgumentException("Arguments may not be empty!");
			
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
			if (LOGGER.isEnabledFor(Level.ERROR))
				LOGGER.error(ex);
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
			if (LOGGER.isEnabledFor(Level.ERROR))
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
			final String storename, final boolean recurse) throws IllegalArgumentException {
		try {
			if (workspace==null || storename==null)
				throw new IllegalArgumentException("Arguments may not be null!");
			if (workspace.isEmpty() || storename.isEmpty())
				throw new IllegalArgumentException("Arguments may not be empty!");
			
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
			if (LOGGER.isEnabledFor(Level.ERROR))
				LOGGER.error(ex);
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
			if (LOGGER.isEnabledFor(Level.ERROR))
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
	 *            values for this parameter are â€œtrueâ€� or â€œfalseâ€�. The default
	 *            value is â€œfalseâ€�.
	 * @return <TT>true</TT> if the WorkSpace was successfully removed.
	 */
	public boolean removeWorkspace(String workspace, boolean recurse) throws IllegalArgumentException {
		workspace = sanitize(workspace);
		try {
			if (workspace==null)
				throw new IllegalArgumentException("Arguments may not be null!");
			if (workspace.isEmpty())
				throw new IllegalArgumentException("Arguments may not be empty!");
			
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
			if (LOGGER.isEnabledFor(Level.ERROR))
				LOGGER.error(ex);
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
				if (LOGGER.isEnabledFor(Level.WARN))
					LOGGER.warn("Could not delete layergroup " + name);
			} else {
				if (LOGGER.isInfoEnabled())
					LOGGER.info("Layergroup successfully deleted: " + name);
			}

			return deleted;
		} catch (MalformedURLException ex) {
			if (LOGGER.isEnabledFor(Level.ERROR))
				LOGGER.error(ex);
			return false;
		}
	}

	// ==========================================================================
	// ===
	// ==========================================================================

	/**
	 * @deprecated please use {@link configureLayer(String workspace, String
	 *             layerName, GSLayerEncoder layer) }
	 */
	public boolean configureLayer(final GSLayerEncoder layer,
			final String layerName) {
		return configureLayer(null, layerName, layer);
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

			if (LOGGER.isEnabledFor(Level.WARN)) {
				LOGGER.warn("Null workspace while removing layer : "
						+ layerName + " -- This behavior is deprecated.");
			}
		} else {
			fqLayerName = workspace + ":" + layerName;
		}
		if (layerName == null) {
			if (LOGGER.isEnabledFor(Level.ERROR)) {
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
			if (LOGGER.isEnabledFor(Level.WARN))
				LOGGER.warn("Error removing layer " + fqLayerName);
		}

		return result;
	}

	/**
	 * Allows to configure some layer attributes such and DefaultStyle TODO
	 * WmsPath
	 */
	public boolean configureLayer(final String workspace,
			final String layerName, final GSLayerEncoder layer) {

		// TODO: check this usecase, layer should always be defined
		if (layer.isEmpty()) {
			if (LOGGER.isEnabledFor(Level.WARN))
				LOGGER.warn("Null layer name while configuring layer -- This behavior is suspicious.");
			return true;
		}

		final String fqLayerName;

		// this null check is here only for backward compatibility. workspace
		// shall be mandatory.
		if (workspace == null) {

			fqLayerName = layerName;

			if (LOGGER.isEnabledFor(Level.WARN)) {
				LOGGER.warn("Null workspace while configuring layer : "
						+ layerName + " -- This behavior is deprecated.");
			}
		} else {
			fqLayerName = workspace + ":" + layerName;
		}

		final String url = restURL + "/rest/layers/" + fqLayerName;

		String layerXml = layer.toString();
		String sendResult = HTTPUtils.putXml(url, layerXml, gsuser, gspass);
		if (sendResult != null) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Layer successfully configured: " + fqLayerName);
			}
		} else {
			if (LOGGER.isEnabledFor(Level.WARN))
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
			if (LOGGER.isEnabledFor(Level.ERROR))
				LOGGER.error("Unable to configure a coverage with no name try using GSCoverageEncoder.setName(String)");
			return false;
		}

		// retrieve coverage name
		GeoServerRESTReader reader;
		try {
			reader = new GeoServerRESTReader(restURL, gsuser, gspass);
		} catch (MalformedURLException e) {
			if (LOGGER.isEnabledFor(Level.ERROR))
				LOGGER.error(e);
			return false;
		}
		final RESTCoverageList covList = reader.getCoverages(wsname, csname);
		if (covList.isEmpty()) {
			if (LOGGER.isEnabledFor(Level.ERROR))
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
			if (LOGGER.isEnabledFor(Level.ERROR))
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
			if (LOGGER.isEnabledFor(Level.WARN))
				LOGGER.warn("Error configuring coverage " + wsname + ":"
						+ csname + ":" + cname + " (" + sendResult + ")");
		}

		return sendResult != null;
	}

	/**
	 * Create a new coverage in a given workspace and coverage store
	 * 
	 * @param ce
	 *            contains the coverage name to create and the configuration to
	 *            apply
	 * @param wsname
	 *            the workspace to search for existent coverage
	 * @param csname
	 *            the coverage store to search for existent coverage
	 * @return
	 */
	public boolean createCoverage(final GSCoverageEncoder ce,
			final String wsname, final String csname) {

		final String cname = ce.getName();
		if (cname == null) {
			if (LOGGER.isEnabledFor(Level.ERROR))
				LOGGER.error("Unable to configure a coverage with no name try using GSCoverageEncoder.setName(String)");
			return false;
		}

		// configure the selected coverage
		final String url = restURL + "/rest/workspaces/" + wsname
				+ "/coveragestores/" + csname + "/coverages.xml";

		final String xmlBody = ce.toString();
		final String sendResult = HTTPUtils.postXml(url, xmlBody, gsuser,
				gspass);
		if (sendResult != null) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Coverage successfully created " + wsname + ":"
						+ csname + ":" + cname);
			}
		} else {
			if (LOGGER.isEnabledFor(Level.WARN))
				LOGGER.warn("Error creating coverage " + wsname + ":" + csname
						+ ":" + cname + " (" + sendResult + ")");
		}

		return sendResult != null;
	}

	/**
	 * Allows to configure some coverage's attributes
	 * 
	 * @param ce
	 *            Coverage encoder
	 * @param wsname
	 *            workspace name
	 * @param csname
	 *            coveragestore name
	 * @param cname
	 *            coverage name (if != null will override the CoverageEncoder
	 *            name)
	 * @return true if success
	 * @deprecated use {@link GeoServerRESTPublisher#configureCoverage(GSCoverageEncoder, String, String)}
	 */
	protected boolean configureCoverage(final GSCoverageEncoder ce,
			final String wsname, final String csname, String cname) {

		if (cname != null)
			ce.setName(cname);
		else
			cname = ce.getName();
		return configureCoverage(ce, wsname, csname);
	}
	
	// ==========================================================================
	// === WORLDIMAGE
	// ==========================================================================
	/**
     * Publish a zipped worldimage file.
     *
     * It is assumed that the the zip-file contain the *.prj to set the srs.
     *
     * <P>This is equivalent call with cUrl:
     * <PRE>{@code
     *curl -u admin:geoserver -XPUT -H 'Content-type: application/zip' \
     *      --data-binary @$ZIPFILE \
     *      http://$GSIP:$GSPORT/$SERVLET/rest/workspaces/$WORKSPACE/coveragestores/$COVERAGESTORE/file.worldimage
     * </PRE>
     *
     * @param workspace Workspace to use 
     * @param coveragestore Name of the coveragestore
     * @param zipFile zip file to upload
     * @return true if the operation completed successfully.
     */
	public boolean publishWorldimage(String workspace, String coveragestore, File zipFile) throws FileNotFoundException {
		// build full URL
		StringBuilder sbUrl = new StringBuilder(restURL).append("/rest/workspaces/").append(workspace).append("/coveragestores/").append(coveragestore)
				.append("/file.worldimage");

		String sentResult = HTTPUtils.put(sbUrl.toString(), zipFile, "application/zip", gsuser, gspass);
		boolean fileSent = sentResult != null;

		if (fileSent) {
			LOGGER.info("Zipfile successfully uploaded ( zip:" + zipFile + ")");
		} else {
			LOGGER.warn("Error in sending zipfile " + zipFile);
		}
		return fileSent;
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
		// try {
		// return URLEncoder.encode(s,"UTF-8");
		// } catch (UnsupportedEncodingException e) {
		// LOGGER.warn("Error encoding :"+s+" with UTF-8: "+e.getLocalizedMessage());
		return URLEncoder.encode(s);
		// }
	}
}

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

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher.UploadMethod;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageGranulesList;
import it.geosolutions.geoserver.rest.decoder.RESTStructuredCoverageIndexSchema;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage GeoTools StructuredGridCoverageReader. It allows to create a store from a file or harvest the coverages contained in a file, to delete
 * granules from an existing coverage and eventually to get information about the granules inside a StructuredGridCoverageReader.
 * 
 * @author Simone Giannecchini, GeoSolutions
 */
public class GeoServerRESTStructuredGridCoverageReaderManager extends GeoServerRESTAbstractManager {
    
    /**
     * Option that tells GeoServer whether or not to configure all the coverages for a certain ImageMosaic.
     * 
     * @author Simone Giannecchini, GeoSolutions
     *
     */
    public enum ConfigureCoveragesOption{
        NONE,
        ALL;
        
        public static ConfigureCoveragesOption getDefault() {
            return ALL;
        }
    }

    /**
     * Default logger
     */
    private final static Logger LOGGER = LoggerFactory
            .getLogger(GeoServerRESTStructuredGridCoverageReaderManager.class);

    /**
     * Default constructor.
     * 
     * @param restURL GeoServer REST API endpoint
     * @param username GeoServer REST API authorized username
     * @param password GeoServer REST API password for the former username
     * @throws IllegalArgumentException
     */
    public GeoServerRESTStructuredGridCoverageReaderManager(URL restURL, String username,
            String password) throws IllegalArgumentException {
        super(restURL, username, password);
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
    public boolean create(String workspace, String coverageStore, String path) {
        return create(workspace, coverageStore, path, ConfigureCoveragesOption.ALL);
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
    public boolean create(String workspace, String coverageStore, String path, ConfigureCoveragesOption configureOpt) {
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
        StringBuilder ss=HTTPUtils.append(gsBaseUrl, "/rest/workspaces/", workspace, "/coveragestores/",
                coverageStore, "/file.imagemosaic");
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
     * Create a store or harvest the coverage from the provided <b>external</b> path.
     * 
     * @param workspace the GeoServer workspace
     * @param coverageStore the GeoServer coverageStore
     * @param format the format of the file to upload
     * @param the absolute path to the file to upload
     * 
     * @return <code>true</code> if the call succeeds or <code>false</code> otherwise.
     * @since geoserver-2.4.0, geoserver-mng-1.6.0
     */
    public boolean harvestExternal(String workspace, String coverageStore, String format,
            String path) {
        // checks
        checkString(workspace);
        checkString(coverageStore);
        checkString(format);
        checkString(path);

        // create URL
        String sUrl = HTTPUtils.append(gsBaseUrl, "/rest/workspaces/", workspace, "/coveragestores/",
                coverageStore, "/", UploadMethod.EXTERNAL.toString(), ".", format).toString();

        // POST request
        String result = HTTPUtils.post(sUrl, "file://" + path, "text/plain", gsuser, gspass);
        return result != null;
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
     * 
     * @since geoserver-2.4.0, geoserver-mng-1.6.0
     */
    public boolean removeGranulesByCQL(final String workspace, String coverageStore,
            String coverage, String filter) throws UnsupportedEncodingException {
        // checks
        checkString(workspace);
        checkString(coverage);
        checkString(filter);
        checkString(coverageStore);

        // does it exist?
        RESTStructuredCoverageGranulesList granulesList = null;
        try {
            granulesList = getGranules(workspace, coverageStore, coverage, filter, null, 1);
        } catch (MalformedURLException e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(e.getMessage(), e);
            }
        } catch (UnsupportedEncodingException e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(e.getMessage(), e);
            }
        }
        if (granulesList == null || granulesList.isEmpty()) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Granules for filter: " + filter + " does not exist for coverage "
                        + coverage);
            }
            return true; // nothing to remove
        }

        // method
        String sUrl = HTTPUtils.append(gsBaseUrl, "/rest/workspaces/", workspace, "/coveragestores",
                "/", coverageStore, "/coverages/", coverage, "/index/granules?filter=",
                URLEncoder.encode(filter, "UTF-8")).toString();
        if (!HTTPUtils.delete(sUrl, gsuser, gspass)) {
            return false;
        }

        // does it exist?
        granulesList = null;
        try {
            granulesList = getGranules(workspace, coverageStore, coverage, filter, null, 1);
        } catch (MalformedURLException e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(e.getMessage(), e);
            }
        } catch (UnsupportedEncodingException e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(e.getMessage(), e);
            }
        }
        if (granulesList == null || granulesList.isEmpty()) {
            return true; // nothing to remove
        }
        return false;
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
     * 
     * @since geoserver-2.4.0, geoserver-mng-1.6.0
     */
    public boolean removeGranuleById(final String workspace, String coverageStore, String coverage,
            String granuleId) {
        // checks
        checkString(workspace);
        checkString(coverage);
        checkString(granuleId);
        checkString(coverageStore);

        // does it exist?
        RESTStructuredCoverageGranulesList granule = null;
        try {
            granule = getGranuleById(workspace, coverageStore, coverage, granuleId);
        } catch (MalformedURLException e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(e.getMessage(), e);
            }
        } catch (UnsupportedEncodingException e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(e.getMessage(), e);
            }
        }
        if (granule == null) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Granule for id: " + granuleId + " does not exist for coverage "
                        + coverage);
            }
            return true; // nothing to remove
        }

        // delete
        String sUrl = HTTPUtils.append(gsBaseUrl, "/rest/workspaces/", workspace, "/coveragestores",
                "/", coverageStore, "/coverages/", coverage, "/index/granules/", granuleId)
                .toString();
        if (!HTTPUtils.delete(sUrl, gsuser, gspass)) {
            return false;
        }

        // has it been canceled?
        // does it exist?
        granule = null;
        try {
            granule = getGranuleById(workspace, coverageStore, coverage, granuleId);
        } catch (MalformedURLException e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(e.getMessage(), e);
            }
        } catch (UnsupportedEncodingException e) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(e.getMessage(), e);
            }
        }
        if (granule == null) {
            return true;
        }

        return false;
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
     * 
     * @since geoserver-2.4.0, geoserver-mng-1.6.0
     */
    public RESTStructuredCoverageIndexSchema getGranuleIndexSchema(final String workspace,
            String coverageStore, String coverage) throws MalformedURLException {
        // checks
        checkString(workspace);
        checkString(coverage);
        checkString(coverageStore);

        // create URL and then call it
        String sUrl = HTTPUtils.append(gsBaseUrl, "/rest/workspaces/", workspace, "/coveragestores/",
                coverageStore, "/coverages/", coverage, "/index.xml").toString();
        String result = HTTPUtils.get(sUrl, gsuser, gspass);
        if (result != null) {
            return RESTStructuredCoverageIndexSchema.build(result);
        }
        return null;
    }

    /**
     * Get information about all the granules for a coverage with optional filter and paging.
     * 
     * @param workspace the GeoServer workspace
     * @param coverageStore the GeoServer coverageStore
     * @param coverage the name of the target coverage
     * 
     * @return <code>null</code> in case the call does not succeed, or an instance of {@link RESTStructuredCoverageGranulesList}.
     * 
     * @throws MalformedURLException
     * @throws UnsupportedEncodingException
     * 
     * @since geoserver-2.4.0, geoserver-mng-1.6.0
     */
    public RESTStructuredCoverageGranulesList getGranules(final String workspace,
            String coverageStore, String coverage)
            throws MalformedURLException, UnsupportedEncodingException {
        return getGranules(workspace, coverageStore, coverage, null, null, null);
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
     * 
     * @since geoserver-2.4.0, geoserver-mng-1.6.0
     */
    public RESTStructuredCoverageGranulesList getGranules(final String workspace,
            String coverageStore, String coverage, String filter, Integer offset, Integer limit)
            throws MalformedURLException, UnsupportedEncodingException {
        // checks
        checkString(workspace);
        checkString(coverage);
        checkString(coverageStore);

        // method
        boolean append = false;
        String sUrl = HTTPUtils.append(gsBaseUrl, "/rest/workspaces/", workspace, "/coveragestores/",
                coverageStore, "/coverages/", coverage, "/index/granules.xml").toString();
        if (filter != null && !filter.isEmpty()) {
            append = true;
            sUrl = HTTPUtils.append(sUrl, "?filter=", URLEncoder.encode(filter, "UTF-8"))
                    .toString();
        }
        if (offset != null) {
            if (offset < 0)
                offset = 0;
            sUrl = HTTPUtils.append(sUrl, append ? "&offset=" : "?offset=", offset.toString())
                    .toString();
            append = true;
        }
        if (limit != null) {
            if (limit < 1) {
                limit = 1;
            }
            sUrl = HTTPUtils.append(sUrl, append ? "&limit=" : "?limit=", limit.toString())
                    .toString();
            append = true;
        }
        String result = HTTPUtils.get(sUrl, gsuser, gspass);
        if (result != null) {
            return RESTStructuredCoverageGranulesList.build(result);
        }
        return null;
    }

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
     * 
     * @since geoserver-2.4.0, geoserver-mng-1.6.0
     */
    public RESTStructuredCoverageGranulesList getGranuleById(final String workspace,
            String coverageStore, String coverage, String id) throws MalformedURLException,
            UnsupportedEncodingException {
        // checks
        checkString(workspace);
        checkString(coverage);
        checkString(coverageStore);
        checkString(id);
        try {
            Integer.parseInt(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }

        // method
        String sUrl = HTTPUtils.append(gsBaseUrl, "/rest/workspaces/", workspace, "/coveragestores/",
                coverageStore, "/coverages/", coverage, "/index/granules/", id, ".xml").toString();
        String result = HTTPUtils.get(sUrl, gsuser, gspass);
        if (result != null) {
            return RESTStructuredCoverageGranulesList.build(result);
        }
        return null;
    }
}

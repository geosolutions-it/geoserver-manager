/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2015 GeoSolutions S.A.S.
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

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher.Format;
import it.geosolutions.geoserver.rest.decoder.RESTLayerList;
import it.geosolutions.geoserver.rest.decoder.gwc.GWCRESTWMSLayer;
import it.geosolutions.geoserver.rest.decoder.gwc.diskquota.GWCQuotaConfiguration;
import it.geosolutions.geoserver.rest.decoder.gwc.diskquota.GWCQuotaConfigJSONWrapper;
import it.geosolutions.geoserver.rest.decoder.gwc.masstruncate.MassTruncateRequests;
import it.geosolutions.geoserver.rest.decoder.gwc.seed.GWCTruncateSeedType;
import it.geosolutions.geoserver.rest.decoder.gwc.seed.GlobalSeedStatus;
import it.geosolutions.geoserver.rest.decoder.gwc.statistics.GWCInMemoryCacheStatistics;
import it.geosolutions.geoserver.rest.decoder.gwc.statistics.GWCInMemoryCacheStatisticsXML;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.commons.httpclient.NameValuePair;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class GeoWebCacheRESTManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoWebCacheRESTManager.class);
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * GeoWebCache instance base URL. E.g.:
     * <TT>http://localhost:8080/geowebcache</TT>.
     */
    private final String restURL;

    /**
     * GeoWebCache instance privileged username, with read & write permission on
     * REST API
     */
    private final String gwcUser;

    /**
     * GeoWebCache instance password for privileged username with r&w permission
     * on REST API
     */
    private final String gwcPass;

    /**
     * Creates a <TT>GeoWebCacheRESTManager</TT> to connect against a
     * GeoWebCache instance with the given URL and user credentials.
     *
     * @param restURL the base GeoWebCache URL (e.g.:
     * <TT>http://localhost:8080/geowebcache</TT>)
     * @param username auth credential
     * @param password auth credential
     */
    public GeoWebCacheRESTManager(String restURL, String username, String password) {
        this.restURL = HTTPUtils.decurtSlash(restURL);
        this.gwcUser = username;
        this.gwcPass = password;

        try {
            new URL(restURL);
        } catch (MalformedURLException ex) {
            LOGGER.error("Bad URL: Calls to GeoWebCache are going to fail", ex);
        }
        mapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
//        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
    }

    /**
     * Check if a GeoWebCache instance is running at the given URL.
     * <BR>
     * Return <TT>true</TT> if the configured GeoWebCache is up and replies to
     * REST requests.
     * <BR>
     * Send a HTTP GET request to the configured URL.<BR>
     * Return <TT>true</TT> if a HTTP 200 code (OK) is read from the HTTP
     * response; any other response code, or connection error, will return a
     * <TT>false</TT> boolean.
     *
     * @return true if a GeoWebCache instance was found at the configured URL.
     */
    public boolean existGeoWebCache() {
        return HTTPUtils.httpPing(restURL + "/rest/", gwcUser, gwcPass);
    }

    // <editor-fold desc="/statistics" defaultstate="collapsed">
    //==========================================================================
    //=== STATISTICS
    //==========================================================================
    /**
     * @return the in memory cache statistics if the blobstore used is an
     * instance of MemoryBlobStore.
     * @throws org.codehaus.jackson.map.JsonMappingException
     */
    public GWCInMemoryCacheStatistics getInMemoryCacheStatisticsJSON() throws JsonMappingException {
        GWCInMemoryCacheStatistics inMemoryCacheStatistics = null;
        String statistics = this.load("/rest/statistics.json");
        if (!Util.isEmpty(statistics)) {
            try {
                inMemoryCacheStatistics = this.mapper.readValue(statistics, 
                        GWCInMemoryCacheStatistics.class);
            } catch (IOException ioe) {
                LOGGER.error("Error parsing the statistics: " + ioe);
                throw new JsonMappingException("Error parsing the statistics: " + ioe);
            }
        }
        return inMemoryCacheStatistics;
    }

    public GWCInMemoryCacheStatisticsXML getInMemoryCacheStatisticsXML() throws JsonMappingException {
        GWCInMemoryCacheStatisticsXML inMemoryCacheStatistics = null;
        String statistics = this.load("/rest/statistics.xml");
        if (!Util.isEmpty(statistics)) {
            try {
                inMemoryCacheStatistics = this.mapper.readValue(statistics, 
                        GWCInMemoryCacheStatisticsXML.class);
            } catch (IOException ioe) {
                LOGGER.error("Error parsing the statistics: " + ioe);
                throw new JsonMappingException("Error parsing the statistics: " + ioe);
            }
        }
        return inMemoryCacheStatistics;
    }
    // </editor-fold>

    // <editor-fold desc="/diskquota" defaultstate="collapsed">
    //==========================================================================
    //=== DISKQUOTA
    //==========================================================================
    /**
     * @param diskQuotaConfiguration the configuration to serve
     * @return the operation result
     * @throws org.codehaus.jackson.map.JsonMappingException
     * @throws IllegalArgumentException if the layerName is null or empty.
     */
    public boolean changeDiskQuotaConfig(GWCQuotaConfiguration diskQuotaConfiguration)
            throws JsonMappingException {
        String result = null;
        if (diskQuotaConfiguration == null) {
            throw new IllegalArgumentException("The diskQuotaConfiguration must not be null or empty");
        }
        /*
         * This is the equivalent call with cUrl:
         *
         * {@code curl -u geowebcache:secured -v -XGET
         *  http://$GWCIP:$GWCPORT/$SERVLET/rest/diskquota.json}
         */
        try {
            GWCQuotaConfigJSONWrapper wrapper = new GWCQuotaConfigJSONWrapper();
            wrapper.setQuotaConfiguration(diskQuotaConfiguration);
            String jsonDiskQuotaConfig = this.mapper.writeValueAsString(wrapper);
            LOGGER.debug("Quota configuration json: " + jsonDiskQuotaConfig);
            result = this.put("/rest/diskquota.json", jsonDiskQuotaConfig, Format.JSON);
        } catch (IOException ioe) {
            LOGGER.error("Error parsing the quota configuration: " + ioe);
            throw new JsonMappingException("Error parsing the quota configuration: " + ioe);
        }
        return result != null;
    }

    /**
     * @return a list of pending (scheduled) and running tasks for all the
     * layers.
     * @throws org.codehaus.jackson.map.JsonMappingException
     * @throws IllegalArgumentException if the layerName is null or empty.
     */
    public GWCQuotaConfiguration getCurrentDiskQuotaConfig() throws JsonMappingException {
        GWCQuotaConfiguration quotaConfiguration = null;
        /*
         * This is the equivalent call with cUrl:
         *
         * {@code curl -u geowebcache:secured -v -XGET
         *  http://$GWCIP:$GWCPORT/$SERVLET/rest/diskquota.json}
         */
        String quotaConfigurationJSON = this.load("/rest/diskquota.json");
        if (!Util.isEmpty(quotaConfigurationJSON)) {
            try {
                LOGGER.debug("Quota configuration json: " + quotaConfigurationJSON);
                GWCQuotaConfigJSONWrapper quotaConfigJSONWrapper
                        = this.mapper.readValue(quotaConfigurationJSON,
                                GWCQuotaConfigJSONWrapper.class);
                quotaConfiguration = quotaConfigJSONWrapper.getQuotaConfiguration();
            } catch (IOException ioe) {
                LOGGER.error("Error parsing the quota configuration: " + ioe);
                throw new JsonMappingException("Error parsing the quota configuration: " + ioe);
            }
        }
        return quotaConfiguration;
    }

    // </editor-fold>
    // <editor-fold desc="/masstruncate" defaultstate="collapsed">
    //==========================================================================
    //=== MASSTRUNCATE: Mass truncation provides a mechanism for completely 
    //=== clearing caches more conveniently than with the seeding system.
    //==========================================================================
    /**
     * TruncateLayer will clear all caches associated with a named layer,
     * including all permutations of gridset, parameter filter values, and image
     * formats.
     *
     * @param layerName the layer name to truncate
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if the layerName is null or empty.
     */
    public boolean truncateLayer(String layerName) {
        /*
         * This is the equivalent call with cUrl:
         *
         * {@code curl -v -u geowebcache:secured -H "Content-type: text/xml" 
         * -d "<truncateLayer><layerName>topp:states</layerName></truncateLayer>"
         *  "http://$GWCIP:$GWCPORT/$SERVLET/rest/masstruncate"}
         */
        if (Util.isEmpty(layerName)) {
            throw new IllegalArgumentException("The layerName may not be null or empty");
        }

        StringBuilder truncateLayerRequestBuilder = new StringBuilder();
        truncateLayerRequestBuilder.append("<truncateLayer><layerName>").
                append(layerName).append("</layerName></truncateLayer>");

        final String result = this.post("/rest/masstruncate",
                truncateLayerRequestBuilder.toString(),
                GeoServerRESTPublisher.Format.TXT_XML);
        LOGGER.debug("Mass truncation result: " + result);
        return result != null;
    }

    /**
     * @return Return a list of mass truncate requests available
     */
    public MassTruncateRequests getMassTruncateReqAvailable() {
        /*
         * This is the equivalent call with cUrl:
         *
         * {@code curl -v -u geowebcache:secured -XGET -H "Content-type: text/xml"
         *  "http://$GWCIP:$GWCPORT/$SERVLET/rest/masstruncate"}
         */
        final String result = this.load("/rest/masstruncate");
        return MassTruncateRequests.build(result);
    }
    // </editor-fold>

    // <editor-fold desc="/seed" defaultstate="collapsed">
    //==========================================================================
    //=== SEED
    //==========================================================================
    /**
     * Add a new seed request assigning it a name.
     *
     * @param seedRequestFile the SEED REQUEST file.
     * @param seedRequestName the seed request name to use.<b>Note the the seed
     * request name must match the name of the seed request in the xml
     * representation.</b>
     *
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if the seedRequestFile or the
     * seedRequestName are null or empty.
     */
    public boolean addSeedRequest(final File seedRequestFile, final String seedRequestName)
            throws IllegalArgumentException {
        /*
         * This is the equivalent call with cUrl:
         *
         * {@code curl -v -u geowebcache:secured -XPUT -H "Content-type: text/xml" -d @seedRequestFile.xml  
         * "http://$GWCIP:$GWCPORT/$SERVLET/rest/seed/seedRequestFile.xml"}
         */
        if (seedRequestFile == null || !seedRequestFile.isFile()) {
            throw new IllegalArgumentException("The seedRequestFile must not be null or empty");
        }
        String url = this.buildSeedRequestNameXMLURL(seedRequestName);

        final String result = this.post(url, seedRequestFile, GeoServerRESTPublisher.Format.TXT_XML);
        return result != null;
    }

    /**
     * @param layerName the layer name seeding task to look for
     * @return a list of pending (scheduled) and running tasks for all the
     * layers.
     * @throws org.codehaus.jackson.map.JsonMappingException
     * @throws IllegalArgumentException if the layerName is null or empty.
     */
    public GlobalSeedStatus getLayerSeedingStatus(String layerName)
            throws JsonMappingException {
        GlobalSeedStatus globalSeedStatus = null;
        if (Util.isEmpty(layerName)) {
            throw new IllegalArgumentException("The layerName may not be null or empty");
        }
        String url = HTTPUtils.append("/rest/seed/", layerName, ".json").toString();
        String seedingStatus = this.load(url);
        if (!Util.isEmpty(seedingStatus)) {
            try {
                globalSeedStatus = this.mapper.readValue(seedingStatus, GlobalSeedStatus.class);
            } catch (IOException ioe) {
                LOGGER.error("Error parsing the seeding status: " + ioe);
                throw new JsonMappingException("Error parsing the quota configuration: " + ioe);
            }
        }
        return globalSeedStatus;
    }

    /**
     * @return a list of pending (scheduled) and running tasks for all the
     * layers.
     * @throws org.codehaus.jackson.map.JsonMappingException
     */
    public GlobalSeedStatus getGlobalSeedingStatus() throws JsonMappingException {
        GlobalSeedStatus globalSeedStatus = null;
        String seedingStatus = this.load("/rest/seed.json");
        if (!Util.isEmpty(seedingStatus)) {
            try {
                globalSeedStatus = this.mapper.readValue(seedingStatus, GlobalSeedStatus.class);
            } catch (IOException ioe) {
                LOGGER.error("Error parsing the seeding status: " + ioe);
                throw new JsonMappingException("Error parsing the quota configuration: " + ioe);
            }
        }
        return globalSeedStatus;
    }

    /**
     * Add a new seed request assigning it a name.
     *
     * @param wmsLayerBody the SEED REQUEST file.
     * @param seedRequestName the seed request name to use.<b>Note the the seed
     * request name must match the name of the seed request in the xml
     * representation.</b>
     *
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if the wmsLayerBody or the
     * seedRequestName are null or empty.
     */
    public boolean addSeedRequest(final String wmsLayerBody, final String seedRequestName)
            throws IllegalArgumentException {
        /*
         * This is the equivalent call with cUrl:
         *
         * {@code curl -v -u geowebcache:secured -XPUT -H "Content-type: text/xml" -d wmsLayerBody  
         * "http://$GWCIP:$GWCPORT/$SERVLET/rest/seed/seedRequestFile.xml"}
         */
        if (Util.isEmpty(wmsLayerBody)) {
            throw new IllegalArgumentException("The wmsLayerBody must not be null or empty");
        }
        String url = this.buildSeedRequestNameXMLURL(seedRequestName);

        final String result = this.post(url, wmsLayerBody, GeoServerRESTPublisher.Format.TXT_XML);
        return result != null;
    }

    /**
     * Terminate a seed request task.
     *
     * @param seedRequestName the seed request name to use.<b>Note the the seed
     * request name must match the name of the seed request in the xml
     * representation.</b>
     * @param truncateSeedType the type of truncation
     *
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if the seedRequestName is null or empty.
     */
    public boolean truncateSeedRequestTask(final String seedRequestName,
            final GWCTruncateSeedType truncateSeedType)
            throws IllegalArgumentException {
        /*
         * This is the equivalent call with cUrl:
         *
         * {@code curl -v -u geowebcache:secured -d "kill_all=all"  
         * "http://$GWCIP:$GWCPORT/$SERVLET/rest/seed"}
         */
        if (Util.isEmpty(seedRequestName)) {
            throw new IllegalArgumentException("The seedRequestName may not be null or empty");
        }
        String url = HTTPUtils.append("/rest/seed/", seedRequestName).toString();
        NameValuePair[] parameters = new NameValuePair[1];
        parameters[0] = new NameValuePair("kill_all", truncateSeedType.name());
        final String result = this.post(url, parameters);
        return result != null;
    }

    private String buildSeedRequestNameXMLURL(final String seedRequestName) {
        if (Util.isEmpty(seedRequestName)) {
            throw new IllegalArgumentException("The seedRequestName may not be null or empty");
        }
        return this.buildSeedRequestRESTXMLPath(seedRequestName);
    }

    private String buildSeedRequestRESTXMLPath(String seedRequestName) {
        return HTTPUtils.append("/rest/seed/", seedRequestName, ".xml").toString();
    }
    // </editor-fold>

    // <editor-fold desc="/layers" defaultstate="collapsed">
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
//        LOGGER.info("--->> Layer response: " + load(url));
        return RESTLayerList.build(this.load(url));
    }

    /**
     * Get detailed info about a given Layer.
     *
     * @param layerName the layer name
     * @return a GWCRESTWMSLayer with layer information or null
     */
    public GWCRESTWMSLayer getLayer(String layerName) {
        if (Util.isEmpty(layerName)) {
            throw new IllegalArgumentException("Layername may not be null");
        }
        String nameEncoded = null;
        try {
            nameEncoded = URLEncoder.encode(layerName, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Error encoding layer name: " + ex);
        }
        String url = this.buildLayerRESTPath(nameEncoded);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving layer from " + url);
        }
        GWCRESTWMSLayer layer = GWCRESTWMSLayer.build(this.load(url));
        return layer;
    }

    /**
     * Add a new GWC's layer assigning it a name.
     *
     * @param wmsLayerFile the wmsLayer file.
     * @param layerName the layer's name to use.<b>Note the the layer's name
     * must match the name of the layer in the xml representation.</b>
     *
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if the wmsLayerFile or the layerName are
     * null or empty.
     */
    public boolean addLayer(final File wmsLayerFile, final String layerName)
            throws IllegalArgumentException {
        /*
         * This is the equivalent call with cUrl:
         *
         * {@code curl -v -u geowebcache:secured -XPUT -H "Content-type: text/xml" -d @wmsLayerFile.xml  
         * "http://$GWCIP:$GWCPORT/$SERVLET/rest/layers/layerName.xml"}
         */
        if (wmsLayerFile == null || !wmsLayerFile.isFile()) {
            throw new IllegalArgumentException("The wmsLayerFile may not be null or empty");
        }
        String url = this.buildLayerNameURL(layerName);

        final String result = this.put(url, wmsLayerFile, GeoServerRESTPublisher.Format.TXT_XML);
        return result != null;
    }

    /**
     * Add a new GWC's layer assigning it a name.
     *
     * @param wmsLayerBody the full wmsLayer document as a String.
     * @param layerName the layer's name to use.<b>Note the the layer's name
     * must match the name of the layer in the xml representation.</b>
     *
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if the wmsLayerBody or the layerName are
     * null or empty.
     */
    public boolean addLayer(final String wmsLayerBody, final String layerName)
            throws IllegalArgumentException {
        /*
         * This is the equivalent call with cUrl:
         *
         * {@code curl -v -u geowebcache:secured -XPUT -H "Content-type: text/xml" -d @wmsLayerBody  
         * "http://$GWCIP:$GWCPORT/$SERVLET/rest/layers/layerName.xml"}
         */
        if (Util.isEmpty(wmsLayerBody)) {
            throw new IllegalArgumentException("The wmsLayerBody may not be null or empty");
        }
        String url = this.buildLayerNameURL(layerName);

        final String result = this.put(url, wmsLayerBody, GeoServerRESTPublisher.Format.TXT_XML);
        return result != null;
    }

    private String buildLayerNameURL(final String layerName) {
        if (Util.isEmpty(layerName)) {
            throw new IllegalArgumentException("The layerName may not be null or empty");
        }
        return this.buildLayerRESTPath(layerName);
    }

    /**
     * Update a Layer.
     *
     * @param wmsLayerBody the full wmsLayer document as a String.
     * @param layerName the layer's name to use.<b>Note the the layer's name
     * must match the name of the layer in the xml representation.</b>
     *
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if the layer file or the layer name are
     * null or empty.
     */
    public boolean updateLayer(final String wmsLayerBody, final String layerName)
            throws IllegalArgumentException {
        /*
         * This is the equivalent call with cUrl:
         *
         * {@code curl -v -u geowebcache:secured -XPOST -H "Content-type: text/xml" -d @layerFile.xml  
         * "http://$GWCIP:$GWCPORT/$SERVLET/rest/layers/layerName.xml"}
         */
        if (Util.isEmpty(wmsLayerBody)) {
            throw new IllegalArgumentException("The wmsLayerBody may not be null or empty");
        }
        String url = this.buildLayerNameURL(layerName);

        final String result = this.post(url, wmsLayerBody, GeoServerRESTPublisher.Format.TXT_XML);
        return result != null;
    }

    /**
     * Update a Layer.
     *
     * @param wmsLayerFile the File containing the wmsLayer document.
     * @param layerName the layer's name to use.<b>Note the the layer's name
     * must match the name of the layer in the xml representation.</b>
     *
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if the layer file or the layer name are
     * null or empty.
     */
    public boolean updateLayer(final File wmsLayerFile, final String layerName)
            throws IllegalArgumentException {
        /*
         * This is the equivalent call with cUrl:
         *
         * {@code curl -v -u geowebcache:secured -XPOST -H "Content-type: text/xml" -d @layerFile.xml  
         * "http://$GWCIP:$GWCPORT/$SERVLET/rest/layers/layerName.xml"}
         */
        if (wmsLayerFile == null || !wmsLayerFile.isFile()) {
            throw new IllegalArgumentException("Unable to update layer using a null parameter file");
        }
        String url = this.buildLayerNameURL(layerName);

        final String result = this.post(url, wmsLayerFile, GeoServerRESTPublisher.Format.TXT_XML);
        return result != null;
    }

    /**
     * Check if a Layer exists in the configured GeoWebCache instance. User can
     * choose if log a possible exception or not
     *
     * @param layerName the name of the layer to check for.
     * @return <TT>true</TT> on HTTP 200, <TT>false</TT> on HTTP 404
     * @throws RuntimeException if any other HTTP code than 200 or 404 was
     * retrieved.
     */
    public boolean existsLayer(String layerName) {
        String url = this.buildLayerRESTPath(layerName);
//        String composed = Util.appendQuietOnNotFound(quietOnNotFound, url);
        return exists(url);
    }

    /**
     * remove a layer
     *
     * @param layerName
     * @return true if success
     */
    public boolean removeLayer(final String layerName) {
        if (layerName == null) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("Null layerName : " + layerName);
            }
            return false;
        }
        String url = this.buildLayerRESTPath(layerName);

        boolean result = this.delete(url);
        if (result) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Layer successfully removed: " + layerName);
            }
        } else {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("Error removing layer " + layerName);
            }
        }

        return result;
    }

    private String buildLayerRESTPath(String layerName) {
        return HTTPUtils.append("/rest/layers/", layerName, ".xml").toString();
    }
    // </editor-fold>

    private String post(String url, NameValuePair[] parameters) {
        LOGGER.info("Posting from REST path " + url);
        return HTTPUtils.post(restURL + url, null, parameters, gwcUser, gwcPass);
    }

    private String post(String url, String content, Format format) {
        LOGGER.info("Posting from REST path " + url);
        return HTTPUtils.post(restURL + url, content, format.getContentType(), gwcUser, gwcPass);
    }

    private String post(String url, File contentFile, Format format) {
        LOGGER.info("Posting from REST path " + url);
        return HTTPUtils.post(restURL + url, contentFile, format.getContentType(), gwcUser, gwcPass);
    }

    private String put(String url, String content, Format format) {
        LOGGER.info("Putting from REST path " + url);
        return HTTPUtils.put(restURL + url, content, format.getContentType(), gwcUser, gwcPass);
    }

    private String put(String url, File contentFile, Format format) {
        LOGGER.info("Putting from REST path " + url);
        return HTTPUtils.put(restURL + url, contentFile, format.getContentType(), gwcUser, gwcPass);
    }

    private String load(String url) {
        LOGGER.info("Loading from REST path " + url);
        return HTTPUtils.get(restURL + url, gwcUser, gwcPass);
    }

    private boolean delete(String url) {
        LOGGER.info("Deleting from REST path " + url);
        return HTTPUtils.delete(restURL + url, gwcUser, gwcPass);
    }

    private boolean exists(String url) {
        LOGGER.info("Checking existence from REST path " + url);
        return HTTPUtils.exists(restURL + url, gwcUser, gwcPass);
    }

}

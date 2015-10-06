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

import it.geosolutions.geoserver.rest.decoder.gwc.GWCRESTWMSLayer;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nazzareno Sileno - CNR IMAA geoSDI Group
 * @email nazzareno.sileno@geosdi.org
 */
public class GeoWebCacheRESTManager {

        private final static Logger LOGGER = LoggerFactory.getLogger(GeoWebCacheRESTManager.class);
    
    /**
     * GeoWebCache instance base URL. E.g.: <TT>http://localhost:8080/geowebcache</TT>.
     */
    private final String restURL;

    /**
     * GeoWebCache instance privileged username, with read & write permission on REST API
     */
    private final String gwcUser;

    /**
     * GeoWebCache instance password for privileged username with r&w permission on REST API
     */
    private final String gwcPass;
    
    /**
     * Creates a <TT>GeoWebCacheRESTManager</TT> to connect against a GeoWebCache instance with the given URL and user credentials.
     * 
     * @param restURL the base GeoWebCache URL (e.g.: <TT>http://localhost:8080/geowebcache</TT>)
     * @param username auth credential
     * @param password auth credential
     */
    public GeoWebCacheRESTManager(String restURL, String username, String password) {
        this.restURL = HTTPUtils.decurtSlash(restURL);
        this.gwcUser = username;
        this.gwcPass = password;

        URL url = null;
        try {
            url = new URL(restURL);
        } catch (MalformedURLException ex) {
            LOGGER.error("Bad URL: Calls to GeoWebCache are going to fail" , ex);
        }
    }
    
    /**
     * Check if a GeoWebCache instance is running at the given URL.
     * <BR>
     * Return <TT>true</TT> if the configured GeoWebCache is up and replies to REST requests.
     * <BR>
     * Send a HTTP GET request to the configured URL.<BR>
     * Return <TT>true</TT> if a HTTP 200 code (OK) is read from the HTTP response;
     * any other response code, or connection error, will return a
     * <TT>false</TT> boolean.
     *
     * @return true if a GeoWebCache instance was found at the configured URL.
     */
    public boolean existGeoWebCache() {
        return HTTPUtils.httpPing(restURL + "/rest/", gwcUser, gwcPass);
    }
    
    // <editor-fold desc="/layers" defaultstate="collapsed">
    /**
     * Get detailed info about a given Layer.
     * 
     * @param name the layer name
     * @return a GWCRESTWMSLayer with layer information or null
     */
    public GWCRESTWMSLayer getLayer(String name) {
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Layername may not be null");
        String nameEncoded = null;
        try {
            nameEncoded = URLEncoder.encode(name, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Error encoding layer name: " + ex);
        }
        String url = HTTPUtils.append("/rest/layers/", nameEncoded, ".xml").toString();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving layer from " + url);
        }
        GWCRESTWMSLayer layer = GWCRESTWMSLayer.build(load(url));
        return layer;
    }
    // </editor-fold>
    
    private String load(String url) {
        LOGGER.info("Loading from REST path " + url);
        String response = HTTPUtils.get(restURL + url, gwcUser, gwcPass);
        return response;
    }
    
}

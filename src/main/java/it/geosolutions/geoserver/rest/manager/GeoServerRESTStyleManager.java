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
package it.geosolutions.geoserver.rest.manager;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.Util;
import it.geosolutions.geoserver.rest.decoder.RESTStyle;
import it.geosolutions.geoserver.rest.decoder.RESTStyleList;
import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class GeoServerRESTStyleManager extends GeoServerRESTAbstractManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(GeoServerRESTStyleManager.class);
    
    /**
     * Default constructor.
     *
     * @param restURL GeoServer REST API endpoint
     * @param username GeoServer REST API authorized username
     * @param password GeoServer REST API password for the former username
     */
    public GeoServerRESTStyleManager(URL restURL, String username, String password)
            throws IllegalArgumentException {
        super(restURL, username, password);
    }

    /**
     * Check if a Style exists in the configured GeoServer instance.
     * @param name the name of the style to check for.
     * @return <TT>true</TT> on HTTP 200, <TT>false</TT> on HTTP 404
     * @throws RuntimeException if any other HTTP code than 200 or 404 was retrieved.
     */
    public boolean existsStyle(String name) throws RuntimeException {
        return existsStyle(name, Util.DEFAULT_QUIET_ON_NOT_FOUND);
    }
    
    /**
     * Check if a Style exists in the configured GeoServer instance. User can choose if log a possible exception or not
     * @param name the name of the style to check for.
     * @param quietOnNotFound if true, mute exception if false is returned
     * @return <TT>true</TT> on HTTP 200, <TT>false</TT> on HTTP 404
     * @throws RuntimeException if any other HTTP code than 200 or 404 was retrieved.
     */
   public boolean existsStyle(String name, boolean quietOnNotFound) {
       String url = buildXmlUrl(null, name);
       String composed = Util.appendQuietOnNotFound(quietOnNotFound, url);
       return HTTPUtils.exists(composed , gsuser, gspass);
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

        String response = HTTPUtils.get(gsBaseUrl + url, gsuser, gspass);
        return RESTStyleList.build(response);
    }

    public RESTStyle getStyle(String name) {
        String url = buildXmlUrl(null, name);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving Style " + name + " from " + url);
        }

        String response = HTTPUtils.get(url, gsuser, gspass);
        return RESTStyle.build(response);
    }

    /**
     * Get the SLD body of a Style.
     */
    public String getSLD(String styleName) {
        String url = buildUrl(null, styleName, ".sld");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving SLD body from " + url);
        }
        return HTTPUtils.get( url, gsuser, gspass);
    }


    //=========================================================================
    // Workspaces
    //=========================================================================

    /**
     *
     * @since GeoServer 2.2
     */
    public boolean existsStyle(String workspace, String name) {
        return existsStyle(workspace, name, Util.DEFAULT_QUIET_ON_NOT_FOUND);
    }
    
    /**
    *
    * @since GeoServer 2.6
    */
   public boolean existsStyle(String workspace, String name, boolean quietOnNotFound) {
       String url = buildXmlUrl(workspace, name);
       String composed = Util.appendQuietOnNotFound(quietOnNotFound, url);
       return HTTPUtils.exists(composed , gsuser, gspass);
   }

    /**
     * Get summary info about Styles in a workspace.
     *
     * @return summary info about Styles as a {@link RESTStyleList}
     * @since GeoServer 2.2
     */
    public RESTStyleList getStyles(String workspace) {
        String url = "/rest/workspaces/"+workspace+"/styles.xml";
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving Styles list from " + url);
        }

        String response = HTTPUtils.get(gsBaseUrl + url, gsuser, gspass);
        return RESTStyleList.build(response);
    }

    /**
     *
     * @since GeoServer 2.2
     */
    public RESTStyle getStyle(String workspace, String name) {
        String url = buildXmlUrl(workspace, name);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving Style " + name + " from " + url);
        }

        String response = HTTPUtils.get(url, gsuser, gspass);
        return RESTStyle.build(response);
    }

    /**
     * Get the SLD body of a Style.
     * @since GeoServer 2.2
     */
    public String getSLD(String workspace, String name) {
        String url = buildUrl(workspace, name, ".sld");
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("### Retrieving SLD body from " + url);
        }
        return HTTPUtils.get(url, gsuser, gspass);
    }

    //=========================================================================
    // Publishing
    //=========================================================================

    /**
     * Store and publish a Style.
     *
     * @param sldBody the full SLD document as a String.
     *
     * @return <TT>true</TT> if the operation completed successfully.
     */
    public boolean publishStyle(String sldBody) {
        /*
         * This is the equivalent call with cUrl:
         *
         * {@code curl -u admin:geoserver -XPOST \ -H 'Content-type: application/vnd.ogc.sld+xml' \ -d @$FULLSLD \
         * http://$GSIP:$GSPORT/$SERVLET/rest/styles}
         */
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
        /*
         * This is the equivalent call with cUrl:
         *
         * {@code curl -u admin:geoserver -XPOST \ -H 'Content-type: application/vnd.ogc.sld+xml' \ -d @$FULLSLD \
         * http://$GSIP:$GSPORT/$SERVLET/rest/styles?name=name}
         */
        if (sldBody == null || sldBody.isEmpty()) {
            throw new IllegalArgumentException("The style body may not be null or empty");
        }

        String sUrl = buildPostUrl(null, name);

        final String result = HTTPUtils.post(sUrl, sldBody, "application/vnd.ogc.sld+xml", gsuser, gspass);
        return result != null;
    }

    /**
     * Store and publish a Style.
     *
     * @param sldFile the File containing the SLD document.
     *
     * @return <TT>true</TT> if the operation completed successfully.
     */
    public boolean publishStyle(File sldFile) {
        return publishStyle(sldFile, null);
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
        String sUrl = buildPostUrl(null, name);
        LOGGER.debug("POSTing new style " + name + " to " + sUrl);
        String result = HTTPUtils.post(sUrl, sldFile, GeoServerRESTPublisher.Format.SLD.getContentType(), gsuser, gspass);
        return result != null;
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
        /*
         * This is the equivalent call with cUrl:
         *
         * {@code curl -u admin:geoserver -XPUT \ -H 'Content-type: application/vnd.ogc.sld+xml' \ -d @$FULLSLD \
         * http://$GSIP:$GSPORT/$SERVLET/rest/styles/$NAME}
         */
        if (sldBody == null || sldBody.isEmpty()) {
            throw new IllegalArgumentException("The style body may not be null or empty");
        } else if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("The style name may not be null or empty");
        }

        final String sUrl = buildUrl(null, name, null);

        final String result = HTTPUtils.put(sUrl, sldBody, "application/vnd.ogc.sld+xml", gsuser, gspass);
        return result != null;
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

        if (sldFile == null) {
            throw new IllegalArgumentException("Unable to updateStyle using a null parameter file");
        } else if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("The style name may not be null or empty");
        }

        final String sUrl = buildUrl(null, name, null);

        final String result = HTTPUtils.put(sUrl, sldFile,
                "application/vnd.ogc.sld+xml", gsuser, gspass);
        return result != null;

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
        if (styleName == null || styleName.isEmpty())
            throw new IllegalArgumentException(
                    "Check styleName parameter, it may never be null or empty");

        // check style name
        // TODO may we want to throw an exception instead of
        // change style name?
        if(styleName.contains(":"))
            LOGGER.warn("Style name is going to be changed ["+styleName+"]");
        styleName = styleName.replaceAll(":", "_");
        
        // currently REST interface does't support URLencoded URL 
//        styleName = URLEncoder.encode(styleName);

        String sUrl = buildUrl(null, styleName, null);
        if (purge) {
            sUrl += "?purge=true";
        }

        return HTTPUtils.delete(sUrl, gsuser, gspass);
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
        try {
            return removeStyle(styleName, true);
        } catch (IllegalArgumentException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        }
        return false;
    }

    //=========================================================================
    // Publishing in workspace
    //=========================================================================

    /**
     * Store and publish a Style.
     *
     * @param sldBody the full SLD document as a String.
     *
     * @return <TT>true</TT> if the operation completed successfully.
     * @since GeoServer 2.2
     */
    public boolean publishStyleInWorkspace(final String workspace, String sldBody) {
        try {
            return publishStyleInWorkspace(workspace, sldBody);
        } catch (IllegalArgumentException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        }
        return false;
    }

    /**
     * Store and publish a Style, assigning it a name.
     *
     * @param sldBody the full SLD document as a String.
     * @param name the Style name.
     *
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if the style body is null or empty.
     * @since GeoServer 2.2
     */
    public boolean publishStyleInWorkspace(final String workspace, final String sldBody, final String name)
            throws IllegalArgumentException {

        if (sldBody == null || sldBody.isEmpty()) {
            throw new IllegalArgumentException("The style body may not be null or empty");
        }
        String sUrl = buildPostUrl(workspace, name);
        final String result = HTTPUtils.post(sUrl, sldBody, "application/vnd.ogc.sld+xml", gsuser, gspass);
        return result != null;
    }

    /**
     * Store and publish a Style.
     *
     * @param sldFile the File containing the SLD document.
     *
     * @return <TT>true</TT> if the operation completed successfully.
     * @since GeoServer 2.2
     */
    public boolean publishStyleInWorkspace(final String workspace, File sldFile) {
        return publishStyleInWorkspace(workspace, sldFile, null);
    }

    /**
     * Store and publish a Style, assigning it a name.
     *
     * @param sldFile the File containing the SLD document.
     * @param name the Style name.
     *
     * @return <TT>true</TT> if the operation completed successfully.
     * @since GeoServer 2.2
     */
    public boolean publishStyleInWorkspace(final String workspace, File sldFile, String name) {
        String sUrl = buildPostUrl(workspace, name);
        LOGGER.debug("POSTing new style " + name + " to " + sUrl);
        String result = HTTPUtils.post(sUrl, sldFile, GeoServerRESTPublisher.Format.SLD.getContentType(), gsuser, gspass);
        return result != null;
    }

    /**
     * Update a Style.
     *
     * @param sldBody the new SLD document as a String.
     * @param name the Style name to update.
     *
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if the style body or name are null or empty.
     * @since GeoServer 2.2
     */
    public boolean updateStyleInWorkspace(final String workspace, final String sldBody, final String name)
            throws IllegalArgumentException {
        if (sldBody == null || sldBody.isEmpty()) {
            throw new IllegalArgumentException("The style body may not be null or empty");
        } else if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("The style name may not be null or empty");
        }

        final String sUrl = buildUrl(workspace, name, null);

        final String result = HTTPUtils.put(sUrl, sldBody,
                "application/vnd.ogc.sld+xml", gsuser, gspass);
        return result != null;
    }

    /**
     * Update a Style.
     *
     * @param sldFile the File containing the SLD document.
     * @param name the Style name.
     *
     * @return <TT>true</TT> if the operation completed successfully.
     * @throws IllegalArgumentException if the sldFile file or name are null or name is empty.
     * @since GeoServer 2.2
     */
    public boolean updateStyleInWorkspace(final String workspace, final File sldFile, final String name)
            throws IllegalArgumentException {

        if (sldFile == null) {
            throw new IllegalArgumentException("Unable to updateStyle using a null parameter file");
        } else if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("The style name may not be null or empty");
        }

        final String sUrl = buildUrl(workspace, name, null);

        final String result = HTTPUtils.put(sUrl, sldFile,
                "application/vnd.ogc.sld+xml", gsuser, gspass);
        return result != null;
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
     * @since GeoServer 2.2
     */
    public boolean removeStyleInWorkspace(final String workspace, String styleName, final boolean purge)
            throws IllegalArgumentException {
        if (styleName == null || styleName.isEmpty())
            throw new IllegalArgumentException(
                    "Check styleName parameter, it may never be null or empty");

        // check style name
        // TODO may we want to throw an exception instead of change style name?
        if(styleName.contains(":"))
            LOGGER.warn("Style name is going to be changed ["+styleName+"]");
        styleName = styleName.replaceAll(":", "_");
        styleName = URLEncoder.encode(styleName);

        String sUrl = buildUrl(workspace, styleName, null);

        if (purge) {
            sUrl += "?purge=true";
        }

        return HTTPUtils.delete(sUrl, gsuser, gspass);
    }

    /**
     * Remove a Style.
     * <P>
     * The Style will be unpublished and the related SLD file will be removed.
     *
     * @param styleName the name of the Style to remove.
     *
     * @return <TT>true</TT> if the operation completed successfully.
     * @since GeoServer 2.2
     */
    public boolean removeStyleInWorkspace(final String workspace, String styleName) {
        try {
            return removeStyleInWorkspace(workspace, styleName, true);
        } catch (IllegalArgumentException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        }
        return false;
    }

    //=========================================================================
    // Util methods
    //=========================================================================

    /**
     * Creates a URL for the given stylename with the name in querystring
     * @param workspace nullable workspace name
     * @param name style name
     * @return
     */
    protected String buildPostUrl(final String workspace, String name) {
        StringBuilder sUrl = new StringBuilder(gsBaseUrl.toString()).append("/rest");

        if(workspace != null)
            sUrl.append("/workspaces/").append(workspace);

        sUrl.append("/styles");
        if ( name != null && !name.isEmpty()) {
            sUrl.append("?name=").append(URLEncoder.encode(name));
        }
        return sUrl.toString();
    }


    protected String buildXmlUrl(final String workspace, final String name) {
        return buildUrl(workspace, name, ".xml");
    }

    /**
     * Creates a URL for the given stylename with the name in the REST path
     * @param workspace nullable workspace name
     * @param name style name
     * @param ext nullable output extension (e.g. ".xml" ".sld")
     */
    protected String buildUrl(final String workspace, final String name, final String ext) {
        StringBuilder sUrl = new StringBuilder(gsBaseUrl.toString()).append("/rest");

        if(workspace != null)
            sUrl.append("/workspaces/").append(workspace);

        sUrl.append("/styles/").append(URLEncoder.encode(name));
                
        if(ext != null)
            sUrl.append(ext);

        return sUrl.toString();
    }

}

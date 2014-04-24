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

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Low level HTTP utilities.
 */
public class HTTPUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPUtils.class);

    /**
     * Performs an HTTP GET on the given URL.
     *
     * @param url The URL where to connect to.
     * @return The HTTP response as a String if the HTTP response code was 200
     *         (OK).
     * @throws MalformedURLException
     */
    public static String get(String url) throws MalformedURLException {
        return get(url, null, null);
    }

    /**
     * Performs an HTTP GET on the given URL. <BR>
     * Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200
     *         (OK).
     * @throws MalformedURLException
     */
    public static String get(String url, String username, String pw) {

        GetMethod httpMethod = null;
        HttpClient client = new HttpClient();
        HttpConnectionManager connectionManager = client.getHttpConnectionManager();
        try {
            String encodedUrl = encodeUrl(url);
            setAuth(client, encodedUrl, username, pw);
            httpMethod = new GetMethod(encodedUrl);
            connectionManager.getParams().setConnectionTimeout(5000);
            int status = client.executeMethod(httpMethod);
            if (status == HttpStatus.SC_OK) {
                InputStream is = httpMethod.getResponseBodyAsStream();
                String response = IOUtils.toString(is);
                IOUtils.closeQuietly(is);
                if (response.trim().length() == 0) { // sometime gs rest fails
                    LOGGER.warn("ResponseBody is empty");
                    return null;
                } else {
                    return response;
                }
            } else {
                LOGGER.info("(" + status + ") " + HttpStatus.getStatusText(status) + " -- " + url);
            }
        } catch (ConnectException e) {
            LOGGER.info("Couldn't connect to [" + url + "]");
        } catch (IOException e) {
            LOGGER.info("Error talking to [" + url + "]", e);
        } finally {
            if (httpMethod != null)
                httpMethod.releaseConnection();
            connectionManager.closeIdleConnections(0);
        }

        return null;
    }

    static String encodeUrl(String url) {
        // perform some simple encoding to the path names.  This takes care of cases where
        // layers or workspaces have illegal http characters.
        // it cannot fix all
        String protocol, authority, path, query, fragment = null;
        String[] protocolPathParts = url.split("://", 2);
        if (protocolPathParts.length == 1) {
            // unexpected format so just try out url
            return url;
        }

        protocol = protocolPathParts[0];
        String[] pathQueryParts = protocolPathParts[1].split("\\?", 2);
        path = pathQueryParts[0];
        if (pathQueryParts.length == 1) {
            query = null;
        } else {
            query = pathQueryParts[1];
        }


        if (query == null) {
            String[] fragmentParts = path.split("#", 2);
            if (fragmentParts.length > 1) {
                path = fragmentParts[0];
                fragment = fragmentParts[1];
            }
        } else {
            String[] fragmentParts = query.split("#", 2);
            if (fragmentParts.length > 1) {
                query = fragmentParts[0];
                fragment = fragmentParts[1];
            }
        }

        int firstSlash = path.indexOf('/');
        if (firstSlash > -1) {
            authority = path.substring(0, firstSlash);
            path = path.substring(firstSlash);
        } else {
            authority = path;
            path = null;
        }

        try {
            return new URI(protocol, authority, path, query, fragment).toString();
        } catch (URISyntaxException e) {
            // fallback to original string
            return url;
        }
    }

    /**
     * PUTs a File to the given URL. <BR>
     * Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param file The File to be sent.
     * @param contentType The content-type to advert in the PUT.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200
     *         (OK).
     * @throws MalformedURLException
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    public static String put(String url, File file, String contentType, String username, String pw) {
        return put(url, new FileRequestEntity(file, contentType), username, pw);
    }

    /**
     * PUTs a String to the given URL. <BR>
     * Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param content The content to be sent as a String.
     * @param contentType The content-type to advert in the PUT.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200
     *         (OK).
     * @throws MalformedURLException
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    public static String put(String url, String content, String contentType, String username, String pw) {
        try {
            return put(url, new StringRequestEntity(content, contentType, null), username, pw);
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Cannot PUT " + url, ex);
            return null;
        }
    }

    /**
     * PUTs a String representing an XML document to the given URL. <BR>
     * Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param content The XML content to be sent as a String.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200
     *         (OK).
     * @throws MalformedURLException
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    public static String putXml(String url, String content, String username, String pw) {
        return put(url, content, "text/xml", username, pw);
    }

    /**
     * Performs a PUT to the given URL. <BR>
     * Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param requestEntity The request to be sent.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200
     *         (OK).
     * @throws MalformedURLException
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    public static String put(String url, RequestEntity requestEntity, String username, String pw) {
        return send(new PutMethod(url), url, requestEntity, username, pw);
    }

    /**
     * POSTs a File to the given URL. <BR>
     * Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param file The File to be sent.
     * @param contentType The content-type to advert in the POST.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200
     *         (OK).
     * @throws MalformedURLException
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    public static String post(String url, File file, String contentType, String username, String pw) {
        return post(url, new FileRequestEntity(file, contentType), username, pw);
    }

    /**
     * POSTs a String to the given URL. <BR>
     * Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param content The content to be sent as a String.
     * @param contentType The content-type to advert in the POST.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200
     *         (OK).
     * @throws MalformedURLException
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    public static String post(String url, String content, String contentType, String username, String pw) {
        try {
            return post(url, new StringRequestEntity(content, contentType, null), username, pw);
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Cannot POST " + url, ex);
            return null;
        }
    }

    /**
     * POSTs a String representing an XML document to the given URL. <BR>
     * Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param content The XML content to be sent as a String.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200
     *         (OK).
     * @throws MalformedURLException
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    public static String postXml(String url, String content, String username, String pw) {
        return post(url, content, "text/xml", username, pw);
    }

    /**
     * Performs a POST to the given URL. <BR>
     * Basic auth is used if both username and pw are not null.
     *
     * @param url The URL where to connect to.
     * @param requestEntity The request to be sent.
     * @param username Basic auth credential. No basic auth if null.
     * @param pw Basic auth credential. No basic auth if null.
     * @return The HTTP response as a String if the HTTP response code was 200
     *         (OK).
     * @throws MalformedURLException
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    public static String post(String url, RequestEntity requestEntity, String username, String pw) {
        return send(new PostMethod(url), url, requestEntity, username, pw);
    }

    /**
     * Send an HTTP request (PUT or POST) to a server. <BR>
     * Basic auth is used if both username and pw are not null.
     * <P>
     * Only
     * <UL>
     * <LI>200: OK</LI>
     * <LI>201: ACCEPTED</LI>
     * <LI>202: CREATED</LI>
     * </UL>
     * are accepted as successful codes; in these cases the response string will
     * be returned.
     *
     * @return the HTTP response or <TT>null</TT> on errors.
     */
    private static String send(final EntityEnclosingMethod httpMethod, String url,
                               RequestEntity requestEntity, String username, String pw) {
        HttpClient client = new HttpClient();
        HttpConnectionManager connectionManager = client.getHttpConnectionManager();
        String encodedUrl = encodeUrl(url);
        try {
            setAuth(client, encodedUrl, username, pw);
            connectionManager.getParams().setConnectionTimeout(5000);
            if (requestEntity != null)
                httpMethod.setRequestEntity(requestEntity);
            int status = client.executeMethod(httpMethod);

            switch (status) {
            case HttpURLConnection.HTTP_OK:
            case HttpURLConnection.HTTP_CREATED:
            case HttpURLConnection.HTTP_ACCEPTED:
                String response = IOUtils.toString(httpMethod.getResponseBodyAsStream());
                // LOGGER.info("================= POST " + url);
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("HTTP " + httpMethod.getStatusText() + ": " + response);
                return response;
            default:
                LOGGER.warn("Bad response: code[" + status + "]" + " msg[" + httpMethod.getStatusText() + "]"
                            + " url[" + encodedUrl + "]" + " method[" + httpMethod.getClass().getSimpleName()
                            + "]: " + IOUtils.toString(httpMethod.getResponseBodyAsStream()));
                return null;
            }
        } catch (ConnectException e) {
            LOGGER.info("Couldn't connect to [" + encodedUrl + "]");
            return null;
        } catch (IOException e) {
            LOGGER.error("Error talking to " + encodedUrl + " : " + e.getLocalizedMessage());
            return null;
        } finally {
            if (httpMethod != null)
                httpMethod.releaseConnection();
            connectionManager.closeIdleConnections(0);
        }
    }

    public static boolean delete(String url, final String user, final String pw) {

        DeleteMethod httpMethod = null;
        HttpClient client = new HttpClient();
        HttpConnectionManager connectionManager = client.getHttpConnectionManager();
        String encodedUrl = encodeUrl(url);
        try {
            setAuth(client, encodedUrl, user, pw);
            httpMethod = new DeleteMethod(encodedUrl);
            connectionManager.getParams().setConnectionTimeout(5000);
            int status = client.executeMethod(httpMethod);
            String response = "";
            if (status == HttpStatus.SC_OK) {
                InputStream is = httpMethod.getResponseBodyAsStream();
                response = IOUtils.toString(is);
                IOUtils.closeQuietly(is);
                if (response.trim().equals("")) {
                    if (LOGGER.isDebugEnabled())
                        LOGGER
                            .debug("ResponseBody is empty (this may be not an error since we just performed a DELETE call)");
                    return true;
                }
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("(" + status + ") " + httpMethod.getStatusText() + " -- " + encodedUrl);
                return true;
            } else {
                LOGGER.info("(" + status + ") " + httpMethod.getStatusText() + " -- " + encodedUrl);
                LOGGER.info("Response: '" + response + "'");
            }
        } catch (ConnectException e) {
            LOGGER.info("Couldn't connect to [" + encodedUrl + "]");
        } catch (IOException e) {
            LOGGER.info("Error talking to [" + encodedUrl + "]", e);
        } finally {
            if (httpMethod != null)
                httpMethod.releaseConnection();
            connectionManager.closeIdleConnections(0);
        }

        return false;
    }

    /**
     * @return true if the server response was an HTTP_OK
     */
    public static boolean httpPing(String url) {
        return httpPing(url, null, null);
    }

    public static boolean httpPing(String url, String username, String pw) {

        GetMethod httpMethod = null;
        HttpClient client = new HttpClient();
        HttpConnectionManager connectionManager = client.getHttpConnectionManager();
        try {
            String encodedUrl = encodeUrl(url);
            setAuth(client, encodedUrl, username, pw);
            httpMethod = new GetMethod(encodedUrl);
            connectionManager.getParams().setConnectionTimeout(2000);
            int status = client.executeMethod(httpMethod);
            if (status != HttpStatus.SC_OK) {
                LOGGER.warn("PING failed at '" + encodedUrl + "': (" + status + ") " + httpMethod.getStatusText());
                return false;
            } else {
                return true;
            }
        } catch (ConnectException e) {
            return false;
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage(),e);
            return false;
        } finally {
            if (httpMethod != null)
                httpMethod.releaseConnection();
            connectionManager.closeIdleConnections(0);
        }
    }

    /**
     * Used to query for REST resources.
     *
     * @param url The URL of the REST resource to query about.
     * @param username
     * @param pw
     * @return true on 200, false on 404.
     * @throws RuntimeException on unhandled status or exceptions.
     */
    public static boolean exists(String url, String username, String pw) {

        GetMethod httpMethod = null;
        HttpClient client = new HttpClient();
        HttpConnectionManager connectionManager = client.getHttpConnectionManager();
        try {
            String encodedUrl = encodeUrl(url);
            setAuth(client, encodedUrl, username, pw);
            httpMethod = new GetMethod(encodedUrl);
            connectionManager.getParams().setConnectionTimeout(2000);
            int status = client.executeMethod(httpMethod);
            switch (status) {
            case HttpStatus.SC_OK:
                return true;
            case HttpStatus.SC_NOT_FOUND:
                return false;
            default:
                throw new RuntimeException("Unhandled response status at '" + encodedUrl + "': (" + status + ") "
                                           + httpMethod.getStatusText());
            }
        } catch (ConnectException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (httpMethod != null)
                httpMethod.releaseConnection();
            connectionManager.closeIdleConnections(0);
        }
    }

    private static void setAuth(HttpClient client, String url, String username, String pw)
        throws MalformedURLException {

        String encodedUrl = encodeUrl(url);
        URL u = new URL(encodedUrl);
        if (username != null && pw != null) {
            Credentials defaultcreds = new UsernamePasswordCredentials(username, pw);
            client.getState().setCredentials(new AuthScope(u.getHost(), u.getPort()), defaultcreds);
            client.getParams().setAuthenticationPreemptive(true); // GS2 by
                                                                  // default
                                                                  // always
                                                                  // requires
                                                                  // authentication
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Not setting credentials to access to " + encodedUrl);
            }
        }
    }

    /**
     * @param geoserverURL
     * @return recursively remove ending slashes
     */
    public static String decurtSlash(String geoserverURL) {
        if (geoserverURL!=null && geoserverURL.endsWith("/")) {
            geoserverURL = decurtSlash(geoserverURL.substring(0, geoserverURL.length() - 1));
        }
        return geoserverURL;
    }

    /**
     * @param str a string array
     * @return create a StringBuilder appending all the passed arguments
     */
    public static StringBuilder append(String ... str){
        if (str==null){
            return null;
        }

        StringBuilder buf=new StringBuilder();
        for (String s: str){
            if (s!=null)
                buf.append(s);
        }
        return buf;
    }

    /**
     * Wrapper for {@link #append(String...)}
     * @param base base URL
     * @param str strings to append
     * @return the base URL with parameters attached
     */
    public static StringBuilder append(URL base, String ... str){
        if (str==null){
            return append(base.toString());
        }

        StringBuilder buf=new StringBuilder(base.toString());
        for (String s: str){
            if (s!=null)
                buf.append(s);
        }
        return buf;
    }

}

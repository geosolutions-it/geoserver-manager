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

import it.geosolutions.geoserver.rest.manager.GeoServerRESTAbstractManager;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStoreManager;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStructuredGridCoverageReaderManager;
import it.geosolutions.geoserver.rest.manager.GeoServerRESTStyleManager;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * <i>The</i> single entry point to all of geoserver-manager functionality.
 * 
 * Instance this one, and use getters to use different components. These are:
 * <ul>
 * <li>getReader() simple, high-level access methods.
 * <li>getPublisher() simple, high-level pubhish methods.
 * <li>get<i>Foo</i>Manager, full-fledged management of catalog objects.
 * </ul>
 * 
 * @author Oscar Fonts
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GeoServerRESTManager extends GeoServerRESTAbstractManager {

    private final GeoServerRESTPublisher publisher;
    private final GeoServerRESTReader reader;

    private final GeoServerRESTStoreManager storeManager;
    private final GeoServerRESTStyleManager styleManager;
    
    private final GeoServerRESTStructuredGridCoverageReaderManager structuredGridCoverageReader;

    /**
     * Default constructor.
     * 
     * Indicates connection parameters to remote GeoServer instance.
     * 
     * @param restURL GeoServer REST API endpoint
     * @param username GeoServer REST API authorized username
     * @param password GeoServer REST API password for the former username
     * @throws MalformedURLException {@link GeoServerRESTAbstractManager#GeoServerRESTAbstractManager(URL, String, String)}
     * @throws IllegalArgumentException {@link GeoServerRESTAbstractManager#GeoServerRESTAbstractManager(URL, String, String)}
     */
    public GeoServerRESTManager(URL restURL, String username, String password)
            throws IllegalArgumentException {
        super(restURL, username, password);

        // Internal publisher and reader, provide simple access methods.
        publisher = new GeoServerRESTPublisher(restURL.toString(), username, password);
        reader = new GeoServerRESTReader(restURL, username, password);
        structuredGridCoverageReader = new GeoServerRESTStructuredGridCoverageReaderManager(restURL, username, password);
        storeManager = new GeoServerRESTStoreManager(restURL, gsuser, gspass);
        styleManager = new GeoServerRESTStyleManager(restURL, gsuser, gspass);
    }

    public GeoServerRESTPublisher getPublisher() {
        return publisher;
    }

    public GeoServerRESTReader getReader() {
        return reader;
    }

    public GeoServerRESTStoreManager getStoreManager() {
        return storeManager;
    }

    public GeoServerRESTStyleManager getStyleManager() {
        return styleManager;
    }

    public GeoServerRESTStructuredGridCoverageReaderManager getStructuredGridCoverageReader() {
        return structuredGridCoverageReader;
    }

}

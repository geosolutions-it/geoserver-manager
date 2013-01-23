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

import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSAbstractDatastoreEncoder;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Manage datastores.
 * 
 * To pass connection parameters, use the encoders derived from {@link GSAbstractDatastoreEncoder}.
 * 
 * @author Oscar Fonts
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 * 
 * @deprecated currently this is only a {@link GeoServerRESTStoreManager} wrapper for datastore
 */
public class GeoServerRESTDatastoreManager extends GeoServerRESTStoreManager {

    /**
     * Default constructor.
     * 
     * @param restURL GeoServer REST API endpoint
     * @param username GeoServer REST API authorized username
     * @param password GeoServer REST API password for the former username
     * @throws MalformedURLException
     * @throws IllegalArgumentException
     */
    public GeoServerRESTDatastoreManager(URL restURL, String username, String password)
            throws IllegalArgumentException, MalformedURLException {
        super(restURL, username, password);
    }

    /**
     * Create a store.
     * 
     * @param workspace Name of the workspace to contain the store. This will also be the prefix of any layer names contained in the store.
     * @param datastore the set of parameters to be set to the store (including connection parameters).
     * @return <TT>true</TT> if the store has been successfully created, <TT>false</TT> otherwise
     * 
     * @deprecated use {@link GeoServerRESTStoreManager#create(String, GSAbstractStoreEncoder)}
     */
    public boolean create(String workspace, GSAbstractDatastoreEncoder store) {
        return super.create(workspace, store);
    }

    /**
     * Update a store.
     * 
     * @param workspace Name of the workspace that contains the store.
     * @param datastore the set of parameters to be set to the store (including connection parameters).
     * @return <TT>true</TT> if the store has been successfully updated, <TT>false</TT> otherwise
     * @deprecated use {@link GeoServerRESTStoreManager#update(String, GSAbstractStoreEncoder)}
     */
    public boolean update(String workspace, GSAbstractDatastoreEncoder store) {
        return super.update(workspace, store);
    }

    /**
     * Remove a given CoverageStore in a given Workspace.
     * 
     * @param workspace The name of the workspace
     * @param storename The name of the CoverageStore to remove.
     * @param recurse if remove should be performed recursively
     * @return <TT>true</TT> if the CoverageStore was successfully removed.
     * @throws MalformedURLException
     */
    public boolean remove(final String workspace, final GSAbstractStoreEncoder store,
            final boolean recurse) throws IllegalArgumentException, MalformedURLException {

        return super.remove(workspace, store, recurse);
    }
}

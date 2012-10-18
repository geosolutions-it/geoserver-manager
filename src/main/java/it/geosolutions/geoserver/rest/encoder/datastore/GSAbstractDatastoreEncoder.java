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
package it.geosolutions.geoserver.rest.encoder.datastore;

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.NestedElementEncoder;

import java.util.Map;

/**
 * Generic Datastore encoder.
 * 
 * Provides getters and setters for parameters common to all Datastores, an internal placeholder for specific connection parameters, and a constructor
 * to read parameters from a {@link RESTDataStore}.
 * 
 * @author Oscar Fonts
 */
public abstract class GSAbstractDatastoreEncoder extends GSAbstractStoreEncoder {

    final static String ROOT = "dataStore";

    NestedElementEncoder connectionParameters = new NestedElementEncoder("connectionParameters");

    GSAbstractDatastoreEncoder(String storeName) {
        super(GeoServerRESTPublisher.StoreType.DATASTORES, ROOT);
        // Add mandatory parameter
        ensureValidName(storeName);
        setName(storeName);

        // Add connection parameters
        addContent(connectionParameters.getRoot());
    }

    /**
     * Create a {@value #TYPE} datastore encoder from a store read from server.
     * 
     * @param store The existing store.
     * @throws IllegalArgumentException if store type or mandatory parameters are not valid
     */
    GSAbstractDatastoreEncoder(RESTDataStore store) {
        this(store.getName());

        // Match datastore type
        ensureValidType(store.getStoreType());

        // Copy store parameters
        setDescription(store.getDescription());
        setEnabled(store.isEnabled());

        // Copy connection parameters - bulk
        Map<String, String> params = store.getConnectionParameters();
        for (String key : params.keySet()) {
            connectionParameters.set(key, params.get(key));
        }
    }

    /**
     * The type of the implementing datastore.
     */
    protected abstract String getValidType();

}

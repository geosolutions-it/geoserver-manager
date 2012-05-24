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

import it.geosolutions.geoserver.rest.HTTPUtils;
import it.geosolutions.geoserver.rest.encoder.datastore.GSAbstractDatastoreEncoder;

import java.net.URL;

/**
 * Manage datastores.
 * 
 * To pass connection parameters, use the encoders derived from
 * {@link GSAbstractDatastoreEncoder}.
 * 
 * @author Oscar Fonts
 */
public class GeoServerRESTDatastoreManager extends GeoServerRESTAbstractManager {

	/**
	 * Default constructor.
	 * 
	 * @param restURL  GeoServer REST API endpoint
	 * @param username GeoServer REST API authorized username
	 * @param password GeoServer REST API password for the former username
	 */
	public GeoServerRESTDatastoreManager(URL restURL, String username, String password) {
		super(restURL, username, password);
	}
	
	/**
	 * Create a datastore.
	 * 
	 * @param workspace
	 *            Name of the workspace to contain the datastore. This will also
	 *            be the prefix of any layer names contained in the datastore.
	 * @param datastore
	 *            the set of parameters to be set to the datastore (including
	 *            connection parameters).
	 * @return <TT>true</TT> if the datastore has been successfully
	 *         created, <TT>false</TT> otherwise
	 */

	public boolean create(String workspace, GSAbstractDatastoreEncoder datastore) {
		String sUrl = restURL + "/rest/workspaces/" + workspace
				+ "/datastores/";
		String xml = datastore.toString();
		String result = HTTPUtils.postXml(sUrl, xml, gsuser, gspass);
		return result != null;
	}

	/**
	 * Update a datastore.
	 * 
	 * @param workspace
	 *            Name of the workspace that contains the datastore.
	 * @param datastore
	 *            the set of parameters to be set to the datastore (including
	 *            connection parameters).
	 * @return <TT>true</TT> if the datastore has been successfully
	 *         updated, <TT>false</TT> otherwise
	 */
	public boolean update(String workspace, GSAbstractDatastoreEncoder datastore) {
		String sUrl = restURL + "/rest/workspaces/" + workspace
				+ "/datastores/" + datastore.getName();
		String xml = datastore.toString();
		String result = HTTPUtils.putXml(sUrl, xml, gsuser, gspass);
		return result != null;
	}
}

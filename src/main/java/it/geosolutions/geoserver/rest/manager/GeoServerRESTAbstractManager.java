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

import java.net.URL;

/**
 * Abstract manager, common functionality and interface
 * for all GeoServerREST<i>Foo</i>Manager classes.
 * 
 * @author Oscar Fonts
 */
public abstract class GeoServerRESTAbstractManager {
	
	protected final URL restURL;
	protected final String gsuser;
	protected final String gspass;
	
	/**
	 * Default constructor.
	 * 
	 * Indicates connection parameters to remote GeoServer instance.
	 * 
	 * @param restURL  GeoServer REST API endpoint
	 * @param username GeoServer REST API authorized username
	 * @param password GeoServer REST API password for the former username
	 */
	public GeoServerRESTAbstractManager(URL restURL, String username, String password) {		
		this.restURL = restURL;
		this.gsuser = username;
		this.gspass = password;
	}
}

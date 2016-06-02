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

import it.geosolutions.geoserver.rest.GeoServerRESTPublisher.StoreType;
import it.geosolutions.geoserver.rest.encoder.GSAbstractStoreEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.XmlElement;

import java.net.URL;

/**
 * Encoder for a {@value #TYPE} datastore.
 * 
 * @author Peter van de Riet
 */
public class GSMosaicDatastoreEncoder extends GSAbstractStoreEncoder {
	static final String TYPE = "ImageMosaic";

	public GSMosaicDatastoreEncoder(String name) {
		 super(StoreType.COVERAGESTORES, name);
//		super(name);
		 setName(name);

		// Set mandatory parameter
		setType(TYPE);
		add("enabled", "true");
	}

	public void setUrl(URL url) {
		add("url", url.toString());
	}

	/**
	 * @return {@value #TYPE}
	 */
	protected String getValidType() {
		return TYPE;
	}

	public void setWorkspaceName(String workspaceName) {
		GsWorkspaceElement workspaceXml = new GsWorkspaceElement(workspaceName);
		addContent(workspaceXml.getRoot());
	}

	private class GsWorkspaceElement extends XmlElement {
		public GsWorkspaceElement(String workspaceName) {
			super("workspace");
			add("name", workspaceName);
		}
	}
}

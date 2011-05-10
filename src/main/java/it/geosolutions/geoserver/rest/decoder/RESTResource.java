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

package it.geosolutions.geoserver.rest.decoder;

import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Parse a resource (FeatureType or Coverage) returned as XML REST objects.
 *
 * @author etj
 */
public class RESTResource {
	protected final Element rootElem;

    public static RESTResource build(String response) {
        Element elem = JDOMBuilder.buildElement(response);
        return elem == null? null : new RESTCoverage(elem);
	}

	public RESTResource(Element resource) {
		this.rootElem = resource;
	}

	public String getName() {
		return rootElem.getChildText("name");
	}

    public String getTitle() {
		return rootElem.getChildText("title");
	}

	public String getNativeName() {
		return rootElem.getChildText("nativeName");
	}

	public String getAbstract() {
		return rootElem.getChildText("abstract");
	}

	public String getNameSpace() {
		return rootElem.getChild("namespace").getChildText("name");
	}

	public String getStoreName() {
		return rootElem.getChild("store").getChildText("name");
	}

	public String getStoreType() {
		return rootElem.getChild("store").getAttributeValue("class");
	}

    public String getStoreUrl() {
		Element store = rootElem.getChild("store");
        Element atom = store.getChild("link", Namespace.getNamespace("atom", "http://www.w3.org/2005/Atom"));
        return atom.getAttributeValue("href");
    }

	public String getCRS() {
		Element elBBox = rootElem.getChild("latLonBoundingBox");
		return elBBox.getChildText("crs");
	}

	protected double getLatLonEdge(String edge) {
		Element elBBox = rootElem.getChild("latLonBoundingBox");
		return Double.parseDouble(elBBox.getChildText(edge));
	}

	public double getMinX() {
		return getLatLonEdge("minx");
	}
	public double getMaxX() {
		return getLatLonEdge("maxx");
	}
	public double getMinY() {
		return getLatLonEdge("miny");
	}
	public double getMaxY() {
		return getLatLonEdge("maxy");
	}
}

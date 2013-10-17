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
 * Parse <TT>Layer</TT>s returned as XML REST objects.
 *
 * <P>This is the XML REST representation:
 * <PRE>
 * {@code
<layer>
    <name>tasmania_cities</name>
    <path>/</path>
    <type>VECTOR</type>
    <defaultStyle>
        <name>capitals</name>
        <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/styles/capitals.xml" type="application/xml"/>
    </defaultStyle>
    <resource class="featureType">
        <name>tasmania_cities</name>
        <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/workspaces/topp/datastores/taz_shapes/featuretypes/tasmania_cities.xml" type="application/xml"/>
    </resource>
    <enabled>true</enabled>
    <attribution>
        <logoWidth>0</logoWidth>
        <logoHeight>0</logoHeight>
    </attribution>
</layer>
 * }</PRE>
 * @author etj
 */
public class RESTLayer {
	private final Element layerElem;

	public enum Type {
		VECTOR("VECTOR"),
		RASTER("RASTER"),
		UNKNOWN(null);

		private final String restName;

		private Type(String restName) {
			this.restName = restName;
		}

		public static Type get(String restName) {
			for (Type type : values()) {
				if(type == UNKNOWN)
					continue;
				if(type.restName.equals(restName))
					return type;
			}
			return UNKNOWN;
		}
	};

    public static RESTLayer build(String response) {
        if(response == null)
            return null;
        
        Element pb = JDOMBuilder.buildElement(response);
        if(pb != null)
            return new RESTLayer(pb);
        else
            return null;
	}

	public RESTLayer(Element layerElem) {
		this.layerElem = layerElem;
	}

	public String getName() {
		return layerElem.getChildText("name");
	}

	public String getTypeString() {
		return layerElem.getChildText("type");
	}

	public Type getType() {
		return Type.get(getTypeString());
	}

	public String getDefaultStyle() {
		Element defaultStyle = layerElem.getChild("defaultStyle");
		return defaultStyle == null? null : defaultStyle.getChildText("name");
  	}

    /**
     * @see <a href="https://github.com/geosolutions-it/geoserver-manager/issues/100">this issue</a>
     */
	public String getDefaultStyleWorkspace() {
		Element defaultStyle = layerElem.getChild("defaultStyle");
		return defaultStyle == null? null : defaultStyle.getChildText("workspace");
  	}

	public String getTitle() {
		Element resource = layerElem.getChild("resource");
		return resource.getChildText("title");
	}

	public String getAbstract() {
		Element resource = layerElem.getChild("resource");
		return resource.getChildText("abstract");
	}

	public String getNameSpace() {
		Element resource = layerElem.getChild("resource");
		return resource.getChild("namespace").getChildText("name");
	}

//	public String getStoreName() {
//		Element resource = layerElem.getChild("resource");
//		return resource.getChild("store").getChildText("name");
//	}
//
//	public String getStoreType() {
//		Element resource = layerElem.getChild("resource");
//		return resource.getChild("store").getAttributeValue("class");
//	}

//	public String getCRS() {
//		Element resource = layerElem.getChild("resource");
//		Element elBBox = resource.getChild("latLonBoundingBox");
//		return elBBox.getChildText("crs");
//	}

    /**
     * Get the URL to retrieve the featuretype.
     * <PRE>{@code
        <resource class="featureType">
        <name>tasmania_cities</name>
        <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/workspaces/topp/datastores/taz_shapes/featuretypes/tasmania_cities.xml" type="application/xml"/>
    </resource>
     * }</CODE>
     */
    public String getResourceUrl() {
	Element resource = layerElem.getChild("resource");
        Element atom = resource.getChild("link", Namespace.getNamespace("atom", "http://www.w3.org/2005/Atom"));
        return atom.getAttributeValue("href");
    }

//	protected double getLatLonEdge(String edge) {
//		Element resource = layerElem.getChild("resource");
//		Element elBBox = resource.getChild("latLonBoundingBox");
//		return Double.parseDouble(elBBox.getChildText(edge));
//	}
//
//	public double getMinX() {
//		return getLatLonEdge("minx");
//	}
//	public double getMaxX() {
//		return getLatLonEdge("maxx");
//	}
//	public double getMinY() {
//		return getLatLonEdge("miny");
//	}
//	public double getMaxY() {
//		return getLatLonEdge("maxy");
//	}
}

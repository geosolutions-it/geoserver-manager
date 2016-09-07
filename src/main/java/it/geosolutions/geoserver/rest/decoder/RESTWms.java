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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

/**
 * Parse <TT>WMS</TT>s returned as XML REST objects.
 *
 * <P>This is the XML REST representation:
 * <PRE>
 *{@code
<wmsLayer>
	<name>comunilazio</name>
	<nativeName>lait:comunilazio</nativeName>
	<namespace>
		<name>arit</name>
		<atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://172.27.30.25:8080/geoserver/rest/namespaces/arit.xml" type="application/xml"/>
	</namespace>
	<title>comunilazio</title>
	<description/>
	<keywords>
		<string>features</string>
		<string>comunilazio</string>
	</keywords>
	<nativeCRS>
		GEOGCS["WGS 84", DATUM["World Geodetic System 1984", SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]], AUTHORITY["EPSG","6326"]], PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]], UNIT["degree", 0.017453292519943295], AXIS["Geodetic longitude", EAST], AXIS["Geodetic latitude", NORTH], AUTHORITY["EPSG","4326"]]
	</nativeCRS>
	<srs>EPSG:4326</srs>
	<nativeBoundingBox>
		<minx>11.4482128</minx>
		<maxx>14.0288013</maxx>
		<miny>40.7848334</miny>
		<maxy>42.8396541</maxy>
		<crs>EPSG:4326</crs>
	</nativeBoundingBox>
	<latLonBoundingBox>
		<minx>11.4482128</minx>
		<maxx>14.0288013</maxx>
		<miny>40.7848334</miny>
		<maxy>42.8396541</maxy>
		<crs>
			GEOGCS["WGS84(DD)", DATUM["WGS84", SPHEROID["WGS84", 6378137.0, 298.257223563]], PRIMEM["Greenwich", 0.0], UNIT["degree", 0.017453292519943295], AXIS["Geodetic longitude", EAST], AXIS["Geodetic latitude", NORTH]]
		</crs>
	</latLonBoundingBox>
	<projectionPolicy>FORCE_DECLARED</projectionPolicy>
	<enabled>true</enabled>
	<metadata>
		<entry key="cachingEnabled">false</entry>
	</metadata>
	<store class="wmsStore">
		<name>regione</name>
		<atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://172.27.30.25:8080/geoserver/rest/workspaces/arit/wmsstores/regione.xml" type="application/xml"/>
	</store>
</wmsLayer>
 * }</PRE>
 *
 * @author cip
 */
public class RESTWms extends RESTResource {


    public static RESTWms build(String response) {
        Element elem = JDOMBuilder.buildElement(response);
        return elem == null? null : new RESTWms(elem);
	}

	public RESTWms(Element resource) {
		super(resource);
	}

	public RESTWms(RESTResource resource) {
		super(resource.rootElem);
	}

//	public String getName() {
//		return rootElem.getChildText("name");
//	}

//	public String getNativeName() {
//		return rootElem.getChildText("nativeName");
//	}



//	public String getNameSpace() {
//		return rootElem.getChild("namespace").getChildText("name");
//	}
//
//	public String getTitle() {
//		return rootElem.getChildText("title");
//	}

	public String getNativeCRS() {
		return rootElem.getChildText("nativeCRS");
	}

	public String getSRS() {
		return rootElem.getChildText("srs");
	}
	
	public RESTMetadataList getMetadataList() {
        return new RESTMetadataList(rootElem.getChild("metadata"));
    }
	
	public List<RESTDimensionInfo> getDimensionInfo() {
        List<RESTDimensionInfo> listDim = new ArrayList<RESTDimensionInfo>();
        for (RESTMetadataList.RESTMetadataElement el : getMetadataList()){
            if(el.getKey().equals(RESTDimensionInfo.TIME) || el.getKey().equals(RESTDimensionInfo.ELEVATION)){
                listDim.add(new RESTDimensionInfo(el.getMetadataElem()));
            }
        }
        return listDim;
    }
		
    /**
     * Retrieves the list of parameters for this wms.
     * 
     * @return a {@link Map} where the key is the name for the parameter and the value is the value for the parameter.
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getParametersList() {
        Map<String, String> paramsList = new HashMap<String, String>();

        final Element paramsRoot = rootElem.getChild("parameters");
        if (paramsRoot != null) {
            final List<Element> params = paramsRoot.getChildren();
            if (params != null) {
                for (Element param : params) {
                    final List<Element> values = param.getChildren();
                    assert values.size()==2;
                    paramsList.put(values.get(0).getValue(), values.get(1).getValue()); // save key and value
                }
            }
        }
        return paramsList;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RESTWms [");
        if (getNativeCRS() != null) {
            builder.append("getNativeCRS()=");
            builder.append(getNativeCRS());
            builder.append(", ");
        }
        if (getSRS() != null) {
            builder.append("getSRS()=");
            builder.append(getSRS());
            builder.append(", ");
        }
        if (getMetadataList() != null) {
            builder.append("getMetadataList()=");
            builder.append(getMetadataList());
            builder.append(", ");
        }
        if (getDimensionInfo() != null) {
            builder.append("getDimensionInfo()=");
            builder.append(getDimensionInfo());
            builder.append(", ");
        }
        if (getParametersList() != null) {
            builder.append("getParametersList()=");
            builder.append(getParametersList());
            builder.append(", ");
        }
        if (getName() != null) {
            builder.append("getName()=");
            builder.append(getName());
            builder.append(", ");
        }
        if (getTitle() != null) {
            builder.append("getTitle()=");
            builder.append(getTitle());
            builder.append(", ");
        }
        if (getNativeName() != null) {
            builder.append("getNativeName()=");
            builder.append(getNativeName());
            builder.append(", ");
        }
        if (getAbstract() != null) {
            builder.append("getAbstract()=");
            builder.append(getAbstract());
            builder.append(", ");
        }
        if (getNameSpace() != null) {
            builder.append("getNameSpace()=");
            builder.append(getNameSpace());
            builder.append(", ");
        }
        if (getStoreName() != null) {
            builder.append("getStoreName()=");
            builder.append(getStoreName());
            builder.append(", ");
        }
        if (getStoreType() != null) {
            builder.append("getStoreType()=");
            builder.append(getStoreType());
            builder.append(", ");
        }
        if (getStoreUrl() != null) {
            builder.append("getStoreUrl()=");
            builder.append(getStoreUrl());
            builder.append(", ");
        }
        if (getCRS() != null) {
            builder.append("getCRS()=");
            builder.append(getCRS());
            builder.append(", ");
        }
        builder.append("getMinX()=");
        builder.append(getMinX());
        builder.append(", getMaxX()=");
        builder.append(getMaxX());
        builder.append(", getMinY()=");
        builder.append(getMinY());
        builder.append(", getMaxY()=");
        builder.append(getMaxY());
        builder.append(", ");
        if (getAttributeList() != null) {
            builder.append("getAttributeList()=");
            builder.append(getAttributeList());
            builder.append(", ");
        }
        if (getEncodedAttributeList() != null) {
            builder.append("getEncodedAttributeList()=");
            builder.append(getEncodedAttributeList());
            builder.append(", ");
        }
        if (getEncodedMetadataLinkInfoList() != null) {
            builder.append("getEncodedMetadataLinkInfoList()=");
            builder.append(getEncodedMetadataLinkInfoList());
        }
        builder.append("]");
        return builder.toString();
    }

//	public String getStoreName() {
//		return rootElem.getChild("store").getChildText("name");
//	}
//
//	public String getStoreType() {
//		return rootElem.getChild("store").getAttributeValue("class");
//	}


//    public String getStoreUrl() {
//		Element store = rootElem.getChild("store");
//        Element atom = store.getChild("link", Namespace.getNamespace("atom", "http://www.w3.org/2005/Atom"));
//        return atom.getAttributeValue("href");
//    }

//	public String getBBCRS() {
//		Element elBBox = rootElem.getChild("latLonBoundingBox");
//		return elBBox.getChildText("crs");
//	}
//
//	protected double getLatLonEdge(String edge) {
//		Element elBBox = rootElem.getChild("latLonBoundingBox");
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

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
import it.geosolutions.geoserver.rest.encoder.feature.FeatureTypeAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

/**
 * Parse <TT>Coverage</TT>s returned as XML REST objects.
 *
 * <P>This is the XML REST representation:
 * <PRE>
 *{@code
<coverage>
  <name>sfdem</name>
  <nativeName>sfdem</nativeName>
  <namespace>
    <name>sf</name>
    <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/namespaces/sf.xml" type="application/xml"/>
  </namespace>
  <title>sfdem is a Tagged Image File Format with Geographic information</title>
  <description>Generated from sfdem</description>
  <keywords>
    <string>WCS</string>
    <string>sfdem</string>
    <string>sfdem</string>
  </keywords>
  <nativeCRS class="projected">PROJCS[&quot;NAD27 / UTM zone 13N&quot;,
  GEOGCS[&quot;NAD27&quot;,
    DATUM[&quot;North American Datum 1927&quot;,
      SPHEROID[&quot;Clarke 1866&quot;, 6378206.4, 294.9786982138982, AUTHORITY[&quot;EPSG&quot;,&quot;7008&quot;]],
      TOWGS84[-4.2, 135.4, 181.9, 0.0, 0.0, 0.0, 0.0],
      AUTHORITY[&quot;EPSG&quot;,&quot;6267&quot;]],
    PRIMEM[&quot;Greenwich&quot;, 0.0, AUTHORITY[&quot;EPSG&quot;,&quot;8901&quot;]],
    UNIT[&quot;degree&quot;, 0.017453292519943295],
    AXIS[&quot;Geodetic longitude&quot;, EAST],
    AXIS[&quot;Geodetic latitude&quot;, NORTH],
    AUTHORITY[&quot;EPSG&quot;,&quot;4267&quot;]],
  PROJECTION[&quot;Transverse_Mercator&quot;],
  PARAMETER[&quot;central_meridian&quot;, -105.0],
  PARAMETER[&quot;latitude_of_origin&quot;, 0.0],
  PARAMETER[&quot;scale_factor&quot;, 0.9996],
  PARAMETER[&quot;false_easting&quot;, 500000.0],
  PARAMETER[&quot;false_northing&quot;, 0.0],
  UNIT[&quot;m&quot;, 1.0],
  AXIS[&quot;Easting&quot;, EAST],
  AXIS[&quot;Northing&quot;, NORTH],
  AUTHORITY[&quot;EPSG&quot;,&quot;26713&quot;]]</nativeCRS>
  <srs>EPSG:26713</srs>
  <nativeBoundingBox>
    <minx>589980.0</minx>
    <maxx>609000.0</maxx>
    <miny>4913700.0</miny>
    <maxy>4928010.0</maxy>
    <crs class="projected">EPSG:26713</crs>
  </nativeBoundingBox>
  <latLonBoundingBox>
    <minx>-103.87108701853181</minx>
    <maxx>-103.62940739432703</maxx>
    <miny>44.370187074132616</miny>
    <maxy>44.5016011535299</maxy>
    <crs>EPSG:4326</crs>
  </latLonBoundingBox>
  <enabled>true</enabled>
  <metadata>
    <entry key="dirName">sfdem_sfdem</entry>
  </metadata>
  <store class="coverageStore">
    <name>sfdem</name>
    <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/workspaces/sf/coveragestores/sfdem.xml" type="application/xml"/>
  </store>
  <nativeFormat>GeoTIFF</nativeFormat>
  <grid dimension="2">
    <range>
      <low>0 0</low>
      <high>634 477</high>
    </range>
    <transform>
      <scaleX>30.0</scaleX>
      <scaleY>-30.0</scaleY>
      <shearX>0.0</shearX>
      <shearY>0.0</shearY>
      <translateX>589995.0</translateX>
      <translateY>4927995.0</translateY>
    </transform>
    <crs>EPSG:26713</crs>
  </grid>
  <supportedFormats>
    <string>ARCGRID</string>
    <string>IMAGEMOSAIC</string>
    <string>GTOPO30</string>
    <string>GEOTIFF</string>
    <string>GIF</string>
    <string>PNG</string>
    <string>JPEG</string>
    <string>TIFF</string>
  </supportedFormats>
  <interpolationMethods>
    <string>nearest neighbor</string>
    <string>bilinear</string>
    <string>bicubic</string>
  </interpolationMethods>
  <defaultInterpolationMethod>nearest neighbor</defaultInterpolationMethod>
  <dimensions>
    <coverageDimension>
      <name>GRAY_INDEX</name>
      <description>GridSampleDimension[-9.999999933815813E36,-9.999999933815813E36]</description>
      <range>
        <min>-9.999999933815813E36</min>
        <max>-9.999999933815813E36</max>
      </range>
    </coverageDimension>
  </dimensions>
  <requestSRS>
    <string>EPSG:26713</string>
  </requestSRS>
  <responseSRS>
    <string>EPSG:26713</string>
  </responseSRS>
</coverage>
 * }</PRE>
 *
 * @author etj
 */
public class RESTCoverage extends RESTResource {


    public static RESTCoverage build(String response) {
        Element elem = JDOMBuilder.buildElement(response);
        return elem == null? null : new RESTCoverage(elem);
	}

	public RESTCoverage(Element resource) {
		super(resource);
	}

	public RESTCoverage(RESTResource resource) {
		super(resource.rootElem);
	}

//	public String getName() {
//		return rootElem.getChildText("name");
//	}

//	public String getNativeName() {
//		return rootElem.getChildText("nativeName");
//	}

	public String getNativeFormat() {
		return rootElem.getChildText("nativeFormat");
	}

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
     * Retrieves the list of parameters for this coverage.
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
        builder.append("RESTCoverage [");
        if (getNativeFormat() != null) {
            builder.append("getNativeFormat()=");
            builder.append(getNativeFormat());
            builder.append(", ");
        }
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

    /**
     * Get the URL to retrieve the featuretype.
     * <PRE> {@code
     * <store class="coverageStore">
     *      <name>sfdem</name>
     *      <atom:link xmlns:atom="http://www.w3.org/2005/Atom"
     *          rel="alternate"
     *          href="http://localhost:8080/geoserver/rest/workspaces/sf/coveragestores/sfdem.xml"
     *          type="application/xml"/>
     * </store>
     * }</CODE>
     */
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

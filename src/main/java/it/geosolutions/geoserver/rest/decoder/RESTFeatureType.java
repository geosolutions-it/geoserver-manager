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
import it.geosolutions.geoserver.rest.decoder.utils.JDOMListIterator;
import it.geosolutions.geoserver.rest.encoder.feature.FeatureTypeAttribute;
import it.geosolutions.geoserver.rest.encoder.feature.GSAttributeEncoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

/**
 * Parse <TT>FeatureType</TT>s returned as XML REST objects.
 *
 * <P>This is the XML REST representation:
 * <PRE>
 * {@code
<featureType>
  <name>tasmania_cities</name>
  <nativeName>tasmania_cities</nativeName>
  <namespace>
    <name>topp</name>
    <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/namespaces/topp.xml" type="application/xml"/>
  </namespace>
  <title>Tasmania cities</title>
  <abstract>Cities in Tasmania (actually, just the capital)</abstract>
  <keywords>
    <string>cities</string>
    <string>Tasmania</string>
  </keywords>
  <nativeCRS>GEOGCS[&quot;GCS_WGS_1984&quot;,
  DATUM[&quot;WGS_1984&quot;,
    SPHEROID[&quot;WGS_1984&quot;, 6378137.0, 298.257223563]],
  PRIMEM[&quot;Greenwich&quot;, 0.0],
  UNIT[&quot;degree&quot;, 0.017453292519943295],
  AXIS[&quot;Longitude&quot;, EAST],
  AXIS[&quot;Latitude&quot;, NORTH]]</nativeCRS>
  <srs>EPSG:4326</srs>
  <nativeBoundingBox>
    <minx>147.2910004483</minx>
    <maxx>147.2910004483</maxx>
    <miny>-42.851001816890005</miny>
    <maxy>-42.851001816890005</maxy>
    <crs>EPSG:4326</crs>
  </nativeBoundingBox>
  <latLonBoundingBox>
    <minx>145.19754</minx>
    <maxx>148.27298000000002</maxx>
    <miny>-43.423512</miny>
    <maxy>-40.852802</maxy>
    <crs>EPSG:4326</crs>
  </latLonBoundingBox>
  <projectionPolicy>FORCE_DECLARED</projectionPolicy>
  <enabled>true</enabled>
  <metadata>
    <entry key="cacheAgeMax">3600</entry>
    <entry key="indexingEnabled">false</entry>
    <entry key="kml.regionateFeatureLimit">10</entry>
    <entry key="cachingEnabled">true</entry>
    <entry key="dirName">tasmania_cities</entry>
  </metadata>
  <store class="dataStore">
    <name>taz_shapes</name>
    <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/workspaces/topp/datastores/taz_shapes.xml" type="application/xml"/>
  </store>
  <attributes>
    <attribute>
      <name>the_geom</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>false</nillable>
    </attribute>
    <attribute>
      <name>CITY_NAME</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>false</nillable>
    </attribute>
    <attribute>
      <name>ADMIN_NAME</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>false</nillable>
    </attribute>
    <attribute>
      <name>CNTRY_NAME</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>false</nillable>
    </attribute>
    <attribute>
      <name>STATUS</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>false</nillable>
    </attribute>
    <attribute>
      <name>POP_CLASS</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>false</nillable>
    </attribute>
  </attributes>
  <maxFeatures>0</maxFeatures>
  <numDecimals>0</numDecimals>
</featureType>
 * }</PRE>
 * @author etj
 */
public class RESTFeatureType extends RESTResource {

    public static class Attribute {
        private String name;
        private String binding;

        public String getBinding() {
            return binding;
        }

        public void setBinding(String binding) {
            this.binding = binding;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static RESTFeatureType build(String response) {
        Element elem = JDOMBuilder.buildElement(response);
        return elem == null? null : new RESTFeatureType(elem);
	}

	public RESTFeatureType(Element resource) {
		super(resource);
	}

	public RESTFeatureType(RESTResource resource) {
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

//	public String getStoreName() {
//		return rootElem.getChild("store").getChildText("name");
//	}
//
//	public String getStoreType() {
//		return rootElem.getChild("store").getAttributeValue("class");
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
//    public String getStoreUrl() {
//		Element store = rootElem.getChild("store");
//        Element atom = store.getChild("link", Namespace.getNamespace("atom", "http://www.w3.org/2005/Atom"));
//        return atom.getAttributeValue("href");
//    }

//	public String getCRS() {
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

    public Iterable<Attribute> getAttributes() {

        return new Iterable<Attribute>() {
            public Iterator<Attribute> iterator() {
                return attributesIterator();
            }
        };
    }

    public Iterator<Attribute> attributesIterator() {
        Element attrs = rootElem.getChild("attributes");
        return new JDOMListIterator<Attribute>(attrs.getChildren()) {
            @Override
            public Attribute transform(Element listItem) {
                Attribute ret = new Attribute();
                ret.setName(listItem.getChildText("name"));
                ret.setBinding(listItem.getChildText("binding"));
                return ret;
            }
        };
    }
    
    /**
     * @return
     */
    public List<Map<FeatureTypeAttribute, String>> getAttributeList() {
        List<Map<FeatureTypeAttribute, String>> attrsList = null;

        final Element attrsRoot = rootElem.getChild("attributes");
        if(attrsRoot!=null){
            final List<Element> attrs = attrsRoot.getChildren();
            if (attrs != null) {
                attrsList = new ArrayList<Map<FeatureTypeAttribute, String>>(attrs.size());
                for (Element attr : attrs) {
                    Map<FeatureTypeAttribute, String> attrsMap = new HashMap<FeatureTypeAttribute, String>();
                    attrsList.add(attrsMap);
                    for (FeatureTypeAttribute at : FeatureTypeAttribute.values()) {
                        String key = at.toString();
                        attrsMap.put(at, attr.getChildText(key));
                    }
                }
            }
        }
        return attrsList;
    }

    public List<GSAttributeEncoder> getEncodedAttributeList() {
        List<GSAttributeEncoder> attrsList = null;

        final Element attrsRoot = rootElem.getChild("attributes");
        if(attrsRoot!=null){
            final List<Element> attrs = attrsRoot.getChildren();
            if (attrs != null) {
                attrsList = new ArrayList<GSAttributeEncoder>(attrs.size());
                for (Element attr : attrs) {
                    final GSAttributeEncoder attrEnc = new GSAttributeEncoder();
                    for (FeatureTypeAttribute at : FeatureTypeAttribute.values()) {
                        String key = at.toString();
                        attrEnc.setAttribute(at, attr.getChildText(key));
                    }
                    attrsList.add(attrEnc);
                }
    
            }
        }
        return attrsList;
    }
}

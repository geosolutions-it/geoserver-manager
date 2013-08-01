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

/**
 * Parse <TT>LayerGroup</TT>s returned as XML REST objects.
 *
 * <P>This is the XML REST representation:
 * <PRE>
 * {@code
<layerGroup>
  <name>tasmania</name>
  <layers>
    <layer>
      <name>tasmania_state_boundaries</name>
      <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/layers/tasmania_state_boundaries.xml" type="application/xml"/>
    </layer>
    <layer>
      <name>tasmania_water_bodies</name>
      <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/layers/tasmania_water_bodies.xml" type="application/xml"/>
    </layer>
    <layer>
      <name>tasmania_roads</name>
      <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/layers/tasmania_roads.xml" type="application/xml"/>
    </layer>
    <layer>
      <name>tasmania_cities</name>
      <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/layers/tasmania_cities.xml" type="application/xml"/>
    </layer>
  </layers>
  <styles>
    <style/>
    <style/>
    <style/>
    <style/>
  </styles>
  <bounds>
    <minx>143.83482400000003</minx>
    <maxx>148.47914100000003</maxx>
    <miny>-43.648056</miny>
    <maxy>-39.573891</maxy>
    <crs>EPSG:4326</crs>
  </bounds>
  <metadata>
    <entry key="rawStyleList"></entry>
  </metadata>
</layerGroup>
 * }</PRE>
 * @author etj
 */

public class RESTLayerGroup {
	private final Element rootElem;

    public static RESTLayerGroup build(String response) {
        Element elem = JDOMBuilder.buildElement(response);
        return elem == null? null : new RESTLayerGroup(elem);
	}

	public RESTLayerGroup(Element layerElem) {
		this.rootElem = layerElem;
	}

	public String getName() {
		return rootElem.getChildText("name");
	}

	public String getWorkspace() {
            Element rootLayer = rootElem.getChild("workspace");
            if (rootLayer != null) {
                return rootLayer.getChildText("name");
            } else {
                return null;
            }	    	    
	}
	
	public String getMode() {
	    return rootElem.getChildText("mode");
	}
	
	public String getTitle() {
	    return rootElem.getChildText("title");
	}
	
	public String getAbstract() {
	    return rootElem.getChildText("abstractTxt");
	}
	
	public String getRootLayer() {
	    Element rootLayer = rootElem.getChild("rootLayer");
	    if (rootLayer != null) {
	        return rootLayer.getChildText("name");
	    } else {
	        return null;
	    }
	}
	
	public RESTLayerList getLayerList() {
	    if (rootElem.getChild("layers") != null) {
                return new RESTLayerList(rootElem.getChild("layers"));
	    } else {
	        return null;
	    }
	}

        public RESTPublishedList getPublishedList() {
            if (rootElem.getChild("publishables") != null) {
                return new RESTPublishedList(rootElem.getChild("publishables"));
            } else {
                return null;
            }
        }	
	
	public String getCRS() {
		Element bounds = rootElem.getChild("bounds");
		return bounds.getChildText("crs");
	}

	protected double getBound(String edge) {
		Element bounds = rootElem.getChild("bounds");
		return Double.parseDouble(bounds.getChildText(edge));
	}

	public double getMinX() {
		return getBound("minx");
	}
	public double getMaxX() {
		return getBound("maxx");
	}
	public double getMinY() {
		return getBound("miny");
	}
	public double getMaxY() {
		return getBound("maxy");
	}
}

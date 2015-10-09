/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2015 GeoSolutions S.A.S.
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

package it.geosolutions.geoserver.rest.decoder.gwc;

import it.geosolutions.geoserver.rest.Util;
import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.jdom.Element;

/**
 * Parse <TT>FormatModifier</TT>s returned as XML REST objects.
 *
 * <P>This is the XML Schema representation:
 * <PRE>
 * {@code
    <xs:complexType name="GridSubset">
        <xs:sequence>
          <xs:element name="gridSetName" type="xs:string">
          </xs:element>
          <xs:element minOccurs="0" name="extent" type="gwc:Bounds">
          </xs:element>
          <xs:element minOccurs="0" name="zoomStart" type="xs:integer">
          </xs:element>
          <xs:element minOccurs="0" name="zoomStop" type="xs:integer">
          </xs:element>
          <xs:element minOccurs="0" name="minCachedLevel" type="xs:integer">
          </xs:element>
          <xs:element minOccurs="0" name="maxCachedLevel" type="xs:integer">
          </xs:element>
        </xs:sequence>
    </xs:complexType>
 * }</PRE>
 * @author Nazzareno Sileno
 */
public class GWCGridSubset {
    
    private final Element gridSubsetElem;

    public static GWCGridSubset build(String response) {
        if (response == null) {
            return null;
        }

        Element pb = JDOMBuilder.buildElement(response);
        if (pb != null) {
            return new GWCGridSubset(pb);
        } else {
            return null;
        }
    }

    public GWCGridSubset(Element formatModifierElem) {
        this.gridSubsetElem = formatModifierElem;
    }
    
    public String getGridSetName() {
        return gridSubsetElem.getChildText("gridSetName");
    }

    public GWCBounds getExtent() {
        GWCBounds bounds = null;
        final Element extent = gridSubsetElem.getChild("extent");
        if(extent != null){
            bounds = new GWCBounds(extent);
        }
        return bounds;
    }

    public Integer getZoomStart() {
        return Util.getIntValueFromText(gridSubsetElem.getChildText("zoomStart"));
    }

    public Integer getZoomStop() {
        return Util.getIntValueFromText(gridSubsetElem.getChildText("zoomStop"));
    }

    public Integer getMinCachedLevel() {
        return Util.getIntValueFromText(gridSubsetElem.getChildText("minCachedLevel"));
    }

    public Integer getMaxCachedLevel() {
        return Util.getIntValueFromText(gridSubsetElem.getChildText("maxCachedLevel"));
    }

}

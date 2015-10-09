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
 * Parse <TT>GeoRssFeed</TT>s returned as XML REST objects.
 *
 * <P>This is the XML Schema representation:
 * <PRE>
 * {@code
    <xs:complexType name="GeoRssFeed">
      <xs:sequence>
        <xs:element minOccurs="1" name="feedUrl" type="xs:string">
        </xs:element>
        <xs:element minOccurs="0" name="gridSetId" type="xs:string">
        </xs:element>
        <xs:element minOccurs="1" name="pollInterval" type="xs:integer">
        </xs:element>
        <xs:element minOccurs="0" name="operation" type="xs:string">
        </xs:element>
        <xs:element minOccurs="0" name="format" type="xs:string">
        </xs:element>
        <xs:element minOccurs="0" name="seedingThreads" type="xs:integer">
        </xs:element>
        <xs:element minOccurs="1" name="maxMaskLevel" type="xs:integer">
        </xs:element>
      </xs:sequence>
    </xs:complexType>
 * }</PRE>
 * @author Nazzareno Sileno
 */
public class GWCGeoRssFeed {
    
    private final Element geoRssFeedElem;

    public static GWCGeoRssFeed build(String response) {
        if (response == null) {
            return null;
        }

        Element pb = JDOMBuilder.buildElement(response);
        if (pb != null) {
            return new GWCGeoRssFeed(pb);
        } else {
            return null;
        }
    }

    public GWCGeoRssFeed(Element geoRssFeedElem) {
        this.geoRssFeedElem = geoRssFeedElem;
    }
    
    public String getFeedUrl() {
        return geoRssFeedElem.getChildText("feedUrl");
    }

    public String getGridSetId() {
        return geoRssFeedElem.getChildText("gridSetId");
    }
    
    public Integer getPollInterval() {
        return Util.getIntValueFromText(geoRssFeedElem.getChildText("pollInterval"));
    }

    public String getOperation() {
        return geoRssFeedElem.getChildText("operation");
    }

    public String getFormat() {
        return geoRssFeedElem.getChildText("format");
    }

    public Integer getSeedingThreads() {
        return Util.getIntValueFromText(geoRssFeedElem.getChildText("seedingThreads"));
    }

    public Integer getMaxMaskLevel() {
        return Util.getIntValueFromText(geoRssFeedElem.getChildText("maxMaskLevel"));
    }
    
}

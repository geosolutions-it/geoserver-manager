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

import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import org.jdom.Element;

/**
 * Parse <TT>RequestFilters</TT>s returned as XML REST objects.
 *
 * <P>This is the XML Schema representation:
 * <PRE>
 * {@code
    <xs:complexType name="RequestFilters">
      <xs:choice maxOccurs="unbounded" minOccurs="0">
        <xs:element name="circularExtentFilter" type="gwc:circularExtentFilter">
        </xs:element>
        <xs:element name="wmsRasterFilter" type="gwc:WmsRasterFilter">
        </xs:element>
        <xs:element name="fileRasterFilter" type="gwc:FileRasterFilter">
        </xs:element>
      </xs:choice>
    </xs:complexType>
 * }</PRE>
 * @author Nazzareno Sileno
 */
public class GWCRequestFilters {
    
    private final Element requestFiltersElem;

    public static GWCRequestFilters build(String response) {
        if (response == null) {
            return null;
        }

        Element pb = JDOMBuilder.buildElement(response);
        if (pb != null) {
            return new GWCRequestFilters(pb);
        } else {
            return null;
        }
    }

    public GWCRequestFilters(Element requestFiltersElem) {
        this.requestFiltersElem = requestFiltersElem;
    }

    public String getCircularExtentFilter() {
        return this.requestFiltersElem.getChildText("circularExtentFilter");
    }
    
    public GWCWmsRasterFilter getWMSRasterFilter(){
        GWCWmsRasterFilter wmsRasterFilter = null;
        final Element wmsRasterFilterRoot = requestFiltersElem.getChild("wmsRasterFilter");
        if(wmsRasterFilterRoot != null){
            wmsRasterFilter = new GWCWmsRasterFilter(wmsRasterFilterRoot);
        }
        return wmsRasterFilter;
    }

    public GWCFileRasterFilter getFileRasterFilter(){
        GWCFileRasterFilter fileRasterFilter = null;
        final Element fileRasterFilterRoot = requestFiltersElem.getChild("fileRasterFilter");
        if(fileRasterFilterRoot != null){
            fileRasterFilter = new GWCFileRasterFilter(fileRasterFilterRoot);
        }
        return fileRasterFilter;
    }
    
    public boolean isFileRasterFilter(){
        return this.getFileRasterFilter() != null;
    }
    public boolean isWMSRasterFilter(){
        return this.getWMSRasterFilter() != null;
    }
    public boolean isCircularExtentFilter(){
        return this.getCircularExtentFilter()!= null;
    }
    
}

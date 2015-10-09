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
import java.util.List;
import org.jdom.Element;

/**
 * Parse <TT>LayerMetaInformation</TT>s returned as XML REST objects.
 *
 * <P>This is the XML Schema representation:
 * <PRE>
 * {@code
    <xs:complexType name="LayerMetaInformation">
        <xs:sequence>
          <xs:element minOccurs="1" name="title" type="xs:string">
          </xs:element>
          <xs:element minOccurs="0" name="description" type="xs:string">
          </xs:element>
          <xs:element maxOccurs="1" minOccurs="0" name="keywords" type="gwc:KeywordsType">
          </xs:element>
        </xs:sequence>
    </xs:complexType>
 * }</PRE>
 * @author Nazzareno Sileno
 */
public class GWCLayerMetaInformation {
    
    private final Element layerMetaInformationElem;

    public static GWCLayerMetaInformation build(String response) {
        if (response == null) {
            return null;
        }

        Element pb = JDOMBuilder.buildElement(response);
        if (pb != null) {
            return new GWCLayerMetaInformation(pb);
        } else {
            return null;
        }
    }

    public GWCLayerMetaInformation(Element layerMetaInformationElem) {
        this.layerMetaInformationElem = layerMetaInformationElem;
    }
    
    public String getTitle() {
        return layerMetaInformationElem.getChildText("title");
    }

    public String getDescription() {
        return layerMetaInformationElem.getChildText("description");
    }

    public List<String> getKeywordsType() {
        List<String> keywordsTypeList = null;
        final Element keywordsRoot = this.layerMetaInformationElem.getChild("keywords");
        if(keywordsRoot != null){
            keywordsTypeList = Util.getElementsChildrenStringContent(keywordsRoot);
        }
        return keywordsTypeList;
    }
    
}

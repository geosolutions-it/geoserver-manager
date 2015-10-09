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
 * Parse <TT>ParameterFilters</TT>s returned as XML REST objects.
 *
 * <P>This is the XML Schema representation:
 * <PRE>
 * {@code
    <xs:complexType name="ParameterFilters">
      <xs:sequence maxOccurs="unbounded" minOccurs="0">
        <xs:choice>
          <xs:element name="regexParameterFilter" type="gwc:RegexParameterFilter">
          </xs:element>
          <xs:element name="floatParameterFilter" type="gwc:FloatParameterFilter">
          </xs:element>
          <xs:element name="stringParameterFilter" type="gwc:StringParameterFilter">
          </xs:element>
        </xs:choice>
      </xs:sequence>
    </xs:complexType>
 * }</PRE>
 * @author Nazzareno Sileno
 */
public class GWCParameterFilters {
    
    private final Element parameterFiltersElem;

    public static GWCParameterFilters build(String response) {
        if (response == null) {
            return null;
        }

        Element pb = JDOMBuilder.buildElement(response);
        if (pb != null) {
            return new GWCParameterFilters(pb);
        } else {
            return null;
        }
    }

    public GWCParameterFilters(Element parameterFiltersElem) {
        this.parameterFiltersElem = parameterFiltersElem;
    }

    public GWCRegexParameterFilter getRegexParameterFilter(){
        GWCRegexParameterFilter regexParameterFilter = null;
        final Element regexParameterFilterRoot = parameterFiltersElem.getChild("regexParameterFilter");
        if(regexParameterFilterRoot != null){
            regexParameterFilter = new GWCRegexParameterFilter(regexParameterFilterRoot);
        }
        return regexParameterFilter;
    }

    public GWCFloatParameterFilter getFloatParameterFilter(){
        GWCFloatParameterFilter floatParameterFilter = null;
        final Element floatParameterFilterRoot = parameterFiltersElem.getChild("floatParameterFilter");
        if(floatParameterFilterRoot != null){
            floatParameterFilter = new GWCFloatParameterFilter(floatParameterFilterRoot);
        }
        return floatParameterFilter;
    }

    public GWCStringParameterFilter getStringParameterFilter(){
        GWCStringParameterFilter stringParameterFilter = null;
        final Element stringParameterFilterRoot = parameterFiltersElem.getChild("stringParameterFilter");
        if(stringParameterFilterRoot != null){
            stringParameterFilter = new GWCStringParameterFilter(stringParameterFilterRoot);
        }
        return stringParameterFilter;
    }
    
    public boolean isRegexParameterFilter(){
        return this.getRegexParameterFilter() != null;
    }
    public boolean isFloatParameterFilter(){
        return this.getFloatParameterFilter() != null;
    }
    public boolean isStringParameterFilter(){
        return this.getStringParameterFilter()!= null;
    }
    
}

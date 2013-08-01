/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2012 GeoSolutions S.A.S.
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
package it.geosolutions.geoserver.rest.encoder.feature;

import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;

import java.util.Map;
import java.util.Map.Entry;

import org.jdom.Element;
import org.jdom.filter.Filter;

/**
 * 
 * @author carlo cancellieri - GeoSolutions
 *
 */
public class GSAttributeEncoder extends PropertyXMLEncoder {
    
    public static class filterByName implements Filter {
        
        final private String key;
        
        public filterByName(String keyword){
            this.key=keyword;
        }
        
        private static final long serialVersionUID = 1L;

        public boolean matches(Object obj) {
                Element el=((Element) obj).getChild(FeatureTypeAttribute.name.toString());
                if (el!=null && el.getTextTrim().equals(key)) {
                        return true;
                }
                return false;
        }
    }
    
    public static Filter getFilterByName(String name){
        return new filterByName(name);
    }

    public GSAttributeEncoder() {
        super("attribute");
    }
    
    public void setup(Map<FeatureTypeAttribute, String> attributes){
        for (Entry<FeatureTypeAttribute,String> attr:attributes.entrySet()){
            set(attr.getKey().toString(),attr.getValue());
        }
    }
    
    public void setAttribute(FeatureTypeAttribute type, String value){
        set(type.toString(),value);
    }
    
    public void delAttribute(FeatureTypeAttribute type){
        ElementUtils.remove(this.getRoot(), get(type.toString()));
    }
    
    public String getAttribute(FeatureTypeAttribute type){
        Element el = get(type.toString());
        if (el!=null)
            return el.getTextTrim();
        else
            return null;
    }
}


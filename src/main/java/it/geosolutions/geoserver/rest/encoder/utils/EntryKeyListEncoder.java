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

package it.geosolutions.geoserver.rest.encoder.utils;

import java.util.HashMap;
import java.util.Map;
import org.jdom.Element;

/**
 * Encodes lists of entries with key attribute.
 * <br/>e.g.:
 * <PRE>
 * {@code 
 *  <listName>
 *   <entry key="k1">val1</entry>
 *   <entry key="k2">val2</entry>
 *   <entry key="k3">val3</entry>
 * </listName>}
 * <PRE>
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class EntryKeyListEncoder {

    private Map<String, String> metadata = new HashMap<String, String>();
    private final String listName;

    public EntryKeyListEncoder(String listName) {
        this.listName = listName;
    }
    
    public void add(String key, String value) {
        metadata.put(key, value);
    }

    public void attachList(Element e) {
        
        if( ! metadata.isEmpty() ) {
            Element md = new Element(listName);
            for (Map.Entry<String, String> entry : metadata.entrySet()) {
                Element entryeElem = new Element("entry")
                                    .setAttribute("key", entry.getKey())
                                    .setText(entry.getValue());
                md.addContent(entryeElem);
            }
            e.addContent(md);
        }
    }
}

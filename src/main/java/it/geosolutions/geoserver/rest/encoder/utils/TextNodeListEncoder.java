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

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;

/**
 * Encodes lists of simple text nodes.
 * <br/>e.g.:
 * <PRE>
 * {@code 
 *  <parentnodename>
 *   <nodename1>nodetext1</nodename1>
 *   <nodename2>nodetext2</nodename2>
 *   <nodename3>nodetext3</nodename3>
 * </parentnodename>}
 * <PRE>
 * 
 * @author ETj (etj at geo-solutions.it)
 */
public class TextNodeListEncoder {

    private List<Pair> list = new ArrayList<Pair>();
    private final String listName;

    public TextNodeListEncoder(String listName) {
        this.listName = listName;
    }   
                    
    public void add(String nodename, String nodetext) {
        list.add(new Pair(nodename, nodetext));
    }

    public void attachList(Element e) {
        
        if( ! list.isEmpty() ) {
            Element elist = new Element(listName);
            for (Pair pair : list) {
                elist.addContent(pair.v1).setText(pair.v2);
            }
            e.addContent(elist);
        }
    }
    
    class Pair {
        String v1;
        String v2;

        public Pair(String v1, String v2) {
            this.v1 = v1;
            this.v2 = v2;
        }
    }
}

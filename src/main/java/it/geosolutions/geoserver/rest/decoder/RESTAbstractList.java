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

import it.geosolutions.geoserver.rest.decoder.utils.JDOMListIterator;

import it.geosolutions.geoserver.rest.decoder.utils.NameLinkElem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

/**
 * Parses list of summary data.
 *
 * <P>This is the XML REST representation:
 * <PRE>
  {@code
<ELEMENTs>
      <ELEMENT>
        <name>elem1</name>
        <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="url1" type="application/xml"/>
      </ELEMENT>
      <ELEMENT>
        <name>elem2</name>
        <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="url2" type="application/xml"/>
      </ELEMENT>
</ELEMENTs>
}</PRE>
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RESTAbstractList<ELEM extends NameLinkElem> implements Iterable<ELEM> {

    protected final List<Element> elementList;

    protected RESTAbstractList(Element list) {
        List<Element> tempList = new ArrayList<Element>();
        String baseName = null;

        for (Element listItem : (List<Element>) list.getChildren()) {
            if(baseName == null)
                baseName = listItem.getName();
            else
                if(! baseName.equals(listItem.getName())) {
                    throw new RuntimeException("List elements mismatching (" + baseName+","+listItem.getName()+")");
                }

            tempList.add(listItem);
        }

        elementList = Collections.unmodifiableList(tempList);
    }

    public int size() {
        return elementList.size();
    }

    public boolean isEmpty() {
        return elementList.isEmpty();
    }

    public ELEM get(int index) {
        return createElement(elementList.get(index));
    }

    public Iterator<ELEM> iterator() {
        return new RESTAbstractListIterator(elementList);
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<String>(elementList.size());
        for (ELEM elem: this) {
            names.add(elem.getName());
        }
        return names;
    }


    private class RESTAbstractListIterator extends JDOMListIterator<ELEM> {

        public RESTAbstractListIterator(List<Element> orig) {
            super(orig);
        }

        @Override
        public ELEM transform(Element listItem) {
            return createElement(listItem);
        }
    }
    
    protected ELEM createElement(Element el) {
        return (ELEM)new NameLinkElem(el);
    }

}

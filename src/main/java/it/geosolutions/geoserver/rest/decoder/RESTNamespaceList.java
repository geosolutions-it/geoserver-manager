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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

/**
 * Parses list of summary data about Namespaces.
 * <BR>Single items are handled by {@link RESTShortNamespace}.
 *
 * @author ETj (etj at geo-solutions.it)
 */
public class RESTNamespaceList implements Iterable<RESTNamespaceList.RESTShortNamespace> {

    private final List<Element> nsList;

    public static RESTNamespaceList build(String response) {
        if(response == null)
            return null;

        Element elem = JDOMBuilder.buildElement(response);
        if(elem != null)
            return new RESTNamespaceList(elem);
        else
            return null;
	}

    protected RESTNamespaceList(Element wslistroot) {
        List<Element> tmpList = new ArrayList<Element>();
        for (Element wselem : (List<Element>) wslistroot.getChildren("namespace")) {
            tmpList.add(wselem);
        }

        nsList = Collections.unmodifiableList(tmpList);
    }

    public int size() {
        return nsList.size();
    }

    public boolean isEmpty() {
        return nsList.isEmpty();
    }

    public RESTShortNamespace get(int index) {
        return new RESTShortNamespace(nsList.get(index));
    }

    public Iterator<RESTShortNamespace> iterator() {
        return new RESTNSListIterator(nsList);
    }


    private static class RESTNSListIterator implements Iterator<RESTShortNamespace> {

        private final Iterator<Element> iter;

        public RESTNSListIterator(List<Element> orig) {
            iter = orig.iterator();
        }        

        public boolean hasNext() {
            return iter.hasNext();
        }

        public RESTShortNamespace next() {
            return new RESTShortNamespace(iter.next());
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    /**
     * Namespace summary info.
     * <BR>This is an XML fragment:
     *
     * <PRE>
     * {@code
     *   <namespace>
     *      <name>it.geosolutions</name>
     *      <atom:link xmlns:atom="http://www.w3.org/2005/Atom"
     *          rel="alternate"
     *          href="http://localhost:8080/geoserver/rest/namespaces/it.geosolutions.xml"
     *          type="application/xml"/>
     *  </namespace>
     * }
     * </PRE>
     */

    public static class RESTShortNamespace {
        private final Element nsElem;

        public RESTShortNamespace(Element elem) {
            this.nsElem = elem;
        }

        public String getName() {
            return nsElem.getChildText("name");
        }
    }

}

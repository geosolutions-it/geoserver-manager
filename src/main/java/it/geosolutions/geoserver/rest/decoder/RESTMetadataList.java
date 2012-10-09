/*
 *  GeoBatch - Open Source geospatial batch processing system
 *  https://github.com/nfms4redd/nfms-geobatch
 *  Copyright (C) 2007-2012 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.geoserver.rest.decoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

/**
 * @author DamianoG
 *
 */
public class RESTMetadataList implements Iterable<RESTMetadataList.RESTMetadataElement> {

    private final List<Element> metadataList;
    
    /**
     * @param list
     */
    protected RESTMetadataList(Element list) {
       List<Element> tmpList = new ArrayList<Element>();
       for(Element el : (List<Element>)list.getChildren("entry")){
           tmpList.add(el);
       }
       metadataList = Collections.unmodifiableList(tmpList);
    }
    
    public int size() {
        return metadataList.size();
    }

    public boolean isEmpty() {
        return metadataList.isEmpty();
    }

    public RESTMetadataElement get(int index) {
        return new RESTMetadataElement(metadataList.get(index));
    }
    
    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<RESTMetadataElement> iterator() {
        return new RESTMetadataIterator(metadataList);
    }
    
    private static class RESTMetadataIterator implements Iterator<RESTMetadataElement>{
        
        private final Iterator<Element> iter;
        
        /**
         * @param iter
         */
        public RESTMetadataIterator(List<Element> orig) {
            this.iter = orig.iterator();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        @Override
        public RESTMetadataElement next() {
            return new RESTMetadataElement(iter.next());
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
            
        }
    }
    
    
    /**
     * Generic metadata Object
     *  
     * @author DamianoG
     *
     */
    public static class RESTMetadataElement {
        protected final Element metadataElem;

        public RESTMetadataElement(Element elem) {
            this.metadataElem = elem;
        }

        public String getKey() {
            return metadataElem.getAttributeValue("key");
        }
        
        public Element getMetadataElem() {
            return metadataElem;
        }
        
    }

}

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
import org.jdom.Namespace;

/**
 * This decode turns index format for a GeoServer StructuredGridCoverageReader into something
 * useful, giving access to the definition of the single attributes.
 * 
 * <P>This is the XML REST representation:
 * <PRE>
  {@code

<?xml version="1.0" encoding="UTF-8"?>
<wfs:FeatureCollection xmlns:gf="http://www.geoserver.org/rest/granules" xmlns:ogc="http://www.opengis.net/ogc" xmlns:wfs="http://www.opengis.net/wfs" xmlns:gml="http://www.opengis.net/gml">
  <gml:boundedBy>
    <gml:Box srsName="http://www.opengis.net/gml/srs/epsg.xml#4326">
      <gml:coord>
        <gml:X>5.0</gml:X>
        <gml:Y>45.0</gml:Y>
      </gml:coord>
      <gml:coord>
        <gml:X>14.875</gml:X>
        <gml:Y>50.9375</gml:Y>
      </gml:coord>
    </gml:Box>
  </gml:boundedBy>
  <gml:featureMember>
    <gf:V fid="V.337">
      <gf:the_geom>
        <gml:Polygon>
          <gml:outerBoundaryIs>
            <gml:LinearRing>
              <gml:coordinates>5.0,45.0 5.0,50.9375 14.875,50.9375 14.875,45.0 5.0,45.0</gml:coordinates>
            </gml:LinearRing>
          </gml:outerBoundaryIs>
        </gml:Polygon>
      </gf:the_geom>
      <gf:location>..\\polyphemus\\polyphemus_20130302.nc</gf:location>
      <gf:imageindex>672</gf:imageindex>
      <gf:time>2013-03-01T23:00:00Z</gf:time>
      <gf:elevation>10.0</gf:elevation>
      <gf:fileDate>2013-03-01T23:00:00Z</gf:fileDate>
      <gf:updated>2013-04-08T05:40:29.061Z</gf:updated>
    </gf:V>
  </gml:featureMember>
</wfs:FeatureCollection>

}</PRE>
 * @author Simone Giannecchini, GeoSolutions SAS
 *
 */
public class RESTStructuredCoverageGranulesList implements Iterable<RESTStructuredCoverageGranulesList.RESTStructuredCoverageGranule> {

    /** GML_NAMESPACE */
    private static final Namespace GML_NAMESPACE = Namespace.getNamespace("gml", "http://www.opengis.net/gml");

    private final List<RESTStructuredCoverageGranule> granulesList;
    
    private final Element bbox;
    
    /**
     * @return the bbox
     */
    public Element getBbox() {
        return bbox;
    }

    /**
     * @param list
     */
    @SuppressWarnings("unchecked")
    protected RESTStructuredCoverageGranulesList(Element featureCollection) {
        
       // check ordering of elements
       if(!featureCollection.getName().equals("FeatureCollection")){
           throw new IllegalStateException("Root element should be wfs:FeatureCollection");
       }
       Element boundedBy = featureCollection.getChild("boundedBy",GML_NAMESPACE);
       if(boundedBy==null){
           throw new IllegalStateException("Unable to find boundedBy element");
       }       
       // save bbox
       bbox=boundedBy.getChild("Box",GML_NAMESPACE);
       
       // now get the feature members
       List<RESTStructuredCoverageGranule> tmpList = new ArrayList<RESTStructuredCoverageGranule>();
       for(Element el : (List<Element>)featureCollection.getChildren("featureMember",GML_NAMESPACE)){
           tmpList.add(new RESTStructuredCoverageGranule(el));
       }
       granulesList = Collections.unmodifiableList(tmpList);
    }
    
    public static RESTStructuredCoverageGranulesList build(String response) {
        if(response == null)
            return null;
        
        Element pb = JDOMBuilder.buildElement(response);
        if(pb != null){
            return new RESTStructuredCoverageGranulesList(pb);
        } else {
            return null;
        }
    }
    
    public int size() {
        return granulesList.size();
    }

    public boolean isEmpty() {
        return granulesList.isEmpty();
    }

    public RESTStructuredCoverageGranule get(int index) {
        return granulesList.get(index);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<RESTStructuredCoverageGranule> iterator() {
        return granulesList.iterator();
    }
    
    /**
     * Generic granule of the index.
     *  
 * <P>This is the XML REST representation:
 * <PRE>
  {@code
  <gml:featureMember>
    <gf:V fid="V.1">
      <gf:the_geom>
        <gml:Polygon>
          <gml:outerBoundaryIs>
            <gml:LinearRing>
              <gml:coordinates>5.0,45.0 5.0,50.9375 14.875,50.9375 14.875,45.0 5.0,45.0</gml:coordinates>
            </gml:LinearRing>
          </gml:outerBoundaryIs>
        </gml:Polygon>
      </gf:the_geom>
      <gf:location>polyphemus_20130301.nc</gf:location>
      <gf:imageindex>672</gf:imageindex>
      <gf:time>2013-02-28T23:00:00Z</gf:time>
      <gf:elevation>10.0</gf:elevation>
      <gf:fileDate>2013-02-28T23:00:00Z</gf:fileDate>
      <gf:updated>2013-04-08T06:18:41.597Z</gf:updated>
    </gf:V>
  </gml:featureMember>

     * @author Simone Giannecchini, GeoSolutions SAS
     *
     */
    public static class RESTStructuredCoverageGranule {

        protected final Element granule;
        
        private final String fid;

        private final List<Element> children;

        @SuppressWarnings("unchecked")
        public RESTStructuredCoverageGranule(Element elem) {
            if(!elem.getName().equals("featureMember")){
                throw new IllegalStateException("Root element should be gml:featureMember for a granule");
            }
            Element feature = (Element) elem.getChildren().get(0);
            if(feature==null){
                throw new IllegalStateException("Unable to find feature element for this granule");
            }    
            this.granule = feature;
            this.fid=granule.getAttribute("fid").getValue();
            this.children=granule.getChildren();
            
        }

        public String getAttributeByName(String name) {
            return granule.getChildTextTrim(name,null);
        }   

        public String getAttributeByIndex(int index) {
            return children.get(index).getValue();
        }   
        
        @SuppressWarnings("unchecked")
        public Iterator<Element> getAttributesIterator() {
            return granule.getChildren().iterator();
        }  
        
        /**
         * @return the fid
         */
        public String getFid() {
            return fid;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RESTStructuredCoverageGranulesList [");
        if (bbox != null) {
            builder.append("bbox=");
            builder.append(bbox);
        }
        if (granulesList != null) {
            builder.append("granulesList={");
            for(RESTStructuredCoverageGranule granule:granulesList){
                builder.append(granule);
            }
            builder.append("}, ");
        }
        builder.append("]");
        return builder.toString();
    }

}

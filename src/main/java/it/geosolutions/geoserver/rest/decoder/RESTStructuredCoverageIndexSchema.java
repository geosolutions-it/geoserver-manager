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
 * This decode turns index format for a GeoServer StructuredGridCoverageReader into something
 * useful, giving access to the definition of the single attributes.
 * 
 * <P>This is the XML REST representation:
 * <PRE>
  {@code

<Schema>
  <attributes>
    <Attribute>
      <name>the_geom</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>com.vividsolutions.jts.geom.Polygon</binding>
    </Attribute>
    <Attribute>
      <name>location</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.String</binding>
    </Attribute>
    <Attribute>
      <name>imageindex</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.Integer</binding>
    </Attribute>
    <Attribute>
      <name>time</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.sql.Timestamp</binding>
    </Attribute>
    <Attribute>
      <name>elevation</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.lang.Double</binding>
    </Attribute>
    <Attribute>
      <name>fileDate</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.sql.Timestamp</binding>
    </Attribute>
    <Attribute>
      <name>updated</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>java.sql.Timestamp</binding>
    </Attribute>
  </attributes>
  <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/workspaces/it.geosolutions/coveragestores/polyphemus/coverages/V/index/granules.xml" type="application/xml"/>
</Schema>
}</PRE>
 * @author Simone Giannecchini, GeoSolutions SAS
 *
 */
public class RESTStructuredCoverageIndexSchema implements Iterable<RESTStructuredCoverageIndexSchema.RESTStructuredCoverageIndexAttribute> {

    private final List<RESTStructuredCoverageIndexAttribute> attributeList;
    
    /**
     * @param list
     */
    @SuppressWarnings("unchecked")
    protected RESTStructuredCoverageIndexSchema(Element schema) {
        
       // check ordering of elements
       if(!schema.getName().equals("Schema")){
           throw new IllegalStateException("Root element should be Schema");
       }
       Element attributes = schema.getChild("attributes");
       if(attributes==null){
           throw new IllegalStateException("Root element should be Schema");
       }       
       
       List<RESTStructuredCoverageIndexAttribute> tmpList = new ArrayList<RESTStructuredCoverageIndexAttribute>();
       for(Element el : (List<Element>)attributes.getChildren()){
           tmpList.add(new RESTStructuredCoverageIndexAttribute(el));
       }
       attributeList = Collections.unmodifiableList(tmpList);
    }
    
    public static RESTStructuredCoverageIndexSchema build(String response) {
        if(response == null)
            return null;
        
        Element pb = JDOMBuilder.buildElement(response);
        if(pb != null){
            return new RESTStructuredCoverageIndexSchema(pb);
        } else {
            return null;
        }
    }
    
    public int size() {
        return attributeList.size();
    }

    public boolean isEmpty() {
        return attributeList.isEmpty();
    }

    public RESTStructuredCoverageIndexAttribute get(int index) {
        return attributeList.get(index);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<RESTStructuredCoverageIndexAttribute> iterator() {
        return attributeList.iterator();
    }
    
    /**
     * Generic granule of the index.
     *  
 * <P>This is the XML REST representation:
 * <PRE>
  {@code
    <Attribute>
      <name>the_geom</name>
      <minOccurs>0</minOccurs>
      <maxOccurs>1</maxOccurs>
      <nillable>true</nillable>
      <binding>com.vividsolutions.jts.geom.Polygon</binding>
    </Attribute>
     * @author Simone Giannecchini, GeoSolutions SAS
     *
     */
    public static class RESTStructuredCoverageIndexAttribute {
        protected final Element attribute;

        public RESTStructuredCoverageIndexAttribute(Element elem) {
            this.attribute = elem;
        }

        public String getName() {
            return attribute.getChildTextTrim("name");
        }
        
        public String getMinOccurs() {
            return attribute.getChildTextTrim("minOccurs");
        }
        
        public String getMaxOccurs() {
            return attribute.getChildTextTrim("maxOccurs");
        }
        
        public String getNillable() {
            return attribute.getChildTextTrim("nillable");
        }
        
        public String getBinding() {
            return attribute.getChildTextTrim("binding");
        }

        @Override
        public String toString() {
            return "RESTStructuredCoverageGranule [getName()=" + getName()
                    + ", getMinOccurs()=" + getMinOccurs() + ", getMaxOccurs()=" + getMaxOccurs()
                    + ", getNillable()=" + getNillable() + ", getBinding()=" + getBinding() + "]";
        }
        
    }

}

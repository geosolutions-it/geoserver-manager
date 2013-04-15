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

package it.geosolutions.geoserver.rest.encoder.feature;

import it.geosolutions.geoserver.rest.encoder.GSResourceEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.GSFeatureDimensionInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadata.virtualtable.GSVirtualTableEncoder;

import org.jdom.Element;

/**
 * 
 * Encode a GeoServer resource as FeatureType
 * 
 * @author ETj (etj at geo-solutions.it)
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 */
public class GSFeatureTypeEncoder extends GSResourceEncoder {

    public final static String ATTRIBUTES = "attributes";

    final private Element attributes = new Element(ATTRIBUTES);

    public GSFeatureTypeEncoder() {
        super("featureType");
        addContent(attributes);
    }


    /**
     * @deprecated Use {@link GSResourceEncoder#addMetadataDimension(String, GSDimensionInfoEncoder)} this method will be removed soon
     * @param key
     * @param dimensionInfo
     * 
     */
    protected void addMetadata(String key, GSFeatureDimensionInfoEncoder dimensionInfo) {
        super.addMetadata(key, dimensionInfo);
    }
   
    
    /**
     * @deprecated Use {@link GSResourceEncoder#setMetadataDimension(String, GSDimensionInfoEncoder)} this method will be removed soon
     * @param key
     * @param dimensionInfo
     * 
     */
    public void setMetadata(String key, GSFeatureDimensionInfoEncoder dimensionInfo) {
        super.setMetadata(key, dimensionInfo);
    }
    
    /**
    * Add a VirtualTable (SQL View feature type)
    * 
    * @param virtualtable
    */
    protected void addMetadataVirtualTable(
    	final GSVirtualTableEncoder virtualtable) {
        super.addMetadata("JDBC_VIRTUAL_TABLE", virtualtable);
    }
    
    /**
    * Set a VirtualTable (SQL View feature type)
    * 
    * @param virtualtable
    */
    public void setMetadataVirtualTable(final GSVirtualTableEncoder virtualtable) {
    	super.setMetadata("JDBC_VIRTUAL_TABLE", virtualtable);
    } 
    
    /**
     * Deletes the VirtualTable metadata
     * 
     * @return true if deleted, false otherwise
     */
    public boolean delMetadataVirtualTable(){
    	return super.delMetadata("JDB_VIRTUAL_TABLE");
    }    
    
    /**
     * delete a keyword from the list
     * 
     * @param keyword
     * @return true if something is removed, false otherwise
     */
    public boolean delAttribute(final String keyword) {
        final Element el = new Element("string");
        el.setText(keyword);
        return (attributes.removeContent(GSAttributeEncoder.getFilterByName(keyword))).size() == 0 ? false
                : true;
    }

    /**
     * @param attribute the attribute to add
     */
    protected void addAttribute(GSAttributeEncoder attribute) {
        attributes.addContent(attribute.getRoot());
    }

    /**
     * @param attribute the attribute to set (overriding an attribute with the same name if present)
     */
    public void setAttribute(GSAttributeEncoder attribute) {
        delAttribute(attribute.getAttribute(FeatureTypeAttribute.name));
        addAttribute(attribute);
    }
    
}
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
import it.geosolutions.geoserver.rest.encoder.feature.FeatureTypeAttribute;
import it.geosolutions.geoserver.rest.encoder.feature.GSAttributeEncoder;
import it.geosolutions.geoserver.rest.encoder.metadatalink.GSMetadataLinkInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadatalink.ResourceMetadataLinkInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Parse a resource (FeatureType or Coverage) returned as XML REST objects.
 * 
 * @author etj
 * @author Emmanuel Blondel - emmanuel.blondel1@gmail.com |
 *         emmanuel.blondel@fao.org
 */
public class RESTResource {
    protected final Element rootElem;

    public static RESTResource build(String response) {
        Element elem = JDOMBuilder.buildElement(response);
        return elem == null ? null : new RESTCoverage(elem);
    }

    public RESTResource(Element resource) {
        this.rootElem = resource;
    }

    public String getName() {
        return rootElem.getChildText("name");
    }

    public String getTitle() {
        return rootElem.getChildText("title");
    }

    public String getNativeName() {
        return rootElem.getChildText("nativeName");
    }

    public String getAbstract() {
        return rootElem.getChildText("abstract");
    }

    public List<String> getKeywords(){
    	List<String> kwdsList = null;
    	
    	final Element keywordsRoot = rootElem.getChild("keywords");
    	if(keywordsRoot != null){
    		final List<Element> keywords = keywordsRoot.getChildren();
    		if(keywords != null){
    			kwdsList = new ArrayList<String>(keywords.size());
    			for(Element keyword : keywords){
    				kwdsList.add(keyword.getValue());
    			}
    		}
    	}
    	return kwdsList;
    }
    
    
    public String getNameSpace() {
        return rootElem.getChild("namespace").getChildText("name");
    }

    public String getStoreName() {
        return rootElem.getChild("store").getChildText("name");
    }

    public String getStoreType() {
        return rootElem.getChild("store").getAttributeValue("class");
    }

    public String getStoreUrl() {
        Element store = rootElem.getChild("store");
        Element atom = store.getChild("link",
                Namespace.getNamespace("atom", "http://www.w3.org/2005/Atom"));
        return atom.getAttributeValue("href");
    }

    public String getCRS() {
        Element elBBox = rootElem.getChild("latLonBoundingBox");
        return elBBox.getChildText("crs");
    }

    protected double getLatLonEdge(String edge) {
        Element elBBox = rootElem.getChild("latLonBoundingBox");
        return Double.parseDouble(elBBox.getChildText(edge));
    }

    public double getMinX() {
        return getLatLonEdge("minx");
    }

    public double getMaxX() {
        return getLatLonEdge("maxx");
    }

    public double getMinY() {
        return getLatLonEdge("miny");
    }

    public double getMaxY() {
        return getLatLonEdge("maxy");
    }

    /**
     * @deprecated use {@link RESTFeatureType#getAttributeList()}
     * @return
     * @throws UnsupportedOperationException
     */
    public List<Map<FeatureTypeAttribute, String>> getAttributeList() {
        throw new UnsupportedOperationException("This method is specific for RESTFeatureType");
    }

    /**
     * @deprecated use {@link RESTFeatureType#getEncodedAttributeList()}
     * @return
     * @throws UnsupportedOperationException
     */
    public List<GSAttributeEncoder> getEncodedAttributeList() {
        throw new UnsupportedOperationException("This method is specific for RESTFeatureType");
    }

	/**
	 * Decodes the list of MetadataLinkInfo from the GeoServer Resource
	 * 
	 * @author Emmanuel Blondel
	 * 
	 * @return the list of GSMetadataLinkEncoder
	 */
	public List<GSMetadataLinkInfoEncoder> getEncodedMetadataLinkInfoList() {
		List<GSMetadataLinkInfoEncoder> metaLinksList = null;

		final Element metaLinksRoot = rootElem.getChild("metadataLinks");
	        if(metaLinksRoot!=null){
        		final List<Element> metaLinks = metaLinksRoot.getChildren();
        		if (metaLinks != null) {
        			metaLinksList = new ArrayList<GSMetadataLinkInfoEncoder>(
        					metaLinks.size());
        			for (Element metaLink : metaLinks) {
        				final GSMetadataLinkInfoEncoder metaLinkEnc = new GSMetadataLinkInfoEncoder();
        				metaLinkEnc.setType(metaLink.getChildText(ResourceMetadataLinkInfo.type.name()));
        				metaLinkEnc.setMetadataType(metaLink.getChildText(ResourceMetadataLinkInfo.metadataType.name()));
        				metaLinkEnc.setContent(metaLink.getChildText(ResourceMetadataLinkInfo.content.name()));
        				metaLinksList.add(metaLinkEnc);
        			}
        
        		}
	        }
		return metaLinksList;
	}
    
}

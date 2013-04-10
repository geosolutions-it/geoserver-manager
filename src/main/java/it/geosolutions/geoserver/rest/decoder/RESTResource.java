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

    public List<Map<FeatureTypeAttribute, String>> getAttributeList() {
        List<Map<FeatureTypeAttribute, String>> attrsList = null;

        final Element attrsRoot = rootElem.getChild("attributes");
        final List<Element> attrs = attrsRoot.getChildren();
        if (attrs != null) {
            attrsList = new ArrayList<Map<FeatureTypeAttribute, String>>(attrs.size());
            for (Element attr : attrs) {
                Map<FeatureTypeAttribute, String> attrsMap = new HashMap<FeatureTypeAttribute, String>();
                attrsList.add(attrsMap);
                for (FeatureTypeAttribute at : FeatureTypeAttribute.values()) {
                    String key = at.toString();
                    attrsMap.put(at, attr.getChildText(key));
                }
            }
        }
        return attrsList;
    }

    public List<GSAttributeEncoder> getEncodedAttributeList() {
        List<GSAttributeEncoder> attrsList = null;

        final Element attrsRoot = rootElem.getChild("attributes");
        final List<Element> attrs = attrsRoot.getChildren();
        if (attrs != null) {
            attrsList = new ArrayList<GSAttributeEncoder>(attrs.size());
            for (Element attr : attrs) {
                final GSAttributeEncoder attrEnc = new GSAttributeEncoder();
                for (FeatureTypeAttribute at : FeatureTypeAttribute.values()) {
                    String key = at.toString();
                    attrEnc.setAttribute(at, attr.getChildText(key));
                }
                attrsList.add(attrEnc);
            }

        }
        return attrsList;
    }

    

	/**
	 * Decodes the list of MetadataLinkInfo from the GeoServer Resource
	 * 
	 * @author Emmanuel Blondel
	 * 
	 * @return the list of Map<ResourceMetadataLinkInfo,String>
	 */
	public List<Map<ResourceMetadataLinkInfo, String>> getMetadataLinkInfoList() {
		List<Map<ResourceMetadataLinkInfo, String>> metaLinksList = null;

		final Element metaLinksRoot = rootElem.getChild("metadataLinks");
		final List<Element> metaLinks = metaLinksRoot.getChildren();
		if (metaLinks != null) {
			metaLinksList = new ArrayList<Map<ResourceMetadataLinkInfo, String>>(
					metaLinks.size());
			for (Element metaLink : metaLinks) {
				Map<ResourceMetadataLinkInfo, String> metaLinkMap = new HashMap<ResourceMetadataLinkInfo, String>();
				metaLinksList.add(metaLinkMap);
				for (ResourceMetadataLinkInfo rmd : ResourceMetadataLinkInfo
						.values()) {
					String key = rmd.toString();
					metaLinkMap.put(rmd, metaLink.getChildText(key));
				}
			}
		}
		return metaLinksList;
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
		final List<Element> metaLinks = metaLinksRoot.getChildren();
		if (metaLinks != null) {
			metaLinksList = new ArrayList<GSMetadataLinkInfoEncoder>(
					metaLinks.size());
			for (Element metaLink : metaLinks) {
				final GSMetadataLinkInfoEncoder metaLinkEnc = new GSMetadataLinkInfoEncoder();
				for (ResourceMetadataLinkInfo rmd : ResourceMetadataLinkInfo
						.values()) {
					String key = rmd.toString();
					metaLinkEnc.setMetadataLinkInfoMember(rmd,
							metaLink.getChildText(key)); // change
				}
				metaLinksList.add(metaLinkEnc);
			}

		}
		return metaLinksList;
	}
    
    // /**
    // * @return the list of available attribute names
    // */
    // public List<String> getAttributeNames() {
    // return getAttributes("name");
    // }
    //
    // /**
    // * @return a list of object which are String representation of Classes
    // */
    // public List<String> getAttributeBinding() {
    // return getAttributes("binding");
    // }

    // /**
    // * <attribute><br>
    // * <name>NATION</name><br>
    // * <minOccurs>0</minOccurs><br>
    // * <maxOccurs>1</maxOccurs><br>
    // * <nillable>true</nillable><br>
    // * <binding>java.lang.Integer</binding><br>
    // * <length>3</length><br>
    // * </attribute><br>
    // *
    // * @param name
    // * @return
    // */
    // private List<String> getAttributes(String name) {
    // final Element attrsRoot = rootElem.getChild("attributes");
    // final List<Element> attrs = attrsRoot.getChildren();
    // List<String> attrNames = null;
    // if (attrs != null) {
    // attrNames = new ArrayList<String>(attrs.size());
    // for (Element attr : attrs) {
    // attrNames.add(attr.getChildText(name));
    // }
    // }
    // return attrNames;
    // }
}

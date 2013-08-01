/*
 *  GeoServer-Manager - Simple Manager Library for GeoServer
 *  
 *  Copyright (C) 2007,2012 GeoSolutions S.A.S.
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

import java.net.URI;

import org.jdom.Element;

/**
 * Parse <TT>namespace</TT>s returned as XML REST objects.
 * 
 * This is the XML REST representation:
 * <pre>{@code
   <namespace>
     <prefix>topp</prefix>
     <uri>http://www.openplans.org/topp</uri>
     <featureTypes>
       <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/workspaces/topp/featuretypes.xml" type="application/xml"/>
     </featureTypes>
   </namespace>
 * }</pre>
 * 
 * @author Oscar Fonts
 */
public class RESTNamespace {
	public final static String NAMESPACE="namespace";
	public final static String PREFIX="prefix";
	public final static String URI="uri";
	public final static String FEATURE_TYPES="featureTypes";
	
	private final Element namespaceElem;
	
    /**
     * Build a RESTNamespace from a REST response.
     * 
     * @param response XML representation of the namespace.
     * @return a new RESTNamespace, or null if XML could not be parsed.
     */
    public static RESTNamespace build(String response) {
        if(response == null)
            return null;
        
        Element pb = JDOMBuilder.buildElement(response);
        if(pb != null)
            return new RESTNamespace(pb);
        else
            return null;
	}
    
    /**
     * Create a RESTNamespace from a XML element.
     * 
     * @param elem The jdom XML Element describing a namespace.
     */
	public RESTNamespace(Element elem) {
		this.namespaceElem = elem;
	}
	
	/**
	 * Get the namespace prefix
	 * 
	 * @return the namespace prefix.
	 */
	public String getPrefix() {
		return namespaceElem.getChildText(PREFIX);
	}
	
	/**
	 * Get the namespace URI.
	 * 
	 * @return the namespace uri.
	 */
	public URI getURI() {
		return java.net.URI.create(namespaceElem.getChildText(URI));
	}
}

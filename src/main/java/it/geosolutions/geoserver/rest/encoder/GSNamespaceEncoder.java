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
package it.geosolutions.geoserver.rest.encoder;

import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;

import java.net.URI;

/**
 * Namespace XML encoder. Namespaces must contain a non empty prefix and a URI:
 * 
 * <pre>
 * &lt;namespace>
 *   &lt;prefix>example&lt;/prefix>
 *   &lt;uri>http://example.com&lt;/uri>
 * &lt;/namespace>
 * </pre>
 * 
 * @see <a href="http://docs.geoserver.org/stable/en/user/restconfig/rest-config-api.html#namespaces"> GeoServer Documentation</a>
 * @author Oscar Fonts
 */
public class GSNamespaceEncoder extends PropertyXMLEncoder {

	public final static String NAMESPACE="namespace";
	public final static String PREFIX="prefix";
	public final static String URI="uri";

    /**
     * Create a namespace XML encoder.
     * 
     * @param prefix the namespace prefix
     * @param uri the namespace URI
     * @throws IllegalArgumentException if prefix or uri are null or empty
     */
    public GSNamespaceEncoder(String prefix, URI uri) {
    	super(NAMESPACE);

    	ensureValidPrefix(prefix);
		ensureValidURI(uri);

		add(PREFIX, prefix);
		add(URI, uri.toString());
    }
    
    /**
     * Get the namespace prefix.
     * @return the prefix
     */
    public String getPrefix() {
    	return ElementUtils.contains(getRoot(), PREFIX).getTextTrim();
    }

    /**
     * Change the namespace prefix.
     * @param prefix the new prefix
     * @throws IllegalArgumentException if prefix is null or empty
     */
    public void setPrefix(final String prefix) {
    	ensureValidPrefix(prefix);
    	ElementUtils.contains(getRoot(), PREFIX).setText(prefix);
    }
    
    /**
     * Get the namespace uri.
     * @return the uri
     */
    public URI getURI() {
    	String sUri = ElementUtils.contains(getRoot(), URI).getTextTrim();
   		return java.net.URI.create(sUri);
    }
    
    /**
     * change the nampespace uri.
     * @param URI the new uri
     * @throws IllegalArgumentException if uri is null or empty
     */
    public void setURI(final URI uri) {
    	ensureValidURI(uri);
    	String sUri = uri.toString();    	
    	ElementUtils.contains(getRoot(), URI).setText(sUri);
    }
    
    /**
     * Check prefix value.
     * 
     * @param prefix the prefix
     * @throws IllegalArgumentException if prefix is null or empty
     */
    private static void ensureValidPrefix(String prefix) {
		if (prefix == null || prefix.isEmpty()) {
			throw new IllegalArgumentException(
				"Namespace prefix cannot be null or empty");			
		}    	
    }
    
    /**
     * Check uri value.
     * 
     * @param prefix the uri
     * @throws IllegalArgumentException if uri is null or empty
     */
    private static void ensureValidURI(URI uri) {
		if (uri == null || uri.toString().isEmpty()) {
			throw new IllegalArgumentException(
				"Namespace uri cannot be null or empty");
		}   	
    }
}

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

import org.jdom.Element;

/**
 * Parse <TT>CoverageStore</TT>s returned as XML REST objects.
 * <P>
 * This is the XML document returned by GeoServer when requesting a CoverageStore:
 * <PRE>
 * {@code 
 * <coverageStore>
 *      <name>testRESTStoreGeotiff</name>
 *      <type>GeoTIFF</type>
 *      <enabled>true</enabled>
 *      <workspace>
 *          <name>it.geosolutions</name>
 *          <href>http://localhost:8080/geoserver/rest/workspaces/it.geosolutions.xml</href>
 *      </workspace>
 *      <url>file:/home/geosolutions/prj/git/gman/target/test-classes/testdata/resttestdem.tif</url>
 *      <coverages>
 *          <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate"
 *              href="http://localhost:8080/geoserver/rest/workspaces/it.geosolutions/coveragestores/testRESTStoreGeotiff/external/coverages.xml"
 *              type="application/xml"/>
 *      </coverages>
 * </coverageStore>
 * }
 * </PRE>
 *
 * <I>Note: the whole XML fragment is stored in memory. At the moment, there are
 * methods to retrieve only the more useful data.
 * 
 * @author etj
 */
public class RESTCoverageStore {
	private final Element cs;


	public RESTCoverageStore(Element cs) {
		this.cs = cs;
	}

    public static RESTCoverageStore build(String response) {
        if(response == null)
            return null;
        if(response.isEmpty())
        	return new RESTCoverageStore(new Element("coverageStore")); // TODO check how to response
        
        Element pb = JDOMBuilder.buildElement(response);
        if(pb != null)
            return new RESTCoverageStore(pb);
        else
            return null;
    }

    public String getName() {
        return cs.getChildText("name");
    }

    public String getWorkspaceName() {
        return cs.getChild("workspace").getChildText("name");
    }

    public String getURL() {
        return cs.getChildText("url");
    }
    
    public String getType() {
        return cs.getChildText("type");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName())
                .append('[');
        if(cs == null)
            sb.append("null");
        else
            sb.append("name:").append(getName())
                .append(" wsname:").append(getWorkspaceName());

        return sb.toString();
    }
}

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

package it.geosolutions.geoserver.rest.decoder.gwc;

import it.geosolutions.geoserver.rest.decoder.*;
import java.util.ArrayList;
import java.util.List;

import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import it.geosolutions.geoserver.rest.encoder.authorityurl.AuthorityURLInfo;
import it.geosolutions.geoserver.rest.encoder.authorityurl.GSAuthorityURLInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.identifier.GSIdentifierInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.identifier.IdentifierInfo;
import it.geosolutions.geoserver.rest.encoder.metadatalink.GSMetadataLinkInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.metadatalink.ResourceMetadataLinkInfo;

import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Parse <TT>wmsLayer</TT>s returned as XML REST objects.
 *
 * <P>This is the XML REST representation:
 * <PRE>
 * {@code
    <wmsLayer>
      <metaInformation>
      <name>img states</name>
        <title>Nicer title for Image States</title>
        <description>This is a description. Fascinating.</description>
      </metaInformation>
      <mimeFormats>
        <string>image/gif</string>
        <string>image/jpeg</string>
        <string>image/png</string>
        <string>image/png8</string>
      </mimeFormats>
      <gridSubsets>
        <gridSubset>
          <gridSetName>EPSG:4326</gridSetName>
          <extent>
            <coords>
              <double>-129.6</double>
              <double>3.45</double>
              <double>-62.1</double>
              <double>70.9</double>
            </coords>
          </extent>
        </gridSubset>
      </gridSubsets>
      <expireCacheList>
        <expirationRule minZoom="0" expiration="60" />
      </expireCacheList>
      <expireClientsList>
        <expirationRule minZoom="0" expiration="500" />
      </expireClientsList>
      <wmsUrl>
        <string>http://demo.opengeo.org/geoserver/wms</string>
      </wmsUrl>
      <wmsLayers>nurc:Img_Sample,topp:states</wmsLayers>
      <transparent>false</transparent>
      <bgColor>0x0066FF</bgColor>
    </wmsLayer>
 * }</PRE>
 * @author Nazzareno Sileno
 */
public class GWCRESTWMSLayer {
	protected final Element wmsLayerElem;

    public static GWCRESTWMSLayer build(String response) {
        if(response == null)
            return null;
        
        Element pb = JDOMBuilder.buildElement(response);
        if(pb != null)
            return new GWCRESTWMSLayer(pb);
        else
            return null;
	}

	public GWCRESTWMSLayer(Element layerElem) {
		this.wmsLayerElem = layerElem;
	}
        
        public String getName() {
        	return wmsLayerElem.getChildText("name");
	}

        public String getTitle() {
		Element metaInformation = wmsLayerElem.getChild("metaInformation");
		return metaInformation.getChildText("title");
	}
        
        public String getDescription() {
		Element metaInformation = wmsLayerElem.getChild("metaInformation");
		return metaInformation.getChildText("description");
	}
        
        public List<String> getMimeFormats() {
		List<String> mimeFormatsList = new ArrayList<String>();
		final Element mimeFormatsRoot = wmsLayerElem.getChild("mimeFormats");
		if (mimeFormatsRoot != null) {
                    for (Element listItem : (List<Element>) mimeFormatsRoot.getChildren()) {
			mimeFormatsList.add(listItem.getChildText("string"));
                    }
		}
		return mimeFormatsList;
	}
        
//	public boolean getEnabled(){
//		return Boolean.parseBoolean(wmsLayerElem.getChildText("enabled"));
//	}
//	
//	public boolean getQueryable(){
//		return Boolean.parseBoolean(wmsLayerElem.getChildText("queryable"));
//	}
//	
//	public boolean getAdvertised(){
//		return Boolean.parseBoolean(wmsLayerElem.getChildText("advertised"));
//	}
//
//	public String getTypeString() {
//		return wmsLayerElem.getChildText("type");
//	}
//
//	public Type getType() {
//		return Type.get(getTypeString());
//	}
//
//	public String getDefaultStyle() {
//		Element defaultStyle = wmsLayerElem.getChild("defaultStyle");
//		return defaultStyle == null? null : defaultStyle.getChildText("name");
//  	}
//	
//	public RESTStyleList getStyles() {
//		RESTStyleList styleList = null;
//		final Element stylesRoot = wmsLayerElem.getChild("styles");
//		if (stylesRoot != null) {
//			styleList = new RESTStyleList(stylesRoot);
//		}
//		return styleList;
//	}
//
//	public String getDefaultStyleWorkspace() {
//		Element defaultStyle = wmsLayerElem.getChild("defaultStyle");
//		return defaultStyle == null? null : defaultStyle.getChildText("workspace");
//  	}
//
//	public String getTitle() {
//		Element resource = wmsLayerElem.getChild("resource");
//		return resource.getChildText("title");
//	}
//
//	public String getAbstract() {
//		Element resource = wmsLayerElem.getChild("resource");
//		return resource.getChildText("abstract");
//	}
//
//	public String getNameSpace() {
//		Element resource = wmsLayerElem.getChild("resource");
//		return resource.getChild("namespace").getChildText("name");
//	}
//
//    /**
//     * Get the URL to retrieve the featuretype.
//     * <PRE>{@code
//        <resource class="featureType">
//        <name>tasmania_cities</name>
//        <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/workspaces/topp/datastores/taz_shapes/featuretypes/tasmania_cities.xml" type="application/xml"/>
//    </resource>
//     * }</CODE>
//     */
//    public String getResourceUrl() {
//	Element resource = wmsLayerElem.getChild("resource");
//        Element atom = resource.getChild("link", Namespace.getNamespace("atom", "http://www.w3.org/2005/Atom"));
//        return atom.getAttributeValue("href");
//    }
//    
//    
//	/**
//	 * Decodes the list of AuthorityURLInfo from the GeoServer Layer
//	 * 
//	 * @return the list of GSAuthorityURLInfoEncoder
//	 */
//	public List<GSAuthorityURLInfoEncoder> getEncodedAuthorityURLInfoList() {
//		List<GSAuthorityURLInfoEncoder> authorityURLList = null;
//
//		final Element authorityURLsRoot = wmsLayerElem.getChild("authorityURLs");
//		if (authorityURLsRoot != null) {
//			final List<Element> authorityURLs = authorityURLsRoot.getChildren();
//			if (authorityURLs != null) {
//				authorityURLList = new ArrayList<GSAuthorityURLInfoEncoder>(
//						authorityURLs.size());
//				for (Element authorityURL : authorityURLs) {
//					final GSAuthorityURLInfoEncoder authEnc = new GSAuthorityURLInfoEncoder();
//					authEnc.setName(authorityURL
//							.getChildText(AuthorityURLInfo.name.name()));
//					authEnc.setHref(authorityURL
//							.getChildText(AuthorityURLInfo.href.name()));
//					authorityURLList.add(authEnc);
//				}
//			}
//		}
//		return authorityURLList;
//	}
//
//	/**
//	 * Decodes the list of IdentifierInfo from the GeoServer Layer
//	 * 
//	 * @return the list of IdentifierInfoEncoder
//	 */
//	public List<GSIdentifierInfoEncoder> getEncodedIdentifierInfoList() {
//		List<GSIdentifierInfoEncoder> idList = null;
//
//		final Element idRoot = wmsLayerElem.getChild("identifiers");
//		if (idRoot != null) {
//			final List<Element> identifiers = idRoot.getChildren();
//			if (identifiers != null) {
//				idList = new ArrayList<GSIdentifierInfoEncoder>(
//						identifiers.size());
//				for (Element identifier : identifiers) {
//					final GSIdentifierInfoEncoder idEnc = new GSIdentifierInfoEncoder();
//					idEnc.setAuthority(identifier
//							.getChildText(IdentifierInfo.authority.name()));
//					idEnc.setIdentifier(identifier
//							.getChildText(IdentifierInfo.identifier.name()));
//					idList.add(idEnc);
//				}
//			}
//		}
//		return idList;
//	}
    
}

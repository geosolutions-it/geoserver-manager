package it.geosolutions.geoserver.rest.decoder;

import it.geosolutions.geoserver.rest.decoder.utils.JDOMBuilder;
import it.geosolutions.geoserver.rest.encoder.authorityurl.GSAuthorityURLInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.identifier.GSIdentifierInfoEncoder;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

/**
 * Parse <TT>Layer</TT>s returned as XML REST objects. Applicable to GS 2.1 for
 * decoding: - AuthorityURLs - Identifiers - advertised property value
 * 
 * <P>
 * This is the XML REST representation:
 * 
 * <PRE>
 *  {@code
 *  <layer>
 *    <name>tasmania_cities</name>
 *    <path>/</path>
 *    <type>VECTOR</type>
 *    <defaultStyle>
 *        <name>capitals</name>
 *        <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/styles/capitals.xml" type="application/xml"/>
 *    </defaultStyle>
 *    <resource class="featureType">
 *        <name>tasmania_cities</name>
 *        <atom:link xmlns:atom="http://www.w3.org/2005/Atom" rel="alternate" href="http://localhost:8080/geoserver/rest/workspaces/topp/datastores/taz_shapes/featuretypes/tasmania_cities.xml" type="application/xml"/>
 *    </resource>
 *    <enabled>true</enabled>
 *    <queryable>true</queryable>
 *    <advertised>true</advertised>
 *    <attribution>
 *        <logoWidth>0</logoWidth>
 *        <logoHeight>0</logoHeight>
 *    </attribution>
 *    <metadata>
 *    	<entry key="identifiers">
 *    		[{"authority":"authority1","identifier":"identifier1"},]
 *   	</entry>
 *   	<entry key="authorityURLs">
 *   		[{"name":"authority1","href":"http://www.authority1.org"},]
 *   	</entry>
 *   	<entry key="advertised">true</entry>
 *    </metadata>
 * </layer>
 *  }
 * </PRE>
 * 
 * @author eblondel
 */
public class RESTLayer21 extends RESTLayer{

   
	public RESTLayer21(Element layerElem) {
		super(layerElem);
	}

	public static RESTLayer21 build(String response) {
        if(response == null)
            return null;
        
        Element pb = JDOMBuilder.buildElement(response);
        if(pb != null)
            return new RESTLayer21(pb);
        else
            return null;
	}
	
	/**
	 * Decodes the advertised property from the Geoserver Layer
	 * 
	 */
	public boolean getAdvertised(){
		boolean advertised = true;
		
		final Element metadataRoot = layerElem.getChild("metadata");
		if(metadataRoot != null){
			final List<Element> metaElements = metadataRoot.getChildren();
			if(metaElements != null){
				for(Element el : metaElements){
					String key = el.getAttributeValue("key");			
					if(key.matches("advertised")){
						advertised = Boolean.parseBoolean(el.getValue());
					}
				}
			}
		}
		return advertised;
	}
	
	/**
	 * Decodes the list of AuthorityURLInfo from the GeoServer Layer
	 * 
	 * @return the list of GSAuthorityURLInfoEncoder
	 */
	public List<GSAuthorityURLInfoEncoder> getEncodedAuthorityURLInfoList() {
		List<GSAuthorityURLInfoEncoder> authorityURLList = null;

		final Element metadataRoot = layerElem.getChild("metadata");
		if (metadataRoot != null) {
			final List<Element> metaElements = metadataRoot.getChildren();
			if (metaElements != null) {
				for (Element element : metaElements) {
					String key = element.getAttributeValue("key");
					if (key.matches("authorityURLs")) {

						String jsonStr = element.getValue();
						jsonStr = jsonStr.substring(2);
						jsonStr = jsonStr.substring(0,
								jsonStr.length() - 3);

						String[] items = jsonStr.split("\\}(,)\\{");
						authorityURLList = new ArrayList<GSAuthorityURLInfoEncoder>(items.length);
						for (String item : items) {
							String[] props = item.split(",");
								
							String[] kvp1 = props[0].split("\":");
							String name = kvp1[1].replace("\"", "");
							String[] kvp2 = props[1].split("\":");
							String href = kvp2[1].replace("\"", "");
		
							authorityURLList
									.add(new GSAuthorityURLInfoEncoder(
											name, href));
						}
					}
				}
			}
		}
		return authorityURLList;
	}

	/**
	 * Decodes the list of IdentifierInfo from the GeoServer Layer
	 * 
	 * @return the list of IdentifierInfoEncoder
	 */
	public List<GSIdentifierInfoEncoder> getEncodedIdentifierInfoList() {
		List<GSIdentifierInfoEncoder> identifierList = null;

		final Element metadataRoot = layerElem.getChild("metadata");
		if (metadataRoot != null) {
			final List<Element> metaElements = metadataRoot.getChildren();
			if (metaElements != null) {
				for (Element element : metaElements) {
					String key = element.getAttributeValue("key");
					if (key.matches("identifiers")) {

						String jsonStr = element.getValue();
						jsonStr = jsonStr.substring(2);
						jsonStr = jsonStr.substring(0,
								jsonStr.length() - 3);
	
						String[] items = jsonStr.split("\\}(,)\\{");
						identifierList = new ArrayList<GSIdentifierInfoEncoder>(items.length);
						for (String item : items) {
							String[] props = item.split(",");
								
							String[] kvp1 = props[0].split("\":");
							String authority = kvp1[1].replace("\"", "");
							String[] kvp2 = props[1].split("\":");
							String identifier = kvp2[1].replace("\"", "");
							
							identifierList
									.add(new GSIdentifierInfoEncoder(
											authority, identifier));
						}
					}
				}
			}
		}
		return identifierList;
	}

}

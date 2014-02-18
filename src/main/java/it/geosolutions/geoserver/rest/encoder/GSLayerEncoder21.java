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

package it.geosolutions.geoserver.rest.encoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.geosolutions.geoserver.rest.encoder.authorityurl.AuthorityURLInfo;
import it.geosolutions.geoserver.rest.encoder.authorityurl.GSAuthorityURLInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.identifier.GSIdentifierInfoEncoder;
import it.geosolutions.geoserver.rest.encoder.identifier.IdentifierInfo;
import it.geosolutions.geoserver.rest.encoder.utils.NestedElementEncoder;
import it.geosolutions.geoserver.rest.encoder.utils.XmlElement;

/**
 * Layer encoder for Geoserver = 2.1
 *
 * @author Emmanuel Blondel - emmanuel.blondel1@gmail.com
 * 
 * The layer encoder is enabled by default
 * 
 * @since gs-2.1.x
 * 
 */
public class GSLayerEncoder21 extends GSLayerEncoder {
	

	public final static String METADATA = "metadata";
	final private GSMetadataEncoder metadata = new GSMetadataEncoder();
	public Map<String,String> authorityURLList;
	public Map<String,List<String>> identifierList;

	private class GSMetadataEncoder extends NestedElementEncoder{
		public GSMetadataEncoder() {
			super(METADATA);
		}
	}
	
    public GSLayerEncoder21() {
    	super();
        addContent(metadata.getRoot());
        addAdvertised();
    }

	/**
	 * @param key
	 * @param dimensionInfo
	 */
	protected void addMetadata(String key, XmlElement dimensionInfo) {
		metadata.add(key, dimensionInfo.getRoot());
	}
	
	/**
     * advertise the layer
     */
    protected void addAdvertised(){
       metadata.add("advertised", "true");
    }
	
	/**
	 * 
	 * @param advertised true if the layer should be advertised
	 */
	public void setAdvertised(boolean advertised){
		if(advertised){
			metadata.add("advertised", "true");
		}else{
			metadata.add("advertised", "false");
		}
	}
		
	/**
	 * Add an authorityURLInfo to the GeoServer layer
	 * 
	 * @param authorityURLInfo
	 */
	public void addAuthorityURL(GSAuthorityURLInfoEncoder authorityURLInfo){	
		if(authorityURLList == null){
			authorityURLList = new HashMap<String,String>();
		}
		authorityURLList.put(authorityURLInfo.getHref(), authorityURLInfo.getName());
		String jsonStr = "";
		for(Entry<String, String> entry : authorityURLList.entrySet()){
			jsonStr += "{"+
					"\""+AuthorityURLInfo.name.name()+"\":\""+entry.getValue()+"\","+
					"\""+AuthorityURLInfo.href.name()+"\":\""+entry.getKey()+"\""+
					"},";
		}
		metadata.set("authorityURLs", "["+jsonStr+"]");
	}
	
	
	/**
	 * Deletes a AuthorityURLInfo from the list using the authorityURL
	 * (AuthorityURLInfo href)
	 * 
	 * @param authorityURL
	 * @return true if something is removed, false otherwise
	 */
	public boolean delAuthorityURL(final String authorityURL){
		boolean delete = false;
		if(!(authorityURLList == null || authorityURLList.isEmpty())){
			if(authorityURLList.containsKey(authorityURL)){
				identifierList.remove(authorityURL);
				String jsonStr = "";
				for (Entry<String, List<String>> entry : identifierList
						.entrySet()) {
					for (String value : entry.getValue()) {
						jsonStr += "{" + "\"" + AuthorityURLInfo.name.name()
								+ "\":\"" + entry.getValue() + "\"," + "\""
								+ AuthorityURLInfo.href.name() + "\":\""
								+ value + "\"" + "},";
					}
				}
				metadata.set("identifiers", "["+jsonStr+"]");
				delete = true;
			}
		}
		return delete;
	}
	
	/**
	 * Add an identifierInfo to the GeoServer layer
	 * 
	 * @param identifierInfo
	 */
	public void addIdentifier(GSIdentifierInfoEncoder identifierInfo){
		if(identifierList == null){
			identifierList = new HashMap<String,List<String>>();
		}
		
		String authority = identifierInfo.getAuthority();

		if (!identifierList.containsKey(authority)) {
			List<String> ids = new ArrayList<String>();
			ids.add(identifierInfo.getIdentifier());
			identifierList.put(authority, ids);
		} else {
			List<String> ids = identifierList.get(authority);
			ids.add(identifierInfo.getIdentifier());
			identifierList.put(authority, ids);
		}
		
		String jsonStr = "";
		for (Entry<String, List<String>> entry : identifierList.entrySet()) {
			for (String value : entry.getValue()) {
				jsonStr += "{" + "\"" + IdentifierInfo.authority.name()
						+ "\":\"" + entry.getKey() + "\"," + "\""
						+ IdentifierInfo.identifier.name() + "\":\"" + value
						+ "\"" + "},";
			}
		}
		metadata.set("identifiers", "["+jsonStr+"]");
	}
	
	/**
	 * Deletes a IdentifierInfo from the list using the authority
	 * name (IdentifierInfo authority)
	 * 
	 * @param authority
	 * @return true if something is removed, false otherwise
	 */
	public boolean delIdentifier(final String authority){
		boolean delete = false;
		if(!(identifierList == null || identifierList.isEmpty())){
			if(identifierList.containsKey(authority)){
				identifierList.remove(authority);
				String jsonStr = "";
				for (Entry<String, List<String>> entry : identifierList
						.entrySet()) {
					for (String value : entry.getValue()) {
						jsonStr += "{" + "\"" + IdentifierInfo.authority.name()
								+ "\":\"" + entry.getKey() + "\"," + "\""
								+ IdentifierInfo.identifier.name() + "\":\""
								+ value + "\"" + "},";
					}
				}
				metadata.set("identifiers", "["+jsonStr+"]");
				delete = true;
			}
		}
		return delete;
	}
	
	
}

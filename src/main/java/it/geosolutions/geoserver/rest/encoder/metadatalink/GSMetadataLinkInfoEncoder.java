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
package it.geosolutions.geoserver.rest.encoder.metadatalink;

import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import it.geosolutions.geoserver.rest.encoder.utils.XmlElement;

import org.jdom.Element;
import org.jdom.filter.Filter;

/**
 * GSMetadataLinkEncoder - encodes a metadataLink for a given GeoServer Resource
 * (feature type /coverage), as follows:
 * <pre>
 * {@code
 * final GSMetadataLinkInfoEncoder mde = new GSMetadataLinkInfoEncoder();
 * mde.setType("text/xml");
 * mde.setMetadataType("ISO19115:2003");
 * mde.setContent("http://www.organization.org/metadata");
 * }
 * </pre>
 * For this example, the XML output is:
 * <pre>
 * {@code
 * <metadataLink>
 * 	<type>text/xml</type>
 * 	<metadataType>ISO19115:2003</metadataType>
 * 	<content>http://www.organization.org/metadata</content>
 * </metadataLink>
 * }
 * </pre>
 * 
 * @author Emmanuel Blondel - emmanuel.blondel1@gmail.com |
 *         emmanuel.blondel@fao.org
 * 
 */
public class GSMetadataLinkInfoEncoder extends XmlElement {

	/** A class to filter the MetadataLinkInfo by content
	 * 
	 *
	 */
	private static class filterByContent implements Filter {

		final private String key;

		public filterByContent(String content) {
			this.key = content;
		}

		private static final long serialVersionUID = 1L;

		public boolean matches(Object obj) {
			Element el = ((Element) obj)
					.getChild(ResourceMetadataLinkInfo.content.toString());
			if (el != null && el.getTextTrim().equals(key)) {
				return true;
			}
			return false;
		}
	}

	/** 
	 * Get a Filter using the MetadataLinkInfo content (metadataURL)
	 * 
	 * @param content
	 * @return the filter
	 */
	public static Filter getFilterByContent(String content) {
		return new filterByContent(content);
	}

	/** 
	 * Constructs a new GSMetadataLinkInfoEncoder
	 * 
	 */
	public GSMetadataLinkInfoEncoder() {
		super("metadataLink");
	}

	/**
	 * Constructs quickly a MetadataLink info
	 * 
	 * @param type (required)
	 * @param metadataType (required)
	 * @param content (required)
	 */
	public GSMetadataLinkInfoEncoder(String type, String metadataType, String content){
		super("metadataLink");
		this.setup(type, metadataType, content);
	}
	
	/** 
	 * Set-up quickly a metadataLinkInfo
	 * 
	 * @param type
	 * @param metadataType
	 * @param content
	 */
	protected void setup(String type, String metadataType, String content) {
		set(ResourceMetadataLinkInfo.type.name(), type);
		set(ResourceMetadataLinkInfo.metadataType.name(), metadataType);
		set(ResourceMetadataLinkInfo.content.name(), content);
	}
	
	/** 
	 * Set a MetadataLinkInfo member (type, metadataType or content)
	 * 
	 * @param type
	 * @param value
	 */
	protected void setMember(ResourceMetadataLinkInfo type,
			String value) {
		set(type.toString(), value);
	}

	/**
	 * Set the mime type
	 * 
	 * @param type
	 */
	public void setType(String type){
		this.setMember(ResourceMetadataLinkInfo.type, type);
	}
	
	/**
	 * Set the metadata type
	 * 
	 * @param metadataType
	 */
	public void setMetadataType(String metadataType){
		this.setMember(ResourceMetadataLinkInfo.metadataType, metadataType);
	}
	
	/**
	 * Set the content
	 * 
	 * @param content
	 */
	public void setContent(String content){
		this.setMember(ResourceMetadataLinkInfo.content, content);
	}
	
	/** 
	 * Deletes a MetadataLinkInfo member
	 * 
	 * @param type
	 * @return true if the metadataLinkInfo member is removed
	 */
	protected boolean delMember(ResourceMetadataLinkInfo type) {
		return ElementUtils.remove(this.getRoot(), this.getRoot().getChild(type.toString()));
	}

	/**
	 * Deletes the type
	 * 
	 * @param type
	 * @return true if removed
	 */
	public boolean delType(){
		return this.delMember(ResourceMetadataLinkInfo.type);
	}
	
	/**
	 * Deletes the metadata type
	 * 
	 * @param metadata type
	 * @return true if removed
	 */
	public boolean delMetadataType(){
		return this.delMember(ResourceMetadataLinkInfo.metadataType);
	}
	
	/**
	 * Deletes the content
	 * 
	 * @param content
	 * @return true if removed
	 */
	public boolean delContent(){
		return this.delMember(ResourceMetadataLinkInfo.content);
	}
	
	/** 
	 * Get the value of the MetadataLinkInfo member
	 * 
	 * @param type
	 * @return the value of the MetadataLinkInfo member
	 */
	protected String getMember(ResourceMetadataLinkInfo type) {
		Element el = this.getRoot().getChild(type.toString());
		if (el != null)
			return el.getTextTrim();
		else
			return null;
	}
	
	/**
	 * Get the mime type
	 * 
	 * @return
	 */
	public String getType(){
		return this.getMember(ResourceMetadataLinkInfo.type);		
	}
	
	/**
	 * Get the metadata type
	 * 
	 * @return
	 */
	public String getMetadataType(){
		return this.getMember(ResourceMetadataLinkInfo.metadataType);
	}
	
	/**
	 * Get the content
	 * 
	 * @return
	 */
	public String getContent(){
		return this.getMember(ResourceMetadataLinkInfo.content);
	}

}

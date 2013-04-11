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

import java.util.Map;
import java.util.Map.Entry;

import org.jdom.Element;
import org.jdom.filter.Filter;

import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;

/**
 * GSMetadataLinkEncoder - encodes a metadataLink for a given GeoServer Resource
 * (feature type /coverage), as follows:
 * <pre>
 * {@code
 * final GSMetadataLinkInfoEncoder mde = new GSMetadataLinkInfoEncoder();
 * mde.setup("text/xml", "ISO19115:2003","http://www.organization.org/metadata");
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
public class GSMetadataLinkInfoEncoder extends PropertyXMLEncoder {

	/** A class to filter the MetadataLinkInfo by content
	 * 
	 *
	 */
	public static class filterByContent implements Filter {

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
	 * Set-up quickly a metadataLinkInfo
	 * 
	 * @param type
	 * @param metadataType
	 * @param content
	 */
	public void setup(String type, String metadataType, String content) {
		set(ResourceMetadataLinkInfo.type.name(), type);
		set(ResourceMetadataLinkInfo.metadataType.name(), metadataType);
		set(ResourceMetadataLinkInfo.content.name(), content);
	}

	/** 
	 * Set-up a metadataLinkInfo
	 * 
	 * @param metadataLinkInfos
	 */
	public void setup(Map<ResourceMetadataLinkInfo, String> metadataLinkInfos) {
		for (Entry<ResourceMetadataLinkInfo, String> mdLinkInfo : metadataLinkInfos
				.entrySet()) {
			set(mdLinkInfo.getKey().toString(), mdLinkInfo.getValue());
		}
	}
	
	/** 
	 * Set a MetadataLinkInfo member (type, metadataType or content)
	 * 
	 * @param type
	 * @param value
	 */
	public void setMetadataLinkInfoMember(ResourceMetadataLinkInfo type,
			String value) {
		set(type.toString(), value);
	}

	/** 
	 * Deletes a MetadataLinkInfo member
	 * 
	 * @param type
	 * @return true if the metadataLinkInfo member is removed
	 */
	public boolean delMetadataLinkInfoMember(ResourceMetadataLinkInfo type) {
		return ElementUtils.remove(this.getRoot(), get(type.toString()));
	}

	/** 
	 * Get the value of the MetadataLinkInfo member
	 * 
	 * @param type
	 * @return the value of the MetadataLinkInfo member
	 */
	public String getMetadataLinkInfoMember(ResourceMetadataLinkInfo type) {
		Element el = get(type.toString());
		if (el != null)
			return el.getTextTrim();
		else
			return null;
	}

}

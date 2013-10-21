/*
* GeoServer-Manager - Simple Manager Library for GeoServer
*
* Copyright (C) 2007,2011 GeoSolutions S.A.S.
* http://www.geo-solutions.it
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
package it.geosolutions.geoserver.rest.encoder.identifier;

import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import it.geosolutions.geoserver.rest.encoder.utils.XmlElement;

import org.jdom.Element;
import org.jdom.filter.Filter;

/**
* GSIdentifierInfoEncoder - encodes an Identifier for a given GeoServer
* layer as follows:
* <pre>
* {@code
* final GSIdentifierInfoEncoder ie = new GSIdentifierInfoEncoder();
* ie.setAuthority("an authority");
* ie.setIdentifier("an identifier");
* }
* </pre>
* For this example, the XML output is:
* <pre>
* {@code
* <Identifier>
*   <authority>an authority</authority>
*   <identifier>an identifier</identifier>
* </Identifier>
* }
* </pre>
*
* @author Emmanuel Blondel - emmanuel.blondel1@gmail.com |
* emmanuel.blondel@fao.org
*
*/
public class GSIdentifierInfoEncoder extends XmlElement {

	/**
	 * A class to filter the Idenfiers by authority
	 * 
	 * 
	 */
	private static class filterByAuthority implements Filter {

		final private String key;

		public filterByAuthority(String authority) {
			this.key = authority;
		}

		private static final long serialVersionUID = 1L;

		public boolean matches(Object obj) {
			Element el = ((Element) obj).getChild(IdentifierInfo.authority
					.toString());
			if (el != null && el.getTextTrim().equals(key)) {
				return true;
			}
			return false;
		}
	}

	/**
	 * Get a Filter using the IdentifierInfo authority
	 * 
	 * @param authority
	 * @return the filter
	 */
	public static Filter getFilterByHref(String authority) {
		return new filterByAuthority(authority);
	}

	/**
	 * Constructs a new GSIdentifierInfoEncoder
	 * 
	 */
	public GSIdentifierInfoEncoder() {
		super("Identifier");
	}

	/**
	 * Constructs quickly an Identifier info
	 * 
	 * @param authority
	 *            (required)
	 * @param identifier
	 *            (required)
	 */
	public GSIdentifierInfoEncoder(String authority, String identifier) {
		super("Identifier");
		this.setup(authority, identifier);
	}

	/**
	 * Set-up quickly an Identifier info
	 * 
	 * @param authority
	 * @param identifier
	 */
	protected void setup(String authority, String identifier) {
		set(IdentifierInfo.authority.name(), authority);
		set(IdentifierInfo.identifier.name(), identifier);
	}

	/**
	 * Set an IdentifierInfo member (authority, identifier)
	 * 
	 * @param type
	 * @param value
	 */
	protected void setMember(IdentifierInfo type, String value) {
		set(type.toString(), value);
	}

	/**
	 * Set the authority
	 * 
	 * @param authority
	 */
	public void setAuthority(String authority) {
		this.setMember(IdentifierInfo.authority, authority);
	}

	/**
	 * Set the identifier
	 * 
	 * @param identifier
	 */
	public void setIdentifier(String identifier) {
		this.setMember(IdentifierInfo.identifier, identifier);
	}

	/**
	 * Deletes an IdentifierInfo member
	 * 
	 * @param type
	 * @return true if the IdentifierInfo member is removed
	 */
	protected boolean delMember(IdentifierInfo type) {
		return ElementUtils.remove(this.getRoot(),
				this.getRoot().getChild(type.toString()));
	}

	/**
	 * Deletes the authority
	 * 
	 * @return true if removed
	 */
	public boolean delAuthority() {
		return this.delMember(IdentifierInfo.authority);
	}

	/**
	 * Deletes the identifier
	 * 
	 * @return true if removed
	 */
	public boolean delIdentifier() {
		return this.delMember(IdentifierInfo.identifier);
	}

	/**
	 * Get the value of the IdentifierInfo member
	 * 
	 * @param type
	 * @return the value of the IdentifierInfo member
	 */
	protected String getMember(IdentifierInfo type) {
		Element el = this.getRoot().getChild(type.toString());
		if (el != null)
			return el.getTextTrim();
		else
			return null;
	}

	/**
	 * Get the authority
	 * 
	 * @return
	 */
	public String getAuthority() {
		return this.getMember(IdentifierInfo.authority);
	}

	/**
	 * Get the identifier
	 * 
	 * @return
	 */
	public String getIdentifier() {
		return this.getMember(IdentifierInfo.identifier);
	}

}
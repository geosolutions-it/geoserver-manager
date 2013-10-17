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
package it.geosolutions.geoserver.rest.encoder.authorityurl;

import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import it.geosolutions.geoserver.rest.encoder.utils.XmlElement;

import org.jdom.Element;
import org.jdom.filter.Filter;

/**
* GSAuthorityURLInfoEncoder - encodes an authorityURL for a given GeoServer
* layer as follows:
* <pre>
* {@code
* final GSAuthorityURLInfoEncoder ae = new GSAuthorityURLInfoEncoder();
* ae.setName("an authority");
* ae.setHref("http://www.organization.org");
* }
* </pre>
* For this example, the XML output is:
* <pre>
* {@code
* <AuthorityURL>
*   <name>an authority</name>
*   <href>http://www.organization.org</href>
* </AuthorityURL>
* }
* </pre>
*
* @author Emmanuel Blondel - emmanuel.blondel1@gmail.com |
* emmanuel.blondel@fao.org
*
*/
public class GSAuthorityURLInfoEncoder extends XmlElement {

	/**
	 * A class to filter the AuthorityURL by href
	 * 
	 * 
	 */
	private static class filterByHref implements Filter {

		final private String key;

		public filterByHref(String href) {
			this.key = href;
		}

		private static final long serialVersionUID = 1L;

		public boolean matches(Object obj) {
			Element el = ((Element) obj).getChild(AuthorityURLInfo.href
					.toString());
			if (el != null && el.getTextTrim().equals(key)) {
				return true;
			}
			return false;
		}
	}

	/**
	 * Get a Filter using the AuthorityURLInfo href (authorityURL)
	 * 
	 * @param href
	 * @return the filter
	 */
	public static Filter getFilterByHref(String href) {
		return new filterByHref(href);
	}

	/**
	 * Constructs a new GSAuthorityURLInfoEncoder
	 * 
	 */
	public GSAuthorityURLInfoEncoder() {
		super("AuthorityURL");
	}

	/**
	 * Constructs quickly an AuthorityURL info
	 * 
	 * @param name
	 *            (required)
	 * @param href
	 *            (required)
	 */
	public GSAuthorityURLInfoEncoder(String name, String href) {
		super("AuthorityURL");
		this.setup(name, href);
	}

	/**
	 * Set-up quickly an AuthorityURL info
	 * 
	 * @param name
	 * @param href
	 */
	protected void setup(String name, String href) {
		set(AuthorityURLInfo.name.name(), name);
		set(AuthorityURLInfo.href.name(), href);
	}

	/**
	 * Set an AuthorityURLInfo member (name, href)
	 * 
	 * @param type
	 * @param value
	 */
	protected void setMember(AuthorityURLInfo type, String value) {
		set(type.toString(), value);
	}

	/**
	 * Set the name
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.setMember(AuthorityURLInfo.name, name);
	}

	/**
	 * Set the href
	 * 
	 * @param href
	 */
	public void setHref(String href) {
		this.setMember(AuthorityURLInfo.href, href);
	}

	/**
	 * Deletes an AuthorityURLInfo member
	 * 
	 * @param type
	 * @return true if the AuthorityURLInfo member is removed
	 */
	protected boolean delMember(AuthorityURLInfo type) {
		return ElementUtils.remove(this.getRoot(),
				this.getRoot().getChild(type.toString()));
	}

	/**
	 * Deletes the authority name
	 * 
	 * @return true if removed
	 */
	public boolean delName() {
		return this.delMember(AuthorityURLInfo.name);
	}

	/**
	 * Deletes the href
	 * 
	 * @return true if removed
	 */
	public boolean delHref() {
		return this.delMember(AuthorityURLInfo.href);
	}

	/**
	 * Get the value of the AuthorityURLInfo member
	 * 
	 * @param type
	 * @return the value of the AuthorityURLInfo member
	 */
	protected String getMember(AuthorityURLInfo type) {
		Element el = this.getRoot().getChild(type.toString());
		if (el != null)
			return el.getTextTrim();
		else
			return null;
	}

	/**
	 * Get the name
	 * 
	 * @return
	 */
	public String getName() {
		return this.getMember(AuthorityURLInfo.name);
	}

	/**
	 * Get the href
	 * 
	 * @return
	 */
	public String getHref() {
		return this.getMember(AuthorityURLInfo.href);
	}

}
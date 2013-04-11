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
package it.geosolutions.geoserver.rest.encoder.metadata;

import it.geosolutions.geoserver.rest.encoder.utils.ElementUtils;
import it.geosolutions.geoserver.rest.encoder.utils.PropertyXMLEncoder;

import java.util.Map;
import java.util.Map.Entry;

import org.jdom.Element;

/**
 * GSVirtualTableParamEncoder - Encodes a metadata VirtualTable parameter for a
 * GeoServer featureType, as follows:
 * 
 * <pre>
 * {
 * 	@code
 * 	final GSVirtualTableParamEncoder vtParam = new GSVirtualTableParamEncoder();
 * 	vtParam.setup("fieldname", "default_value", "^[\\w\\d\\s]+$");
 * }
 * </pre>
 * 
 * For this example, the XML output is:
 * 
 * <pre>
 * {@code
 * <parameter>
 * 	<name>fieldname</name>
 * 	<defaultValue>default_value</defaultValue>
 * 	<regexpValidator>^[\w\d\s]+$</regexpValidator>
 * </parameter>
 * }
 * </pre>
 * 
 * @author Emmanuel Blondel - emmanuel.blondel1@gmail.com |
 *         emmanuel.blondel@fao.org
 * 
 */
public class GSVirtualTableParamEncoder extends PropertyXMLEncoder {

	/**
	 * Constructs a GSVirtualTableParamEncoder
	 */
	public GSVirtualTableParamEncoder() {
		super("parameter");
	}

	/**
	 * Set-up quickly a VirtualTable parameter
	 * 
	 * @param name
	 * @param defaultValue
	 * @param regexpValidator
	 */
	public void setup(String name, String defaultValue, String regexpValidator) {
		set(VirtualTableParameter.name.name(), name);
		set(VirtualTableParameter.defaultValue.name(), defaultValue);
		set(VirtualTableParameter.regexpValidator.name(), regexpValidator);
	}

	/**
	 * Set-up a VirtualTable parameter
	 * 
	 * @param vtParamMembers
	 */
	public void setup(Map<VirtualTableParameter, String> vtParamMembers) {
		for (Entry<VirtualTableParameter, String> vtParamMember : vtParamMembers
				.entrySet()) {
			set(vtParamMember.getKey().toString(), vtParamMember.getValue());
		}
	}

	/**
	 * Set a VirtualTableParameter member
	 * 
	 * @param type
	 * @param value
	 */
	public void setVirtualTableParamMember(VirtualTableParameter type,
			String value) {
		set(type.toString(), value);
	}

	/**
	 * Deletes a VirtualTableParameter member
	 * 
	 * @param type
	 * @return
	 */
	public boolean delVirtualTableParamMember(VirtualTableParameter type) {
		return ElementUtils.remove(this.getRoot(), get(type.toString()));
	}

	/**
	 * Get a VirtualTableParameter member
	 * 
	 * @param type
	 * @return
	 */
	public String getVirtualTableParamMember(VirtualTableParameter type) {
		Element el = get(type.toString());
		if (el != null)
			return el.getTextTrim();
		else
			return null;
	}
}